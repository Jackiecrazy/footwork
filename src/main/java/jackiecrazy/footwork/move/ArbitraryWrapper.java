package jackiecrazy.footwork.move;

import jackiecrazy.footwork.move.action.Action;

import java.util.List;

public class ArbitraryWrapper extends ActionSetWrapper{
    public ArbitraryWrapper(List<Action> actions) {
        super(List.of());
    }
}
