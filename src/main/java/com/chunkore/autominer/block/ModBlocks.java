package com.chunkore.autominer.block;

import com.chunkore.autominer.ChunkOreAutoMiner;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

public class ModBlocks {
    public static final Block MINING_CORE = registerBlock("mining_core",
            new MiningCoreBlock(FabricBlockSettings.create()
                    .mapColor(MapColor.IRON_GRAY)
                    .strength(5.0f, 6.0f)
                    .sounds(BlockSoundGroup.METAL)
                    .requiresTool()
                    .luminance(state -> state.get(MiningCoreBlock.POWERED) ? 15 : 0)));
    
    public static final Block LOGGING_CORE = registerBlock("logging_core",
            new LoggingCoreBlock(FabricBlockSettings.create()
                    .mapColor(MapColor.BROWN)
                    .strength(4.0f, 6.0f)
                    .sounds(BlockSoundGroup.WOOD)
                    .requiresTool()
                    .luminance(state -> state.get(LoggingCoreBlock.POWERED) ? 15 : 0)));
    
    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, new Identifier(ChunkOreAutoMiner.MOD_ID, name), block);
    }
    
    private static Item registerBlockItem(String name, Block block) {
        return Registry.register(Registries.ITEM, new Identifier(ChunkOreAutoMiner.MOD_ID, name),
                new BlockItem(block, new FabricItemSettings()));
    }
    
    public static void registerModBlocks() {
        ChunkOreAutoMiner.LOGGER.info("Registering ModBlocks for " + ChunkOreAutoMiner.MOD_ID);
    }
}