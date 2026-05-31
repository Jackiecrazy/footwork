package jackiecrazy.footwork.move.motionframe;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;

public final class SoundInfo {
    private final ResourceLocation name;
    private final double volume;
    private final double pitch;

    public SoundInfo(ResourceLocation name, double volume, double pitch) {
        this.name = name;
        this.volume = volume;
        this.pitch = pitch;
    }



    public ResourceLocation sound() {
        return name;
    }

    public double volume() {
        return volume;
    }

    public double pitch() {
        return pitch;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (SoundInfo) obj;
        return Objects.equals(this.name, that.name) &&
                Double.doubleToLongBits(this.volume) == Double.doubleToLongBits(that.volume) &&
                Double.doubleToLongBits(this.pitch) == Double.doubleToLongBits(that.pitch);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, volume, pitch);
    }

    @Override
    public String toString() {
        return "SoundInfo[" +
                "sound=" + name + ", " +
                "volume=" + volume + ", " +
                "pitch=" + pitch + ']';
    }

}
