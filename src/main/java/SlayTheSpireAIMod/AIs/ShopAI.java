package SlayTheSpireAIMod.AIs;

import SlayTheSpireAIMod.STSAIMod;
import SlayTheSpireAIMod.communicationmod.ChoiceScreenUtils;
import SlayTheSpireAIMod.util.ScreenUpdateUtils;
import com.megacrit.cardcrawl.daily.mods.AbstractDailyMod;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

/** Class which decides what to do at a shop (in room or shop screen). */
public class ShopAI {
    public static final Logger logger = LogManager.getLogger(STSAIMod.class.getName());

    /** Buy nothing. */
    public static void execute(){
        logger.info("Executing ShopAI");
        // TODO make basic AI
        ChoiceScreenUtils.ChoiceType type = ChoiceScreenUtils.getCurrentChoiceType();
        if(type != ChoiceScreenUtils.ChoiceType.SHOP_ROOM && type != ChoiceScreenUtils.ChoiceType.SHOP_SCREEN) return;
        ArrayList<String> choices = ChoiceScreenUtils.getCurrentChoiceList();
        if(type == ChoiceScreenUtils.ChoiceType.SHOP_ROOM){
            ChoiceScreenUtils.pressConfirmButton();
        }
        else{
            ChoiceScreenUtils.pressCancelButton(); // exit the shop screen
            ScreenUpdateUtils.update();
            AbstractDungeon.shopScreen.update();
            AbstractDungeon.dungeonMapScreen.update();
            AbstractDungeon.currMapNode.room.update();
            AbstractDungeon.scene.update();
            AbstractDungeon.currMapNode.room.eventControllerInput();

            ChoiceScreenUtils.pressConfirmButton();
        }
    }
}
