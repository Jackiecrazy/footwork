package jackiecrazy.footwork.move.motionframe;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public interface MotionManager {
    Vec3 getNextPoint(Entity referent, Vec3 defaultOffset, double range, int elapsedTicks);
}
