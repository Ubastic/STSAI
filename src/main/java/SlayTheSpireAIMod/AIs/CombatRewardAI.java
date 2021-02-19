package SlayTheSpireAIMod.AIs;

import SlayTheSpireAIMod.STSAIMod;
import SlayTheSpireAIMod.communicationmod.ChoiceScreenUtils;
import SlayTheSpireAIMod.communicationmod.CommandExecutor;
import SlayTheSpireAIMod.util.ScreenUpdateUtils;
import basemod.DevConsole;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.potions.PotionSlot;
import com.megacrit.cardcrawl.screens.CombatRewardScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

/** Class which decides what to do given combat rewards. */
public class CombatRewardAI {
    public static final Logger logger = LogManager.getLogger(STSAIMod.class.getName());

    /** Execute the following strategy:
     *  - Take potions if slots are not full
     *  - Take gold
     *  - Take cards according to CardSelectAI
     *  - Take relics
     *  - Take keys */
    public static void execute(){
        logger.info("Executing CombatRewardAI");
        try{
            if(ChoiceScreenUtils.getCurrentChoiceType() != ChoiceScreenUtils.ChoiceType.COMBAT_REWARD) return;
            ArrayList<String> choices = ChoiceScreenUtils.getCurrentChoiceList();
            for(int i = 0; i < choices.size(); i++){
                String choice = choices.get(i);
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
                            ChoiceScreenUtils.makeCombatRewardChoice(i);
                        }
                        break;
                    case "gold":
                    case "stolen_gold":
                        ChoiceScreenUtils.makeCombatRewardChoice(i);
                        break;
                    case "relic":
                        String relicName = AbstractDungeon.combatRewardScreen.rewards.get(i).relic.name;
                        ChoiceScreenUtils.makeCombatRewardChoice(i);
                        break;
                    case "card":
                        ChoiceScreenUtils.makeCombatRewardChoice(i);
                        // Skip card select screen
                        ScreenUpdateUtils.update();
                        CardSelectAI.execute();
                        ScreenUpdateUtils.update();
                        break;
                    case "sapphire_key":
                    case "emerald_key":
                        ChoiceScreenUtils.makeCombatRewardChoice(i);
                        break;
                }
            }
            ChoiceScreenUtils.pressConfirmButton();
        }catch(Exception e){
            DevConsole.log(e.toString());
        }


    }
}
