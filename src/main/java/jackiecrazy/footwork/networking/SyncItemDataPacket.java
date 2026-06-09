package jackiecrazy.footwork.networking;

import jackiecrazy.footwork.move.motionframe.render.ItemPreTransforms;
import jackiecrazy.footwork.move.motionframe.render.RenderNode;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.Item;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public class SyncItemDataPacket {
    private static final FriendlyByteBuf.Writer<Item> item = (f, item) -> f.writeResourceLocation(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(item)));
    private static final FriendlyByteBuf.Writer<RenderNode> info = RenderNode.SERIALIZER::write;

    private static final FriendlyByteBuf.Reader<Item> ritem = friendlyByteBuf -> ForgeRegistries.ITEMS.getValue(friendlyByteBuf.readResourceLocation());
    private static final FriendlyByteBuf.Reader<RenderNode> rinfo = RenderNode.SERIALIZER::read;
    private final Map<Item, RenderNode> map;

    public SyncItemDataPacket(Map<Item, RenderNode> map) {
        this.map = map;
    }

    public static void encode(SyncItemDataPacket packet, FriendlyByteBuf packetBuffer) {
        packetBuffer.writeMap(packet.map, item, info);
    }

    public static SyncItemDataPacket decode(FriendlyByteBuf packetBuffer) {
        final Map<Item, RenderNode> huh = packetBuffer.readMap(ritem, rinfo);
        return new SyncItemDataPacket(huh);
    }

    public static void handle(SyncItemDataPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {

        //prevent client overriding server
        if (contextSupplier.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT || contextSupplier.get().getDirection() == NetworkDirection.LOGIN_TO_CLIENT) {
            contextSupplier.get().enqueueWork(() -> {
                ItemPreTransforms.clientWeaponOverride(packet.map);
            });
        }
        contextSupplier.get().setPacketHandled(true);
    }
}
