package jackiecrazy.footwork.capability.resources;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class CombatStorage implements Capability.IStorage<ICombatCapability> {
    @Nullable
    @Override
    public Tag writeNBT(Capability<ICombatCapability> capability, ICombatCapability iCombatCapability, Direction direction) {
        return iCombatCapability.write();
    }

    @Override
    public void readNBT(Capability<ICombatCapability> capability, ICombatCapability iCombatCapability, Direction direction, Tag inbt) {
        if(inbt instanceof CompoundTag) {
            iCombatCapability.read((CompoundTag) inbt);
        }
    }
}
