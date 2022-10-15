package jackiecrazy.footwork.utils;

import jackiecrazy.footwork.capability.goal.GoalCapabilityProvider;
import jackiecrazy.footwork.potion.FootworkEffects;
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
     * Attempts to add the potion effect. If it fails, the function will *permanently* apply all the attribute modifiers, with the option to stack them as well
     * Take that, wither!
     */
    public static boolean attemptAddPot(LivingEntity elb, MobEffectInstance pot, boolean stackWhenFailed) {
        MobEffect p = pot.m_19544_();
        elb.m_7292_(pot);
        if (!elb.m_21023_(p)) {
            //I'm gonna do it anyways, take that.
            for (Map.Entry<Attribute, AttributeModifier> e : p.m_19485_().entrySet()) {
                final AttributeInstance attribute = elb.m_21051_(e.getKey());
                if (attribute != null) {
                    if (stackWhenFailed) {
                        AttributeModifier am = attribute.m_22111_(e.getValue().m_22209_());
                        if (am != null && am.m_22217_() == e.getValue().m_22217_()) {
                            AttributeModifier apply = new AttributeModifier(e.getValue().m_22209_(), e.getValue().m_22214_(), am.m_22218_() + e.getValue().m_22218_(), am.m_22217_());
                            attribute.m_22120_(e.getValue().m_22209_());
                            attribute.m_22118_(apply);
                        } else attribute.m_22118_(e.getValue());
                    } else {
                        attribute.m_22120_(e.getValue().m_22209_());
                        attribute.m_22118_(e.getValue());
                    }
                }
            }
            elb.m_21221_().put(pot.m_19544_(), pot);
            return false;
        } else {
            elb.m_21124_(pot.m_19544_()).m_19558_(pot);
        }
        return true;
    }

    /**
     * increases the potion amplifier on the entity, with options on the duration
     */
    public static MobEffectInstance stackPot(LivingEntity elb, MobEffectInstance toAdd, StackingMethod method) {
        MobEffect p = toAdd.m_19544_();
        MobEffectInstance pe = elb.m_21124_(p);
        if (pe == null || method == StackingMethod.NONE) {
            //System.out.println("beep1");
            return toAdd;
        }
        //System.out.println(pe);
        int length = pe.m_19557_();
        int potency = pe.m_19564_() + 1 + toAdd.m_19564_();
        //System.out.println(length);
        //System.out.println(potency);

        switch (method) {
            case ADD:
                length = toAdd.m_19557_() + pe.m_19557_();
                break;
            case MAXDURATION:
                length = Math.max(pe.m_19557_(), toAdd.m_19557_());
                break;
            case MAXPOTENCY:
                length = pe.m_19564_() == toAdd.m_19564_() ? Math.max(pe.m_19557_(), toAdd.m_19557_()) : pe.m_19564_() > toAdd.m_19564_() ? pe.m_19557_() : toAdd.m_19557_();
                break;
            case MINDURATION:
                length = Math.min(pe.m_19557_(), toAdd.m_19557_());
                break;
            case MINPOTENCY:
                length = pe.m_19564_() == toAdd.m_19564_() ? Math.min(pe.m_19557_(), toAdd.m_19557_()) : pe.m_19564_() < toAdd.m_19564_() ? pe.m_19557_() : toAdd.m_19557_();
                break;
            case ONLYADD:
                potency = toAdd.m_19564_();
                length = toAdd.m_19557_() + pe.m_19557_();
                break;
        }
        //System.out.println(ret);
        return new MobEffectInstance(p, length, potency, pe.m_19571_(), pe.m_19572_(), pe.m_19575_());
    }

    public static int getEffectiveLevel(LivingEntity elb, MobEffect p) {
        if (elb.m_21124_(p) != null)
            return elb.m_21124_(p).m_19564_() + 1;
        return 0;
    }

    public static void causeFear(LivingEntity elb, LivingEntity applier, int duration) {
        attemptAddPot(elb, new MobEffectInstance(FootworkEffects.FEAR.get(), duration, 0), false);
        if (elb instanceof Mob) {
            Mob el = (Mob) elb;
            el.m_21573_().m_26573_();
            el.m_6710_(null);
            GoalCapabilityProvider.getCap(elb).ifPresent(a->a.setFearSource(applier));
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
