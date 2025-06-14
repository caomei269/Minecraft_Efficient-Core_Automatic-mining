package com.chunkore.autominer.block;

import com.chunkore.autominer.blockentity.MiningCoreBlockEntity;
import com.chunkore.autominer.blockentity.ModBlockEntities;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import com.mojang.serialization.MapCodec;
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
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class MiningCoreBlock extends BlockWithEntity {
    public static final BooleanProperty POWERED = Properties.POWERED;
    
    public MiningCoreBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(POWERED, false));
    }
    
    @Override
    protected MapCodec<? extends MiningCoreBlock> getCodec() {
        return createCodec(MiningCoreBlock::new);
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
        super.neighborUpdate(state, world, pos, sourceBlock, sourcePos, notify);
    }
    
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof MiningCoreBlockEntity miningCore) {
                player.openHandledScreen(miningCore);
            }
        }
        return ActionResult.SUCCESS;
    }
    
    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }
    
    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new MiningCoreBlockEntity(pos, state);
    }
    
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return validateTicker(type, ModBlockEntities.MINING_CORE_BLOCK_ENTITY, MiningCoreBlockEntity::tick);
    }
    
    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof MiningCoreBlockEntity) {
                ((MiningCoreBlockEntity) blockEntity).dropInventory();
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }
}