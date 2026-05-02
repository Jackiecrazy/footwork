package jackiecrazy.footwork.move.motionframe;

import jackiecrazy.footwork.utils.EasingFunctionEnum;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public abstract class MotionManager {
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
    private Vector3f angular_velocity = new Vector3f();       // radians per tick, axis * speed

    public MotionManager setAngularVelocity(Vector3f angularVelocity) {
        this.angular_velocity = angularVelocity;
        return this;
    }

    public Vector3f getSpin() {
        return angular_velocity;
    }

    public abstract MotionFrame getNextPoint(double elapsedTicks);

    public abstract int getDuration();

    public boolean hasEnded(int atTick, double atTimer) {
        return atTick > getDuration() * 1.5 || atTimer > getDuration();
    }

    public MotionFrame getStartFrame() {
        return getNextPoint(0);
    }

    public MotionFrame getEndFrame() {
        return getNextPoint(getDuration());
    }

    public abstract MotionManager flipFrames();
}
