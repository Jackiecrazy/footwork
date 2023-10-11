//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package jackiecrazy.footwork.utils;

import jackiecrazy.footwork.capability.resources.CombatData;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.InteractionHand;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class GeneralUtils {
    public static double getSpeedSq(Entity e) {
        if (e.getVehicle() != null)
            if (e.getRootVehicle() instanceof LivingEntity)
                return CombatData.getCap((LivingEntity) e.getRootVehicle()).getMotionConsistently().lengthSqr();
            else return e.getRootVehicle().getDeltaMovement().lengthSqr();
        if (e instanceof LivingEntity)
            return Math.max(CombatData.getCap((LivingEntity) e).getMotionConsistently().lengthSqr(), e.getDeltaMovement().lengthSqr());
        return e.getDeltaMovement().lengthSqr();
    }

    @Nullable
    public static EntityType getEntityTypeFromResourceLocation(ResourceLocation rl) {
        if (ForgeRegistries.ENTITY_TYPES.containsKey(rl))
            return ForgeRegistries.ENTITY_TYPES.getValue(rl);
        return null;
    }

    @Nullable
    public static ResourceLocation getResourceLocationFromEntityType(EntityType et) {
        if (ForgeRegistries.ENTITY_TYPES.containsValue(et))
            return ForgeRegistries.ENTITY_TYPES.getKey(et);
        return null;
    }

    @Nullable
    public static ResourceLocation getResourceLocationFromEntity(Entity et) {
        final EntityType<?> type = et.getType();
        if (ForgeRegistries.ENTITY_TYPES.containsValue(type)) {
            final ResourceLocation key = ForgeRegistries.ENTITY_TYPES.getKey(type);
            return key;
        }
        return null;
    }

    @Nonnull
    public static HitResult raytraceAnything(Level world, LivingEntity attacker, double range) {
        Vec3 start = attacker.getEyePosition(0.5f);
        Vec3 look = attacker.getLookAngle().scale(range + 2);
        Vec3 end = start.add(look);
        Entity entity = null;
        List<Entity> list = world.getEntities(attacker, attacker.getBoundingBox().expandTowards(look.x, look.y, look.z).inflate(1.0D), EntitySelector.ENTITY_STILL_ALIVE);
        double d0 = 0.0D;

        for (Entity entity1 : list) {
            if (entity1 != attacker) {
                AABB axisalignedbb = entity1.getBoundingBox();
                Optional<Vec3> raytraceresult = axisalignedbb.clip(start, end);
                if (raytraceresult.isPresent()) {
                    double d1 = getDistSqCompensated(entity1, attacker);

                    if ((d1 < d0 || d0 == 0.0D) && d1 < range * range) {
                        entity = entity1;
                        d0 = d1;
                    }
                }
            }
        }
        if (entity != null) return new EntityHitResult(entity);
        look = attacker.getLookAngle().scale(range);
        end = start.add(look);
        HitResult rtr = world.clip(new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, null));
        if (rtr != null) {
            return rtr;
        }
        return BlockHitResult.miss(end, Direction.UP, BlockPos.containing(end.x, end.y, end.z));
    }

    /**
     * modified getdistancesq to account for thicc mobs
     */
    public static double getDistSqCompensated(Entity from, Entity to) {
        double x = from.getX() - to.getX();
        x = Math.max(Math.abs(x) - ((from.getBbWidth() / 2) + (to.getBbWidth() / 2)), 0);
        //stupid inconsistent game
        double y = (from.getY() + from.getBbHeight() / 2) - (to.getY() + to.getBbHeight() / 2);
        y = Math.max(Math.abs(y) - (from.getBbHeight() / 2 + to.getBbHeight() / 2), 0);
        double z = from.getZ() - to.getZ();
        z = Math.max(Math.abs(z) - (from.getBbWidth() / 2 + to.getBbWidth() / 2), 0);
        double me = x * x + y * y + z * z;
        double you = from.distanceToSqr(to);
        return Math.min(me, you);
    }

    public static float getMaxHealthBeforeWounding(LivingEntity of) {
        return of.getMaxHealth();
    }

    /**
     * modified getdistancesq to account for thicc mobs
     */
    public static double getDistSqCompensated(Entity from, Vec3 to) {
        double x = from.getX() - to.x;
        x = Math.max(Math.abs(x) - ((from.getBbWidth() / 2)), 0);
        //stupid inconsistent game
        double y = (from.getY() + from.getBbHeight() / 2) - (to.y);
        y = Math.max(Math.abs(y) - (from.getBbHeight() / 2), 0);
        double z = from.getZ() - to.z;
        z = Math.max(Math.abs(z) - (from.getBbWidth() / 2), 0);
        return x * x + y * y + z * z;
    }

    /**
     * modified getdistancesq to account for thicc mobs
     */
    public static double getDistSqCompensated(Entity from, BlockPos to) {
        double x = from.getX() - to.getX();
        x = Math.max(Math.abs(x) - ((from.getBbWidth() / 2)), 0);
        //stupid inconsistent game
        double y = (from.getY() + from.getBbHeight() / 2) - (to.getY());
        y = Math.max(Math.abs(y) - (from.getBbHeight() / 2), 0);
        double z = from.getZ() - to.getZ();
        z = Math.max(Math.abs(z) - (from.getBbWidth() / 2), 0);
        return x * x + y * y + z * z;
    }

    public static Entity raytraceEntity(Level world, LivingEntity attacker, double range) {
        Vec3 start = attacker.getEyePosition(0.5f);
        Vec3 look = attacker.getLookAngle().scale(range + 2);
        Vec3 end = start.add(look);
        Entity entity = null;
        List<Entity> list = world.getEntities(attacker, attacker.getBoundingBox().expandTowards(look.x, look.y, look.z).inflate(1.0D), EntitySelector.LIVING_ENTITY_STILL_ALIVE);
        double d0 = -1.0D;//necessary to prevent small derps

        for (Entity entity1 : list) {
            if (entity1 != attacker) {
                AABB axisalignedbb = entity1.getBoundingBox();
                Optional<Vec3> raytraceresult = axisalignedbb.clip(start, end);
                if (raytraceresult.isPresent()) {
                    double d1 = getDistSqCompensated(entity1, attacker);

                    if ((d1 < d0 || d0 == -1.0D) && d1 < range * range) {
                        entity = entity1;
                        d0 = d1;
                    }
                }
            }
        }
        return entity;
    }

    public static LivingEntity raytraceLiving(LivingEntity attacker, double range) {
        return raytraceLiving(attacker.level(), attacker, range);
    }

    public static LivingEntity raytraceLiving(Level world, LivingEntity attacker, double range) {
        Vec3 start = attacker.getEyePosition(0.5f);
        Vec3 look = attacker.getLookAngle().scale(range + 2);
        Vec3 end = start.add(look);
        LivingEntity entity = null;
        List<LivingEntity> list = world.getEntitiesOfClass(LivingEntity.class, attacker.getBoundingBox().expandTowards(look.x, look.y, look.z).inflate(1.5D), EntitySelector.LIVING_ENTITY_STILL_ALIVE);
        double d0 = -1.0D;//necessary to prevent small derps

        for (LivingEntity entity1 : list) {
            if (entity1 != attacker) {
                AABB axisalignedbb = entity1.getBoundingBox();
                Optional<Vec3> raytraceresult = axisalignedbb.clip(start, end);
                if (raytraceresult.isPresent()) {
                    double d1 = getDistSqCompensated(entity1, attacker);

                    if ((d1 < d0 || d0 == -1.0D) && d1 < range * range) {
                        entity = entity1;
                        d0 = d1;
                    }
                }
            }
        }
        return entity;
    }

    /**
     * Checks the +x, -x, +y, -y, +z, -z, in that order
     *
     * @param elb
     * @return
     */
    public static Entity collidingEntity(Entity elb) {
        AABB aabb = elb.getBoundingBox();
        Vec3 motion = elb.getDeltaMovement().normalize().scale(0.5);
        List<Entity> entities = elb.level().getEntities(elb, aabb.expandTowards(motion.x, motion.y, motion.z), EntitySelector.ENTITY_STILL_ALIVE);
        double dist = 0;
        Entity pick = null;
        for (Entity e : entities) {
            if (e.distanceToSqr(elb) < dist || dist == 0) {
                pick = e;
                dist = e.distanceToSqr(elb);
            }
        }
        return pick;
    }

    public static List<Entity> raytraceEntities(Level world, LivingEntity attacker, double range) {
        Vec3 start = attacker.getEyePosition(0.5f);
        Vec3 look = attacker.getLookAngle().scale(range + 2);
        Vec3 end = start.add(look);
        ArrayList<Entity> ret = new ArrayList<>();
        List<Entity> list = world.getEntities(attacker, attacker.getBoundingBox().expandTowards(look.x, look.y, look.z).inflate(1.0D), EntitySelector.ENTITY_STILL_ALIVE);

        for (Entity entity1 : list) {
            if (entity1 != attacker && getDistSqCompensated(attacker, entity1) < range * range) {
                AABB axisalignedbb = entity1.getBoundingBox();
                Optional<Vec3> raytraceresult = axisalignedbb.clip(start, end);
                if (raytraceresult.isPresent()) {
                    ret.add(entity1);
                }
            }
        }
        return ret;
    }

    public static Vec3 getPointInFrontOf(Entity target, Entity from, double distance) {
        Vec3 end = target.position().add(from.position().subtract(target.position()).normalize().scale(distance));
        return getClosestAirSpot(from.position(), end, from);
    }

    /**
     * returns the coordinate closest to the end point of the vector that fits the entity
     * From and To should be from the feet and at the center, respectively.
     * After that, it performs 4 ray casts: one from the bottom, one at the top, and two at the sides.
     * Two sides are omitted if you're 1 block wide or less, another ray cast is done for every block of height you have
     * So a player will be casted 3 times: once at the foot, once at the midriff, and once at the head
     * The closest RayTraceResult will be used, with compensation if it didn't hit the top of a block
     */
    public static Vec3 getClosestAirSpot(Vec3 from, Vec3 to, Entity e) {
        Vec3 ret = to;
        //extend the to vector slightly to make it hit what it originally hit
        to = to.add(to.subtract(from).normalize().scale(2));
        double widthParse = e.getBbWidth() / 2;
        double heightParse = e.getBbHeight();
        if (widthParse <= 0.5) widthParse = 0;
        if (heightParse <= 1) heightParse = 0;
        for (double addX = -widthParse; addX <= widthParse; addX += 0.5) {
            for (double addZ = -widthParse; addZ <= widthParse; addZ += 0.5) {
                for (double addY = e.getBbHeight() / 2; addY <= heightParse; addY += 0.5) {
                    Vec3 mod = new Vec3(addX, addY, addZ);
                    BlockHitResult r = e.level().clip(new ClipContext(from.add(mod), to.add(mod), ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY, e));
                    if (r != null && r.getType() == HitResult.Type.BLOCK && !r.getLocation().equals(from.add(mod))) {
                        Vec3 hit = r.getLocation().subtract(mod);
                        switch (r.getDirection()) {
                            case NORTH:
                                hit = hit.add(0, 0, -1);
                                break;
                            case SOUTH:
                                hit = hit.add(0, 0, 1);
                                break;
                            case EAST:
                                hit = hit.add(1, 0, 0);
                                break;
                            case WEST:
                                hit = hit.add(-1, 0, 0);
                                break;
                            case UP:
                                //hit.add(0, -1, 0); //apparently unnecessary.
                                break;
                            case DOWN:
                                hit = hit.add(0, -1, 0);
                                break;
                        }
                        if (from.distanceToSqr(hit) < from.distanceToSqr(ret)) {
                            ret = hit;
                        }
                    }
                }
            }
        }
        return ret;
    }

    /**
     * @author Suff/Swirtzly
     */
    public static boolean viewBlocked(LivingEntity viewer, LivingEntity viewed, boolean flimsy) {
        if (viewer.distanceToSqr(viewed) > 1000) return true;//what
        AABB viewerBoundBox = viewer.getBoundingBox();
        AABB angelBoundingBox = viewed.getBoundingBox();
        Vec3[] viewerPoints = {new Vec3(viewerBoundBox.minX, viewerBoundBox.minY, viewerBoundBox.minZ),
                new Vec3(viewerBoundBox.minX, viewerBoundBox.minY, viewerBoundBox.maxZ),
                new Vec3(viewerBoundBox.minX, viewerBoundBox.maxY, viewerBoundBox.minZ),
                new Vec3(viewerBoundBox.minX, viewerBoundBox.maxY, viewerBoundBox.maxZ),
                new Vec3(viewerBoundBox.maxX, viewerBoundBox.maxY, viewerBoundBox.minZ),
                new Vec3(viewerBoundBox.maxX, viewerBoundBox.maxY, viewerBoundBox.maxZ),
                new Vec3(viewerBoundBox.maxX, viewerBoundBox.minY, viewerBoundBox.maxZ),
                new Vec3(viewerBoundBox.maxX, viewerBoundBox.minY, viewerBoundBox.minZ),};

        Vec3[] angelPoints = {new Vec3(angelBoundingBox.minX, angelBoundingBox.minY, angelBoundingBox.minZ),
                new Vec3(angelBoundingBox.minX, angelBoundingBox.minY, angelBoundingBox.maxZ),
                new Vec3(angelBoundingBox.minX, angelBoundingBox.maxY, angelBoundingBox.minZ),
                new Vec3(angelBoundingBox.minX, angelBoundingBox.maxY, angelBoundingBox.maxZ),
                new Vec3(angelBoundingBox.maxX, angelBoundingBox.maxY, angelBoundingBox.minZ),
                new Vec3(angelBoundingBox.maxX, angelBoundingBox.maxY, angelBoundingBox.maxZ),
                new Vec3(angelBoundingBox.maxX, angelBoundingBox.minY, angelBoundingBox.maxZ),
                new Vec3(angelBoundingBox.maxX, angelBoundingBox.minY, angelBoundingBox.minZ),};

        for (int i = 0; i < viewerPoints.length; i++) {
            if (viewer.level().clip(new ClipContext(viewerPoints[i], angelPoints[i], flimsy ? ClipContext.Block.OUTLINE : ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, viewer)).getType() == HitResult.Type.MISS) {
                return false;
            }
            if (rayTraceBlocks(viewer, viewer.level(), viewerPoints[i], angelPoints[i], pos -> {
                BlockState state = viewer.level().getBlockState(pos);
                return flimsy ? !state.liquid() : !canSeeThrough(state, viewer.level(), pos);
            }) == null) return false;
        }

        return true;
    }

    /**
     * @author Suff/Swirtzly
     */
    @Nullable
    private static HitResult rayTraceBlocks(LivingEntity livingEntity, Level world, Vec3 vec31, Vec3 vec32, Predicate<BlockPos> stopOn) {
        if (!Double.isNaN(vec31.x) && !Double.isNaN(vec31.y) && !Double.isNaN(vec31.z)) {
            if (!Double.isNaN(vec32.x) && !Double.isNaN(vec32.y) && !Double.isNaN(vec32.z)) {
                int i = Mth.floor(vec32.x);
                int j = Mth.floor(vec32.y);
                int k = Mth.floor(vec32.z);
                int l = Mth.floor(vec31.x);
                int i1 = Mth.floor(vec31.y);
                int j1 = Mth.floor(vec31.z);
                BlockPos blockpos = new BlockPos(l, i1, j1);
                if (stopOn.test(blockpos)) {
                    HitResult raytraceresult = world.clip(new ClipContext(vec31, vec32, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, livingEntity));
                    if (raytraceresult != null) {
                        return raytraceresult;
                    }
                }

                int k1 = 200;

                while (k1-- >= 0) {
                    if (Double.isNaN(vec31.x) || Double.isNaN(vec31.y) || Double.isNaN(vec31.z)) {
                        return null;
                    }

                    if (l == i && i1 == j && j1 == k) {
                        return null;
                    }

                    boolean flag2 = true;
                    boolean flag = true;
                    boolean flag1 = true;
                    double d0 = 999.0D;
                    double d1 = 999.0D;
                    double d2 = 999.0D;

                    if (i > l) {
                        d0 = (double) l + 1.0D;
                    } else if (i < l) {
                        d0 = (double) l + 0.0D;
                    } else {
                        flag2 = false;
                    }

                    if (j > i1) {
                        d1 = (double) i1 + 1.0D;
                    } else if (j < i1) {
                        d1 = (double) i1 + 0.0D;
                    } else {
                        flag = false;
                    }

                    if (k > j1) {
                        d2 = (double) j1 + 1.0D;
                    } else if (k < j1) {
                        d2 = (double) j1 + 0.0D;
                    } else {
                        flag1 = false;
                    }

                    double d3 = 999.0D;
                    double d4 = 999.0D;
                    double d5 = 999.0D;
                    double d6 = vec32.x - vec31.x;
                    double d7 = vec32.y - vec31.y;
                    double d8 = vec32.z - vec31.z;

                    if (flag2) {
                        d3 = (d0 - vec31.x) / d6;
                    }

                    if (flag) {
                        d4 = (d1 - vec31.y) / d7;
                    }

                    if (flag1) {
                        d5 = (d2 - vec31.z) / d8;
                    }

                    if (d3 == -0.0D) {
                        d3 = -1.0E-4D;
                    }

                    if (d4 == -0.0D) {
                        d4 = -1.0E-4D;
                    }

                    if (d5 == -0.0D) {
                        d5 = -1.0E-4D;
                    }

                    Direction enumfacing;

                    if (d3 < d4 && d3 < d5) {
                        enumfacing = i > l ? Direction.WEST : Direction.EAST;
                        vec31 = new Vec3(d0, vec31.y + d7 * d3, vec31.z + d8 * d3);
                    } else if (d4 < d5) {
                        enumfacing = j > i1 ? Direction.DOWN : Direction.UP;
                        vec31 = new Vec3(vec31.x + d6 * d4, d1, vec31.z + d8 * d4);
                    } else {
                        enumfacing = k > j1 ? Direction.NORTH : Direction.SOUTH;
                        vec31 = new Vec3(vec31.x + d6 * d5, vec31.y + d7 * d5, d2);
                    }

                    l = Mth.floor(vec31.x) - (enumfacing == Direction.EAST ? 1 : 0);
                    i1 = Mth.floor(vec31.y) - (enumfacing == Direction.UP ? 1 : 0);
                    j1 = Mth.floor(vec31.z) - (enumfacing == Direction.SOUTH ? 1 : 0);
                    blockpos = new BlockPos(l, i1, j1);
                    if (stopOn.test(blockpos)) {
                        HitResult raytraceresult1 = world.clip(new ClipContext(vec31, vec32, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, livingEntity));

                        if (raytraceresult1 != null) {
                            return raytraceresult1;
                        }
                    }
                }
            }
        }

        return null;
    }

    /**
     * @author Suff/Swirtzly
     */
    public static boolean canSeeThrough(BlockState blockState, Level world, BlockPos pos) {

        // Covers all Block, Material and Tag checks :D
        if (!blockState.canOcclude() || !blockState.isSolidRender(world, pos)) {
            return true;
        }

        Block block = blockState.getBlock();

        // Special Snowflakes
        if (block instanceof DoorBlock) {
            return blockState.getValue(DoorBlock.HALF) == DoubleBlockHalf.UPPER;
        }

        return blockState.getCollisionShape(world, pos) == Shapes.empty();
    }

    /**
     * returns the BlockPos at the center of an AABB
     */
    public static BlockPos posFromAABB(AABB aabb) {
        return new BlockPos((int) ((aabb.maxX + aabb.minX) / 2), (int) ((aabb.maxY + aabb.minY) / 2), (int) ((aabb.maxZ + aabb.minZ) / 2));
    }


    /**
     * returns true if entity2 is within a (angle) degree sector in front of entity1
     */
    public static boolean isFacingEntity(Entity entity1, Entity entity2, int angle) {
        if (angle >= 360) return true;//well, duh.
        if (angle < 0) return isBehindEntity(entity2, entity1, -angle);
        Vec3 posVec = entity2.position().add(0, entity2.getEyeHeight(), 0);
        Vec3 lookVec = entity1.getViewVector(1.0F);
        Vec3 relativePosVec = posVec.vectorTo(entity1.position().add(0, entity1.getEyeHeight(), 0)).normalize();
        //relativePosVec = new Vector3d(relativePosVec.x, 0.0D, relativePosVec.z);

        double dotsq = ((relativePosVec.dot(lookVec) * Math.abs(relativePosVec.dot(lookVec))) / (relativePosVec.lengthSqr() * lookVec.lengthSqr()));
        double cos = Mth.cos(rad(angle / 2f));
        return dotsq < -(cos * cos);
    }

    /**
     * returns true if entity is within a 90 degree sector behind the reference
     */
    public static boolean isBehindEntity(Entity entity, Entity reference, int angle) {
        if (angle >= 360) return true;//well, duh.
        Vec3 posVec = entity.position().add(0, entity.getEyeHeight(), 0);
        Vec3 lookVec = getBodyOrientation(reference);
        Vec3 relativePosVec = posVec.vectorTo(reference.position().add(0, reference.getEyeHeight(), 0)).normalize();
        relativePosVec = new Vec3(relativePosVec.x, 0.0D, relativePosVec.z);
        double dotsq = ((relativePosVec.dot(lookVec) * Math.abs(relativePosVec.dot(lookVec))) / (relativePosVec.lengthSqr() * lookVec.lengthSqr()));
        double cos = Mth.cos(rad(angle / 2f));
        return dotsq > cos * cos;
    }

    public static float rad(float angle) {
        return (float) (angle * Math.PI / 180d);
    }

    /**
     * literally a copy-paste of {@link Entity#getLookAngle()} ()} for {@link LivingEntity}, since they calculate from their head instead
     */
    public static Vec3 getBodyOrientation(Entity e) {
        float f = Mth.cos(-e.getYRot() * 0.017453292F - (float) Math.PI);
        float f1 = Mth.sin(-e.getYRot() * 0.017453292F - (float) Math.PI);
        float f2 = -Mth.cos(-e.getXRot() * 0.017453292F);
        float f3 = Mth.sin(-e.getXRot() * 0.017453292F);
        return new Vec3((double) (f1 * f2), (double) f3, (double) (f * f2));
    }

    public static float deg(float rad) {
        return (float) (rad * 180d / Math.PI);
    }

    /**
     * returns true if entity2 is within a (horAngle) degree sector in front of entity1, and within (vertAngle)
     * if horAngle is negative, it'll invoke isBehindEntity instead.
     */
    public static boolean isFacingEntity(Entity entity1, Entity entity2, int horAngle, int vertAngle) {
        horAngle = Math.min(horAngle, 360);
        vertAngle = Math.min(vertAngle, 360);
        if (horAngle < 0) return isBehindEntity(entity2, entity1, -horAngle, Math.abs(vertAngle));
        double xDiff = entity1.getX() - entity2.getX(), zDiff = entity1.getZ() - entity2.getZ();
        if (vertAngle != 360) {
            Vec3 posVec = entity2.position().add(0, entity2.getEyeHeight(), 0);
            //y calculations
            double distIgnoreY = Math.sqrt(xDiff * xDiff + zDiff * zDiff);
            double relativeHeadVec = entity2.getY() - entity1.getY() - entity1.getEyeHeight() + entity2.getBbHeight();
            double relativeFootVec = entity2.getY() - entity1.getY() - entity1.getEyeHeight();
            double angleHead = -Mth.atan2(relativeHeadVec, distIgnoreY);
            double angleFoot = -Mth.atan2(relativeFootVec, distIgnoreY);
            //straight up is -90 and straight down is 90
            double maxRot = rad(entity1.getXRot() + vertAngle / 2f);
            double minRot = rad(entity1.getXRot() - vertAngle / 2f);
            if (angleHead > maxRot || angleFoot < minRot) return false;
        }
        if (horAngle != 360) {
            Vec3 lookVec = entity1.getViewVector(1.0F);
            Vec3 bodyVec = getBodyOrientation(entity1);
            //lookVec=new Vector3d(lookVec.x, 0, lookVec.z);
            //bodyVec=new Vector3d(bodyVec.x, 0, bodyVec.z);
            Vec3 relativePosVec = entity2.position().subtract(entity1.position());
            double angleLook = Mth.atan2(lookVec.z, lookVec.x);
            double angleBody = Mth.atan2(bodyVec.z, bodyVec.x);
            double anglePos = Mth.atan2(relativePosVec.z, relativePosVec.x);
            angleBody += Math.PI;
            angleLook += Math.PI;
            anglePos += Math.PI;
            double rad = rad(horAngle / 2f);
            if (Math.abs(angleLook - anglePos) > rad && Math.abs(angleBody - anglePos) > rad) return false;
        }
        return true;
    }

    public static boolean isBehindEntity(Entity entity, Entity reference, int horAngle, int vertAngle) {
        if (horAngle < 0) return isFacingEntity(reference, entity, -horAngle, Math.abs(vertAngle));
        Vec3 posVec = reference.position().add(0, reference.getEyeHeight(), 0);
        //y calculations
        double xDiff = reference.getX() - entity.getX(), zDiff = reference.getZ() - entity.getZ();
        double distIgnoreY = Math.sqrt(xDiff * xDiff + zDiff * zDiff);
        double relativeHeadVec = reference.getY() - entity.getY() - entity.getEyeHeight() + reference.getBbHeight();
        double relativeFootVec = reference.getY() - entity.getY() - entity.getEyeHeight();
        double angleHead = -Mth.atan2(relativeHeadVec, distIgnoreY);
        double angleFoot = -Mth.atan2(relativeFootVec, distIgnoreY);
        //straight up is -90 and straight down is 90
        double maxRot = rad(reference.getXRot() + vertAngle / 2f);
        double minRot = rad(reference.getXRot() - vertAngle / 2f);
        if (angleHead > maxRot || angleFoot < minRot) return false;
        //xz begins
        //subtract half of width from calculations in the xz plane so wide mobs that are barely in frame still get lambasted
        double xDiffCompensated;
        if (xDiff < 0) {
            xDiffCompensated = Math.min(-0.1, xDiff + entity.getBbWidth() / 2 + reference.getBbWidth() / 2);
        } else {
            xDiffCompensated = Math.max(0.1, xDiff - entity.getBbWidth() / 2 - reference.getBbWidth() / 2);
        }
        double zDiffCompensated;
        if (zDiff < 0) {
            zDiffCompensated = Math.min(-0.1, zDiff + entity.getBbWidth() / 2 + reference.getBbWidth() / 2);
        } else {
            zDiffCompensated = Math.max(0.1, zDiff - entity.getBbWidth() / 2 - reference.getBbWidth() / 2);
        }
        Vec3 bodyVec = getBodyOrientation(reference);
        Vec3 lookVec = reference.getViewVector(1f);
        Vec3 relativePosVec = new Vec3(xDiffCompensated, 0, zDiffCompensated);
        double dotsqLook = ((relativePosVec.dot(lookVec) * Math.abs(relativePosVec.dot(lookVec))) / (relativePosVec.lengthSqr() * lookVec.lengthSqr()));
        double dotsqBody = ((relativePosVec.dot(bodyVec) * Math.abs(relativePosVec.dot(bodyVec))) / (relativePosVec.lengthSqr() * bodyVec.lengthSqr()));
        double cos = Mth.cos(rad(horAngle / 2f));
        return dotsqBody > cos * cos || dotsqLook > cos * cos;
    }

    public static BlockPos[] bresenham(BlockPos from, BlockPos to) {

        double p_x = from.getX();
        double p_y = from.getY();
        double p_z = from.getZ();
        double d_x = to.getX() - from.getX();
        double d_y = to.getY() - from.getY();
        double d_z = to.getZ() - from.getZ();
        int N = (int) Math.ceil(Math.max(Math.abs(d_x), Math.max(Math.abs(d_y), Math.abs(d_z))));
        double s_x = d_x / N;
        double s_y = d_y / N;
        double s_z = d_z / N;
        //System.out.println(N);
        BlockPos[] out = new BlockPos[N];
        if (out.length == 0) {
            //System.out.println("nay!");
            return out;
        }
        out[0] = new BlockPos((int) p_x, (int) p_y, (int) p_z);
        for (int ii = 1; ii < N; ii++) {
            p_x += s_x;
            p_y += s_y;
            p_z += s_z;
            out[ii] = new BlockPos((int) p_x, (int) p_y, (int) p_z);
        }
        return out;
    }

    public static float getCosAngleSq(Vec3 from, Vec3 to) {
        double top = from.dot(to) * from.dot(to);
        double bot = from.lengthSqr() * to.lengthSqr();
        return (float) (top / bot);
    }

    /**
     * drops a skull of the given type. For players it will retrieve their skin
     */
    public static ItemStack dropSkull(LivingEntity elb) {
        ItemStack ret = null;
        if (elb instanceof AbstractSkeleton) {
            if (elb instanceof WitherSkeleton)
                ret = new ItemStack(Items.WITHER_SKELETON_SKULL);
            else ret = new ItemStack(Items.SKELETON_SKULL);
        } else if (elb instanceof Zombie)
            ret = new ItemStack(Items.ZOMBIE_HEAD);
        else if (elb instanceof Creeper)
            ret = new ItemStack(Items.CREEPER_HEAD);
        else if (elb instanceof EnderDragon)
            ret = new ItemStack(Items.DRAGON_HEAD);
        else if (elb instanceof Player) {
            Player p = (Player) elb;
            ret = new ItemStack(Items.PLAYER_HEAD);
            ret.setTag(new CompoundTag());
            ret.getTag().putString("SkullOwner", p.getName().getString());
        }
        return ret;
    }

    public static double getAttributeValueHandSensitive(LivingEntity e, Attribute a, InteractionHand h) {
        final AttributeInstance instance = e.getAttribute(a);
        if (instance == null) return 4;
        if (h == InteractionHand.MAIN_HAND) return getAttributeValueSafe(e, a);
        AttributeInstance mai = new AttributeInstance(a, (n) -> {
        });
        mai.setBaseValue(instance.getBaseValue());
        Collection<AttributeModifier> ignore = e.getMainHandItem().getAttributeModifiers(EquipmentSlot.MAINHAND).get(a);
        instance.getModifiers().forEach(am -> {
            if (ignore.stream().noneMatch(b -> am.getId() == b.getId())) mai.addTransientModifier(am);
        });
        for (AttributeModifier f : e.getOffhandItem().getAttributeModifiers(EquipmentSlot.MAINHAND).get(a)) {
            mai.removeModifier(f.getId());
            mai.addTransientModifier(f);
        }
        return mai.getValue();
    }

    public static double getAttributeValueSafe(LivingEntity e, Attribute a) {
        if (e.getAttribute(a) != null) return e.getAttributeValue(a);
        return a.getDefaultValue();
    }

    public static boolean isKitMain(ItemStack is) {
        //if (is.getItem() == Items.IRON_AXE) return true;
        return is.getTag() != null && is.getTag().getBoolean("kit");
    }
}
