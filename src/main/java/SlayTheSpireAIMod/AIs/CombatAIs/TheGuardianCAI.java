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

/** AI versus encounter "TheGuardian". */
public class TheGuardianCAI extends AbstractCAI{
    public static final Logger logger = LogManager.getLogger(TheGuardianCAI.class.getName());

    @Override
    public String getCombat() {
        return "The Guardian";
    }

    @Override
    public Move pickMove() {
        Move tryPotion = usePotion();
        if(usePotion() != null){
            return tryPotion;
        }

        // if a no-negative card can be played, play it
        Move tryFree = FreeCard();
        if(tryFree != null){
            return tryFree;
        }

        // play the card that leads to the best state
        // first, remove cards that cannot be played
        // looks only at monster health and damage player will take from attacks
        CardSequence start = new CardSequence(getMonsters());
        ArrayList<AbstractCard> unplayable = new ArrayList<>();
        for(AbstractCard c : start.simplePlayer.hand){
            if(!c.canUse(AbstractDungeon.player, CombatUtils.getWeakestTarget())){
                unplayable.add(c);
            }
        }
        for(AbstractCard c : unplayable){
            start.simplePlayer.hand.remove(c);
        }

        CardSequence bestState = start.getBestPossibility(x -> heuristic(x, 0));

        if(bestState != start){
            logger.info("Evaluated best state: " + bestState.toString());
            int bestIndex = AbstractDungeon.player.hand.group.indexOf(bestState.first);
            return new Move(Move.TYPE.CARD, bestIndex,
                    AbstractDungeon.getCurrRoom().monsters.monsters.get(bestState.firstTargetIndex));
        }
        return new Move(Move.TYPE.PASS);
    }

    /**
     * Returns an evaluation of the usage of a potion.
     *
     * @param potion the ID of the potion to evaluate
     * @return       the evaluation of how good it is to use a potion. The larger the better.
     *               0 indicates not useful enough to use.
     * */
    public static int potionEval(String potion){
        switch(potion){
            case AncientPotion.POTION_ID:
            case Elixir.POTION_ID:
            case GamblersBrew.POTION_ID:
            case PowerPotion.POTION_ID: // TODO
            case SkillPotion.POTION_ID: // TODO
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

    /**
     * Returns the optimal Move which uses a potion. Returns null if none exists.
     *
     * @return the optimal Move which uses a potion. Null if none exists
     * */
    public static Move usePotion(){
        ArrayList<AbstractPotion> potions = AbstractDungeon.player.potions;
        int maxEval = 0;
        AbstractPotion maxPotion = null;
        for(AbstractPotion p : potions){
            int eval = potionEval(p.ID);
            if(eval > maxEval){
                maxEval = eval;
                maxPotion = p;
            }
        }
        if(maxPotion == null){
            return null;
        }
        return new Move(Move.TYPE.POTION, potions.indexOf(maxPotion), CombatUtils.getWeakestTarget());
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

        /** @param player The player who plays the attack.
         * @param attack The attack played.
         * Update monster values after player plays an attack on this monster. */
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
                    ", attack=" + attack +
                    ", health=" + health +
                    ", block=" + block +
                    ", vulnerable=" + vulnerable +
                    ", intangible=" + intangible +
                    '}';
        }
    }
}
