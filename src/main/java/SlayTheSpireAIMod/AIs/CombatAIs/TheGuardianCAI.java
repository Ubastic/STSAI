package SlayTheSpireAIMod.AIs.CombatAIs;

import SlayTheSpireAIMod.util.CombatUtils;
import SlayTheSpireAIMod.util.Move;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.exordium.TheGuardian;
import com.megacrit.cardcrawl.potions.*;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

/** AI versus encounter "The Guardian". */
public class TheGuardianCAI extends AbstractCAI{
    public static final Logger logger = LogManager.getLogger(TheGuardianCAI.class.getName());

    @Override
    public String getCombat() {
        return "The Guardian";
    }

    @Override
    public Move pickMove() {
        return GenericCAI.pickMove(TheGuardianCAI::heuristic,
                TheGuardianCAI::potionEval, new CardSequence(getMonsters()));
    }

    public static double heuristic(CardSequence state){
        double genericFactor = GenericCAI.heuristic(state);

        int demonFormWeight = 20;
        int demonFormFactor = demonFormWeight * state.simplePlayer.demonForm;

        return genericFactor + demonFormFactor;
    }

    /**
     * Returns an evaluation of the usage of a potion.
     *
     * @param p the ID of the potion to evaluate
     * @return  the evaluation of how good it is to use a potion. The larger the better.
     *          0 indicates not useful enough to use.
     * */
    public static int potionEval(AbstractPotion p){
        switch(p.ID){
            case AncientPotion.POTION_ID:
            case Elixir.POTION_ID:
            case GamblersBrew.POTION_ID:
            case PowerPotion.POTION_ID:
            case SkillPotion.POTION_ID:
            case SmokeBomb.POTION_ID:
            case SneckoOil.POTION_ID:
            case SpeedPotion.POTION_ID:
            case SteroidPotion.POTION_ID:
            case SwiftPotion.POTION_ID:
                return 0;
            case EntropicBrew.POTION_ID: return 1;
            case BlessingOfTheForge.POTION_ID: return 2;
            case AttackPotion.POTION_ID:
            case BlockPotion.POTION_ID:
            case DistilledChaosPotion.POTION_ID:
            case DuplicationPotion.POTION_ID:
            case EnergyPotion.POTION_ID:
            case ExplosivePotion.POTION_ID:
            case FearPotion.POTION_ID:
            case FirePotion.POTION_ID:
            case LiquidMemories.POTION_ID:
            case RegenPotion.POTION_ID:
            case WeakenPotion.POTION_ID:
                return 5;
            case BloodPotion.POTION_ID:
            case ColorlessPotion.POTION_ID:
            case CultistPotion.POTION_ID:
            case DexterityPotion.POTION_ID:
            case EssenceOfSteel.POTION_ID:
            case FruitJuice.POTION_ID:
            case HeartOfIron.POTION_ID:
            case LiquidBronze.POTION_ID:
            case StrengthPotion.POTION_ID:
                return 10;
            default: return 0;
        }
    }

    public ArrayList<CombatUtils.SimpleMonster> getMonsters(){
        ArrayList<CombatUtils.SimpleMonster> toRet = new ArrayList<>();
        toRet.add(new TheGuardianMonster((TheGuardian)AbstractDungeon.getCurrRoom().monsters.monsters.get(0)));
        return toRet;
    }

    public static class TheGuardianMonster extends CombatUtils.SimpleMonster{
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

        public TheGuardianMonster(TheGuardianMonster m){
            super(m);
            modeShiftAmount = m.modeShiftAmount;
            sharpHideAmount = m.sharpHideAmount;
        }

        public TheGuardianMonster copy(){
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
            if(modeShiftAmount <= 0){
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
}
