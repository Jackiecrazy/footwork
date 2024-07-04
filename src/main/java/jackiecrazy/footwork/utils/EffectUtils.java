package jackiecrazy.footwork.utils;

import jackiecrazy.footwork.capability.goal.GoalCapabilityProvider;
import jackiecrazy.footwork.entity.FootworkDataAttachments;
import jackiecrazy.footwork.mixin.EffectStartAccessor;
import jackiecrazy.footwork.potion.FootworkEffects;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

import java.util.Map;

public class EffectUtils {
    //so you think immunity to my potions is clever, eh?

    /**
     * Attempts to add the potion effect. If it fails, the function will *permanently* apply all the attribute modifiers.
     * Take that, wither.
     */
    public static boolean attemptAddPot(LivingEntity elb, MobEffectInstance pot) {
        Holder<MobEffect> p = pot.getEffect();
        elb.addEffect(pot);
        if (!elb.hasEffect(p)) {
            //I'm going to do it anyways, take that.
            p.value().addAttributeModifiers(elb.getAttributes(), pot.getAmplifier());
            elb.getActiveEffectsMap().put(pot.getEffect(), pot);
            ((EffectStartAccessor)elb).callOnEffectAdded(pot, elb);
            return false;
        } else {
            elb.getEffect(pot.getEffect()).update(pot);
        }
        return true;
    }

    /**
     * increases the potion amplifier on the entity, with options on the duration
     */
    public static MobEffectInstance stackPot(LivingEntity elb, MobEffectInstance toAdd, StackingMethod method) {
        Holder<MobEffect> p = toAdd.getEffect();
        MobEffectInstance pe = elb.getEffect(p);
        if (pe == null || method == StackingMethod.NONE) {
            //System.out.println("beep1");
            return toAdd;
        }
        //System.out.println(pe);
        int length = pe.getDuration();
        int potency = pe.getAmplifier() + 1 + toAdd.getAmplifier();
        //System.out.println(length);
        //System.out.println(potency);

        switch (method) {
            case ADD:
                length = toAdd.getDuration() + pe.getDuration();
                break;
            case MAXDURATION:
                length = Math.max(pe.getDuration(), toAdd.getDuration());
                break;
            case MAXPOTENCY:
                length = pe.getAmplifier() == toAdd.getAmplifier() ? Math.max(pe.getDuration(), toAdd.getDuration()) : pe.getAmplifier() > toAdd.getAmplifier() ? pe.getDuration() : toAdd.getDuration();
                break;
            case MINDURATION:
                length = Math.min(pe.getDuration(), toAdd.getDuration());
                break;
            case MINPOTENCY:
                length = pe.getAmplifier() == toAdd.getAmplifier() ? Math.min(pe.getDuration(), toAdd.getDuration()) : pe.getAmplifier() < toAdd.getAmplifier() ? pe.getDuration() : toAdd.getDuration();
                break;
            case ONLYADD:
                potency = toAdd.getAmplifier();
                length = toAdd.getDuration() + pe.getDuration();
                break;
        }
        //System.out.println(ret);
        return new MobEffectInstance(p, length, potency, pe.isAmbient(), pe.isVisible(), pe.showIcon());
    }

    public static int getEffectiveLevel(LivingEntity elb, Holder<MobEffect> p) {
        if (elb.getEffect(p) != null)
            return elb.getEffect(p).getAmplifier() + 1;
        return 0;
    }

    public static void causeFear(LivingEntity elb, LivingEntity applier, int duration) {
        attemptAddPot(elb, new MobEffectInstance(FootworkEffects.FEAR, duration, 0, true, false));
        if (elb instanceof Mob el) {
            el.getNavigation().stop();
            el.setTarget(null);
            el.setData(FootworkDataAttachments.FEAR_TARGET, applier);
        }
//        if (!elb.level.isClientSide) {
//            //PigEntity f=new PigEntity(EntityType.PIG, elb.world);
//            FearEntity f = new FearEntity(WarEntities.fear, elb.level);
//            f.setFearSource(applier);
//            f.setTetheringEntity(elb);
//            f.teleportTo(elb.getX(), elb.getY(), elb.getZ());
//            elb.level.addFreshEntity(f);
//        }
    }

    public enum StackingMethod {
        NONE,
        ADD,
        MAXDURATION,
        MAXPOTENCY,
        MINDURATION,
        MINPOTENCY,
        ONLYADD
    }
}
