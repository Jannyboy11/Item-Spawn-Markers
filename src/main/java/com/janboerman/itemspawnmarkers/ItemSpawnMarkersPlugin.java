package com.janboerman.itemspawnmarkers;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.ItemID;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ItemDespawned;
import net.runelite.api.events.ItemSpawned;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

@Slf4j
@PluginDescriptor(name = "Item Spawn Markers")
public class ItemSpawnMarkersPlugin extends Plugin {

    private final ItemSpawnCache itemSpawnCache = new ItemSpawnCache();
    private final ItemSpawnList itemSpawns = new ItemSpawnList(); {
        //TODO make this more dynamic
        itemSpawns.put(new ItemSpawn(new WorldPoint(3368, 3152, 0), ItemID.BONES, 100));
        itemSpawns.put(new ItemSpawn(new WorldPoint(3376, 3152, 0), ItemID.BONES, 100));
        itemSpawns.put(new ItemSpawn(new WorldPoint(3377, 3157, 0), ItemID.BONES, 100));
        itemSpawns.put(new ItemSpawn(new WorldPoint(3375, 3145, 0), ItemID.BONES, 100));
        itemSpawns.put(new ItemSpawn(new WorldPoint(3380, 3148, 0), ItemID.BONES, 100));
    }

    @Inject private Client client;
    @Inject private ItemSpawnMarkersConfig config;
    @Inject private OverlayManager overlayManager;

    private ItemSpawnOverlay itemSpawnOverlay;

    @Override
    protected void startUp() throws Exception {
        itemSpawnOverlay = new ItemSpawnOverlay(client, config, itemSpawns, itemSpawnCache);
        overlayManager.add(itemSpawnOverlay);

        log.info("Item Spawn Markers started!");
    }

    @Override
    protected void shutDown() throws Exception {
        overlayManager.remove(itemSpawnOverlay);
        itemSpawnOverlay = null;

        itemSpawnCache.clear();

        log.info("Item Spawn Markers stopped!");
    }

    @Provides
    ItemSpawnMarkersConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(ItemSpawnMarkersConfig.class);
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        itemSpawnCache.tick();
    }

    @Subscribe
    public void onItemSpawned(ItemSpawned event) {
        final SpawnKey spawnKey = new SpawnKey(event.getTile().getWorldLocation(), event.getItem().getId());

        final ItemSpawn itemSpawn = itemSpawns.getItemSpawn(spawnKey);
        if (itemSpawn != null) {
            itemSpawnCache.itemAppeared(itemSpawn, client.getWorld());
        }
    }

    @Subscribe
    public void onItemDespawned(ItemDespawned event) {
        final SpawnKey spawnKey = new SpawnKey(event.getTile().getWorldLocation(), event.getItem().getId());

        final ItemSpawn itemSpawn = itemSpawns.getItemSpawn(spawnKey);
        if (itemSpawn != null) {
            itemSpawnCache.itemPickedUp(itemSpawn, client.getWorld());
        }
    }

}
