package jackiecrazy.footwork.client.render;

import jackiecrazy.footwork.Footwork;
import jackiecrazy.footwork.entity.DummyEntity;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.resources.ResourceLocation;

public class NothingRender extends EntityRenderer<DummyEntity, EntityRenderState> {
    ResourceLocation nothing= ResourceLocation.fromNamespaceAndPath(Footwork.MODID,"nothing");

    @Override
    public EntityRenderState createRenderState() {
        return new EntityRenderState();
    }

    public NothingRender(EntityRendererProvider.Context p_174008_) {
        super(p_174008_);
    }

    public boolean shouldRender(DummyEntity livingEntity, Frustum camera, double camX, double camY, double camZ) {
        return false;
    }
}
