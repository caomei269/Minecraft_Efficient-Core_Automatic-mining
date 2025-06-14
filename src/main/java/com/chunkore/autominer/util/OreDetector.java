package com.chunkore.autominer.util;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OreDetector {
    private static final Set<Block> VANILLA_ORES = new HashSet<>();
    
    static {
        // 添加所有原版矿石
        VANILLA_ORES.add(Blocks.COAL_ORE);
        VANILLA_ORES.add(Blocks.DEEPSLATE_COAL_ORE);
        VANILLA_ORES.add(Blocks.IRON_ORE);
        VANILLA_ORES.add(Blocks.DEEPSLATE_IRON_ORE);
        VANILLA_ORES.add(Blocks.COPPER_ORE);
        VANILLA_ORES.add(Blocks.DEEPSLATE_COPPER_ORE);
        VANILLA_ORES.add(Blocks.GOLD_ORE);
        VANILLA_ORES.add(Blocks.DEEPSLATE_GOLD_ORE);
        VANILLA_ORES.add(Blocks.REDSTONE_ORE);
        VANILLA_ORES.add(Blocks.DEEPSLATE_REDSTONE_ORE);
        VANILLA_ORES.add(Blocks.EMERALD_ORE);
        VANILLA_ORES.add(Blocks.DEEPSLATE_EMERALD_ORE);
        VANILLA_ORES.add(Blocks.LAPIS_ORE);
        VANILLA_ORES.add(Blocks.DEEPSLATE_LAPIS_ORE);
        VANILLA_ORES.add(Blocks.DIAMOND_ORE);
        VANILLA_ORES.add(Blocks.DEEPSLATE_DIAMOND_ORE);
        VANILLA_ORES.add(Blocks.NETHER_GOLD_ORE);
        VANILLA_ORES.add(Blocks.NETHER_QUARTZ_ORE);
        VANILLA_ORES.add(Blocks.ANCIENT_DEBRIS);
    }
    
    /**
     * 检查给定方块是否为矿石
     */
    public static boolean isOre(Block block) {
        return VANILLA_ORES.contains(block);
    }
    
    /**
     * 在指定区块中查找所有矿石
     */
    public static List<BlockPos> findOresInChunk(World world, ChunkPos chunkPos) {
        List<BlockPos> orePositions = new ArrayList<>();
        
        // 区块的起始坐标
        int startX = chunkPos.getStartX();
        int startZ = chunkPos.getStartZ();
        int endX = chunkPos.getEndX();
        int endZ = chunkPos.getEndZ();
        
        // 扫描整个区块的Y轴范围
        int minY = world.getBottomY();
        int maxY = world.getTopY();
        
        for (int x = startX; x <= endX; x++) {
            for (int z = startZ; z <= endZ; z++) {
                for (int y = minY; y < maxY; y++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    Block block = world.getBlockState(pos).getBlock();
                    
                    if (isOre(block)) {
                        orePositions.add(pos);
                    }
                }
            }
        }
        
        return orePositions;
    }
    
    /**
     * 获取所有支持的矿石类型
     */
    public static Set<Block> getSupportedOres() {
        return new HashSet<>(VANILLA_ORES);
    }
    
    /**
     * 添加自定义矿石支持
     */
    public static void addCustomOre(Block ore) {
        VANILLA_ORES.add(ore);
    }
    
    /**
     * 移除矿石支持
     */
    public static void removeOre(Block ore) {
        VANILLA_ORES.remove(ore);
    }
}