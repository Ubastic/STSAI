package MyFirstMod.AIs.CombatAIs;

import MyFirstMod.util.Move;

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
