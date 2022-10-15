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
    public void m_6742_(LivingEntity l, int amplifier) {
        if (this == FootworkEffects.RESTORATION.get()) {
            CombatData.getCap(l).addWounding(-amplifier);
        }
        if (this == FootworkEffects.REENERGIZATION.get()) {
            CombatData.getCap(l).addBurnout(-amplifier);
        }
        if (this == FootworkEffects.REFRESHMENT.get()) {
            CombatData.getCap(l).addFatigue(-amplifier);
        }
        if (this == FootworkEffects.FEAR.get() && l.f_19853_ instanceof ServerLevel) {
            ((ServerLevel) l.f_19853_).m_8767_(ParticleTypes.f_123803_, l.m_20185_(), l.m_20186_() + l.m_20206_() / 2, l.m_20189_(), 5, l.m_20205_() / 4, l.m_20206_() / 4, l.m_20205_() / 4, 0.5f);
        }
    }

    @Override
    public boolean m_6584_(int duration, int amplifier) {
        return duration % 20 == 1;
    }
}
