package SlayTheSpireAIMod.AIs.CombatAIs;

import SlayTheSpireAIMod.util.Move;

public class ChampCAI extends AbstractCAI {
    public static final String KEY = "Champ";

    @Override
    public Move pickMove() {
        return GenericCAI.pickMove(ChampCAI::heuristic, TheGuardianCAI::potionEval);
    }

    public static double heuristic(CardSequence state){
        double genericFactor = GenericCAI.heuristic(state);

        int demonFormWeight = 20;
        int demonFormFactor = demonFormWeight * state.simplePlayer.demonForm;

        return genericFactor + demonFormFactor;
    }
}
