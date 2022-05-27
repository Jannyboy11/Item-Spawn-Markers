package com.janboerman.itemspawnmarkers;

import lombok.Value;
import net.runelite.api.coords.WorldPoint;

@Value
class ItemSpawn {

    private final WorldPoint location;
    private final int itemId;
    private final int respawnTime;      //game ticks

}
