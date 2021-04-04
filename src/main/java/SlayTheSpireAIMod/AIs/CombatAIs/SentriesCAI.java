package SlayTheSpireAIMod.AIs.CombatAIs;

import SlayTheSpireAIMod.AIs.CombatAIs.Monsters.SimpleMonster;
import SlayTheSpireAIMod.util.Move;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SentriesCAI extends AbstractCAI {
    public static final String KEY = "3 Sentries";
    public static final Logger logger = LogManager.getLogger(SentriesCAI.class.getName());

    @Override
    public Move pickMove() {
        return GenericCAI.pickMove(SentriesCAI::heuristic);
    }

    /**
     * Returns an evaluation of the specified state. Greater is better.
     *
     * @param state the state to be evaluated
     * @return      the evaluation of the state. Greater is better
     * */
    public static double heuristic(CardSequence state){
        double genericFactor = GenericCAI.heuristic(state);

        int aliveMonsters = 0;
        for(SimpleMonster m : state.simpleMonsters){
            if(m.isAlive()){
                aliveMonsters += 1;
            }
        }

        // it is better to kill either the left or right sentry first
        int multiSentryFactor = 0;
        if(aliveMonsters == 3){
            SimpleMonster left = state.simpleMonsters.get(0);
            SimpleMonster right = state.simpleMonsters.get(2);
            multiSentryFactor = Math.min(left.health, right.health);
        }
        return genericFactor - multiSentryFactor;
    }
}
