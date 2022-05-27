package com.janboerman.itemspawnmarkers;

import lombok.Value;
import net.runelite.api.coords.WorldPoint;

@Value
public class SpawnKey {

    private final WorldPoint location;
    private final int itemId;

}
