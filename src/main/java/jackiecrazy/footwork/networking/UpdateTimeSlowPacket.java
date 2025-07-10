package jackiecrazy.footwork.networking;

import jackiecrazy.footwork.capability.resources.CombatData;
import jackiecrazy.footwork.capability.timeslow.TimeSlowData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class UpdateTimeSlowPacket {
    int e;
    double spd;

    public UpdateTimeSlowPacket(int ent, double c) {
        e = ent;
        spd = c;
    }

    public static class UpdateClientEncoder implements BiConsumer<UpdateTimeSlowPacket, FriendlyByteBuf> {

        @Override
        public void accept(UpdateTimeSlowPacket updateClientResourcePacket, FriendlyByteBuf packetBuffer) {
            packetBuffer.writeInt(updateClientResourcePacket.e);
            packetBuffer.writeDouble(updateClientResourcePacket.spd);
        }
    }

    public static class UpdateClientDecoder implements Function<FriendlyByteBuf, UpdateTimeSlowPacket> {

        @Override
        public UpdateTimeSlowPacket apply(FriendlyByteBuf packetBuffer) {
            return new UpdateTimeSlowPacket(packetBuffer.readInt(), packetBuffer.readDouble());
        }
    }

    public static class UpdateClientHandler implements BiConsumer<UpdateTimeSlowPacket, Supplier<NetworkEvent.Context>> {

        @Override
        public void accept(UpdateTimeSlowPacket updateClientResourcePacket, Supplier<NetworkEvent.Context> contextSupplier) {
            contextSupplier.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                ClientLevel world = Minecraft.getInstance().level;
                if (world != null) {
                    Entity entity = world.getEntity(updateClientResourcePacket.e);
                    if (entity instanceof LivingEntity) TimeSlowData.getCap(entity).setRawSpeed(updateClientResourcePacket.spd);
                }
            }));
            contextSupplier.get().setPacketHandled(true);
        }
    }
}
