package jackiecrazy.footwork.utils;

import jackiecrazy.footwork.Footwork;
import jackiecrazy.footwork.event.LuckEvent;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.TriState;

public class LuckUtils {
    public static boolean luckRoll(LivingEntity checker, float baseChance) {
        LuckEvent.Pre lre = new LuckEvent.Pre(checker, baseChance);
        NeoForge.EVENT_BUS.post(lre);
        boolean ret = Footwork.rand.nextFloat() < lre.getChance();
        if (lre.getResult() == TriState.FALSE) ret = false;
        if (lre.getResult() == TriState.TRUE) ret = true;
        LuckEvent.Post le = new LuckEvent.Post(checker, lre.getChance(), ret);
        NeoForge.EVENT_BUS.post(le);
        return ret;
    }
}
