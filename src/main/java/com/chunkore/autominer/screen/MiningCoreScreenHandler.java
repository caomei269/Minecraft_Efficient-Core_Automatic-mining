package com.chunkore.autominer.screen;

import com.chunkore.autominer.blockentity.MiningCoreBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.BlockPos;

public class MiningCoreScreenHandler extends ScreenHandler {
    private final Inventory inventory;
    private final MiningCoreBlockEntity blockEntity;
    
    // 用于客户端的构造函数
    public MiningCoreScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
        this(syncId, playerInventory, getMiningCoreBlockEntity(playerInventory, buf));
    }
    
    private static MiningCoreBlockEntity getMiningCoreBlockEntity(PlayerInventory playerInventory, PacketByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        return (MiningCoreBlockEntity) playerInventory.player.getWorld().getBlockEntity(pos);
    }
    
    // 用于服务端的构造函数
    public MiningCoreScreenHandler(int syncId, PlayerInventory playerInventory, MiningCoreBlockEntity blockEntity) {
        super(ModScreenHandlers.MINING_CORE_SCREEN_HANDLER, syncId);
        this.inventory = blockEntity;
        this.blockEntity = blockEntity;
        
        // 添加矿物存储槽位 (3x3 = 9个槽位)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                this.addSlot(new Slot(inventory, col + row * 3, 8 + col * 18, 18 + row * 18));
            }
        }
        
        // 添加主镐子槽位 (1个槽位)
        this.addSlot(new PickaxeSlot(inventory, MiningCoreBlockEntity.MAIN_PICKAXE_SLOT, 80, 36));
        
        // 添加加速镐子槽位 (3x3 = 9个槽位)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                this.addSlot(new PickaxeSlot(inventory, MiningCoreBlockEntity.BOOST_PICKAXE_START + col + row * 3, 116 + col * 18, 18 + row * 18));
            }
        }
        
        // 添加玩家库存槽位
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
        
        // 添加玩家快捷栏槽位
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }
    }
    
    @Override
    public ItemStack quickMove(PlayerEntity player, int slotIndex) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotIndex);
        
        if (slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();
            
            if (slotIndex < 19) {
                // 从方块实体库存移动到玩家库存
                if (!this.insertItem(originalStack, 19, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // 从玩家库存移动到方块实体库存
                ItemStack stack = originalStack.copy();
                
                // 如果是镐子，优先放入镐子槽位
                if (stack.getItem() instanceof PickaxeItem) {
                    // 先尝试主镐子槽位
                    if (this.slots.get(MiningCoreBlockEntity.MAIN_PICKAXE_SLOT).getStack().isEmpty()) {
                        if (this.insertItem(originalStack, MiningCoreBlockEntity.MAIN_PICKAXE_SLOT, MiningCoreBlockEntity.MAIN_PICKAXE_SLOT + 1, false)) {
                            return newStack;
                        }
                    }
                    // 再尝试加速镐子槽位
                    if (this.insertItem(originalStack, MiningCoreBlockEntity.BOOST_PICKAXE_START, MiningCoreBlockEntity.BOOST_PICKAXE_START + MiningCoreBlockEntity.BOOST_PICKAXE_SIZE, false)) {
                        return newStack;
                    }
                    // 如果镐子槽位都满了，返回空堆栈防止卡死
                    return ItemStack.EMPTY;
                } else {
                    // 非镐子物品放入矿物存储区
                    if (!this.insertItem(originalStack, 0, MiningCoreBlockEntity.MINERAL_STORAGE_SIZE, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            }
            
            if (originalStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }
        
        return newStack;
    }
    
    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }
    
    public MiningCoreBlockEntity getBlockEntity() {
        return blockEntity;
    }
    
    // UI状态查询方法
    public boolean isScanning() {
        return blockEntity != null && blockEntity.isScanning();
    }
    
    public int getScanProgress() {
        return blockEntity != null ? blockEntity.getScanProgress() : 0;
    }
    
    public int getMaxScanTime() {
        return blockEntity != null ? blockEntity.getMaxScanTime() : 1;
    }
    
    public boolean isMining() {
        return blockEntity != null && blockEntity.isMining();
    }
    
    public int getTotalOres() {
        return blockEntity != null ? blockEntity.getTotalOres() : 0;
    }
    
    public int getOresMined() {
        return blockEntity != null ? blockEntity.getOresMined() : 0;
    }
    
    // 镐子专用槽位类
    public static class PickaxeSlot extends Slot {
        public PickaxeSlot(Inventory inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }
        
        @Override
        public boolean canInsert(ItemStack stack) {
            return stack.getItem() instanceof PickaxeItem;
        }
        
        @Override
        public int getMaxItemCount() {
            return 1; // 每个槽位只能放一个镐子
        }
    }
}