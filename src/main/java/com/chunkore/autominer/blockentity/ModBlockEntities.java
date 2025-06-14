package com.chunkore.autominer.blockentity;

import com.chunkore.autominer.ChunkOreAutoMiner;
import com.chunkore.autominer.block.ModBlocks;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {
    public static final BlockEntityType<MiningCoreBlockEntity> MINING_CORE_BLOCK_ENTITY =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(ChunkOreAutoMiner.MOD_ID, "mining_core_block_entity"),
                    FabricBlockEntityTypeBuilder.create(MiningCoreBlockEntity::new, ModBlocks.MINING_CORE).build());
    
    public static final BlockEntityType<LoggingCoreBlockEntity> LOGGING_CORE_BLOCK_ENTITY =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(ChunkOreAutoMiner.MOD_ID, "logging_core_block_entity"),
                    FabricBlockEntityTypeBuilder.create(LoggingCoreBlockEntity::new, ModBlocks.LOGGING_CORE).build());
    
    public static void registerBlockEntities() {
        ChunkOreAutoMiner.LOGGER.info("Registering Block Entities for " + ChunkOreAutoMiner.MOD_ID);
    }
}