package SlayTheSpireAIMod.AIs;

import SlayTheSpireAIMod.communicationmod.ChoiceScreenUtils;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.potions.PotionSlot;
import com.megacrit.cardcrawl.rewards.RewardItem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

/** Class which decides what to do given combat rewards. */
public class CombatRewardAI {
    public static final Logger logger = LogManager.getLogger(CombatRewardAI.class.getName());

    public static void execute(){
        logger.info("Executing...");
        try{
            if(ChoiceScreenUtils.getCurrentChoiceType() != ChoiceScreenUtils.ChoiceType.COMBAT_REWARD){
                logger.info("Done: choice type not suitable");
                return;
            }
            ArrayList<String> choices = ChoiceScreenUtils.getCurrentChoiceList();
            logger.info("Choosing between: " + choices.toString());
            for(int i = 0; i < choices.size(); i++){
                String choice = choices.get(i);
                logger.info("Processing choice: " + choice);
                switch(choice){
                    case "potion":
                        String potionName = AbstractDungeon.combatRewardScreen.rewards.get(i).potion.name;
                        boolean full = true;
                        for(AbstractPotion potion : AbstractDungeon.player.potions){
                            if(potion.getClass() == PotionSlot.class){
                                full = false;
                                break;
                            }
                        }
                        if(!full){
                            choose(i);
                        }else{
                            logger.info("Not taking potion");
                        }
                        break;
                    case "gold":
                    case "stolen_gold":
                    case "emerald_key":
                        choose(i);
                        break;
                    case "relic":
                        String relicName = AbstractDungeon.combatRewardScreen.rewards.get(i).relic.name;
                        choose(i);
                        break;
                    case "card":
                        choose(i);
                        AbstractDungeon.combatRewardScreen.update();
                        CardSelectAI.execute();
                        break;
                    case "sapphire_key":
                        break;
                }
            }
            logger.info("Leaving reward screen");
            ChoiceScreenUtils.pressConfirmButton();
            logger.info("Done");
        }catch(Exception e){
            logger.info("An error occurred:" + e.toString());
        }
    }

    /**
     * Make the specified combat reward choice if it is valid.
     *
     * @param i the 0-based index of the choice to make
     * */
    public static void choose(int i){
        try{
            ArrayList<String> choices = ChoiceScreenUtils.getCurrentChoiceList();
            logger.info("Making choice: " + choices.get(i));
            RewardItem reward = AbstractDungeon.combatRewardScreen.rewards.get(i);
            reward.isDone = true;
        }catch(Exception e){
            logger.info("Failed to make choice: " + i + ". Error: " + e.getMessage());
        }
    }
}
