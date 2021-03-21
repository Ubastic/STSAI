package SlayTheSpireAIMod.AIs.CombatAIs;

import SlayTheSpireAIMod.util.CombatUtils;
import SlayTheSpireAIMod.util.Move;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.potions.FruitJuice;
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
        return pickMove(x -> heuristic(x, 0));
    }

    public static Move pickMove(Heuristic h){
        return pickMove(h, GenericCAI::potionEval);
    }

    public static Move pickMove(Heuristic h, PotionEval pe){
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
        // first, remove cards that cannot be played
        // looks only at monster health and damage player will take from attacks
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
                incomingDmg += m.attack.getHitDamage() * m.attack.getHits();
            }
        }

        int willLoseHP = Math.max(0, incomingDmg - state.simplePlayer.block - extraBlock);
        int hpLossFactor =  3 * Math.max(0, willLoseHP - tolerance);
        int aliveMonstersFactor = 5 * aliveMonsters;

        if(aliveMonsters == 0){
            aliveMonstersFactor = -100;
        }

        int strength = state.simplePlayer.strength;
        int strengthA = -5; // strength is less important if little dmg needs to be dealt
        int strengthFactor = strength * strengthA;

        int metallicize = state.simplePlayer.metallicize;
        int metallicizeA = -3;
        int metallicizeFactor = metallicize * metallicizeA;

        return totalHealth + aliveMonstersFactor + hpLossFactor + strengthFactor + metallicizeFactor;
    }

    /** Determine if there are any "safe" cards to play.
     * Ignores negative effects that trigger on playing a card.
     * Ignores no-draw from Battle Trance(+).
     * @return Move Return a Move which costs no energy which can only help the player, null if none exists. */
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
            if(freeCards.contains(card.cardID) && card.costForTurn == 0){
                return new Move(Move.TYPE.CARD, cards.indexOf(card), null);
            }
        }
        return null;
    }
}
