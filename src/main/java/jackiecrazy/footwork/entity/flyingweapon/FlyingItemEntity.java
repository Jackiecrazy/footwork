package jackiecrazy.footwork.entity.flyingweapon;

import jackiecrazy.footwork.api.ITetherAnchor;
import jackiecrazy.footwork.move.motionframe.MotionGroup;
import jackiecrazy.footwork.move.motionframe.MotionFrame;
import jackiecrazy.footwork.move.motionframe.MotionManager;
import jackiecrazy.footwork.move.motionframe.MotionManagers;
import jackiecrazy.footwork.utils.EasingFunction;
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
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.joml.Vector4d;

import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

public abstract class FlyingItemEntity extends Entity implements OwnableEntity, ITetherAnchor, Targeting {
    public static final EntityDataSerializer<STATE> STATESERIALIZER = EntityDataSerializer.simpleEnum(STATE.class);
    public static final int MAX_TRAIL_LENGTH = 6;
    public static final int CLIENT_SMOOTHING_SUBTICKS = 7;
    protected static final List<MotionFrame> STAB = List.of(new MotionFrame(new Vec3(0, 0, 1), new Vec3(0, 0, -1)), new MotionFrame(new Vec3(0, 0, 1), new Vec3(0, 0, 1.4)), new MotionFrame(new Vec3(0, 0, 1), new Vec3(0, 0, 1.4)));
    protected static final List<MotionFrame> CIRCLE = List.of(new MotionFrame(new Vec3(0, 0, 1), new Vec3(0, 0, 1), new Vector4d(0, 0, 1, 90)), new MotionFrame(new Vec3(-1, 0, 0), new Vec3(0, 0, 1), new Vector4d(-1, 0, 0, 90)), new MotionFrame(new Vec3(0, 0, -1), new Vec3(0, 0, 1), new Vector4d(0, 0, -1, 90)), new MotionFrame(new Vec3(1, 0, 0), new Vec3(0, 0, 1), new Vector4d(1, 0, 0, 90)), new MotionFrame(new Vec3(0, 0, 1), new Vec3(0, 0, 1), new Vector4d(0, 0, 1, 90)));
    protected static final List<MotionFrame> SLASH = List.of(new MotionFrame(new Vec3(1, 0.6, 1), new Vec3(0, 0, 1)), new MotionFrame(new Vec3(-1, -0.4, 0), new Vec3(0, 0, 1), new Vector4d(-1, -1, 1, 45)));
    protected static final List<MotionFrame> BACKSLASH = List.of(new MotionFrame(new Vec3(-1, 0.6, 1), new Vec3(0, 0, 1), -45), new MotionFrame(new Vec3(1, -0.4, 0), new Vec3(0, 0, 1), new Vector4d(1, -1, 1, -45)));
    protected static final List<MotionFrame> CHOP = List.of(new MotionFrame(new Vec3(0, 1, -1), new Vec3(0, 0, 1)), new MotionFrame(new Vec3(0, 1, 0.2), new Vec3(0, 0, 1)), new MotionFrame(new Vec3(0, -0.5, 1), new Vec3(0, 0, 1)));
    protected static final List<MotionFrame> LOOP = List.of(
            //new MotionFrame(new Vec3(0, -1, 0), new Vec3(0, 0, 1)),
            new MotionFrame(new Vec3(1, 0, -1), new Vec3(0, 0, 1), new Vector4d(0, 0, -1, 180)),
            new MotionFrame(new Vec3(0, 1, 0.1), new Vec3(0, 0, 1), 0),
            new MotionFrame(new Vec3(0, 0, 1), new Vec3(0, 0, 1)),
            new MotionFrame(new Vec3(-1, -2, 1), new Vec3(0, 0, 1)));
    protected static final List<MotionManager> EVERYONE = List.of(new MotionManagers.DefinitionMM(new MotionGroup(CIRCLE, EasingFunction.IN_OUT_CUBIC, 30)), new MotionManagers.DefinitionMM(new MotionGroup(STAB, EasingFunction.IN_CUBIC, 30)), new MotionManagers.DefinitionMM(new MotionGroup(SLASH, EasingFunction.IN_OUT_CUBIC, 30)), new MotionManagers.DefinitionMM(new MotionGroup(BACKSLASH, EasingFunction.IN_OUT_CUBIC, 30)), new MotionManagers.DefinitionMM(new MotionGroup(CHOP, EasingFunction.IN_CUBIC, 30)));
    protected static final EntityDataAccessor<Optional<UUID>> DATA_OWNERUUID_ID = SynchedEntityData.defineId(FlyingItemEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    protected static final EntityDataAccessor<Float> ROLL = SynchedEntityData.defineId(FlyingItemEntity.class, EntityDataSerializers.FLOAT);
    protected static final EntityDataAccessor<Float> ATTACK_RANGE = SynchedEntityData.defineId(FlyingItemEntity.class, EntityDataSerializers.FLOAT);
    protected static final EntityDataAccessor<Integer> MOB_OWNER = SynchedEntityData.defineId(FlyingItemEntity.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Integer> TARGET_ID = SynchedEntityData.defineId(FlyingItemEntity.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Integer> TETHER_ID = SynchedEntityData.defineId(FlyingItemEntity.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Integer> VISUAL_TAG = SynchedEntityData.defineId(FlyingItemEntity.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Integer> IDLE_TICK = SynchedEntityData.defineId(FlyingItemEntity.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Integer> LAST_UPD = SynchedEntityData.defineId(FlyingItemEntity.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Boolean> IS_INTANGIBLE = SynchedEntityData.defineId(FlyingItemEntity.class, EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<ItemStack> HELD = SynchedEntityData.defineId(FlyingItemEntity.class, EntityDataSerializers.ITEM_STACK);
    protected static final EntityDataAccessor<MotionFrame> LAST_FRAME = SynchedEntityData.defineId(FlyingItemEntity.class, MotionFrame.SERIALIZER);
    protected static final EntityDataAccessor<MotionFrame> CURRENT_FRAME = SynchedEntityData.defineId(FlyingItemEntity.class, MotionFrame.SERIALIZER);
    //uses
    protected static final EntityDataAccessor<Vector3f> UNIVERSAL_OFFSET = SynchedEntityData.defineId(FlyingItemEntity.class, EntityDataSerializers.VECTOR3);
    //todo state machine
    protected static final EntityDataAccessor<Vector3f> LOCK_POS = SynchedEntityData.defineId(FlyingItemEntity.class, EntityDataSerializers.VECTOR3);
    protected static final EntityDataAccessor<Vector3f> LOCK_LOOK = SynchedEntityData.defineId(FlyingItemEntity.class, EntityDataSerializers.VECTOR3);
    //how the weapon floats when idle: point downwards
    protected static final EntityDataAccessor<MotionManager> IDLE_POSE = SynchedEntityData.defineId(FlyingItemEntity.class, MotionManager.SERIALIZER);
    protected static final EntityDataAccessor<STATE> CURRENT_STATE = SynchedEntityData.defineId(FlyingItemEntity.class, STATESERIALIZER);
    private static final Vector4d nothing = new Vector4d();
    //first one goes up to attack range, second does not scale
    protected final Deque<Tuple<SwingHistory, SwingHistory>> trailHistory = new ArrayDeque<>();
    public float rollO, displacementO, sizeO;
    public int renderLag = 0, renderLagO = 0;
    public int alpha = 0, alphaO = 0;
    protected int internalIdleTimer = 0;
    protected LivingEntity owner;
    protected Deque<MotionManager> moveQueue = new ConcurrentLinkedDeque<>();
    protected int animProgress = 0;
    protected MotionFrame update;
    protected Vector4d recalculatedOrientation;
    private int version = 0;
    private Entity target, tether;

    public FlyingItemEntity(EntityType<? extends FlyingItemEntity> type, Level level) {
        super(type, level);
        setHeldItem(new ItemStack(Items.IRON_SWORD));
        noPhysics = true;
    }

    public MotionManager getIdlePose() {
        return entityData.get(IDLE_POSE);
    }

    public void setIdlePose(MotionManager idlePose) {
        entityData.set(IDLE_POSE, idlePose);
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
        return moveQueue.isEmpty() && getState() == STATE.FOLLOW;
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
        MotionFrame last = update;
        if (update == null) last = getIdlePose().getEndFrame();
        //animProgress = 0;//why do you need this?
        //add the transition in, actual move, and transition out
        if (inTick > 0) moveQueue.add(new MotionManagers.TransitionMM(last, path, inTick, EasingFunction.IN_OUT_CUBIC));
        moveQueue.add(path);
        if (outTick > 0)
            moveQueue.add(new MotionManagers.TransitionMM(path, getIdlePose(), outTick, EasingFunction.LINEAR));
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
        if (target == null || target.getId() != id)
            target = level().getEntity(id);
        return target;
    }

    public void setMotionTarget(Entity track) {
        if (level().isClientSide()) return;
        target = track;
        int id = target == null ? -1 : target.getId();
        entityData.set(TARGET_ID, id);
    }

    public void clearPath() {
        while (!isIdle())
            updateMotionTargets(true);
    }

    protected boolean updateMotionTargets(boolean forceskip) {
        LivingEntity owner = getOwner();
        if (owner == null) return false;
        MotionManager motion = moveQueue.peek();
        if (motion == null || motion.hasEnded(animProgress) || forceskip) {
            moveQueue.poll();
            //motion = moveQueue.peek();
            //setTransitioning(motion == null || motion instanceof MotionManagers.TransitionMM);
            animProgress = 0;
            flushTrailHistory();
            return true;
        }
        return false;
    }

    public ItemStack getHeldItem() {
        return entityData.get(HELD);
    }

    public void setHeldItem(ItemStack stack) {
        if (level().isClientSide()) return;
        entityData.set(HELD, stack);
        //throw new IllegalArgumentException();
        //System.out.println("set stack to "+stack.getItem());
    }

    public float getRoll() {
        return entityData.get(ROLL);
    }

    public float getDisplacementForRender() {
        return 0;//entityData.get(ID_DISPLACE);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(ROLL, 0f);
        this.entityData.define(IS_INTANGIBLE, true);
        this.entityData.define(LAST_FRAME, new MotionFrame(Vec3.ZERO, Vec3.ZERO));
        this.entityData.define(CURRENT_FRAME, new MotionFrame(Vec3.ZERO, Vec3.ZERO));
        this.entityData.define(HELD, ItemStack.EMPTY);
        this.entityData.define(DATA_OWNERUUID_ID, Optional.empty());
        this.entityData.define(MOB_OWNER, 0);
        this.entityData.define(TARGET_ID, 0);
        this.entityData.define(TETHER_ID, 0);
        this.entityData.define(UNIVERSAL_OFFSET, new Vector3f());
        this.entityData.define(LOCK_POS, new Vector3f());
        this.entityData.define(LOCK_LOOK, new Vector3f());
        this.entityData.define(IDLE_POSE, new MotionManagers.FixedMM(new MotionFrame(new Vec3(0, -1, 0), Vec3.ZERO, new Vector4d(0, 0, 1, 0)), 5));
        this.entityData.define(ATTACK_RANGE, 3f);
        this.entityData.define(VISUAL_TAG, 11);//binary value 1011
        this.entityData.define(IDLE_TICK, 10);
        this.entityData.define(LAST_UPD, 0);
        this.entityData.define(CURRENT_STATE, STATE.FOLLOW);
    }

    public int getRawVisualTag() {
        return entityData.get(VISUAL_TAG);
    }

    public boolean shouldRender(FlyingWeaponEffect f) {
        return ((getRawVisualTag() >> f.ordinal()) & 1) > 0;
    }

    public void setShouldRender(FlyingWeaponEffect f, boolean enable) {
        if (level().isClientSide()) return;
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

    public void setShouldRender(FlyingWeaponEffect... ff) {
        if (level().isClientSide()) return;
        int finalMask = 0;
        for (FlyingWeaponEffect f : ff) {
            if (ff == null) continue;
            int mask = 1 << f.ordinal();
            finalMask |= mask;
        }
        entityData.set(VISUAL_TAG, finalMask); // Clear the bit
    }

    // Custom motion logic and collision in tick()
    @Override
    public void tick() {
        super.tick();
        setGlowingTag(false);
        entityData.set(LAST_FRAME, new MotionFrame(position(), getLookAngle(), new Vector4d(getXRot(), getYRot(), getRoll(), 0)));
        // Movement logic
        if (level().isClientSide) {
            rollO = getRoll();
            displacementO = getDisplacementForRender();
            this.move(MoverType.SELF, this.getDeltaMovement());
            updateTetheringVelocity();
            updateClientData();
            //how can the client get ahold of moveset data for smoothing?
            // answer: don't. It's painful. Just sync whether it's idle
            return;
        }
        if (getMotionTarget() == null || !getMotionTarget().isAlive()) setMotionTarget(getOwner());
        if (getOwner() != null && getMotionTarget() != null) {

            if (!moveQueue.isEmpty()) {
                entityData.set(IDLE_TICK, 0);
                animProgress++;
                executeMoveQueue();

            } else if (getState() == STATE.THROW_TRACK) {
                trackingFly();
            } else if (getState() == STATE.THROW_NATURAL) {
                naturallyFly();
            } else if (isIdle()) {
                //idle state
                entityData.set(IDLE_TICK, getIdlePose().getDuration());
                returnToIdle(getIdlePose().getDuration());
            }

            //move set here to ensure orientation is not messed up first
            this.move(MoverType.SELF, this.getDeltaMovement());
            updateTetheringVelocity();

            // Collision and attack logic
            if (!level().isClientSide()) {
                handleBlockCollisions();
                handleEntityCollisions();
            }

            // update client for trail rendering, done after block collision checks
            if (update != null) {
                //entityData.set(LAST_FRAME, entityData.get(CURRENT_FRAME));
                MotionFrame reconstructed = new MotionFrame(update.direction(), update.offset(), recalculatedOrientation);
                entityData.set(CURRENT_FRAME, reconstructed);
            }
        }
    }

    private void trackingFly() {
        if (getDeltaMovement().lengthSqr() > 0.003)
            recalculatedOrientation = recalculateOrientation(nothing, 1);
    }

    private void naturallyFly() {
        if (getDeltaMovement().lengthSqr() > 0.003)
            recalculatedOrientation = recalculateOrientation(nothing, 1);
    }

    protected void handleEntityCollisions() {
        if (!transitioning()) {
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

    public void lock(Entity to) {
        if (level().isClientSide()) return;
        getEntityData().set(CURRENT_STATE, STATE.FIXED_POINT);
        getEntityData().set(LOCK_POS, to.position().add(0, to.getBbHeight() / 2, 0).toVector3f());
        getEntityData().set(LOCK_LOOK, to.getLookAngle().toVector3f());
    }

    public void unlock() {
        if (level().isClientSide()) return;
        getEntityData().set(CURRENT_STATE, STATE.FOLLOW);
    }

    protected abstract boolean onHitEntity(List<Entity> targets);

    protected void handleBlockCollisions() {
        if (!transitioning()) {
            final float range = getInteractionRange();
            HitResult hit = level().clip(new ClipContext(getPosition(0).add(getViewVector(0).scale(range)), getPosition(1).add(getViewVector(1).scale(range)), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
            if (hit.getType() == HitResult.Type.BLOCK) {
                BlockHitResult blockHit = (BlockHitResult) hit;
                BlockPos blockPos = blockHit.getBlockPos();
                Direction hitFace = blockHit.getDirection();
                onHitBlock(blockPos, hitFace, blockHit.getLocation());
            }
        }
    }

    protected void returnToIdle(int duration) {
        unlock();
        if (getMotionTarget() == null) return;
        //idle animation, float next to the player
        update = getIdlePose().getEndFrame();
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
        update = new MotionFrame(lerp.subtract(bundle.getA()), new Vec3(0, 0, 0), reverseEngineer(recalculatedOrientation));

        displacementO = getDisplacementForRender();
    }

    protected void executeMoveQueue() {
        MotionManager activeMove = moveQueue.peek();
        if (activeMove == null) return;
        //motion path test
        // Recalculate target every tick relative to player
        //keep the lerped frame for future movement
        update = activeMove.getNextPoint(animProgress);
        final Tuple<Vec3, Vec3> bundle = stateDependentPositionLook();
        Vec3 transformedDirection = update.resolveTargetOffset(bundle, getUniversalOffset(), 1);

        // Move toward the desired position smoothly
        Vec3 delta = transformedDirection.subtract(this.position());

        this.setPos(transformedDirection);
        //make the first anim progress also reset the last frame
        //setDeltaMovement(delta);
        recalculatedOrientation = recalculateOrientation(update.renderOrientation(), 1f);

        //prevent recursion
        if (animProgress == 0) return;

        if (activeMove.hasEnded(animProgress)) {//end current path, execute next path (either transition or next path)
            updateMotionTargets(false);
        } else {
            if (activeMove != getIdlePose()) {
                //still animating, keep frames on
                displacementO = getDisplacementForRender();
            }
        }
        //entityData.set(ID_DISPLACE, (float) Math.max(0, lerped.offset().length() * attackRange-1));
    }

    private Tuple<Vec3, Vec3> stateDependentPositionLook() {
        switch (getState()) {
            case FIXED_POINT -> {
                return new Tuple<>(new Vec3(getEntityData().get(LOCK_POS)), new Vec3(getEntityData().get(LOCK_LOOK)));
            }
            case THROW_TRACK -> {
                return new Tuple<>(getMotionTarget().position(), getDeltaMovement());
            }
            case THROW_NATURAL -> {
                return new Tuple<>(position(), getDeltaMovement());
            }
            default -> {
                return new Tuple<>(getOwner().position().add(0, getOwner().getBbHeight() / 2, 0), getOwner().getLookAngle());//fixme
            }
        }
    }

    protected void updateClientData() {
        renderLagO = renderLag;
        sizeO = getInteractionRange();
        alphaO = alpha;
        if (shouldRender(FlyingWeaponEffect.BIG_SHADOW)) {
            if (alpha < 130) alpha += 35;
        } else if (alpha > 0) alpha -= 35;
        if (entityData.get(IDLE_TICK) > 0) {
            //special case, idle needs smooth animations
            returnToIdle(entityData.get(IDLE_TICK));
        }
        //lerp 5 points between each tick
        if (getMotionTarget() != null) {
            //fixme interpolations don't calculate from the right to frame during idle, directly store to and from in idle handling?
            Vec3 universalOffset = getUniversalOffset();
            MotionFrame from = entityData.get(LAST_FRAME);//position, look, (xrot, yrot, roll)
            MotionFrame to = entityData.get(CURRENT_FRAME);//direction, offset, only recalculated orientation (xrot, yrot, zrot... in theory)
            double dist = transitioning() ? -1 : getInteractionRange() - 1;//the -1 here is necessary to counteract the base
            Vec3 fromTrailPos = from.direction().add(from.offset().scale(dist));
            //this is NOT accurate!
            Vec3 fromShadowPos = from.direction();
            Vec3 toTrailPos = to.resolveTargetOffset(stateDependentPositionLook(), universalOffset, getInteractionRange());
            Vec3 toShadowPos = to.resolveTargetOffset(stateDependentPositionLook(), universalOffset, 1);
            for (double i = 0; i < CLIENT_SMOOTHING_SUBTICKS; i++) {
                double partialTick = i / CLIENT_SMOOTHING_SUBTICKS;

                //trail, max range
                float lerpX = (float) Mth.lerp(partialTick, from.renderOrientation().x, to.renderOrientation().x);
                float lerpY = (float) Mth.lerp(partialTick, from.renderOrientation().y, to.renderOrientation().y);
                float lerpZ = (float) Mth.lerp(partialTick, from.renderOrientation().z, to.renderOrientation().z);
                Vec3 trailPosition = fromTrailPos.lerp(toTrailPos, partialTick);
                SwingHistory trail = new SwingHistory(trailPosition, !transitioning(), lerpX, lerpY, lerpZ);//yes, this is correct, stop asking

                //shadow, range 1
                lerpX = (float) Mth.lerp(partialTick, from.renderOrientation().x, to.renderOrientation().x);
                lerpY = (float) Mth.lerp(partialTick, from.renderOrientation().y, to.renderOrientation().y);
                lerpZ = (float) Mth.lerp(partialTick, from.renderOrientation().z, to.renderOrientation().z);
                trailPosition = fromShadowPos.lerp(toShadowPos, partialTick);
                SwingHistory shadow = new SwingHistory(trailPosition, !transitioning(), lerpX, lerpZ, lerpY);//yes, this is correct, stop asking

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

    protected abstract void onHitBlock(BlockPos blockPos, Direction hitFace, Vec3 location);

    public Vec3 stateDependentOffset(Vector4d quaternion) {
        // Get player's rotation as a basis
        Vec3 forward = null;
        if (getState() == STATE.FOLLOW) {
            //fixme transitioning to using worldDirection does not guarantee proper offsets
            LivingEntity referent = getOwner();
            forward = referent.getLookAngle().normalize();
            if (forward.lengthSqr() < 0.0001) forward = new Vec3(0, 0, 1); // fallback
        } else if (getState() == STATE.FIXED_POINT) {
            forward = new Vec3(getEntityData().get(LOCK_LOOK));
        }
        if (forward != null) {
            // Create right and up basis vectors
            Vec3 globalUp = new Vec3(0, 1, 0);
            Vec3 right = forward.cross(globalUp).normalize();
            Vec3 up = right.cross(forward).normalize();  // Ensure orthogonal
            return right.scale(quaternion.x).add(up.scale(quaternion.y)).add(forward.scale(quaternion.z)).normalize();
        }
        return getDeltaMovement();
    }

    public Vector4d recalculateOrientation(Vector4d quaternion, float snappiness) {
        // Step 1: Convert visualOrientation to world-space direction
        // This assumes visualOrientation is like a local-space forward vector (e.g., (0, 0, 1))
        Vec3 worldDirection = stateDependentOffset(quaternion);

        // Step 2: Face that world direction
        float targetPitch = (float) Mth.wrapDegrees(-Mth.atan2(worldDirection.y, Math.sqrt(worldDirection.x * worldDirection.x + worldDirection.z * worldDirection.z)) * (180F / Math.PI));
        float targetYaw = (float) Mth.wrapDegrees(-Mth.atan2(worldDirection.x, worldDirection.z) * (180F / Math.PI));
        rollO = entityData.get(ROLL);
        float lerpX = Mth.lerp(snappiness, getXRot(), targetPitch);
        float lerpY = Mth.lerp(snappiness, getYRot(), targetYaw);
        double lerpZ = Mth.lerp(snappiness, rollO, quaternion.w);
        setYRot(lerpY);
        setXRot(lerpX);
        entityData.set(ROLL, (float) lerpZ);
        return new Vector4d(getXRot(), getYRot(), lerpZ, 0);
    }

    private Vector4d reverseEngineer(Vector4d returned) {
        // Extract pitch/yaw/roll from the returned vector (matches your recalc output)
        double pitchDeg = returned.x; // you returned new Vector4d(getXRot(), getYRot(), rollZ, 0)
        double yawDeg = returned.y;
        double rollDeg = returned.z; // or returned.w if you use w for roll; adapt accordingly

        // Step A: recreate the world-space forward direction from pitch/yaw.
        // Recall: your recalc used targetPitch = -atan2(worldY, sqrt(x^2+z^2))
        // and targetYaw = -atan2(worldX, worldZ).
        // The inverse mapping is:
        double pitchRad = Math.toRadians(-pitchDeg); // note the negative used previously
        double yawRad = Math.toRadians(-yawDeg);

        double cosPitch = Math.cos(pitchRad);
        double worldX = Math.sin(yawRad) * cosPitch;
        double worldY = Math.sin(pitchRad);
        double worldZ = Math.cos(yawRad) * cosPitch;
        Vec3 worldDir = new Vec3(worldX, worldY, worldZ).normalize();

        // Step B: compute owner's basis axes in world-space.
        // Forward is the owner's look direction. Use same convention as your resolve code:
        Vec3 ownerForward = stateDependentOffset(new Vector4d(0, 0, 1, 0));
        Vec3 worldUp = new Vec3(0, 1, 0);
        Vec3 ownerRight = ownerForward.cross(worldUp).normalize();
        Vec3 ownerUp = ownerRight.cross(ownerForward).normalize();

        // Step C: express worldDir in owner's local coordinates (dot with axes)
        double localX = ownerRight.x * worldDir.x + ownerRight.y * worldDir.y + ownerRight.z * worldDir.z;
        double localY = ownerUp.x * worldDir.x + ownerUp.y * worldDir.y + ownerUp.z * worldDir.z;
        double localZ = ownerForward.x * worldDir.x + ownerForward.y * worldDir.y + ownerForward.z * worldDir.z;

        // Optional: renormalize the local vector if you rely on it being unit-length
        double len = Math.sqrt(localX * localX + localY * localY + localZ * localZ);
        if (len > 1e-9) {
            localX /= len;
            localY /= len;
            localZ /= len;
        } else {
            // fallback: forward in local space
            localX = 0;
            localY = 0;
            localZ = 1;
        }

        // Step D: pack into the same Vector4d shape your MotionFrame expects:
        // (localDir.x, localDir.y, localDir.z, roll)
        return new Vector4d(localX, localY, localZ, rollDeg);
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

    public boolean transitioning() {
        return entityData.get(IS_INTANGIBLE);
    }

    public void setTransitioning(boolean incorporeal) {
        if (level().isClientSide()) return;
        entityData.set(IS_INTANGIBLE, incorporeal);
    }

    @Override
    public @Nullable LivingEntity getTarget() {
        return getMotionTarget() instanceof LivingEntity le ? le : null;
    }

    @Override
    public Entity getTetheringEntity() {
        final int id = entityData.get(TETHER_ID);
        if (tether == null || tether.getId() != id)
            tether = level().getEntity(id);
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
        FOLLOW,
        FIXED_POINT,
        THROW_NATURAL,
        THROW_TRACK
    }
}
