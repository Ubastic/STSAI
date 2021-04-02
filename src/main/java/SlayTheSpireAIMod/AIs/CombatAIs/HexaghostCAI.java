package SlayTheSpireAIMod.AIs.CombatAIs;

import SlayTheSpireAIMod.util.Move;

/** AI versus encounter "Hexaghost". */
public class HexaghostCAI extends AbstractCAI {
    public static final String KEY = "Hexaghost";

    @Override
    public Move pickMove() {
        return GenericCAI.pickMove(HexaghostCAI::heuristic, TheGuardianCAI::potionEval);
    }

    public static double heuristic(CardSequence state){
        double genericFactor = GenericCAI.heuristic(state);

        int demonFormWeight = 20;
        int demonFormFactor = demonFormWeight * state.simplePlayer.demonForm;

        return genericFactor + demonFormFactor;
    }
}
