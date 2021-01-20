package SlayTheSpireAIMod.AIs;

import SlayTheSpireAIMod.communicationmod.ChoiceScreenUtils;
import basemod.DevConsole;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import java.util.ArrayList;
import java.util.HashMap;

/** Class which decides what to do given card rewards. */
public class CardSelectAI {
    /** Choose the card on the left. */
    public static void execute(){
        if(ChoiceScreenUtils.getCurrentChoiceType() != ChoiceScreenUtils.ChoiceType.CARD_REWARD){
            return;
        }
        ArrayList<String> choices = ChoiceScreenUtils.getCurrentChoiceList(); // can include "bowl"
        ArrayList<AbstractCard> rewardCards = AbstractDungeon.cardRewardScreen.rewardGroup;
        Deck deck = new Deck(AbstractDungeon.player.masterDeck);
        String choice = deck.chooseCard(rewardCards);
        if(choice != null){
            ChoiceScreenUtils.makeCardRewardChoice(choices.indexOf(choice));
        }else{
            ChoiceScreenUtils.pressCancelButton();
        }
    }
}
