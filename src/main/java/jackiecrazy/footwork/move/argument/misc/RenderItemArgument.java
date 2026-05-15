package jackiecrazy.footwork.move.argument.misc;

import jackiecrazy.footwork.move.argument.Argument;
import jackiecrazy.footwork.move.motionframe.ItemNode;
import jackiecrazy.footwork.move.motionframe.RenderItemGroup;
import jackiecrazy.footwork.move.utils.ArgumentContext;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class RenderItemArgument implements Argument<RenderItemGroup> {
    private List<ItemNodeArgument> nodes = new ArrayList<>();

    public List<ItemNodeArgument> getNodes() {
        return nodes;
    }

    public RenderItemArgument withNodes(ItemNodeArgument... nodes) {
        this.nodes = List.of(nodes);
        return this;
    }

    @Override
    public @Nullable RenderItemGroup resolve(ArgumentContext argumentContext) {

        final ItemNode[] ret = nodes.stream().map(a -> a.resolve(argumentContext)).toList().toArray(new ItemNode[0]);
        return new RenderItemGroup(ret);
    }
}
