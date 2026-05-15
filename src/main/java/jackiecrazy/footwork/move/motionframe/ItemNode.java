package jackiecrazy.footwork.move.motionframe;

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
}
