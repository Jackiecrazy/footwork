package jackiecrazy.footwork.entity.flyingweapon;

import jackiecrazy.footwork.Footwork;
import jackiecrazy.footwork.move.motionframe.MotionDefinition;
import jackiecrazy.footwork.move.motionframe.MotionFrame;
import jackiecrazy.footwork.move.motionframe.MotionManager;
import jackiecrazy.footwork.move.motionframe.MotionManagers;
import jackiecrazy.footwork.utils.EasingFunction;
import jackiecrazy.footwork.utils.GeneralUtils;
import jackiecrazy.footwork.utils.TargetingUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.monster.Husk;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector4d;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;

public class FlyingWeaponEntity extends Entity implements OwnableEntity {
    public static final int MAX_TRAIL_LENGTH = 15;
    public static final int CLIENT_SMOOTHING_SUBTICKS = 7;
    protected static final EntityDataAccessor<Optional<UUID>> DATA_OWNERUUID_ID = SynchedEntityData.defineId(FlyingWeaponEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final List<MotionFrame> TWIST_THE_KNIFE = List.of(new MotionFrame(new Vec3(1, -1, 1), new Vec3(0, 0, 1), 135), new MotionFrame(new Vec3(0, 0, 1), new Vec3(0, 0, 0), 135), new MotionFrame(new Vec3(0, 0, 1), new Vec3(0, 0, 1.3)), new MotionFrame(new Vec3(1, -1, 1), new Vec3(0, 0, 1), -60));
    private static final List<MotionFrame> STAB = List.of(
            new MotionFrame(new Vec3(0, 0, 1), new Vec3(0, 0, -1)),
            new MotionFrame(new Vec3(0, 0, 1), new Vec3(0, 0, 1.4)),
            new MotionFrame(new Vec3(0, 0, 1), new Vec3(0, 0, 1.4))
    );
    private static final List<MotionFrame> CIRCLE = List.of(
            new MotionFrame(new Vec3(0, 0, 1), new Vec3(0, 0, 1), new Vector4d(0, 0, 1, 90)),
            new MotionFrame(new Vec3(-1, 0, 0), new Vec3(0, 0, 1), new Vector4d(-1, 0, 0, 90)),
            new MotionFrame(new Vec3(0, 0, -1), new Vec3(0, 0, 1), new Vector4d(0, 0, -1, 90)),
            new MotionFrame(new Vec3(1, 0, 0), new Vec3(0, 0, 1), new Vector4d(1, 0, 0, 90)),
            new MotionFrame(new Vec3(0, 0, 1), new Vec3(0, 0, 1), new Vector4d(0, 0, 1, 90)));
    private static final List<MotionFrame> SLASH = List.of(
            new MotionFrame(new Vec3(1, 0.6, 1), new Vec3(0, 0, 1)),
            new MotionFrame(new Vec3(-1, -0.4, 0), new Vec3(0, 0, 1), new Vector4d(-1, -1, 1, 45)));
    private static final List<MotionFrame> BACKSLASH = List.of(
            new MotionFrame(new Vec3(-1, 0.6, 1), new Vec3(0, 0, 1), -45),
            new MotionFrame(new Vec3(1, -0.4, 0), new Vec3(0, 0, 1), new Vector4d(1, -1, 1, -45)));
    private static final List<MotionFrame> CHOP = List.of(
            new MotionFrame(new Vec3(0, 1, 0.2), new Vec3(0, 0, 1)),
            new MotionFrame(new Vec3(0, -0.5, 1), new Vec3(0, 0, 1)));
    private static final List<MotionManager> EVERYONE = List.of(
            new MotionManagers.DefinitionMM(new MotionDefinition(CIRCLE, EasingFunction.IN_OUT_CUBIC, 30)),
            new MotionManagers.DefinitionMM(new MotionDefinition(STAB, EasingFunction.IN_CUBIC, 30)),
            new MotionManagers.DefinitionMM(new MotionDefinition(SLASH, EasingFunction.IN_OUT_CUBIC, 30)),
            new MotionManagers.DefinitionMM(new MotionDefinition(BACKSLASH, EasingFunction.IN_OUT_CUBIC, 30)),
            new MotionManagers.DefinitionMM(new MotionDefinition(CHOP, EasingFunction.IN_CUBIC, 30)));
    private static final EntityDataAccessor<Float> ID_ROLL = SynchedEntityData.defineId(FlyingWeaponEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> MOB_OWNER = SynchedEntityData.defineId(FlyingWeaponEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> ID_INTANGIBLE = SynchedEntityData.defineId(FlyingWeaponEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<ItemStack> HELD = SynchedEntityData.defineId(FlyingWeaponEntity.class, EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<MotionFrame> LAST_FRAME = SynchedEntityData.defineId(FlyingWeaponEntity.class, MotionFrame.SERIALIZER);
    private static final EntityDataAccessor<MotionFrame> CURRENT_FRAME = SynchedEntityData.defineId(FlyingWeaponEntity.class, MotionFrame.SERIALIZER);

    public MotionManager getIdlePose() {
        return idlePose;
    }

    public void setIdlePose(MotionManager idlePose) {
        this.idlePose = idlePose;
    }

    //how the weapon floats when idle: point downwards
    private MotionManager idlePose = new MotionManagers.FixedMM(new MotionFrame(new Vec3(0, -1, 0), Vec3.ZERO, new Vector4d(0, 0, 1, 0)), 20);
    private final List<Entity> alreadyHit = new ArrayList<>();
    private final Deque<Tuple<SwingHistory, SwingHistory>> trailHistory = new ArrayDeque<>();
    public float rollO, displacementO;
    public double attackRange = 3;
    public int renderLag = 0, renderLagO=0;
    private int internalIdleTimer=0;
    private LivingEntity owner;
    private Deque<MotionManager> moveQueue = new ConcurrentLinkedDeque<>();
    private int animProgress = 0;

    public void setUniversalOffset(Vec3 universalOffset) {
        this.universalOffset = universalOffset;
    }

    private Vec3 universalOffset = new Vec3(1, 0, 0);

    public FlyingWeaponEntity(EntityType<? extends FlyingWeaponEntity> type, Level level) {
        super(type, level);
        setHeldItem(new ItemStack(Items.IRON_SWORD));
    }

    public Deque<Tuple<SwingHistory, SwingHistory>> getTrailHistory() {
        return trailHistory;
    }

    public boolean isIdle(){
        return moveQueue.isEmpty();
    }


    public void queuePath(MotionManager path) {
        internalIdleTimer=0;
        //if there was another return to idle animation, remove it first
        if (moveQueue.peekLast() instanceof MotionManagers.TransitionMM)
            moveQueue.removeLast();
        //grab the (new) end of move queue, or idle if we are currently idle
        MotionManager last = moveQueue.peekLast();
        if (last == null) last = idlePose;
        animProgress = 0;
        //add the transition in, actual move, and transition out
        moveQueue.add(new MotionManagers.TransitionMM(last, path, 8, EasingFunction.LINEAR));
        moveQueue.add(path);
        moveQueue.add(new MotionManagers.TransitionMM(path, idlePose, 8, EasingFunction.LINEAR));
        updateMotionTargets(false);
    }

    @Override
    public @Nullable UUID getOwnerUUID() {
        return entityData.get(DATA_OWNERUUID_ID).orElse(null);
    }

    private void setOwnerUUID(UUID to) {
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
                    if (e instanceof LivingEntity le)
                        owner = le;
            }
        } else if (owner == null && getOwnerEntityID() != 0) {
            //owned by a mob
            if (level().getEntity(getOwnerEntityID()) instanceof LivingEntity le)
                owner = le;
        }
        return owner;
    }

    public void setOwner(LivingEntity e) {
        if(e==null)return;
        if (e instanceof Player)
            setOwnerUUID(e.getUUID());
        else entityData.set(MOB_OWNER, e.getId());
        owner = e;
        if (e.getAttribute(ForgeMod.ENTITY_REACH.get()) != null)
            attackRange = e.getAttributeValue(ForgeMod.ENTITY_REACH.get());
    }

    private void updateMotionTargets(boolean forceskip) {
        LivingEntity owner = getOwner();
        if (owner == null) return;
        MotionManager motion = moveQueue.peek();
        if (motion == null || motion.hasEnded(animProgress)||forceskip) {
            moveQueue.poll();
            motion = moveQueue.peek();
            setIncorporeal(motion == null || motion instanceof MotionManagers.TransitionMM);
            alreadyHit.clear();
            animProgress = 0;
        }
    }

    public ItemStack getHeldItem() {
        return entityData.get(HELD);
    }

    public void setHeldItem(ItemStack stack) {
        entityData.set(HELD, stack);
    }

    public float getRoll() {
        return entityData.get(ID_ROLL);
    }

    public float getDisplacementForRender() {
        return 0;//entityData.get(ID_DISPLACE);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(ID_ROLL, 0f);
        this.entityData.define(ID_INTANGIBLE, false);
        this.entityData.define(LAST_FRAME, new MotionFrame(Vec3.ZERO, Vec3.ZERO));
        this.entityData.define(CURRENT_FRAME, new MotionFrame(Vec3.ZERO, Vec3.ZERO));
        this.entityData.define(HELD, ItemStack.EMPTY);
        this.entityData.define(DATA_OWNERUUID_ID, Optional.empty());
        this.entityData.define(MOB_OWNER, 0);
    }

    // Custom motion logic and collision in tick()
    @Override
    public void tick() {
        super.tick();
        setGlowingTag(false);
        // Movement logic
        if (level().isClientSide) {
            rollO = getRoll();
            displacementO = getDisplacementForRender();
            updateClientData();
            return;
        }
        if (getOwner() != null) {
            setOldPosAndRot();
            MotionFrame update;
            Vector4d recalculatedOrientation;

            if (!moveQueue.isEmpty()) {
                MotionManager activeMove = moveQueue.peek();
                //motion path test
                // Recalculate target every tick relative to player
                animProgress++;
                update = activeMove.getNextPoint(animProgress);
                Vec3 transformedDirection = update.resolveTargetOffset(owner, universalOffset, 1);

                // Move toward the desired position smoothly
                Vec3 delta = transformedDirection.subtract(this.position());

                this.setPos(transformedDirection);
                setDeltaMovement(delta);
                recalculatedOrientation = recalculateOrientation(owner, update.renderOrientation());

                if (activeMove.hasEnded(animProgress)) {//end current path, execute next path (either transition or next path)
                    updateMotionTargets(false);
                } else if (activeMove != idlePose) {
                    //still animating, keep frames on
                    //setIncorporeal(false);
                    displacementO = getDisplacementForRender();
                }
                //entityData.set(ID_DISPLACE, (float) Math.max(0, lerped.offset().length() * attackRange-1));

            } else {
                //idle animation, float next to the player
                update = idlePose.getNextPoint(0);
                Vec3 transformedDirection = update.resolveTargetOffset(owner, universalOffset, 1);

                // Move toward the desired position smoothly
                //todo lerp
                Vec3 currentPos=position();
                Vec3 lerp = currentPos.lerp(transformedDirection, 0.25);
                Vec3 delta = lerp.subtract(this.position());
                setPos(lerp);
                setDeltaMovement(delta);
                recalculatedOrientation = recalculateOrientation(owner, update.renderOrientation());

                //provisional. Makes the weapon perform a random move

                animProgress++;
                if (animProgress > 20) {
                    queuePath(EVERYONE.get(Footwork.rand.nextInt(EVERYONE.size())));
                    //setIncorporeal(false);
                    while (!trailHistory.isEmpty()) trailHistory.pop();
                }
                displacementO = getDisplacementForRender();
            }

            // Collision and attack logic
            if (!isIncorporeal()) {
                HitResult hit = level().clip(new ClipContext(getPosition(0).add(getViewVector(0).scale(attackRange)), getPosition(1).add(getViewVector(1).scale(attackRange)), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
                if (hit.getType() == HitResult.Type.BLOCK) {
                    BlockHitResult blockHit = (BlockHitResult) hit;
                    BlockPos blockPos = blockHit.getBlockPos();
                    Direction hitFace = blockHit.getDirection();
                    onHitBlock(blockPos, hitFace, blockHit.getLocation());
                }
                Vec3 sourcePoint = idlePose.getNextPoint(0).resolveTargetOffset(owner, universalOffset, 1);


                //List<Entity> targets = GeneralUtils.arcTraceEntities(level(), owner, getPosition(0), getPosition(1), attackRange, 1.2, tg -> tg != owner && !TargetingUtils.isAlly(tg, owner) && !tg.isInvulnerable());//level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(0.3));
                List<Entity> targets = GeneralUtils.arcTraceEntitiesOld(level(), sourcePoint, getPosition(0).add(getViewVector(0).scale(-attackRange)), getPosition(1).add(getViewVector(1).scale(-attackRange)), 1.4, attackRange, tg -> tg != owner && !TargetingUtils.isAlly(tg, owner) && !tg.isInvulnerable());//level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(0.3));
                GeneralUtils.attackTargetsWith(owner, targets, alreadyHit, getHeldItem(), Objects::nonNull);
            }

            // update client for trail rendering, done after block collision checks
            entityData.set(LAST_FRAME, entityData.get(CURRENT_FRAME));
            MotionFrame reconstructed = new MotionFrame(update.direction(), update.offset(), recalculatedOrientation);
            entityData.set(CURRENT_FRAME, reconstructed);

        }
//        else{
//            setOwner(level().getNearestPlayer(this, 16));
//        }
        internalIdleTimer++;
        if(internalIdleTimer>1200)//reasonably sure the player doesn't need it anymore
            remove(RemovalReason.DISCARDED);
    }

    private void updateClientData() {
        renderLagO=renderLag;
        if (!isIncorporeal()) {
            if (renderLag < 2)
                renderLag++;
        } else if (renderLag > 0) renderLag--;
        //lerp 5 points between each tick
        if (getOwner() != null)
            for (int i = 0; i < CLIENT_SMOOTHING_SUBTICKS; i++) {
                MotionFrame from = entityData.get(LAST_FRAME);
                MotionFrame to = entityData.get(CURRENT_FRAME);
                MotionFrame lerped = from.lerp(to, (double) i / CLIENT_SMOOTHING_SUBTICKS);
                Vec3 trailPosition = lerped.resolveTargetOffset(owner, universalOffset, attackRange);
                SwingHistory trail = new SwingHistory(trailPosition, (float) lerped.renderOrientation().z, (float) lerped.renderOrientation().y, (float) lerped.renderOrientation().x);
                Vec3 shadowPosition = lerped.resolveTargetOffset(owner, universalOffset, 1);
                SwingHistory shadow = new SwingHistory(shadowPosition, (float) lerped.renderOrientation().z, (float) lerped.renderOrientation().y, (float) lerped.renderOrientation().x);
                trailHistory.addFirst(new Tuple<>(trail, shadow));
                //fixme why tf do I need to flip pitch and yaw???
            }
        while (trailHistory.size() > MAX_TRAIL_LENGTH * CLIENT_SMOOTHING_SUBTICKS) {
            trailHistory.removeLast();
        }
    }

    private void onHitBlock(BlockPos blockPos, Direction hitFace, Vec3 location) {
        //setPos(location);
    }

    public Vector4d recalculateOrientation(LivingEntity owner, Vector4d quaternion) {
        // Step 1: Convert visualOrientation to world-space direction
        // This assumes visualOrientation is like a local-space forward vector (e.g., (0, 0, 1))

        // Get player's rotation as a basis
        Vec3 forward = owner.getLookAngle().normalize();
        if (forward.lengthSqr() < 0.0001) forward = new Vec3(0, 0, 1); // fallback

        // Create right and up basis vectors
        Vec3 globalUp = new Vec3(0, 1, 0);
        Vec3 right = forward.cross(globalUp).normalize();
        Vec3 up = right.cross(forward).normalize();  // Ensure orthogonal
        Vec3 worldDirection = right.scale(quaternion.x).add(up.scale(quaternion.y)).add(forward.scale(quaternion.z)).normalize();

        // Step 2: Face that world direction
        float targetYaw = (float) (Mth.atan2(worldDirection.x, worldDirection.z) * (180F / Math.PI)) % 180f;
        float targetPitch = (float) (-Mth.atan2(worldDirection.y, Math.sqrt(worldDirection.x * worldDirection.x + worldDirection.z * worldDirection.z)) * (180F / Math.PI)) % 180f;

        rollO = entityData.get(ID_ROLL);
        float lerpX = Mth.lerp(0.25f, getXRot(), targetPitch);
        float lerpY = Mth.lerp(0.25f, getYRot(), targetYaw);
        double lerpZ = Mth.lerp(0.25f, rollO, quaternion.w);
        if(!Float.isFinite(lerpX)){
            Footwork.LOGGER.warn("x is somehow not finite, resetting");
            lerpX=targetPitch;
        }
        if(!Float.isFinite(lerpY)){
            Footwork.LOGGER.warn("y is somehow not finite, resetting");
            lerpY=targetYaw;
        }
        if(!Double.isFinite(lerpZ)){
            Footwork.LOGGER.warn("z is somehow not finite, resetting");
            lerpZ=quaternion.w;
        }
        //fixme when facing north this will cause the weapon to speeeen
        setYRot(lerpY);
        setXRot(lerpX);
        entityData.set(ID_ROLL, (float) lerpZ);
        return new Vector4d(lerpX, lerpZ, lerpY, 0);
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


    public boolean isIncorporeal() {
        return entityData.get(ID_INTANGIBLE);
    }

    public void setIncorporeal(boolean incorporeal) {
        entityData.set(ID_INTANGIBLE, incorporeal);
    }
}
