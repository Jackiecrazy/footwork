package jackiecrazy.footwork.move.motionframe;

import jackiecrazy.footwork.utils.EasingFunction;
import net.minecraft.world.entity.Entity;

public class MotionManagers {
    public record FixedMM(MotionFrame frame, int duration) implements MotionManager {
        @Override
        public MotionFrame getNextPoint(int elapsedTicks) {
            return frame();
        }

        @Override
        public int getDuration() {
            return duration;
        }
    }

    public record TransitionMM(MotionFrame from, MotionFrame to, int totalDuration,
                               EasingFunction easing) implements MotionManager {
        public TransitionMM(MotionFrame from, MotionManager to, int totalDuration,
                            EasingFunction easing){
            this(from, to.getStartFrame(), totalDuration, easing);
        }
        public TransitionMM(MotionManager from, MotionFrame to, int totalDuration,
                            EasingFunction easing){
            this(from.getStartFrame(), to, totalDuration, easing);
        }
        public TransitionMM(MotionManager from, MotionManager to, int totalDuration,
                            EasingFunction easing){
            this(from.getEndFrame(), to.getStartFrame(), totalDuration, easing);
        }
        @Override
        public MotionFrame getNextPoint(int elapsedTicks) {
            MotionFrame lerped = from.lerp(to, easing.ease((double) elapsedTicks / totalDuration));
            return lerped;
        }

        @Override
        public int getDuration() {
            return totalDuration;
        }
    }

    public record TwoFrameMM(MotionFrame from, MotionFrame to, int totalDuration,
                               EasingFunction easing) implements MotionManager {
        @Override
        public MotionFrame getNextPoint(int elapsedTicks) {
            return from.lerp(to, easing.ease((double) elapsedTicks / totalDuration));
        }

        @Override
        public int getDuration() {
            return totalDuration;
        }
    }

    public record DefinitionMM(MotionDefinition def) implements MotionManager {
        @Override
        public MotionFrame getNextPoint(int elapsedTicks) {
            return def.interpret(elapsedTicks);
        }

        @Override
        public int getDuration() {
            return def.duration();
        }
    }
}
