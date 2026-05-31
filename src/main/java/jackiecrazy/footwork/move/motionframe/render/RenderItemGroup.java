package jackiecrazy.footwork.move.motionframe.render;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
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

    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();
        ListTag list = new ListTag();

        for (ItemNode node : nodes()) {
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
        ItemNode[] nodes = new ItemNode[list.size()];

        for (int i = 0; i < list.size(); i++) {
            nodes[i] = ItemNode.fromTag(list.getCompound(i));
        }

        return new RenderItemGroup(nodes);
    }
}
