package SlayTheSpireAIMod.AIs;

import SlayTheSpireAIMod.communicationmod.ChoiceScreenUtils;
import SlayTheSpireAIMod.util.ScreenUpdateUtils;

import java.util.ArrayList;

/** Class which decides what to do given boss rewards. */
public class BossRewardAI {
    /** Pick the first option. */
    public static void execute(){
        // TODO
        if(ChoiceScreenUtils.getCurrentChoiceType() != ChoiceScreenUtils.ChoiceType.BOSS_REWARD) return;
        ArrayList<String> choices = ChoiceScreenUtils.getCurrentChoiceList();
        ChoiceScreenUtils.makeBossRewardChoice(0);
        ScreenUpdateUtils.update();
        ChoiceScreenUtils.pressConfirmButton();
    }

}
