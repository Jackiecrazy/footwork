package jackiecrazy.footwork.client.particle;

import com.mojang.serialization.Codec;
import jackiecrazy.footwork.Footwork;
import net.minecraft.core.particles.ParticleType;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class FootworkParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, Footwork.MODID);
    public static final RegistryObject<ParticleType<ScalingParticleType>> IMPACT = PARTICLES.register("impact", scalingParticle());
    public static final RegistryObject<ParticleType<ScalingParticleType>> LINE = PARTICLES.register("line", scalingParticle());
    public static final RegistryObject<ParticleType<ScalingParticleType>> CIRCLE = PARTICLES.register("circle", scalingParticle());
    public static final RegistryObject<ParticleType<ScalingParticleType>> CLEAVE = PARTICLES.register("cleave", scalingParticle());
    public static final RegistryObject<ParticleType<ScalingParticleType>> SWEEP = PARTICLES.register("sweep", scalingParticle());
    public static final RegistryObject<ParticleType<ScalingParticleType>> SWEEP_LEFT = PARTICLES.register("sweep_left", scalingParticle());
    public static final RegistryObject<ParticleType<ScalingParticleType>> BONK = PARTICLES.register("bonk", scalingParticle());

    @NotNull
    private static Supplier<ParticleType<ScalingParticleType>> scalingParticle() {
        return () -> new ParticleType<>(true, ScalingParticleType.DESERIALIZER) {
            @Override
            public Codec<ScalingParticleType> codec() {
                return ScalingParticleType.codec(this);
            }
        };
    }

}
