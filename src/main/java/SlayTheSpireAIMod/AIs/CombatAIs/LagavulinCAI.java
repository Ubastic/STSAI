package SlayTheSpireAIMod.AIs.CombatAIs;

import SlayTheSpireAIMod.util.CombatUtils;
import SlayTheSpireAIMod.util.Move;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.red.DemonForm;
import com.megacrit.cardcrawl.cards.red.Inflame;
import com.megacrit.cardcrawl.cards.red.Metallicize;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

/** AI versus encounter "Lagavulin". */
public class LagavulinCAI extends AbstractCAI {
    public static final Logger logger = LogManager.getLogger(LagavulinCAI.class.getName());

    @Override
    public String getCombat() {
        return "Lagavulin";
    }

    @Override
    public Move pickMove() {
        int turn = GameActionManager.turn;
        if(turn < 3){
            Move tryPower = power();
            if(tryPower != null){ return tryPower; }

            boolean wait = false;
            ArrayList<AbstractCard> draw = AbstractDungeon.player.drawPile.group;
            for(AbstractCard c : draw){
                if(c.name.equals("Bash+")){
                    wait = true;
                    break;
                }
                if(c.type == AbstractCard.CardType.POWER){
                    wait = true;
                    break;
                }
            }
            if(wait){
                return new Move(Move.TYPE.PASS);
            }
        }
        return GenericCAI.pickMove(x -> GenericCAI.heuristic(x, 1));
    }

    /**
     * Returns a Move which plays a power card. Returns null if none exists.
     * Uses decreasing priority: Demon Form, Inflame, Metallicize.
     *
     * @return a Move which plays a power card. Null if none exists
     * */
    public Move power(){
        AbstractPlayer p = AbstractDungeon.player;
        ArrayList<AbstractCard> hand = p.hand.group;
        AbstractCard demonForm = null;
        AbstractCard inflame = null;
        AbstractCard metallicize = null;
        AbstractCard otherPower = null;
        for(AbstractCard c : hand){
            if(c.type == AbstractCard.CardType.POWER) {
                switch (c.cardID) {
                    case DemonForm.ID:
                        demonForm = c;
                        break;
                    case Inflame.ID:
                        inflame = c;
                        break;
                    case Metallicize.ID:
                        metallicize = c;
                        break;
                    default:
                        otherPower = c;
                }
            }
        }
        if(demonForm != null){
            if(demonForm.canUse(p, CombatUtils.getWeakestTarget())){
                return new Move(Move.TYPE.CARD, hand.indexOf(demonForm), CombatUtils.getWeakestTarget());
            }
        }else if(inflame != null){
            if(inflame.canUse(p, CombatUtils.getWeakestTarget())){
                return new Move(Move.TYPE.CARD, hand.indexOf(inflame), CombatUtils.getWeakestTarget());
            }
        }else if(metallicize != null){
            if(metallicize.canUse(p, CombatUtils.getWeakestTarget())){
                return new Move(Move.TYPE.CARD, hand.indexOf(metallicize), CombatUtils.getWeakestTarget());
            }
        }else if(otherPower != null){
            if(otherPower.canUse(p, CombatUtils.getWeakestTarget())){
                return new Move(Move.TYPE.CARD, hand.indexOf(otherPower), CombatUtils.getWeakestTarget());
            }
        }
        return null;
    }
}
