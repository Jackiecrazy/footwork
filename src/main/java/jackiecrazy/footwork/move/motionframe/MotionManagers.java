package jackiecrazy.footwork.move.motionframe;

import jackiecrazy.footwork.utils.EasingFunction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class MotionManagers {
    public record FixedMM(MotionFrame frame) implements MotionManager {
        @Override
        public Vec3 getNextPoint(Entity referent, Vec3 defaultOffset, double range, int elapsedTicks) {
            return frame().resolveTargetOffset(referent, defaultOffset, range);
        }
    }

    public record TransitionMM(MotionFrame from, MotionFrame to, int totalDuration,
                               EasingFunction easing) implements MotionManager {
        @Override
        public Vec3 getNextPoint(Entity referent, Vec3 defaultOffset, double range, int elapsedTicks) {
            MotionFrame lerped = from.lerp(to, easing.ease((double) elapsedTicks / totalDuration));
            return lerped.resolveTargetOffset(referent, defaultOffset, range);
        }
    }

    public record DefinitionMM(MotionDefinition def) implements MotionManager {
        @Override
        public Vec3 getNextPoint(Entity referent, Vec3 defaultOffset, double range, int elapsedTicks) {
            return def.resolveTargetOffset(referent, defaultOffset, range, elapsedTicks);
        }
    }
}
