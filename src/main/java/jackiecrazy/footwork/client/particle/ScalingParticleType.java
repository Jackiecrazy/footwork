package jackiecrazy.footwork.client.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;

import java.util.Locale;

public class ScalingParticleType implements ParticleOptions {
    public static final ParticleOptions.Deserializer<ScalingParticleType> DESERIALIZER = new ParticleOptions.Deserializer<ScalingParticleType>() {
        public ScalingParticleType fromCommand(ParticleType<ScalingParticleType> type, StringReader p_175860_) throws CommandSyntaxException {
            p_175860_.expect(' ');
            float f = (float) p_175860_.readDouble();
            return new ScalingParticleType(type, f);
        }

        public ScalingParticleType fromNetwork(ParticleType<ScalingParticleType> type, FriendlyByteBuf buf) {

            double f = buf.readDouble();
            return new ScalingParticleType(type, f);
        }
    };
    private final ParticleType<ScalingParticleType> type;
    private final double size;
    public ScalingParticleType(ParticleType<ScalingParticleType> type, double size) {
        super();
        this.type = type;
        this.size = size;
    }

    public static Codec<ScalingParticleType> codec(ParticleType<ScalingParticleType> type) {
        return RecordCodecBuilder.create((grouper) -> grouper.group(Codec.DOUBLE.fieldOf("size").forGetter((orig) -> orig.size)).apply(grouper, size1 -> new ScalingParticleType(type, size1)));
    }

    @Override
    public ParticleType<?> getType() {
        return type;
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf p_123732_) {
        p_123732_.writeDouble(size);
    }

    @Override
    public String writeToString() {
        return String.format(Locale.ROOT, "%s %.2f", Registry.PARTICLE_TYPE.getKey(this.getType()), size);
    }

    public double getSize() {
        return size;
    }

}
