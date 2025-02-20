package jackiecrazy.footwork.compat;


import net.neoforged.fml.ModList;

public class FootworkCompat {
    public static boolean patchouli;

    public static void checkCompatStatus() {
        patchouli = ModList.get().isLoaded("patchouli");
    }
}
