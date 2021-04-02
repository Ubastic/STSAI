package SlayTheSpireAIMod.AIs.CombatAIs;

import SlayTheSpireAIMod.util.CombatUtils;
import com.megacrit.cardcrawl.monsters.city.SphericGuardian;
import com.megacrit.cardcrawl.powers.IntangiblePower;
import com.megacrit.cardcrawl.powers.VulnerablePower;

public class SphericGuardianMonster extends CombatUtils.SimpleMonster {

    public SphericGuardianMonster(SphericGuardian m){
        super(new CombatUtils.MonsterAttack(m), m.currentHealth+m.currentBlock, 0, CombatUtils.amountOfPower(m, VulnerablePower.POWER_ID),
                m.hasPower(IntangiblePower.POWER_ID));
    }

    public SphericGuardianMonster(SphericGuardianMonster m){
        super(m);
    }

    @Override
    public SphericGuardianMonster copy() {
        return new SphericGuardianMonster(this);
    }

    @Override
    public String toString() {
        return "SphericGuardianMonster{" +
                "attack=" + attack +
                ", health=" + health +
                ", block=" + block +
                ", vulnerable=" + vulnerable +
                ", intangible=" + intangible +
                '}';
    }
}
