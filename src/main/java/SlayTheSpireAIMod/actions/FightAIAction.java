package SlayTheSpireAIMod.actions;

import SlayTheSpireAIMod.util.CombatUtils;
import SlayTheSpireAIMod.util.Move;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.utility.NewQueueCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.potions.PotionSlot;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

/** Action which executes the next move in combat.
 * Screen decisions from cards (select card from hand/pile) are handled by respective AIs. */
public class FightAIAction extends AbstractGameAction {
    public static final Logger logger = LogManager.getLogger(FightAIAction.class.getName());

    @Override
    public void update() {
        // Ensure Time Warp power is not violated
        if(AbstractDungeon.player.endTurnQueued){
            isDone = true;
            return;
        }
        ArrayList<AbstractCard> cards = AbstractDungeon.player.hand.group;
        Move toMake = CombatUtils.pickMove(); // There is no check for validity of non-card moves
        logger.info("Making Move: " + toMake);
        switch (toMake.type){
            case CARD:
                AbstractCard toPlay = cards.get(toMake.index);
                if(toPlay == null || !toPlay.canUse(AbstractDungeon.player, toMake.target)){
                    isDone = true;
                    logger.info("ERROR Illegal card on target played: " + toMake.toString());
                    return;
                }
                logger.info("Playing card: " + toPlay.name);
                NewQueueCardAction queueCard = new NewQueueCardAction(cards.get(toMake.index), toMake.target);
                this.addToTop(queueCard);
                isDone = true;
                break;
            case POTION:
                AbstractPotion toUse = AbstractDungeon.player.potions.get(toMake.index);
                if(toUse == null || toUse instanceof PotionSlot){
                    logger.info("ERROR Tried to use non-existent potion");
                    isDone = true;
                    break;
                }
                if(!toUse.canUse()){
                    logger.info("ERROR Tried to use unusable potion: " + toMake.toString());
                    isDone = true;
                    break;
                }
                logger.info("Using potion: " + toUse.name);
                toUse.use(toMake.target);
                for (AbstractRelic r : AbstractDungeon.player.relics) {
                    r.onUsePotion();
                }
                AbstractDungeon.topPanel.destroyPotion(toMake.index);
                isDone = true;
                break;
            case PASS:
                isDone = true;
                logger.info("Ending turn");
                AbstractDungeon.overlayMenu.endTurnButton.disable(true);
                break;
            case DISCARD:
                AbstractPotion toDiscard = AbstractDungeon.player.potions.get(toMake.index);
                if(toDiscard == null || toDiscard instanceof PotionSlot){
                    logger.info("ERROR Tried to discard non-existent potion");
                    isDone = true;
                    break;
                }
                if(!toDiscard.canDiscard()){
                    logger.info("ERROR Tried to discard undiscardable potion: " + toMake.toString());
                    isDone = true;
                    break;
                }
                logger.info("Discarding potion: " + toDiscard.name);
                AbstractDungeon.topPanel.destroyPotion(toDiscard.slot);
                isDone = true;
                break;
        }
    }
}
