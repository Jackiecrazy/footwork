package jackiecrazy.footwork;

import jackiecrazy.footwork.api.FootworkAttributes;
import jackiecrazy.footwork.capability.action.IAttachAction;
import jackiecrazy.footwork.capability.goal.IGoalHelper;
import jackiecrazy.footwork.capability.resources.ICombatCapability;
import jackiecrazy.footwork.capability.stylish.IStyleCapability;
import jackiecrazy.footwork.capability.timeslow.ITimeChange;
import jackiecrazy.footwork.capability.weaponry.ICombatItemCapability;
import jackiecrazy.footwork.client.particle.FootworkParticles;
import jackiecrazy.footwork.client.render.ItemEntityRenderer;
import jackiecrazy.footwork.client.render.NothingRender;
import jackiecrazy.footwork.command.AttributizeCommand;
import jackiecrazy.footwork.command.SteveTimeCommand;
import jackiecrazy.footwork.compat.FootworkCompat;
import jackiecrazy.footwork.entity.FootworkEntities;
import jackiecrazy.footwork.entity.flyingweapon.FlyingItemEntity;
import jackiecrazy.footwork.move.ActionSets;
import jackiecrazy.footwork.move.Macros;
import jackiecrazy.footwork.move.action.ActionRegistry;
import jackiecrazy.footwork.move.argument.ArgumentRegistry;
import jackiecrazy.footwork.move.condition.ConditionRegistry;
import jackiecrazy.footwork.move.filter.FilterRegistry;
import jackiecrazy.footwork.move.motionframe.MotionFrame;
import jackiecrazy.footwork.move.motionframe.MotionManager;
import jackiecrazy.footwork.networking.FootworkChannel;
import jackiecrazy.footwork.networking.UpdateTimeSlowPacket;
import jackiecrazy.footwork.potion.FootworkEffects;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.Random;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("footwork")
public class Footwork {

    public static final String MODID = "footwork";
    public static File configDirPath;

    public static final Random rand = new Random();

    // Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger();

    public Footwork() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::packets);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::attribute);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onClientSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        FootworkAttributes.ATTRIBUTES.register(bus);
        FootworkEffects.EFFECTS.register(bus);
        FootworkEntities.ENTITIES.register(bus);
        FootworkParticles.PARTICLES.register(bus);
        MinecraftForge.EVENT_BUS.addListener(this::commands);

        ArgumentRegistry.SUPPLIER = ArgumentRegistry.ARGUMENTS.makeRegistry(RegistryBuilder::new);
        ArgumentRegistry.ARGUMENTS.register(bus);
        ConditionRegistry.SUPPLIER = ConditionRegistry.CONDITIONS.makeRegistry(RegistryBuilder::new);
        ConditionRegistry.CONDITIONS.register(bus);
        ActionRegistry.SUPPLIER = ActionRegistry.ACTIONS.makeRegistry(RegistryBuilder::new);
        ActionRegistry.ACTIONS.register(bus);
        FilterRegistry.SUPPLIER = FilterRegistry.FILTERS.makeRegistry(RegistryBuilder::new);
        FilterRegistry.FILTERS.register(bus);
    }

    private void packets(FMLCommonSetupEvent e){
        FootworkChannel.INSTANCE.registerMessage(1, UpdateTimeSlowPacket.class, new UpdateTimeSlowPacket.UpdateClientEncoder(), new UpdateTimeSlowPacket.UpdateClientDecoder(), new UpdateTimeSlowPacket.UpdateClientHandler());
        EntityDataSerializers.registerSerializer(MotionFrame.SERIALIZER);
        EntityDataSerializers.registerSerializer(MotionManager.SERIALIZER);
        EntityDataSerializers.registerSerializer(FlyingItemEntity.STATESERIALIZER);
    }

    private void setup(final RegisterCapabilitiesEvent event) {
        event.register(ICombatCapability.class);
        event.register(IStyleCapability.class);
        event.register(ICombatItemCapability.class);
        event.register(IAttachAction.class);
        event.register(ITimeChange.class);
        event.register(IGoalHelper.class);
    }

    private void attribute(EntityAttributeModificationEvent e) {
        for (EntityType<? extends LivingEntity> type : e.getTypes()) {
            if (!e.has(type, Attributes.FOLLOW_RANGE))
                e.add(type, Attributes.FOLLOW_RANGE, 32);
            if (!e.has(type, Attributes.ATTACK_SPEED))
                e.add(type, Attributes.ATTACK_SPEED);
            if (!e.has(type, Attributes.LUCK))
                e.add(type, Attributes.LUCK);
            for (RegistryObject<Attribute> a : FootworkAttributes.ATTRIBUTES.getEntries())
                e.add(type, a.get());
        }
    }



    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onJsonListener(AddReloadListenerEvent event) {
        Macros.register(event);
        ActionSets.register(event,"action_sets");
    }

    public void onClientSetup(FMLClientSetupEvent event) {
        EntityRenderers.register(FootworkEntities.DUMMY.get(), NothingRender::new);
        EntityRenderers.register(FootworkEntities.WEAPON.get(), ItemEntityRenderer::new);

    }

    private void processIMC(final InterModProcessEvent event) {
        // some example code to receive and process InterModComms from other mods
        FootworkCompat.checkCompatStatus();
    }


    private void commands(final RegisterCommandsEvent event) {
        AttributizeCommand.register(event.getDispatcher());
        SteveTimeCommand.register(event.getDispatcher());
    }
}
