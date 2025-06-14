package com.chunkore.autominer.blockentity;

import com.chunkore.autominer.screen.LoggingCoreScreenHandler;
import com.chunkore.autominer.util.LogDetector;
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
import net.minecraft.item.AxeItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.ToolMaterials;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class LoggingCoreBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory, ImplementedInventory, SidedInventory {
    // 库存布局: 0-8原木存储(3x3), 9主斧头槽, 10-18加速斧头(3x3)
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(19, ItemStack.EMPTY);
    
    // 槽位常量
    public static final int STORAGE_START = 0;
    public static final int STORAGE_SIZE = 9;
    public static final int LOG_STORAGE_SIZE = STORAGE_SIZE; // 原木存储区大小
    public static final int MAIN_AXE_SLOT = 9;
    public static final int BOOST_AXE_START = 10;
    public static final int BOOST_AXE_SIZE = 9;
    public static final int INVENTORY_SIZE = 19; // 总库存大小
    
    // 状态变量
    private boolean isScanning = false;
    private boolean isCutting = false;
    private int scanProgress = 0;
    private int maxScanTime = 100; // 5秒 (20 ticks/秒)
    private int cuttingProgress = 0;
    private int logsFound = 0;
    private int logsCut = 0;
    
    // 原木位置列表
    private List<BlockPos> logPositions = new ArrayList<>();
    private int currentLogIndex = 0;
    
    public LoggingCoreBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.LOGGING_CORE_BLOCK_ENTITY, pos, state);
    }
    
    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }
    
    public static void tick(World world, BlockPos pos, BlockState state, LoggingCoreBlockEntity blockEntity) {
        if (world.isClient) return;
        
        // 检查是否接收到红石信号
        boolean powered = state.get(com.chunkore.autominer.block.LoggingCoreBlock.POWERED);
        if (!powered) {
            // 没有红石信号时停止所有操作
            if (blockEntity.isScanning) {
                blockEntity.isScanning = false;
                blockEntity.scanProgress = 0;
            }
            if (blockEntity.isCutting) {
                blockEntity.isCutting = false;
                blockEntity.cuttingProgress = 0;
                blockEntity.currentLogIndex = 0;
            }
            return;
        }
        
        if (blockEntity.isScanning) {
            blockEntity.tickScanning(world, pos);
        } else if (blockEntity.isCutting) {
            blockEntity.tickCutting(world, pos);
        } else if (!blockEntity.isScanning && !blockEntity.isCutting) {
            if (blockEntity.logPositions.isEmpty()) {
                // 没有原木位置，开始扫描
                blockEntity.startScanning(world, pos);
            } else {
                // 有原木位置但没在砍伐，开始砍伐
                blockEntity.startCutting(world, pos);
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
    
    private void tickCutting(World world, BlockPos pos) {
        // 检查是否有主斧头
        ItemStack mainAxe = inventory.get(MAIN_AXE_SLOT);
        if (mainAxe.isEmpty() || !(mainAxe.getItem() instanceof AxeItem)) {
            // 没有斧头，停止砍伐
            isCutting = false;
            cuttingProgress = 0;
            markDirty();
            return;
        }
        
        if (currentLogIndex >= logPositions.size()) {
            // 砍伐完成
            isCutting = false;
            currentLogIndex = 0;
            logPositions.clear();
            markDirty();
            return;
        }
        
        cuttingProgress++;
        
        // 计算砍伐速度（基础40 ticks除以速度倍数）
        int cuttingTime = calculateCuttingTime();
        
        if (cuttingProgress >= cuttingTime) {
            BlockPos logPos = logPositions.get(currentLogIndex);
            BlockState logState = world.getBlockState(logPos);
            
            // 检查原木是否仍然存在
            if (logState.isAir() || !LogDetector.isLog(logState.getBlock())) {
                // 原木已经不存在，直接跳到下一个
                currentLogIndex++;
                cuttingProgress = 0;
            } else {
                // 砍伐一个原木并消耗斧头耐久
                cutLog(world, logPos);
                damageAxe(mainAxe);
                currentLogIndex++;
                cuttingProgress = 0;
                logsCut++;
                
                // 播放砍伐音效
                world.playSound(null, pos, SoundEvents.BLOCK_WOOD_BREAK, SoundCategory.BLOCKS, 0.8f, 1.0f);
            }
        }
        
        markDirty();
    }
    
    private void startScanning(World world, BlockPos pos) {
        isScanning = true;
        scanProgress = 0;
        logPositions.clear();
        logsFound = 0;
        logsCut = 0;
        currentLogIndex = 0;
        markDirty();
    }
    
    private void startCutting(World world, BlockPos pos) {
        // 检查是否有主斧头
        ItemStack mainAxe = inventory.get(MAIN_AXE_SLOT);
        if (mainAxe.isEmpty() || !(mainAxe.getItem() instanceof AxeItem)) {
            return; // 没有斧头，无法开始砍伐
        }
        
        // 检查是否有原木可砍
        if (logPositions.isEmpty()) {
            return; // 没有原木，无法开始砍伐
        }
        
        isCutting = true;
        cuttingProgress = 0;
        // 不重置currentLogIndex，从上次停止的地方继续
        markDirty();
    }
    
    private void finishScanning(World world, BlockPos pos) {
        isScanning = false;
        scanProgress = 0;
        
        // 扫描当前区块的原木
        ChunkPos chunkPos = new ChunkPos(pos);
        logPositions = LogDetector.findLogsInChunk(world, chunkPos);
        logsFound = logPositions.size();
        
        // 播放扫描完成音效
        world.playSound(null, pos, SoundEvents.BLOCK_BEACON_ACTIVATE, SoundCategory.BLOCKS, 1.0f, 1.0f);
        
        markDirty();
    }
    
    private int calculateCuttingTime() {
        // 基础砍伐时间40 ticks (2秒)
        int baseTime = 40;
        
        // 计算速度倍数
        float speedMultiplier = getSpeedMultiplier();
        
        return Math.max(1, (int)(baseTime / speedMultiplier));
    }
    
    private float getSpeedMultiplier() {
        float multiplier = 1.0f;
        
        // 主斧头的材料加成
        ItemStack mainAxe = inventory.get(MAIN_AXE_SLOT);
        if (!mainAxe.isEmpty() && mainAxe.getItem() instanceof AxeItem axeItem) {
            ToolMaterial material = axeItem.getMaterial();
            if (material == ToolMaterials.WOOD) multiplier += 0.1f;
            else if (material == ToolMaterials.STONE) multiplier += 0.2f;
            else if (material == ToolMaterials.IRON) multiplier += 0.3f;
            else if (material == ToolMaterials.GOLD) multiplier += 0.5f;
            else if (material == ToolMaterials.DIAMOND) multiplier += 0.7f;
            else if (material == ToolMaterials.NETHERITE) multiplier += 1.0f;
        }
        
        // 加速斧头的材质和数量加成
        float boostAxeBonus = getBoostAxeSpeedBonus();
        multiplier += boostAxeBonus;
        
        return multiplier;
    }
    
    private float getBoostAxeSpeedBonus() {
        float bonus = 0.0f;
        
        // 检查加速斧头槽位
        for (int i = BOOST_AXE_START; i < BOOST_AXE_START + BOOST_AXE_SIZE; i++) {
            ItemStack boostAxe = inventory.get(i);
            if (!boostAxe.isEmpty() && boostAxe.getItem() instanceof AxeItem axeItem) {
                ToolMaterial material = axeItem.getMaterial();
                
                // 不同材质的斧头提供不同的加速分数
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
    
    private void cutLog(World world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        
        // 获取原木掉落物
        List<ItemStack> drops = Block.getDroppedStacks(state, (net.minecraft.server.world.ServerWorld) world, pos, null);
        
        // 尝试将掉落物放入存储槽位
        for (ItemStack drop : drops) {
            ItemStack remaining = drop.copy();
            
            // 尝试放入存储槽位
            for (int i = STORAGE_START; i < STORAGE_START + STORAGE_SIZE; i++) {
                ItemStack slotStack = inventory.get(i);
                if (slotStack.isEmpty()) {
                    inventory.set(i, remaining);
                    remaining = ItemStack.EMPTY;
                    break;
                } else if (ItemStack.canCombine(slotStack, remaining)) {
                    int canAdd = Math.min(remaining.getCount(), slotStack.getMaxCount() - slotStack.getCount());
                    slotStack.increment(canAdd);
                    remaining.decrement(canAdd);
                    if (remaining.isEmpty()) break;
                }
            }
            
            // 如果还有剩余物品，掉落到地面
            if (!remaining.isEmpty()) {
                ItemEntity itemEntity = new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, remaining);
                world.spawnEntity(itemEntity);
            }
        }
        
        // 移除原木方块
        world.setBlockState(pos, net.minecraft.block.Blocks.AIR.getDefaultState());
    }
    
    private void damageAxe(ItemStack axe) {
        if (axe.isDamageable()) {
            // 直接设置损坏值，避免传递null entity导致的崩溃
            axe.setDamage(axe.getDamage() + 1);
            if (axe.getDamage() >= axe.getMaxDamage()) {
                axe.decrement(1);
            }
        }
    }
    
    private void damageBoostAxes() {
        // 损坏所有加速斧头
        for (int i = BOOST_AXE_START; i < BOOST_AXE_START + BOOST_AXE_SIZE; i++) {
            ItemStack boostAxe = inventory.get(i);
            if (!boostAxe.isEmpty() && boostAxe.getItem() instanceof AxeItem && boostAxe.isDamageable()) {
                // 加速斧头损坏速度较慢，每次砍伐有30%概率损坏
                if (world.getRandom().nextFloat() < 0.3f) {
                    boostAxe.setDamage(boostAxe.getDamage() + 1);
                    if (boostAxe.getDamage() >= boostAxe.getMaxDamage()) {
                        // 斧头损坏，移除
                        inventory.set(i, ItemStack.EMPTY);
                    }
                }
            }
        }
    }
    
    // UI状态查询方法
    public boolean isScanning() {
        return isScanning;
    }
    
    public boolean isCutting() {
        return isCutting;
    }
    
    public boolean isLogging() {
        return isCutting;
    }
    
    public int getScanProgress() {
        return scanProgress;
    }
    
    public int getMaxScanTime() {
        return maxScanTime;
    }
    
    public int getTotalLogs() {
        return logsFound;
    }
    
    public int getLogsCut() {
        return logsCut;
    }
    
    public int getLogsChopped() {
        return logsCut;
    }
    
    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        Inventories.readNbt(nbt, inventory);
        isScanning = nbt.getBoolean("IsScanning");
        isCutting = nbt.getBoolean("IsCutting");
        scanProgress = nbt.getInt("ScanProgress");
        cuttingProgress = nbt.getInt("CuttingProgress");
        logsFound = nbt.getInt("LogsFound");
        logsCut = nbt.getInt("LogsCut");
        currentLogIndex = nbt.getInt("CurrentLogIndex");
        
        // 读取原木位置列表
        logPositions.clear();
        NbtList logPosList = nbt.getList("LogPositions", 10);
        for (int i = 0; i < logPosList.size(); i++) {
            NbtCompound posNbt = logPosList.getCompound(i);
            BlockPos pos = new BlockPos(posNbt.getInt("x"), posNbt.getInt("y"), posNbt.getInt("z"));
            logPositions.add(pos);
        }
    }
    
    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, inventory);
        nbt.putBoolean("IsScanning", isScanning);
        nbt.putBoolean("IsCutting", isCutting);
        nbt.putInt("ScanProgress", scanProgress);
        nbt.putInt("CuttingProgress", cuttingProgress);
        nbt.putInt("LogsFound", logsFound);
        nbt.putInt("LogsCut", logsCut);
        nbt.putInt("CurrentLogIndex", currentLogIndex);
        
        // 写入原木位置列表
        NbtList logPosList = new NbtList();
        for (BlockPos pos : logPositions) {
            NbtCompound posNbt = new NbtCompound();
            posNbt.putInt("x", pos.getX());
            posNbt.putInt("y", pos.getY());
            posNbt.putInt("z", pos.getZ());
            logPosList.add(posNbt);
        }
        nbt.put("LogPositions", logPosList);
    }
    
    @Override
    public Text getDisplayName() {
        return Text.translatable("block.chunkoreautominer.logging_core");
    }
    
    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new LoggingCoreScreenHandler(syncId, playerInventory, this);
    }
    
    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(this.pos);
    }
    
    public void dropInventoryItems(World world, BlockPos pos) {
        ItemScatterer.spawn(world, pos, this);
    }
    
    // SidedInventory implementation
    @Override
    public int[] getAvailableSlots(Direction side) {
        // 只允许从存储槽位输出物品
        int[] slots = new int[STORAGE_SIZE];
        for (int i = 0; i < STORAGE_SIZE; i++) {
            slots[i] = STORAGE_START + i;
        }
        return slots;
    }
    
    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return false; // 不允许从外部插入物品
    }
    
    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        // 只允许从存储槽位提取物品
        return slot >= STORAGE_START && slot < STORAGE_START + STORAGE_SIZE;
    }
}