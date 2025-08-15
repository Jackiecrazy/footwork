package jackiecrazy.footwork.entity.flyingweapon;

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
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.joml.Vector4d;

import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

public abstract class FlyingItemEntity extends Entity implements OwnableEntity, Targeting {
    public static final int MAX_TRAIL_LENGTH = 15;
    public static final int CLIENT_SMOOTHING_SUBTICKS = 7;
    protected static final List<MotionFrame> STAB = List.of(new MotionFrame(new Vec3(0, 0, 1), new Vec3(0, 0, -1)), new MotionFrame(new Vec3(0, 0, 1), new Vec3(0, 0, 1.4)), new MotionFrame(new Vec3(0, 0, 1), new Vec3(0, 0, 1.4)));
    protected static final List<MotionFrame> CIRCLE = List.of(new MotionFrame(new Vec3(0, 0, 1), new Vec3(0, 0, 1), new Vector4d(0, 0, 1, 90)), new MotionFrame(new Vec3(-1, 0, 0), new Vec3(0, 0, 1), new Vector4d(-1, 0, 0, 90)), new MotionFrame(new Vec3(0, 0, -1), new Vec3(0, 0, 1), new Vector4d(0, 0, -1, 90)), new MotionFrame(new Vec3(1, 0, 0), new Vec3(0, 0, 1), new Vector4d(1, 0, 0, 90)), new MotionFrame(new Vec3(0, 0, 1), new Vec3(0, 0, 1), new Vector4d(0, 0, 1, 90)));
    protected static final List<MotionFrame> SLASH = List.of(new MotionFrame(new Vec3(1, 0.6, 1), new Vec3(0, 0, 1)), new MotionFrame(new Vec3(-1, -0.4, 0), new Vec3(0, 0, 1), new Vector4d(-1, -1, 1, 45)));
    protected static final List<MotionFrame> BACKSLASH = List.of(new MotionFrame(new Vec3(-1, 0.6, 1), new Vec3(0, 0, 1), -45), new MotionFrame(new Vec3(1, -0.4, 0), new Vec3(0, 0, 1), new Vector4d(1, -1, 1, -45)));
    protected static final List<MotionFrame> CHOP = List.of(new MotionFrame(new Vec3(0, 1, 0.2), new Vec3(0, 0, 1)), new MotionFrame(new Vec3(0, -0.5, 1), new Vec3(0, 0, 1)));
    protected static final List<MotionManager> EVERYONE = List.of(new MotionManagers.DefinitionMM(new MotionGroup(CIRCLE, EasingFunction.IN_OUT_CUBIC, 3)), new MotionManagers.DefinitionMM(new MotionGroup(STAB, EasingFunction.IN_CUBIC, 3)), new MotionManagers.DefinitionMM(new MotionGroup(SLASH, EasingFunction.IN_OUT_CUBIC, 3)), new MotionManagers.DefinitionMM(new MotionGroup(BACKSLASH, EasingFunction.IN_OUT_CUBIC, 3)), new MotionManagers.DefinitionMM(new MotionGroup(CHOP, EasingFunction.IN_CUBIC, 3)));
    protected static final EntityDataAccessor<Optional<UUID>> DATA_OWNERUUID_ID = SynchedEntityData.defineId(FlyingItemEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    protected static final EntityDataAccessor<Float> ROLL = SynchedEntityData.defineId(FlyingItemEntity.class, EntityDataSerializers.FLOAT);
    protected static final EntityDataAccessor<Float> ATTACK_RANGE = SynchedEntityData.defineId(FlyingItemEntity.class, EntityDataSerializers.FLOAT);
    protected static final EntityDataAccessor<Integer> MOB_OWNER = SynchedEntityData.defineId(FlyingItemEntity.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Integer> TARGET_ID = SynchedEntityData.defineId(FlyingItemEntity.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Integer> VISUAL_TAG = SynchedEntityData.defineId(FlyingItemEntity.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Boolean> IS_INTANGIBLE = SynchedEntityData.defineId(FlyingItemEntity.class, EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<ItemStack> HELD = SynchedEntityData.defineId(FlyingItemEntity.class, EntityDataSerializers.ITEM_STACK);
    protected static final EntityDataAccessor<MotionFrame> LAST_FRAME = SynchedEntityData.defineId(FlyingItemEntity.class, MotionFrame.SERIALIZER);
    protected static final EntityDataAccessor<MotionFrame> CURRENT_FRAME = SynchedEntityData.defineId(FlyingItemEntity.class, MotionFrame.SERIALIZER);
    //fixme not precise enough
    protected static final EntityDataAccessor<Vector3f> UNIVERSAL_OFFSET = SynchedEntityData.defineId(FlyingItemEntity.class, EntityDataSerializers.VECTOR3);
    //first one goes up to attack range, second does not scale
    protected final Deque<Tuple<SwingHistory, SwingHistory>> trailHistory = new ArrayDeque<>();
    public float rollO, displacementO, sizeO;
    public int renderLag = 0, renderLagO = 0;
    public int alpha = 0, alphaO = 0;
    //how the weapon floats when idle: point downwards
    protected MotionManager idlePose = new MotionManagers.FixedMM(new MotionFrame(new Vec3(0, -1, 0), Vec3.ZERO, new Vector4d(0, 0, 1, 0)), 5);
    protected int internalIdleTimer = 0;
    protected LivingEntity owner;
    protected Deque<MotionManager> moveQueue = new ConcurrentLinkedDeque<>();
    protected int animProgress = 0;
    protected MotionFrame update;
    protected Vector4d recalculatedOrientation;
    private Entity target;

    public FlyingItemEntity(EntityType<? extends FlyingItemEntity> type, Level level) {
        super(type, level);
        setHeldItem(new ItemStack(Items.IRON_SWORD));
    }

    public MotionManager getIdlePose() {
        return idlePose;
    }

    public void setIdlePose(MotionManager idlePose) {
        this.idlePose = idlePose;
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
        entityData.set(ATTACK_RANGE, range);
    }

    public Deque<Tuple<SwingHistory, SwingHistory>> getTrailHistory() {
        return trailHistory;
    }

    public boolean isIdle() {
        return moveQueue.isEmpty();
    }

    public void queuePath(MotionManager path, int inTick, int outTick) {
        internalIdleTimer = 0;
        //if there was another return to idle animation, remove it first
        if (moveQueue.peekLast() instanceof MotionManagers.TransitionMM) moveQueue.removeLast();
        //grab the (new) end of move queue, or idle if we are currently idle
        //CHANGED: grab the last motion we did
        MotionFrame last = update;
        if (update == null) last = idlePose.getNextPoint(0);
        animProgress = 0;
        //add the transition in, actual move, and transition out
        if (inTick > 0) moveQueue.add(new MotionManagers.TransitionMM(last, path, inTick, EasingFunction.IN_OUT_CUBIC));
        moveQueue.add(path);
        if (outTick > 0) moveQueue.add(new MotionManagers.TransitionMM(path, idlePose, outTick, EasingFunction.LINEAR));
        updateMotionTargets(false);
    }

    @Override
    public @Nullable UUID getOwnerUUID() {
        return entityData.get(DATA_OWNERUUID_ID).orElse(null);
    }

    protected void setOwnerUUID(UUID to) {
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
        if (e == null) return;
        if (e instanceof Player) setOwnerUUID(e.getUUID());
        else entityData.set(MOB_OWNER, e.getId());
        owner = e;
        if (e.getAttribute(ForgeMod.ENTITY_REACH.get()) != null)
            setInteractionRange((float) e.getAttributeValue(ForgeMod.ENTITY_REACH.get()));
    }

    public Entity getMotionReferent() {
        final int id = entityData.get(TARGET_ID);
        if (target == null || target.getId() != id)
            target = level().getEntity(id);
        return target;
    }

    public void setMotionReferent(Entity track) {
        target = track;
        int id = target == null ? -1 : target.getId();
        entityData.set(TARGET_ID, id);
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
            return true;
        }
        return false;
    }

    public ItemStack getHeldItem() {
        return entityData.get(HELD);
    }

    public void setHeldItem(ItemStack stack) {
        entityData.set(HELD, stack);
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
        this.entityData.define(UNIVERSAL_OFFSET, new Vector3f());
        this.entityData.define(ATTACK_RANGE, 3f);
        this.entityData.define(VISUAL_TAG, 11);//binary value 1011
    }

    public int getRawVisualTag() {
        return entityData.get(VISUAL_TAG);
    }

    public boolean shouldRender(FlyingWeaponEffect f) {
        return ((getRawVisualTag() >> f.ordinal()) & 1) > 0;
    }

    public void setShouldRender(FlyingWeaponEffect f, boolean enable) {
        int mask = 1 << f.ordinal();
        if (enable) {
            entityData.set(VISUAL_TAG, getRawVisualTag() | mask); // Set the bit
        } else {
            entityData.set(VISUAL_TAG, getRawVisualTag() & ~mask); // Clear the bit
        }
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
            updateClientData();
            //todo how can the client get ahold of moveset data for smoothing?
            return;
        }
        if (getMotionReferent() == null || !getMotionReferent().isAlive()) setMotionReferent(getOwner());
        if (getOwner() != null && getMotionReferent() != null) {

            if (!moveQueue.isEmpty()) {
                executeMoveQueue();

            } else {
                returnToIdle();
            }

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

    protected void handleEntityCollisions() {
        if (!transitioning()) {
            final float range = getInteractionRange();
            List<Entity> selfTarget = level().getEntities(getOwner(), getBoundingBox().inflate(0.2f), e -> e != getOwner() && e.isAlive() && e.isAttackable());
            List<Entity> viewTarget = level().getEntities(getOwner(), getBoundingBox().move(getLookAngle().scale(range)).inflate(0.2f), e -> e != getOwner() && e.isAlive() && e.isAttackable());
            List<Entity> targets = GeneralUtils.arcTraceEntities(level(), getMotionReferent(), getPosition(0).add(getViewVector(0).scale(range)), getPosition(1).add(getViewVector(1).scale(range)), range, range / 3, Entity::isAttackable);//level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(0.3));

            selfTarget.addAll(viewTarget);
            selfTarget.addAll(targets);
            onHitEntity(selfTarget);
        }
    }

    protected abstract void onHitEntity(List<Entity> targets);

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

    protected void returnToIdle() {
        //idle animation, float next to the player
        update = idlePose.getNextPoint(0);
        Vec3 transformedDirection = update.resolveTargetOffset(getMotionReferent(), getUniversalOffset(), 1);

        // Move toward the desired position smoothly
        Vec3 currentPos = position();
        //final double snappiness = Mth.clamp(5/transformedDirection.distanceToSqr(currentPos), 0.25, 1);
        final float snappiness = 1f/idlePose.getDuration();//todo 0.25f make this a variable
        //fixme it's gonna be choppy unless I sync to client
        // sync first frame and last frame unless it's a full definitionmm
        Vec3 lerp = currentPos.lerp(transformedDirection, snappiness);
        Vec3 delta = lerp.subtract(currentPos);
        setPos(lerp);
        setDeltaMovement(delta);
        recalculatedOrientation = recalculateOrientation(getMotionReferent(), update.renderOrientation(), (float) snappiness);

        displacementO = getDisplacementForRender();
    }

    protected void executeMoveQueue() {
        MotionManager activeMove = moveQueue.peek();
        //motion path test
        // Recalculate target every tick relative to player
        animProgress++;
        //keep the lerped frame for future movement
        update = activeMove.getNextPoint(animProgress);
        Vec3 transformedDirection = update.resolveTargetOffset(getMotionReferent(), getUniversalOffset(), 1);

        // Move toward the desired position smoothly
        Vec3 delta = transformedDirection.subtract(this.position());

        this.setPos(transformedDirection);
        setDeltaMovement(delta);
        recalculatedOrientation = recalculateOrientation(getMotionReferent(), update.renderOrientation(), 1f);

        if (activeMove.hasEnded(animProgress)) {//end current path, execute next path (either transition or next path)
            updateMotionTargets(false);
        } else if (activeMove != idlePose) {
            //still animating, keep frames on
            //setIncorporeal(false);
            displacementO = getDisplacementForRender();
        }
        //entityData.set(ID_DISPLACE, (float) Math.max(0, lerped.offset().length() * attackRange-1));
    }

    protected void updateClientData() {
        renderLagO = renderLag;
        sizeO = getInteractionRange();
        if (!transitioning()) {
            if (renderLag < 2) renderLag++;
        } else {
            if (renderLag > 0) renderLag--;
        }
        alphaO = alpha;
        if (shouldRender(FlyingWeaponEffect.BIG_SHADOW)) {
            if (alpha < 130) alpha += 35;
        } else if (alpha > 0) alpha -= 35;
        //lerp 5 points between each tick
        if (getMotionReferent() != null) {
            Vec3 universalOffset = getUniversalOffset();
            MotionFrame from = entityData.get(LAST_FRAME);//position, look, (xrot, yrot, roll)
            MotionFrame to = entityData.get(CURRENT_FRAME);//direction, offset, only recalculated orientation (xrot, yrot, zrot... in theory)
            Vec3 fromTrailPos = from.direction().add(from.offset().scale(getInteractionRange()));
            //this is NOT accurate!
            Vec3 fromShadowPos = from.direction();
            Vec3 toTrailPos = to.resolveTargetOffset(getMotionReferent(), universalOffset, getInteractionRange());
            Vec3 toShadowPos = to.resolveTargetOffset(getMotionReferent(), universalOffset, 1);
            for (double i = 0; i < CLIENT_SMOOTHING_SUBTICKS; i++) {
                //fixme weapon trembles for some reason
                double partialTick = i / CLIENT_SMOOTHING_SUBTICKS;

                //trail, max range
                float lerpX = (float) Mth.lerp(partialTick, from.renderOrientation().x, to.renderOrientation().x);
                float lerpY = (float) Mth.lerp(partialTick, from.renderOrientation().y, to.renderOrientation().y);
                float lerpZ = (float) Mth.lerp(partialTick, from.renderOrientation().z, to.renderOrientation().z);
                Vec3 trailPosition = fromTrailPos.lerp(toTrailPos, partialTick);
                SwingHistory trail = new SwingHistory(trailPosition, lerpX, lerpY, lerpZ);

                //shadow, range 1
                lerpX = (float) Mth.lerp(partialTick, from.renderOrientation().x, to.renderOrientation().x);
                lerpY = (float) Mth.lerp(partialTick, from.renderOrientation().y, to.renderOrientation().y);
                lerpZ = (float) Mth.lerp(partialTick, from.renderOrientation().z, to.renderOrientation().z);
                trailPosition = fromShadowPos.lerp(toShadowPos, partialTick);
                SwingHistory shadow = new SwingHistory(trailPosition, lerpX, lerpZ, lerpY);

                trailHistory.addFirst(new Tuple<>(trail, shadow));
            }
            while (trailHistory.size() > MAX_TRAIL_LENGTH * CLIENT_SMOOTHING_SUBTICKS) {
                trailHistory.removeLast();
            }
        }
    }

    protected abstract void onHitBlock(BlockPos blockPos, Direction hitFace, Vec3 location);

    public Vector4d recalculateOrientation(Entity referent, Vector4d quaternion, float snappiness) {
        // Step 1: Convert visualOrientation to world-space direction
        // This assumes visualOrientation is like a local-space forward vector (e.g., (0, 0, 1))

        Vec3 worldDirection = getDeltaMovement();
        // Get player's rotation as a basis
        if (referent == getOwner()) {
            Vec3 forward = referent.getLookAngle().normalize();
            if (forward.lengthSqr() < 0.0001) forward = new Vec3(0, 0, 1); // fallback

            // Create right and up basis vectors
            Vec3 globalUp = new Vec3(0, 1, 0);
            Vec3 right = forward.cross(globalUp).normalize();
            Vec3 up = right.cross(forward).normalize();  // Ensure orthogonal
            worldDirection = right.scale(quaternion.x).add(up.scale(quaternion.y)).add(forward.scale(quaternion.z)).normalize();
        }
        // Step 2: Face that world direction
        float targetPitch = (float) Mth.wrapDegrees(-Mth.atan2(worldDirection.y, Math.sqrt(worldDirection.x * worldDirection.x + worldDirection.z * worldDirection.z)) * (180F / Math.PI));
        float targetYaw = (float) Mth.wrapDegrees(-Mth.atan2(worldDirection.x, worldDirection.z) * (180F / Math.PI));
        rollO = entityData.get(ROLL);
        float lerpX = Mth.lerp(snappiness, getXRot(), targetPitch);
        float lerpY = Mth.lerp(snappiness, getYRot(), targetYaw);
        double lerpZ = Mth.lerp(snappiness, rollO, quaternion.w);
        //detect drastic changes that imply the weapon reached a 180 location
//        if (!Float.isFinite(lerpX)) {
//            Footwork.LOGGER.warn("x is somehow not finite, resetting");
//            lerpX = targetPitch;
//        }
//        if (!Float.isFinite(lerpY)) {
//            Footwork.LOGGER.warn("y is somehow not finite, resetting");
//            lerpY = targetYaw;
//        }
//        if (!Double.isFinite(lerpZ)) {
//            Footwork.LOGGER.warn("z is somehow not finite, resetting");
//            lerpZ = quaternion.w;
//        }
        setYRot(lerpY);
        setXRot(lerpX);
        entityData.set(ROLL, (float) lerpZ);
        return new Vector4d(getXRot(), getYRot(), lerpZ, 0);
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
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        if (!getHeldItem().isEmpty()) tag.put("Item", getHeldItem().save(new CompoundTag()));
        if (getOwnerUUID() != null) tag.putUUID("ownerUUID", getOwnerUUID());
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    @Override
    public boolean isAttackable() {
        return false;
    }

    @Override
    public AABB getBoundingBoxForCulling() {
        return super.getBoundingBoxForCulling().inflate(8.0D);
    }

    public boolean transitioning() {
        return entityData.get(IS_INTANGIBLE);
    }

    public void setTransitioning(boolean incorporeal) {
        entityData.set(IS_INTANGIBLE, incorporeal);
    }

    @Override
    public @Nullable LivingEntity getTarget() {
        return getMotionReferent() instanceof LivingEntity le ? le : null;
    }
}
