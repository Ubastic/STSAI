package SlayTheSpireAIMod.AIs.CombatAIs;

import SlayTheSpireAIMod.util.CombatUtils;
import SlayTheSpireAIMod.util.Move;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SentriesCAI extends AbstractCAI {
    public static final Logger logger = LogManager.getLogger(SentriesCAI.class.getName());

    @Override
    public String getCombat() {
        return "3 Sentries";
    }

    @Override
    public Move pickMove() {
        return GenericCAI.pickMove(x -> heuristic(x, 0));
    }

    /**
     * Returns the evaluation of the given state. Lower is better.
     *
     * @param state the state to be evaluated.
     * @return how good the state is
     * */
    public static int heuristic(CardSequence state, int tolerance){
        int genericFactor = GenericCAI.heuristic(state, tolerance);
        int aliveMonsters = 0;
        for(CombatUtils.SimpleMonster m : state.simpleMonsters){
            if(m.isAlive()){
                aliveMonsters += 1;
            }
        }

        // it is better to kill either the left or right sentry first
        int multiSentryFactor = 0;
        if(aliveMonsters == 3){
            CombatUtils.SimpleMonster left = state.simpleMonsters.get(0);
            CombatUtils.SimpleMonster right = state.simpleMonsters.get(2);
            multiSentryFactor = Math.min(left.health, right.health);
        }
        return genericFactor + multiSentryFactor;
    }
}
