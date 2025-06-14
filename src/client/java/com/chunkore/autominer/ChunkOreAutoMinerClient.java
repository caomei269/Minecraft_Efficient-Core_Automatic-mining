package com.chunkore.autominer;

import com.chunkore.autominer.screen.LoggingCoreScreen;
import com.chunkore.autominer.screen.MiningCoreScreen;
import com.chunkore.autominer.screen.ModScreenHandlers;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

public class ChunkOreAutoMinerClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // 注册GUI屏幕
        HandledScreens.register(ModScreenHandlers.MINING_CORE_SCREEN_HANDLER, MiningCoreScreen::new);
        HandledScreens.register(ModScreenHandlers.LOGGING_CORE_SCREEN_HANDLER, LoggingCoreScreen::new);
        
        ChunkOreAutoMiner.LOGGER.info("ChunkOreAutoMiner client initialized!");
    }
}