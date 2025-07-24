package jackiecrazy.footwork.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.List;

public class WhiteTintedModel implements BakedModel {
    private final BakedModel original;
    private final TextureAtlasSprite whiteSprite;

    public WhiteTintedModel(BakedModel original) {
        this.original = original;
        this.whiteSprite = Minecraft.getInstance()
                .getTextureAtlas(TextureAtlas.LOCATION_BLOCKS)
                .apply(FootworkRenderTypes.white); // from white.png
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand) {
        List<BakedQuad> quads = original.getQuads(state, side, rand);
        return quads.stream().map(quad -> {
            return new BakedQuad(
                    quad.getVertices().clone(),  // same geometry
                    quad.getTintIndex(),
                    quad.getDirection(),
                    whiteSprite,                // <== replace sprite!
                    quad.isShade()
            );
        }).toList();
    }

    // Forward everything else
    @Override public boolean isCustomRenderer() { return original.isCustomRenderer(); }
    @Override public boolean usesBlockLight() { return original.usesBlockLight(); }
    @Override public boolean isGui3d() { return original.isGui3d(); }
    @Override public boolean useAmbientOcclusion() { return original.useAmbientOcclusion(); }
    @Override public TextureAtlasSprite getParticleIcon() { return whiteSprite; }
    @Override public ItemTransforms getTransforms() { return original.getTransforms(); }
    @Override public ItemOverrides getOverrides() { return original.getOverrides(); }
}
