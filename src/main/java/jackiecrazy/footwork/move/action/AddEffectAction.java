package jackiecrazy.footwork.move.action;

import jackiecrazy.footwork.move.argument.Argument;
import jackiecrazy.footwork.move.condition.Condition;
import jackiecrazy.footwork.move.condition.FalseCondition;
import jackiecrazy.footwork.move.condition.TrueCondition;
import jackiecrazy.footwork.move.utils.ActionContext;
import jackiecrazy.footwork.move.argument.entity.TargetEntityArgument;
import jackiecrazy.footwork.utils.EffectUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.registries.ForgeRegistries;

public class AddEffectAction extends Action {
    private Argument<ResourceLocation> effect;
    private transient MobEffect me;
    private Argument<Double> potency, duration;
    private Argument<Entity> recipient = TargetEntityArgument.INSTANCE;
    private EffectUtils.StackingMethod stacking = EffectUtils.StackingMethod.NONE;
    private Condition forced = FalseCondition.INSTANCE;
    private Condition ambient = FalseCondition.INSTANCE, visible = TrueCondition.INSTANCE, show_icon = TrueCondition.INSTANCE;

    @Override
    public int perform(ActionContext actionContext) {
        if (me == null)
            me = ForgeRegistries.MOB_EFFECTS.getValue(effect.resolve(actionContext));
        if (me != null && recipient.resolve(actionContext) instanceof LivingEntity e) {
            final MobEffectInstance fx = new MobEffectInstance(me, duration.resolve(actionContext).intValue(), potency.resolve(actionContext).intValue(), ambient.resolve(actionContext), visible.resolve(actionContext), show_icon.resolve(actionContext));
            EffectUtils.attemptAddPot(e, EffectUtils.stackPot(e, fx, stacking), forced.resolve(actionContext));
        }
        return 0;
    }
}
