package jackiecrazy.footwork.client;

import jackiecrazy.footwork.Footwork;
import jackiecrazy.footwork.potion.FootworkEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

@EventBusSubscriber(modid = Footwork.MODID, value = Dist.CLIENT)
public class ClientEvents {
    public static boolean paralysis=false;
    @SubscribeEvent
    public static void paralyze(ClientTickEvent.Pre e) {
        final Minecraft minecraft = Minecraft.getInstance();
        AbstractClientPlayer p = minecraft.player;
        if (p == null) return;
        if (p.hasEffect(FootworkEffects.PARALYSIS) || p.hasEffect(FootworkEffects.SLEEP) || p.hasEffect(FootworkEffects.PETRIFY)) {
            //the big three!
            paralysis=true;
            if (!(minecraft.screen instanceof ParalysisScreen)) {
                minecraft.setScreen(new ParalysisScreen());
                minecraft.mouseHandler.grabMouse();
            }
        } else {
            paralysis=false;
            if (minecraft.screen instanceof ParalysisScreen) {
                minecraft.setScreen(null);
                minecraft.keyboardHandler.tick();
                minecraft.player.input.tick(false, 0);
            }
        }
    }
}
