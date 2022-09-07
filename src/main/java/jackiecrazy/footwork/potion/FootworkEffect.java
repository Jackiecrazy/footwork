package jackiecrazy.footwork.potion;

import jackiecrazy.footwork.capability.resources.CombatData;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.server.level.ServerLevel;

class FootworkEffect extends MobEffect {
    FootworkEffect(MobEffectCategory typeIn, int liquidColorIn) {
        super(typeIn, liquidColorIn);
    }

    @Override
    public void applyEffectTick(LivingEntity l, int amplifier) {
        if (this == FootworkEffects.RESTORATION.get()) {
            CombatData.getCap(l).addWounding(-amplifier);
        }
        if (this == FootworkEffects.REENERGIZATION.get()) {
            CombatData.getCap(l).addBurnout(-amplifier);
        }
        if (this == FootworkEffects.REFRESHMENT.get()) {
            CombatData.getCap(l).addFatigue(-amplifier);
        }
        if (this == FootworkEffects.FEAR.get() && l.level instanceof ServerLevel) {
            ((ServerLevel) l.level).sendParticles(ParticleTypes.DRIPPING_WATER, l.getX(), l.getY() + l.getBbHeight() / 2, l.getZ(), 5, l.getBbWidth() / 4, l.getBbHeight() / 4, l.getBbWidth() / 4, 0.5f);
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration % 20 == 1;
    }
}
