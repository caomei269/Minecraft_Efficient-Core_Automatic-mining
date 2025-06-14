package com.chunkore.autominer.block;

import com.chunkore.autominer.blockentity.LoggingCoreBlockEntity;
import com.chunkore.autominer.blockentity.ModBlockEntities;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class LoggingCoreBlock extends BlockWithEntity {
    public static final BooleanProperty POWERED = Properties.POWERED;
    
    public LoggingCoreBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(POWERED, false));
    }
    
    @Override
    protected MapCodec<? extends LoggingCoreBlock> getCodec() {
        return createCodec(LoggingCoreBlock::new);
    }
    
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(POWERED);
    }
    
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(POWERED, ctx.getWorld().isReceivingRedstonePower(ctx.getBlockPos()));
    }
    
    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (!world.isClient) {
            boolean powered = world.isReceivingRedstonePower(pos);
            if (powered != state.get(POWERED)) {
                world.setBlockState(pos, state.with(POWERED, powered), Block.NOTIFY_ALL);
            }
        }
    }
    
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof LoggingCoreBlockEntity loggingCore) {
                player.openHandledScreen(loggingCore);
            }
        }
        return ActionResult.SUCCESS;
    }
    
    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }
    
    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof LoggingCoreBlockEntity) {
                ((LoggingCoreBlockEntity) blockEntity).dropInventoryItems(world, pos);
                world.updateComparators(pos, this);
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }
    
    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new LoggingCoreBlockEntity(pos, state);
    }
    
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return validateTicker(type, ModBlockEntities.LOGGING_CORE_BLOCK_ENTITY,
                (world1, pos, state1, blockEntity) -> LoggingCoreBlockEntity.tick(world1, pos, state1, blockEntity));
    }
}