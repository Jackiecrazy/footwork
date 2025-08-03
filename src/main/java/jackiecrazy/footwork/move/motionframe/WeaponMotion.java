package jackiecrazy.footwork.move.motionframe;

import jackiecrazy.footwork.utils.EasingFunction;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector4d;

import java.util.List;

public record WeaponMotion(List<MotionFrame> frames, EasingFunction easing, int duration) {

    /*
    each tick, increase duration.
    Find the normalized duration float and ease it,
    multiply that by the number of frames to figure out approximately which frame we're supposed to be in
     */

    public WeaponMotion(MotionFrame frames, EasingFunction easing, int time) {
        this(List.of(frames), easing, time);
    }

    public WeaponMotion(MotionFrame frames, EasingFunction easing) {
        this(List.of(frames), easing, 20);
    }

    public WeaponMotion(Vec3 direction, Vec3 offset, Vector4d renderOrientation, EasingFunction easing) {
        this(new MotionFrame(direction, offset, renderOrientation), easing);
    }

    public WeaponMotion(Vec3 dir, Vec3 offset, Vector4d orient) {
        this(dir, offset, orient, EasingFunction.IN_CUBIC);
    }

    public MotionFrame interpret(int time) {
        int frameCount = frames.size();
        if (frameCount < 2) return frames.get(0);

        double progress = (double) time / duration();
        double easedProgress = easing().ease(progress); // Output in [0, 1]

        // Total number of segments is one less than the number of frames
        int segmentCount = frameCount - 1;
        double segmentLength = 1.0 / segmentCount;

        // Determine current segment and local progress
        int segment = Math.min((int) (easedProgress / segmentLength), segmentCount - 1);
        double localT = (easedProgress - segment * segmentLength) / segmentLength;//FIXME?

        MotionFrame start = frames().get(segment);
        MotionFrame end = frames().get(segment + 1);
        return start.lerp(end, localT);
    }
}