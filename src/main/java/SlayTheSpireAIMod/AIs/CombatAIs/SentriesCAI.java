package SlayTheSpireAIMod.AIs.CombatAIs;

import SlayTheSpireAIMod.util.Move;

public class SentriesCAI extends AbstractCAI {
    @Override
    public String getCombat() {
        return "3 Sentries";
    }

    @Override
    public Move pickMove() {
        return genericPickMove();
    }
}
