package jackiecrazy.footwork.client.render;

import jackiecrazy.footwork.entity.DummyEntity;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;

public class NothingRender extends EntityRenderer<DummyEntity> {
    ResourceLocation nothing= new ResourceLocation("footwork:nothing");

    @Override
    public boolean shouldRender(DummyEntity p_114491_, Frustum p_114492_, double p_114493_, double p_114494_, double p_114495_) {
        return false;
    }

    public NothingRender(EntityRendererProvider.Context p_174008_) {
        super(p_174008_);
    }

    @Override
    public ResourceLocation getTextureLocation(DummyEntity p_114482_) {
        return nothing;
    }
}
