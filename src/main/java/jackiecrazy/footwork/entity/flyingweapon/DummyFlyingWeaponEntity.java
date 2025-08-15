package jackiecrazy.footwork.entity.flyingweapon;

import jackiecrazy.footwork.Footwork;
import jackiecrazy.footwork.move.motionframe.MotionFrame;
import jackiecrazy.footwork.move.motionframe.MotionManagers;
import jackiecrazy.footwork.utils.GeneralUtils;
import jackiecrazy.footwork.utils.TargetingUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector4d;

import java.util.List;

public class DummyFlyingWeaponEntity extends FlyingItemEntity {
    public DummyFlyingWeaponEntity(EntityType<? extends FlyingItemEntity> type,
                                   Level level) {
        super(type, level);
        final ItemStack stack = new ItemStack(Items.IRON_SWORD);
        stack.enchant(Enchantments.ALL_DAMAGE_PROTECTION,1);
        setHeldItem(stack);
        //setUniversalOffset(new Vec3(0,0,4));
        setIdlePose(new MotionManagers.FixedMM(new MotionFrame(new Vec3(0, 0, 1), new Vec3(0, 0, 0), new Vector4d(0, 0, 1, 0)), 10));
        this.setUniversalOffset(new Vec3(-1.3,0,0));
    }

    @Override
    protected void onHitEntity(List<Entity> targets) {
        if (!level().isClientSide)
            targets.forEach(a -> {
                a.setSecondsOnFire(1);
                a.invulnerableTime = 0;
                GeneralUtils.attack(getOwner(), a);
            });
    }

    @Override
    protected void onHitBlock(BlockPos blockPos, Direction hitFace, Vec3 location) {

    }

    @Override
    public void tick() {
        //setInteractionRange(3 + 2 * Mth.sin(Mth.DEG_TO_RAD * tickCount * 10));
        setShouldRender(FlyingWeaponEffect.BIG_SHADOW, true);
        setShouldRender(FlyingWeaponEffect.AFTERIMAGE, false);
        setShouldRender(FlyingWeaponEffect.TRAIL, false);
        setShouldRender(FlyingWeaponEffect.WEAPON, true);
        if (getOwner() == null) {
            setOwner(level().getNearestPlayer(this, 16));
        }
        if (!level().isClientSide && getMotionReferent() == null || getMotionReferent() == getOwner()) {
            for (Entity e : level().getEntities(this, this.getBoundingBox().inflate(16), a -> !TargetingUtils.isAlly(a, this))) {
                if (!(e instanceof FlyingItemEntity)) {
                    setMotionReferent(e);
                    setUniversalOffset(Vec3.ZERO);
                    break;
                }
            }
        }
        if(getMotionReferent()==getOwner()){
            setUniversalOffset(new Vec3(1.3,0,0));
        }

        super.tick();
    }

    @Override
    protected void returnToIdle() {
        super.returnToIdle();
        setTransitioning(false);
        //provisional. Used to test movement.

        animProgress++;
        if (animProgress > 20) {
            queuePath(EVERYONE.get(Footwork.rand.nextInt(EVERYONE.size())), 2, 3);
            setTransitioning(false);
            while (!trailHistory.isEmpty()) trailHistory.pop();
            //setIdlePose(idlePose == firstIdle ? secondIdle : firstIdle);
            animProgress = 0;
        }
        //recalculatedOrientation = recalculateOrientation(null, update.renderOrientation(), (float) 0.1f);
    }

    @Override
    public boolean shouldRender(double p_20296_, double p_20297_, double p_20298_) {
        return true;
    }
}
