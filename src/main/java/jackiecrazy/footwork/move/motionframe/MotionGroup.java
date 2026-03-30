package jackiecrazy.footwork.move.motionframe;

import jackiecrazy.footwork.utils.EasingFunctionEnum;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector4d;

import java.util.ArrayList;
import java.util.List;

public class MotionGroup {

    private final List<MotionFrame> frames;
    private final EasingFunctionEnum easing;
    private final int duration;
    private transient final double[] lengths;

    public MotionGroup(List<MotionFrame> frames, EasingFunctionEnum easing, int duration, FrameEffects e) {
        this(frames, easing, duration);
        frames.get(0).setEffects(e);
    }

    public MotionGroup(List<MotionFrame> frames, EasingFunctionEnum easing, int duration) {
        this.frames = frames;
        this.easing = easing;
        this.duration = duration;

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
            lengths[i] /= totalLength;
        }
    }

    public MotionGroup(MotionFrame frames, EasingFunctionEnum easing, int time) {
        this(List.of(frames), easing, time);
    }

    /*
    each tick, increase duration.
    Find the normalized duration float and ease it,
    multiply that by the number of frames to figure out approximately which frame we're supposed to be in
     */

    public MotionGroup(MotionFrame frames, EasingFunctionEnum easing) {
        this(List.of(frames), easing, 20);
    }

    public MotionGroup(Vec3 direction, Vec3 offset, Vector4d renderOrientation, EasingFunctionEnum easing) {
        this(new MotionFrame(direction, offset, renderOrientation), easing);
    }

    public MotionGroup(Vec3 dir, Vec3 offset, Vector4d orient) {
        this(dir, offset, orient, EasingFunctionEnum.IN_CUBIC);
    }

    public List<MotionFrame> frames() {
        return frames;
    }

    public EasingFunctionEnum easing() {
        return easing;
    }

    public int duration() {
        return duration;
    }

    public MotionFrame getFirstFrame() {
        return frames.get(0);
    }

    public MotionFrame getLastFrame() {
        return frames.get(frames.size() - 1);
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

        if (easedProgress >= 1) return frames.get(frames.size() - 1);

        double accum = 0;
        double localT = 1;
        int segment = 0;

        for (int i = 0; i < lengths.length; i++) {
            double next = accum + lengths[i];
            segment = i;
            if (easedProgress <= next) {
                localT = Mth.clamp((easedProgress - accum) / lengths[i], 0, 1);
                break;
            }
            accum = next;
        }

        MotionFrame start = frames().get(segment);
        MotionFrame end = frames().get(segment + 1);
        final MotionFrame lerp = start.lerp(end, localT);
        return lerp;
    }
    public MotionGroup invert(){
        List<MotionFrame> flipped=new ArrayList<>();
        frames.forEach(a->flipped.add(a.flip()));
        return new MotionGroup(flipped, easing, duration);
    }
}