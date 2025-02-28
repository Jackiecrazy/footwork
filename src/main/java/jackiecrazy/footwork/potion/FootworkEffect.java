package jackiecrazy.footwork.potion;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

class FootworkEffect extends MobEffect {
    FootworkEffect(MobEffectCategory typeIn, int liquidColorIn) {
        super(typeIn, liquidColorIn);
    }

    @Override
    public void applyEffectTick(LivingEntity l, int amplifier) {

        if (this == FootworkEffects.FEAR.get() && l.level() instanceof ServerLevel s) {
            s.sendParticles(ParticleTypes.DRIPPING_WATER, l.getX(), l.getY() + l.getBbHeight() / 2, l.getZ(), 5, l.getBbWidth() / 4, l.getBbHeight() / 4, l.getBbWidth() / 4, 0.5f);
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration % 20 == 1;
    }
}
