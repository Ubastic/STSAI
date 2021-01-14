package SlayTheSpireAIMod.AIs;

import SlayTheSpireAIMod.communicationmod.ChoiceScreenUtils;
import basemod.DevConsole;

import java.util.ArrayList;

/** Class which decides what to do given card rewards. */
public class CardSelectAI {
    /** Choose the card on the left. */
    public static void execute(){
        if(ChoiceScreenUtils.getCurrentChoiceType() != ChoiceScreenUtils.ChoiceType.CARD_REWARD){
            return;
        }
        ArrayList<String> choices = ChoiceScreenUtils.getCurrentChoiceList();
        if(choices.size() > 0){
            ChoiceScreenUtils.makeCardRewardChoice(0);
        }
    }

}
