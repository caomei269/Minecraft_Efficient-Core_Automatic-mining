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

public class LoggingCoreScreen extends HandledScreen<LoggingCoreScreenHandler> {
    private static final Identifier TEXTURE = new Identifier(ChunkOreAutoMiner.MOD_ID, "textures/gui/logging_core.png");
    
    public LoggingCoreScreen(LoggingCoreScreenHandler handler, PlayerInventory inventory, Text title) {
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
        LoggingCoreScreenHandler handler = this.handler;
        
    }
    

    
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
        context.drawText(this.textRenderer, Text.translatable("gui.chunkoreautominer.log_storage_area"), 8, 8, 0x404040, false);
        
        // 斧头区标题 (中间单个槽位，位置: x=80, y=36)
        context.drawText(this.textRenderer, Text.translatable("gui.chunkoreautominer.main_axe"), 75, 56, 0x404040, false);
        
        // 加速斧头区标题 (右侧3x3槽位，位置: x=116, y=18)
        context.drawText(this.textRenderer, Text.translatable("gui.chunkoreautominer.boost_axe"), 116, 8, 0x404040, false);
        
        // 绘制进度条标签
        // 状态显示已移除
    }
}