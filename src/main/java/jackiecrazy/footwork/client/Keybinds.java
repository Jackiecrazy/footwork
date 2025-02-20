package jackiecrazy.footwork.client;

import com.mojang.blaze3d.platform.InputConstants;
import jackiecrazy.footwork.Footwork;
import jackiecrazy.footwork.capability.resources.CombatData;
import jackiecrazy.footwork.capability.resources.ICombatCapability;
import jackiecrazy.footwork.client.screen.dashboard.DashboardScreen;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = Footwork.MODID)
public class Keybinds {
    public static final KeyMapping SELECT = new KeyMapping("footwork.trance", KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_V, "key.categories.wardance");
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void handleInputEvent(InputEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        ICombatCapability itsc = CombatData.getCap(mc.player);
        if (SELECT.getKeyConflictContext().isActive() && SELECT.consumeClick() && mc.player.isAlive()) {
            mc.setScreen(new DashboardScreen(mc.player));
            //mc.setScreen(new ScrollScreen(WarSkills.VITAL_STRIKE.get(), WarSkills.SUPLEX.get(), WarSkills.DEMON_HUNTER.get(), WarSkills.SIFU.get(), WarSkills.GOLD_RUSH.get(), WarSkills.UNSTABLE_SPIRIT.get(), WarSkills.CURSE_OF_ECHOES.get(), WarSkills.PRIDEFUL_MIGHT.get(), WarSkills.BACKFLIP.get(), WarSkills.MOMENTUM.get(), WarSkills.MONTANTE.get(), WarSkills.PHANTOM_DIVE.get(), WarSkills.SHIELD_CRUSH.get()));
        }
    }
}
