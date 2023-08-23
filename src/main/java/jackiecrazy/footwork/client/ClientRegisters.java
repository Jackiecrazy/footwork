package jackiecrazy.footwork.client;

import jackiecrazy.footwork.Footwork;
import jackiecrazy.footwork.client.particle.CustomSweepParticle;
import jackiecrazy.footwork.client.particle.FootworkParticles;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Footwork.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientRegisters {

    @SubscribeEvent
    public static void particles(RegisterParticleProvidersEvent e) {
        e.register(FootworkParticles.IMPACT.get(), set -> new CustomSweepParticle.SweepProvider(set, CustomSweepParticle.ROTATIONTYPE.NORMAL));
        e.register(FootworkParticles.LINE.get(), set -> new CustomSweepParticle.SweepProvider(set, CustomSweepParticle.ROTATIONTYPE.FLAT));
        e.register(FootworkParticles.CLEAVE.get(), set -> new CustomSweepParticle.SweepProvider(set, CustomSweepParticle.ROTATIONTYPE.NORMAL));
        e.register(FootworkParticles.SWEEP.get(), set -> new CustomSweepParticle.SweepProvider(set, CustomSweepParticle.ROTATIONTYPE.NORMAL));
        e.register(FootworkParticles.CIRCLE.get(), set -> new CustomSweepParticle.SweepProvider(set, CustomSweepParticle.ROTATIONTYPE.SUPERFLAT));
        e.register(FootworkParticles.SWEEP_LEFT.get(), set -> new CustomSweepParticle.SweepProvider(set, CustomSweepParticle.ROTATIONTYPE.NORMAL));
        e.register(FootworkParticles.BONK.get(), CustomSweepParticle.BonkProvider::new);
    }
}
