package jackiecrazy.footwork.compat;

import net.minecraftforge.fml.ModList;

public class FootworkCompat {
    public static boolean patchouli;

    public static void checkCompatStatus() {
        patchouli = ModList.get().isLoaded("patchouli");
    }
}
