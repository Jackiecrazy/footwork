package jackiecrazy.footwork.move.motionframe;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.util.Mth;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.joml.*;

import java.lang.Math;

public class MotionFrame {
    public static final EntityDataSerializer<MotionFrame> SERIALIZER = new EntityDataSerializer<>() {
        @Override
        public void write(FriendlyByteBuf buf, MotionFrame frame) {
            buf.writeVector3f(frame.direction.toVector3f());
            buf.writeVector3f(frame.offset.toVector3f());//fixme why do I not automatically orient?
            buf.writeDouble(frame.renderRotation.x);
            buf.writeDouble(frame.renderRotation.y);
            buf.writeDouble(frame.renderRotation.z);
            buf.writeDouble(frame.renderRotation.w);
        }

        @Override
        public MotionFrame read(FriendlyByteBuf buf) {
            return new MotionFrame(new Vec3(buf.readVector3f()), new Vec3(buf.readVector3f()), new Quaternionf(buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readDouble()));
        }

        @Override
        public MotionFrame copy(MotionFrame mf) {
            return new MotionFrame(mf.direction.scale(1), mf.offset.scale(1), new Quaternionf(mf.renderRotation), mf.effects);
        }
    };
    private Vec3 direction = new Vec3(0, 0, 1);
    private Vec3 offset = new Vec3(0, 0, 1);
    private Quaternionf renderRotation;//this is not in snake_case because this is usually synthesized later
    private FrameEffects effects;

    public MotionFrame() {
        //set quaternion to null explicitly so the json adapter fills it in later
        this(new Vec3(0, 0, 1), new Vec3(0, 0, 1), (Quaternionf) null);
    }

    public MotionFrame(Vec3 dir, Vec3 offset) {
        this(dir, offset, 0);
    }

    public MotionFrame(Vec3 dir, Vec3 offset, int rotation) {
        this(dir, offset, new Vector4d(dir.x, dir.y, dir.z, rotation));
    }

    public MotionFrame(Vec3 direction, Vec3 offset, Quaternionf render_orientation, FrameEffects effects) {
        this.direction = direction;
        this.offset = offset;
        this.renderRotation = render_orientation;
        this.effects = effects;
    }

    public MotionFrame(Vec3 direction, Vec3 offset, Quaternionf render_orientation) {
        this(direction, offset, render_orientation, null);
    }

    public MotionFrame(Vec3 dir, Vec3 offset, Vector4d orthodox) {
        this(dir, offset, buildLocalRotation(orthodox));
    }

    public MotionFrame(Vec3 direction, Vec3 offset, int i, FrameEffects effects) {
        this(direction, offset, i);
        this.effects = effects;
    }

    public static Quaternionf buildLocalRotation(Vector4d o) {
        double rollDeg = o.w;
        Vec3 fwd = new Vec3(-o.x, o.y, o.z).normalize();

        // Build rotation from +Z → direction
        Quaternionf q = new Quaternionf()//.rotateAxis((float) rollDeg, new Vector3f(0, 0, 1))
                .rotateTo(0, 0, 1, (float) fwd.x, (float) fwd.y, (float) fwd.z);

        // Apply axial roll around local forward
        if (rollDeg != 0.0) {
            q.rotateAxis(
                    (float) Math.toRadians(rollDeg),
                    0, 0, 1
            );
        }

        return q;
    }

    public static Quaternionf lookQuatFromVec(Vec3 forward) {
        Vec3 f = forward.normalize();

        Vec3 up = Math.abs(f.y) > 0.999
                ? new Vec3(0, 0, 1)   // fallback up
                : new Vec3(0, 1, 0);

        Vec3 r = up.cross(f).normalize();
        up = f.cross(r).normalize();

        Matrix3f m = new Matrix3f(
                (float) r.x, (float) r.y, (float) r.z,
                (float) up.x, (float) up.y, (float) up.z,
                (float) f.x, (float) f.y, (float) f.z
        );

        return new Quaternionf().setFromNormalized(m);
    }

    /**
     * Lerp angles (in degrees) across wrap boundaries cleanly.
     */
    private static float lerpAngleDeg(double from, double to, double partial) {
        double delta = Mth.wrapDegrees(to - from);
        return (float) (from + delta * partial);
    }

    public Vec3 direction() {
        return direction;
    }

    public Vec3 offset() {
        return offset;
    }

    public Quaternionf renderOrientation() {
        return renderRotation;
    }

    public FrameEffects effects() {
        return effects;
    }

    public MotionFrame setEffects(FrameEffects e){
        effects=e;//.clone();
        return this;
    }

    public Vec3 resolveTargetOffset(Tuple<Vec3, Vec3> bundle, Vec3 defaultOffset, double range) {
        return resolveTargetOffset(bundle.getA(), bundle.getB(), defaultOffset, range);
    }

    public Vec3 resolveTargetOffset() {
        return resolveTargetOffset(Vec3.ZERO, new Vec3(0, 0, 1), Vec3.ZERO, 1);
    }

    public Vec3 resolveTargetOffset(Vec3 position, Vec3 forward, Vec3 defaultOffset, double range) {
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

        return position.add(adjustedDefault).add(offsetWorld);
    }

//    public MotionFrame lerp(MotionFrame with, double partial) {
//        Vector4d copy = new Vector4d(renderOrientation);
//        return new MotionFrame(direction.lerp(with.direction, partial), offset.lerp(with.offset, partial), copy.lerp(with.renderOrientation, partial));
//    }

    public Vec3 resolveTargetOffset(Entity referent, Vec3 defaultOffset, double range) {
        Vec3 forward = referent.getLookAngle().normalize();
        if (forward.lengthSqr() < 0.0001) forward = new Vec3(0, 0, 1); // fallback

        return resolveTargetOffset(referent.position().add(0, 1, 0), forward, defaultOffset, range);
    }

    public MotionFrame lerp(MotionFrame with, double partial) {
        Vec3 dir = this.direction.lerp(with.direction, partial);
        Vec3 offset = this.offset.lerp(with.offset, partial);

        Quaternionf from = this.renderRotation;
        Quaternionf to = with.renderRotation;
        Quaternionf out = from.slerp(to, (float) partial, new Quaternionf());

        return new MotionFrame(dir, offset, out, effects);
    }

    public MotionFrame flip(){
        Quaternionf reconstruct =new Quaternionf(renderOrientation());
        reconstruct.x*=-1;
        reconstruct.w*=-1;
        return new MotionFrame(direction.multiply(-1,1,1), offset.multiply(-1,1,1), reconstruct, effects);
    }

    public void _setRenderRotationRaw(Quaternionf spin){
        renderRotation=spin;
    }

    public MotionFrame setRenderRotation(Quaternionf spin){
        return new MotionFrame(direction, offset, spin, effects);
    }

}
