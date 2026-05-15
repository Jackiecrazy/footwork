package jackiecrazy.footwork.move.motionframe;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;
import org.jetbrains.annotations.NotNull;

public record RenderItemGroup(ItemNode... nodes) {
    public static EntityDataSerializer<RenderItemGroup> SERIALIZER = new EntityDataSerializer<>() {

        @Override
        public void write(FriendlyByteBuf buf, RenderItemGroup mm) {
            buf.writeInt(mm.nodes.length);
            for (ItemNode i : mm.nodes) {
                ItemNode.SERIALIZER.write(buf, i);
            }
        }

        @Override
        public @NotNull RenderItemGroup read(FriendlyByteBuf buf) {
            int length = buf.readInt();
            ItemNode[] mf = new ItemNode[length];
            for (int x = 0; x < length; x++) {
                mf[x] = ItemNode.SERIALIZER.read(buf);
            }
            return new RenderItemGroup(mf);
        }

        @Override
        public @NotNull RenderItemGroup copy(RenderItemGroup mm) {
            ItemNode[] ret = new ItemNode[mm.nodes.length];
            for (int x = 0; x < ret.length; x++) {
                ret[x] = ItemNode.SERIALIZER.copy(mm.nodes[x]);
            }
            return new RenderItemGroup(ret);
        }
    };
}
