package jackiecrazy.footwork.client.particle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.awt.*;

public class ScalingParticleType implements ParticleOptions {

    public static StreamCodec<? super RegistryFriendlyByteBuf, ScalingParticleType> streamcodec(ParticleType<ScalingParticleType> type) {
        return new StreamCodec<>() {
            @Override
            public void encode(RegistryFriendlyByteBuf pBuffer, ScalingParticleType pValue) {
                pBuffer.writeDouble(pValue.xsize);
                pBuffer.writeDouble(pValue.ysize);
                pBuffer.writeInt(pValue.life);
                pBuffer.writeInt(pValue.c.getRed());
                pBuffer.writeInt(pValue.c.getGreen());
                pBuffer.writeInt(pValue.c.getBlue());
            }

            @Override
            public ScalingParticleType decode(RegistryFriendlyByteBuf pBuffer) {
                return new ScalingParticleType(type, pBuffer.readDouble(), pBuffer.readDouble(), pBuffer.readInt(), pBuffer.readInt(), pBuffer.readInt(), pBuffer.readInt());
            }
        };
    }

    public static MapCodec<ScalingParticleType> mapCodec(ParticleType<ScalingParticleType> type) {
        return RecordCodecBuilder.mapCodec(
                builder -> builder.group(
                                Codec.DOUBLE.fieldOf("xSize").forGetter(ScalingParticleType::getXSize),
                                Codec.DOUBLE.fieldOf("ySize").forGetter(ScalingParticleType::getXSize),
                                Codec.INT.fieldOf("life").forGetter(ScalingParticleType::getLife),
                                Codec.INT.fieldOf("red").forGetter((a)->a.getColor().getRed()),
                                Codec.INT.fieldOf("green").forGetter((a)->a.getColor().getGreen()),
                                Codec.INT.fieldOf("blue").forGetter((a)->a.getColor().getBlue())
                        )
                        .apply(builder, (a,b,c,d,e,f)-> new ScalingParticleType(type, a, b, c, d, e, f))
        );
    }


    private final ParticleType<ScalingParticleType> type;
    private final double xsize, ysize;
    private final Color c;
    private final int life;

    public ScalingParticleType(ParticleType<ScalingParticleType> type, double xsize, double ysize, int ticks, int r, int g, int b) {
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



    public int getLife() {
        return life;
    }

    public Color getColor() {
        return c;
    }

    @Override
    public ParticleType<?> getType() {
        return type;
    }

    public double getXSize() {
        return xsize;
    }

    public double getYSize() {
        return ysize;
    }

}
