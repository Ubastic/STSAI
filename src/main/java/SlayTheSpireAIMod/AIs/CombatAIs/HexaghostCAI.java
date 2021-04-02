package SlayTheSpireAIMod.AIs.CombatAIs;

import SlayTheSpireAIMod.util.Move;

/** AI versus encounter "Hexaghost". */
public class HexaghostCAI extends AbstractCAI {
    @Override
    public String getCombat() {
        return "Hexaghost";
    }

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
