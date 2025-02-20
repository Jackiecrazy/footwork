package jackiecrazy.footwork.client;

import jackiecrazy.footwork.Footwork;
import jackiecrazy.footwork.potion.FootworkEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;

@Mod.EventBusSubscriber(modid = Footwork.MODID, value = Dist.CLIENT)
public class ClientEvents {
    public static boolean paralysis=false;
    @SubscribeEvent
    public static void paralyze(TickEvent.ClientTickEvent e) {
        final Minecraft minecraft = Minecraft.getInstance();
        AbstractClientPlayer p = minecraft.player;
        if (p == null) return;
        if (p.hasEffect(FootworkEffects.PARALYSIS.get()) || p.hasEffect(FootworkEffects.SLEEP.get()) || p.hasEffect(FootworkEffects.PETRIFY.get())) {
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
