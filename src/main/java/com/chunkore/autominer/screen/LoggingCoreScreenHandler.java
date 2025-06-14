package com.chunkore.autominer.screen;

import com.chunkore.autominer.blockentity.LoggingCoreBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.BlockPos;

public class LoggingCoreScreenHandler extends ScreenHandler {
    private final Inventory inventory;
    private final LoggingCoreBlockEntity blockEntity;
    
    public LoggingCoreScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
        this(syncId, playerInventory, getLoggingCoreBlockEntity(playerInventory, buf));
    }
    
    private static LoggingCoreBlockEntity getLoggingCoreBlockEntity(PlayerInventory playerInventory, PacketByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        return (LoggingCoreBlockEntity) playerInventory.player.getWorld().getBlockEntity(pos);
    }
    
    // 用于服务端的构造函数
    public LoggingCoreScreenHandler(int syncId, PlayerInventory playerInventory, LoggingCoreBlockEntity blockEntity) {
        super(ModScreenHandlers.LOGGING_CORE_SCREEN_HANDLER, syncId);
        this.inventory = blockEntity;
        this.blockEntity = blockEntity;
        
        // 原木存储区 (0-8) - 3x3布局
        for (int i = 0; i < LoggingCoreBlockEntity.LOG_STORAGE_SIZE; i++) {
            int row = i / 3;
            int col = i % 3;
            this.addSlot(new Slot(inventory, i, 8 + col * 18, 18 + row * 18));
        }
        
        // 主斧头槽位 (9) - 居中位置
        this.addSlot(new AxeSlot(inventory, LoggingCoreBlockEntity.MAIN_AXE_SLOT, 80, 36));
        
        // 加速斧头槽位 (10-18) - 3x3布局
        for (int i = 0; i < LoggingCoreBlockEntity.BOOST_AXE_SIZE; i++) {
            int row = i / 3;
            int col = i % 3;
            this.addSlot(new AxeSlot(inventory, LoggingCoreBlockEntity.BOOST_AXE_START + i, 116 + col * 18, 18 + row * 18));
        }
        
        // 玩家背包
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        
        // 玩家快捷栏
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }
    
    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);
        if (slot != null && slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();
            
            if (invSlot < this.inventory.size()) {
                // 从机器内部移动到玩家背包
                if (!this.insertItem(originalStack, this.inventory.size(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // 从玩家背包移动到机器内部
                
                // 如果是斧头，优先放入斧头槽位
                if (originalStack.getItem() instanceof AxeItem) {
                    // 先尝试主斧头槽位
                    if (this.slots.get(LoggingCoreBlockEntity.MAIN_AXE_SLOT).getStack().isEmpty()) {
                        if (this.insertItem(originalStack, LoggingCoreBlockEntity.MAIN_AXE_SLOT, LoggingCoreBlockEntity.MAIN_AXE_SLOT + 1, false)) {
                            return newStack;
                        }
                    }
                    // 再尝试加速斧头槽位
                    if (this.insertItem(originalStack, LoggingCoreBlockEntity.BOOST_AXE_START, LoggingCoreBlockEntity.BOOST_AXE_START + LoggingCoreBlockEntity.BOOST_AXE_SIZE, false)) {
                        return newStack;
                    }
                    // 如果斧头槽位都满了，返回空堆栈防止卡死
                    return ItemStack.EMPTY;
                } else {
                    // 非斧头物品放入原木存储区
                    if (!this.insertItem(originalStack, 0, LoggingCoreBlockEntity.LOG_STORAGE_SIZE, false)) {
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
    
    public LoggingCoreBlockEntity getBlockEntity() {
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
    
    public boolean isLogging() {
        return blockEntity != null && blockEntity.isLogging();
    }
    
    public int getTotalLogs() {
        return blockEntity != null ? blockEntity.getTotalLogs() : 0;
    }
    
    public int getLogsChopped() {
        return blockEntity != null ? blockEntity.getLogsChopped() : 0;
    }
    
    // 斧头专用槽位类
    public static class AxeSlot extends Slot {
        public AxeSlot(Inventory inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }
        
        @Override
        public boolean canInsert(ItemStack stack) {
            return stack.getItem() instanceof AxeItem;
        }
        
        @Override
        public int getMaxItemCount() {
            return 1; // 每个槽位只能放一个斧头
        }
    }
}