package SlayTheSpireAIMod.AIs.CombatAIs;

import SlayTheSpireAIMod.util.CombatUtils;
import SlayTheSpireAIMod.util.Move;

/** AI versus encounter "Slime Boss". */
public class SlimeBossCAI extends AbstractCAI {
    @Override
    public String getCombat() {
        return "Slime Boss";
    }

    @Override
    public Move pickMove() {
        return GenericCAI.pickMove(x -> GenericCAI.heuristic(x, 0), TheGuardianCAI::potionEval);
    }

    static class BigSlime extends CombatUtils.SimpleMonster{

        public BigSlime(CombatUtils.MonsterAttack attack, int health, int block, int vulnerable, boolean intangible) {
            super(attack, health, block, vulnerable, intangible);
        }
    }
}
