package jackiecrazy.footwork.move.motionframe.render;

import jackiecrazy.footwork.Footwork;
import jackiecrazy.footwork.utils.NBTUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public sealed interface RenderNode
        permits RenderNode.BaseItemNode, RenderNode.BlockNode, RenderNode.ItemNode {

    Vec3 rotation();
    Vec3 translation();

    // ====================== SERIALIZER ======================
    EntityDataSerializer<RenderNode> SERIALIZER = new EntityDataSerializer<>() {

        @Override
        public void write(FriendlyByteBuf buf, RenderNode node) {
            NodeType type = node.getType();
            buf.writeEnum(type);

            switch (type) {
                case BASE -> {
                }
                case BLOCK -> {
                    BlockNode n = (BlockNode) node;
                    buf.writeVarInt(Block.getId(n.state()));
                }
                case ITEM -> {
                    ItemNode n = (ItemNode) node;
                    buf.writeItem(n.stack());
                }
            }
            buf.writeVector3f(node.rotation().toVector3f());
            buf.writeVector3f(node.translation().toVector3f());
        }

        @Override
        public RenderNode read(FriendlyByteBuf buf) {
            NodeType type = buf.readEnum(NodeType.class);

            return switch (type) {
                case BASE -> new BaseItemNode(
                        new Vec3(buf.readVector3f()),
                        new Vec3(buf.readVector3f())
                );
                case BLOCK -> new BlockNode(
                        Block.stateById(buf.readVarInt()),
                        new Vec3(buf.readVector3f()),
                        new Vec3(buf.readVector3f())
                );
                case ITEM -> new ItemNode(
                        buf.readItem(),
                        new Vec3(buf.readVector3f()),
                        new Vec3(buf.readVector3f())
                );
            };
        }

        @Override
        public RenderNode copy(RenderNode node) {
            return switch (node.getType()) {
                case BASE -> {
                    BaseItemNode n = (BaseItemNode) node;
                    yield new BaseItemNode(n.rotation().scale(1), n.translation().scale(1));
                }
                case BLOCK -> {
                    BlockNode n = (BlockNode) node;
                    yield new BlockNode(n.state(), n.rotation().scale(1), n.translation().scale(1));
                }
                case ITEM -> {
                    ItemNode n = (ItemNode) node;
                    yield new ItemNode(n.stack().copy(), n.rotation().scale(1), n.translation().scale(1));
                }
            };
        }
    };

    // ====================== NBT ======================
    default CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();
        tag.putString("type", getType().name());

        switch (getType()) {
            case BASE -> {
                BaseItemNode n = (BaseItemNode) this;
            }
            case BLOCK -> {
                BlockNode n = (BlockNode) this;
                Tag stateTag = BlockState.CODEC.encodeStart(NbtOps.INSTANCE, n.state())
                        .getOrThrow(false, Footwork.LOGGER::error);
                tag.put("state", stateTag);
            }
            case ITEM -> {
                ItemNode n = (ItemNode) this;
                CompoundTag stackTag = new CompoundTag();
                n.stack().save(stackTag);
                tag.put("stack", stackTag);
            }
        }
        tag.put("rotation", NBTUtils.toTag(rotation()));
        tag.put("translation", NBTUtils.toTag(translation()));
        return tag;
    }

    static RenderNode fromTag(CompoundTag tag) {
        if (tag == null) return null;

        String typeStr = tag.getString("type");
        NodeType type = NodeType.valueOf(typeStr);

        return switch (type) {
            case BASE -> BaseItemNode.fromBasicTag(tag);
            case BLOCK -> BlockNode.fromBasicTag(tag);
            case ITEM -> ItemNode.fromBasicTag(tag);
        };
    }

    // ====================== Helper ======================
     default NodeType getType() {
        if (this instanceof BaseItemNode) return NodeType.BASE;
        if (this instanceof BlockNode) return NodeType.BLOCK;
        if (this instanceof ItemNode) return NodeType.ITEM;
        throw new IllegalStateException("Unknown RenderNode type: " + this.getClass());
    }

    enum NodeType {
        BASE, BLOCK, ITEM
    }

    // ====================== Records ======================
    record BaseItemNode(Vec3 rotation, Vec3 translation) implements RenderNode {
        static BaseItemNode fromBasicTag(CompoundTag tag) {
            return new BaseItemNode(
                    NBTUtils.fromTag(tag.getCompound("rotation")),
                    NBTUtils.fromTag(tag.getCompound("translation"))
            );
        }
    }

    record BlockNode(BlockState state, Vec3 rotation, Vec3 translation) implements RenderNode {
        static BlockNode fromBasicTag(CompoundTag tag) {
            BlockState state = BlockState.CODEC.parse(NbtOps.INSTANCE, tag.get("state"))
                    .getOrThrow(false, Footwork.LOGGER::error);

            return new BlockNode(
                    state,
                    NBTUtils.fromTag(tag.getCompound("rotation")),
                    NBTUtils.fromTag(tag.getCompound("translation"))
            );
        }
    }

    record ItemNode(ItemStack stack, Vec3 rotation, Vec3 translation) implements RenderNode {
        static ItemNode fromBasicTag(CompoundTag tag) {
            ItemStack stack = ItemStack.of(tag.getCompound("stack"));

            return new ItemNode(
                    stack,
                    NBTUtils.fromTag(tag.getCompound("rotation")),
                    NBTUtils.fromTag(tag.getCompound("translation"))
            );
        }
    }
}