package SlayTheSpireAIMod.util;

import SlayTheSpireAIMod.AIs.CombatAIs.AbstractCAI;
import basemod.ReflectionHacks;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;

import java.util.ArrayList;
import java.util.HashMap;

public class CombatUtils {
    /** @return int Return the amount of energy the player has left to use this turn. */
    public static int usableEnergy(){
        return EnergyPanel.totalCount;
    }

    /** Evaluate the combat state and return the best valid next move.
     * @return Move Return the determined best next move.  */
    public static Move pickMove() {
        String combat = AbstractDungeon.lastCombatMetricKey;
        return AbstractCAI.pickMove(combat);
    }

    /** @return Return the Move for playing the leftmost card possible at the leftmost monster. */
    public static Move playLeft(){
        ArrayList<AbstractMonster> monsters = AbstractDungeon.getCurrRoom().monsters.monsters;

        // Use the first alive monster as the target
        AbstractMonster target = null;
        for (AbstractMonster m : monsters) {
            if (!m.isDeadOrEscaped()) {
                target = m;
                break;
            }
        }

        // Play the leftmost playable card
        ArrayList<AbstractCard> cards = AbstractDungeon.player.hand.group;
        AbstractCard toPlay = null;
        for (AbstractCard c : cards) {
            if (c.canUse(AbstractDungeon.player, target)) {
                toPlay = c;
                break;
            }
        }
        if(toPlay == null){
            return new Move(Move.TYPE.PASS);
        }
        return new Move(Move.TYPE.CARD, cards.indexOf(toPlay), target);
    }

    /** @return boolean Return true if the intent is one which includes an attack. */
    public static boolean isAttack(AbstractMonster.Intent intent){
        return intent == AbstractMonster.Intent.ATTACK || intent == AbstractMonster.Intent.ATTACK_BUFF
                || intent == AbstractMonster.Intent.ATTACK_DEBUFF || intent == AbstractMonster.Intent.ATTACK_DEFEND;
    }

    /** For Whirlwind(+) returns damage dealt per strike.
     * @return int Return how much damage would be dealt to target by playing card at it (if applicable). */
    public static int getDamage(AbstractCard card, AbstractMonster target){
        if(card.type != AbstractCard.CardType.ATTACK){ //TODO maybe include a thousand cuts
            return 0;
        }
        DamageInfo dinfo = new DamageInfo(AbstractDungeon.player, card.baseDamage, card.damageTypeForTurn);
        dinfo.applyPowers(AbstractDungeon.player, target);

        int hits = getHits(card.cardID);
        return dinfo.output * hits;
    }

    /** @param attackID CardID of the attack.
     * @return int Return number of strikes in the given attack. */
    public static int getHits(String attackID){
        if(attackID.equals("Twin Strike")) return 2;
        if(attackID.equals("Twin Strike+")) return 2;
        if(attackID.equals("Pummel")) return 4;
        if(attackID.equals("Pummel+")) return 5;
        return 1;
    }



    /** @return AbstractMonster Return a random alive monster. */
    public static AbstractMonster getRandomTarget(){
        return AbstractDungeon.getRandomMonster();
    }

    /** @return AbstractMonster Return the alive monster with the lowest health left. */
    public static AbstractMonster getWeakestTarget(){
        int minHealth = 999;
        AbstractMonster weakest = null;
        for(AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters){
            if(m.currentHealth > 0 && m.currentHealth < minHealth){
                minHealth = m.currentHealth;
                weakest = m;
            }
        }
        return weakest;
    }

    /** Represent an incoming attack by a monster. */
    public static class MonsterAttack{
        AbstractMonster monster; // attacking monster
        int baseDmg; // amount of damage each strike deals, no modifiers
        int damage; // amount of damage each strike deals
        int hits; // number of times monster strikes in the attack
        int strength; // + or - for strength of monster
        boolean weakened; // true if monster is weakened
        boolean vulnerable; // true if player is vulnerable

        /** @param m the monster which is giving this attack. */
        public MonsterAttack(AbstractMonster m){
            monster = m;
            update();
        }

        /** Update field values to represent the combat state. */
        public void update(){
            if(monster.isDeadOrEscaped()){
                hits = 0;
                return;
            }
            EnemyMoveInfo moveInfo = ReflectionHacks.getPrivate(monster, AbstractMonster.class, "move");
            baseDmg = moveInfo.baseDamage;
            damage =  monster.getIntentDmg();
            hits = Math.max(1, moveInfo.multiplier);
            strength = monster.getPower("Strength") != null ? monster.getPower("Strength").amount : 0;
            weakened = monster.getPower("Weakened") != null && monster.getPower("Weakened").amount > 0;
            vulnerable = AbstractDungeon.player.getPower("Vulnerable") != null;
        }

        /** @return int Return amount of damage dealt from this attack. */
        public int getDamage(){
            if(!isAttack(monster.intent)){
                return 0;
            }
            double vFactor = vulnerable ? 1.5 : 1;
            return (int)Math.floor(damage * vFactor) * hits; // damage accounts for monster strength and weakened
        }

        /** @return int Return the amount of damage that would be dealt after weakened is applied. */
        public int getWeakenedDamage(){
            if(!isAttack(monster.intent)){
                return 0;
            }
            if(weakened){
                return getDamage();
            }
            else{
                double vFactor = getVulnerableFactor();
                double wFactor = AbstractDungeon.player.hasRelic("Paper Crane") ? 0.6 : 0.75;
                int wBase = Math.max(0, (int)Math.floor((baseDmg + strength) * wFactor)); // damage per hit if weakened
                return (int)Math.floor(wBase * vFactor) * hits;
            }
        }

        /** @return double Return the factor of increased damage due to vulnerable. */
        public double getVulnerableFactor(){
            if(vulnerable){
                return AbstractDungeon.player.hasRelic("Odd Mushroom") ? 1.25 : 1.5;
            }
            return 1;
        }

        /** TODO (very fringe, 1 relic)
         * @return int Return the amount of damage that would be dealt if vulnerable was removed. */
        public int getNonVulnerableDamage(){
            return -1;
        }

        /** @return AbstractMonster Return the owner of this attack. */
        public AbstractMonster getMonster(){
            return monster;
        }

        public String toString(){
            StringBuilder toRet = new StringBuilder();
            toRet.append("Attacker: ").append(monster.name).append(", ");
            toRet.append("Base: ").append(baseDmg).append(", ");
            toRet.append("Hits: ").append(hits).append(", ");
            toRet.append("Str: ").append(strength).append(", ");
            toRet.append("Weak?: ").append(weakened).append(", ");
            toRet.append("Intent: ").append(monster.getIntentDmg()).append(", ");
            toRet.append("dmg: ").append(getDamage()).append(", ");
            toRet.append("wDmg: ").append(getWeakenedDamage());
            return toRet.toString();
        }


    }

}
