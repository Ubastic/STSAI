package SlayTheSpireAIMod.AIs;

import SlayTheSpireAIMod.communicationmod.ChoiceScreenUtils;
import SlayTheSpireAIMod.communicationmod.patches.MerchantPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.red.DemonForm;
import com.megacrit.cardcrawl.cards.red.Inflame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.ShopRoom;
import com.megacrit.cardcrawl.shop.Merchant;
import com.megacrit.cardcrawl.shop.ShopScreen;
import com.megacrit.cardcrawl.shop.StorePotion;
import com.megacrit.cardcrawl.shop.StoreRelic;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

/** Class which decides what to do at a shop (in room or shop screen). */
public class ShopAI {
    public static final Logger logger = LogManager.getLogger(ShopAI.class.getName());

    public static void execute(){
        logger.info("Executing ShopAI");
        ChoiceScreenUtils.ChoiceType type = ChoiceScreenUtils.getCurrentChoiceType();
        if(type != ChoiceScreenUtils.ChoiceType.SHOP_ROOM && type != ChoiceScreenUtils.ChoiceType.SHOP_SCREEN) {
            logger.info("Done: choice type not suitable");
            return;
        }
        if(type == ChoiceScreenUtils.ChoiceType.SHOP_ROOM) {
            openShop();
        }
        ArrayList<String> choices = ChoiceScreenUtils.getCurrentChoiceList();
        logger.info("Choosing between: " + choices.toString());
        Object best = bestPurchase();
        if(best != null) {
            choose(best);
        } else {
            closeShop();
            ChoiceScreenUtils.pressConfirmButton();
        }
        logger.info("Done");
    }

    /**
     * Returns a measure of how good it would be to purchase purge at the specified price.
     * Higher is better, with 0 meaning not worth purchasing.
     *
     * @param price the price of the purge
     * @return      how good it would be to purchase purge at the price
     * */
    public static double purgePurchaseRating(int price) {
        return 1;
    }

    /**
     * Returns a measure of how good it would be to purchase the specified card at the specified price.
     * Higher is better, with 0 meaning not worth purchasing.
     *
     * @param c     the card to evaluate purchasing
     * @param price the price of the card
     * @return      how good it would be to purchase the card at the price
     * */
    public static double cardPurchaseRating(AbstractCard c, int price) {
        Deck deck = new Deck(AbstractDungeon.player.masterDeck);
        switch (c.cardID) {
            case DemonForm.ID: return deck.isLimitBreak || deck.isDemonForm || deck.isBarricade ? 0 : 100;
            case Inflame.ID: return deck.contains(Inflame.ID) ? 0 : 10;
            default: return 0;
        }
    }

    /**
     * Returns a measure of how good it would be to purchase the specified relic at the specified price.
     * Higher is better, with 0 meaning not worth purchasing.
     *
     * @param r     the relic to evaluate purchasing
     * @param price the price of the relic
     * @return      how good it would be to purchase the relic at the price
     * */
    public static double relicPurchaseRating(StoreRelic r, int price) {
        return 0;
    }

    /**
     * Returns a measure of how good it would be to purchase the specified potion at the specified price.
     * Higher is better, with 0 meaning not worth purchasing.
     *
     * @param p     the potion to evaluate purchasing
     * @param price the price of the potion
     * @return      how good it would be to purchase the potion at the price
     * */
    public static double potionPurchaseRating(StorePotion p, int price) {
        return 0;
    }

    /**
     * Returns the best item to purchase. Returns null if nothing is worth/can be purchased.
     *
     * @return the best item to purchase, null if none
     * */
    public static Object bestPurchase(){
        double bestPurchaseRating = 0;
        Object best = null;
        if(AbstractDungeon.shopScreen.purgeAvailable && AbstractDungeon.player.gold >= ShopScreen.actualPurgeCost) {
            double rating = purgePurchaseRating(ShopScreen.actualPurgeCost);
            if(rating > bestPurchaseRating) {
                bestPurchaseRating = rating;
                best = "purge";
            }
        }
        for(AbstractCard card : ChoiceScreenUtils.getShopScreenCards()) {
            if(card.price <= AbstractDungeon.player.gold) {
                double rating = cardPurchaseRating(card, card.price);
                if(rating > bestPurchaseRating) {
                    bestPurchaseRating = rating;
                    best = card;
                }
            }
        }
        for(StoreRelic relic : ChoiceScreenUtils.getShopScreenRelics()) {
            if(relic.price <= AbstractDungeon.player.gold) {
                double rating = relicPurchaseRating(relic, relic.price);
                if(rating > bestPurchaseRating) {
                    bestPurchaseRating = rating;
                    best = relic;
                }
            }
        }
        for(StorePotion potion : ChoiceScreenUtils.getShopScreenPotions()) {
            if(potion.price <= AbstractDungeon.player.gold) {
                double rating = potionPurchaseRating(potion, potion.price);
                if(rating > bestPurchaseRating) {
                    bestPurchaseRating = rating;
                    best = potion;
                }
            }
        }
        return best;
    }

    /** Opens the shop screen if in the shop room. */
    public static void openShop() {
        logger.info("Opening shop");
        MerchantPatch.visitMerchant = true;
        if(AbstractDungeon.currMapNode != null && AbstractDungeon.currMapNode.room instanceof ShopRoom) {
            Merchant m = ((ShopRoom) AbstractDungeon.currMapNode.room).merchant;
            m.update();
        }
    }

    /** Closes the shop screen if it is open. */
    public static void closeShop() {
        logger.info("Closing shop");
        AbstractDungeon.overlayMenu.cancelButton.hb.clicked = true;
        AbstractDungeon.overlayMenu.update();
    }

    /**
     * Purchases the specified item if it is valid.
     *
     * @param o the card/relic/potion to purchase ("purge" for purge purchase)
     * */
    public static void choose(Object o) {
        try {
            String choice = "";
            if(o.equals("purge")) {
                choice = "purge";
            } else if(o instanceof AbstractCard) {
                choice = ((AbstractCard) o).name.toLowerCase();
            } else if(o instanceof StoreRelic) {
                choice = ((StoreRelic) o).relic.name.toLowerCase();
            } else if (o instanceof StorePotion) {
                choice = ((StorePotion) o).potion.name.toLowerCase();
            }
            ArrayList<String> choices = ChoiceScreenUtils.getCurrentChoiceList();
            logger.info("Making choice: " + choice);
            ChoiceScreenUtils.makeShopScreenChoice(choices.indexOf(choice));
        } catch (Exception e) {
            logger.info("Failed to make choice: " + o.toString() + ". Error: " + e.getMessage());
        }
    }
}
