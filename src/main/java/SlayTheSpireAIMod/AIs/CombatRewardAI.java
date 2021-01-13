package SlayTheSpireAIMod.AIs;

import SlayTheSpireAIMod.communicationmod.ChoiceScreenUtils;
import SlayTheSpireAIMod.communicationmod.CommandExecutor;
import basemod.DevConsole;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.screens.CombatRewardScreen;

import java.util.ArrayList;

/** Class which decides what to do given combat rewards. */
public class CombatRewardAI {
    /** TODO */
    public static void execute(){
        try{
            if(ChoiceScreenUtils.getCurrentChoiceType() != ChoiceScreenUtils.ChoiceType.COMBAT_REWARD) return;
            ArrayList<String> choices = ChoiceScreenUtils.getCurrentChoiceList();
            for(int i = 0; i < choices.size(); i++){
                String choice = choices.get(i);
                switch(choice){
                    case "potion":
                        String potionName = AbstractDungeon.combatRewardScreen.rewards.get(i).potion.name;
//                        if(AbstractDungeon.player.potions.size())
                        DevConsole.log("Num potions: " + AbstractDungeon.player.potions.size());
                        break;
                    case "gold":
                        ChoiceScreenUtils.makeCombatRewardChoice(i);
                        break;
                    case "stolen_gold":
                        ChoiceScreenUtils.makeCombatRewardChoice(i);
                        break;
                    case "relic":
                        String relicName = AbstractDungeon.combatRewardScreen.rewards.get(i).relic.name;
                        ChoiceScreenUtils.makeCombatRewardChoice(i);
                        break;
                    case "card":
                        ChoiceScreenUtils.makeCombatRewardChoice(i);
                        // TODO find a way to use more broad update()
                        AbstractDungeon.combatRewardScreen.update();
//                        AbstractDungeon current = CardCrawlGame.dungeon;
//                        if(AbstractDungeon.screen != AbstractDungeon.CurrentScreen.CARD_REWARD)
//                            current.update();
                        CardSelectAI.execute();
                        break;
                    case "sapphire_key":
                    case "emerald_key":
                        break;
                }
            }
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
