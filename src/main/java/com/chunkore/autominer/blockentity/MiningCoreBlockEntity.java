package com.chunkore.autominer.blockentity;

import com.chunkore.autominer.screen.MiningCoreScreenHandler;
import com.chunkore.autominer.util.OreDetector;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.ToolMaterials;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MiningCoreBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory, ImplementedInventory, SidedInventory {
    // 库存布局: 0-8矿物存储(3x3), 9主镐子槽, 10-18加速镐子(3x3)
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(19, ItemStack.EMPTY);
    
    // 槽位常量
    public static final int STORAGE_START = 0;
    public static final int STORAGE_SIZE = 9;
    public static final int MINERAL_STORAGE_SIZE = STORAGE_SIZE; // 矿物存储区大小
    public static final int MAIN_PICKAXE_SLOT = 9;
    public static final int BOOST_PICKAXE_START = 10;
    public static final int BOOST_PICKAXE_SIZE = 9;
    
    // 状态变量
    private boolean isScanning = false;
    private boolean isMining = false;
    private int scanProgress = 0;
    private int maxScanTime = 100; // 5秒 (20 ticks/秒)
    private int miningProgress = 0;
    private int oresFound = 0;
    private int oresMined = 0;
    
    // 矿石位置列表
    private List<BlockPos> orePositions = new ArrayList<>();
    private int currentOreIndex = 0;
    
    public MiningCoreBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MINING_CORE_BLOCK_ENTITY, pos, state);
    }
    
    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }
    
    public static void tick(World world, BlockPos pos, BlockState state, MiningCoreBlockEntity blockEntity) {
        if (world.isClient) return;
        
        // 检查是否接收到红石信号
        boolean powered = state.get(com.chunkore.autominer.block.MiningCoreBlock.POWERED);
        if (!powered) {
            // 没有红石信号时停止所有操作
            if (blockEntity.isScanning) {
                blockEntity.isScanning = false;
                blockEntity.scanProgress = 0;
            }
            if (blockEntity.isMining) {
                blockEntity.isMining = false;
                blockEntity.miningProgress = 0;
                blockEntity.currentOreIndex = 0;
            }
            return;
        }
        
        if (blockEntity.isScanning) {
            blockEntity.tickScanning(world, pos);
        } else if (blockEntity.isMining) {
            blockEntity.tickMining(world, pos);
        } else if (!blockEntity.isScanning && !blockEntity.isMining) {
            if (blockEntity.orePositions.isEmpty()) {
                // 没有矿石位置，开始扫描
                blockEntity.startScanning(world, pos);
            } else {
                // 有矿石位置但没在挖掘，开始挖掘
                blockEntity.startMining(world, pos);
            }
        }
    }
    
    private void tickScanning(World world, BlockPos pos) {
        scanProgress++;
        
        // 播放扫描音效
        if (scanProgress % 20 == 0) {
            world.playSound(null, pos, SoundEvents.BLOCK_BEACON_AMBIENT, SoundCategory.BLOCKS, 0.5f, 1.0f);
        }
        
        if (scanProgress >= maxScanTime) {
            finishScanning(world, pos);
        }
        
        markDirty();
    }
    
    private void tickMining(World world, BlockPos pos) {
        // 检查是否有主镐子
        ItemStack mainPickaxe = inventory.get(MAIN_PICKAXE_SLOT);
        if (mainPickaxe.isEmpty() || !(mainPickaxe.getItem() instanceof PickaxeItem)) {
            // 没有镐子，停止挖掘
            isMining = false;
            miningProgress = 0;
            markDirty();
            return;
        }
        
        if (currentOreIndex >= orePositions.size()) {
            // 挖掘完成
            isMining = false;
            currentOreIndex = 0;
            orePositions.clear();
            markDirty();
            return;
        }
        
        miningProgress++;
        
        // 计算挖掘速度（基础40 ticks除以速度倍数）
        int miningTime = calculateMiningTime();
        
        if (miningProgress >= miningTime) {
            BlockPos orePos = orePositions.get(currentOreIndex);
            BlockState oreState = world.getBlockState(orePos);
            
            // 检查矿石是否仍然存在
            if (oreState.isAir() || !OreDetector.isOre(oreState.getBlock())) {
                // 矿石已经不存在，直接跳到下一个
                currentOreIndex++;
                miningProgress = 0;
            } else {
                // 挖掘一个矿石并消耗镐子耐久
                mineOre(world, orePos);
                damagePickaxe(mainPickaxe);
                currentOreIndex++;
                miningProgress = 0;
                oresMined++;
                
                // 播放挖掘音效
                world.playSound(null, pos, SoundEvents.BLOCK_STONE_BREAK, SoundCategory.BLOCKS, 0.8f, 1.0f);
            }
        }
        
        markDirty();
    }
    
    private void startScanning(World world, BlockPos pos) {
        isScanning = true;
        scanProgress = 0;
        orePositions.clear();
        oresFound = 0;
        oresMined = 0;
        currentOreIndex = 0;
        markDirty();
    }
    
    private void startMining(World world, BlockPos pos) {
        // 检查是否有主镐子
        ItemStack mainPickaxe = inventory.get(MAIN_PICKAXE_SLOT);
        if (mainPickaxe.isEmpty() || !(mainPickaxe.getItem() instanceof PickaxeItem)) {
            return; // 没有镐子，无法开始挖掘
        }
        
        // 检查是否有矿石可挖
        if (orePositions.isEmpty()) {
            return; // 没有矿石，无法开始挖掘
        }
        
        isMining = true;
        miningProgress = 0;
        // 不重置currentOreIndex，从上次停止的地方继续
        markDirty();
    }
    
    private void finishScanning(World world, BlockPos pos) {
        isScanning = false;
        scanProgress = 0;
        
        // 扫描当前区块的矿石
        ChunkPos chunkPos = new ChunkPos(pos);
        orePositions = OreDetector.findOresInChunk(world, chunkPos);
        oresFound = orePositions.size();
        
        if (!orePositions.isEmpty()) {
            isMining = true;
            miningProgress = 0;
        }
        
        markDirty();
    }
    
    private void mineOre(World world, BlockPos orePos) {
        BlockState oreState = world.getBlockState(orePos);
        // 检查矿石是否仍然存在且确实是矿石
        if (oreState.isAir() || !OreDetector.isOre(oreState.getBlock())) {
            // 矿石已经不存在，跳过这个位置
            return;
        }
        if (OreDetector.isOre(oreState.getBlock())) {
            // 获取矿石掉落物
            List<ItemStack> drops = Block.getDroppedStacks(oreState, world.getServer().getWorld(world.getRegistryKey()), orePos, null);
            
            // 将掉落物添加到矿物存储区（只使用槽位0-8）
            for (ItemStack drop : drops) {
                boolean added = false;
                // 先尝试与现有物品堆叠
                for (int i = STORAGE_START; i < STORAGE_START + STORAGE_SIZE; i++) {
                    ItemStack slot = inventory.get(i);
                    if (!slot.isEmpty() && ItemStack.canCombine(slot, drop)) {
                        int maxCount = Math.min(slot.getMaxCount(), slot.getCount() + drop.getCount());
                        int remaining = drop.getCount() - (maxCount - slot.getCount());
                        slot.setCount(maxCount);
                        if (remaining > 0) {
                            drop.setCount(remaining);
                        } else {
                            added = true;
                            break;
                        }
                    }
                }
                // 如果没有完全堆叠，尝试放入空槽位
                if (!added) {
                    for (int i = STORAGE_START; i < STORAGE_START + STORAGE_SIZE; i++) {
                        ItemStack slot = inventory.get(i);
                        if (slot.isEmpty()) {
                            inventory.set(i, drop.copy());
                            added = true;
                            break;
                        }
                    }
                }
                // 如果矿物存储区已满，将物品掉落到地面
                if (!added) {
                    ItemEntity itemEntity = new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, drop.copy());
                    world.spawnEntity(itemEntity);
                }
            }
            
            // 移除矿石方块
            world.removeBlock(orePos, false);
        }
    }
    
    public void dropInventory() {
        ItemScatterer.spawn(world, pos, inventory);
    }
    

    
    // 镐子相关方法
    private int calculateMiningTime() {
        ItemStack mainPickaxe = inventory.get(MAIN_PICKAXE_SLOT);
        if (mainPickaxe.isEmpty() || !(mainPickaxe.getItem() instanceof PickaxeItem)) {
            return 40; // 默认挖掘时间
        }
        
        // 获取主镐子的挖掘速度
        float baseSpeed = getPickaxeSpeed(mainPickaxe);
        
        // 计算加速镐子的加成
        float speedBonus = calculateSpeedBonus();
        
        // 总速度 = 基础速度 + 加速加成
        float totalSpeed = baseSpeed + speedBonus;
        
        // 挖掘时间 = 基础时间 / 总速度（最小1 tick）
        return Math.max(1, (int)(40 / totalSpeed));
    }
    
    private float getPickaxeSpeed(ItemStack pickaxe) {
        if (!(pickaxe.getItem() instanceof PickaxeItem pickaxeItem)) {
            return 1.0f;
        }
        
        ToolMaterial material = pickaxeItem.getMaterial();
        if (material == ToolMaterials.WOOD) return 1.0f;
        if (material == ToolMaterials.STONE) return 1.5f;
        if (material == ToolMaterials.IRON) return 2.0f;
        if (material == ToolMaterials.GOLD) return 3.0f;
        if (material == ToolMaterials.DIAMOND) return 2.5f;
        if (material == ToolMaterials.NETHERITE) return 3.0f;
        
        return 1.0f; // 默认速度
    }
    
    private float calculateSpeedBonus() {
        float bonus = 0.0f;
        
        // 检查加速镐子槽位
        for (int i = BOOST_PICKAXE_START; i < BOOST_PICKAXE_START + BOOST_PICKAXE_SIZE; i++) {
            ItemStack boostPickaxe = inventory.get(i);
            if (!boostPickaxe.isEmpty() && boostPickaxe.getItem() instanceof PickaxeItem pickaxeItem) {
                ToolMaterial material = pickaxeItem.getMaterial();
                
                // 不同材质的镐子提供不同的加速分数
                if (material == ToolMaterials.WOOD) bonus += 0.1f;
                else if (material == ToolMaterials.STONE) bonus += 0.2f;
                else if (material == ToolMaterials.IRON) bonus += 0.3f;
                else if (material == ToolMaterials.GOLD) bonus += 0.4f;
                else if (material == ToolMaterials.DIAMOND) bonus += 0.5f;
                else if (material == ToolMaterials.NETHERITE) bonus += 0.6f;
            }
        }
        
        return bonus;
    }
    
    private void damagePickaxe(ItemStack pickaxe) {
        if (pickaxe.isDamageable()) {
            // 直接设置损坏值，避免传递null entity导致的崩溃
            pickaxe.setDamage(pickaxe.getDamage() + 1);
            if (pickaxe.getDamage() >= pickaxe.getMaxDamage()) {
                // 镐子损坏，移除
                inventory.set(MAIN_PICKAXE_SLOT, ItemStack.EMPTY);
            }
        }
    }
    
    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        Inventories.readNbt(nbt, inventory);
        isScanning = nbt.getBoolean("IsScanning");
        isMining = nbt.getBoolean("IsMining");
        scanProgress = nbt.getInt("ScanProgress");
        miningProgress = nbt.getInt("MiningProgress");
        oresFound = nbt.getInt("OresFound");
        oresMined = nbt.getInt("OresMined");
        currentOreIndex = nbt.getInt("CurrentOreIndex");
        
        // 读取矿石位置列表
        orePositions.clear();
        if (nbt.contains("OrePositions")) {
            NbtList oreList = nbt.getList("OrePositions", 10); // 10 = NbtElement.COMPOUND_TYPE
            for (int i = 0; i < oreList.size(); i++) {
                NbtCompound posNbt = oreList.getCompound(i);
                BlockPos pos = new BlockPos(
                    posNbt.getInt("x"),
                    posNbt.getInt("y"),
                    posNbt.getInt("z")
                );
                orePositions.add(pos);
            }
        }
    }
    
    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, inventory);
        nbt.putBoolean("IsScanning", isScanning);
        nbt.putBoolean("IsMining", isMining);
        nbt.putInt("ScanProgress", scanProgress);
        nbt.putInt("MiningProgress", miningProgress);
        nbt.putInt("OresFound", oresFound);
        nbt.putInt("OresMined", oresMined);
        nbt.putInt("CurrentOreIndex", currentOreIndex);
        
        // 保存矿石位置列表
        NbtList oreList = new NbtList();
        for (BlockPos pos : orePositions) {
            NbtCompound posNbt = new NbtCompound();
            posNbt.putInt("x", pos.getX());
            posNbt.putInt("y", pos.getY());
            posNbt.putInt("z", pos.getZ());
            oreList.add(posNbt);
        }
        nbt.put("OrePositions", oreList);
    }
    
    @Override
    public Text getDisplayName() {
        return Text.translatable("block.chunkoreautominer.mining_core");
    }
    
    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new MiningCoreScreenHandler(syncId, playerInventory, this);
    }
    
    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(this.pos);
    }
    
    // UI状态查询方法
    public boolean isScanning() {
        return isScanning;
    }
    
    public boolean isMining() {
        return isMining;
    }
    
    public int getScanProgress() {
        return scanProgress;
    }
    
    public int getMaxScanTime() {
        return maxScanTime;
    }
    
    public int getTotalOres() {
        return oresFound;
    }
    
    public int getOresMined() {
        return oresMined;
    }
    
    // SidedInventory 接口实现
    @Override
    public int[] getAvailableSlots(Direction side) {
        // 只允许访问矿物存储区 (槽位 0-8)
        return new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8};
    }
    
    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        // 只允许在矿物存储区插入非镐子物品
        if (slot >= STORAGE_START && slot < STORAGE_START + STORAGE_SIZE) {
            return !(stack.getItem() instanceof PickaxeItem);
        }
        return false;
    }
    
    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        // 只允许从矿物存储区提取物品
        return slot >= STORAGE_START && slot < STORAGE_START + STORAGE_SIZE;
    }
}