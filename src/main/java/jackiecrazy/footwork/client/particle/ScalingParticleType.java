package jackiecrazy.footwork.client.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.VibrationParticleOption;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.gameevent.PositionSource;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class ScalingParticleType implements ParticleOptions {
    public static final ParticleOptions.Deserializer<ScalingParticleType> DESERIALIZER = new ParticleOptions.Deserializer<>() {
        public @NotNull ScalingParticleType fromCommand(ParticleType<ScalingParticleType> type, StringReader p_175860_) throws CommandSyntaxException {
            p_175860_.expect(' ');
            float f = (float) p_175860_.readDouble();
            float f1 = (float) p_175860_.readDouble();
            return new ScalingParticleType(type, f, f1);
        }

        public @NotNull ScalingParticleType fromNetwork(ParticleType<ScalingParticleType> type, FriendlyByteBuf buf) {

            double f = buf.readDouble();
            double f1 = buf.readDouble();
            return new ScalingParticleType(type, f, f1);
        }
    };
    private final ParticleType<ScalingParticleType> type;
    private final double xsize, ysize;
    public ScalingParticleType(ParticleType<ScalingParticleType> type, double xsize, double ysize) {
        super();
        this.type = type;
        this.xsize = xsize;
        this.ysize = ysize;
    }

    public static Codec<ScalingParticleType> codec(ParticleType<ScalingParticleType> type) {
        return RecordCodecBuilder.create((grouper) -> grouper.group(Codec.DOUBLE.fieldOf("xsize").forGetter((orig) -> orig.xsize), Codec.DOUBLE.fieldOf("ysize").forGetter((orig) -> orig.ysize)).apply(grouper, (size1, size2) -> new ScalingParticleType(type, size1, size2)));
    }

    @Override
    public ParticleType<?> getType() {
        return type;
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf p_123732_) {
        p_123732_.writeDouble(xsize);
        p_123732_.writeDouble(ysize);
    }

    @Override
    public String writeToString() {
        return String.format(Locale.ROOT, "%s %.2f %.2f", Registry.PARTICLE_TYPE.getKey(this.getType()), xsize, ysize);
    }

    public double getXSize() {
        return xsize;
    }

    public double getYSize() {
        return ysize;
    }

}
