//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package jackiecrazy.footwork.utils;

import jackiecrazy.footwork.capability.resources.CombatData;
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
        if (e.m_20202_() != null)
            if (e.m_20201_() instanceof LivingEntity)
                return CombatData.getCap((LivingEntity) e.m_20201_()).getMotionConsistently().m_82556_();
            else return e.m_20201_().m_20184_().m_82556_();
        if (e instanceof LivingEntity)
            return Math.max(CombatData.getCap((LivingEntity) e).getMotionConsistently().m_82556_(), e.m_20184_().m_82556_());
        return e.m_20184_().m_82556_();
    }

    @Nullable
    public static EntityType getEntityTypeFromResourceLocation(ResourceLocation rl) {
        if (ForgeRegistries.ENTITIES.containsKey(rl))
            return ForgeRegistries.ENTITIES.getValue(rl);
        return null;
    }

    @Nullable
    public static ResourceLocation getResourceLocationFromEntityType(EntityType et) {
        if (ForgeRegistries.ENTITIES.containsValue(et))
            return ForgeRegistries.ENTITIES.getKey(et);
        return null;
    }

    @Nullable
    public static ResourceLocation getResourceLocationFromEntity(Entity et) {
        final EntityType<?> type = et.m_6095_();
        if (ForgeRegistries.ENTITIES.containsValue(type)) {
            final ResourceLocation key = ForgeRegistries.ENTITIES.getKey(type);
            return key;
        }
        return null;
    }

    @Nonnull
    public static HitResult raytraceAnything(Level world, LivingEntity attacker, double range) {
        Vec3 start = attacker.m_20299_(0.5f);
        Vec3 look = attacker.m_20154_().m_82490_(range + 2);
        Vec3 end = start.m_82549_(look);
        Entity entity = null;
        List<Entity> list = world.m_6249_(attacker, attacker.m_142469_().m_82363_(look.f_82479_, look.f_82480_, look.f_82481_).m_82400_(1.0D), null);
        double d0 = 0.0D;

        for (Entity entity1 : list) {
            if (entity1 != attacker) {
                AABB axisalignedbb = entity1.m_142469_();
                Optional<Vec3> raytraceresult = axisalignedbb.m_82371_(start, end);
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
        look = attacker.m_20154_().m_82490_(range);
        end = start.m_82549_(look);
        HitResult rtr = world.m_45547_(new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, null));
        if (rtr != null) {
            return rtr;
        }
        return new BlockHitResult(end, Direction.UP, new BlockPos(end), false);
    }

    /**
     * modified getdistancesq to account for thicc mobs
     */
    public static double getDistSqCompensated(Entity from, Entity to) {
        double x = from.m_20185_() - to.m_20185_();
        x = Math.max(Math.abs(x) - ((from.m_20205_() / 2) + (to.m_20205_() / 2)), 0);
        //stupid inconsistent game
        double y = (from.m_20186_() + from.m_20206_() / 2) - (to.m_20186_() + to.m_20206_() / 2);
        y = Math.max(Math.abs(y) - (from.m_20206_() / 2 + to.m_20206_() / 2), 0);
        double z = from.m_20189_() - to.m_20189_();
        z = Math.max(Math.abs(z) - (from.m_20205_() / 2 + to.m_20205_() / 2), 0);
        double me = x * x + y * y + z * z;
        double you = from.m_20280_(to);
        return Math.min(me, you);
    }

    public static float getMaxHealthBeforeWounding(LivingEntity of) {
        return of.m_21233_() + CombatData.getCap(of).getWounding();
    }

    /**
     * modified getdistancesq to account for thicc mobs
     */
    public static double getDistSqCompensated(Entity from, Vec3 to) {
        double x = from.m_20185_() - to.f_82479_;
        x = Math.max(Math.abs(x) - ((from.m_20205_() / 2)), 0);
        //stupid inconsistent game
        double y = (from.m_20186_() + from.m_20206_() / 2) - (to.f_82480_);
        y = Math.max(Math.abs(y) - (from.m_20206_() / 2), 0);
        double z = from.m_20189_() - to.f_82481_;
        z = Math.max(Math.abs(z) - (from.m_20205_() / 2), 0);
        return x * x + y * y + z * z;
    }

    /**
     * modified getdistancesq to account for thicc mobs
     */
    public static double getDistSqCompensated(Entity from, BlockPos to) {
        double x = from.m_20185_() - to.m_123341_();
        x = Math.max(Math.abs(x) - ((from.m_20205_() / 2)), 0);
        //stupid inconsistent game
        double y = (from.m_20186_() + from.m_20206_() / 2) - (to.m_123342_());
        y = Math.max(Math.abs(y) - (from.m_20206_() / 2), 0);
        double z = from.m_20189_() - to.m_123343_();
        z = Math.max(Math.abs(z) - (from.m_20205_() / 2), 0);
        return x * x + y * y + z * z;
    }

    public static Entity raytraceEntity(Level world, LivingEntity attacker, double range) {
        Vec3 start = attacker.m_20299_(0.5f);
        Vec3 look = attacker.m_20154_().m_82490_(range + 2);
        Vec3 end = start.m_82549_(look);
        Entity entity = null;
        List<Entity> list = world.m_6249_(attacker, attacker.m_142469_().m_82363_(look.f_82479_, look.f_82480_, look.f_82481_).m_82400_(1.0D), null);
        double d0 = -1.0D;//necessary to prevent small derps

        for (Entity entity1 : list) {
            if (entity1 != attacker) {
                AABB axisalignedbb = entity1.m_142469_();
                Optional<Vec3> raytraceresult = axisalignedbb.m_82371_(start, end);
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
        return raytraceLiving(attacker.f_19853_, attacker, range);
    }

    public static LivingEntity raytraceLiving(Level world, LivingEntity attacker, double range) {
        Vec3 start = attacker.m_20299_(0.5f);
        Vec3 look = attacker.m_20154_().m_82490_(range + 2);
        Vec3 end = start.m_82549_(look);
        LivingEntity entity = null;
        List<LivingEntity> list = world.m_6443_(LivingEntity.class, attacker.m_142469_().m_82363_(look.f_82479_, look.f_82480_, look.f_82481_).m_82400_(1.5D), null);
        double d0 = -1.0D;//necessary to prevent small derps

        for (LivingEntity entity1 : list) {
            if (entity1 != attacker) {
                AABB axisalignedbb = entity1.m_142469_();
                Optional<Vec3> raytraceresult = axisalignedbb.m_82371_(start, end);
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
        AABB aabb = elb.m_142469_();
        Vec3 motion = elb.m_20184_().m_82541_().m_82490_(0.5);
        List<Entity> entities = elb.f_19853_.m_6249_(elb, aabb.m_82363_(motion.f_82479_, motion.f_82480_, motion.f_82481_), EntitySelector.f_20402_);
        double dist = 0;
        Entity pick = null;
        for (Entity e : entities) {
            if (e.m_20280_(elb) < dist || dist == 0) {
                pick = e;
                dist = e.m_20280_(elb);
            }
        }
        return pick;
    }

    public static List<Entity> raytraceEntities(Level world, LivingEntity attacker, double range) {
        Vec3 start = attacker.m_20299_(0.5f);
        Vec3 look = attacker.m_20154_().m_82490_(range + 2);
        Vec3 end = start.m_82549_(look);
        ArrayList<Entity> ret = new ArrayList<>();
        List<Entity> list = world.m_6249_(attacker, attacker.m_142469_().m_82363_(look.f_82479_, look.f_82480_, look.f_82481_).m_82400_(1.0D), EntitySelector.f_20402_);

        for (Entity entity1 : list) {
            if (entity1 != attacker && getDistSqCompensated(attacker, entity1) < range * range) {
                AABB axisalignedbb = entity1.m_142469_();
                Optional<Vec3> raytraceresult = axisalignedbb.m_82371_(start, end);
                if (raytraceresult.isPresent()) {
                    ret.add(entity1);
                }
            }
        }
        return ret;
    }

    public static Vec3 getPointInFrontOf(Entity target, Entity from, double distance) {
        Vec3 end = target.m_20182_().m_82549_(from.m_20182_().m_82546_(target.m_20182_()).m_82541_().m_82490_(distance));
        return getClosestAirSpot(from.m_20182_(), end, from);
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
        to = to.m_82549_(to.m_82546_(from).m_82541_().m_82490_(2));
        double widthParse = e.m_20205_() / 2;
        double heightParse = e.m_20206_();
        if (widthParse <= 0.5) widthParse = 0;
        if (heightParse <= 1) heightParse = 0;
        for (double addX = -widthParse; addX <= widthParse; addX += 0.5) {
            for (double addZ = -widthParse; addZ <= widthParse; addZ += 0.5) {
                for (double addY = e.m_20206_() / 2; addY <= heightParse; addY += 0.5) {
                    Vec3 mod = new Vec3(addX, addY, addZ);
                    BlockHitResult r = e.f_19853_.m_45547_(new ClipContext(from.m_82549_(mod), to.m_82549_(mod), ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY, e));
                    if (r != null && r.m_6662_() == HitResult.Type.BLOCK && !r.m_82450_().equals(from.m_82549_(mod))) {
                        Vec3 hit = r.m_82450_().m_82546_(mod);
                        switch (r.m_82434_()) {
                            case NORTH:
                                hit = hit.m_82520_(0, 0, -1);
                                break;
                            case SOUTH:
                                hit = hit.m_82520_(0, 0, 1);
                                break;
                            case EAST:
                                hit = hit.m_82520_(1, 0, 0);
                                break;
                            case WEST:
                                hit = hit.m_82520_(-1, 0, 0);
                                break;
                            case UP:
                                //hit.add(0, -1, 0); //apparently unnecessary.
                                break;
                            case DOWN:
                                hit = hit.m_82520_(0, -1, 0);
                                break;
                        }
                        if (from.m_82557_(hit) < from.m_82557_(ret)) {
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
        if (viewer.m_20280_(viewed) > 1000) return true;//what
        AABB viewerBoundBox = viewer.m_142469_();
        AABB angelBoundingBox = viewed.m_142469_();
        Vec3[] viewerPoints = {new Vec3(viewerBoundBox.f_82288_, viewerBoundBox.f_82289_, viewerBoundBox.f_82290_),
                new Vec3(viewerBoundBox.f_82288_, viewerBoundBox.f_82289_, viewerBoundBox.f_82293_),
                new Vec3(viewerBoundBox.f_82288_, viewerBoundBox.f_82292_, viewerBoundBox.f_82290_),
                new Vec3(viewerBoundBox.f_82288_, viewerBoundBox.f_82292_, viewerBoundBox.f_82293_),
                new Vec3(viewerBoundBox.f_82291_, viewerBoundBox.f_82292_, viewerBoundBox.f_82290_),
                new Vec3(viewerBoundBox.f_82291_, viewerBoundBox.f_82292_, viewerBoundBox.f_82293_),
                new Vec3(viewerBoundBox.f_82291_, viewerBoundBox.f_82289_, viewerBoundBox.f_82293_),
                new Vec3(viewerBoundBox.f_82291_, viewerBoundBox.f_82289_, viewerBoundBox.f_82290_),};

        Vec3[] angelPoints = {new Vec3(angelBoundingBox.f_82288_, angelBoundingBox.f_82289_, angelBoundingBox.f_82290_),
                new Vec3(angelBoundingBox.f_82288_, angelBoundingBox.f_82289_, angelBoundingBox.f_82293_),
                new Vec3(angelBoundingBox.f_82288_, angelBoundingBox.f_82292_, angelBoundingBox.f_82290_),
                new Vec3(angelBoundingBox.f_82288_, angelBoundingBox.f_82292_, angelBoundingBox.f_82293_),
                new Vec3(angelBoundingBox.f_82291_, angelBoundingBox.f_82292_, angelBoundingBox.f_82290_),
                new Vec3(angelBoundingBox.f_82291_, angelBoundingBox.f_82292_, angelBoundingBox.f_82293_),
                new Vec3(angelBoundingBox.f_82291_, angelBoundingBox.f_82289_, angelBoundingBox.f_82293_),
                new Vec3(angelBoundingBox.f_82291_, angelBoundingBox.f_82289_, angelBoundingBox.f_82290_),};

        for (int i = 0; i < viewerPoints.length; i++) {
            if (viewer.f_19853_.m_45547_(new ClipContext(viewerPoints[i], angelPoints[i], flimsy ? ClipContext.Block.OUTLINE : ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, viewer)).m_6662_() == HitResult.Type.MISS) {
                return false;
            }
            if (rayTraceBlocks(viewer, viewer.f_19853_, viewerPoints[i], angelPoints[i], pos -> {
                BlockState state = viewer.f_19853_.m_8055_(pos);
                return flimsy ? !state.m_60767_().m_76332_() : !canSeeThrough(state, viewer.f_19853_, pos);
            }) == null) return false;
        }

        return true;
    }

    /**
     * @author Suff/Swirtzly
     */
    @Nullable
    private static HitResult rayTraceBlocks(LivingEntity livingEntity, Level world, Vec3 vec31, Vec3 vec32, Predicate<BlockPos> stopOn) {
        if (!Double.isNaN(vec31.f_82479_) && !Double.isNaN(vec31.f_82480_) && !Double.isNaN(vec31.f_82481_)) {
            if (!Double.isNaN(vec32.f_82479_) && !Double.isNaN(vec32.f_82480_) && !Double.isNaN(vec32.f_82481_)) {
                int i = Mth.m_14107_(vec32.f_82479_);
                int j = Mth.m_14107_(vec32.f_82480_);
                int k = Mth.m_14107_(vec32.f_82481_);
                int l = Mth.m_14107_(vec31.f_82479_);
                int i1 = Mth.m_14107_(vec31.f_82480_);
                int j1 = Mth.m_14107_(vec31.f_82481_);
                BlockPos blockpos = new BlockPos(l, i1, j1);
                if (stopOn.test(blockpos)) {
                    HitResult raytraceresult = world.m_45547_(new ClipContext(vec31, vec32, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, livingEntity));
                    if (raytraceresult != null) {
                        return raytraceresult;
                    }
                }

                int k1 = 200;

                while (k1-- >= 0) {
                    if (Double.isNaN(vec31.f_82479_) || Double.isNaN(vec31.f_82480_) || Double.isNaN(vec31.f_82481_)) {
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
                    double d6 = vec32.f_82479_ - vec31.f_82479_;
                    double d7 = vec32.f_82480_ - vec31.f_82480_;
                    double d8 = vec32.f_82481_ - vec31.f_82481_;

                    if (flag2) {
                        d3 = (d0 - vec31.f_82479_) / d6;
                    }

                    if (flag) {
                        d4 = (d1 - vec31.f_82480_) / d7;
                    }

                    if (flag1) {
                        d5 = (d2 - vec31.f_82481_) / d8;
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
                        vec31 = new Vec3(d0, vec31.f_82480_ + d7 * d3, vec31.f_82481_ + d8 * d3);
                    } else if (d4 < d5) {
                        enumfacing = j > i1 ? Direction.DOWN : Direction.UP;
                        vec31 = new Vec3(vec31.f_82479_ + d6 * d4, d1, vec31.f_82481_ + d8 * d4);
                    } else {
                        enumfacing = k > j1 ? Direction.NORTH : Direction.SOUTH;
                        vec31 = new Vec3(vec31.f_82479_ + d6 * d5, vec31.f_82480_ + d7 * d5, d2);
                    }

                    l = Mth.m_14107_(vec31.f_82479_) - (enumfacing == Direction.EAST ? 1 : 0);
                    i1 = Mth.m_14107_(vec31.f_82480_) - (enumfacing == Direction.UP ? 1 : 0);
                    j1 = Mth.m_14107_(vec31.f_82481_) - (enumfacing == Direction.SOUTH ? 1 : 0);
                    blockpos = new BlockPos(l, i1, j1);
                    if (stopOn.test(blockpos)) {
                        HitResult raytraceresult1 = world.m_45547_(new ClipContext(vec31, vec32, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, livingEntity));

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
        if (!blockState.m_60815_() || !blockState.m_60804_(world, pos)) {
            return true;
        }

        Block block = blockState.m_60734_();

        // Special Snowflakes
        if (block instanceof DoorBlock) {
            return blockState.m_61143_(DoorBlock.f_52730_) == DoubleBlockHalf.UPPER;
        }

        return blockState.m_60812_(world, pos) == Shapes.m_83040_();
    }

    /**
     * returns the BlockPos at the center of an AABB
     */
    public static BlockPos posFromAABB(AABB aabb) {
        return new BlockPos((aabb.f_82291_ + aabb.f_82288_) / 2, (aabb.f_82292_ + aabb.f_82289_) / 2, (aabb.f_82293_ + aabb.f_82290_) / 2);
    }


    /**
     * returns true if entity2 is within a (angle) degree sector in front of entity1
     */
    public static boolean isFacingEntity(Entity entity1, Entity entity2, int angle) {
        if (angle >= 360) return true;//well, duh.
        if (angle < 0) return isBehindEntity(entity2, entity1, -angle);
        Vec3 posVec = entity2.m_20182_().m_82520_(0, entity2.m_20192_(), 0);
        Vec3 lookVec = entity1.m_20252_(1.0F);
        Vec3 relativePosVec = posVec.m_82505_(entity1.m_20182_().m_82520_(0, entity1.m_20192_(), 0)).m_82541_();
        //relativePosVec = new Vector3d(relativePosVec.x, 0.0D, relativePosVec.z);

        double dotsq = ((relativePosVec.m_82526_(lookVec) * Math.abs(relativePosVec.m_82526_(lookVec))) / (relativePosVec.m_82556_() * lookVec.m_82556_()));
        double cos = Mth.m_14089_(rad(angle / 2f));
        return dotsq < -(cos * cos);
    }

    /**
     * returns true if entity is within a 90 degree sector behind the reference
     */
    public static boolean isBehindEntity(Entity entity, Entity reference, int angle) {
        if (angle >= 360) return true;//well, duh.
        Vec3 posVec = entity.m_20182_().m_82520_(0, entity.m_20192_(), 0);
        Vec3 lookVec = getBodyOrientation(reference);
        Vec3 relativePosVec = posVec.m_82505_(reference.m_20182_().m_82520_(0, reference.m_20192_(), 0)).m_82541_();
        relativePosVec = new Vec3(relativePosVec.f_82479_, 0.0D, relativePosVec.f_82481_);
        double dotsq = ((relativePosVec.m_82526_(lookVec) * Math.abs(relativePosVec.m_82526_(lookVec))) / (relativePosVec.m_82556_() * lookVec.m_82556_()));
        double cos = Mth.m_14089_(rad(angle / 2f));
        return dotsq > cos * cos;
    }

    public static float rad(float angle) {
        return (float) (angle * Math.PI / 180d);
    }

    /**
     * literally a copy-paste of {@link Entity#getLookAngle()} ()} for {@link LivingEntity}, since they calculate from their head instead
     */
    public static Vec3 getBodyOrientation(Entity e) {
        float f = Mth.m_14089_(-e.m_146908_() * 0.017453292F - (float) Math.PI);
        float f1 = Mth.m_14031_(-e.m_146908_() * 0.017453292F - (float) Math.PI);
        float f2 = -Mth.m_14089_(-e.m_146909_() * 0.017453292F);
        float f3 = Mth.m_14031_(-e.m_146909_() * 0.017453292F);
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
        double xDiff = entity1.m_20185_() - entity2.m_20185_(), zDiff = entity1.m_20189_() - entity2.m_20189_();
        if (vertAngle != 360) {
            Vec3 posVec = entity2.m_20182_().m_82520_(0, entity2.m_20192_(), 0);
            //y calculations
            double distIgnoreY = Math.sqrt(xDiff * xDiff + zDiff * zDiff);
            double relativeHeadVec = entity2.m_20186_() - entity1.m_20186_() - entity1.m_20192_() + entity2.m_20206_();
            double relativeFootVec = entity2.m_20186_() - entity1.m_20186_() - entity1.m_20192_();
            double angleHead = -Mth.m_14136_(relativeHeadVec, distIgnoreY);
            double angleFoot = -Mth.m_14136_(relativeFootVec, distIgnoreY);
            //straight up is -90 and straight down is 90
            double maxRot = rad(entity1.m_146909_() + vertAngle / 2f);
            double minRot = rad(entity1.m_146909_() - vertAngle / 2f);
            if (angleHead > maxRot || angleFoot < minRot) return false;
        }
        if (horAngle != 360) {
            Vec3 lookVec = entity1.m_20252_(1.0F);
            Vec3 bodyVec = getBodyOrientation(entity1);
            //lookVec=new Vector3d(lookVec.x, 0, lookVec.z);
            //bodyVec=new Vector3d(bodyVec.x, 0, bodyVec.z);
            Vec3 relativePosVec = entity2.m_20182_().m_82546_(entity1.m_20182_());
            double angleLook = Mth.m_14136_(lookVec.f_82481_, lookVec.f_82479_);
            double angleBody = Mth.m_14136_(bodyVec.f_82481_, bodyVec.f_82479_);
            double anglePos = Mth.m_14136_(relativePosVec.f_82481_, relativePosVec.f_82479_);
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
        Vec3 posVec = reference.m_20182_().m_82520_(0, reference.m_20192_(), 0);
        //y calculations
        double xDiff = reference.m_20185_() - entity.m_20185_(), zDiff = reference.m_20189_() - entity.m_20189_();
        double distIgnoreY = Math.sqrt(xDiff * xDiff + zDiff * zDiff);
        double relativeHeadVec = reference.m_20186_() - entity.m_20186_() - entity.m_20192_() + reference.m_20206_();
        double relativeFootVec = reference.m_20186_() - entity.m_20186_() - entity.m_20192_();
        double angleHead = -Mth.m_14136_(relativeHeadVec, distIgnoreY);
        double angleFoot = -Mth.m_14136_(relativeFootVec, distIgnoreY);
        //straight up is -90 and straight down is 90
        double maxRot = rad(reference.m_146909_() + vertAngle / 2f);
        double minRot = rad(reference.m_146909_() - vertAngle / 2f);
        if (angleHead > maxRot || angleFoot < minRot) return false;
        //xz begins
        //subtract half of width from calculations in the xz plane so wide mobs that are barely in frame still get lambasted
        double xDiffCompensated;
        if (xDiff < 0) {
            xDiffCompensated = Math.min(-0.1, xDiff + entity.m_20205_() / 2 + reference.m_20205_() / 2);
        } else {
            xDiffCompensated = Math.max(0.1, xDiff - entity.m_20205_() / 2 - reference.m_20205_() / 2);
        }
        double zDiffCompensated;
        if (zDiff < 0) {
            zDiffCompensated = Math.min(-0.1, zDiff + entity.m_20205_() / 2 + reference.m_20205_() / 2);
        } else {
            zDiffCompensated = Math.max(0.1, zDiff - entity.m_20205_() / 2 - reference.m_20205_() / 2);
        }
        Vec3 bodyVec = getBodyOrientation(reference);
        Vec3 lookVec = reference.m_20252_(1f);
        Vec3 relativePosVec = new Vec3(xDiffCompensated, 0, zDiffCompensated);
        double dotsqLook = ((relativePosVec.m_82526_(lookVec) * Math.abs(relativePosVec.m_82526_(lookVec))) / (relativePosVec.m_82556_() * lookVec.m_82556_()));
        double dotsqBody = ((relativePosVec.m_82526_(bodyVec) * Math.abs(relativePosVec.m_82526_(bodyVec))) / (relativePosVec.m_82556_() * bodyVec.m_82556_()));
        double cos = Mth.m_14089_(rad(horAngle / 2f));
        return dotsqBody > cos * cos || dotsqLook > cos * cos;
    }

    public static BlockPos[] bresenham(BlockPos from, BlockPos to) {

        double p_x = from.m_123341_();
        double p_y = from.m_123342_();
        double p_z = from.m_123343_();
        double d_x = to.m_123341_() - from.m_123341_();
        double d_y = to.m_123342_() - from.m_123342_();
        double d_z = to.m_123343_() - from.m_123343_();
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
        double top = from.m_82526_(to) * from.m_82526_(to);
        double bot = from.m_82556_() * to.m_82556_();
        return (float) (top / bot);
    }

    /**
     * drops a skull of the given type. For players it will retrieve their skin
     */
    public static ItemStack dropSkull(LivingEntity elb) {
        ItemStack ret = null;
        if (elb instanceof AbstractSkeleton) {
            if (elb instanceof WitherSkeleton)
                ret = new ItemStack(Items.f_42679_);
            else ret = new ItemStack(Items.f_42678_);
        } else if (elb instanceof Zombie)
            ret = new ItemStack(Items.f_42681_);
        else if (elb instanceof Creeper)
            ret = new ItemStack(Items.f_42682_);
        else if (elb instanceof EnderDragon)
            ret = new ItemStack(Items.f_42683_);
        else if (elb instanceof Player) {
            Player p = (Player) elb;
            ret = new ItemStack(Items.f_42680_);
            ret.m_41751_(new CompoundTag());
            ret.m_41783_().m_128359_("SkullOwner", p.m_7755_().getString());
        }
        return ret;
    }

    public static double getAttributeValueHandSensitive(LivingEntity e, Attribute a, InteractionHand h) {
        if (e.m_21051_(a) == null) return 4;
        if (h == InteractionHand.MAIN_HAND) return getAttributeValueSafe(e, a);
        AttributeInstance mai = new AttributeInstance(a, (n) -> {
        });
        Collection<AttributeModifier> ignore = e.m_21205_().m_41638_(EquipmentSlot.MAINHAND).get(a);
        apply:
        for (AttributeModifier am : e.m_21051_(a).m_22122_()) {
            for (AttributeModifier f : ignore) if (f.m_22209_().equals(am.m_22209_())) continue apply;
            mai.m_22118_(am);
        }
        for (AttributeModifier f : e.m_21206_().m_41638_(EquipmentSlot.MAINHAND).get(a)) {
            mai.m_22120_(f.m_22209_());
            mai.m_22118_(f);
        }
        return mai.m_22135_();
    }

    public static double getAttributeValueSafe(LivingEntity e, Attribute a) {
        if (e.m_21051_(a) != null) return e.m_21133_(a);
        return a.m_22082_();
    }

    public static boolean isKitMain(ItemStack is) {
        //if (is.getItem() == Items.IRON_AXE) return true;
        return is.m_41783_() != null && is.m_41783_().m_128471_("kit");
    }
}
