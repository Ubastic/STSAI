package SlayTheSpireAIMod.AIs.CombatAIs;

import SlayTheSpireAIMod.util.CombatUtils;
import SlayTheSpireAIMod.util.Move;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.exordium.TheGuardian;

import java.util.ArrayList;

/** AI versus encounter "TheGuardian". */
public class TheGuardianCAI extends AbstractCAI{
    @Override
    public String getCombat() {
        return "The Guardian";
    }

    @Override
    public Move pickMove() {
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
            logger.info("Evaluated best state (from TheGuardianCAI): " + bestState.toString());
            int bestIndex = AbstractDungeon.player.hand.group.indexOf(bestState.first);
            return new Move(Move.TYPE.CARD, bestIndex,
                    AbstractDungeon.getCurrRoom().monsters.monsters.get(bestState.firstTargetIndex));
        }
        return new Move(Move.TYPE.PASS);
    }

    public ArrayList<CombatUtils.SimpleMonster> getMonsters(){
        ArrayList<CombatUtils.SimpleMonster> toRet = new ArrayList<>();
        toRet.add(new TheGuardianMonster((TheGuardian)AbstractDungeon.getCurrRoom().monsters.monsters.get(0)));
        return toRet;
    }

    public static class TheGuardianMonster extends CombatUtils.SimpleMonster{
        private int modeShiftAmount;

        public TheGuardianMonster(TheGuardian m) {
            super(new CombatUtils.MonsterAttack(m), m.currentHealth, m.currentBlock, m.hasPower("Vulnerable"),
                    m.hasPower("Intangible"));
            if(m.hasPower("Mode Shift")){
                modeShiftAmount = m.getPower("Mode Shift").amount;
            }else{
                modeShiftAmount = 999;
            }
        }

        public TheGuardianMonster(TheGuardianMonster m){
            super(m);
            modeShiftAmount = m.modeShiftAmount;
        }

        public TheGuardianMonster copy(){
            return new TheGuardianMonster(this);
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
