package jackiecrazy.footwork.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraftforge.common.ForgeMod;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class DummyEntity extends Mob {

    public DummyEntity setTicksToLive(int ticksToLive) {
        this.ticksToLive = ticksToLive;
        return this;
    }

    private int ticksToLive;
    private LivingEntity boundTo;

    protected DummyEntity(EntityType<? extends DummyEntity> p_21368_, Level p_21369_) {
        super(p_21368_, p_21369_);
        setNoAi(true);
        setInvisible(true);
        ticksToLive = 100;
    }

    @Override
    public boolean shouldRender(double p_20296_, double p_20297_, double p_20298_) {
        return false;
    }

    public static AttributeSupplier.Builder createAttributes()
    {
        return LivingEntity.createLivingAttributes();
    }

    public LivingEntity getBoundTo() {
        return boundTo;
    }

    public DummyEntity setBoundTo(LivingEntity boundTo) {
        this.boundTo = boundTo;
        return this;
    }

    @Override
    public void tick() {
        super.tick();
        if (ticksToLive-- < 0) {
            remove(RemovalReason.KILLED);
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        tag.putInt("tickstolive", ticksToLive);
        super.addAdditionalSaveData(tag);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        ticksToLive = tag.getInt("tickstolive");
        super.readAdditionalSaveData(tag);
    }

    @Nullable
    protected SoundEvent getHurtSound(DamageSource p_21239_) {
        return null;
    }

    @Nullable
    protected SoundEvent getDeathSound() {
        return null;
    }


}
