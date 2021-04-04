package SlayTheSpireAIMod.AIs.CombatAIs.Monsters;

import SlayTheSpireAIMod.util.CombatUtils;
import com.megacrit.cardcrawl.monsters.exordium.SlimeBoss;
import com.megacrit.cardcrawl.powers.VulnerablePower;

public class SlimeBossMonster extends SimpleMonster {
    private final int maxHealth = CombatUtils.atLevel(9) ? 150 : 140;

    public SlimeBossMonster(SlimeBoss m) {
        super(new CombatUtils.MonsterAttack(m), m.currentHealth, m.currentBlock, CombatUtils.amountOfPower(m, VulnerablePower.POWER_ID),
                m.hasPower("Intangible"));
    }

    public SlimeBossMonster(SlimeBossMonster m) {
        super(m);
    }

    public SlimeBossMonster copy() {
        return new SlimeBossMonster(this);
    }

    @Override
    public void takeDamage(int amount, boolean ignoreBlock) {
        super.takeDamage(amount, ignoreBlock);
        if (health <= maxHealth / 2) {
            attack = new CombatUtils.MonsterAttack(attack.getMonster(), true);
        }
    }

    public int getMaxHealth() {
        return maxHealth;
    }
}
