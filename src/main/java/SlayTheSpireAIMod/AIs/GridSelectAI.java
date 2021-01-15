package SlayTheSpireAIMod.AIs;

import SlayTheSpireAIMod.communicationmod.ChoiceScreenUtils;
import SlayTheSpireAIMod.util.ScreenUpdateUtils;
import com.megacrit.cardcrawl.actions.unique.DiscardPileToTopOfDeckAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.screens.select.GridCardSelectScreen;

import java.util.ArrayList;

/** Class which decides what to do at a grid choice. */
public class GridSelectAI {
    /** If a confirm screen is up, select confirm. Otherwise select and confirm an option. */
    public static void execute(){
        if(ChoiceScreenUtils.getCurrentChoiceType() != ChoiceScreenUtils.ChoiceType.GRID) return;
        ArrayList<String> choices = ChoiceScreenUtils.getCurrentChoiceList();

        GridCardSelectScreen screen = AbstractDungeon.gridSelectScreen;
        if(screen.confirmScreenUp){
            ChoiceScreenUtils.pressConfirmButton();
            return;
        }
        if(screen.forUpgrade){
            // upgrade the last card
            ChoiceScreenUtils.makeGridScreenChoice(choices.size() - 1);
            ScreenUpdateUtils.update();
            ChoiceScreenUtils.pressConfirmButton();
        }else if(screen.forTransform){
            // transform the first card
            ChoiceScreenUtils.makeGridScreenChoice(0);
            ScreenUpdateUtils.update();
            ChoiceScreenUtils.pressConfirmButton();
        }else if(screen.forPurge){
            // purge the first card
            ChoiceScreenUtils.makeGridScreenChoice(0);
            ScreenUpdateUtils.update();
            ChoiceScreenUtils.pressConfirmButton();
        }else if(AbstractDungeon.actionManager.currentAction instanceof DiscardPileToTopOfDeckAction){
            // possibilities: headbutt
            ChoiceScreenUtils.makeGridScreenChoice(0);
        }else{
            //duplicator

        }
    }
}
