package SlayTheSpireAIMod.AIs.CombatAIs;

import SlayTheSpireAIMod.util.CombatUtils;
import SlayTheSpireAIMod.util.Move;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.exordium.SlimeBoss;
import com.megacrit.cardcrawl.powers.VulnerablePower;

import java.util.ArrayList;

/** AI versus encounter "Slime Boss". */
public class SlimeBossCAI extends AbstractCAI {
    @Override
    public String getCombat() {
        return "Slime Boss";
    }

    @Override
    public Move pickMove() {
        return GenericCAI.pickMove(SlimeBossCAI::heuristic,
                TheGuardianCAI::potionEval, new CardSequence(getMonsters()));
    }

    public ArrayList<CombatUtils.SimpleMonster> getMonsters(){
        ArrayList<CombatUtils.SimpleMonster> toRet = new ArrayList<>();
        ArrayList<AbstractMonster> monsters = AbstractDungeon.getCurrRoom().monsters.monsters;
        if(monsters.size() == 1){
            if(monsters.get(0) instanceof SlimeBoss){
                toRet.add(new SlimeBossMonster((SlimeBoss)monsters.get(0)));
                return toRet;
            }
        }
        for (AbstractMonster m : monsters) {
            toRet.add(new CombatUtils.SimpleMonster(new CombatUtils.MonsterAttack(m), m.currentHealth,
                    m.currentBlock, CombatUtils.amountOfPower(m, VulnerablePower.POWER_ID), m.hasPower("Intangible")));
        }
        return toRet;
    }

    public static int heuristic(CardSequence state){
        int genericFactor = GenericCAI.heuristic(state, 0);
        int BossSplitFactor = 0; // boss at half health or slightly below is bad

        ArrayList<CombatUtils.SimpleMonster> monsters = state.simpleMonsters;

        if(monsters.size() > 0 && monsters.get(0) instanceof SlimeBossMonster){
            SlimeBossMonster m = (SlimeBossMonster)monsters.get(0);
            int halfHP = m.maxHealth / 2;
            if(m.health <= halfHP){
                int underHalf = halfHP - m.health;
                // splitting at within 10 of half hp is bad
                if(underHalf <= 10){
                    BossSplitFactor += 120;
                }else{
                    BossSplitFactor -= underHalf * 3;
                }
            }
        }
        return genericFactor + BossSplitFactor;
    }

    static class SlimeBossMonster extends CombatUtils.SimpleMonster{
        final int maxHealth = CombatUtils.atLevel(9) ? 150 : 140;

        public SlimeBossMonster(SlimeBoss m){
            super(new CombatUtils.MonsterAttack(m), m.currentHealth, m.currentBlock, CombatUtils.amountOfPower(m, VulnerablePower.POWER_ID),
                    m.hasPower("Intangible"));
        }

        public SlimeBossMonster(SlimeBossMonster m){
            super(m);
        }

        public SlimeBossMonster copy(){
            return new SlimeBossMonster(this);
        }

        @Override
        public void takeDamage(int amount, boolean ignoreBlock) {
            super.takeDamage(amount, ignoreBlock);
            if(health <= maxHealth / 2){
                attack = new CombatUtils.MonsterAttack(attack.getMonster(), true);
            }
        }
    }
}
