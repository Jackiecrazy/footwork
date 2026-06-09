package jackiecrazy.footwork.networking;

import jackiecrazy.footwork.move.motionframe.render.ItemPreTransforms;
import jackiecrazy.footwork.move.motionframe.render.RenderNode;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class SyncTagDataPacket {
    private static final FriendlyByteBuf.Writer<TagKey<Item>> item = (f, item) -> f.writeResourceLocation(item.location());
    private static final FriendlyByteBuf.Writer<RenderNode> info = RenderNode.SERIALIZER::write;

    private static final FriendlyByteBuf.Reader<TagKey<Item>> ritem = f -> ItemTags.create(f.readResourceLocation());;
    private static final FriendlyByteBuf.Reader<RenderNode> rinfo = RenderNode.SERIALIZER::read;
    private final Map<TagKey<Item>, RenderNode> map;

    public SyncTagDataPacket(Map<TagKey<Item>, RenderNode> map) {
        this.map = map;
    }

    public static void encode(SyncTagDataPacket packet, FriendlyByteBuf packetBuffer) {
            packetBuffer.writeMap(packet.map, item, info);
        }

    public static SyncTagDataPacket decode(FriendlyByteBuf packetBuffer) {
            return new SyncTagDataPacket(packetBuffer.readMap(ritem, rinfo));
    }

    public static void handle(SyncTagDataPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {

            //prevent client overriding server
            if (contextSupplier.get().getDirection() == NetworkDirection.LOGIN_TO_CLIENT||contextSupplier.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT)
                contextSupplier.get().enqueueWork(() -> {
                    ItemPreTransforms.clientTagOverride(packet.map);
                });
            contextSupplier.get().setPacketHandled(true);
    }
}
