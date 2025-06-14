package com.chunkore.autominer.recipe;

import com.chunkore.autominer.ChunkOreAutoMiner;
import com.chunkore.autominer.block.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.util.Identifier;

public class ModRecipes extends FabricRecipeProvider {
    public ModRecipes(FabricDataOutput output) {
        super(output);
    }
    
    @Override
    public void generate(RecipeExporter exporter) {
        // 矿脉挖掘核心合成配方
        // III
        // IPI
        // IRI
        // I=铁块, P=铁镐, R=红石块
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.MINING_CORE, 1)
                .pattern("III")
                .pattern("IPI")
                .pattern("IRI")
                .input('I', Items.IRON_BLOCK)
                .input('P', Items.IRON_PICKAXE)
                .input('R', Items.REDSTONE_BLOCK)
                .criterion(hasItem(Items.IRON_BLOCK), conditionsFromItem(Items.IRON_BLOCK))
                .criterion(hasItem(Items.IRON_PICKAXE), conditionsFromItem(Items.IRON_PICKAXE))
                .criterion(hasItem(Items.REDSTONE_BLOCK), conditionsFromItem(Items.REDSTONE_BLOCK))
                .offerTo(exporter, new Identifier(ChunkOreAutoMiner.MOD_ID, "mining_core"));
        
        // 伐木核心合成配方
        // IOI
        // OAO
        // IRI
        // I=铁块, A=铁斧, O=橡木原木, R=红石块
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.LOGGING_CORE, 1)
                .pattern("IOI")
                .pattern("OAO")
                .pattern("IRI")
                .input('I', Items.IRON_BLOCK)
                .input('A', Items.IRON_AXE)
                .input('O', Items.OAK_LOG)
                .input('R', Items.REDSTONE_BLOCK)
                .criterion(hasItem(Items.IRON_BLOCK), conditionsFromItem(Items.IRON_BLOCK))
                .criterion(hasItem(Items.IRON_AXE), conditionsFromItem(Items.IRON_AXE))
                .criterion(hasItem(Items.OAK_LOG), conditionsFromItem(Items.OAK_LOG))
                .criterion(hasItem(Items.REDSTONE_BLOCK), conditionsFromItem(Items.REDSTONE_BLOCK))
                .offerTo(exporter, new Identifier(ChunkOreAutoMiner.MOD_ID, "logging_core"));
    }
}