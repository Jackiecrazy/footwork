package jackiecrazy.footwork.entity.flyingweapon;

import jackiecrazy.footwork.Footwork;
import jackiecrazy.footwork.move.motionframe.MotionFrame;
import jackiecrazy.footwork.move.motionframe.MotionManager;
import jackiecrazy.footwork.utils.GeneralUtils;
import jackiecrazy.footwork.utils.TargetingUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.monster.Husk;
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

public class FlyingWeaponEntity extends Entity implements OwnableEntity {
    public static final int MAX_TRAIL_LENGTH = 20;
    private static final List<MotionFrame> TWIST_THE_KNIFE = List.of(new MotionFrame(new Vec3(1, -1, 1), new Vec3(0, 0, 1), 135), new MotionFrame(new Vec3(0, 0, 1), new Vec3(0, 0, 0), 135), new MotionFrame(new Vec3(0, 0, 1), new Vec3(0, 0, 1.3)), new MotionFrame(new Vec3(1, -1, 1), new Vec3(0, 0, 1), -60));
    private static final List<MotionFrame> STAB = List.of(
            new MotionFrame(new Vec3(0, 0, 1), new Vec3(0, 0, 0)),
            new MotionFrame(new Vec3(0, 0, 1), new Vec3(0, 0, 1))
    );
    private static final List<MotionFrame> CIRCLE = List.of(
            new MotionFrame(new Vec3(0, 0, 1), new Vec3(0, 0, 1), new Vector4d(0, 0, 1, 90)),
            new MotionFrame(new Vec3(-1, 0, 0), new Vec3(0, 0, 1), new Vector4d(-1, 0, 0, 90)),
            new MotionFrame(new Vec3(0, 0, -1), new Vec3(0, 0, 1), new Vector4d(0, 0, -1, 90)),
            new MotionFrame(new Vec3(1, 0, 0), new Vec3(0, 0, 1), new Vector4d(1, 0, 0, 90)),
            new MotionFrame(new Vec3(0, 0, 1), new Vec3(0, 0, 1), new Vector4d(0, 0, 1, 90)));
    private static final List<MotionFrame> SLASH = List.of(new MotionFrame(new Vec3(1, 0.6, 1), new Vec3(0, 0, 1)), new MotionFrame(new Vec3(-1, -0.4, 0), new Vec3(0, 0, 1), new Vector4d(-1, -1, 1, 45)));
    private static final List<MotionFrame> BACKSLASH = List.of(new MotionFrame(new Vec3(-1, 0.6, 1), new Vec3(0, 0, 1), -45), new MotionFrame(new Vec3(1, -0.4, 0), new Vec3(0, 0, 1), new Vector4d(1, -1, 1, -45)));
    private static final List<MotionFrame> CHOP = List.of(new MotionFrame(new Vec3(0, 1, 0.2), new Vec3(0, 0, 1)), new MotionFrame(new Vec3(0, -0.5, 1), new Vec3(0, 0, 1)));
    private static final List<List<MotionFrame>> EVERYONE = List.of(CIRCLE, STAB, SLASH, BACKSLASH, CHOP);
    private static final double FRAMEPERTICK = 0.14;
    private static final EntityDataAccessor<Float> ID_ROLL = SynchedEntityData.defineId(FlyingWeaponEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> ID_INTANGIBLE = SynchedEntityData.defineId(FlyingWeaponEntity.class, EntityDataSerializers.BOOLEAN);
    private final List<MotionFrame> motionPath = new ArrayList<>();
    //how the weapon floats when idle: point downwards
    private final MotionFrame idlePose = new MotionFrame(new Vec3(0, -1, 0), Vec3.ZERO, new Vector4d(0, 0, 0, 60));
    private final List<Entity> alreadyHit = new ArrayList<>();
    private final Deque<SwingHistory> history = new ArrayDeque<>();
    public float rollO, displacementO;
    public double attackRange = 3;
    public int renderLag = 0;
    private ItemStack heldItem;
    private LivingEntity owner;
    private UUID ownerID;
    private int currentTargetIndex = 0;
    private double frameDuration = 0;
    private MotionManager motionManager;
    private int animProgress = 0;
    private Vec3 universalOffset = new Vec3(1, 0, 0);

    public FlyingWeaponEntity(EntityType<? extends FlyingWeaponEntity> type, Level level) {
        super(type, level);
        ItemStack stack = new ItemStack(Items.IRON_AXE);
        stack.enchant(Enchantments.ALL_DAMAGE_PROTECTION, 1);
        this.heldItem = stack;
        setMotionPath(TWIST_THE_KNIFE);
    }

    public Deque<SwingHistory> getHistory() {
        return history;
    }

    public void setMotionPath(List<MotionFrame> path) {
        this.motionPath.clear();
        this.motionPath.addAll(path);
        this.currentTargetIndex = 0;
        updateMotionTargets();
    }

    @Override
    public @Nullable UUID getOwnerUUID() {
        return ownerID;
    }

    public LivingEntity getOwner() {
        if (owner == null && ownerID != null) {
            owner = level().getPlayerByUUID(ownerID);
        }
        return owner;
    }

    public void setOwner(LivingEntity e) {
        ownerID = e.getUUID();
        owner = e;
        if (e.getAttribute(ForgeMod.ENTITY_REACH.get()) != null)
            attackRange = e.getAttributeValue(ForgeMod.ENTITY_REACH.get());
    }

    private void updateMotionTargets() {
        currentTargetIndex %= motionPath.size();
        alreadyHit.clear();
        MotionFrame next = motionPath.get(currentTargetIndex);
        LivingEntity owner = getOwner();
        if (owner == null) return;
        previousFrame = nextFrame;
        nextFrame = next;
        frameDuration = 0;
        animProgress = 0;
    }

    public ItemStack getHeldItem() {
        return heldItem;
    }

    public void setHeldItem(ItemStack stack) {
        this.heldItem = stack;
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
    }

    // Custom motion logic and collision in tick()
    @Override
    public void tick() {
        super.tick();
        setGlowingTag(true);
        // Movement logic
        if (level().isClientSide) {
            rollO = getRoll();
            displacementO = getDisplacementForRender();
            updateClientData();
            return;
        }
        if (getOwner() != null) {
            setOldPosAndRot();

            //idle animation, float next to the player
            /*setRot(owner.yHeadRot, owner.getXRot());
            double rightOffset = 2.0;  // 1 block to the right
            double verticalOffset = 1.0; // roughly hand height

            // Player's facing direction (horizontal)
            float bodyYaw = owner.getYRot() + 80;
            double angleRad = Math.toRadians(bodyYaw);

            // Scales the offset outwards
            double offsetX = -Math.sin(angleRad) * rightOffset;
            double offsetZ = Math.cos(angleRad) * rightOffset;

            double x = owner.getX() + offsetX;
            double y = owner.getY() + verticalOffset;
            double z = owner.getZ() + offsetZ;

            Vec3 current = position();
            Vec3 toward = new Vec3(x, y, z);
            Vec3 smoothed = current.lerp(toward, 0.25);

            setPos(smoothed);*/

            if (motionManager != null) {

                //motion path test
                // Recalculate target every tick relative to player
                animProgress++;
                MotionFrame next = motionManager.getNextPoint(animProgress);
                Vec3 desiredPosition = next.resolveTargetOffset(owner, universalOffset, attackRange);

                // Move toward the desired position smoothly
                Vec3 delta = desiredPosition.subtract(this.position());

                this.setPos(desiredPosition);

                //finished frame, move to the next target frame
                if (frameDuration >= 1) {
                    currentTargetIndex++;
                    if (currentTargetIndex >= motionPath.size()) {
                        //end current path, return to neutral
                        currentTargetIndex = 0;
                        frameDuration = 0;
                        if (!isIncorporeal()) {
                            setIncorporeal(true);
                            setMotionPath(List.of(idlePose));
                            //while (!history.isEmpty()) history.pop();
                        } else {
                            setMotionPath(EVERYONE.get(random.nextInt(EVERYONE.size())));
                            setIncorporeal(false);
                        }
                    } else {
                        setIncorporeal(false);
                        updateMotionTargets();
                    }
                }
                displacementO = getDisplacementForRender();
                //entityData.set(ID_DISPLACE, (float) Math.max(0, lerped.offset().length() * attackRange-1));
                setDeltaMovement(delta);
                recalculateOrientation(owner, lerped.renderOrientation());

            } else updateMotionTargets();

            // Collision and attack logic
            if (!isIncorporeal()) {
                HitResult hit = level().clip(new ClipContext(getPosition(0), getPosition(1), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
                if (hit.getType() == HitResult.Type.BLOCK) {
                    BlockHitResult blockHit = (BlockHitResult) hit;
                    BlockPos blockPos = blockHit.getBlockPos();
                    Direction hitFace = blockHit.getDirection();
                    onHitBlock(blockPos, hitFace, blockHit.getLocation());
                }
                Vec3 sourcePoint = idlePose.resolveTargetOffset(owner, universalOffset, attackRange);


                //List<Entity> targets = GeneralUtils.arcTraceEntities(level(), owner, getPosition(0), getPosition(1), attackRange, 1.2, tg -> tg != owner && !TargetingUtils.isAlly(tg, owner) && !tg.isInvulnerable());//level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(0.3));
                List<Entity> targets = GeneralUtils.arcTraceEntitiesOld(level(), sourcePoint, getPosition(0), getPosition(1), 1.4, attackRange, tg -> tg != owner && !TargetingUtils.isAlly(tg, owner) && !tg.isInvulnerable());//level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(0.3));
                GeneralUtils.attackTargetsWith(owner, targets, alreadyHit, getHeldItem(), Objects::nonNull);
            }

        } else {
            level().getEntities(this, this.getBoundingBox().inflate(10), a -> a instanceof Husk).forEach(a -> {
                if (a instanceof Husk h) setOwner(h);
            });
            //owner = level().getNearestPlayer(this, 16);
        }


    }

    private void updateClientData() {
        if (!isIncorporeal()) {
            if (renderLag < 4)
                renderLag++;
        } else if (renderLag > 0) renderLag--;
        history.addFirst(new SwingHistory(position(), getYRot(), getRoll(), getXRot()));
        while (history.size() > MAX_TRAIL_LENGTH) {
            history.removeLast();
        }
    }

    private void onHitBlock(BlockPos blockPos, Direction hitFace, Vec3 location) {
        Footwork.LOGGER.debug("hit " + blockPos.toString());
        setPos(location);
    }

    public void recalculateOrientation(LivingEntity owner, Vector4d quaternion) {
        // Step 1: Convert visualOrientation to world-space direction
        // This assumes visualOrientation is like a local-space forward vector (e.g., (0, 0, 1))

        // Get player's rotation as a basis
        float yaw = owner.getYHeadRot();   // Horizontal
        float pitch = owner.getXRot();     // Vertical

        Vec3 visualOrientation = new Vec3(quaternion.x, quaternion.y, quaternion.z);

        // Rotate the local vector by player's rotation
        Vec3 worldDirection = visualOrientation.xRot((float) -Math.toRadians(pitch))  // Apply pitch first (X-axis)
                .yRot((float) -Math.toRadians(yaw));   // Then yaw (Y-axis)

        // Step 2: Face that world direction
        float targetYaw = (float) (Mth.atan2(worldDirection.x, worldDirection.z) * (180F / Math.PI));
        float targetPitch = (float) (-Mth.atan2(worldDirection.y, Math.sqrt(worldDirection.x * worldDirection.x + worldDirection.z * worldDirection.z)) * (180F / Math.PI));

        setYRot(targetYaw);
        setXRot(targetPitch);
        rollO = entityData.get(ID_ROLL);
        entityData.set(ID_ROLL, (float) quaternion.w);
    }


    // Save/load NBT
    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if (tag.contains("Item")) {
            this.heldItem = ItemStack.of(tag.getCompound("Item"));
        }
        if (tag.contains("ownerUUID")) {
            ownerID = tag.getUUID("ownerUUID");
            owner = getOwner();
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        if (!this.heldItem.isEmpty()) tag.put("Item", this.heldItem.save(new CompoundTag()));
        if (ownerID != null) tag.putUUID("ownerUUID", ownerID);
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
