package com.chunkore.autominer.screen;

import com.chunkore.autominer.ChunkOreAutoMiner;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class MiningCoreScreen extends HandledScreen<MiningCoreScreenHandler> {
    private static final Identifier TEXTURE = new Identifier(ChunkOreAutoMiner.MOD_ID, "textures/gui/mining_core.png");
    
    public MiningCoreScreen(MiningCoreScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundWidth = 176;  // 设置GUI宽度以适配176x166纹理
        this.backgroundHeight = 166;
        this.playerInventoryTitleY = this.backgroundHeight - 94;
    }
    
    @Override
    protected void init() {
        super.init();
        // 设置标题位置
        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;
        titleY = 6;
    }
    
    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, TEXTURE);
        
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        
        // 绘制背景 - 使用完整的UV坐标防止纹理缩放问题
        context.drawTexture(TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight, 176, 166);
        
        // 绘制进度条
        drawProgressBars(context, x, y);
    }
    
    private void drawProgressBars(DrawContext context, int x, int y) {
        MiningCoreScreenHandler handler = this.handler;
        
        // 扫描进度条 - 适配176x166 GUI尺寸
        if (handler.isScanning()) {
            int progress = handler.getScanProgress();
            int maxProgress = handler.getMaxScanTime();
            float progressPercent = (float) progress / maxProgress;
            
            // 绘制扫描进度条，调整位置以适配176像素宽度
            drawEnhancedProgressBar(context, x + 13, y + 70, 150, 12, progressPercent, 
                0xFF2D5A2D, 0xFF4CAF50, 0xFF66BB6A, "扫描中...");
        }
        
        // 挖掘进度条 - 适配176x166 GUI尺寸
        if (handler.isMining()) {
            int totalOres = handler.getTotalOres();
            int oresMined = handler.getOresMined();
            float progressPercent = totalOres > 0 ? (float) oresMined / totalOres : 0.0f;
            
            // 绘制挖掘进度条，调整位置以适配176像素宽度
            drawEnhancedProgressBar(context, x + 13, y + 85, 150, 12, progressPercent,
                0xFF1A237E, 0xFF2196F3, 0xFF42A5F5, "挖掘中...");
        }
        
        // 取消红石状态指示器
    }
    
    private void drawEnhancedProgressBar(DrawContext context, int x, int y, int width, int height, 
                                       float progress, int darkColor, int mainColor, int lightColor, String label) {
        // 绘制外边框
        context.fill(x - 1, y - 1, x + width + 1, y + height + 1, 0xFF000000);
        
        // 绘制背景
        context.fill(x, y, x + width, y + height, 0xFF404040);
        
        // 绘制进度
        if (progress > 0) {
            int progressWidth = (int) (width * progress);
            
            // 主进度条
            context.fill(x, y, x + progressWidth, y + height, mainColor);
            
            // 顶部高光
            context.fill(x, y, x + progressWidth, y + 2, lightColor);
            
            // 底部阴影
            context.fill(x, y + height - 2, x + progressWidth, y + height, darkColor);
            
            // 动画效果（闪烁边缘）
            if (progress < 1.0f) {
                long time = System.currentTimeMillis();
                float alpha = (float) (0.5 + 0.5 * Math.sin(time * 0.01));
                int animColor = (int) (255 * alpha) << 24 | (lightColor & 0x00FFFFFF);
                context.fill(x + progressWidth - 2, y, x + progressWidth, y + height, animColor);
            }
        }
    }
    
    // 已删除红石指示器方法
    
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
        
        // 绘制状态文本
        drawStatusText(context);
    }
    
    private void drawStatusText(DrawContext context) {
        // 取消所有状态和红石提示文本显示
    }
    
    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        // 绘制标题
        context.drawText(this.textRenderer, this.title, this.titleX, this.titleY, 0x404040, false);
        
        // 绘制玩家库存标题
        context.drawText(this.textRenderer, this.playerInventoryTitle, this.playerInventoryTitleX, this.playerInventoryTitleY, 0x404040, false);
        
        // 绘制区域小标题
        // 储物区标题 (左侧3x3槽位，位置: x=8, y=18)
        context.drawText(this.textRenderer, Text.translatable("gui.chunkoreautominer.storage_area"), 8, 8, 0x404040, false);
        
        // 主镐子区标题 (中间单个槽位，位置: x=80, y=36)
        context.drawText(this.textRenderer, Text.translatable("gui.chunkoreautominer.main_pickaxe"), 75, 56, 0x404040, false);
        
        // 加速镐子区标题 (右侧3x3槽位，位置: x=116, y=18)
        context.drawText(this.textRenderer, Text.translatable("gui.chunkoreautominer.boost_pickaxe"), 116, 8, 0x404040, false);
        
        // 绘制进度条标签
        MiningCoreScreenHandler handler = this.handler;
        
        if (handler.isScanning()) {
            // 扫描进度百分比
            int progress = handler.getScanProgress();
            int maxProgress = handler.getMaxScanTime();
            float percent = (float) progress / maxProgress * 100;
            String percentText = String.format("%.1f%%", percent);
            
            // 在进度条右侧显示百分比，适配176像素宽度
            int percentX = 169 - textRenderer.getWidth(percentText);
            context.drawText(textRenderer, percentText, percentX, 73, 0xFFFFFF, false);
            
            // 进度条标签，调整位置以匹配进度条
            context.drawText(textRenderer, "扫描", 16, 73, 0xFFFFFF, false);
        }
        
        if (handler.isMining()) {
            // 挖掘进度百分比
            int totalOres = handler.getTotalOres();
            int oresMined = handler.getOresMined();
            float percent = totalOres > 0 ? (float) oresMined / totalOres * 100 : 0;
            String percentText = String.format("%.1f%%", percent);
            
            // 在进度条右侧显示百分比，适配176像素宽度
            int percentX = 169 - textRenderer.getWidth(percentText);
            context.drawText(textRenderer, percentText, percentX, 88, 0xFFFFFF, false);
            
            // 进度条标签，调整位置以匹配进度条
            context.drawText(textRenderer, "挖掘", 16, 88, 0xFFFFFF, false);
        }
        
        // 取消红石指示器标签
    }
}