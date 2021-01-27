package SlayTheSpireAIMod.AIs;

import basemod.DevConsole;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

/** Class which represents the player's current permanent deck/relics and evaluates changes to it. */
public class Deck {
    ArrayList<AbstractCard> cards; // copy of player's current deck at the start of combat
    boolean hasDamage; // true if deck contains non-starter attacks
    boolean isBarricade;
    boolean isLimitBreak;
    boolean isDemonForm;
    // TODO is Snecko
    // TODO is SearingBlow

    public Deck(CardGroup deck){
        cards = new ArrayList<>();
        cards.addAll(deck.group);
        int newAttacks = -1; // don't count bash
        for(AbstractCard c : cards){
            if(c.type == AbstractCard.CardType.ATTACK && !c.isStarterStrike()){
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
     * @param rewardOptions List of cards in a card reward.
     * @return String Return lowercase name of chosen card or null if chosen skip. */
    public String chooseCard(ArrayList<AbstractCard> rewardOptions){
        ArrayList<String> rewardOptionNames = new ArrayList<>();
        for(AbstractCard c : rewardOptions){
            rewardOptionNames.add(c.name.toLowerCase());
        }
        HashSet<String> rewardOptionNameSet = new HashSet<>(rewardOptionNames);

        // first check for win conditions
        if(!isBarricade && !isLimitBreak && !isDemonForm){
            // TODO decide between multiple wincons better
            String[] winConNames = // names of win condition cards
                    {"barricade", "barricade+", "limit break", "limit break+", "demon form", "demon form+"};
            for(String cardName : winConNames){
                if(rewardOptionNameSet.contains(cardName)){
                    return cardName;
                }
            }
        }

        // create set of all cards currently in the deck
        HashSet<String> deckCardSet = new HashSet<>();
        for(AbstractCard card : cards){
            deckCardSet.add(card.cardID);
        }

        // create set of cards deck cannot have multiple copies of
        String[] maxOneCards = // cardIDs of cards the deck cannot have multiple copies of
                {"Whirlwind", "Body Slam", "Searing Blow", "Armaments"};
        HashSet<String> maxOneCardSet = new HashSet<>(Arrays.asList(maxOneCards));

        // TODO add upgraded cards, replace with indexOf
        // prioritize damageCards if deck lacks attacks
        if(!hasDamage){
            String[] damageNames = // names of takeable cards when the deck lacks damage, best->worst
                    {"whirlwind", "pommel strike", "cleave", "headbutt", "body slam", "rampage", "iron wave"};
            for(String cardName : damageNames){
                if(rewardOptionNameSet.contains(cardName)){
                    AbstractCard c = rewardOptions.get(rewardOptionNames.indexOf(cardName));
                    if(!maxOneCardSet.contains(c.cardID) || !deckCardSet.contains(c.cardID)){
                        return cardName;
                    }
                }
            }
        }

        // take cards that are always good
        String[] goodNames = // names of cards that are always good, best->worst
                {"shrug it off", "metallicize", "battle trance", "inflame"};
        for(String cardName : goodNames){
            if(rewardOptionNameSet.contains(cardName)){
                AbstractCard c = rewardOptions.get(rewardOptionNames.indexOf(cardName));
                if(!maxOneCardSet.contains(c.cardID) || !deckCardSet.contains(c.cardID)){
                    return cardName;
                }
            }
        }

        // otherwise don't take anything
        return null;
    }

    /** Remove and return the best card to upgrade.
     * @return AbstractCard Return the best card to upgrade. */
    public AbstractCard upgradeCard(){
        AbstractCard toRemove = getUpgrade();
        cards.remove(toRemove);
        return toRemove;
    }

    /** @return AbstractCard Return the best card to upgrade in the deck. */
    public AbstractCard getUpgrade(){
        String[] upgradeIDs = // cardIDs of cards to upgrade, best->worst
                {"Body Slam", "Armaments", "Whirlwind", "Barricade", "Limit Break", "Demon Form", "Entrench"};
        for(String id : upgradeIDs){
            if(containsUpgradable(id)){
                return getCard(id);
            }
        }
        for(AbstractCard c : cards){
            if(!c.isStarterStrike() && !c.isStarterDefend() && !c.upgraded)
                return c;
        }
        for(AbstractCard c : cards){
            if(!c.upgraded)
                return c;
        }
        return null;
    }

    /** Remove and return the best card to remove.
     * @return AbstractCard Return the best card to remove. */
    public AbstractCard removeCard(){
        AbstractCard toRemove = getRemove(false);
        cards.remove(toRemove);
        return toRemove;
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
            if(c.isStarterStrike()){
                strikes += 1;
            }else if(c.isStarterDefend()){
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

    /** Remove and return the best card to transform.
     * @return AbstractCard Return the best card to transform in the deck. */
    public AbstractCard transformCard(){
        AbstractCard toTransform = getTransform(false);
        cards.remove(toTransform);
        return toTransform;
    }

    /** @return AbstractCard Return the best card to transform in the deck. */
    public AbstractCard getTransform(boolean canPass){
        return getRemove(canPass);
    }

    /** @param cardID ID of a card (no '+').
     * @return boolean Return true if the deck contains an upgradable version of the given card. */
    public boolean containsUpgradable(String cardID){
        for(AbstractCard c : cards){
            if(c.cardID.equals(cardID) && (!c.upgraded || c.cardID.equals("Searing Blow"))){
                return true;
            }
        }
        return false;
    }

    /** @param cardID ID of a card (no '+').
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
