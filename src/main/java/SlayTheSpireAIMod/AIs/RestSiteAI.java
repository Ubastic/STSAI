package SlayTheSpireAIMod.AIs;

import SlayTheSpireAIMod.AIs.CombatAIs.HexaghostCAI;
import SlayTheSpireAIMod.communicationmod.ChoiceScreenUtils;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.RestRoom;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

/** Class which decides what to do at a rest site. */
public class RestSiteAI {
    public static final Logger logger = LogManager.getLogger(RestSiteAI.class.getName());

    /** Rest if health is below 60, upgrade otherwise. */
    public static void execute() {
        logger.info("Executing...");
        if(ChoiceScreenUtils.getCurrentChoiceType() != ChoiceScreenUtils.ChoiceType.REST){
            logger.info("Done: choice type not suitable");
            return;
        }
        ArrayList<String> choices = ChoiceScreenUtils.getCurrentChoiceList();
        if(ChoiceScreenUtils.isConfirmButtonAvailable()){
            logger.info("Pressing confirm");
            ChoiceScreenUtils.pressConfirmButton();
            logger.info("Done");
            return;
        }
        if(((RestRoom)AbstractDungeon.getCurrRoom()).campfireUI.somethingSelected){
            logger.info("Done: something was already selected");
            return;
        }

        int needRest = 59;
        String bossKey = AbstractDungeon.bossKey;
        if(bossKey.equals(HexaghostCAI.KEY)){
            needRest = 49;
        }

        // ignore options that are not rest/smith for now TODO
        int health = AbstractDungeon.player.currentHealth;
        if(health <= needRest){
            if(choices.contains("rest")){
                choose("rest");
                return;
            }
        }else{
           if(choices.contains("smith")){
               choose("smith");
               return;
           }
        }

        // if rest/smith unavailable and at least one other is, pick the first one
        if(choices.size() > 0){
            choose(choices.get(0));
        }
        logger.info("Done");

    }

    public static void choose(String choice){
        try{
            ArrayList<String> choices = ChoiceScreenUtils.getCurrentChoiceList();
            logger.info("Making choice: " + choice);
            ChoiceScreenUtils.makeRestRoomChoice(choices.indexOf(choice));
        }catch(Exception e){
            logger.info("Failed to make choice: " + choice + ". Error: " + e.getMessage());
        }
    }
}
