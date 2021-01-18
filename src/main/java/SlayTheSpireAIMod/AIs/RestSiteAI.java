package SlayTheSpireAIMod.AIs;

import SlayTheSpireAIMod.communicationmod.ChoiceScreenUtils;
import SlayTheSpireAIMod.communicationmod.InvalidCommandException;
import SlayTheSpireAIMod.util.ScreenUpdateUtils;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import java.util.ArrayList;

/** Class which decides what to do at a rest site. */
public class RestSiteAI {
    /** Rest if health is below 60, upgrade otherwise. */
    public static void execute() {
        if(ChoiceScreenUtils.getCurrentChoiceType() != ChoiceScreenUtils.ChoiceType.REST) return;
        ArrayList<String> choices = ChoiceScreenUtils.getCurrentChoiceList();
        if(ChoiceScreenUtils.isConfirmButtonAvailable()){
            ChoiceScreenUtils.pressConfirmButton();
        }

        // ignore options that are not rest/smith for now
        int health = AbstractDungeon.player.currentHealth;
        if(health < 60){
            if(choices.contains("rest")){
                ChoiceScreenUtils.makeRestRoomChoice(choices.indexOf("rest"));
                ChoiceScreenUtils.pressConfirmButton();
                return;
            }
        }else{
           if(choices.contains("smith")){
               ChoiceScreenUtils.makeRestRoomChoice(choices.indexOf("smith"));
               return;
           }
        }

        // if rest/smith unavailable and at least one other is, pick the first one
        if(choices.size() > 0){
            ChoiceScreenUtils.makeRestRoomChoice(0);
//            ChoiceScreenUtils.pressConfirmButton();
        }

    }
}
