package SlayTheSpireAIMod.AIs.CombatAIs;

import SlayTheSpireAIMod.util.Move;

public class CollectorCAI extends AbstractCAI{
    public static final String KEY = "Collector";

    @Override
    public Move pickMove() {
        return GenericCAI.pickMove(CollectorCAI::heuristic, TheGuardianCAI::potionEval);
    }

    public static double heuristic(CardSequence state){
        double genericFactor = GenericCAI.heuristic(state);

        int demonFormWeight = 20;
        int demonFormFactor = demonFormWeight * state.simplePlayer.demonForm;

        return genericFactor + demonFormFactor;
    }
}
