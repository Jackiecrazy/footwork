package jackiecrazy.footwork.handler;

import jackiecrazy.footwork.Footwork;
import jackiecrazy.footwork.capability.resources.CombatData;
import jackiecrazy.footwork.entity.ai.CompelledVengeanceGoal;
import jackiecrazy.footwork.entity.ai.FearGoal;
import jackiecrazy.footwork.entity.ai.NoGoal;
import jackiecrazy.footwork.potion.FootworkEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Footwork.MODID)
public class EntityHandler {

    @SubscribeEvent
    public static void takeThis(EntityJoinWorldEvent e) {
        if (e.getEntity() instanceof Mob) {
            Mob mob = (Mob) e.getEntity();
            mob.f_21345_.m_25352_(-1, new NoGoal(mob));
            mob.f_21346_.m_25352_(-1, new NoGoal(mob));
            if (e.getEntity() instanceof PathfinderMob) {
                PathfinderMob creature = (PathfinderMob) e.getEntity();
                mob.f_21346_.m_25352_(0, new CompelledVengeanceGoal(creature));
                mob.f_21346_.m_25352_(0, new FearGoal(creature));
            }
        }
    }

    @SubscribeEvent
    public static void tickMobs(LivingEvent.LivingUpdateEvent e) {
        LivingEntity elb = e.getEntityLiving();
        if (!elb.f_19853_.f_46443_) {
            if (elb.m_21023_(FootworkEffects.PETRIFY.get())) {
                elb.m_146926_(elb.f_19860_);
                elb.m_146922_(elb.f_19859_);
                elb.f_20885_ = elb.f_20886_;
                elb.m_20334_(0, 0, 0);
            }
        }
    }

    @SubscribeEvent
    public static void noJump(LivingEvent.LivingJumpEvent e) {
        if (!(e.getEntityLiving() instanceof Player) && CombatData.getCap(e.getEntityLiving()).getStaggerTime() > 0) {
            e.getEntityLiving().m_20334_(0, 0, 0);
        }
    }

}
