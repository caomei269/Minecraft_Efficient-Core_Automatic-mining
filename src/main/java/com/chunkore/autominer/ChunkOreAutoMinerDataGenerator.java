package com.chunkore.autominer;

import com.chunkore.autominer.recipe.ModRecipes;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class ChunkOreAutoMinerDataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        
        // 注册合成表生成器
        pack.addProvider(ModRecipes::new);
    }
}