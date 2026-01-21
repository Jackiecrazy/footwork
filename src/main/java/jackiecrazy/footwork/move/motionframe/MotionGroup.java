package jackiecrazy.footwork.move.motionframe;

import jackiecrazy.footwork.utils.EasingFunction;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector4d;

import java.util.List;

public class MotionGroup {

    private final List<MotionFrame> frames;
    private final EasingFunction easing;
    private final int duration;

    public List<MotionFrame> frames() {
        return frames;
    }

    public EasingFunction easing() {
        return easing;
    }

    public int duration() {
        return duration;
    }

    private final double[] lengths;

    /*
    each tick, increase duration.
    Find the normalized duration float and ease it,
    multiply that by the number of frames to figure out approximately which frame we're supposed to be in
     */

    public MotionGroup(List<MotionFrame> frames, EasingFunction easing, int duration){
        this.frames=frames;
        this.easing=easing;
        this.duration=duration;

        lengths = new double[frames.size() - 1];
        double totalLength = 0;

        for (int i = 0; i < frames.size() - 1; i++) {
            final Vec3 curPos = frames.get(i).resolveTargetOffset();
            final Vec3 topPos = frames.get(i + 1).resolveTargetOffset();
            lengths[i] = curPos
                    .distanceTo(topPos);
            totalLength += lengths[i];
        }
        for (int i = 0; i < lengths.length; i++) {
            lengths[i] /=totalLength;
        }
    }

    public MotionGroup(MotionFrame frames, EasingFunction easing, int time) {
        this(List.of(frames), easing, time);
    }

    public MotionGroup(MotionFrame frames, EasingFunction easing) {
        this(List.of(frames), easing, 20);
    }

    public MotionGroup(Vec3 direction, Vec3 offset, Vector4d renderOrientation, EasingFunction easing) {
        this(new MotionFrame(direction, offset, renderOrientation), easing);
    }

    public MotionGroup(Vec3 dir, Vec3 offset, Vector4d orient) {
        this(dir, offset, orient, EasingFunction.IN_CUBIC);
    }

    public MotionFrame interpret(int time) {
        int frameCount = frames.size();
        if (frameCount < 2) return frames.get(0);

        double progress = (double) time / duration();
        double easedProgress = easing().ease(progress); // Output in [0, 1]

//        // Total number of segments is one less than the number of frames
//        int segmentCount = frameCount - 1;
//        double segmentLength = 1.0 / segmentCount;
//
//        // Determine current segment and local progress
//        int segment = Math.min((int) (easedProgress / segmentLength), segmentCount - 1);
//        double localT = (easedProgress - segment * segmentLength) / segmentLength;

        if(easedProgress>1)return frames.get(frames.size()-1);

        double accum = 0;
        double localT=0;
        int segment=0;

        for (int i = 0; i < lengths.length; i++) {
            double next = accum + lengths[i];
            if (easedProgress <= next) {
                localT = Mth.clamp((easedProgress - accum) / lengths[i], 0,1);
                segment=i;
                break;
            }
            accum = next;
        }

        MotionFrame start = frames().get(segment);
        MotionFrame end = frames().get(segment + 1);
        return start.lerp(end, localT);
    }
}