package com.janboerman.itemspawnmarkers;

import lombok.Value;
import net.runelite.api.coords.WorldPoint;

@Value
class ActiveSpawn {

    private final WorldPoint location;
    private final int world;
    private final int itemId;

}