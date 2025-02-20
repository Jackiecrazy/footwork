package jackiecrazy.footwork.client.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Locale;

public class ScalingParticleType implements ParticleOptions {
    public static final ParticleOptions.Deserializer<ScalingParticleType> DESERIALIZER = new ParticleOptions.Deserializer<>() {
        public @NotNull ScalingParticleType fromCommand(ParticleType<ScalingParticleType> type, StringReader reader) throws CommandSyntaxException {
            reader.expect(' ');
            float f = (float) reader.readDouble();
            float f1 = (float) reader.readDouble();
            return new ScalingParticleType(type, f, f1, reader.readInt(), reader.readInt(), reader.readInt(), reader.readInt());
        }

        public @NotNull ScalingParticleType fromNetwork(ParticleType<ScalingParticleType> type, FriendlyByteBuf buf) {

            double f = buf.readDouble();
            double f1 = buf.readDouble();
            return new ScalingParticleType(type, f, f1, buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt());
        }
    };
    private final ParticleType<ScalingParticleType> type;
    private final double xsize, ysize;
    private final Color c;

    public int getLife() {
        return life;
    }

    private final int life;

    public ScalingParticleType(ParticleType<ScalingParticleType> type, double xsize, double ysize, int ticks, int r, int g, int b) {
        super();
        this.type = type;
        this.xsize = xsize;
        this.ysize = ysize;
        c = new Color(r, g, b);
        life = ticks;
    }

    public ScalingParticleType(ParticleType<ScalingParticleType> type, double xsize, double ysize, int life, Color c) {
        this(type, xsize, ysize, life, c.getRed(), c.getGreen(), c.getBlue());
    }
public ScalingParticleType(ParticleType<ScalingParticleType> type, double xsize, double ysize, int life) {
        this(type, xsize, ysize, life, 255, 255, 255);
    }

    public ScalingParticleType(ParticleType<ScalingParticleType> type, double xsize, double ysize) {
        this(type, xsize, ysize, 10);
    }

    public static Codec<ScalingParticleType> codec(ParticleType<ScalingParticleType> type) {
        return RecordCodecBuilder.create((grouper) ->
                grouper.group(Codec.DOUBLE.fieldOf("xsize").forGetter((orig) -> orig.xsize),
                                Codec.DOUBLE.fieldOf("ysize").forGetter((orig) -> orig.ysize),
                                Codec.INT.fieldOf("time").forGetter((orig) -> orig.life),
                                Codec.INT.fieldOf("red").forGetter((orig) -> orig.c.getRed()),
                                Codec.INT.fieldOf("green").forGetter((orig) -> orig.c.getGreen()),
                                Codec.INT.fieldOf("blue").forGetter((orig) -> orig.c.getBlue()))
                        .apply(grouper, (size1, size2, time, r, g, b) -> new ScalingParticleType(type, size1, size2, time, r, g, b)));
    }

    public Color getColor() {
        return c;
    }

    @Override
    public ParticleType<?> getType() {
        return type;
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf buf) {
        buf.writeDouble(xsize);
        buf.writeDouble(ysize);
        buf.writeInt(life);
        buf.writeInt(c.getRed());
        buf.writeInt(c.getGreen());
        buf.writeInt(c.getBlue());
    }

    @Override
    public String writeToString() {
        return String.format(Locale.ROOT, "%s %.2f %.2f", ForgeRegistries.PARTICLE_TYPES.getKey(this.getType()), xsize, ysize);
    }

    public double getXSize() {
        return xsize;
    }

    public double getYSize() {
        return ysize;
    }

}
