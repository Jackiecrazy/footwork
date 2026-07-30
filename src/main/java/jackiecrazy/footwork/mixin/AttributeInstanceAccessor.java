package jackiecrazy.footwork.mixin;

import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Collection;

@org.spongepowered.asm.mixin.Mixin(net.minecraft.world.entity.ai.attributes.AttributeInstance.class)
public interface AttributeInstanceAccessor {
    @Invoker
    Collection<AttributeModifier> callGetModifiersOrEmpty(AttributeModifier.Operation p_22117_);
}
