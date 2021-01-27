package SlayTheSpireAIMod.AIs;

import SlayTheSpireAIMod.communicationmod.ChoiceScreenUtils;
import SlayTheSpireAIMod.util.ScreenUpdateUtils;
import com.megacrit.cardcrawl.daily.mods.AbstractDailyMod;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import java.util.ArrayList;

/** Class which decides what to do at a shop (in room or shop screen). */
public class ShopAI {
    /** Buy nothing. */
    public static void execute(){
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
