package jackiecrazy.footwork.move.motionframe.render;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;
import org.jetbrains.annotations.NotNull;

public record RenderItemGroup(RenderNode... nodes) {
    public static EntityDataSerializer<RenderItemGroup> SERIALIZER = new EntityDataSerializer<>() {

        @Override
        public void write(FriendlyByteBuf buf, RenderItemGroup mm) {
            buf.writeInt(mm.nodes.length);
            for (RenderNode i : mm.nodes) {
                RenderNode.SERIALIZER.write(buf, i);
            }
        }

        @Override
        public @NotNull RenderItemGroup read(FriendlyByteBuf buf) {
            int length = buf.readInt();
            RenderNode[] mf = new RenderNode[length];
            for (int x = 0; x < length; x++) {
                mf[x] = RenderNode.SERIALIZER.read(buf);
            }
            return new RenderItemGroup(mf);
        }

        @Override
        public @NotNull RenderItemGroup copy(RenderItemGroup mm) {
            RenderNode[] ret = new RenderNode[mm.nodes.length];
            for (int x = 0; x < ret.length; x++) {
                ret[x] = RenderNode.SERIALIZER.copy(mm.nodes[x]);
            }
            return new RenderItemGroup(ret);
        }
    };

    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();
        ListTag list = new ListTag();

        for (RenderNode node : nodes()) {
            if (node != null) {
                list.add(node.toTag());
            }
        }

        tag.put("nodes", list);
        return tag;
    }

    public static RenderItemGroup fromTag(CompoundTag tag) {
        if (tag == null || !tag.contains("nodes", Tag.TAG_LIST)) {
            return new RenderItemGroup(); // empty group
        }

        ListTag list = tag.getList("nodes", Tag.TAG_COMPOUND);
        RenderNode[] nodes = new RenderNode[list.size()];

        for (int i = 0; i < list.size(); i++) {
            nodes[i] = RenderNode.fromTag(list.getCompound(i));
        }

        return new RenderItemGroup(nodes);
    }
}
