package jackiecrazy.footwork.entity.flyingweapon;

import jackiecrazy.footwork.Footwork;
import jackiecrazy.footwork.api.ITetherAnchor;
import jackiecrazy.footwork.move.motionframe.*;
import jackiecrazy.footwork.move.motionframe.render.RenderItemGroup;
import jackiecrazy.footwork.move.motionframe.render.RenderNode;
import jackiecrazy.footwork.utils.EasingFunctionEnum;
import jackiecrazy.footwork.utils.GeneralUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4d;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

public abstract class FlyingItemEntity extends Entity implements OwnableEntity, ITetherAnchor, Targeting {
    public static final EntityDataSerializer<STATE> STATESERIALIZER = EntityDataSerializer.simpleEnum(STATE.class);
    public static final int MAX_TRAIL_LENGTH = 6;
    public static final int CLIENT_SMOOTHING_SUBTICKS = 1;
    public static final Color DEFAULT_TRAIL_COLOR = new Color(0.6f, 0.8f, 1.0f);
    protected static final List<MotionFrame> STAB = List.of(new MotionFrame(new Vec3(0, 0, 1), new Vec3(0, 0, -1)), new MotionFrame(new Vec3(0, 0, 1), new Vec3(0, 0, 1.4)), new MotionFrame(new Vec3(0, 0, 1), new Vec3(0, 0, 1.4)));
    protected static final List<MotionFrame> CIRCLE = List.of(new MotionFrame(new Vec3(0, 0, 1), new Vec3(0, 0, 1), new Vector4d(0, 0, 1, 90)), new MotionFrame(new Vec3(-1, 0, 0), new Vec3(0, 0, 1), new Vector4d(-1, 0, 0, 90)), new MotionFrame(new Vec3(0, 0, -1), new Vec3(0, 0, 1), new Vector4d(0, 0, -1, 90)), new MotionFrame(new Vec3(1, 0, 0), new Vec3(0, 0, 1), new Vector4d(1, 0, 0, 90)), new MotionFrame(new Vec3(0, 0, 1), new Vec3(0, 0, 1), new Vector4d(0, 0, 1, 90)));
    protected static final List<MotionFrame> SLASH = List.of(new MotionFrame(new Vec3(1, 0.6, 1), new Vec3(0, 0, 1)), new MotionFrame(new Vec3(-1, -0.4, 0), new Vec3(0, 0, 1), new Vector4d(-1, -1, 1, 45)));
    protected static final List<MotionFrame> BACKSLASH = List.of(new MotionFrame(new Vec3(-1, 0.6, 1), new Vec3(0, 0, 1), -45), new MotionFrame(new Vec3(1, -0.4, 0), new Vec3(0, 0, 1), new Vector4d(1, -1, 1, -45)));
    protected static final List<MotionFrame> CHOP = List.of(new MotionFrame(new Vec3(0, 1, -1), new Vec3(0, 0, 1)), new MotionFrame(new Vec3(0, 1, 0.2), new Vec3(0, 0, 1)), new MotionFrame(new Vec3(0, -0.5, 1), new Vec3(0, 0, 1)));
    protected static final List<MotionFrame> LOOP = List.of(
            //new MotionFrame(new Vec3(0, -1, 0), new Vec3(0, 0, 1)),
            new MotionFrame(new Vec3(1, 0, -1), new Vec3(0, 0, 1), new Vector4d(0, 0, -1, 180)), new MotionFrame(new Vec3(0, 1, 0.1), new Vec3(0, 0, 1), 0), new MotionFrame(new Vec3(0, 0, 1), new Vec3(0, 0, 1)), new MotionFrame(new Vec3(-1, -2, 1), new Vec3(0, 0, 1)));
    protected static final List<MotionManager> EVERYONE = List.of(new MotionManagers.DefinitionMM(new MotionGroup(CIRCLE, EasingFunctionEnum.IN_OUT_CUBIC, 30)), new MotionManagers.DefinitionMM(new MotionGroup(STAB, EasingFunctionEnum.IN_CUBIC, 30)), new MotionManagers.DefinitionMM(new MotionGroup(SLASH, EasingFunctionEnum.IN_OUT_CUBIC, 30)), new MotionManagers.DefinitionMM(new MotionGroup(BACKSLASH, EasingFunctionEnum.IN_OUT_CUBIC, 30)), new MotionManagers.DefinitionMM(new MotionGroup(CHOP, EasingFunctionEnum.IN_CUBIC, 30)));
    protected static final EntityDataAccessor<Optional<UUID>> DATA_OWNERUUID_ID = SynchedEntityData.defineId(FlyingItemEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    protected static final EntityDataAccessor<Quaternionf> ROLL = SynchedEntityData.defineId(FlyingItemEntity.class, EntityDataSerializers.QUATERNION);
    protected static final EntityDataAccessor<Float> ATTACK_RANGE = SynchedEntityData.defineId(FlyingItemEntity.class, EntityDataSerializers.FLOAT);
    protected static final EntityDataAccessor<Integer> MOB_OWNER = SynchedEntityData.defineId(FlyingItemEntity.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Integer> TARGET_ID = SynchedEntityData.defineId(FlyingItemEntity.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Integer> TETHER_ID = SynchedEntityData.defineId(FlyingItemEntity.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Integer> VISUAL_TAG = SynchedEntityData.defineId(FlyingItemEntity.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Integer> IDLE_TICK = SynchedEntityData.defineId(FlyingItemEntity.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Integer> LAST_UPD = SynchedEntityData.defineId(FlyingItemEntity.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Boolean> IS_INTANGIBLE = SynchedEntityData.defineId(FlyingItemEntity.class, EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Boolean> OFFHAND_RENDER = SynchedEntityData.defineId(FlyingItemEntity.class, EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<ItemStack> HELD = SynchedEntityData.defineId(FlyingItemEntity.class, EntityDataSerializers.ITEM_STACK);
    protected static final EntityDataAccessor<RenderItemGroup> COSMETIC = SynchedEntityData.defineId(FlyingItemEntity.class, RenderItemGroup.SERIALIZER);
    protected static final EntityDataAccessor<MotionFrame> LAST_FRAME = SynchedEntityData.defineId(FlyingItemEntity.class, MotionFrame.SERIALIZER);
    protected static final EntityDataAccessor<MotionFrame> CURRENT_FRAME = SynchedEntityData.defineId(FlyingItemEntity.class, MotionFrame.SERIALIZER);
    //uses
    protected static final EntityDataAccessor<Vector3f> UNIVERSAL_OFFSET = SynchedEntityData.defineId(FlyingItemEntity.class, EntityDataSerializers.VECTOR3);
    protected static final EntityDataAccessor<Vector3f> LOCK_POS = SynchedEntityData.defineId(FlyingItemEntity.class, EntityDataSerializers.VECTOR3);
    protected static final EntityDataAccessor<Vector3f> LOCK_LOOK = SynchedEntityData.defineId(FlyingItemEntity.class, EntityDataSerializers.VECTOR3);
    //protected static final EntityDataAccessor<Vector3f> SPIN = SynchedEntityData.defineId(FlyingItemEntity.class, EntityDataSerializers.VECTOR3);
    //how the weapon floats when idle: point downwards
    protected static final EntityDataAccessor<MotionManager> IDLE_POSE = SynchedEntityData.defineId(FlyingItemEntity.class, MotionManager.SERIALIZER);
    protected static final EntityDataAccessor<STATE> CURRENT_STATE = SynchedEntityData.defineId(FlyingItemEntity.class, STATESERIALIZER);
    protected static final EntityDataAccessor<Color> TRAIL_COLOR = SynchedEntityData.defineId(FlyingItemEntity.class, FrameEffects.COLOR);
    private static final Quaternionf nothing = new Quaternionf(0, 0, 0, 1);
    private static final Vector3f BOGUS = new Vector3f(0, 100000, 0);
    //first one goes up to attack range, second does not scale
    protected final Deque<Tuple<SwingHistory, SwingHistory>> trailHistory = new ArrayDeque<>();
    public Quaternionf rollO = new Quaternionf();
    public float displacementO, sizeO;
    public int renderLag = 0, renderLagO = 0;
    public int alpha = 0, alphaO = 0;
    protected int internalIdleTimer = 0;
    protected LivingEntity owner;
    protected Deque<MotionManager> moveQueue = new ConcurrentLinkedDeque<>();
    protected int animTicker = 0;
    protected double animProgress = 0;
    protected MotionFrame update;
    protected Vector4d recalculatedOrientation;
    protected boolean wasIdle = false;
    protected FrameEffects currentEffects = null;
    Vec3 prevShadPos = Vec3.ZERO;
    Vec3 prevTrailPos = Vec3.ZERO;
    private int version = 0;
    private Entity target, tether;
    private Vector3f currentSpin = new Vector3f();
    private boolean prevSpinning = false;

    public FlyingItemEntity(EntityType<? extends FlyingItemEntity> type, Level level) {
        super(type, level);
        setHeldItem(new ItemStack(Items.IRON_SWORD));
        noPhysics = true;
    }

    public MotionManager getIdlePose() {
        return entityData.get(IDLE_POSE);
    }

    public void setIdlePose(MotionManager idlePose) {
        if(idlePose.getEndFrame().renderOrientation().isFinite()) {
            entityData.set(IDLE_POSE, idlePose);
        }else Footwork.LOGGER.warn("received a MotionManager with invalid rotation, discarding.");
        if (isIdle()) updateFrameEffects(idlePose.getStartFrame().effects());
    }

    public Vec3 getUniversalOffset() {
        return new Vec3(entityData.get(UNIVERSAL_OFFSET));
    }

    public void setUniversalOffset(Vec3 universalOffset) {
        entityData.set(UNIVERSAL_OFFSET, universalOffset.toVector3f());
    }

    public float getInteractionRange() {
        return entityData.get(ATTACK_RANGE);
    }

    public void setInteractionRange(float range) {
        if (level().isClientSide()) return;
        entityData.set(ATTACK_RANGE, range);
    }

    public Deque<Tuple<SwingHistory, SwingHistory>> getTrailHistory() {
        return trailHistory;
    }

    public boolean isIdle() {
        return moveQueue.isEmpty();
    }

    public void queuePath(MotionManager path) {
        internalIdleTimer = 0;
        //if there was another return to idle animation, remove it first
        if (moveQueue.peekLast() instanceof MotionManagers.TransitionMM) moveQueue.removeLast();
        moveQueue.add(path);
        updateMotionTargets(false);
    }

    public void queuePath(MotionManager path, int inTick, int outTick) {
        internalIdleTimer = 0;
        //if there was another return to idle animation, remove it first
        if (moveQueue.peekLast() instanceof MotionManagers.TransitionMM) moveQueue.removeLast();
        //grab the (new) end of move queue, or idle if we are currently idle
        //CHANGED: grab the last motion we did
        //just... find the last frame it'll be in
        MotionFrame last = update;
        if (isIdle()) last = getIdlePose().getEndFrame();
        else if (!moveQueue.isEmpty()) last = moveQueue.getLast().getEndFrame();
        //animProgress = 0;//why do you need this?
        //add the transition in, actual move, and transition out
        if (inTick > 0)
            moveQueue.add(new MotionManagers.TransitionMM(last, path, inTick, EasingFunctionEnum.IN_OUT_CUBIC));
        moveQueue.add(path);
        if (outTick > 0)
            moveQueue.add(new MotionManagers.TransitionMM(path, getIdlePose(), outTick, EasingFunctionEnum.LINEAR));
        updateMotionTargets(false);
    }

    @Override
    public @Nullable UUID getOwnerUUID() {
        return entityData.get(DATA_OWNERUUID_ID).orElse(null);
    }

    protected void setOwnerUUID(UUID to) {
        if (level().isClientSide()) return;
        entityData.set(DATA_OWNERUUID_ID, Optional.of(to));
    }

    public int getOwnerEntityID() {
        return entityData.get(MOB_OWNER);
    }

    public LivingEntity getOwner() {
        if (owner == null && getOwnerUUID() != null) {

            owner = level().getPlayerByUUID(getOwnerUUID());
            if (owner == null) {//owned by a mob
                for (Entity e : level().getEntities(this, this.getBoundingBoxForCulling().inflate(5), a -> a.getUUID() == getOwnerUUID()))
                    if (e instanceof LivingEntity le) owner = le;
            }
        } else if (owner == null && getOwnerEntityID() != 0) {
            //owned by a mob
            if (level().getEntity(getOwnerEntityID()) instanceof LivingEntity le) owner = le;
        }
        return owner;
    }

    public void setOwner(LivingEntity e) {
        if (level().isClientSide()) return;
        if (e == null) return;
        if (e instanceof Player) setOwnerUUID(e.getUUID());
        else entityData.set(MOB_OWNER, e.getId());
        owner = e;
        if (e.getAttribute(ForgeMod.ENTITY_REACH.get()) != null)
            setInteractionRange((float) e.getAttributeValue(ForgeMod.ENTITY_REACH.get()));
    }

    public Entity getMotionTarget() {
        final int id = entityData.get(TARGET_ID);
        if (target == null || target.getId() != id) target = level().getEntity(id);
        if (target == null) return getOwner();
        return target;
    }

    public void setMotionTarget(Entity track) {
        if (level().isClientSide()) return;
        target = track;
        int id = target == null ? -1 : target.getId();
        entityData.set(TARGET_ID, id);
    }

    public void clearPath() {
        while (!isIdle()) updateMotionTargets(true);
    }

    protected boolean updateMotionTargets(boolean forceskip) {
        LivingEntity owner = getOwner();
        if (owner == null) return false;
        MotionManager motion = moveQueue.peek();
        if (motion == null || motion.hasEnded(animTicker, animProgress) || forceskip) {
            if (motion != null) updateFrameEffects(motion.getEndFrame().effects());
            moveQueue.poll();
            motion = moveQueue.peek();
            if (motion != null) this.updateFrameEffects(motion.getStartFrame().effects());
            //setTransitioning(motion == null || motion instanceof MotionManagers.TransitionMM);
            animProgress = animTicker = 0;
            flushTrailHistory();
            currentEffects = null;
            return true;
        }
        return false;
    }

    public ItemStack getHeldItem() {
        return entityData.get(HELD);
    }

    public void setHeldItem(ItemStack stack) {
        //if (level().isClientSide()) return;
        entityData.set(HELD, stack);
        //throw new IllegalArgumentException();
        //System.out.println("set stack to "+stack.getItem());
        setCosmeticItem(stack);
    }

    public Color getTrailColor() {
        return entityData.get(TRAIL_COLOR);
    }

    public void setTrailColor(Color c) {
        entityData.set(TRAIL_COLOR, c);
    }

    public RenderItemGroup getCosmeticItem() {
        return entityData.get(COSMETIC);
    }

    public void setCosmeticItem(ItemStack stack) {
        setCosmeticItem(new RenderItemGroup(new RenderNode.ItemNode(stack, Vec3.ZERO, Vec3.ZERO)));
    }

    public void setCosmeticItem(RenderItemGroup stack) {
        entityData.set(COSMETIC, stack);
    }

    public Quaternionf getRoll() {
        return entityData.get(ROLL);
    }

    public float getDisplacementForRender() {
        return 0;//entityData.get(ID_DISPLACE);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(ROLL, new Quaternionf(0, 0, 1, 0));
        this.entityData.define(IS_INTANGIBLE, true);
        this.entityData.define(OFFHAND_RENDER, false);
        this.entityData.define(LAST_FRAME, new MotionFrame(Vec3.ZERO, Vec3.ZERO));
        this.entityData.define(CURRENT_FRAME, new MotionFrame(Vec3.ZERO, Vec3.ZERO));
        this.entityData.define(HELD, ItemStack.EMPTY);
        this.entityData.define(COSMETIC, new RenderItemGroup(new RenderNode.ItemNode(ItemStack.EMPTY, Vec3.ZERO, Vec3.ZERO)));
        this.entityData.define(DATA_OWNERUUID_ID, Optional.empty());
        this.entityData.define(MOB_OWNER, 0);
        this.entityData.define(TARGET_ID, 0);
        this.entityData.define(TETHER_ID, 0);
        this.entityData.define(UNIVERSAL_OFFSET, new Vector3f());
        this.entityData.define(LOCK_POS, BOGUS);
        this.entityData.define(LOCK_LOOK, BOGUS);
        //this.entityData.define(SPIN, new Vector3f());
        this.entityData.define(IDLE_POSE, new MotionManagers.FixedMM(new MotionFrame(new Vec3(0, -1, 0), Vec3.ZERO, new Vector4d(0, 0, 1, 0)), 5));
        this.entityData.define(ATTACK_RANGE, 3f);
        this.entityData.define(VISUAL_TAG, 11);//binary value 1011
        this.entityData.define(IDLE_TICK, 10);
        this.entityData.define(LAST_UPD, 0);
        this.entityData.define(CURRENT_STATE, STATE.FOLLOW);
        this.entityData.define(TRAIL_COLOR, DEFAULT_TRAIL_COLOR);
    }

    public int getRawVisualTag() {
        return entityData.get(VISUAL_TAG);
    }

    public boolean hasEffect(FlyingWeaponEffect f) {
        switch (f) {
            case LOCK_POSITION -> {
                return !getEntityData().get(LOCK_POS).equals(BOGUS);
            }
            case LOCK_ORIENTATION -> {
                return !getEntityData().get(LOCK_LOOK).equals(BOGUS);
            }
            case UNLOCK_POSITION -> {
                return getEntityData().get(LOCK_POS).equals(BOGUS);
            }
            case UNLOCK_ORIENTATION -> {
                return getEntityData().get(LOCK_LOOK).equals(BOGUS);
            }
            default -> {
                return ((getRawVisualTag() >> f.ordinal()) & 1) > 0;
            }
        }
    }

    public void setEffect(FlyingWeaponEffect f, boolean enable) {
        if (level().isClientSide()) return;
        if (f == FlyingWeaponEffect.LOCK_POSITION) {
            Vector3f locked = getEntityData().get(LOCK_POS);
            if (locked.equals(BOGUS) && enable) lockPos(stateDependentPositionLook().getA());
            else if (!locked.equals(BOGUS) && !enable) unlockPos();
            return;
        }
        if (f == FlyingWeaponEffect.LOCK_ORIENTATION) {
            Vector3f locked = getEntityData().get(LOCK_LOOK);
            if (locked.equals(BOGUS) && enable) lockLook(stateDependentPositionLook().getB());
            else if (!locked.equals(BOGUS) && !enable) unlockLook();
            return;
        }
        int mask = 1 << f.ordinal();
        if (enable) {
            entityData.set(VISUAL_TAG, getRawVisualTag() | mask); // Set the bit
        } else {
            entityData.set(VISUAL_TAG, getRawVisualTag() & ~mask); // Clear the bit
        }
    }

    public void flushTrailHistory() {
        entityData.set(LAST_UPD, getEntityData().get(LAST_UPD) + 1);
    }

    public void setEffect(FlyingWeaponEffect... ff) {
        if (level().isClientSide() || ff == null) return;
        int finalMask = 0;
        for (FlyingWeaponEffect f : ff) {
            if (f == null) continue;
            switch (f) {
                case UNLOCK_POSITION -> setEffect(FlyingWeaponEffect.LOCK_POSITION, false);
                case UNLOCK_ORIENTATION -> setEffect(FlyingWeaponEffect.LOCK_ORIENTATION, false);
                case LOCK_POSITION -> setEffect(FlyingWeaponEffect.LOCK_POSITION, true);
                case LOCK_ORIENTATION -> setEffect(FlyingWeaponEffect.LOCK_ORIENTATION, true);
            }
            int mask = 1 << f.ordinal();
            finalMask |= mask;
        }
        entityData.set(VISUAL_TAG, finalMask); // Clear the bit
    }

    // Custom motion logic and collision in tick()
    @Override
    public void tick() {
        super.tick();
        entityData.set(LAST_FRAME, new MotionFrame(position(), getLookAngle(), getRoll()));// Movement logic
        if (level().isClientSide) {
            rollO = getRoll();
            displacementO = getDisplacementForRender();
            this.move(MoverType.SELF, this.getDeltaMovement());
            //updateTetheringVelocity();//is this necessary?
            updateClientData();
            //how can the client get ahold of moveset data for smoothing?
            // answer: don't. It's painful. Just sync whether it's idle
            return;
        }
        if (getMotionTarget() == null || !getMotionTarget().isAlive()) setMotionTarget(getOwner());
        if (getOwner() != null && getMotionTarget() != null) {

            if (!moveQueue.isEmpty()) {
                wasIdle = false;
                entityData.set(IDLE_TICK, 0);
                animTicker++;
                animProgress += getWeight();
                executeMoveQueue();
            } else if (getState() == STATE.THROW_TRACK) {
                wasIdle = false;
                trackingFly();
            } else if (getState() == STATE.THROW_NATURAL) {
                wasIdle = false;
                naturallyFly();
            } else if (isIdle()) {
                //idle state
                if (!wasIdle) updateFrameEffects(getIdlePose().getStartFrame().effects());
                wasIdle = true;
                entityData.set(IDLE_TICK, getIdlePose().getDuration());
                returnToIdle(getIdlePose().getDuration());
            }

            //move set here to ensure orientation is not messed up first
            this.move(MoverType.SELF, this.getDeltaMovement());
            updateTetheringVelocity();

            // Collision and attack logic
            if (!level().isClientSide()) {
                handleEntityCollisions();
                handleBlockCollisions();
            }

            // update client for trail rendering, done after block collision checks
            if (update != null) {
                //entityData.set(LAST_FRAME, entityData.get(CURRENT_FRAME));
                MotionFrame reconstructed = new MotionFrame(update.direction(), update.offset(), getRoll());
                entityData.set(CURRENT_FRAME, reconstructed);
            }
        }
    }

    protected double getWeight() {
        return 1;
    }

    private void trackingFly() {
        Vec3 velocity = getDeltaMovement();
        double speed = velocity.length();

        if (speed < 0.01) {
            // emergency recovery
            Vec3 dir = getMotionTarget().getEyePosition().subtract(position()).normalize();
            setDeltaMovement(dir.scale(getMinimumSpeed()));
            return;
        }
        updateSpin(getIdlePose());

        Vec3 toTargetDir = getMotionTarget().getEyePosition().subtract(position()).normalize();

        // Project toTarget onto the plane perpendicular to current velocity
        Vec3 forward = velocity.normalize();
        double dot = forward.dot(toTargetDir);
        Vec3 lateralCorrection = toTargetDir.subtract(forward.scale(dot));

        double turnAggression = 0.22;        // ← main tuning value (0.15–0.35 typical)
        // 0.18 → gentle arc, 0.28 → quite aggressive, 0.35+ → very sharp

        Vec3 newVelocity = velocity.add(lateralCorrection.scale(turnAggression * speed));

        // Optional: very gentle speed recovery when turning hard
        double targetSpeed = getMinimumSpeed();                    // or Math.max(speed, getBaseSpeed())
        double speedRecovery = 0.04;                            // 0 = perfect conservation
        newVelocity = newVelocity.normalize().scale(speed + (targetSpeed - speed) * speedRecovery);

        setDeltaMovement(newVelocity);

        if (speed > 0.003) {
            recalculatedOrientation = recalculateOrientation(nothing, 1);
        }
    }

    private double getMinimumSpeed() {
        return 0.5;
    }

    private void naturallyFly() {
        MotionFrame nextPoint = getIdlePose().getNextPoint(tickCount);
        updateSpin(getIdlePose());
        if (getDeltaMovement().lengthSqr() > 0.003) {
            //recalculatedOrientation = recalculateOrientation(nothing, 1);
            recalculatedOrientation = recalculateOrientation(nextPoint.renderOrientation().normalize(), 1);
        }
    }

    protected void handleEntityCollisions() {
        if (!intangible()) {
            final float range = getInteractionRange();
            List<Entity> selfTarget = level().getEntities(getOwner(), getBoundingBox().inflate(0.2f), e -> e != getOwner() && e.isAlive() && e.isAttackable());
            List<Entity> viewTarget = level().getEntities(getOwner(), getBoundingBox().move(getLookAngle().scale(range)).inflate(0.2f), e -> e != getOwner() && e.isAlive() && e.isAttackable());
            List<Entity> targets = GeneralUtils.arcTraceEntities(level(), getOwner(), stateDependentPositionLook().getA(), getPosition(0).add(getViewVector(0).scale(range)), getPosition(1).add(getViewVector(1).scale(range)), range, 0.5, Entity::isAttackable);//level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(0.3));

            selfTarget.addAll(viewTarget);
            selfTarget.addAll(targets);
            onHitEntity(selfTarget);
        }
    }

    public STATE getState() {
        return getEntityData().get(CURRENT_STATE);
    }

    public void setState(STATE s) {
        getEntityData().set(CURRENT_STATE, s);
    }

    public void lockPos(Vec3 pos) {
        if (level().isClientSide()) return;
        getEntityData().set(LOCK_POS, pos.toVector3f());
    }

    public void lockLook(Vec3 look) {
        if (level().isClientSide()) return;
        getEntityData().set(LOCK_LOOK, look.toVector3f());
    }

    public void unlockPos() {
        if (level().isClientSide()) return;
        getEntityData().set(LOCK_POS, BOGUS);
    }

    public void unlockLook() {
        if (level().isClientSide()) return;
        getEntityData().set(LOCK_LOOK, BOGUS);
    }

    public void lock(Entity to) {
        if (level().isClientSide()) return;
        //getEntityData().set(CURRENT_STATE, STATE.FIXED_POINT);
        getEntityData().set(LOCK_POS, to.position().add(0, to.getBbHeight() / 2, 0).toVector3f());
        getEntityData().set(LOCK_LOOK, to.getLookAngle().toVector3f());
    }

    public void unlock() {
        if (level().isClientSide()) return;
        getEntityData().set(CURRENT_STATE, STATE.FOLLOW);
        unlockLook();
        unlockPos();
    }

    protected abstract boolean onHitEntity(List<Entity> targets);

    protected void handleBlockCollisions() {
        if (!intangible()) {
            final float range = getInteractionRange();
            HitResult hit = level().clip(new ClipContext(getPosition(0).add(getViewVector(0).scale(range)), getPosition(1).add(getViewVector(1).scale(range)), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
            if (hit.getType() == HitResult.Type.BLOCK) {
                BlockHitResult blockHit = (BlockHitResult) hit;
                BlockPos blockPos = blockHit.getBlockPos();
                Direction hitFace = blockHit.getDirection();
                onHitBlock(blockPos, hitFace, blockHit.getLocation());
            }
            tryCheckInsideBlocks();
        }
    }

    protected void returnToIdle(int duration) {
        unlock();
        if (getMotionTarget() == null) return;
        //idle animation, float next to the player
        update = getIdlePose().getEndFrame();
        updateSpin(getIdlePose());
        final Tuple<Vec3, Vec3> bundle = stateDependentPositionLook();
        Vec3 transformedDirection = update.resolveTargetOffset(bundle, getUniversalOffset(), 1);

        // Move toward the desired position smoothly
        Vec3 currentPos = position();
        //final double snappiness = Mth.clamp(5/transformedDirection.distanceToSqr(currentPos), 0.25, 1);
        final float snappiness = 1f / duration;
        Vec3 lerp = currentPos.lerp(transformedDirection, snappiness);
        Vec3 delta = lerp.subtract(currentPos);
        setPos(lerp);
        //setDeltaMovement(delta);
        //reconstruct the right new position
        recalculatedOrientation = recalculateOrientation(update.renderOrientation(), (float) snappiness);
        update = new MotionFrame(lerp.subtract(bundle.getA()), new Vec3(0, 0, 0), recalculatedOrientation);

        displacementO = getDisplacementForRender();
    }

    protected void updateSpin(MotionManager cur) {
        if (cur.getSpin().lengthSquared() == 0) {
            if (prevSpinning) {
                //instantly modulo spin
                currentSpin = new Vector3f(GeneralUtils.clampAndInvert(currentSpin.x), GeneralUtils.clampAndInvert(currentSpin.y), GeneralUtils.clampAndInvert(currentSpin.z));
                prevSpinning = false;
            }
            //gradually reduce spin
            currentSpin = currentSpin.mul(0.5f, 0.5f, 0.5f);
            if (currentSpin.lengthSquared() < 0.05) currentSpin = new Vector3f();
        } else {
            currentSpin = currentSpin.add(cur.getSpin());
            prevSpinning = true;
        }
    }

    protected void executeMoveQueue() {
        MotionManager activeMove = moveQueue.peek();
        if (activeMove == null) return;
        //motion path test
        // Recalculate target every tick relative to player
        //keep the lerped frame for future movement
        update = activeMove.getNextPoint(animProgress);
        updateSpin(activeMove);
        if (update.effects() != currentEffects) {
            updateFrameEffects(update.effects());
        }
        final Tuple<Vec3, Vec3> bundle = stateDependentPositionLook();
        Vec3 transformedDirection = update.resolveTargetOffset(bundle, getUniversalOffset(), 1);

        // Move toward the desired position smoothly
        Vec3 delta = transformedDirection.subtract(this.position());

        this.setPos(transformedDirection);
        //make the first anim progress also reset the last frame
        //setDeltaMovement(delta);
        recalculatedOrientation = recalculateOrientation(update.renderOrientation(), 1f);

        //prevent recursion
        if (animTicker == 0) return;

        if (activeMove.hasEnded(animTicker, animProgress)) {//end current path, execute next path (either transition or next path)
            updateMotionTargets(false);
        } else {
            if (activeMove != getIdlePose()) {
                //still animating, keep frames on
                displacementO = getDisplacementForRender();
            }
        }
        //entityData.set(ID_DISPLACE, (float) Math.max(0, lerped.offset().length() * attackRange-1));
    }

    protected void updateFrameEffects(FrameEffects effects) {

    }

    protected Tuple<Vec3, Vec3> stateDependentPositionLook() {
        switch (getState()) {
            case THROW_TRACK -> {
                return new Tuple<>(getMotionTarget().position(), getDeltaMovement());
            }
            case THROW_NATURAL -> {
                return new Tuple<>(position(), getDeltaMovement());
            }
            default -> {
                Vector3f pos = getEntityData().get(LOCK_POS);
                Vector3f look = getEntityData().get(LOCK_LOOK);
                Vec3 poss = new Vec3(pos);
                Vec3 lookk = new Vec3(look);
                if (pos.equals(BOGUS))
                    poss = getMotionTarget().position().add(0, getMotionTarget().getBbHeight() / 2, 0);
                if (look.equals(BOGUS)) lookk = getMotionTarget().getLookAngle();
                return new Tuple<>(poss, lookk);//fixme
            }
        }
    }

    protected void updateClientData() {
        renderLagO = renderLag;
        sizeO = getInteractionRange();
        alphaO = alpha;
        if (hasEffect(FlyingWeaponEffect.BIG_SHADOW)) {
            if (alpha < 130) alpha += 35;
        } else if (alpha > 0) alpha -= 35;
        if (entityData.get(IDLE_TICK) > 0) {
            //special case, idle needs smooth animations
            returnToIdle(entityData.get(IDLE_TICK));
        }
        //lerp 5 points between each tick
        if (getMotionTarget() != null) {
            Vec3 universalOffset = getUniversalOffset();
            MotionFrame from = entityData.get(LAST_FRAME);//position, look, (xrot, yrot, roll)
            MotionFrame to = entityData.get(CURRENT_FRAME);//direction, offset, only recalculated orientation (xrot, yrot, zrot... in theory)
            double dist = intangible() ? -1 : getInteractionRange() - 1;//the -1 here is necessary to counteract the base
            Vec3 fromTrailPos = from.direction().add(from.offset().scale(dist));
            //this is NOT accurate!
            Vec3 fromShadowPos = from.direction();
            Vec3 toTrailPos = to.resolveTargetOffset(stateDependentPositionLook(), universalOffset, getInteractionRange());
            Vec3 toShadowPos = to.resolveTargetOffset(stateDependentPositionLook(), universalOffset, 1);
            for (double i = 0; i < CLIENT_SMOOTHING_SUBTICKS; i++) {
                double partialTick = i / CLIENT_SMOOTHING_SUBTICKS;

                //trail, max range
                Vec3 trailPosition = fromTrailPos.lerp(toTrailPos, partialTick);
                SwingHistory trail = new SwingHistory(trailPosition, !intangible(), getTrailColor(), from.renderOrientation().slerp(to.renderOrientation(), (float) partialTick, new Quaternionf()));//yes, this is correct, stop asking

                //shadow, range 1
                trailPosition = fromShadowPos.lerp(toShadowPos, partialTick);
                SwingHistory shadow = new SwingHistory(trailPosition, !intangible(), getTrailColor(), from.renderOrientation().slerp(to.renderOrientation(), (float) partialTick, new Quaternionf()));//yes, this is correct, stop asking

                trailHistory.addFirst(new Tuple<>(trail, shadow));
            }
            while (trailHistory.size() > MAX_TRAIL_LENGTH * CLIENT_SMOOTHING_SUBTICKS) {
                trailHistory.removeLast();
            }
        }
        //flush
        if (getEntityData().get(LAST_UPD) != version) {
            trailHistory.clear();
            version = getEntityData().get(LAST_UPD);
        }
    }


    @Override
    protected void onInsideBlock(BlockState bs) {
        super.onInsideBlock(bs);
        if (bs.blocksMotion()) onHitBlock(getOnPos(), Direction.DOWN, position());
    }

    protected abstract void onHitBlock(BlockPos blockPos, Direction hitFace, Vec3 location);

    public Vec3 stateDependentOrientation() {
        // Get player's rotation as a basis
        Vec3 forward = null;
        if (getState() == STATE.FOLLOW) {
            Vector3f look = getEntityData().get(LOCK_LOOK);
            if (look.equals(BOGUS)) forward = getMotionTarget().getLookAngle().normalize();
            else forward = new Vec3(look);
            if (forward.lengthSqr() < 0.0001) forward = new Vec3(0, 0, 1); // fallback
        }
//        else if (getState() == STATE.FIXED_POINT) {
//            forward = new Vec3(getEntityData().get(LOCK_LOOK));
//        }
        if (forward != null) {
            return forward;
        }
        return getDeltaMovement();
    }

    public Quaternionf applySpin(Quaternionf base, Vector3f prevRot) {
        Quaternionf out = new Quaternionf();
        Quaternionf spin = getRuntimeRotation(prevRot);

        return out.set(base).mul(spin).normalize(); // local space spin
    }

    protected Quaternionf getRuntimeRotation(Vector3f cur) {
        Quaternionf out = new Quaternionf();

        float speed = cur.length();
        if (speed < 1e-6f) {
            return out.identity();
        }

        Vector3f axis = new Vector3f(cur).normalize();

        // Total angle = ω × time
        float angle = speed;// * (ticks + partialTick);

        return out.fromAxisAngleDeg(axis, angle);
    }


    public Vector4d recalculateOrientation(Quaternionf localOrientation, float snappiness) {
        // Get world-space base rotation
        Vec3 worldBase = stateDependentOrientation(); // Identity for base
        if (worldBase.lengthSqr() < 0.003) worldBase = new Vec3(0, -1, 0);//fallback

        // rotation into world frame
        Quaternionf worldQuat = MotionFrame.lookQuatFromVec(worldBase).normalize();

        localOrientation = applySpin(localOrientation, currentSpin);

        // Build target quat from effective world forward (look-at)
        Quaternionf targetQuat = worldQuat.mul(localOrientation);
        //Footwork.LOGGER.debug("finally it's "+targetQuat);

        Quaternionf current = getRoll();
        // Ensure shortest path for smooth slerp
        if (current.dot(targetQuat) < 0) {
            targetQuat.mul(-1);
        }
        Quaternionf lerpRot = new Quaternionf();
        current.slerp(targetQuat, snappiness, lerpRot);

        // Update entity rotation (for Minecraft's Euler if needed, but prefer quat for rendering)
        Vector3f fwd = new Vector3f(0, 0, 1); // weapon points along +Z locally
        lerpRot.transform(fwd);
        //fwd.mul((float) (180d / Math.PI));

//        Footwork.LOGGER.debug("worldbase was "+worldBase);
//        Footwork.LOGGER.debug("lerped rot is "+lerpRot);
//        Footwork.LOGGER.debug("orienting weapon to "+fwd);

        float targetPitch = (float) Mth.wrapDegrees(-Mth.atan2(fwd.y, Math.sqrt(fwd.x * fwd.x + fwd.z * fwd.z)) * (180F / Math.PI));
        float targetYaw = (float) Mth.wrapDegrees(-Mth.atan2(fwd.x, fwd.z) * (180F / Math.PI));
        float lerpX = Mth.lerp(snappiness, getXRot(), targetPitch);
        float lerpY = Mth.lerp(snappiness, getYRot(), targetYaw);

        setYRot(lerpY);
        setXRot(lerpX);

        entityData.set(ROLL, lerpRot);
        return new Vector4d(localOrientation.x, localOrientation.y, localOrientation.z, localOrientation.w); // If you need degrees
    }

    // Save/load NBT
    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if (tag.contains("Item")) {
            setHeldItem(ItemStack.of(tag.getCompound("Item")));
        }
        if (tag.contains("ownerUUID")) {
            setOwnerUUID(tag.getUUID("ownerUUID"));
            owner = getOwner();
        }
        if (tag.contains("cosmetic")) setCosmeticItem(RenderItemGroup.fromTag(tag.getCompound("cosmetic")));
        setState(STATE.values()[tag.getInt("state")]);
        entityData.set(VISUAL_TAG, tag.getInt("visuals"));
        setInteractionRange(tag.getFloat("range"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        if (!getHeldItem().isEmpty()) tag.put("Item", getHeldItem().save(new CompoundTag()));
        if (getOwnerUUID() != null) tag.putUUID("ownerUUID", getOwnerUUID());
        tag.putInt("state", getState().ordinal());
        tag.putInt("visuals", getRawVisualTag());
        tag.putFloat("range", getInteractionRange());
        if (getCosmeticItem() != null) tag.put("cosmetic", getCosmeticItem().toTag());
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    @Override
    public boolean skipAttackInteraction(Entity p_20357_) {
        return true;
    }

    @Override
    public AABB getBoundingBoxForCulling() {
        return super.getBoundingBoxForCulling().inflate(8.0D);
    }

    public boolean intangible() {
        return entityData.get(IS_INTANGIBLE);
    }

    public void setIntangible(boolean incorporeal) {
        if (level().isClientSide()) return;
        entityData.set(IS_INTANGIBLE, incorporeal);
    }

    public boolean flipClientRender() {
        return entityData.get(OFFHAND_RENDER);
    }

    public void setFlipRender(boolean leftHand) {
        entityData.set(OFFHAND_RENDER, leftHand);
    }

    @Override
    public @Nullable LivingEntity getTarget() {
        return getMotionTarget() instanceof LivingEntity le ? le : null;
    }

    @Override
    public Entity getTetheringEntity() {
        final int id = entityData.get(TETHER_ID);
        if (tether == null || tether.getId() != id) tether = level().getEntity(id);
        return tether;
    }

    @Override
    public void setTetheringEntity(Entity to) {
        tether = to;
        int id = tether == null ? -1 : tether.getId();
        entityData.set(TETHER_ID, id);
    }

    @Override
    public @NotNull Vec3 getTetheredOffset() {
        return Vec3.ZERO;
    }

    @Override
    public @Nullable Entity getTetheredEntity() {
        return this;
    }

    @Override
    public void setTetheredEntity(Entity to) {

    }

    @Override
    public double getTetherLength() {
        return 0;
    }

    @Override
    public boolean shouldRepel() {
        return true;
    }

    public enum STATE {
        FOLLOW, //FIXED_POINT,
        THROW_NATURAL, THROW_TRACK
    }
}
