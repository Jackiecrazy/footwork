package jackiecrazy.footwork.entity.flyingweapon;

import jackiecrazy.footwork.move.motionframe.FrameEffects;
import jackiecrazy.footwork.move.motionframe.MotionFrame;
import jackiecrazy.footwork.move.motionframe.MotionGroup;
import jackiecrazy.footwork.move.motionframe.MotionManagers;
import jackiecrazy.footwork.utils.EasingFunctionEnum;
import jackiecrazy.footwork.utils.GeneralUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import org.joml.Vector4d;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class DummyFlyingWeaponEntity extends FlyingItemEntity {
    private final ArrayList<Entity> alreadyHit=new ArrayList<>();
    public DummyFlyingWeaponEntity(EntityType<? extends FlyingItemEntity> type,
                                   Level level) {
        super(type, level);
        final ItemStack stack = new ItemStack(Items.IRON_SWORD);
        stack.enchant(Enchantments.ALL_DAMAGE_PROTECTION,1);
        //setHeldItem(ItemStack.EMPTY);
        setHeldItem(stack);
        //setFlipRender(true);
        //setUniversalOffset(new Vec3(0,0,4));
        final MotionManagers.FixedMM pose = new MotionManagers.FixedMM(new MotionFrame(new Vec3(0, 0, 1), new Vec3(0, 0, 0), new Vector4d(0, 0, 1, 0)), 100);
        //pose.setAngularVelocity(new Vector3f(0.1f,0,0));
        setIdlePose(pose);
        this.setUniversalOffset(new Vec3(-1.3,0,0));
    }

    @Override
    protected boolean onHitEntity(List<Entity> targets) {
        AtomicBoolean hit= new AtomicBoolean(false);
        if (!level().isClientSide)
            targets.forEach(a -> {
                if(!alreadyHit.contains(a)) {
                    a.setSecondsOnFire(1);
                    a.invulnerableTime = 0;
                    GeneralUtils.attack(getOwner(), a);
                    hit.set(true);
                    alreadyHit.add(a);
                }
            });
        return hit.get();
    }

    @Override
    protected void onHitBlock(BlockPos blockPos, Direction hitFace, Vec3 location) {

    }

    @Override
    public void tick() {
        //setInteractionRange(3 + 2 * Mth.sin(Mth.DEG_TO_RAD * tickCount * 10));
//        setEffect(FlyingWeaponEffect.BIG_SHADOW, false);
//        setEffect(FlyingWeaponEffect.AFTERIMAGE, false);
//        setEffect(FlyingWeaponEffect.TRAIL, false);
//        setEffect(FlyingWeaponEffect.WEAPON, true);
        if (getOwner() == null) {
            setOwner(level().getNearestPlayer(this, 16));
        }
//        if (!level().isClientSide &&isIdle() && getMotionTarget() == null || getMotionTarget() == getOwner()) {
//            for (Entity e : level().getEntities(this, this.getBoundingBox().inflate(16), a -> !TargetingUtils.isAlly(a, this))) {
//                if (!(e instanceof FlyingItemEntity)) {
//                    setMotionTarget(e);
//                    setUniversalOffset(Vec3.ZERO);
//                    break;
//                }
//            }
//        }
        if(getMotionTarget()==getOwner()){
            setUniversalOffset(new Vec3(1.3,0,0));
        }

        super.tick();
    }

    @Override
    protected void updateFrameEffects(FrameEffects effects) {
        if(effects!=null&&effects!=currentEffects){
            if(effects.getRange() >=0)setInteractionRange((float) effects.getRange());
            if(effects.getEffects() !=null)setEffect(effects.getEffects().toArray(new FlyingWeaponEffect[0]));
        }
        currentEffects=effects;
    }

    @Override
    protected void returnToIdle(int duration) {
        super.returnToIdle(duration);
        //lock(getOwner());
        setIntangible(true);
        alreadyHit.clear();
        //provisional. Used to test movement.
        setState(STATE.FOLLOW);
        setEffect(FlyingWeaponEffect.WEAPON);
        animProgress++;
        if (animProgress > 20) {
            setHeldItem(new ItemStack(Items.IRON_AXE));
            List<MotionFrame> loop=List.of(
                    new MotionFrame(new Vec3(0, 0, 1), new Vec3(0, 0, 1), -90, new FrameEffects().setEffects(FlyingWeaponEffect.WEAPON, FlyingWeaponEffect.LOCK_POSITION, FlyingWeaponEffect.LOCK_ORIENTATION)),
                    new MotionFrame(new Vec3(1, 0, 0), new Vec3(0, 0, 1), -90),
                    new MotionFrame(new Vec3(0, 0, -1), new Vec3(0, 0, 1), -90),
                    new MotionFrame(new Vec3(-1, 0, 0), new Vec3(0, 0, 1), -90),
                    new MotionFrame(new Vec3(0, 0, 1), new Vec3(0, 0, 1), -90));
            List<MotionFrame> chop=List.of(
                    new MotionFrame(new Vec3(0, 0.6, -1), new Vec3(0, 0, 1),0, new FrameEffects().setEffects(FlyingWeaponEffect.WEAPON, FlyingWeaponEffect.LOCK_POSITION, FlyingWeaponEffect.LOCK_ORIENTATION)),
                    new MotionFrame(new Vec3(0, 1, 0), new Vec3(0, 0, 1)),
                    new MotionFrame(new Vec3(0, 0, 1), new Vec3(0, 0, 1)),
                    new MotionFrame(new Vec3(0, -0.4, 0), new Vec3(0, 0, 1)));
            List<MotionFrame> slash=List.of(
                    new MotionFrame(new Vec3(1, 0.6, 1), new Vec3(0, 0, 1), 45),
                    new MotionFrame(new Vec3(-1, -0.4, 0), new Vec3(0, 0, 1), 45));
            setInteractionRange(6);
            animProgress = 0;
            queuePath(new MotionManagers.DefinitionMM(new MotionGroup(chop, EasingFunctionEnum.IN_OUT_CUBIC, 60)), 0, 0);
            setIntangible(false);
            //setFlipRender(!flipClientRender());
            setFlipRender(!flipClientRender());
            while (!trailHistory.isEmpty()) trailHistory.pop();
            //setIdlePose(idlePose == firstIdle ? secondIdle : firstIdle);
            lock(getOwner());
            //setEffect(FlyingWeaponEffect.BIG_SHADOW, true);
        }
        //recalculatedOrientation = recalculateOrientation(null, update.renderOrientation(), (float) 0.1f);
    }

    @Override
    public void unlock() {
        super.unlock();
    }

    @Override
    public boolean shouldRender(double p_20296_, double p_20297_, double p_20298_) {
        return true;
    }
}
