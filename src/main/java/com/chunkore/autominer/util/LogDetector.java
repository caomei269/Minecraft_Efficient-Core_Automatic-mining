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

public class LogDetector {
    // 原版原木方块集合
    private static final Set<Block> VANILLA_LOGS = new HashSet<>();
    
    static {
        // 添加所有原版原木
        VANILLA_LOGS.add(Blocks.OAK_LOG);
        VANILLA_LOGS.add(Blocks.SPRUCE_LOG);
        VANILLA_LOGS.add(Blocks.BIRCH_LOG);
        VANILLA_LOGS.add(Blocks.JUNGLE_LOG);
        VANILLA_LOGS.add(Blocks.ACACIA_LOG);
        VANILLA_LOGS.add(Blocks.DARK_OAK_LOG);
        VANILLA_LOGS.add(Blocks.MANGROVE_LOG);
        VANILLA_LOGS.add(Blocks.CHERRY_LOG);
        
        // 去皮原木
        VANILLA_LOGS.add(Blocks.STRIPPED_OAK_LOG);
        VANILLA_LOGS.add(Blocks.STRIPPED_SPRUCE_LOG);
        VANILLA_LOGS.add(Blocks.STRIPPED_BIRCH_LOG);
        VANILLA_LOGS.add(Blocks.STRIPPED_JUNGLE_LOG);
        VANILLA_LOGS.add(Blocks.STRIPPED_ACACIA_LOG);
        VANILLA_LOGS.add(Blocks.STRIPPED_DARK_OAK_LOG);
        VANILLA_LOGS.add(Blocks.STRIPPED_MANGROVE_LOG);
        VANILLA_LOGS.add(Blocks.STRIPPED_CHERRY_LOG);
        
        // 木头方块
        VANILLA_LOGS.add(Blocks.OAK_WOOD);
        VANILLA_LOGS.add(Blocks.SPRUCE_WOOD);
        VANILLA_LOGS.add(Blocks.BIRCH_WOOD);
        VANILLA_LOGS.add(Blocks.JUNGLE_WOOD);
        VANILLA_LOGS.add(Blocks.ACACIA_WOOD);
        VANILLA_LOGS.add(Blocks.DARK_OAK_WOOD);
        VANILLA_LOGS.add(Blocks.MANGROVE_WOOD);
        VANILLA_LOGS.add(Blocks.CHERRY_WOOD);
        
        // 去皮木头
        VANILLA_LOGS.add(Blocks.STRIPPED_OAK_WOOD);
        VANILLA_LOGS.add(Blocks.STRIPPED_SPRUCE_WOOD);
        VANILLA_LOGS.add(Blocks.STRIPPED_BIRCH_WOOD);
        VANILLA_LOGS.add(Blocks.STRIPPED_JUNGLE_WOOD);
        VANILLA_LOGS.add(Blocks.STRIPPED_ACACIA_WOOD);
        VANILLA_LOGS.add(Blocks.STRIPPED_DARK_OAK_WOOD);
        VANILLA_LOGS.add(Blocks.STRIPPED_MANGROVE_WOOD);
        VANILLA_LOGS.add(Blocks.STRIPPED_CHERRY_WOOD);
        
        // 下界原木
        VANILLA_LOGS.add(Blocks.CRIMSON_STEM);
        VANILLA_LOGS.add(Blocks.WARPED_STEM);
        VANILLA_LOGS.add(Blocks.STRIPPED_CRIMSON_STEM);
        VANILLA_LOGS.add(Blocks.STRIPPED_WARPED_STEM);
        VANILLA_LOGS.add(Blocks.CRIMSON_HYPHAE);
        VANILLA_LOGS.add(Blocks.WARPED_HYPHAE);
        VANILLA_LOGS.add(Blocks.STRIPPED_CRIMSON_HYPHAE);
        VANILLA_LOGS.add(Blocks.STRIPPED_WARPED_HYPHAE);
    }
    
    /**
     * 检查给定方块是否为原木
     */
    public static boolean isLog(Block block) {
        return VANILLA_LOGS.contains(block);
    }
    
    /**
     * 在指定区块中查找所有原木
     * @param world 世界
     * @param chunkPos 区块位置
     * @return 原木位置列表
     */
    public static List<BlockPos> findLogsInChunk(World world, ChunkPos chunkPos) {
        List<BlockPos> logPositions = new ArrayList<>();
        
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
                    
                    if (isLog(block)) {
                        logPositions.add(pos);
                    }
                }
            }
        }
        
        return logPositions;
    }
    
    /**
     * 获取所有支持的原木类型
     */
    public static Set<Block> getSupportedLogs() {
        return new HashSet<>(VANILLA_LOGS);
    }
}