package SlayTheSpireAIMod.AIs.CombatAIs;

import SlayTheSpireAIMod.util.CombatUtils;
import SlayTheSpireAIMod.util.Move;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import java.util.ArrayList;

/** AI versus encounter "Gremlin Nob". */
public class GremlinNobCAI extends AbstractCAI {

    @Override
    public String getCombat() {
        return "Gremlin Nob";
    }

    @Override
    public Move pickMove() {
        ArrayList<AbstractMonster> monsters = AbstractDungeon.getCurrRoom().monsters.monsters;
        CombatUtils.MonsterAttack[] monsterAttacks = new CombatUtils.MonsterAttack[monsters.size()];
        int aliveMonsters = 0;
        for(int i = 0; i < monsters.size(); i++){
            AbstractMonster m = monsters.get(i);
            if(m.currentHealth > 0){
                aliveMonsters++;
            }
            monsterAttacks[i] = new CombatUtils.MonsterAttack(m);
        }

        // if player can kill only alive monster, do it
        if(aliveMonsters == 1){
            Move tryKill = toKill(CombatUtils.getRandomTarget());
            if(tryKill != null) return tryKill;
        }
        return withFullBlockCard();
    }
}
