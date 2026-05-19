package jackiecrazy.footwork.networking;

import jackiecrazy.footwork.items.AnimationTesterItem;
import jackiecrazy.footwork.move.argument.misc.RenderItemArgument;
import jackiecrazy.footwork.move.motionframe.ItemNode;
import jackiecrazy.footwork.move.motionframe.RenderItemGroup;
import jackiecrazy.footwork.move.utils.ArgumentContext;
import jackiecrazy.footwork.utils.ActionJsonAdapters;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

import static jackiecrazy.footwork.items.AnimationTesterItem.*;

public class AnimationTesterSavePacket {
    private final InteractionHand hand;
    private final String renderData;
    private final String motionJson;
    private final boolean isManager;

    public AnimationTesterSavePacket(InteractionHand h, String renderData, String motionJson, boolean isManager) {
        this.hand = h;
        this.renderData = renderData;
        this.motionJson = motionJson;
        this.isManager = isManager;
    }

    // Constructor for reading + encode/decode methods using FriendlyByteBuf
    public static void encode(AnimationTesterSavePacket msg, FriendlyByteBuf buf) {
        buf.writeBoolean(msg.hand==InteractionHand.MAIN_HAND);
        buf.writeUtf(msg.renderData, 8192);
        buf.writeUtf(msg.motionJson, 16384);
        buf.writeBoolean(msg.isManager);
    }

    public static AnimationTesterSavePacket decode(FriendlyByteBuf buf) {
        return new AnimationTesterSavePacket(
            buf.readBoolean()?InteractionHand.MAIN_HAND:InteractionHand.OFF_HAND,
            buf.readUtf(8192),
            buf.readUtf(16384),
            buf.readBoolean()
        );
    }

    public static void handle(AnimationTesterSavePacket msg, Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();
            if (player == null) return;

            ItemStack held = player.getItemInHand(msg.hand);
            if (held.getItem() instanceof AnimationTesterItem) {
                CompoundTag tag = held.getOrCreateTag();

                // Parse upper box
                try {
                    try{
                        ItemStack itemStack = new ItemStack(BuiltInRegistries.ITEM.get(new ResourceLocation(msg.renderData)));
                        // Wrap as single-node group
                        RenderItemGroup group = new RenderItemGroup(new ItemNode(itemStack, Vec3.ZERO, Vec3.ZERO));
                        tag.put(NBT_RENDER_GROUP, group.toTag());
                    }catch (Exception e){
                        // List of objects -> your deserializer
                        RenderItemArgument group = ActionJsonAdapters.gson.fromJson(msg.renderData, RenderItemArgument.class);
                        tag.put(NBT_RENDER_GROUP, group.resolve(new ArgumentContext(player, player)).toTag()); // implement toNbt()
                    }
                } catch (Exception e) {
                    player.sendSystemMessage(Component.literal("Failed to parse RenderGroup: " + e.getMessage()));
                    e.printStackTrace();
                }

                tag.putString("inputted_item", msg.renderData);
                tag.putString(NBT_MOTION_DATA, msg.motionJson);
                tag.putBoolean(NBT_IS_MANAGER, msg.isManager);
            }
        });
        ctx.setPacketHandled(true);
    }
}