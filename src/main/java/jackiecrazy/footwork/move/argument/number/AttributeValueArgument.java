package jackiecrazy.footwork.move.argument.number;

import jackiecrazy.footwork.move.argument.Argument;
import jackiecrazy.footwork.move.utils.ArgumentContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraftforge.registries.ForgeRegistries;

public class AttributeValueArgument implements Argument<Double> {
    private Argument<Entity> reference_point;
    private Argument<ResourceLocation> attribute;
    private Attribute attr;

    @Override
    public Double resolve(ArgumentContext argumentContext) {
        if (attr == null)
            attr = ForgeRegistries.ATTRIBUTES.getValue(attribute.resolve(argumentContext));
        if (attr != null && reference_point.resolve(argumentContext) instanceof LivingEntity le)
            return le.getAttributeValue(attr);
        return attr.getDefaultValue();
    }
}
