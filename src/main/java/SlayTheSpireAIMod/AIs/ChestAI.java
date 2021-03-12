package SlayTheSpireAIMod.AIs;

import SlayTheSpireAIMod.STSAIMod;
import SlayTheSpireAIMod.communicationmod.ChoiceScreenUtils;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rewards.chests.AbstractChest;
import com.megacrit.cardcrawl.rooms.TreasureRoom;
import com.megacrit.cardcrawl.rooms.TreasureRoomBoss;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/** Class which decides what to do at a chest. */
public class ChestAI {
    public static final Logger logger = LogManager.getLogger(ChestAI.class.getName());

    /** Always open the chest, and let the appropriate AI decide what to take. */
    public static void execute(){
        logger.info("Executing...");
        // TODO
        if(ChoiceScreenUtils.getCurrentChoiceType() != ChoiceScreenUtils.ChoiceType.CHEST){
            logger.info("Done: choice type not suitable");
            return;
        }
        AbstractChest chest = null;
        if (AbstractDungeon.getCurrRoom() instanceof TreasureRoomBoss) {
            chest = ((TreasureRoomBoss) AbstractDungeon.getCurrRoom()).chest;
        } else if (AbstractDungeon.getCurrRoom() instanceof TreasureRoom) {
            chest = ((TreasureRoom) AbstractDungeon.getCurrRoom()).chest;
        }
        assert chest != null;
        if(chest.isOpen){
            // TODO don't press confirm until actions are processed
            if(ChoiceScreenUtils.isConfirmButtonAvailable()){
                ChoiceScreenUtils.pressConfirmButton();
            }
        }else{
            logger.info("Opening chest");
            ChoiceScreenUtils.makeChestRoomChoice(0); //open the chest
        }
        logger.info("Done");


    }
}
