package SlayTheSpireAIMod.AIs.CombatAIs;

import SlayTheSpireAIMod.util.Move;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

/** AI versus encounter "Gremlin Nob". */
public class GremlinNobCAI extends AbstractCAI {
    public static final String KEY = "Gremlin Nob";
    public static final Logger logger = LogManager.getLogger(GremlinNobCAI.class.getName());

    @Override
    public Move pickMove() {
        // first, remove cards that cannot be played
        CardSequence start = new CardSequence();

        // never play skills vs Gremlin Nob
        ArrayList<AbstractCard> skills = new ArrayList<>();
        for(AbstractCard c : start.simplePlayer.hand){
            if(c.type == AbstractCard.CardType.SKILL){
                skills.add(c);
            }
        }
        for(AbstractCard c : skills){
            start.simplePlayer.hand.remove(c);
        }

        CardSequence bestState = start.getBestPossibility(GenericCAI::heuristic);
        if(bestState != start){
            logger.info("Evaluated best state: " + bestState.toString());
            int bestIndex = AbstractDungeon.player.hand.group.indexOf(bestState.first);
            return new Move(Move.TYPE.CARD, bestIndex, AbstractDungeon.getCurrRoom().monsters.monsters.get(bestState.firstTargetIndex));
        }
        return new Move(Move.TYPE.PASS);
    }
}
