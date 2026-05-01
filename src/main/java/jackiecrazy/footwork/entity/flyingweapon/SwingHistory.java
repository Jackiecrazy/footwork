package jackiecrazy.footwork.entity.flyingweapon;

import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;

import java.awt.*;

public record SwingHistory(Vec3 position, boolean corporeal, Color color, Quaternionf orientation) {
}
