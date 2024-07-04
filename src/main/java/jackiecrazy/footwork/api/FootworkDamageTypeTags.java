package jackiecrazy.footwork.api;

import jackiecrazy.footwork.Footwork;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;

public class FootworkDamageTypeTags {
    public static final TagKey<DamageType> AUTO = create("normal_attack");
    public static final TagKey<DamageType> ATTACK = create("attack");
    public static final TagKey<DamageType> SKILL = create("skill");

    private static TagKey<DamageType> create(String tag) {
        return TagKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(Footwork.MODID, tag));
    }
}
