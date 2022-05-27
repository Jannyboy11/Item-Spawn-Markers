package com.janboerman.itemspawnmarkers;

import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.Tile;
import net.runelite.api.TileItem;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.*;

import java.awt.*;
import java.util.List;

//adapted from GroundMarkerOverlay
class ItemSpawnOverlay extends Overlay {

    private static final int MAX_DRAW_DISTANCE = 32;

    private final Client client;
    private final ItemSpawnMarkersConfig config;
    private final ItemSpawnCache cache;
    private final ItemSpawnList itemSpawns;

    ItemSpawnOverlay(Client client, ItemSpawnMarkersConfig config, ItemSpawnList itemSpawns, ItemSpawnCache cache) {
        this.client = client;
        this.config = config;
        this.itemSpawns = itemSpawns;
        this.cache = cache;

        setPosition(OverlayPosition.DYNAMIC);
        setPriority(OverlayPriority.LOW);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D graphics2D) {
        //TODO Optimization:
        //TODO Instead of iterating over all itemSpawns we should iterate only over those spawns that are in range.
        //TODO To get those tile locations efficiently, we should probably store them in a QuadTree.

        //TODO actually, we can probably get away by just iterating over the spawns that are in the live cache.

        for (ItemSpawn itemSpawn : itemSpawns) {
            WorldPoint worldPoint = itemSpawn.getLocation();

            WorldPoint playerLocation = client.getLocalPlayer().getWorldLocation();
            if (worldPoint.distanceTo(playerLocation) >= MAX_DRAW_DISTANCE) continue;

            LocalPoint localPoint = LocalPoint.fromWorld(client, worldPoint);
            if (localPoint == null) continue;

            ActiveSpawn activeSpawn = new ActiveSpawn(worldPoint, client.getWorld(), itemSpawn.getItemId());
            List<TileItem> groundItems = client.getScene().getTiles()[worldPoint.getPlane()][localPoint.getSceneX()][localPoint.getSceneY()].getGroundItems();
            boolean itemPresent = groundItems != null && groundItems.stream().anyMatch(tileItem -> tileItem.getId() == itemSpawn.getItemId());

            ItemSpawnCooldown itemSpawnCooldown = cache.getCooldown(activeSpawn, itemPresent);
            renderCooldown(graphics2D, worldPoint, itemSpawnCooldown, itemSpawn.getRespawnTime());
        }

        return null;
    }


    private void renderCooldown(Graphics2D graphics, WorldPoint point, ItemSpawnCooldown cooldown, int maxCooldown) {
        final LocalPoint localPoint = LocalPoint.fromWorld(client, point);
        assert localPoint != null : "Unreachable";

        String label;
        Color colour;
        if (cooldown instanceof Cooldown) {
            int ticksRemaining = ((Cooldown) cooldown).getTicks();
            assert 0 <= ticksRemaining && ticksRemaining <= maxCooldown : "cooldown out of range (got: 0 <= " + ticksRemaining + " <= " + maxCooldown + ")";
            label = String.valueOf(ticksRemaining);
            colour = getColour(((float) (maxCooldown - ticksRemaining)) / ((float) maxCooldown));
            assert colour != null : "colour cannot be null";
        } else if (cooldown instanceof CooledDown) {
            return;
        } else if (cooldown instanceof UnknownCooldown){
            label = "???";
            colour = Color.ORANGE;
        } else {
            assert false : "Unreachable";
            label = null;
            colour = null;
        }

        final Point canvasTextLocation = Perspective.getCanvasTextLocation(client, graphics, localPoint, label, 0);
        if (canvasTextLocation != null) {
            OverlayUtil.renderTextLocation(graphics, canvasTextLocation, label, colour);
        }
    }


    private static Color getColour(float percentage) {
        int red, green, blue;

        if (0 <= percentage && percentage <= 0.5) {
            red = 255;
            green = blue = (int) (percentage * 2 * 255);
        } else if (0.5 <= percentage && percentage <= 1.0) {
            blue = 255;
            green = red = (int) ((1.0 - (percentage - 0.5) * 2) * 255);
        } else {
            return null;
        }

        return new Color(red, green, blue);
    }

}
