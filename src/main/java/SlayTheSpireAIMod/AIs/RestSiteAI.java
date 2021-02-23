package SlayTheSpireAIMod.AIs;

import SlayTheSpireAIMod.STSAIMod;
import SlayTheSpireAIMod.communicationmod.ChoiceScreenUtils;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.RestRoom;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

/** Class which decides what to do at a rest site. */
public class RestSiteAI {
    public static final Logger logger = LogManager.getLogger(STSAIMod.class.getName());

    /** Rest if health is below 60, upgrade otherwise. */
    public static void execute() {
        logger.info("Executing RestSiteAI");
        if(ChoiceScreenUtils.getCurrentChoiceType() != ChoiceScreenUtils.ChoiceType.REST) return;
        ArrayList<String> choices = ChoiceScreenUtils.getCurrentChoiceList();
        if(ChoiceScreenUtils.isConfirmButtonAvailable()){
            ChoiceScreenUtils.pressConfirmButton();
        }
        if(((RestRoom)AbstractDungeon.getCurrRoom()).campfireUI.somethingSelected){
            logger.info("RestSiteAI: something was already selected");
            return;
        }

        // ignore options that are not rest/smith for now TODO
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
        }

    }
}
