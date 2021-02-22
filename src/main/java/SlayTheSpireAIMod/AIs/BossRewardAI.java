package SlayTheSpireAIMod.AIs;

import SlayTheSpireAIMod.STSAIMod;
import SlayTheSpireAIMod.communicationmod.ChoiceScreenUtils;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
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
        if(ChoiceScreenUtils.isConfirmButtonAvailable()){
            ChoiceScreenUtils.pressConfirmButton();
        }
        ArrayList<String> choices = ChoiceScreenUtils.getCurrentChoiceList();
        ArrayList<String> man = new ArrayList<>();
        for(AbstractRelic relic : AbstractDungeon.bossRelicScreen.relics) {
            man.add(relic.name);
        }
        if(choices.contains("astrolabe")){ // for testing that astrolabe functions properly
           ChoiceScreenUtils.makeBossRewardChoice(choices.indexOf("astrolabe"));
        }else{
            ChoiceScreenUtils.makeBossRewardChoice(0);
        }

    }

}
