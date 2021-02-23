package SlayTheSpireAIMod.AIs;

import SlayTheSpireAIMod.STSAIMod;
import SlayTheSpireAIMod.communicationmod.ChoiceScreenUtils;
import com.megacrit.cardcrawl.actions.unique.GamblingChipAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

/** Class which decides what to do at a hand choice. */
public class HandSelectAI {
    public static final Logger logger = LogManager.getLogger(STSAIMod.class.getName());

    /** Pick the leftmost option(s). */
    public static void execute(){
        logger.info("Executing HandSelectAI");
        if(ChoiceScreenUtils.getCurrentChoiceType() != ChoiceScreenUtils.ChoiceType.HAND_SELECT) return;
        int toSelect = AbstractDungeon.handCardSelectScreen.numCardsToSelect;
        ArrayList<String> choices = ChoiceScreenUtils.getCurrentChoiceList();
        if(choices.size() == 0){
            logger.info("HandSelectAI: no choices");
            return;
        }
        if(AbstractDungeon.actionManager.currentAction instanceof GamblingChipAction){
            logger.info("HandSelectAI: Processing Gambling Chip");
            if(ChoiceScreenUtils.isConfirmButtonAvailable()){
                logger.info("HandSelectAI: Pressing confirm");
                ChoiceScreenUtils.pressConfirmButton();
            }
            return;
        }


        for(int i = 0; i < toSelect; i++){
            ChoiceScreenUtils.makeHandSelectScreenChoice(0);
        }
        ChoiceScreenUtils.pressConfirmButton();
    }

}
