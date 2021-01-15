package SlayTheSpireAIMod.actions;

import SlayTheSpireAIMod.util.CombatUtils;
import SlayTheSpireAIMod.util.Move;
import basemod.DevConsole;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.EndTurnAction;
import com.megacrit.cardcrawl.actions.utility.NewQueueCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.potions.AbstractPotion;

import java.util.ArrayList;

/** Action which plays the rest of the turn in combat (includes end turn).
 * Screen decisions from cards (select card from hand/pile) are handled by respective AIs. */
public class FightAIAction extends AbstractGameAction {
    int id;
    public FightAIAction(int id){
        this.id = id;
    }

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
                    DevConsole.log("Illegal card played: " + toMake.toString());
                    return;
                }
                NewQueueCardAction queueCard = new NewQueueCardAction(cards.get(toMake.index), toMake.target);
                this.addToTop(queueCard);
                isDone = true; // so that actionManager.update() does not keep calling this method
                while(AbstractDungeon.actionManager.actions.contains(queueCard) || AbstractDungeon.actionManager.cardQueue.size() != 0){
                    AbstractDungeon.actionManager.update();
                }
                this.addToBot(new FightAIAction(id + 1));
                break;
            case POTION:
                //TODO test this after the AI starts to use potions
                AbstractPotion toUse = AbstractDungeon.player.potions.get(toMake.index);
                toUse.use(toMake.target);
                isDone = true;
                break;
            case PASS:
                isDone = true;
                AbstractDungeon.overlayMenu.endTurnButton.disable(true);
                break;
        }
    }
}
