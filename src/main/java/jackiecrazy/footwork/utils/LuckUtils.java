package jackiecrazy.footwork.utils;

import jackiecrazy.footwork.Footwork;
import jackiecrazy.footwork.event.LuckEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;

public class LuckUtils {
    public static boolean luckRoll(LivingEntity checker, float baseChance) {
        LuckEvent.Pre lre = new LuckEvent.Pre(checker, baseChance);
        MinecraftForge.EVENT_BUS.post(lre);
        boolean ret = Footwork.rand.nextFloat() < lre.getChance();
        if (lre.getResult() == Event.Result.DENY) ret = false;
        if (lre.getResult() == Event.Result.ALLOW) ret = true;
        LuckEvent.Post le = new LuckEvent.Post(checker, lre.getChance(), ret);
        MinecraftForge.EVENT_BUS.post(le);
        return ret;
    }
}
