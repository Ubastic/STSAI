package SlayTheSpireAIMod.actions;

import SlayTheSpireAIMod.util.CombatUtils;
import SlayTheSpireAIMod.util.Move;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.utility.NewQueueCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.potions.AbstractPotion;
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
        switch (toMake.type){
            case CARD:
                AbstractCard toPlay = cards.get(toMake.index);
                if(!toPlay.canUse(AbstractDungeon.player, toMake.target)){
                    isDone = true;
                    logger.info("Illegal card played: " + toMake.toString());
                    return;
                }
                NewQueueCardAction queueCard = new NewQueueCardAction(cards.get(toMake.index), toMake.target);
                this.addToTop(queueCard);
                isDone = true;
                break;
            case POTION:
                //TODO test this after the AI starts to use potions
                AbstractPotion toUse = AbstractDungeon.player.potions.get(toMake.index);
                toUse.use(toMake.target);
                isDone = true;
                break;
            case PASS:
                isDone = true;
                logger.info("Ending turn");
                AbstractDungeon.overlayMenu.endTurnButton.disable(true);
                break;
        }
    }
}
