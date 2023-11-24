package jackiecrazy.footwork.client;

import jackiecrazy.footwork.Footwork;
import jackiecrazy.footwork.client.particle.CustomSweepParticle;
import jackiecrazy.footwork.client.particle.FootworkParticles;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Footwork.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientRegisters {

    @SubscribeEvent
    public static void particles(RegisterParticleProvidersEvent e) {
        e.registerSpriteSet(FootworkParticles.IMPACT.get(), set -> new CustomSweepParticle.SweepProvider(set, CustomSweepParticle.ROTATIONTYPE.NORMAL));
        e.registerSpriteSet(FootworkParticles.LINE.get(), set -> new CustomSweepParticle.SweepProvider(set, CustomSweepParticle.ROTATIONTYPE.FLAT));
        e.registerSpriteSet(FootworkParticles.CLEAVE.get(), set -> new CustomSweepParticle.SweepProvider(set, CustomSweepParticle.ROTATIONTYPE.NORMAL));
        e.registerSpriteSet(FootworkParticles.SWEEP.get(), set -> new CustomSweepParticle.SweepProvider(set, CustomSweepParticle.ROTATIONTYPE.NORMAL));
        e.registerSpriteSet(FootworkParticles.CIRCLE.get(), set -> new CustomSweepParticle.SweepProvider(set, CustomSweepParticle.ROTATIONTYPE.SUPERFLAT));
        e.registerSpriteSet(FootworkParticles.SWEEP_LEFT.get(), set -> new CustomSweepParticle.SweepProvider(set, CustomSweepParticle.ROTATIONTYPE.NORMAL));
        e.registerSpriteSet(FootworkParticles.BONK.get(), CustomSweepParticle.BonkProvider::new);
    }


    @SubscribeEvent
    public static void keys(final RegisterKeyMappingsEvent event) {
        event.register(Keybinds.SELECT);
    }
}
