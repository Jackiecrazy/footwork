package jackiecrazy.footwork.entity.flyingweapon;

import jackiecrazy.footwork.move.motionframe.MotionFrame;
import jackiecrazy.footwork.move.motionframe.MotionManagers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector4d;

import java.util.List;

public class DummyFlyingWeaponEntity extends FlyingItemEntity{
    public DummyFlyingWeaponEntity(EntityType<? extends FlyingItemEntity> type,
                                   Level level) {
        super(type, level);
        setHeldItem(new ItemStack(Items.IRON_SWORD));
        //setUniversalOffset(new Vec3(0,0,4));
        setIdlePose(new MotionManagers.FixedMM(new MotionFrame(new Vec3(0,0,1),new Vec3(0,0,3),new Vector4d(0, 1, 0,90)), 10));
    }

    @Override
    protected void onHitEntity(List<Entity> targets) {

    }

    @Override
    protected void onHitBlock(BlockPos blockPos, Direction hitFace, Vec3 location) {

    }

    @Override
    public void tick() {
        setInteractionRange(3+2* Mth.sin(Mth.DEG_TO_RAD*tickCount*10));
        setShouldRender(FlyingWeaponEffect.BIG_SHADOW, true);
        if(getOwner()==null) {
            setOwner(level().getNearestPlayer(this, 16));
        }
        internalIdleTimer++;
        if (internalIdleTimer > 1200)//reasonably sure the player doesn't need it anymore
            remove(RemovalReason.DISCARDED);
        super.tick();
    }
}
