package SlayTheSpireAIMod.AIs;

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
    public static final Logger logger = LogManager.getLogger(GridSelectAI.class.getName());

    /** If a confirm screen is up, select confirm. Otherwise select and confirm an option. */
    public static void execute(){
        logger.info("Executing...");
        if(ChoiceScreenUtils.getCurrentChoiceType() != ChoiceScreenUtils.ChoiceType.GRID){
            logger.info("Done: choice type not suitable");
            return;
        }
        ArrayList<String> choices = ChoiceScreenUtils.getCurrentChoiceList();
        logger.info("Choosing between: " + choices.toString());
        GridCardSelectScreen screen = AbstractDungeon.gridSelectScreen;
        if(screen.confirmScreenUp){
            logger.info("Pressing confirm");
            ChoiceScreenUtils.pressConfirmButton();
            logger.info("Done");
            return;
        }

        int numCards = ReflectionHacks.getPrivate(AbstractDungeon.gridSelectScreen, GridCardSelectScreen.class, "numCards");
        String tipMsg = ReflectionHacks.getPrivate(AbstractDungeon.gridSelectScreen, GridCardSelectScreen.class, "tipMsg");
        logger.info("Number of Cards to Select:" + numCards);
        logger.info("Tip Message: " + tipMsg);

        ArrayList<AbstractCard> gridScreenCards = ChoiceScreenUtils.getGridScreenCards();
        if(gridScreenCards.size() == 0){
            logger.info("Done: grid contains no cards");
            return;
        }
        AbstractCard defaultChoice = gridScreenCards.get(0);

        if(screen.forUpgrade){
            logger.info("Processing Upgrade");
            Deck deck = new Deck(AbstractDungeon.player.masterDeck);
            for(int i = 0; i < numCards; i++){
                AbstractCard toUpgrade = deck.upgradeCard();
                if(AbstractDungeon.gridSelectScreen.selectedCards.contains(toUpgrade)){
                    toUpgrade = deck.upgradeCard();
                }
                choose(toUpgrade);
            }
        }else if(screen.forTransform){
            logger.info("Processing Transform");
            Deck deck = new Deck(AbstractDungeon.player.masterDeck);
            for(int i = 0; i < numCards; i++){
                AbstractCard toTransform = deck.transformCard();
                choose(toTransform);
            }
        }else if(screen.forPurge){
            logger.info("Processing Purge");
            Deck deck = new Deck(AbstractDungeon.player.masterDeck);
            for(int i = 0; i < numCards; i++){
                AbstractCard toRemove = deck.removeCard();
                choose(toRemove);
            }
        }else if(AbstractDungeon.actionManager.currentAction instanceof DiscardPileToTopOfDeckAction){
            // TODO
            // possibilities: headbutt
            logger.info("Processing DiscardPileToTopOfDeck");
            choose(defaultChoice);
        }else if(tipMsg.equals("Choose a card for Bottled Flame.")){
            // TODO
            logger.info("Processing Bottled Flame");
            choose(defaultChoice);
        }else if(tipMsg.equals("Choose a card for Bottled Lightning.")){
            // TODO
            logger.info("Processing Bottled Lightning");
            choose(defaultChoice);
        }else if(tipMsg.equals("Choose a card for Bottled Tornado.")){
            // TODO
            logger.info("Processing Bottled Tornado");
            choose(defaultChoice);
        }else if(tipMsg.equals("Choose 3 cards for Astrolabe.")){
            // FIXME 3 cards removed but not added
            logger.info("Processing Astrolabe");
            CardGroup onGrid = AbstractDungeon.player.masterDeck.getPurgeableCards();
            for(AbstractCard selected : AbstractDungeon.gridSelectScreen.selectedCards){
                onGrid.removeCard(selected);
            }

            Deck deck = new Deck(onGrid);
            for(int i = 0; i < numCards; i++){
                AbstractCard toTransform = deck.transformCard();
                choose(toTransform);
            }
        }else if(tipMsg.equals("The Bell Tolls...")){
            logger.info("Processing Toll of the Bell");
            logger.info("Pressing confirm");
            ChoiceScreenUtils.pressConfirmButton();
        }
        else{
            // TODO
            // possibilities: duplicator
            logger.info("Processing unknown grid selection");
            choose(defaultChoice);
        }
        logger.info("Done");
    }

    public static void choose(AbstractCard choice){
        try{
            ArrayList<AbstractCard> gridScreenCards = ChoiceScreenUtils.getGridScreenCards();
            GridCardSelectScreenPatch.hoverCard = gridScreenCards.get(gridScreenCards.indexOf(choice));
            GridCardSelectScreenPatch.replaceHoverCard = true;
            logger.info("Making choice: " + choice);
            AbstractDungeon.gridSelectScreen.update();
        }catch(Exception e){
            logger.info("Failed to make choice: " + choice + ". Error: " + e.getMessage());
        }
    }
}
