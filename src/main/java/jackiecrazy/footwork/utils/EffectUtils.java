package jackiecrazy.footwork.utils;

import jackiecrazy.footwork.potion.FootworkEffects;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

public class EffectUtils {
    //so you think immunity to my potions is clever, eh?

    /**
     * Attempts to add the potion effect. If it fails, the function will *permanently* apply all the attribute modifiers, with the option to stack them as well
     * Take that, wither!
     */
    public static boolean attemptAddPot(LivingEntity elb, MobEffectInstance pot, boolean stackWhenFailed) {
        Holder<MobEffect> mp = pot.getEffect();
        MobEffect p = mp.value();
        elb.addEffect(pot);
        if (!elb.hasEffect(mp)) {
            //I'm gonna do it anyways, take that.
            p.addAttributeModifiers(elb.getAttributes(), pot.getAmplifier());
            elb.getActiveEffectsMap().put(pot.getEffect(), pot);
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
        Holder<MobEffect> mp=toAdd.getEffect();
        MobEffect p = mp.value();
        MobEffectInstance pe = elb.getEffect(mp);
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
        return new MobEffectInstance(mp, length, potency, pe.isAmbient(), pe.isVisible(), pe.showIcon());
    }

    public static int getEffectiveLevel(LivingEntity elb, Holder<MobEffect> p) {
        if (elb.getEffect(p) != null)
            return elb.getEffect(p).getAmplifier() + 1;
        return 0;
    }

    public static void causeFear(LivingEntity elb, LivingEntity applier, int duration) {
        attemptAddPot(elb, new MobEffectInstance(FootworkEffects.FEAR, duration, 0), false);
        if (elb instanceof Mob) {
            Mob el = (Mob) elb;
            el.getNavigation().stop();
            el.setTarget(null);
            TargetingUtils.setFearTarget(elb, applier);
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
