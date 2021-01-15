package SlayTheSpireAIMod.AIs;

import SlayTheSpireAIMod.communicationmod.ChoiceScreenUtils;
import SlayTheSpireAIMod.communicationmod.InvalidCommandException;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import java.util.ArrayList;

/** Class which decides what to do at a rest site. */
public class RestSiteAI {
    /** Rest. */
    public static void execute() {
        if(ChoiceScreenUtils.getCurrentChoiceType() != ChoiceScreenUtils.ChoiceType.REST) return;
        ArrayList<String> choices = ChoiceScreenUtils.getCurrentChoiceList();
        if(choices.size() > 0){
            ChoiceScreenUtils.makeRestRoomChoice(0);
            ChoiceScreenUtils.pressConfirmButton();
        }
        if(ChoiceScreenUtils.isConfirmButtonAvailable()){
            ChoiceScreenUtils.pressConfirmButton();
        }
    }
}
