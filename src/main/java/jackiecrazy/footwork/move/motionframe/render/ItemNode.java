package jackiecrazy.footwork.move.motionframe.render;

import jackiecrazy.footwork.utils.NBTUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public record ItemNode(ItemStack stack, Vec3 rotation, Vec3 translation) {
    public static EntityDataSerializer<ItemNode> SERIALIZER = new EntityDataSerializer<>() {

        @Override
        public void write(FriendlyByteBuf buf, ItemNode mm) {
            buf.writeItem(mm.stack);
            buf.writeVector3f(mm.rotation.toVector3f());
            buf.writeVector3f(mm.translation.toVector3f());
        }

        @Override
        public ItemNode read(FriendlyByteBuf buf) {
            return new ItemNode(buf.readItem(), new Vec3(buf.readVector3f()), new Vec3(buf.readVector3f()));
        }

        @Override
        public ItemNode copy(ItemNode mm) {
            return new ItemNode(mm.stack().copy(), mm.rotation.scale(1), mm.translation.scale(1));
        }
    };

    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();

        // ItemStack
        CompoundTag stackTag = new CompoundTag();
        stack().save(stackTag);           // or node.stack().serialize() in some versions
        tag.put("stack", stackTag);

        // Rotation Vec3
        tag.put("rotation", NBTUtils.toTag(rotation));

        // Translation Vec3
        tag.put("translation", NBTUtils.toTag(translation));

        return tag;
    }

    public static ItemNode fromTag(CompoundTag tag) {
        if (tag == null) return null;

        CompoundTag stackTag = tag.getCompound("stack");
        ItemStack stack = ItemStack.of(stackTag);   // or ItemStack.parse() in newer versions

        Vec3 rotation = NBTUtils.fromTag(tag.getCompound("rotation"));
        Vec3 translation = NBTUtils.fromTag(tag.getCompound("translation"));

        return new ItemNode(stack, rotation, translation);
    }
}
