package MyFirstMod.AIs.CombatAIs;

import MyFirstMod.util.Move;

/** AI versus encounter "Gremlin Nob". */
public class GremlinNobAI extends AbstractAI{

    @Override
    public String getCombat() {
        return "Gremlin Nob";
    }

    @Override
    public Move pickMove() {
        return genericPickMove();
    }
}
