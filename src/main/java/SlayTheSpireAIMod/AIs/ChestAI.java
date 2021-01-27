package SlayTheSpireAIMod.AIs;

import SlayTheSpireAIMod.communicationmod.ChoiceScreenUtils;
import SlayTheSpireAIMod.util.ScreenUpdateUtils;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import java.util.ArrayList;

/** Class which decides what to do at a chest. */
public class ChestAI {
    /** Always open the chest, and let the appropriate AI decide what to take. */
    public static void execute(){
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
