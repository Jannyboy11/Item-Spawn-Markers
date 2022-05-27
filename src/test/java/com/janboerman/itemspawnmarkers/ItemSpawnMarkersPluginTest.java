package com.janboerman.itemspawnmarkers;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class ItemSpawnMarkersPluginTest {

    public static void main(String[] args) throws Exception {
        ExternalPluginManager.loadBuiltin(ItemSpawnMarkersPlugin.class);
        RuneLite.main(args);
    }

}