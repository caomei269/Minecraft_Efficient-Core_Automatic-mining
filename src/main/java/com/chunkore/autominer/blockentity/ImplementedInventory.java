package com.chunkore.autominer.blockentity;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

public interface ImplementedInventory extends Inventory {
    DefaultedList<ItemStack> getItems();
    
    @Override
    default int size() {
        return getItems().size();
    }
    
    @Override
    default boolean isEmpty() {
        for (int i = 0; i < size(); i++) {
            ItemStack stack = getStack(i);
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    default ItemStack getStack(int slot) {
        return getItems().get(slot);
    }
    
    @Override
    default ItemStack removeStack(int slot, int count) {
        ItemStack result = Inventories.splitStack(getItems(), slot, count);
        if (!result.isEmpty()) {
            markDirty();
        }
        return result;
    }
    
    @Override
    default ItemStack removeStack(int slot) {
        return Inventories.removeStack(getItems(), slot);
    }
    
    @Override
    default void setStack(int slot, ItemStack stack) {
        getItems().set(slot, stack);
        if (stack.getCount() > getMaxCountPerStack()) {
            stack.setCount(getMaxCountPerStack());
        }
        markDirty();
    }
    
    @Override
    default void clear() {
        getItems().clear();
    }
    
    @Override
    default void markDirty() {
        // Override this method in your BlockEntity
    }
    
    @Override
    default boolean canPlayerUse(PlayerEntity player) {
        return true;
    }
}