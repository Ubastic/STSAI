package SlayTheSpireAIMod.AIs.CombatAIs;

import SlayTheSpireAIMod.util.Move;

public class SentriesAI extends AbstractAI{
    @Override
    public String getCombat() {
        return "3 Sentries";
    }

    @Override
    public Move pickMove() {
        return genericPickMove();
    }
}
