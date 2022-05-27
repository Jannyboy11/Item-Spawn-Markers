package com.janboerman.itemspawnmarkers;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@Slf4j
@PluginDescriptor(name = "Item Spawn Markers")
public class ItemSpawnMarkersPlugin extends Plugin {

    @Inject private Client client;
    @Inject private ItemSpawnMarkersConfig config;

    @Override
    protected void startUp() throws Exception {
        log.info("Item Spawn Markers started!");
    }

    @Override
    protected void shutDown() throws Exception {
        log.info("Item Spawn Markers stopped!");
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged gameStateChanged) {
        if (gameStateChanged.getGameState() == GameState.LOGGED_IN) {
            client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Item Spawn Markers says " + config.greeting(), null);
        }
    }

    @Provides
    ItemSpawnMarkersConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(ItemSpawnMarkersConfig.class);
    }

}
