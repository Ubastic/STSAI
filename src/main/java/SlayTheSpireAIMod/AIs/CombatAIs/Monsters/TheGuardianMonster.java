package SlayTheSpireAIMod.AIs.CombatAIs.Monsters;

import SlayTheSpireAIMod.util.CombatUtils;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.monsters.exordium.TheGuardian;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;

public class TheGuardianMonster extends SimpleMonster {
    private int modeShiftAmount;
    private final int sharpHideAmount;

    public TheGuardianMonster(TheGuardian m) {
        super(new CombatUtils.MonsterAttack(m), m.currentHealth, m.currentBlock, CombatUtils.amountOfPower(m, VulnerablePower.POWER_ID),
                m.hasPower("Intangible"));
        AbstractPower modeShift = m.getPower("Mode Shift");
        modeShiftAmount = modeShift == null ? 999 : modeShift.amount;
        AbstractPower sharpHide = m.getPower("Sharp Hide");
        sharpHideAmount = sharpHide == null ? 0 : sharpHide.amount;
    }

    public TheGuardianMonster(TheGuardianMonster m) {
        super(m);
        modeShiftAmount = m.modeShiftAmount;
        sharpHideAmount = m.sharpHideAmount;
    }

    public TheGuardianMonster copy() {
        return new TheGuardianMonster(this);
    }

    @Override
    public void takeAttack(CombatUtils.SimplePlayer player, AbstractCard attack) {
        super.takeAttack(player, attack);
        player.takeDamage(sharpHideAmount, false);
    }

    @Override
    public void takeDamage(int amount, boolean ignoreBlock) {
        int healthBefore = health;
        super.takeDamage(amount, ignoreBlock);
        int healthLost = healthBefore - health;
        modeShiftAmount -= healthLost;
        if (modeShiftAmount <= 0) {
            attack = new CombatUtils.MonsterAttack(attack.getMonster(), true);
        }
    }

    @Override
    public String toString() {
        return "TheGuardianMonster{" +
                "modeShiftAmount=" + modeShiftAmount +
                ", sharpHideAmount=" + sharpHideAmount +
                ", attack=" + attack +
                ", health=" + health +
                ", block=" + block +
                ", vulnerable=" + vulnerable +
                ", intangible=" + intangible +
                '}';
    }
}
