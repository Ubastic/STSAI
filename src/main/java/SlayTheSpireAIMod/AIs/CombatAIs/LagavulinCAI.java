package SlayTheSpireAIMod.AIs.CombatAIs;

import SlayTheSpireAIMod.util.Move;

/** AI versus encounter "Lagavulin". */
public class LagavulinCAI extends AbstractCAI {
    @Override
    public String getCombat() {
        return "Lagavulin";
    }

    @Override
    public Move pickMove() {
        return newGenericPickMove();
    }
}
