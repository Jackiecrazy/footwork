package jackiecrazy.footwork.utils;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.phys.Vec3;

public class NBTUtils {
    public static CompoundTag toTag(Vec3 vec) {
        CompoundTag tag = new CompoundTag();
        tag.putDouble("x", vec.x);
        tag.putDouble("y", vec.y);
        tag.putDouble("z", vec.z);
        return tag;
    }

    public static Vec3 fromTag(CompoundTag tag) {
        if (tag == null || tag.isEmpty()) {
            return Vec3.ZERO;
        }
        return new Vec3(
                tag.getDouble("x"),
                tag.getDouble("y"),
                tag.getDouble("z")
        );
    }

    // Optional: ListTag version (used by entities for position/motion)
    public static ListTag toList(Vec3 vec) {
        ListTag list = new ListTag();
        list.add(DoubleTag.valueOf(vec.x));
        list.add(DoubleTag.valueOf(vec.y));
        list.add(DoubleTag.valueOf(vec.z));
        return list;
    }
}
