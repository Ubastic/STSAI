package SlayTheSpireAIMod.AIs.CombatAIs.Monsters;

import SlayTheSpireAIMod.util.CombatUtils;
import com.megacrit.cardcrawl.monsters.city.Byrd;
import com.megacrit.cardcrawl.powers.FlightPower;

public class ByrdMonster extends SimpleMonster{
    private int flight;

    public ByrdMonster(Byrd m) {
        super(m);
        flight = CombatUtils.amountOfPower(m, FlightPower.POWER_ID);
    }

    public ByrdMonster(ByrdMonster m) {
        super(m);
        flight = m.flight;
    }

    public ByrdMonster copy() {
        return new ByrdMonster(this);
    }

    @Override
    public void takeDamage(int amount, boolean ignoreBlock) {
        if(flight > 0){
            amount = amount / 2;
        }
        if (ignoreBlock) {
            health -= amount;
        } else {
            block -= amount;
            if(block < 0){
                flight -= 1;
                if(flight <= 0){
                    attack = new CombatUtils.MonsterAttack(attack.getMonster(), true);
                }
            }
            health += Math.min(0, block);
            block = Math.max(0, block);
        }
    }

    @Override
    public String toString() {
        return "ByrdMonster{" +
                "flight=" + flight +
                ", attack=" + attack +
                ", health=" + health +
                ", block=" + block +
                ", vulnerable=" + vulnerable +
                ", intangible=" + intangible +
                '}';
    }
}
