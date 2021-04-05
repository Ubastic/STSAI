package SlayTheSpireAIMod.AIs.CombatAIs;

import SlayTheSpireAIMod.AIs.CombatAIs.Monsters.SimpleMonster;
import SlayTheSpireAIMod.AIs.CombatAIs.Monsters.SlimeBossMonster;
import SlayTheSpireAIMod.util.CombatUtils;
import SlayTheSpireAIMod.util.Move;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.exordium.SlimeBoss;
import com.megacrit.cardcrawl.powers.VulnerablePower;

import java.util.ArrayList;

/** AI versus encounter "Slime Boss". */
public class SlimeBossCAI extends AbstractCAI {
    public static final String KEY = "Slime Boss";

    @Override
    public Move pickMove() {
        return GenericCAI.pickMove(SlimeBossCAI::heuristic,
                TheGuardianCAI::potionEval, new CardSequence(getMonsters()));
    }

    public ArrayList<SimpleMonster> getMonsters(){
        ArrayList<SimpleMonster> toRet = new ArrayList<>();
        ArrayList<AbstractMonster> monsters = AbstractDungeon.getCurrRoom().monsters.monsters;
        if(monsters.size() == 1){
            if(monsters.get(0) instanceof SlimeBoss){
                toRet.add(new SlimeBossMonster((SlimeBoss)monsters.get(0)));
                return toRet;
            }
        }
        for (AbstractMonster m : monsters) {
            toRet.add(new SimpleMonster(m));
        }
        return toRet;
    }

    public static double heuristic(CardSequence state){
        double genericFactor = GenericCAI.heuristic(state);
        int BossSplitFactor = 0; // boss at half health or slightly below is bad

        ArrayList<SimpleMonster> monsters = state.simpleMonsters;

        if(monsters.size() > 0 && monsters.get(0) instanceof SlimeBossMonster){
            SlimeBossMonster m = (SlimeBossMonster)monsters.get(0);
            int halfHP = m.getMaxHealth() / 2;
            if(m.health <= halfHP){
                int underHalf = halfHP - m.health;
                // splitting at within 10 of half hp is bad
                if(underHalf <= 10){
                    BossSplitFactor += 40;
                }else{
                    BossSplitFactor -= underHalf * 3;
                }
            }
        }

        int demonFormWeight = 20;
        int demonFormFactor = demonFormWeight * state.simplePlayer.demonForm;

        return genericFactor - BossSplitFactor + demonFormFactor;
    }

}
