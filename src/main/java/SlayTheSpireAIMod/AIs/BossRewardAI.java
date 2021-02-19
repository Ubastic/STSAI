package SlayTheSpireAIMod.AIs;

import SlayTheSpireAIMod.STSAIMod;
import SlayTheSpireAIMod.communicationmod.ChoiceScreenUtils;
import SlayTheSpireAIMod.util.ScreenUpdateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

/** Class which decides what to do given boss rewards. */
public class BossRewardAI {
    public static final Logger logger = LogManager.getLogger(STSAIMod.class.getName());

    /** Pick the first option. */
    public static void execute(){
        // TODO
        logger.info("Executing BossRewardAI");
        if(ChoiceScreenUtils.getCurrentChoiceType() != ChoiceScreenUtils.ChoiceType.BOSS_REWARD) return;
        ArrayList<String> choices = ChoiceScreenUtils.getCurrentChoiceList();
        ChoiceScreenUtils.makeBossRewardChoice(0);
        ScreenUpdateUtils.update();
        ChoiceScreenUtils.pressConfirmButton();
    }

}
