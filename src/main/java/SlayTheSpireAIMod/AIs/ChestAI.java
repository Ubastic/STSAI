package SlayTheSpireAIMod.AIs;

import SlayTheSpireAIMod.STSAIMod;
import SlayTheSpireAIMod.communicationmod.ChoiceScreenUtils;
import SlayTheSpireAIMod.util.ScreenUpdateUtils;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

/** Class which decides what to do at a chest. */
public class ChestAI {
    public static final Logger logger = LogManager.getLogger(STSAIMod.class.getName());

    /** Always open the chest, and let the appropriate AI decide what to take. */
    public static void execute(){
        logger.info("Executing ChestAi");
        // FIXME glitch when pressed after chest already opened
        if(ChoiceScreenUtils.getCurrentChoiceType() != ChoiceScreenUtils.ChoiceType.CHEST) return;
        ChoiceScreenUtils.makeChestRoomChoice(0); //open the chest
        ScreenUpdateUtils.update();
        if(ChoiceScreenUtils.getCurrentChoiceType() == ChoiceScreenUtils.ChoiceType.COMBAT_REWARD){
            CombatRewardAI.execute();
        }
        if(ChoiceScreenUtils.getCurrentChoiceType() == ChoiceScreenUtils.ChoiceType.BOSS_REWARD){
            BossRewardAI.execute();
        }
    }
}
