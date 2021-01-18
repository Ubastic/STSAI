package SlayTheSpireAIMod.AIs;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

/** Class which represents the player's current permanent deck/relics and evaluates changes to it. */
public class Deck {
    ArrayList<AbstractCard> cards; // player's current deck at the start of combat
    boolean hasDamage; // true if deck contains non-starter attacks
    boolean isBarricade;
    boolean isLimitBreak;
    boolean isDemonForm;
    // TODO isSnecko

    public Deck(CardGroup deck){
        cards = deck.group;
        int newAttacks = 0;
        for(AbstractCard c : cards){
            if(c.type == AbstractCard.CardType.ATTACK && !c.cardID.equals("Strike_R")){
                newAttacks += 1;
            }
            switch (c.cardID) {
                case "Barricade":
                    isBarricade = true;
                    break;
                case "Limit Break":
                    isLimitBreak = true;
                    break;
                case "Demon Form":
                    isDemonForm = true;
                    break;
            }
        }
        hasDamage = newAttacks > 1;
    }

    /** Choose what to do given a card reward.
     * @return String Return name of chosen card or null if chosen skip. */
    public String chooseCard(ArrayList<String> options){
        // TODO
        return options.get(0);
    }

    /** @return AbstractCard Return the best card to upgrade in the deck. */
    public AbstractCard getUpgrade(){
        // TODO
        return null;
    }

    /** @param canPass If true, can return null if passing is preferred to removing a card.
     * @return AbstractCard Return the best card to remove in the deck. */
    public AbstractCard getRemove(boolean canPass){
        // Remove removable curse if possible (Ignores curse-relics)
        String[] permCurses = {"AscendersBane", "CurseOfTheBell", "Necronomicurse"};
        HashSet<String> permCurseSet = new HashSet<>(Arrays.asList(permCurses));
        for(AbstractCard card : cards){
            if(card.type == AbstractCard.CardType.CURSE){
                if(!permCurseSet.contains(card.cardID)){
                    return getCard(card.cardID);
                }
            }
        }

        int strikes = 0;
        int defends = 0;
        for(AbstractCard c : cards){
            if(c.cardID.equals("Strike_R")){
                strikes += 1;
            }else if(c.cardID.equals("Defend_R")){
                defends += 1;
            }
        }

        // remove strikes, then defends as long as offense is not too weak
        if(strikes > 0){
            if(hasDamage || defends <= strikes + 1){
                return getCard("Strike_R");
            }
        }
        if(defends > 0){
            return getCard("Defend_R");
        }else if(canPass){
            return null;
        }else{
            // TODO If there are no strikes or defends
            return cards.get(0);
        }
    }

    /** @return AbstractCard Return the best card to transform in the deck. */
    public AbstractCard getTransform(boolean canPass){
        return getRemove(canPass);
    }

    /** @param cardID Name of a card (no '+').
     * @return boolean Return true if the deck contains the given card, either upgraded or unupgraded. */
    public boolean contains(String cardID){
        for(AbstractCard c : cards){
            if(c.cardID.equals(cardID)){
                return true;
            }
        }
        return false;
    }

    /** @param upgradedFirst If true, the upgraded version has priority. If false unupgraded has priority.
     * @return AbstractCard Return a card in the deck with cardID cardID, null if none exists. */
    public AbstractCard getCard(String cardID, boolean upgradedFirst){
        if(upgradedFirst){
            AbstractCard unupgraded = null;
            for(AbstractCard c : cards){
                if(c.cardID.equals(cardID)){
                    if(c.upgraded){
                        return c;
                    }else{
                        unupgraded = c;
                    }
                }
            }
            return unupgraded;
        }else{
            AbstractCard upgraded = null;
            for(AbstractCard c : cards){
                if(c.cardID.equals(cardID)){
                    if(!c.upgraded){
                        return c;
                    }else{
                        upgraded = c;
                    }
                }
            }
            return upgraded;
        }

    }

    /** @return AbstractCard Return card with given ID if it exists, unupgraded version takes priority. */
    public AbstractCard getCard(String cardID){
        return getCard(cardID, false);
    }

}
