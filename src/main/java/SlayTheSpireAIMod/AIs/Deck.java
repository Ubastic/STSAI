package SlayTheSpireAIMod.AIs;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.curses.AscendersBane;
import com.megacrit.cardcrawl.cards.curses.CurseOfTheBell;
import com.megacrit.cardcrawl.cards.curses.Necronomicurse;
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

    public Deck(CardGroup deck) {
        cards = new ArrayList<>();
        cards.addAll(deck.group);
        int newAttacks = -1; // don't count bash
        for(AbstractCard c : cards) {
            if(c.type == AbstractCard.CardType.ATTACK && !c.isStarterStrike()) {
                newAttacks += 1;
            }
            switch (c.cardID) {
                case Barricade.ID:
                    isBarricade = true;
                    break;
                case LimitBreak.ID:
                    isLimitBreak = true;
                    break;
                case DemonForm.ID:
                    isDemonForm = true;
                    break;
            }
        }
        hasDamage = newAttacks > 2;
    }

    /**
     * Returns the best card to add to deck from the specified options.
     * Returns null if no card is worth adding.
     *
     * @param rewardOptions the potential cards to add
     * @return              the best card to add, null if none are worth adding.
     * */
    public AbstractCard chooseCard(ArrayList<AbstractCard> rewardOptions) {
        double bestEval = 0;
        AbstractCard best = null;
        for(AbstractCard c : rewardOptions) {
            double eval = chooseEval(c);
            if(eval > bestEval) {
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
    private double chooseEval(AbstractCard c) {
        switch (c.cardID) {
            // win-cons
            case Barricade.ID:
                return isBarricade || isLimitBreak || isDemonForm ? 0 : 0; // TODO change when barricade is effective
            case LimitBreak.ID:
                return isBarricade || isLimitBreak || isDemonForm ? 0 : 1000;
            case DemonForm.ID:
                return isBarricade || isLimitBreak || isDemonForm ? 0 : 1000.1;
            // powers
            case Inflame.ID: return 500;
            case Metallicize.ID: return 50;
            // Attacks
            case Whirlwind.ID:
                if(contains(c.cardID)) { return 0; }
                return hasDamage ? 0 : 100.99;
            case TwinStrike.ID: return hasDamage ? 0 : 100.98;
            case Cleave.ID: return hasDamage ? 0 : 100.97;
            case Headbutt.ID: return hasDamage ? 0 : 100.96;
            case BodySlam.ID:
                if(contains(c.cardID)) { return 0; }
                return hasDamage ? 0 : 100.95;
            case PommelStrike.ID: return hasDamage ? 0 : 100.94;
            case Rampage.ID: return hasDamage ? 0 : 100.93;
            case IronWave.ID: return hasDamage ? 0 : 100.92;
            case Uppercut.ID: return hasDamage ? 0 : 100.91;
            // Skills
            case ShrugItOff.ID: return 70;
            case GhostlyArmor.ID: return 40;
            case BattleTrance.ID:
                if(contains(c.cardID)) { return 0; }
                return 60;
        }
        return 0;
    }

    /**
     * Removes and returns the best card to upgrade.
     *
     * @return the best card to upgrade
     * */
    public AbstractCard upgradeCard() {
        AbstractCard toRemove = getUpgrade();
        cards.remove(toRemove);
        return toRemove;
    }

    /**
     * Returns the best card to upgrade.
     *
     * @return the best card to upgrade in the deck
     * */
    public AbstractCard getUpgrade() {
        ArrayList<String> badUpgrades = new ArrayList<>();
        if(AbstractDungeon.player.hasRelic(SneckoEye.ID)) {
            String[] costUpgrades = { BodySlam.ID, Havoc.ID, BloodForBlood.ID, DarkEmbrace.ID, Entrench.ID,
                    InfernalBlade.ID, SeeingRed.ID, Barricade.ID, Corruption.ID, Exhume.ID };
            badUpgrades.addAll(Arrays.asList(costUpgrades));
        }
        String[] upgradeIDs = // cardIDs of cards to upgrade, best->worst
                { BodySlam.ID, Whirlwind.ID, Barricade.ID, LimitBreak.ID, DemonForm.ID, Entrench.ID, Inflame.ID,
                        Armaments.ID };
        for(String id : upgradeIDs) {
            if(containsUpgradable(id) && !badUpgrades.contains(id)) {
                return getCard(id);
            }
        }
        for(AbstractCard c : cards) {
            if(!c.isStarterStrike() && !c.isStarterDefend() && c.canUpgrade() && !badUpgrades.contains(c.cardID))
                return c;
        }
        for(AbstractCard c : cards) {
            if(c.canUpgrade())
                return c;
        }
        return null;
    }

    /**
     * Removes and returns the best card to remove.
     *
     * @return the best card to remove
     * */
    public AbstractCard removeCard() {
        AbstractCard toRemove = getRemove(false);
        cards.remove(toRemove);
        return toRemove;
    }

    /**
     * Returns the best card to remove in the deck.
     *
     * @param canPass if true, can return null if passing is preferred to removing a card
     * @return        the best card to remove in the deck
     * */
    public AbstractCard getRemove(boolean canPass){
        // Remove removable curse if possible (Ignores curse-relics)
        String[] permCurses = {AscendersBane.ID, CurseOfTheBell.ID, Necronomicurse.ID };
        HashSet<String> permCurseSet = new HashSet<>(Arrays.asList(permCurses));
        for(AbstractCard card : cards) {
            if(card.type == AbstractCard.CardType.CURSE) {
                if(!permCurseSet.contains(card.cardID)) {
                    return getCard(card.cardID);
                }
            }
        }

        int strikes = 0;
        int defends = 0;
        for(AbstractCard c : cards) {
            if(c.isStarterStrike()) {
                strikes += 1;
            } else if(c.isStarterDefend()) {
                defends += 1;
            }
        }

        // remove strikes, then defends as long as offense is not too weak
        if(strikes > 0) {
            if(hasDamage || defends <= strikes + 1) {
                return getCard("Strike_R");
            }
        }
        if(defends > 0) {
            return getCard("Defend_R");
        } else if(canPass) {
            return null;
        } else {
            // TODO If there are no strikes or defends
            return cards.get(0);
        }
    }

    /**
     * Removes and returns the best card to transform.
     *
     * @return the best card to transform in the deck
     * */
    public AbstractCard transformCard() {
        AbstractCard toTransform = getTransform(false);
        cards.remove(toTransform);
        return toTransform;
    }

    /**
     * Returns the best card to transform in the deck.
     *
     * @param canPass if true, can return null if passing is preferred to transforming a card
     * @return        the best card to transform in the deck
     *  */
    public AbstractCard getTransform(boolean canPass) {
        return getRemove(canPass);
    }

    /**
     * Returns whether this deck contains an upgradable version of the specified card.
     *
     * @param cardID the ID of the card to check
     * @return       whether this deck contains an upgradable version of the specified card
     * */
    public boolean containsUpgradable(String cardID){
        for(AbstractCard c : cards) {
            if(c.cardID.equals(cardID) && (!c.upgraded || c.cardID.equals(SearingBlow.ID))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns whether the deck contains the given card.
     *
     * @param cardID the ID of the card to check
     * @return       whether the deck contains the given card, either upgraded or unupgraded
     * */
    public boolean contains(String cardID) {
        for(AbstractCard c : cards) {
            if(c.cardID.equals(cardID)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the specified card in this deck, if it exists. Returns null otherwise.
     *
     * @param cardID        the ID of the card to return
     * @param upgradedFirst if true, the upgraded version has priority. If false unupgraded has priority
     * @return              a card in the deck with cardID cardID, null if none exists
     * */
    public AbstractCard getCard(String cardID, boolean upgradedFirst) {
        if(upgradedFirst) {
            AbstractCard unupgraded = null;
            for(AbstractCard c : cards) {
                if(c.cardID.equals(cardID)) {
                    if(c.upgraded) {
                        return c;
                    } else{
                        unupgraded = c;
                    }
                }
            }
            return unupgraded;
        } else {
            AbstractCard upgraded = null;
            for(AbstractCard c : cards) {
                if(c.cardID.equals(cardID)) {
                    if(!c.upgraded) {
                        return c;
                    } else {
                        upgraded = c;
                    }
                }
            }
            return upgraded;
        }
    }

    /**
     * Returns the specified card in this deck, if it exists. Prioritizes the unupgraded version.
     * Returns null if no version exists.
     *
     * @param cardID the ID of the card to return
     * @return       a card in the deck with ID cardID, unupgraded version takes priority.
     *               Null if no version exists
     * */
    public AbstractCard getCard(String cardID) {
        return getCard(cardID, false);
    }
}
