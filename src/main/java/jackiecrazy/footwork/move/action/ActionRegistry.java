package jackiecrazy.footwork.move.action;

import jackiecrazy.footwork.Footwork;
import jackiecrazy.footwork.move.action.timer.*;
import jackiecrazy.footwork.move.action.trigger.Trigger;
import jackiecrazy.footwork.move.argument.entity.EntityArgument;
import jackiecrazy.footwork.move.argument.number.NumberArgument;
import jackiecrazy.footwork.move.argument.resourcelocation.ResourceLocationArgument;
import jackiecrazy.footwork.move.argument.vector.VectorArgument;
import jackiecrazy.footwork.utils.ActionJsonAdapters;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ActionRegistry {
    public static ResourceLocation REGISTRY_NAME = new ResourceLocation(Footwork.MODID, "actions");
    public static Supplier<IForgeRegistry<ActionType>> SUPPLIER;
    public static DeferredRegister<ActionType> ACTIONS = DeferredRegister.create(REGISTRY_NAME, Footwork.MODID);

    //Timer Actions//
    public static final RegistryObject<ActionType> WAIT = ACTIONS.register("wait", () -> (a) -> ActionJsonAdapters.gson.fromJson(a, WaitAction.class));
    public static final RegistryObject<ActionType> ADD_VELOCITY = ACTIONS.register("modify_velocity", () -> (a) -> ActionJsonAdapters.gson.fromJson(a, ModifyVelocityAction.class));
    public static final RegistryObject<ActionType> MOVE_TO = ACTIONS.register("move_to", () -> (a) -> ActionJsonAdapters.gson.fromJson(a, MoveToAction.class));
    public static final RegistryObject<ActionType> PROJECT_HITBOX = ACTIONS.register("project_hitbox", () -> (a) -> ActionJsonAdapters.gson.fromJson(a, ProjectHitboxAction.class));
    public static final RegistryObject<ActionType> FOLLOW_PATH = ACTIONS.register("move_along_path", () -> (a) -> ActionJsonAdapters.gson.fromJson(a, MoveAlongPathAction.class));
    public static final RegistryObject<ActionType> ADD_CALLBACK = ACTIONS.register("attach_callback", () -> (a) -> ActionJsonAdapters.gson.fromJson(a, AttachCallbackAction.class));

    //Triggers//
    public static final RegistryObject<ActionType> ACTION_ON_HIT = ACTIONS.register("trigger_on_hit", () -> (a) -> ActionJsonAdapters.gson.fromJson(a, Trigger.class));
    public static final RegistryObject<ActionType> ACTION_ON_DEATH = ACTIONS.register("trigger_on_death", () -> (a) -> ActionJsonAdapters.gson.fromJson(a, Trigger.class));
    public static final RegistryObject<ActionType> ACTION_ON_EFFECT_A = ACTIONS.register("trigger_on_effect_applicable", () -> (a) -> ActionJsonAdapters.gson.fromJson(a, Trigger.class));
    public static final RegistryObject<ActionType> ACTION_ON_EFFECT = ACTIONS.register("trigger_on_effect_applied", () -> (a) -> ActionJsonAdapters.gson.fromJson(a, Trigger.class));
    public static final RegistryObject<ActionType> ACTION_ON_STUN = ACTIONS.register("trigger_on_stunned", () -> (a) -> ActionJsonAdapters.gson.fromJson(a, Trigger.class));
    public static final RegistryObject<ActionType> ACTION_ON_HURT = ACTIONS.register("trigger_on_hurt", () -> (a) -> ActionJsonAdapters.gson.fromJson(a, Trigger.class));
    public static final RegistryObject<ActionType> ACTION_ON_POS_DAM = ACTIONS.register("trigger_on_posture_damage", () -> (a) -> ActionJsonAdapters.gson.fromJson(a, Trigger.class));
    //public static final RegistryObject<ActionType> ACTION_ON_COLLIDE_BLOCK = ACTIONS.register("trigger_on_block_collision", () -> (a) -> JsonAdapters.gson.fromJson(a, Trigger.class));
    //public static final RegistryObject<ActionType> ACTION_ON_COLLIDE_ENTITY = ACTIONS.register("trigger_on_entity_collision", () -> (a) -> JsonAdapters.gson.fromJson(a, Trigger.class));

    //Simple Actions//
    public static final RegistryObject<ActionType> STORE_NUMBER = ACTIONS.register("store_number", () -> (a) -> ActionJsonAdapters.gson.fromJson(a, NumberArgument.Store.class));
    public static final RegistryObject<ActionType> STORE_VECTOR = ACTIONS.register("store_vector", () -> (a) -> ActionJsonAdapters.gson.fromJson(a, VectorArgument.Store.class));
    public static final RegistryObject<ActionType> STORE_ENTITY = ACTIONS.register("store_entity", () -> (a) -> ActionJsonAdapters.gson.fromJson(a, EntityArgument.Store.class));
    public static final RegistryObject<ActionType> STORE_RLOC = ACTIONS.register("store_resource_location", () -> (a) -> ActionJsonAdapters.gson.fromJson(a, ResourceLocationArgument.Store.class));
    public static final RegistryObject<ActionType> ALTER_RESOURCE = ACTIONS.register("change_resource", () -> (a) -> ActionJsonAdapters.gson.fromJson(a, AlterResourceAction.class));

    public static final RegistryObject<ActionType> DEBUG = ACTIONS.register("debug", () -> (a) -> ActionJsonAdapters.gson.fromJson(a, DebugAction.class));
    public static final RegistryObject<ActionType> ATTACH_ACTION = ACTIONS.register("attach_action", () -> (a) -> ActionJsonAdapters.gson.fromJson(a, AttachActionAction.class));
    public static final RegistryObject<ActionType> DEAL_DAMAGE = ACTIONS.register("deal_damage", () -> (a) -> ActionJsonAdapters.gson.fromJson(a, DealDamageAction.class));
    public static final RegistryObject<ActionType> ADD_EFFECT = ACTIONS.register("add_effect", () -> (a) -> ActionJsonAdapters.gson.fromJson(a, AddEffectAction.class));
    public static final RegistryObject<ActionType> EXPLODE = ACTIONS.register("explode", () -> (a) -> ActionJsonAdapters.gson.fromJson(a, ExplodeAction.class));
    public static final RegistryObject<ActionType> FREEZE = ACTIONS.register("freeze", () -> (a) -> ActionJsonAdapters.gson.fromJson(a, FreezeAction.class));
    public static final RegistryObject<ActionType> IGNITE = ACTIONS.register("ignite", () -> (a) -> ActionJsonAdapters.gson.fromJson(a, IgniteAction.class));
    public static final RegistryObject<ActionType> LOOK_AT = ACTIONS.register("look_at", () -> (a) -> ActionJsonAdapters.gson.fromJson(a, LookAtAction.class));
    public static final RegistryObject<ActionType> SET_AGGRESSIVE = ACTIONS.register("set_aggressive", () -> (a) -> ActionJsonAdapters.gson.fromJson(a, SetAggressiveAction.class));
    public static final RegistryObject<ActionType> SPAWN_ENTITY = ACTIONS.register("spawn_entity", () -> (a) -> ActionJsonAdapters.gson.fromJson(a, SpawnEntityAction.class));
    public static final RegistryObject<ActionType> ADD_ATTRIBUTE = ACTIONS.register("add_attribute", () -> (a) -> ActionJsonAdapters.gson.fromJson(a, AddAttributeModifierAction.class));
    public static final RegistryObject<ActionType> PLAY_PARTICLE = ACTIONS.register("play_particle", () -> (a) -> ActionJsonAdapters.gson.fromJson(a, PlayParticleAction.class));
    public static final RegistryObject<ActionType> PLAY_SOUND = ACTIONS.register("play_sound", () -> (a) -> ActionJsonAdapters.gson.fromJson(a, PlaySoundAction.class));
    public static final RegistryObject<ActionType> TELEPORT = ACTIONS.register("teleport", () -> (a) -> ActionJsonAdapters.gson.fromJson(a, TeleportAction.class));
    public static final RegistryObject<ActionType> COMMAND = ACTIONS.register("run_command", () -> (a) -> ActionJsonAdapters.gson.fromJson(a, RunCommandAction.class));
    public static final RegistryObject<ActionType> SWING_ARM = ACTIONS.register("swing_arm", () -> (a) -> ActionJsonAdapters.gson.fromJson(a, SwingArmAction.class));
    public static final RegistryObject<ActionType> REMOVE = ACTIONS.register("remove", () -> (a) -> ActionJsonAdapters.gson.fromJson(a, RemoveFromExistenceAction.class));
    public static final RegistryObject<ActionType> MOUNT = ACTIONS.register("mount", () -> (a) -> ActionJsonAdapters.gson.fromJson(a, MountAction.class));
    public static final RegistryObject<ActionType> NOHIT = ACTIONS.register("reset_hit_timer", () -> (a) -> ActionJsonAdapters.gson.fromJson(a, ResetHitTimerAction.class));
    public static final RegistryObject<ActionType> STEVETIME = ACTIONS.register("steve_time", () -> (a) -> ActionJsonAdapters.gson.fromJson(a, SteveTimeAction.class));
    public static final RegistryObject<ActionType> PIN = ACTIONS.register("pin", () -> (a) -> ActionJsonAdapters.gson.fromJson(a, PinAction.class));
    public static final RegistryObject<ActionType> BIND = ACTIONS.register("bind", () -> (a) -> ActionJsonAdapters.gson.fromJson(a, BindAction.class));
    public static final RegistryObject<ActionType> KNOCKBACK = ACTIONS.register("knockback", () -> (a) -> ActionJsonAdapters.gson.fromJson(a, KnockbackAction.class));

    //Meta Actions//
    public static final RegistryObject<ActionType> GOTO = ACTIONS.register("goto", () -> (a) -> ActionJsonAdapters.gson.fromJson(a, GotoAction.class));
    public static final RegistryObject<ActionType> STOP = ACTIONS.register("stop", () -> (a) -> ActionJsonAdapters.gson.fromJson(a, StopAction.class));
    public static final RegistryObject<ActionType> IF_ELSE = ACTIONS.register("if_else", () -> (a) -> ActionJsonAdapters.gson.fromJson(a, IfElseAction.class));
    public static final RegistryObject<ActionType> META = ACTIONS.register("meta", () -> (a) -> ActionJsonAdapters.gson.fromJson(a, MetaAction.class));
}
