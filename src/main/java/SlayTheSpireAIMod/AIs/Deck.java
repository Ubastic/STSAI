package SlayTheSpireAIMod.AIs;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.red.*;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.SneckoEye;

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

    /**
     * Returns the best card to add to deck from the specified options.
     * Returns null if no card is worth adding.
     *
     * @param rewardOptions the potential cards to add
     * @return              the best card to add, null if none are worth adding.
     * */
    public AbstractCard chooseCard(ArrayList<AbstractCard> rewardOptions){
        int bestEval = 0;
        AbstractCard best = null;
        for(AbstractCard c : rewardOptions){
            int eval = chooseEval(c);
            if(eval > bestEval){
                bestEval = eval;
                best = c;
            }
        }
        return best;
    }

    /**
     * Returns a measure of how good it is to add the specified card to this deck.
     *
     * @param c the card to evaluate
     * @return  how good it is to add the specified card to this deck
     * */
    private int chooseEval(AbstractCard c){
        switch (c.cardID){
            case Barricade.ID:
                return isBarricade || isLimitBreak || isDemonForm ? 0 : 0; // TODO change when barricade is effective
            case LimitBreak.ID:
                return isBarricade || isLimitBreak || isDemonForm ? 0 : 900;
            case DemonForm.ID:
                return isBarricade || isLimitBreak || isDemonForm ? 0 : 1000;
            case Inflame.ID: return 500;
            case Metallicize.ID: return 50;
            case Whirlwind.ID:
                if(contains(c.cardID)){ return 0; }
                return hasDamage ? 100 : 0;
            case PommelStrike.ID: return hasDamage ? 99 : 0;
            case Cleave.ID: return hasDamage ? 98 : 0;
            case Headbutt.ID: return hasDamage ? 97 : 0;
            case BodySlam.ID:
                if(contains(c.cardID)){ return 0; }
                return hasDamage ? 96 : 0;
            case Rampage.ID: return hasDamage ? 95 : 0;
            case IronWave.ID: return hasDamage ? 94 : 0;
            case ShrugItOff.ID: return 70;
            case BattleTrance.ID:
                if(contains(c.cardID)){ return 0; }
                return 60;
        }
        return 0;
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
        ArrayList<String> badUpgrades = new ArrayList<>();
        if(AbstractDungeon.player.hasRelic(SneckoEye.ID)){
            String[] costUpgrades = { BodySlam.ID, Havoc.ID, BloodForBlood.ID, DarkEmbrace.ID, Entrench.ID,
                    InfernalBlade.ID, SeeingRed.ID, Barricade.ID, Corruption.ID, Exhume.ID};
            badUpgrades.addAll(Arrays.asList(costUpgrades));
        }
        String[] upgradeIDs = // cardIDs of cards to upgrade, best->worst
                { BodySlam.ID, Whirlwind.ID, Barricade.ID, LimitBreak.ID, DemonForm.ID, Entrench.ID, Inflame.ID,
                        Armaments.ID };
        for(String id : upgradeIDs){
            if(containsUpgradable(id) && !badUpgrades.contains(id)){
                return getCard(id);
            }
        }
        for(AbstractCard c : cards){
            if(!c.isStarterStrike() && !c.isStarterDefend() && c.canUpgrade() && !badUpgrades.contains(c.cardID))
                return c;
        }
        for(AbstractCard c : cards){
            if(c.canUpgrade())
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
