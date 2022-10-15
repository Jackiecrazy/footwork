package jackiecrazy.footwork.potion;

import jackiecrazy.footwork.utils.EffectUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffectCategory;

import javax.annotation.Nullable;

public class TerrorEffect extends FootworkEffect{
    TerrorEffect() {
        super(MobEffectCategory.HARMFUL, 0xfcfc00);
    }

    @Override
    public void m_19461_(@Nullable Entity source, @Nullable Entity indirectSource, LivingEntity entityLivingBaseIn, int amplifier, double health) {
        if(source instanceof LivingEntity)
        EffectUtils.causeFear(entityLivingBaseIn, (LivingEntity) source, amplifier);
        else EffectUtils.causeFear(entityLivingBaseIn, entityLivingBaseIn.m_21232_(), amplifier*20);
    }

    @Override
    public boolean m_8093_() {
        return true;
    }

    @Override
    public void m_6742_(LivingEntity l, int amplifier) {
        l.m_21195_(this);
        EffectUtils.causeFear(l, l.m_21232_(), amplifier*20);
    }

    @Override
    public boolean m_6584_(int duration, int amplifier) {
        return true;
    }


}
