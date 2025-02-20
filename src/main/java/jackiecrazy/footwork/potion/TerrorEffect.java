package jackiecrazy.footwork.potion;

import jackiecrazy.footwork.utils.EffectUtils;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;

public class TerrorEffect extends FootworkEffect{
    TerrorEffect() {
        super(MobEffectCategory.HARMFUL, 0xfcfc00);
    }

    @Override
    public void applyInstantenousEffect(@Nullable Entity source, @Nullable Entity indirectSource, LivingEntity entityLivingBaseIn, int amplifier, double health) {
        if(source instanceof LivingEntity)
        EffectUtils.causeFear(entityLivingBaseIn, (LivingEntity) source, amplifier);
        else EffectUtils.causeFear(entityLivingBaseIn, entityLivingBaseIn.getKillCredit(), amplifier*20);
    }

    @Override
    public boolean isInstantenous() {
        return true;
    }

    @Override
    public void applyEffectTick(LivingEntity l, int amplifier) {
        l.removeEffect(this);
        EffectUtils.causeFear(l, l.getKillCredit(), amplifier*20);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }


}
