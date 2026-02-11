package jackiecrazy.footwork.move.motionframe;

import jackiecrazy.footwork.utils.EasingFunctionEnum;
import org.joml.Quaternionf;

import java.util.Objects;

public class MotionManagers {
    public static final class FixedMM extends MotionManager {
        private final MotionFrame frame;
        private final int duration;

        public FixedMM(MotionFrame frame, int duration) {
            this.frame = frame;
            this.duration = duration;
        }

        @Override
        public MotionFrame getNextPoint(int elapsedTicks) {
            return frame.setRenderOrientation(resolveFinalRotation(frame, elapsedTicks, 0, new Quaternionf()));
        }

        @Override
        public int getDuration() {
            return duration;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (FixedMM) obj;
            return Objects.equals(this.frame, that.frame) &&
                    this.duration == that.duration;
        }

        @Override
        public int hashCode() {
            return Objects.hash(frame, duration);
        }

        @Override
        public String toString() {
            return "FixedMM[" +
                    "frame=" + frame + ", " +
                    "duration=" + duration + ']';
        }


    }

    public static final class TransitionMM extends MotionManager {
        private final MotionFrame from;
        private final MotionFrame to;
        private final int totalDuration;
        private final EasingFunctionEnum easing;

        public TransitionMM(MotionFrame from, MotionFrame to, int totalDuration,
                            EasingFunctionEnum easing) {
            this.from = from;
            this.to = to;
            this.totalDuration = totalDuration;
            this.easing = easing;
        }

        public TransitionMM(MotionFrame from, MotionManager to, int totalDuration,
                            EasingFunctionEnum easing) {
            this(from, to.getStartFrame(), totalDuration, easing);
        }

        public TransitionMM(MotionManager from, MotionFrame to, int totalDuration,
                            EasingFunctionEnum easing) {
            this(from.getStartFrame(), to, totalDuration, easing);
        }

        public TransitionMM(MotionManager from, MotionManager to, int totalDuration,
                            EasingFunctionEnum easing) {
            this(from.getEndFrame(), to.getStartFrame(), totalDuration, easing);
        }

        @Override
        public MotionFrame getNextPoint(int elapsedTicks) {
            MotionFrame lerped = from.lerp(to, easing.ease((double) elapsedTicks / totalDuration));
            return lerped.setRenderOrientation(resolveFinalRotation(lerped, elapsedTicks, 0, new Quaternionf()));
        }

        @Override
        public int getDuration() {
            return totalDuration;
        }

        public MotionFrame from() {
            return from;
        }

        public MotionFrame to() {
            return to;
        }

        public int totalDuration() {
            return totalDuration;
        }

        public EasingFunctionEnum easing() {
            return easing;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (TransitionMM) obj;
            return Objects.equals(this.from, that.from) &&
                    Objects.equals(this.to, that.to) &&
                    this.totalDuration == that.totalDuration &&
                    Objects.equals(this.easing, that.easing);
        }

        @Override
        public int hashCode() {
            return Objects.hash(from, to, totalDuration, easing);
        }

        @Override
        public String toString() {
            return "TransitionMM[" +
                    "from=" + from + ", " +
                    "to=" + to + ", " +
                    "totalDuration=" + totalDuration + ", " +
                    "easing=" + easing + ']';
        }

    }

    public static final class TwoFrameMM extends MotionManager {
        private final MotionFrame from;
        private final MotionFrame to;
        private final int totalDuration;
        private final EasingFunctionEnum easing;

        public TwoFrameMM(MotionFrame from, MotionFrame to, int totalDuration,
                          EasingFunctionEnum easing) {
            this.from = from;
            this.to = to;
            this.totalDuration = totalDuration;
            this.easing = easing;
        }

        @Override
        public MotionFrame getNextPoint(int elapsedTicks) {
            return from.lerp(to, easing.ease((double) elapsedTicks / totalDuration));
        }

        @Override
        public int getDuration() {
            return totalDuration;
        }

        public MotionFrame from() {
            return from;
        }

        public MotionFrame to() {
            return to;
        }

        public int totalDuration() {
            return totalDuration;
        }

        public EasingFunctionEnum easing() {
            return easing;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (TwoFrameMM) obj;
            return Objects.equals(this.from, that.from) &&
                    Objects.equals(this.to, that.to) &&
                    this.totalDuration == that.totalDuration &&
                    Objects.equals(this.easing, that.easing);
        }

        @Override
        public int hashCode() {
            return Objects.hash(from, to, totalDuration, easing);
        }

        @Override
        public String toString() {
            return "TwoFrameMM[" +
                    "from=" + from + ", " +
                    "to=" + to + ", " +
                    "totalDuration=" + totalDuration + ", " +
                    "easing=" + easing + ']';
        }


    }

    public static final class DefinitionMM extends MotionManager {
        private final MotionGroup def;

        public DefinitionMM(MotionGroup def) {
            this.def = def;
        }

        @Override
        public MotionFrame getStartFrame() {
            return def.getFirstFrame();
        }

        @Override
        public MotionFrame getEndFrame() {
            return def.getLastFrame();
        }

        @Override
        public MotionFrame getNextPoint(int elapsedTicks) {
            final MotionFrame lerped = def.interpret(elapsedTicks);
            return lerped.setRenderOrientation(resolveFinalRotation(lerped, elapsedTicks, 0, new Quaternionf()));
        }

        @Override
        public int getDuration() {
            return def.duration();
        }

        public MotionGroup def() {
            return def;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (DefinitionMM) obj;
            return Objects.equals(this.def, that.def);
        }

        @Override
        public int hashCode() {
            return Objects.hash(def);
        }

        @Override
        public String toString() {
            return "DefinitionMM[" +
                    "def=" + def + ']';
        }


    }
}
