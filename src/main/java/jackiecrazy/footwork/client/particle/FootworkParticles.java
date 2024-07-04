package jackiecrazy.footwork.client.particle;

import com.mojang.serialization.MapCodec;
import jackiecrazy.footwork.Footwork;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class FootworkParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, Footwork.MODID);
    public static final DeferredHolder<ParticleType<?>, ParticleType<ScalingParticleType>> IMPACT = PARTICLES.register("impact", scalingParticle());
    public static final DeferredHolder<ParticleType<?>, ParticleType<ScalingParticleType>> LINE = PARTICLES.register("line", scalingParticle());
    public static final DeferredHolder<ParticleType<?>, ParticleType<ScalingParticleType>> CIRCLE = PARTICLES.register("circle", scalingParticle());
    public static final DeferredHolder<ParticleType<?>, ParticleType<ScalingParticleType>> CLEAVE = PARTICLES.register("cleave", scalingParticle());
    public static final DeferredHolder<ParticleType<?>, ParticleType<ScalingParticleType>> SWEEP = PARTICLES.register("sweep", scalingParticle());
    public static final DeferredHolder<ParticleType<?>, ParticleType<ScalingParticleType>> SWEEP_LEFT = PARTICLES.register("sweep_left", scalingParticle());
    public static final DeferredHolder<ParticleType<?>, ParticleType<ScalingParticleType>> BONK = PARTICLES.register("bonk", scalingParticle());

    @NotNull
    private static Supplier<ParticleType<ScalingParticleType>> scalingParticle() {
        return () -> new ParticleType<>(true) {
            @Override
            public MapCodec<ScalingParticleType> codec() {
                return ScalingParticleType.mapCodec(this);
            }

            @Override
            public StreamCodec<? super RegistryFriendlyByteBuf, ScalingParticleType> streamCodec() {
                return ScalingParticleType.streamcodec(this);
            }
        };
    }

}
