package SlayTheSpireAIMod.AIs;

import SlayTheSpireAIMod.communicationmod.ChoiceScreenUtils;
import SlayTheSpireAIMod.communicationmod.CommandExecutor;
import SlayTheSpireAIMod.util.ScreenUpdateUtils;
import basemod.DevConsole;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.potions.PotionSlot;
import com.megacrit.cardcrawl.screens.CombatRewardScreen;

import java.util.ArrayList;

/** Class which decides what to do given combat rewards. */
public class CombatRewardAI {
    /** Execute the following strategy:
     *  - Take potions if slots are not full
     *  - Take gold
     *  - Take cards according to CardSelectAI
     *  - Take relics
     *  - Take keys */
    public static void execute(){
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
//
//            int goldIndex = choices.indexOf("gold");
//            int cardIndex1 = choices.indexOf("card");
//
//            if(choices.contains("gold")){
////                ChoiceScreenUtils.makeCombatRewardChoice(ChoiceScreenUtils.get);
//            }
//            if(choices.contains("card")){
//                CommandExecutor.executeCommand("choose card");
//                CardSelectAI.execute();
//            }
//            if(choices.size() > 1){
//                ChoiceScreenUtils.makeCombatRewardChoice(0);
//                ChoiceScreenUtils.makeCombatRewardChoice(1);
//            }
        }catch(Exception e){
            DevConsole.log(e.toString());
        }


    }
}
