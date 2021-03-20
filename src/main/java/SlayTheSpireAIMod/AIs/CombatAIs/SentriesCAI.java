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
        return GenericCAI.pickMove(x -> heuristic(x, 0));
    }

    /**
     * Returns the evaluation of the given state. Lower is better.
     *
     * @param state the state to be evaluated.
     * @return how good the state is
     * */
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
                incomingDmg += m.attack.getHitDamage(); // always only 1 hit
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
