package jackiecrazy.footwork.compat;

import net.minecraft.resources.ResourceLocation;

public class PatchouliCompat {
    private static final ResourceLocation MANUAL = ResourceLocation.parse("footwork:combat_manual");
    public static void openManualClient(){
        //PatchouliAPI.get().openBookGUI(MANUAL); TODO reenable when patchouli updates
    }
}
