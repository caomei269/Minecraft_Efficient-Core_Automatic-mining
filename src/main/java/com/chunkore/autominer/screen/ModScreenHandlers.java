package com.chunkore.autominer.screen;

import com.chunkore.autominer.ChunkOreAutoMiner;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class ModScreenHandlers {
    public static final ScreenHandlerType<MiningCoreScreenHandler> MINING_CORE_SCREEN_HANDLER =
            Registry.register(Registries.SCREEN_HANDLER, new Identifier(ChunkOreAutoMiner.MOD_ID, "mining_core"),
                    new ExtendedScreenHandlerType<>(MiningCoreScreenHandler::new));
    
    public static final ScreenHandlerType<LoggingCoreScreenHandler> LOGGING_CORE_SCREEN_HANDLER =
            Registry.register(Registries.SCREEN_HANDLER, new Identifier(ChunkOreAutoMiner.MOD_ID, "logging_core"),
                    new ExtendedScreenHandlerType<>(LoggingCoreScreenHandler::new));
    
    public static void registerScreenHandlers() {
        ChunkOreAutoMiner.LOGGER.info("Registering Screen Handlers for " + ChunkOreAutoMiner.MOD_ID);
    }
}