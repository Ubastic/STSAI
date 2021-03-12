package SlayTheSpireAIMod.AIs;

import SlayTheSpireAIMod.communicationmod.ChoiceScreenUtils;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
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
        Deck deck = new Deck(AbstractDungeon.player.masterDeck);
        String choice = deck.chooseCard(rewardCards);
        if(choice != null){
            logger.info("Making choice: " + choice);
            ChoiceScreenUtils.makeCardRewardChoice(choices.indexOf(choice));
        }else{
            logger.info("Skipping this card reward");
            ChoiceScreenUtils.pressCancelButton();
        }
        logger.info("Done");
    }
}
