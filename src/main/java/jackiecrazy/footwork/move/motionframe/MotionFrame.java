package jackiecrazy.footwork.move.motionframe;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector4d;

public record MotionFrame(Vec3 direction, Vec3 offset, Vector4d renderOrientation) {

    public MotionFrame(Vec3 dir, Vec3 offset) {
        this(dir, offset, 0);
    }

    public MotionFrame(Vec3 dir, Vec3 offset, int rotation) {
        this(dir, offset, new Vector4d(dir.x, dir.y, dir.z, rotation));
    }

    /**
     * Converts a local-space offset (x, y, z) into world-space coordinates
     * using the player's current look direction as the forward (Z) axis.
     *
     * @return World-space vector to add to the player position
     */
    public Vec3 rebaseToVector(Vec3 basis) {
        Vec3 offset = offset();
        Vec3 forward = basis.normalize(); // Z axis
        Vec3 worldUp = new Vec3(0, 1, 0);         // world Y

        // Compute right (X axis)
        Vec3 right = forward.cross(worldUp).normalize(); // right-handed X
        if (right.lengthSqr() < 1e-4) {
            // Handle gimbal lock (looking straight up/down)
            right = new Vec3(1, 0, 0);
        }

        // Recompute up to ensure orthogonality (angled upward from look vector)
        Vec3 up = right.cross(forward).normalize(); // Y axis

        // Combine basis vectors
        return right.scale(offset.x)
                .add(up.scale(offset.y))
                .add(forward.scale(offset.z));
    }

    public Vec3 resolveTargetOffset(Entity referent, Vec3 defaultOffset, double range) {
        Vec3 forward = referent.getLookAngle().normalize();
        if (forward.lengthSqr() < 0.0001) forward = new Vec3(0, 0, 1); // fallback

        // Create right and up basis vectors
        Vec3 globalUp = new Vec3(0, 1, 0);
        Vec3 right = forward.cross(globalUp).normalize();
        Vec3 up = right.cross(forward).normalize();  // Ensure orthogonal

        // step 0: find the default offset position to begin calculations
        Vec3 adjustedDefault = forward.scale(defaultOffset.z)
                .add(right.scale(defaultOffset.x))
                .add(up.scale(defaultOffset.y));

        // --- Step 1: Modify the forward vector using the direction vector ---
        // The direction vector says "rotate forward this much toward right and up"
        Vec3 lookAdjusted = forward.scale(direction.z)
                .add(right.scale(direction.x))
                .add(up.scale(direction.y))
                .normalize();

        // Rebuild the new right and up basis based on the adjusted look vector
        Vec3 newRight = lookAdjusted.cross(globalUp).normalize();
        Vec3 newUp = newRight.cross(lookAdjusted).normalize();

        // --- Step 2: Apply the offset in this modified direction space ---
        Vec3 offsetWorld = newRight.scale(offset.x * range)
                .add(newUp.scale(offset.y * range))
                .add(lookAdjusted.scale(offset.z * range));

        Vec3 handOrigin = referent.position().add(0, 1.0, 0); // approx. hand height

        return handOrigin.add(adjustedDefault).add(offsetWorld);
    }

    public MotionFrame lerp(MotionFrame with, double partial) {
        Vector4d copy = new Vector4d(renderOrientation);
        return new MotionFrame(direction.lerp(with.direction, partial), offset.lerp(with.offset, partial), copy.lerp(with.renderOrientation, partial));
    }
}
