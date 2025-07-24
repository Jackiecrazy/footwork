package jackiecrazy.footwork.move.motionframe;

import jackiecrazy.footwork.utils.EasingFunction;

public class MotionManagers {
    public record FixedMM(MotionFrame frame) implements MotionManager {
        @Override
        public MotionFrame getNextPoint(int elapsedTicks) {
            return frame();
        }
    }

    public record TransitionMM(MotionFrame from, MotionFrame to, int totalDuration,
                               EasingFunction easing) implements MotionManager {
        @Override
        public MotionFrame getNextPoint(int elapsedTicks) {
            MotionFrame lerped = from.lerp(to, easing.ease((double) elapsedTicks / totalDuration));
            return lerped;
        }
    }

    public record DefinitionMM(MotionDefinition def) implements MotionManager {
        @Override
        public MotionFrame getNextPoint(int elapsedTicks) {
            return def.interpret(elapsedTicks);
        }
    }
}
