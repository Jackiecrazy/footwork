package jackiecrazy.footwork.entity;

import jackiecrazy.footwork.Footwork;
import jackiecrazy.footwork.entity.flyingweapon.DummyFlyingWeaponEntity;
import jackiecrazy.footwork.entity.flyingweapon.FlyingItemEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = Footwork.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class FootworkEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Footwork.MODID);

    public static final RegistryObject<EntityType<DummyEntity>> DUMMY = ENTITIES.register("dummy", () -> EntityType.Builder
            .of(DummyEntity::new, MobCategory.MISC)
            .sized(0.5F, 0.5F)
            .build("dummy"));
    public static final RegistryObject<EntityType<DummyFlyingWeaponEntity>> WEAPON = ENTITIES.register("dummy_flying", () -> EntityType.Builder
            .of(DummyFlyingWeaponEntity::new, MobCategory.MISC)
            .sized(0.1F, 0.1F)
            .updateInterval(1)
            .setShouldReceiveVelocityUpdates(true)
            .build("dummy_flying"));

    @SubscribeEvent
    public static void attribute(EntityAttributeCreationEvent e){
        e.put(DUMMY.get(), DummyEntity.createAttributes().build());
    }
}
