package jackiecrazy.footwork.move.motionframe;

import io.netty.buffer.ByteBufUtil;
import jackiecrazy.footwork.utils.EasingFunction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public interface MotionManager {

    EntityDataSerializer<MotionManager> SERIALIZER = new EntityDataSerializer<>() {

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
            return new MotionManagers.DefinitionMM(new MotionGroup(mf, EasingFunction.LINEAR, dur));
        }

        @Override
        public MotionManager copy(MotionManager mm) {
            return new MotionManagers.FixedMM(new MotionFrame(mm.getStartFrame().direction(), mm.getStartFrame().offset(), mm.getStartFrame().renderOrientation()), mm.getDuration());
        }
    };

    MotionFrame getNextPoint(int elapsedTicks);

    int getDuration();

    default boolean hasEnded(int atTick) {
        return atTick > getDuration();
    }

    default MotionFrame getStartFrame() {
        return getNextPoint(0);
    }

    default MotionFrame getEndFrame() {
        return getNextPoint(getDuration());
    }
}
