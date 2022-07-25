package jackiecrazy.footwork;

import jackiecrazy.footwork.api.WarAttributes;
import jackiecrazy.footwork.capability.resources.CombatStorage;
import jackiecrazy.footwork.capability.resources.DummyCombatCap;
import jackiecrazy.footwork.capability.resources.ICombatCapability;
import jackiecrazy.footwork.capability.weaponry.DummyCombatItemCap;
import jackiecrazy.footwork.capability.weaponry.ICombatItemCapability;
import jackiecrazy.footwork.potion.FootworkEffects;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;
import java.util.stream.Collectors;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("footwork")
public class Footwork {

    public static final String MODID="footwork";

    public static final Random rand=new Random();

    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    public Footwork() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::attribute);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        WarAttributes.ATTRIBUTES.register(bus);
        FootworkEffects.EFFECTS.register(bus);
    }

    private void setup(final FMLCommonSetupEvent event) {
        CapabilityManager.INSTANCE.register(ICombatCapability.class, new CombatStorage(), DummyCombatCap::new);
        CapabilityManager.INSTANCE.register(ICombatItemCapability.class, new DummyCombatItemCap.Storage(), DummyCombatItemCap::new);
    }

    private void attribute(EntityAttributeModificationEvent e) {
        for (EntityType<? extends LivingEntity> type:e.getTypes()){
            if(!e.has(type, Attributes.FOLLOW_RANGE))
                e.add(type, Attributes.FOLLOW_RANGE, 32);
            if(!e.has(type, Attributes.ATTACK_SPEED))
                e.add(type, Attributes.ATTACK_SPEED);
            for(RegistryObject<Attribute> a: WarAttributes.ATTRIBUTES.getEntries())
                e.add(type, a.get());
        }
    }
}
