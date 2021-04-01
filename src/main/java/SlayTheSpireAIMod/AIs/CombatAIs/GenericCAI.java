package SlayTheSpireAIMod.AIs.CombatAIs;

import SlayTheSpireAIMod.util.CombatUtils;
import SlayTheSpireAIMod.util.Move;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.status.Burn;
import com.megacrit.cardcrawl.cards.status.Slimed;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.potions.FruitJuice;
import com.megacrit.cardcrawl.powers.PlatedArmorPower;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashSet;

public class GenericCAI extends AbstractCAI{
    public static final Logger logger = LogManager.getLogger(GenericCAI.class.getName());

    @Override
    public String getCombat() {
        return null;
    }

    @Override
    public Move pickMove() {
        return pickMove(x -> heuristic(x));
    }

    public static Move pickMove(Heuristic h){
        return pickMove(h, GenericCAI::potionEval);
    }

    /**
     * Returns the evaluated best next Move from the current state.
     * Uses the specified state and potion evaluation functions.
     *
     * @param h  the heuristic to evaluate states
     * @param pe the function to evaluate potions by
     * @return   the evaluated best next Move from the current state
     * */
    public static Move pickMove(Heuristic h, PotionEval pe){
        return pickMove(h, pe, new CardSequence());
    }

    /**
     * Returns the evaluated best next Move from the specified state.
     * Uses the specified state and potion evaluation functions.
     *
     * @param h     the heuristic to evaluate states
     * @param pe    the function to evaluate potions by
     * @param start the initial state to evaluate moves from
     * @return      the evaluated best next Move from the current state
     * */
    public static Move pickMove(Heuristic h, PotionEval pe, CardSequence start){
        Move tryPotion = usePotion(pe);
        if(tryPotion != null){
            return tryPotion;
        }

        // if a no-negative card can be played, play it
        Move tryFree = FreeCard();
        if(tryFree != null){
            return tryFree;
        }

        // play the card that leads to the best state
        CardSequence bestState = start.getBestPossibility(h);

        if(bestState != start){
            logger.info("Evaluated best state: " + bestState.toString());
            int bestIndex = AbstractDungeon.player.hand.group.indexOf(bestState.first);
            return new Move(Move.TYPE.CARD, bestIndex,
                    AbstractDungeon.getCurrRoom().monsters.monsters.get(bestState.firstTargetIndex));
        }
        return new Move(Move.TYPE.PASS);
    }

    /**
     * Returns an evaluation of the usage of a potion.
     *
     * @param p the potion to evaluate
     * @return  the evaluation of how good it is to use a potion. The larger the better.
     *          0 indicates not useful enough to use.
     * */
    public static int potionEval(AbstractPotion p){
        if (p instanceof FruitJuice) {
            return 10;
        }
        return 0;
    }

