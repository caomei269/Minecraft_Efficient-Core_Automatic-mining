package com.chunkore.autominer;

import com.chunkore.autominer.block.ModBlocks;
import com.chunkore.autominer.blockentity.ModBlockEntities;
import com.chunkore.autominer.screen.ModScreenHandlers;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChunkOreAutoMiner implements ModInitializer {
	public static final String MOD_ID = "chunkoreautominer";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	
	// 创建物品组
	public static final ItemGroup CHUNK_ORE_AUTO_MINER_GROUP = FabricItemGroup.builder()
			.icon(() -> new ItemStack(ModBlocks.MINING_CORE))
			.displayName(Text.translatable("itemgroup.chunkoreautominer"))
			.entries((displayContext, entries) -> {
				entries.add(ModBlocks.MINING_CORE);
				entries.add(ModBlocks.LOGGING_CORE);
			})
			.build();

	@Override
	public void onInitialize() {
		LOGGER.info("Initializing ChunkOreAutoMiner...");
		
		// 注册物品组
		Registry.register(Registries.ITEM_GROUP, new Identifier(MOD_ID, "chunk_ore_auto_miner"), CHUNK_ORE_AUTO_MINER_GROUP);
		
		// 注册方块
		ModBlocks.registerModBlocks();
		
		// 注册方块实体
		ModBlockEntities.registerBlockEntities();
		
		// 注册屏幕处理器
		ModScreenHandlers.registerScreenHandlers();
		
		LOGGER.info("ChunkOreAutoMiner initialized successfully!");
	}
}