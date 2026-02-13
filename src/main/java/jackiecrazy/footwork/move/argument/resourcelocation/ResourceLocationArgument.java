package jackiecrazy.footwork.move.argument.resourcelocation;

import jackiecrazy.footwork.move.action.Action;
import jackiecrazy.footwork.move.argument.Argument;
import jackiecrazy.footwork.move.utils.ActionContext;
import jackiecrazy.footwork.move.utils.ArgumentContext;
import net.minecraft.resources.ResourceLocation;

public abstract class ResourceLocationArgument implements Argument<ResourceLocation> {
    public static class Raw extends ResourceLocationArgument{
        public Raw(){

        }
        public Raw(String of){
            value=ResourceLocation.tryParse(of);
        }
        private ResourceLocation value;
        @Override
        public ResourceLocation resolve(ArgumentContext argumentContext) {
            return value;
        }
    }
    public static class Get extends ResourceLocationArgument{
        private String from;
        @Override
        public ResourceLocation resolve(ArgumentContext argumentContext) {
            return ResourceLocation.tryParse(argumentContext.performer().getPersistentData().getString(from));
        }
    }
    public static class Store extends Action {
        private Argument<ResourceLocation> value;
        private String into;

        @Override
        public int perform(ActionContext actionContext) {
            final ResourceLocation vec = value.resolve(actionContext);
            actionContext.performer().getPersistentData().putString(into, vec.toString());
            return 0;
        }
    }
}
