package jackiecrazy.footwork.move.motionframe.render;

import jackiecrazy.footwork.Footwork;
import jackiecrazy.footwork.utils.NBTUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.world.entity.monster.warden.WardenAi;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;
import org.openjdk.nashorn.internal.ir.EmptyNode;

public sealed interface RenderNode
    permits RenderNode.ItemNode, RenderNode.BlockNode, RenderNode.EmptyNode {
    Vec3 rotation();
    Vec3 translation();

    record EmptyNode(Vec3 rotation, Vec3 translation) implements RenderNode {
        public static EntityDataSerializer<EmptyNode> SERIALIZER = new EntityDataSerializer<>() {

            @Override
            public void write(FriendlyByteBuf buf, EmptyNode mm) {
                buf.writeVector3f(mm.rotation.toVector3f());
                buf.writeVector3f(mm.translation.toVector3f());
            }

            @Override
            public EmptyNode read(FriendlyByteBuf buf) {
                return new EmptyNode(new Vec3(buf.readVector3f()), new Vec3(buf.readVector3f()));
            }

            @Override
            public EmptyNode copy(EmptyNode mm) {
                return new EmptyNode( mm.rotation.scale(1), mm.translation.scale(1));
            }
        };

        public CompoundTag toTag() {
            CompoundTag tag = new CompoundTag();

            // Rotation Vec3
            tag.put("rotation", NBTUtils.toTag(rotation));

            // Translation Vec3
            tag.put("translation", NBTUtils.toTag(translation));

            return tag;
        }

        public static EmptyNode fromTag(CompoundTag tag) {
            if (tag == null) return null;

            Vec3 rotation = NBTUtils.fromTag(tag.getCompound("rotation"));
            Vec3 translation = NBTUtils.fromTag(tag.getCompound("translation"));

            return new EmptyNode(rotation, translation);
        }
    }

    record BlockNode(BlockState state, Vec3 rotation, Vec3 translation) implements RenderNode {
        public static EntityDataSerializer<BlockNode> SERIALIZER = new EntityDataSerializer<>() {

            @Override
            public void write(FriendlyByteBuf buf, BlockNode mm) {
                buf.writeVarInt(Block.getId(mm.state));
                buf.writeVector3f(mm.rotation.toVector3f());
                buf.writeVector3f(mm.translation.toVector3f());
            }

            @Override
            public BlockNode read(FriendlyByteBuf buf) {
                return new BlockNode(Block.stateById(buf.readVarInt()), new Vec3(buf.readVector3f()), new Vec3(buf.readVector3f()));
            }

            @Override
            public BlockNode copy(BlockNode mm) {
                return new BlockNode(mm.state(), mm.rotation.scale(1), mm.translation.scale(1));
            }
        };

        public CompoundTag toTag() {
            CompoundTag tag = new CompoundTag();

            // ItemStack
            Tag stackTag = BlockState.CODEC.encodeStart(
                    NbtOps.INSTANCE,
                    state
            ).getOrThrow(false, Footwork.LOGGER::error);
            tag.put("state", stackTag);

            // Rotation Vec3
            tag.put("rotation", NBTUtils.toTag(rotation));

            // Translation Vec3
            tag.put("translation", NBTUtils.toTag(translation));

            return tag;
        }

        public static BlockNode fromTag(CompoundTag tag) {
            if (tag == null) return null;

            BlockState state = BlockState.CODEC.parse(
                    NbtOps.INSTANCE,
                    tag.get("state")
            ).getOrThrow(false, Footwork.LOGGER::error);

            Vec3 rotation = NBTUtils.fromTag(tag.getCompound("rotation"));
            Vec3 translation = NBTUtils.fromTag(tag.getCompound("translation"));

            return new BlockNode(state, rotation, translation);
        }
    }
    record ItemNode(ItemStack stack, Vec3 rotation, Vec3 translation) implements RenderNode {
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
}