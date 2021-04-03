package SlayTheSpireAIMod.AIs.CombatAIs;

import SlayTheSpireAIMod.util.Move;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AutomatonCAI extends AbstractCAI{
    public static final String KEY = "Automaton";

    @Override
    public Move pickMove() {
        return GenericCAI.pickMove(AutomatonCAI::heuristic, TheGuardianCAI::potionEval);
    }

    public static double heuristic(CardSequence state){
        double genericFactor = GenericCAI.heuristic(state);

        int demonFormWeight = 20;
        int demonFormFactor = demonFormWeight * state.simplePlayer.demonForm;

        return genericFactor + demonFormFactor;
    }
}
