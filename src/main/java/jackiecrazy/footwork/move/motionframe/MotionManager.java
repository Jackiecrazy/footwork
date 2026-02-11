package jackiecrazy.footwork.move.motionframe;

import jackiecrazy.footwork.utils.EasingFunctionEnum;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public abstract class MotionManager {
    public MotionManager setAngularVelocity(Vector3f angularVelocity) {
        this.angularVelocity = angularVelocity;
        return this;
    }

    private Vector3f angularVelocity = new Vector3f();       // radians per tick, axis * speed
    protected Quaternionf getRuntimeRotation(int ticks, float partialTick, Quaternionf out) {
        float speed = angularVelocity.length();
        if (speed < 1e-6f) {
            return out.identity();
        }

        Vector3f axis = new Vector3f(angularVelocity).normalize();

        // Total angle = ω × time
        float angle = speed * (ticks + partialTick);

        return out.fromAxisAngleRad(axis, angle);
    }

    protected Quaternionf resolveFinalRotation(MotionFrame frame, int ticks, float partial, Quaternionf out) {
        Quaternionf base = new Quaternionf(frame.renderOrientation());
        Quaternionf spin = getRuntimeRotation(ticks, partial, new Quaternionf());

        return out.set(base).mul(spin).normalize(); // local space spin
    }



    public static EntityDataSerializer<MotionManager> SERIALIZER = new EntityDataSerializer<>() {

        @Override
        public void write(FriendlyByteBuf buf, MotionManager mm) {
            //write 20 frames in total, spread out over the duration. If duration<20, write as many as needed
            buf.writeInt(mm.getDuration());
            final int increments = Math.max(1, mm.getDuration() / 20);
            int numOfFrames = mm.getDuration() / (increments + 1);
            buf.writeInt(numOfFrames);
            for (int x = 0; x < numOfFrames; x += increments) {
                MotionFrame.SERIALIZER.write(buf, mm.getNextPoint(x));
            }
        }

        @Override
        public MotionManager read(FriendlyByteBuf buf) {
            int dur = buf.readInt();
            List<MotionFrame> mf = new ArrayList<>();
            int max = buf.readInt();
            for (int x = 0; x < max; x++) {
                mf.add(MotionFrame.SERIALIZER.read(buf));
            }
            return new MotionManagers.DefinitionMM(new MotionGroup(mf, EasingFunctionEnum.LINEAR, dur));
        }

        @Override
        public MotionManager copy(MotionManager mm) {
            return new MotionManagers.FixedMM(new MotionFrame(mm.getStartFrame().direction(), mm.getStartFrame().offset(), mm.getStartFrame().renderOrientation()), mm.getDuration());
        }
    };

    public abstract MotionFrame getNextPoint(int elapsedTicks);

    public abstract int getDuration();

    public boolean hasEnded(int atTick) {
        return atTick > getDuration();
    }

    public MotionFrame getStartFrame() {
        return getNextPoint(0);
    }

    public MotionFrame getEndFrame() {
        return getNextPoint(getDuration());
    }
}
