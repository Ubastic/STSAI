package SlayTheSpireAIMod.AIs;

import SlayTheSpireAIMod.communicationmod.ChoiceScreenUtils;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.CursedKey;
import com.megacrit.cardcrawl.rewards.chests.AbstractChest;
import com.megacrit.cardcrawl.rooms.TreasureRoom;
import com.megacrit.cardcrawl.rooms.TreasureRoomBoss;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/** Class which decides what to do at a chest. */
public class ChestAI {
    public static final Logger logger = LogManager.getLogger(ChestAI.class.getName());

    /** Opens the chest unless player owns relic "Cursed Key". */
    public static void execute(){
        logger.info("Executing...");
        if(ChoiceScreenUtils.getCurrentChoiceType() != ChoiceScreenUtils.ChoiceType.CHEST){
            logger.info("Done: choice type not suitable");
            return;
        }
        boolean isBoss = false;
        AbstractChest chest = null;
        if (AbstractDungeon.getCurrRoom() instanceof TreasureRoomBoss) {
            isBoss = true;
            chest = ((TreasureRoomBoss) AbstractDungeon.getCurrRoom()).chest;
        } else if (AbstractDungeon.getCurrRoom() instanceof TreasureRoom) {
            chest = ((TreasureRoom) AbstractDungeon.getCurrRoom()).chest;
        }
        if(chest == null){
            logger.info("Done: chest does not exist in this room");
            return;
        }

        if(chest.isOpen){
            // TODO don't press confirm until actions are processed
            if(ChoiceScreenUtils.isConfirmButtonAvailable()){
                ChoiceScreenUtils.pressConfirmButton();
            }
        }else{
            if(AbstractDungeon.player.hasRelic(CursedKey.ID) && !isBoss){
                logger.info("Skipping Chest");
                ChoiceScreenUtils.pressConfirmButton();
            }else{
                logger.info("Opening chest");
                ChoiceScreenUtils.makeChestRoomChoice(0);
            }
        }
        logger.info("Done");
    }
}
