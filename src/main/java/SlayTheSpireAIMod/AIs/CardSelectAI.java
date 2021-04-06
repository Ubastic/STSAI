package SlayTheSpireAIMod.AIs;

import SlayTheSpireAIMod.communicationmod.ChoiceScreenUtils;
import com.megacrit.cardcrawl.actions.unique.DiscoveryAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.colorless.DramaticEntrance;
import com.megacrit.cardcrawl.cards.colorless.Finesse;
import com.megacrit.cardcrawl.cards.colorless.FlashOfSteel;
import com.megacrit.cardcrawl.cards.red.*;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

/** Class which decides what to do given card rewards. */
public class CardSelectAI {
    public static final Logger logger = LogManager.getLogger(CardSelectAI.class.getName());

    /** Choose the card on the left. */
    public static void execute(){
        logger.info("Executing...");
        if(ChoiceScreenUtils.getCurrentChoiceType() != ChoiceScreenUtils.ChoiceType.CARD_REWARD){
            logger.info("Done: choice type not suitable");
            return;
        }
        ArrayList<String> choices = ChoiceScreenUtils.getCurrentChoiceList(); // can include "bowl"
        logger.info("Choosing between: " + choices.toString());
        ArrayList<AbstractCard> rewardCards = AbstractDungeon.cardRewardScreen.rewardGroup;

        String choice = null;
        if(AbstractDungeon.actionManager.currentAction instanceof DiscoveryAction){
            // Attack/Skill/Power Potion
            logger.info("Choosing for Discovery Action...");
            AbstractCard best = bestDiscover(rewardCards);
            if(best != null){
                choice = best.name.toLowerCase();
            }
        }else{
            Deck deck = new Deck(AbstractDungeon.player.masterDeck);
            AbstractCard chosenCard = deck.chooseCard(rewardCards);
            if(chosenCard != null){
                choice = chosenCard.name.toLowerCase();
            }
        }

        if(choice != null){
            logger.info("Making choice: " + choice);
            ChoiceScreenUtils.makeCardRewardChoice(choices.indexOf(choice));
        }else{
            logger.info("Skipping this card selection");
            ChoiceScreenUtils.pressCancelButton();
        }
        logger.info("Done");
    }

    /**
     * Returns the best card to discover (add 0-cost copy to hand) from the specified options.
     * Returns null if no card is worth discovering.
     *
     * @param options the potential cards to discover
     * @return        the best card to discover from the specified options, null if none are good
     * */
    public static AbstractCard bestDiscover(ArrayList<AbstractCard> options){
        if(options.size() == 0){
            return null;
        }
        AbstractCard first = options.get(0);
        if(first.color == AbstractCard.CardColor.COLORLESS){
            return bestColorlessDiscover(options);
        }else if(first.type == AbstractCard.CardType.ATTACK){
            return bestAttackDiscover(options);
        }else if(first.type == AbstractCard.CardType.SKILL){
            return bestSkillDiscover(options);
        }else if(first.type == AbstractCard.CardType.POWER){
            return bestPowerDiscover(options);
        }
        return null;
    }

    /**
     * Returns the best colorless card to discover (add 0-cost copy to hand) from the specified options.
     * Returns null if no card is worth discovering.
     *
     * @param options the potential colorless cards to discover
     * @return        the best card to discover from the specified options, null if none are good
     * */
    public static AbstractCard bestColorlessDiscover(ArrayList<AbstractCard> options){
        String[] colorless = {Finesse.ID, FlashOfSteel.ID, DramaticEntrance.ID};
        return bestCard(options, colorless);
    }

    /**
     * Returns the best attack card to discover (add 0-cost copy to hand) from the specified options.
     * Returns null if no card is worth discovering.
     *
     * @param choices the potential attack cards to discover
     * @return        the best card to discover from the specified options, null if none are good
     * */
    public static AbstractCard bestAttackDiscover(ArrayList<AbstractCard> choices){
        String[] attacks = { Reaper.ID, ThunderClap.ID, Cleave.ID, IronWave.ID, Rampage.ID, PommelStrike.ID, Pummel.ID, Clothesline.ID, TwinStrike.ID, Uppercut.ID, Carnage.ID, Bludgeon.ID };
        return bestCard(choices, attacks);
    }

    /**
     * Returns the best skill card to discover (add 0-cost copy to hand) from the specified options.
     * Returns null if no card is worth discovering.
     *
     * @param choices the potential skill cards to discover
     * @return        the best card to discover from the specified options, null if none are good
     * */
    public static AbstractCard bestSkillDiscover(ArrayList<AbstractCard> choices){
        String[] skills = { ShrugItOff.ID, GhostlyArmor.ID, FlameBarrier.ID, Shockwave.ID, Impervious.ID };
        return bestCard(choices, skills);
    }

    /**
     * Returns the best power card to discover (add 0-cost copy to hand) from the specified options.
     * Returns null if no card is worth discovering.
     *
     * @param choices the potential power cards to discover
     * @return        the best card to discover from the specified options, null if none are good
     * */
    public static AbstractCard bestPowerDiscover(ArrayList<AbstractCard> choices){
        // ranked from worst -> best
        String[] powers = {FireBreathing.ID, FeelNoPain.ID, Evolve.ID, Barricade.ID, Metallicize.ID, Inflame.ID,
                Juggernaut.ID, DemonForm.ID };
        return bestCard(choices, powers);
    }

    /**
     * Returns the best card from the specified options according to the specified ranking.
     * Returns null if no card is ranked.
     *
     * @param choices the potential cards to select
     * @param ranking the cardIDs of acceptable cards to select in worst to best order
     * @return        the best card to select from choices according to ranking, null if none are ranked
     * */
    private static AbstractCard bestCard(ArrayList<AbstractCard> choices, String[] ranking){
        int bestIndex = -1;
        AbstractCard best = null;
        for(AbstractCard choice : choices){
            int index = ArrayUtils.indexOf(ranking, choice.cardID);
            if(index > bestIndex){
                bestIndex = index;
                best = choice;
            }
        }
        return best;
    }
}
