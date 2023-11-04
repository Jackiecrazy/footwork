package jackiecrazy.footwork.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

public class GuiComponent {
    public static void blit(PoseStack pose, int p_282225_, int p_281487_, int p_281985_, int p_281329_, int p_283035_, TextureAtlasSprite p_281614_) {
        innerBlit(pose, p_281614_.atlasLocation(), p_282225_, p_282225_ + p_281329_, p_281487_, p_281487_ + p_283035_, p_281985_, p_281614_.getU0(), p_281614_.getU1(), p_281614_.getV0(), p_281614_.getV1());
    }

    public static void blit(PoseStack pose, int p_282416_, int p_282989_, int p_282618_, int p_282755_, int p_281717_, TextureAtlasSprite p_281874_, float p_283559_, float p_282730_, float p_283530_, float p_282246_) {
        innerBlit(pose, p_281874_.atlasLocation(), p_282416_, p_282416_ + p_282755_, p_282989_, p_282989_ + p_281717_, p_282618_, p_281874_.getU0(), p_281874_.getU1(), p_281874_.getV0(), p_281874_.getV1(), p_283559_, p_282730_, p_283530_, p_282246_);
    }

    public static void blit(PoseStack pose, ResourceLocation p_283377_, int p_281970_, int p_282111_, int p_283134_, int p_282778_, int p_281478_, int p_281821_) {
        blit(pose, p_283377_, p_281970_, p_282111_, 0, (float)p_283134_, (float)p_282778_, p_281478_, p_281821_, 256, 256);
    }

    public static void blit(PoseStack pose, ResourceLocation p_283573_, int p_283574_, int p_283670_, int p_283545_, float p_283029_, float p_283061_, int p_282845_, int p_282558_, int p_282832_, int p_281851_) {
        blit(pose, p_283573_, p_283574_, p_283574_ + p_282845_, p_283670_, p_283670_ + p_282558_, p_283545_, p_282845_, p_282558_, p_283029_, p_283061_, p_282832_, p_281851_);
    }

    public static void blit(PoseStack pose, ResourceLocation p_282034_, int p_283671_, int p_282377_, int p_282058_, int p_281939_, float p_282285_, float p_283199_, int p_282186_, int p_282322_, int p_282481_, int p_281887_) {
        blit(pose, p_282034_, p_283671_, p_283671_ + p_282058_, p_282377_, p_282377_ + p_281939_, 0, p_282186_, p_282322_, p_282285_, p_283199_, p_282481_, p_281887_);
    }

    public static void blit(PoseStack pose, ResourceLocation p_283272_, int p_283605_, int p_281879_, float p_282809_, float p_282942_, int p_281922_, int p_282385_, int p_282596_, int p_281699_) {
        blit(pose, p_283272_, p_283605_, p_281879_, p_281922_, p_282385_, p_282809_, p_282942_, p_281922_, p_282385_, p_282596_, p_281699_);
    }

    public static void blit(PoseStack pose, ResourceLocation p_282639_, int p_282732_, int p_283541_, int p_281760_, int p_283298_, int p_283429_, int p_282193_, int p_281980_, float p_282660_, float p_281522_, int p_282315_, int p_281436_) {
        innerBlit(pose, p_282639_, p_282732_, p_283541_, p_281760_, p_283298_, p_283429_, (p_282660_ + 0.0F) / (float)p_282315_, (p_282660_ + (float)p_282193_) / (float)p_282315_, (p_281522_ + 0.0F) / (float)p_281436_, (p_281522_ + (float)p_281980_) / (float)p_281436_);
    }

    public static void innerBlit(PoseStack pose, ResourceLocation p_283461_, int p_281399_, int p_283222_, int p_283615_, int p_283430_, int p_281729_, float p_283247_, float p_282598_, float p_282883_, float p_283017_) {
        RenderSystem.setShaderTexture(0, p_283461_);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        Matrix4f matrix4f = pose.last().pose();
        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferbuilder.vertex(matrix4f, (float)p_281399_, (float)p_283615_, (float)p_281729_).uv(p_283247_, p_282883_).endVertex();
        bufferbuilder.vertex(matrix4f, (float)p_281399_, (float)p_283430_, (float)p_281729_).uv(p_283247_, p_283017_).endVertex();
        bufferbuilder.vertex(matrix4f, (float)p_283222_, (float)p_283430_, (float)p_281729_).uv(p_282598_, p_283017_).endVertex();
        bufferbuilder.vertex(matrix4f, (float)p_283222_, (float)p_283615_, (float)p_281729_).uv(p_282598_, p_282883_).endVertex();
        BufferUploader.drawWithShader(bufferbuilder.end());
    }

    public static void innerBlit(PoseStack pose, ResourceLocation p_283254_, int p_283092_, int p_281930_, int p_282113_, int p_281388_, int p_283583_, float p_281327_, float p_281676_, float p_283166_, float p_282630_, float p_282800_, float p_282850_, float p_282375_, float p_282754_) {
        RenderSystem.setShaderTexture(0, p_283254_);
        RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
        RenderSystem.enableBlend();
        Matrix4f matrix4f = pose.last().pose();
        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);
        bufferbuilder.vertex(matrix4f, (float)p_283092_, (float)p_282113_, (float)p_283583_).color(p_282800_, p_282850_, p_282375_, p_282754_).uv(p_281327_, p_283166_).endVertex();
        bufferbuilder.vertex(matrix4f, (float)p_283092_, (float)p_281388_, (float)p_283583_).color(p_282800_, p_282850_, p_282375_, p_282754_).uv(p_281327_, p_282630_).endVertex();
        bufferbuilder.vertex(matrix4f, (float)p_281930_, (float)p_281388_, (float)p_283583_).color(p_282800_, p_282850_, p_282375_, p_282754_).uv(p_281676_, p_282630_).endVertex();
        bufferbuilder.vertex(matrix4f, (float)p_281930_, (float)p_282113_, (float)p_283583_).color(p_282800_, p_282850_, p_282375_, p_282754_).uv(p_281676_, p_283166_).endVertex();
        BufferUploader.drawWithShader(bufferbuilder.end());
        RenderSystem.disableBlend();
    }
}
