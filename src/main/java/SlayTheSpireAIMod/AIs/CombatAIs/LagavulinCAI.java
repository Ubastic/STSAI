package SlayTheSpireAIMod.AIs.CombatAIs;

import SlayTheSpireAIMod.util.CombatUtils;
import SlayTheSpireAIMod.util.Move;
import com.megacrit.cardcrawl.cards.AbstractCard;
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

    // TODO play powers during dormant turns
    @Override
    public Move pickMove() {
        return GenericCAI.pickMove(x -> GenericCAI.heuristic(x, 13));
    }
}
