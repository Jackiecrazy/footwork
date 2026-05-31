package jackiecrazy.footwork.entity.flyingweapon;

import jackiecrazy.footwork.move.motionframe.*;
import jackiecrazy.footwork.move.motionframe.render.RenderItemGroup;
import jackiecrazy.footwork.move.motionframe.render.RenderNode;
import jackiecrazy.footwork.move.utils.ArgumentContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector4d;

import java.util.ArrayList;
import java.util.List;

public class DummyFlyingWeaponEntity extends FlyingItemEntity {
    private final ArrayList<Entity> alreadyHit = new ArrayList<>();
    private MotionManager testing = null;

    public DummyFlyingWeaponEntity(EntityType<? extends FlyingItemEntity> type,
                                   Level level) {
        super(type, level);
        final ItemStack stack = new ItemStack(Items.IRON_AXE);
        stack.enchant(Enchantments.ALL_DAMAGE_PROTECTION, 1);
        //setHeldItem(ItemStack.EMPTY);
        setHeldItem(stack);
        //setFlipRender(true);
        //setUniversalOffset(new Vec3(0,0,4));
        final MotionManagers.FixedMM pose = new MotionManagers.FixedMM(new MotionFrame(new Vec3(0, 0, 1), new Vec3(0, 0, 0), new Vector4d(0, 0, 1, 0)), 100);
        //pose.setAngularVelocity(new Vector3f(0.1f,0,0));
        setIdlePose(pose);
        this.setUniversalOffset(new Vec3(-1.3, 0, 0));
        setCosmeticItem(new RenderItemGroup(
                new RenderNode.ItemNode(stack, Vec3.ZERO, Vec3.ZERO),
                new RenderNode.ItemNode(ItemStack.EMPTY, new Vec3(0, 45, 0), new Vec3(0, 1, 0)),
                new RenderNode.ItemNode(new ItemStack(Items.IRON_AXE), new Vec3(0, 90, 0), new Vec3(0, 2, 0))
        ));
    }

    @Override
    protected boolean onHitEntity(List<Entity> targets) {
        return false;
    }

    @Override
    protected void onHitBlock(BlockPos blockPos, Direction hitFace, Vec3 location) {

    }

    @Override
    public void tick() {
        if (getOwner() == null) {
            setOwner(level().getNearestPlayer(this, 16));
        }
        if (getMotionTarget() == getOwner()) {
            setUniversalOffset(new Vec3(1.3, 0, 0));
        }
        super.tick();
    }

    @Override
    protected void updateFrameEffects(FrameEffects effects) {
        currentEffects = effects;
        if (effects != null) {
            setIntangible(false);
            if (effects.getRange() >= 0) setInteractionRange((float) effects.getRange());
            //special handling for vector adjustments on initial orientation lock
            if (effects.getEffects().contains(FlyingWeaponEffect.LOCK_ORIENTATION)) {
                lockLook(stateDependentPositionLook().getB().multiply(effects.modify_initial_rotation().x, effects.modify_initial_rotation().y, effects.modify_initial_rotation().z));
            }
            if (effects.getEffects() != null)
                setEffect(effects.getEffects().toArray(new FlyingWeaponEffect[0]));
            if (effects.reset_hit())
                alreadyHit.clear();
            LivingEntity e = getOwner();
            if (e != null)
                effects.runEffects(e, this, InteractionHand.MAIN_HAND, getHeldItem());
            if (effects.getDisplayItems() != null)
                setCosmeticItem(effects.getDisplayItems().resolve(new ArgumentContext(getOwner(), getOwner())));
            if (effects.getColor() != null)
                setTrailColor(effects.getColor());
        }
    }

    @Override
    protected double getWeight() {
        return 0.5;
    }

    public void setTesting(MotionManager testing) {
        this.testing = testing;
    }

    @Override
    protected void returnToIdle(int duration) {
        super.returnToIdle(duration);
        //lock(getOwner());
        setIntangible(true);
        alreadyHit.clear();
        //provisional. Used to test movement.
        setState(STATE.FOLLOW);
        animTicker++;
        if (animTicker > 20 && testing != null) {
            animTicker = 0;
            queuePath(testing, 0, 0);
            setIntangible(false);
            //setFlipRender(!flipClientRender());
            setFlipRender(!flipClientRender());
            //setPos(getX(), getY()+10, getZ());
            while (!trailHistory.isEmpty()) trailHistory.pop();
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
