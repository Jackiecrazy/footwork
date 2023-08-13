package jackiecrazy.footwork.client.particle;

import com.mojang.serialization.Codec;
import jackiecrazy.footwork.Footwork;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.VibrationParticleOption;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;
import java.util.function.Supplier;

public class FootworkParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, Footwork.MODID);
    public static final RegistryObject<ParticleType<ScalingParticleType>> IMPACT = PARTICLES.register("impact", scalingParticle());
    public static final RegistryObject<ParticleType<ScalingParticleType>> LINE = PARTICLES.register("line", scalingParticle());
    public static final RegistryObject<ParticleType<ScalingParticleType>> CIRCLE = PARTICLES.register("circle", scalingParticle());
    public static final RegistryObject<ParticleType<ScalingParticleType>> CLEAVE = PARTICLES.register("cleave", scalingParticle());
    public static final RegistryObject<ParticleType<ScalingParticleType>> SWEEP = PARTICLES.register("sweep", scalingParticle());
    public static final RegistryObject<ParticleType<ScalingParticleType>> SWEEP_LEFT = PARTICLES.register("sweep_left", scalingParticle());
    @NotNull
    private static Supplier<ParticleType<ScalingParticleType>> scalingParticle() {
        return () -> new ParticleType<>(true, ScalingParticleType.DESERIALIZER) {
            @Override
            public Codec<ScalingParticleType> codec() {
                return ScalingParticleType.codec(this);
            }
        };
    }

    private static <T extends ParticleOptions> ParticleType<T> register(String p_235906_, boolean p_235907_, ParticleOptions.Deserializer<T> p_235908_, final Function<ParticleType<T>, Codec<T>> p_235909_) {
        return Registry.register(Registry.PARTICLE_TYPE, p_235906_, new ParticleType<T>(p_235907_, p_235908_) {
            public Codec<T> codec() {
                return p_235909_.apply(this);
            }
        });
    }

}
