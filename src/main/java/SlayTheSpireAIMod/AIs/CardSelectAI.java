package SlayTheSpireAIMod.AIs;

import SlayTheSpireAIMod.communicationmod.ChoiceScreenUtils;
import basemod.DevConsole;

import java.util.ArrayList;

/** Class which decides what to do given card rewards. */
public class CardSelectAI {
    /** If */
    public static void execute(){
        if(ChoiceScreenUtils.getCurrentChoiceType() != ChoiceScreenUtils.ChoiceType.CARD_REWARD){
            DevConsole.log("not card reward choice");
            return;
        }
        ArrayList<String> choices = ChoiceScreenUtils.getCurrentChoiceList();
        if(choices.size() > 0){
            ChoiceScreenUtils.makeCardRewardChoice(0);
        }
    }

}
