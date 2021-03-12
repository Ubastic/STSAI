package SlayTheSpireAIMod.AIs.CombatAIs;

import SlayTheSpireAIMod.util.CombatUtils;
import SlayTheSpireAIMod.util.Move;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

/** AI versus encounter "Lagavulin". */
public class LagavulinCAI extends AbstractCAI {
    public static final Logger logger = LogManager.getLogger(LagavulinCAI.class.getName());

    @Override
    public String getCombat() {
        return "Lagavulin";
    }

    // TODO play powers during dormant turns
    @Override
    public Move pickMove() {
        // if a no-negative card can be played, play it
        Move tryFree = FreeCard();
        if(tryFree != null){
            return tryFree;
        }

        CardSequence start = new CardSequence();
        ArrayList<AbstractCard> unplayable = new ArrayList<>();
        for(AbstractCard c : start.simplePlayer.hand){
            if(!c.canUse(AbstractDungeon.player, CombatUtils.getWeakestTarget())){
                unplayable.add(c);
            }
        }
        for(AbstractCard c : unplayable){
            start.simplePlayer.hand.remove(c);
        }

        CardSequence bestState = start.getBestPossibility(x -> heuristic(x, 13));

        if(bestState != start){
            logger.info("Evaluated best state (from LagavulinCAI): " + bestState.toString());
            int bestIndex = AbstractDungeon.player.hand.group.indexOf(bestState.first);
            return new Move(Move.TYPE.CARD, bestIndex,
                    AbstractDungeon.getCurrRoom().monsters.monsters.get(bestState.firstTargetIndex));
        }
        return new Move(Move.TYPE.PASS);
    }
}
