package SlayTheSpireAIMod.util;

import SlayTheSpireAIMod.AIs.CombatAIs.AbstractCAI;
import SlayTheSpireAIMod.STSAIMod;
import basemod.ReflectionHacks;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.red.PerfectedStrike;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class CombatUtils {
    public static final Logger logger = LogManager.getLogger(STSAIMod.class.getName());
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
        int realBaseDamage = card.baseDamage;
        if(card.cardID.equals("Perfected Strike")){
            realBaseDamage += card.magicNumber * PerfectedStrike.countCards();
        }
        DamageInfo dinfo = new DamageInfo(AbstractDungeon.player, realBaseDamage, card.damageTypeForTurn);
        dinfo.applyPowers(AbstractDungeon.player, target);

        int hits = getHits(card.name);
        return dinfo.output * hits;
    }

    /** @param attackName The name of the attack.
     * @return int Return number of strikes in the given attack. */
    public static int getHits(String attackName){
        if(attackName.equals("Twin Strike")) return 2;
        if(attackName.equals("Twin Strike+")) return 2;
        if(attackName.equals("Pummel")) return 4;
        if(attackName.equals("Pummel+")) return 5;
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

        public MonsterAttack(AbstractMonster m, boolean noDamage){
            if(noDamage){
                monster = m;
                baseDmg = -1;
                damage = 0;
                hits = 0;
                strength = 0;
                weakened = false;
                vulnerable = false;
            }else{
                monster = m;
                update();
            }
        }

        public MonsterAttack(MonsterAttack a){
            monster = a.monster;
            baseDmg = a.baseDmg;
            damage = a.damage;
            hits = a.hits;
            strength = a.strength;
            weakened = a.weakened;
            vulnerable = a.vulnerable;
        }

        public MonsterAttack copy(){
            return new MonsterAttack(this);
        }

        /** Update field values to represent the combat state. */
        public void update(){
            if(monster.isDeadOrEscaped()){
                hits = 0;
                return;
            }
            // TODO reflect when you cannot see intents
            EnemyMoveInfo moveInfo = ReflectionHacks.getPrivate(monster, AbstractMonster.class, "move");
            baseDmg = moveInfo.baseDamage;
            damage =  monster.getIntentDmg();
            hits = Math.max(1, moveInfo.multiplier);
            strength = monster.getPower("Strength") != null ? monster.getPower("Strength").amount : 0;
            weakened = monster.hasPower("Weakened");
            vulnerable = AbstractDungeon.player.hasPower("Vulnerable");
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

        /** @param strength The amount of strength to be applied to monster, + or -
         * @return int Return the amount of damage that would be dealt after strength is applied. */
        public int getStrengthDamage(int strength){
            // TODO
            return -1;
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
            return "Attacker: " + monster.name + ", " +
                    "Base: " + baseDmg + ", " +
                    "Hits: " + hits + ", " +
                    "Str: " + strength + ", " +
                    "Weak?: " + weakened + ", " +
                    "Intent: " + monster.getIntentDmg() + ", " +
                    "dmg: " + getDamage() + ", " +
                    "wDmg: " + getWeakenedDamage();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            MonsterAttack that = (MonsterAttack) o;
            return baseDmg == that.baseDmg &&
                    damage == that.damage &&
                    hits == that.hits &&
                    strength == that.strength &&
                    weakened == that.weakened &&
                    vulnerable == that.vulnerable &&
                    monster.equals(that.monster);
        }

        @Override
        public int hashCode() {
            return Objects.hash(monster, baseDmg, damage, hits, strength, weakened, vulnerable);
        }
    }

    /** Represent a monster during combat. */
    public static class SimpleMonster{
        public MonsterAttack attack;
        public int health;
        public int block;
        public boolean vulnerable; // true if monster is vulnerable TODO change to int
        public boolean intangible;
        // TODO add Louse armor thing
        // TODO artifact

        public SimpleMonster(MonsterAttack attack, int health, int block, boolean vulnerable, boolean intangible){
            this.attack = attack;
            this.health = health;
            this.block = block;
            this.vulnerable = vulnerable;
            this.intangible = intangible;
        }

        public SimpleMonster(SimpleMonster m){
            this.attack = m.attack;
            this.health = m.health;
            this.block = m.block;
            this.vulnerable = m.vulnerable;
            this.intangible = m.intangible;
        }

        public SimpleMonster copy(){
            return new SimpleMonster(this);
        }


        /** @param player The player who plays the attack.
         * @param attack The attack played.
         * Update monster values after player plays an attack on this monster. */
        public void takeAttack(SimplePlayer player, AbstractCard attack){
            if(attack.type != AbstractCard.CardType.ATTACK){
                throw new IllegalArgumentException("tried to attack with non-attack card");
            }

            // calculate damage dealt by attack
            int realBaseDamage = attack.baseDamage;
            if(attack.cardID.equals("Perfected Strike")){
                realBaseDamage += attack.magicNumber * PerfectedStrike.countCards();
            }else if (attack.cardID.equals("Body Slam")){
                realBaseDamage = player.block;
            }
            double vFactor = vulnerable ? player.getVulnerableDealFactor() : 1;
            double wFactor = player.getWeakDealFactor();
            int strikeDamage = (int)Math.max(0, (realBaseDamage + player.strength) * wFactor * vFactor);
            if(intangible){
                strikeDamage = Math.max(1, strikeDamage);
            }
            int hits = getHits(attack.name);
            if(attack.cardID.equals("Whirlwind")){
                hits = player.energy;
                if(AbstractDungeon.player.hasRelic("Chemical X")){
                    hits += 2;
                }
            }
            // take damage from attack
            takeDamage(strikeDamage * hits, false);
            // apply vulnerable
            if(attack.cardID.equals("Bash") || attack.cardID.equals("Thunderclap") || attack.cardID.equals("Uppercut")){
                vulnerable = true;
            }
        }

        /** @param ignoreBlock If true, deal all damage to health
         * Update this monster's health and block after taking damage. */
        public void takeDamage(int amount, boolean ignoreBlock){
            if(ignoreBlock){
                health -= amount;
            }else{
                if(block >= amount){
                    block -= amount;
                }else{
                    health -= amount - block;
                    block = 0;
                }
            }
        }

        public boolean isAlive(){
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

    /** Represent the player during combat. */
    public static class SimplePlayer{
        public ArrayList<AbstractCard> hand;
        public int energy;
        public int health;
        public int block;
        public int strength;
        // TODO add dexterity
        public int metallicize;
        boolean weakened;
        boolean vulnerable;
        boolean intangible;

        /** Current game state. */
        public SimplePlayer(){
            AbstractPlayer p = AbstractDungeon.player;
            hand = new ArrayList<>();
            hand.addAll(p.hand.group);
            energy = usableEnergy();
            health = p.currentHealth;
            block = p.currentBlock;
            strength = p.hasPower("Strength") ? p.getPower("Strength").amount : 0;
            metallicize = p.hasPower("Metallicize") ? p.getPower("Metallicize").amount : 0;
            weakened = p.hasPower("Weakened");
            vulnerable = p.hasPower("Vulnerable");
            intangible = p.hasPower("Intangible");
        }

        public SimplePlayer(ArrayList<AbstractCard> hand, int energy, int health, int block, int strength,
                            int metallicize, boolean weakened, boolean vulnerable, boolean intangible){
            this.hand = hand;
            this.energy = energy;
            this.health = health;
            this.block = block;
            this.strength = strength;
            this.metallicize = metallicize;
            this.weakened = weakened;
            this.vulnerable = vulnerable;
            this.intangible = intangible;
        }

        public SimplePlayer(SimplePlayer p){
            this.hand = new ArrayList<>();
            hand.addAll(p.hand);
            this.energy = p.energy;
            this.health = p.health;
            this.block = p.block;
            this.strength = p.strength;
            this.metallicize = p.metallicize;
            this.weakened = p.weakened;
            this.vulnerable = p.vulnerable;
            this.intangible = p.intangible;
        }

        /** @param toPlay The card played.
         * @param target The target of the card.
         * Update player values after player plays an attack on a monster. */
        public void playCard(AbstractCard toPlay, SimpleMonster target, ArrayList<SimpleMonster> monsters){
            hand.remove(toPlay);
            if(toPlay.costForTurn > 0){ // Whirlwind costs -1
                energy -= toPlay.costForTurn;
            }
            if(toPlay.type == AbstractCard.CardType.ATTACK){
                if(toPlay.cardID.equals("Whirlwind")){
                    for(SimpleMonster m : monsters){
                        if(m.isAlive()){
                            m.takeAttack(this, toPlay);
                        }
                    }
                    energy = 0;
                }else{
                    target.takeAttack(this, toPlay);
                    block += toPlay.block;
                }
            }else if(toPlay.type == AbstractCard.CardType.SKILL){
                block += toPlay.block;
            }else if(toPlay.type == AbstractCard.CardType.POWER){
                if(toPlay.cardID.equals("Inflame")){
                    strength += toPlay.magicNumber;
                }else if(toPlay.cardID.equals("Metallicize")){
                    metallicize += toPlay.magicNumber;
                }
            }
        }

        /** @return double Return the factor of increased damage by the player due to vulnerable. */
        public double getVulnerableDealFactor(){
            return AbstractDungeon.player.hasRelic("Paper Frog") ? 1.75 : 1.5;
        }

        /** @return double Return the factor of decreased damage by the player due to weak. */
        public double getWeakDealFactor(){
            return weakened ? 0.75 : 1;
        }

        @Override
        public String toString() {
            return "SimplePlayer{" +
                    "hand=" + hand +
                    ", energy=" + energy +
                    ", health=" + health +
                    ", block=" + block +
                    ", strength=" + strength +
                    ", weakened=" + weakened +
                    ", vulnerable=" + vulnerable +
                    ", intangible=" + intangible +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SimplePlayer that = (SimplePlayer) o;
            return energy == that.energy &&
                    health == that.health &&
                    block == that.block &&
                    strength == that.strength &&
                    weakened == that.weakened &&
                    vulnerable == that.vulnerable &&
                    intangible == that.intangible &&
                    handsEqual(hand, that.hand);
//                    hand.equals(that.hand);
        }

        /** Check if two hands are effectively equal (e.g. 2 unupgraded strikes are the same) */
        public boolean handsEqual(ArrayList<AbstractCard> h1, ArrayList<AbstractCard> h2){
            ArrayList<String> names1 = new ArrayList<>();
            for(AbstractCard c : h1){
                names1.add(c.name);
            }
            ArrayList<String> names2 = new ArrayList<>();
            for(AbstractCard c : h2){
                names2.add(c.name);
            }
            Collections.sort(names1);
            Collections.sort(names2);
            return names1.equals(names2);
        }

        @Override
        public int hashCode() {
            return Objects.hash(hand, energy, health, block, strength, weakened, vulnerable, intangible);
        }
    }

}
