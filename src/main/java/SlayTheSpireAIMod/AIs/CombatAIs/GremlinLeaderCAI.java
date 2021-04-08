package SlayTheSpireAIMod.AIs.CombatAIs;

import SlayTheSpireAIMod.AIs.CombatAIs.Monsters.SimpleMonster;
import SlayTheSpireAIMod.util.Move;
import com.megacrit.cardcrawl.monsters.city.GremlinLeader;

public class GremlinLeaderCAI extends AbstractCAI {
    public static final String KEY = "Gremlin Leader";

    @Override
    public Move pickMove() {
        return GenericCAI.pickMove(GremlinLeaderCAI::heuristic);
    }

    public static double heuristic(CardSequence state){
        double genericFactor = GenericCAI.heuristic(state);

        int lethalFactor = 0;
        for(SimpleMonster m : state.simpleMonsters){
            if(m.attack.getMonster() instanceof GremlinLeader && !m.isAlive()){
                lethalFactor = 1000; // minions leave if leader dies
            }
        }

        return genericFactor + lethalFactor;
    }
}
