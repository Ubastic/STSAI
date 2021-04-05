package SlayTheSpireAIMod.AIs.CombatAIs.Monsters;

import SlayTheSpireAIMod.util.CombatUtils;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.red.BodySlam;
import com.megacrit.cardcrawl.cards.red.PerfectedStrike;
import com.megacrit.cardcrawl.cards.red.Whirlwind;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.IntangiblePower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.relics.ChemicalX;

import java.util.Objects;

/**
 * Represent a monster during combat.
 */
public class SimpleMonster {
    public CombatUtils.MonsterAttack attack;
    public int health;
    public int block;
    public int vulnerable;
    public boolean intangible;
    // TODO add Louse armor thing
    // TODO artifact

    public SimpleMonster(AbstractMonster m){
        this.attack = new CombatUtils.MonsterAttack(m);
        this.health = m.escaped ? 0 : m.currentHealth;
        this.block = m.currentBlock;
        this.vulnerable = CombatUtils.amountOfPower(m, VulnerablePower.POWER_ID);
        this.intangible = m.hasPower(IntangiblePower.POWER_ID);
    }

    public SimpleMonster(CombatUtils.MonsterAttack attack, int health, int block, int vulnerable, boolean intangible) {
        this.attack = attack;
        this.health = health;
        this.block = block;
        this.vulnerable = vulnerable;
        this.intangible = intangible;
    }

    public SimpleMonster(SimpleMonster m) {
        this.attack = m.attack;
        this.health = m.health;
        this.block = m.block;
        this.vulnerable = m.vulnerable;
        this.intangible = m.intangible;
    }

    public SimpleMonster copy() {
        return new SimpleMonster(this);
    }

    /**
     * Updates after player plays an attack on this monster.
     *
     * @param player the player who plays the attack
     * @param attack the attack played
     */
    public void takeAttack(CombatUtils.SimplePlayer player, AbstractCard attack) {
        if (attack.type != AbstractCard.CardType.ATTACK) {
            throw new IllegalArgumentException("tried to attack with non-attack card");
        }

        // calculate damage dealt by attack
        int realBaseDamage = attack.baseDamage;
        if (attack.cardID.equals(PerfectedStrike.ID)) {
            realBaseDamage += attack.magicNumber * PerfectedStrike.countCards();
        } else if (attack.cardID.equals(BodySlam.ID)) {
            realBaseDamage = player.block;
        }
        double vFactor = vulnerable > 0 ? player.getVulnerableDealFactor() : 1;
        double wFactor = player.getWeakDealFactor();
        int strikeDamage = (int) Math.max(0, (realBaseDamage + player.strength) * wFactor * vFactor);
        if (intangible) {
            strikeDamage = Math.min(1, strikeDamage);
        }
        int hits = CombatUtils.getHits(attack);
        if (attack.cardID.equals(Whirlwind.ID)) {
            hits = player.energy;
            if (AbstractDungeon.player.hasRelic(ChemicalX.ID)) {
                hits += 2;
            }
        }
        // take damage from attack
        takeDamage(strikeDamage * hits, false);
        // apply vulnerable
        vulnerable += CombatUtils.getVulnerable(attack);
    }

    /**
     * Updates health and block after taking damage.
     *
     * @param amount      the amount of damage to take
     * @param ignoreBlock if true, deal all damage to health
     */
    public void takeDamage(int amount, boolean ignoreBlock) {
        if (ignoreBlock) {
            health -= amount;
        } else {
            block -= amount;
            health += Math.min(0, block);
            block = Math.max(0, block);
        }
    }

    public boolean isAlive() {
        return health > 0;
    }

    @Override
    public String toString() {
        return "SimpleMonster{" +
                "attack=" + attack +
                ", health=" + health +
                ", block=" + block +
                ", vulnerable=" + vulnerable +
                ", intangible=" + intangible +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimpleMonster that = (SimpleMonster) o;
        return health == that.health &&
                block == that.block &&
                vulnerable == that.vulnerable &&
                intangible == that.intangible &&
                attack.equals(that.attack);
    }

    @Override
    public int hashCode() {
        return Objects.hash(attack, health, block, vulnerable, intangible);
    }
}
