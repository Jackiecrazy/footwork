package jackiecrazy.footwork.client.screen.dashboard;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import jackiecrazy.footwork.Footwork;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import javax.annotation.Nonnull;

public class PonderingOrb extends ImageButton {
    private final ResourceLocation interior;
    double xVelocity, yVelocity, partialX, partialY;
    float alpha, alphaPer;
    private DashboardScreen parent;
    private Component tip;

    public PonderingOrb(DashboardScreen parent, ResourceLocation interior, OnPress click, Component tooltip) {
        super(1, 1, 16, 16, new WidgetSprites(interior, interior), click);
        tip=tooltip;
        this.interior = interior;
        this.parent = parent;
    }

    void init(int atX, int atY, int size) {
        partialX = atX;
        partialY = atY;
        setX(Math.min(atX, parent.width - size));
        setY(Math.min(atY, parent.height - size));
        width = height = size;
        xVelocity = (Footwork.rand.nextFloat() - 0.5);
        yVelocity = (Footwork.rand.nextFloat() - 0.5);
        alpha = 0;
        alphaPer = 0.01f + Footwork.rand.nextFloat() / 100;
    }

    void init() {
        init(Footwork.rand.nextInt(parent.width - width), Footwork.rand.nextInt(parent.height - height), width);
    }

    boolean inRange(int base, int from, int to) {
        return base <= to && base >= from;
    }

    public void renderWidget(@Nonnull GuiGraphics matrixStack, int mouseX, int mouseY, float partialTicks) {
        //move around slowly unless you're hovering over it
        //may need to be in parent instead to handle collisions
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, this.interior);
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
        RenderSystem.setShaderColor(1, 1f, 1, alpha);
        RenderSystem.enableDepthTest();
        matrixStack.blit(interior, getX(), getY(), 0, 0, this.width + 1, this.height + 1, this.width, this.height);
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
        if (parent.focus == this&& parent.clickOn==null) {
            matrixStack.renderTooltip(parent.getMinecraft().font, tip, mouseX, mouseY);
            alpha += 0.03;
            alpha = Math.min(1.5f, alpha);
        } else {
            if (alpha > 2)
                alphaPer = -Mth.abs(alphaPer);
            if(parent.clickOn!=this) {
                xVelocity = Mth.clamp(xVelocity, -0.7, 0.7);
                yVelocity = Mth.clamp(yVelocity, -0.7, 0.7);
                if(parent.clickOn!=null)
                    alpha-=0.1;
                else if (alpha < -0.5)
                    init();
                else alpha += alphaPer;
            }
            //handle literal edge cases
            if (partialX + width + xVelocity >= parent.width)
                xVelocity = -Math.abs(xVelocity);
            if (partialX + xVelocity <= 0)
                xVelocity = Math.abs(xVelocity);
            if (partialY + height + yVelocity >= parent.height)
                yVelocity = -Math.abs(yVelocity);
            if (partialY + yVelocity <= 0)
                yVelocity = Math.abs(yVelocity);
            partialX += xVelocity;
            partialY += yVelocity;
            setX( (int) partialX);
            setY((int) partialY);
        }
        isHovered = hovered(mouseX, mouseY);
    }

    private boolean hovered(int mouseX, int mouseY) {
        double dx = getX() + width / 2d - mouseX;
        double dy = getY() + height / 2d - mouseY;
        double distance = (dx * dx + dy * dy);
        if (distance <= (width * height) / 4f) {
            parent.focus = this;
            return true;
        }
        if (parent.focus == this) parent.focus = null;
        return false;
    }
}
