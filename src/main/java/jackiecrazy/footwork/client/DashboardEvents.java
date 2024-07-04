package jackiecrazy.footwork.client;

import jackiecrazy.footwork.Footwork;
import jackiecrazy.footwork.client.screen.dashboard.PonderingOrb;
import jackiecrazy.footwork.compat.PatchouliCompat;
import jackiecrazy.footwork.compat.FootworkCompat;
import jackiecrazy.footwork.event.DashboardEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(value = Dist.CLIENT, modid = Footwork.MODID)
public class DashboardEvents {
    private static final ResourceLocation MANUAL = ResourceLocation.fromNamespaceAndPath(Footwork.MODID, "textures/gui/manual.png");
    @SubscribeEvent
    public static void add(DashboardEvent e) {
        Player player = e.getPlayer();
        if (FootworkCompat.patchouli)
            e.addThought(new PonderingOrb(e.getScreen(), MANUAL, a -> PatchouliCompat.openManualClient(), Component.translatable("footwork.dashboard.manual")));

    }
}
