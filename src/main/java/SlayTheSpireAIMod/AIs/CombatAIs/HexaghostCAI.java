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
        return GenericCAI.pickMove(x -> GenericCAI.heuristic(x, 0), TheGuardianCAI::potionEval);
    }
}
