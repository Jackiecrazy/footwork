package jackiecrazy.footwork.client;

import jackiecrazy.footwork.Footwork;
import jackiecrazy.footwork.potion.FootworkEffects;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.KeyboardInput;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.awt.event.MouseEvent;

@Mod.EventBusSubscriber(modid = Footwork.MODID)
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
