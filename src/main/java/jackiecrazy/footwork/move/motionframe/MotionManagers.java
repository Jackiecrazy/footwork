package jackiecrazy.footwork.move.motionframe;

import jackiecrazy.footwork.utils.EasingFunctionEnum;
import org.joml.Vector3f;

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
        public MotionFrame getNextPoint(double elapsedTicks) {
            return frame;
        }

        @Override
        public int getDuration() {
            return duration;
        }

        @Override
        public MotionManager flipFrames() {
            return new FixedMM(frame.flip(), duration).setAngularVelocity(getSpin().mul(-1,1,1, new Vector3f()));
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
        public MotionFrame getNextPoint(double elapsedTicks) {
            MotionFrame lerped = from.lerp(to, easing.ease((double) elapsedTicks / totalDuration));
            return lerped;
        }

        @Override
        public int getDuration() {
            return totalDuration;
        }

        @Override
        public MotionManager flipFrames() {
            return this;//this cannot be flipped by principle
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
        public MotionFrame getNextPoint(double elapsedTicks) {
            return from.lerp(to, easing.ease((double) elapsedTicks / totalDuration));
        }

        @Override
        public int getDuration() {
            return totalDuration;
        }

        @Override
        public MotionManager flipFrames() {
            return new TwoFrameMM(from.flip(), to.flip(), totalDuration, easing).setAngularVelocity(getSpin().mul(-1,1,1, new Vector3f()));
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
        public MotionManager flipFrames() {
            return new DefinitionMM(def.invert()).setAngularVelocity(getSpin().mul(-1,1,1, new Vector3f()));
        }

        @Override
        public MotionFrame getNextPoint(double elapsedTicks) {
            final MotionFrame lerped = def.interpret(elapsedTicks);
            return lerped;
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
