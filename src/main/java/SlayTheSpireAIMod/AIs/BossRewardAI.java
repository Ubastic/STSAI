package SlayTheSpireAIMod.AIs;

import SlayTheSpireAIMod.communicationmod.ChoiceScreenUtils;

import java.util.ArrayList;

/** Class which decides what to do given boss rewards. */
public class BossRewardAI {
    /** Pick the first option. */
    public static void execute(){
        if(ChoiceScreenUtils.getCurrentChoiceType() != ChoiceScreenUtils.ChoiceType.BOSS_REWARD) return;
        ArrayList<String> choices = ChoiceScreenUtils.getCurrentChoiceList();
        ChoiceScreenUtils.makeBossRewardChoice(0);
    }

}
