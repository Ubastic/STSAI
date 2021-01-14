package SlayTheSpireAIMod.AIs;

import SlayTheSpireAIMod.communicationmod.ChoiceScreenUtils;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import java.util.ArrayList;

/** Class which decides what to do at a hand choice. */
public class HandSelectAI {
    /** Pick the leftmost option(s). */
    public static void execute(){
        if(ChoiceScreenUtils.getCurrentChoiceType() != ChoiceScreenUtils.ChoiceType.HAND_SELECT) return;
        int toSelect = AbstractDungeon.handCardSelectScreen.numCardsToSelect;
        ArrayList<String> choices = ChoiceScreenUtils.getCurrentChoiceList();
        for(int i = 0; i < toSelect; i++){
            ChoiceScreenUtils.makeHandSelectScreenChoice(0);
        }
        ChoiceScreenUtils.pressConfirmButton();
    }

}
