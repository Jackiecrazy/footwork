package jackiecrazy.footwork.move.argument.resourcelocation;

import jackiecrazy.footwork.move.action.Action;
import jackiecrazy.footwork.move.argument.Argument;
import jackiecrazy.footwork.move.argument.entity.CasterEntityArgument;
import jackiecrazy.footwork.move.utils.ActionContext;
import jackiecrazy.footwork.move.utils.ArgumentContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public abstract class ResourceLocationArgument implements Argument<ResourceLocation> {
    public static class Raw extends ResourceLocationArgument {
        private ResourceLocation value;

        public Raw() {

        }

        public Raw(String of) {
            value = ResourceLocation.tryParse(of);
        }

        @Override
        public ResourceLocation resolve(ArgumentContext argumentContext) {
            return value;
        }
    }

    public static class Get extends ResourceLocationArgument {
        private Argument<Entity> storage = CasterEntityArgument.INSTANCE;
        private String from;

        @Override
        public ResourceLocation resolve(ArgumentContext argumentContext) {
            final Entity resolve = storage.resolve(argumentContext);
            if (resolve != null)
                return ResourceLocation.tryParse(resolve.getPersistentData().getString(from));
            return null;
        }
    }

    public static class Store extends Action {
        private Argument<Entity> storage = CasterEntityArgument.INSTANCE;
        private Argument<ResourceLocation> value;
        private String into;

        @Override
        public int perform(ActionContext actionContext) {
            final ResourceLocation vec = value.resolve(actionContext);
            final Entity resolve = storage.resolve(actionContext);
            if (resolve != null)
                resolve.getPersistentData().putString(into, vec.toString());
            return 0;
        }
    }
}
