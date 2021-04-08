package SlayTheSpireAIMod.AIs.CombatAIs;

import SlayTheSpireAIMod.util.Move;

public class SlaversCAI extends AbstractCAI {
    public static final String KEY = "Slavers";

    @Override
    public Move pickMove() {
        return GenericCAI.pickMove(SlaversCAI::heuristic);
    }

    public static double heuristic(CardSequence state) {
        double genericFactor = GenericCAI.heuristic(state);

        double targetSlaverFactor = 0;
        // target the red slaver, then the blue one
        if (state.simpleMonsters.size() == 3) {
            int redSlaverHealth = state.simpleMonsters.get(2).health;
            int taskmasterHealth = state.simpleMonsters.get(1).health;
            if (redSlaverHealth > 0) {
                targetSlaverFactor += 10 + 0.1 * redSlaverHealth;
            }
            targetSlaverFactor -= 0.1 * taskmasterHealth;
        }

        return genericFactor - targetSlaverFactor;
    }
}
