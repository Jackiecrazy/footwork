package jackiecrazy.footwork.move.action;

import jackiecrazy.footwork.move.argument.Argument;
import jackiecrazy.footwork.move.utils.ActionContext;
import jackiecrazy.footwork.move.utils.ArgumentContext;
import jackiecrazy.footwork.move.argument.entity.CasterEntityArgument;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.UUID;

public class AddAttributeModifierAction extends Action {
    private Argument<ResourceLocation> attribute;
    private transient Attribute attr;
    private Argument<Double> amount;
    private AttributeModifier.Operation operation;
    private String name;
    private UUID uuid;
    private Argument<Entity> recipient = CasterEntityArgument.INSTANCE;

    @Override
    public int perform(ActionContext actionContext) {
        if (attr == null) attr = ForgeRegistries.ATTRIBUTES.getValue(attribute.resolve(actionContext));
        if (attr != null && recipient.resolve(actionContext) instanceof LivingEntity ent && ent.getAttribute(attr) != null) {
            ent.getAttribute(attr).removeModifier(uuid);
            AttributeModifier am = new AttributeModifier(uuid, name, amount.resolve(actionContext), operation);
            ent.getAttribute(attr).addTransientModifier(am);
        }
        return 0;
    }
}
