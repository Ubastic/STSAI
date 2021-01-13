package SlayTheSpireAIMod.AIs.CombatAIs;

import SlayTheSpireAIMod.util.Move;

/** AI versus encounter "Gremlin Nob". */
public class GremlinNobCAI extends AbstractCAI {

    @Override
    public String getCombat() {
        return "Gremlin Nob";
    }

    @Override
    public Move pickMove() {
        return genericPickMove();
    }
}