    /**
     * Returns an evaluation of a state. Lower is better.
     *
     * @param state the state to be evaluated
     * @return      an evaluation of a state. Lower is better
     * */
    public static int heuristic(CardSequence state){
        int terms = 4;
        // playerHP  : expected remaining HP at start of next turn
        // monsterHP : total vulnerable-adjusted monster health plus multiple-monster factor
        // power     : evaluation of player powers
        // status    : number of status cards which are not exhausted
        // [playerHP, monsterHP, power, status]
        int[] values = new int[terms];
        int[] weights = { -3, 1, 1, 1 };

        int aliveMonsters = 0;
        int incomingDmg = 0;

        int totalVAdjHealth = 0;
        for(CombatUtils.SimpleMonster m : state.simpleMonsters){
            if(m.isAlive()){
                aliveMonsters += 1;
                // health is effectively lower if monster will be vulnerable in the future
                int vAdjHealth = m.health;
                int futureVul = m.vulnerable - 1;
                vAdjHealth = Math.max(1, vAdjHealth - futureVul * 4);
                // Potential issue: health at 1 not preferred to >1 and vulnerable
                totalVAdjHealth += vAdjHealth;
                incomingDmg += m.attack.getHitDamage() * m.attack.getHits();
            }
        }

        // PLAYERHP
        int extraBlock = state.simplePlayer.metallicize;
        if(AbstractDungeon.player.hasPower(PlatedArmorPower.POWER_ID)){
            extraBlock += AbstractDungeon.player.getPower(PlatedArmorPower.POWER_ID).amount;
        }
        int willLoseHP = Math.max(0, incomingDmg - state.simplePlayer.block - extraBlock);
        values[0] = state.simplePlayer.health - willLoseHP;

        // MONSTERHP
        if(aliveMonsters == 0){
            values[1] = -100;
        }else{
            values[1] = totalVAdjHealth + 5 * aliveMonsters;
        }

        // POWER
        int powerTerms = 2;
        // [strength, metallicize]
        int[] powerValues = new int[powerTerms];
        int[] powerWeights = { -5, -3 };
        powerValues[0] = state.simplePlayer.strength;
        powerValues[1] = state.simplePlayer.metallicize;
        for(int i = 0; i < powerTerms; i++){
            values[2] += powerWeights[i] * powerValues[i];
        }

        // STATUS
        for(AbstractCard c : state.simplePlayer.hand){
            if(c.cardID.equals(Slimed.ID)){
                values[3] += 1;
            }
            if(c.cardID.equals(Burn.ID)){
                values[3] += 1;
                incomingDmg += c.magicNumber;
            }
        }

        int evaluation = 0;
        for(int i = 0; i < terms; i++){
            evaluation += weights[i] * values[i];
        }
        return evaluation;
    }


//    /**
//     * Returns an evaluation of a state. Lower is better.
//     *
//     * @param state the state to be evaluated
//     * @param dphp  the amount of monsterHP that must be dealt to be willing to lose 1 HP
//     * @return      an evaluation of a state. Lower is better
//     * */
//    public static int heuristic(CardSequence state, int dphp){
//        int terms = 4;
//        // playerHP  : expected remaining HP at start of next turn
//        // monsterHP : total vulnerable-adjusted monster health plus multiple-monster factor
//        // power     : evaluation of player powers
//        // status    : number of status cards which are not exhausted
//        // [playerHP, monsterHP, power, status]
//        int[] values = new int[terms];
//        int[] weights = { -3, 1, 1, 1 };
//
//        int aliveMonsters = 0;
//        int incomingDmg = 0;
//
//        int totalVAdjHealth = 0;
//        for(CombatUtils.SimpleMonster m : state.simpleMonsters){
//            if(m.isAlive()){
//                aliveMonsters += 1;
//                // health is effectively lower if monster will be vulnerable in the future
//                int vAdjHealth = m.health;
//                int futureVul = m.vulnerable - 1;
//                vAdjHealth = Math.max(1, vAdjHealth - futureVul * 4);
//                // Potential issue: health at 1 not preferred to >1 and vulnerable
//                totalVAdjHealth += vAdjHealth;
//                incomingDmg += m.attack.getHitDamage() * m.attack.getHits();
//            }
//        }
//
//        // PLAYERHP
//        int extraBlock = state.simplePlayer.metallicize;
//        if(AbstractDungeon.player.hasPower(PlatedArmorPower.POWER_ID)){
//            extraBlock += AbstractDungeon.player.getPower(PlatedArmorPower.POWER_ID).amount;
//        }
//        int willLoseHP = Math.max(0, incomingDmg - state.simplePlayer.block - extraBlock);
//        values[0] = state.simplePlayer.health - willLoseHP;
//
//        // MONSTERHP
//        if(aliveMonsters == 0){
//            values[1] = -100;
//        }else{
//            values[1] = totalVAdjHealth + 5 * aliveMonsters;
//        }
//
//        // POWER
//        int powerTerms = 2;
//        // [strength, metallicize]
//        int[] powerValues = new int[powerTerms];
//        int[] powerWeights = { -5, -3 };
//        powerValues[0] = state.simplePlayer.strength;
//        powerValues[1] = state.simplePlayer.metallicize;
//        for(int i = 0; i < powerTerms; i++){
//            values[2] += powerWeights[i] * powerValues[i];
//        }
//
//        // STATUS
//        for(AbstractCard c : state.simplePlayer.hand){
//            if(c.cardID.equals(Slimed.ID)){
//                values[3] += 1;
//            }
//            if(c.cardID.equals(Burn.ID)){
//                values[3] += 1;
//                incomingDmg += c.magicNumber;
//            }
//        }
//
//        int evaluation = 0;
//        for(int i = 0; i < terms; i++){
//            evaluation += weights[i] * values[i];
//        }
//        return evaluation;
//    }

    /**
     * Returns a Move which plays a "safe" card. Returns null if none exists.
     * Ignores negative effects that trigger on playing a card.
     * Ignores no-draw from Battle Trance(+).
     *
     * @return a Move which costs no energy which can only help the player. Null if none exists
     * */
    public static Move FreeCard(){
        ArrayList<AbstractCard> cards = AbstractDungeon.player.hand.group;
        HashSet<String> freeCards = new HashSet<>();
        freeCards.add("Flex");
        freeCards.add("Flex+");
        freeCards.add("Havoc+");
        freeCards.add("Warcry+");
        freeCards.add("Battle Trance");
        freeCards.add("Battle Trance+");
        freeCards.add("Infernal Blade+");
        freeCards.add("Intimidate");
        freeCards.add("Intimidate+");
        freeCards.add("Rage");
        freeCards.add("Rage+");
        for(AbstractCard card : cards){
            if(freeCards.contains(card.name) && card.costForTurn == 0){
                return new Move(Move.TYPE.CARD, cards.indexOf(card), null);
            }
        }
        return null;
    }
}
