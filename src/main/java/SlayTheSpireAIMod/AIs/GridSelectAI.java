package SlayTheSpireAIMod.AIs;

import SlayTheSpireAIMod.STSAIMod;
import SlayTheSpireAIMod.communicationmod.ChoiceScreenUtils;
import SlayTheSpireAIMod.communicationmod.patches.GridCardSelectScreenPatch;
import basemod.ReflectionHacks;
import com.megacrit.cardcrawl.actions.unique.DiscardPileToTopOfDeckAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.screens.select.GridCardSelectScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

/** Class which decides what to do at a grid choice. */
public class GridSelectAI {
    public static final Logger logger = LogManager.getLogger(STSAIMod.class.getName());

    /** If a confirm screen is up, select confirm. Otherwise select and confirm an option. */
    public static void execute(){
        logger.info("Executing GridSelectAI");
        if(ChoiceScreenUtils.getCurrentChoiceType() != ChoiceScreenUtils.ChoiceType.GRID) return;
        ArrayList<String> choices = ChoiceScreenUtils.getCurrentChoiceList();
        logger.info(choices.toString());
        GridCardSelectScreen screen = AbstractDungeon.gridSelectScreen;
        if(screen.confirmScreenUp){
            ChoiceScreenUtils.pressConfirmButton();
            return;
        }

        int numCards = ReflectionHacks.getPrivate(AbstractDungeon.gridSelectScreen, GridCardSelectScreen.class, "numCards");
        String tipMsg = ReflectionHacks.getPrivate(AbstractDungeon.gridSelectScreen, GridCardSelectScreen.class, "tipMsg");
        logger.info("Tip Message: " + tipMsg);

        if(screen.forUpgrade){
            Deck deck = new Deck(AbstractDungeon.player.masterDeck);
            for(int i = 0; i < numCards; i++){
                AbstractCard toUpgrade = deck.upgradeCard();
                ArrayList<AbstractCard> gridScreenCards = ChoiceScreenUtils.getGridScreenCards();
                GridCardSelectScreenPatch.hoverCard = gridScreenCards.get(gridScreenCards.indexOf(toUpgrade));
                GridCardSelectScreenPatch.replaceHoverCard = true;
                AbstractDungeon.gridSelectScreen.update();

            }
            ChoiceScreenUtils.pressConfirmButton();
        }else if(screen.forTransform){
            Deck deck = new Deck(AbstractDungeon.player.masterDeck);
            for(int i = 0; i < numCards; i++){
                AbstractCard toTransform = deck.transformCard();
                ArrayList<AbstractCard> gridScreenCards = ChoiceScreenUtils.getGridScreenCards();
                GridCardSelectScreenPatch.hoverCard = gridScreenCards.get(gridScreenCards.indexOf(toTransform));
                GridCardSelectScreenPatch.replaceHoverCard = true;
                AbstractDungeon.gridSelectScreen.update();
            }
            ChoiceScreenUtils.pressConfirmButton();
        }else if(screen.forPurge){
            Deck deck = new Deck(AbstractDungeon.player.masterDeck);
            for(int i = 0; i < numCards; i++){
                AbstractCard toRemove = deck.removeCard();
                ArrayList<AbstractCard> gridScreenCards = ChoiceScreenUtils.getGridScreenCards();
                GridCardSelectScreenPatch.hoverCard = gridScreenCards.get(gridScreenCards.indexOf(toRemove));
                GridCardSelectScreenPatch.replaceHoverCard = true;
                AbstractDungeon.gridSelectScreen.update();
            }
            ChoiceScreenUtils.pressConfirmButton();
        }else if(AbstractDungeon.actionManager.currentAction instanceof DiscardPileToTopOfDeckAction){
            // TODO
            // possibilities: headbutt
            ChoiceScreenUtils.makeGridScreenChoice(0);
        }else if(tipMsg.equals("Choose a card for Bottled Flame.")){
            // TODO
            ChoiceScreenUtils.makeGridScreenChoice(0);
        }else if(tipMsg.equals("Choose a card for Bottled Lightning.")){
            // TODO
            ChoiceScreenUtils.makeGridScreenChoice(0);
        }else if(tipMsg.equals("Choose a card for Bottled Tornado.")){
            // TODO
            ChoiceScreenUtils.makeGridScreenChoice(0);
        }else if(tipMsg.equals("Choose 3 cards for Astrolabe.")){
            CardGroup onGrid = AbstractDungeon.player.masterDeck.getPurgeableCards();
            for(AbstractCard selected : AbstractDungeon.gridSelectScreen.selectedCards){
                onGrid.removeCard(selected);
            }

            Deck deck = new Deck(onGrid);
            for(int i = 0; i < numCards; i++){
                AbstractCard toTransform = deck.transformCard();
                ArrayList<AbstractCard> gridScreenCards = ChoiceScreenUtils.getGridScreenCards();
                GridCardSelectScreenPatch.hoverCard = gridScreenCards.get(gridScreenCards.indexOf(toTransform));
                logger.info("index: " + gridScreenCards.indexOf(toTransform));
                GridCardSelectScreenPatch.replaceHoverCard = true;
                AbstractDungeon.gridSelectScreen.update();
            }
            ChoiceScreenUtils.pressConfirmButton();
        }else{
            // TODO
            // possibilities: duplicator

        }
    }
}
