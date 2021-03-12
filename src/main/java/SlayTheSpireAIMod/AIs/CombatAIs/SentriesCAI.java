package SlayTheSpireAIMod.AIs.CombatAIs;

import SlayTheSpireAIMod.util.CombatUtils;
import SlayTheSpireAIMod.util.Move;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

public class SentriesCAI extends AbstractCAI {
    public static final Logger logger = LogManager.getLogger(SentriesCAI.class.getName());

    @Override
    public String getCombat() {
        return "3 Sentries";
    }

    @Override
    public Move pickMove() {
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

        CardSequence bestState = start.getBestPossibility(x -> heuristic(x, 0));

        if(bestState != start){
            logger.info("Evaluated best state: " + bestState.toString());
            int bestIndex = AbstractDungeon.player.hand.group.indexOf(bestState.first);
            return new Move(Move.TYPE.CARD, bestIndex,
                    AbstractDungeon.getCurrRoom().monsters.monsters.get(bestState.firstTargetIndex));
        }
        return new Move(Move.TYPE.PASS);
    }

    /** @param state The state to be evaluated.
     * Evaluation of the given state (lower is better).
     * @return int Return a measure of how good a state is. */
    public static int heuristic(CardSequence state, int tolerance){
        int aliveMonsters = 0;
        int totalHealth = 0;
        int incomingDmg = 0;

        int extraBlock = 0;
        extraBlock += state.simplePlayer.metallicize;

        if(AbstractDungeon.player.hasPower("Plated Armor")){
            extraBlock += AbstractDungeon.player.getPower("Plated Armor").amount;
        }

        for(CombatUtils.SimpleMonster m : state.simpleMonsters){
            if(m.isAlive()){
                aliveMonsters += 1;
                totalHealth += m.health;
                incomingDmg += m.attack.getDamage();
            }
        }

        int willLoseHP = Math.max(0, incomingDmg - state.simplePlayer.block - extraBlock);
        int hpLossFactor =  3 * Math.max(0, willLoseHP - tolerance);
        int aliveMonstersFactor = 5 * aliveMonsters;

        if(aliveMonsters == 0){
            aliveMonstersFactor = -100;
        }

        int strength = state.simplePlayer.strength;
        int strengthA = -5;
        int strengthFactor = strength * strengthA;

        int metallicize = state.simplePlayer.metallicize;
        int metallicizeA = -3;
        int metallicizeFactor = metallicize * metallicizeA;

        // it is better to kill either the left or right sentry first
        int multiSentryFactor = 0;
        if(aliveMonsters == 3){
            CombatUtils.SimpleMonster left = state.simpleMonsters.get(0);
            CombatUtils.SimpleMonster right = state.simpleMonsters.get(2);
            multiSentryFactor = Math.min(left.health, right.health);
        }

        return totalHealth + aliveMonstersFactor + hpLossFactor + strengthFactor + metallicizeFactor +
                multiSentryFactor;
    }

}
