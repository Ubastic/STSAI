package SlayTheSpireAIMod.AIs.CombatAIs;

import SlayTheSpireAIMod.util.Move;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.cards.AbstractCard;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

/** AI versus encounter "Gremlin Nob". */
public class GremlinNobCAI extends AbstractCAI {
    public static final String KEY = "Gremlin Nob";
    public static final Logger logger = LogManager.getLogger(GremlinNobCAI.class.getName());

    @Override
    public Move pickMove() {
        CardSequence start = new CardSequence();
        if(GameActionManager.turn > 1) {
            // never play skills to enrage Gremlin Nob
            ArrayList<AbstractCard> skills = new ArrayList<>();
            for(AbstractCard c : start.simplePlayer.hand) {
                if(c.type == AbstractCard.CardType.SKILL) {
                    skills.add(c);
                }
            }
            for(AbstractCard c : skills) {
                start.simplePlayer.hand.remove(c);
            }
        }

        return GenericCAI.pickMove(GenericCAI::heuristic, GenericCAI::potionEval, start);
    }
}
