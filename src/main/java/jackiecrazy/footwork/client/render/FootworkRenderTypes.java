package jackiecrazy.footwork.client.render;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;

public class FootworkRenderTypes extends RenderType {
    public static final ResourceLocation white= new ResourceLocation("footwork", "textures/white.png");
    public static final RenderType CURRENTLY_WORKS = RenderType.create(
            "ghost_item",
            DefaultVertexFormat.BLOCK,
            VertexFormat.Mode.QUADS,
            256,
            false,
            true,
            RenderType.CompositeState.builder()
                    .setShaderState(RenderStateShard.RENDERTYPE_ENTITY_TRANSLUCENT_SHADER)
                    .setTextureState(new RenderStateShard.TextureStateShard(
                            TextureAtlas.LOCATION_BLOCKS,
                            false,
                            false
                    ))
                    .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                    .setDepthTestState(RenderStateShard.LEQUAL_DEPTH_TEST) // Keep depth testing
                    .setWriteMaskState(RenderStateShard.COLOR_DEPTH_WRITE)
                    .createCompositeState(true)
    );

    public static final RenderType WHITE_GHOST = RenderType.create(
            "white_ghost",
            DefaultVertexFormat.BLOCK,
            VertexFormat.Mode.QUADS,
            256,
            false,
            true,
            RenderType.CompositeState.builder()
                    .setShaderState(RenderStateShard.RENDERTYPE_ENTITY_TRANSLUCENT_SHADER)
                    .setTextureState(new RenderStateShard.TextureStateShard(
                            new ResourceLocation("yourmod", "textures/misc/white.png"),
                            false,
                            false
                    ))
                    .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                    .setDepthTestState(RenderStateShard.LEQUAL_DEPTH_TEST)
                    .setWriteMaskState(RenderStateShard.COLOR_DEPTH_WRITE)
                    .setLightmapState(RenderStateShard.LIGHTMAP) // Enable fullbright support
                    .setOverlayState(RenderStateShard.OVERLAY)   // Enable overlay (optional)
                    .createCompositeState(true)
    );


    public FootworkRenderTypes(String p_173178_,
                               VertexFormat p_173179_,
                               VertexFormat.Mode p_173180_,
                               int p_173181_,
                               boolean p_173182_,
                               boolean p_173183_,
                               Runnable p_173184_,
                               Runnable p_173185_) {
        super(p_173178_, p_173179_, p_173180_, p_173181_, p_173182_, p_173183_, p_173184_, p_173185_);
    }
}
