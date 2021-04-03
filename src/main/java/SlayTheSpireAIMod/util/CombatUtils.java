package SlayTheSpireAIMod.util;

import SlayTheSpireAIMod.AIs.CombatAIs.*;
import basemod.ReflectionHacks;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.red.*;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.*;
import com.megacrit.cardcrawl.relics.*;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class CombatUtils {
    public static final Logger logger = LogManager.getLogger(CombatUtils.class.getName());

    /**
     * Returns the amount of energy the player has left to use this turn.
     *
     * @return the amount of energy the player has left to use this turn.
     * */
    public static int usableEnergy(){
        return EnergyPanel.totalCount;
    }

    /**
     * Returns whether the current game is at least at the specified level.
     *
     * @param l the ascension level to compare to
     * @return  whether the current game is at least at the specified level
     * */
    public static boolean atLevel(int l){
        return AbstractDungeon.ascensionLevel >= l;
    }

    /**
     * Returns the best valid next move according to the appropriate Combat AI.
     *
     * @return the determined best next move.
     * */
    public static Move pickMove() {
        String combat = AbstractDungeon.lastCombatMetricKey;
        return pickMove(combat);
    }

    /**
     * Returns a CAI corresponding to the specified combat.
     * Returns a generic CAI if no specific CAI exists for the combat.
     *
     * @param combat the name of the combat
     * @return       the CAI based on specified combat
     * */
    public static AbstractCAI getAI(String combat){
        // TODO add "Mind Bloom Boss Battle"
        switch (combat){
            case GremlinNobCAI.KEY: return new GremlinNobCAI();
            case SentriesCAI.KEY: return new SentriesCAI();
            case LagavulinCAI.KEY: return new LagavulinCAI();
            case HexaghostCAI.KEY: return new HexaghostCAI();
            case SlimeBossCAI.KEY: return new SlimeBossCAI();
            case TheGuardianCAI.KEY: return new TheGuardianCAI();
            case AutomatonCAI.KEY: return new AutomatonCAI();
            case ChampCAI.KEY: return new ChampCAI();
            case CollectorCAI.KEY: return new CollectorCAI();
            default: return new GenericCAI();
        }
    }

    /**
     * Evaluate the combat state and return the best valid next move.
     * Use a combat specific AI if possible.
     *
     * @param combat the name of current combat
     * @return       the determined best next move.
     * */
    public static Move pickMove(String combat){
        AbstractCAI ai = getAI(combat);
        return ai.pickMove();
    }

    /**
     * Returns whether the specified intent does not include an attack.
     *
     * @param in the intent to check the type of
     * @return   whether the intent does not include an attack
     * */
    public static boolean isNotAttack(AbstractMonster.Intent in){
        return in != AbstractMonster.Intent.ATTACK && in != AbstractMonster.Intent.ATTACK_BUFF
                && in != AbstractMonster.Intent.ATTACK_DEBUFF && in != AbstractMonster.Intent.ATTACK_DEFEND;
    }

    /**
     * Returns how much damage would be dealt to a monster by playing a card at it.
     * For Whirlwind(+) returns damage dealt per strike.
     *
     * @param card   the card to play
     * @param target the monster to target by the card
     * @return       how much damage would be dealt to target by playing card at it
     * */
    public static int getDamage(AbstractCard card, AbstractMonster target){
        if(card.type != AbstractCard.CardType.ATTACK){
            return 0;
        }
        int realBaseDamage = card.baseDamage;
        if(card.cardID.equals("Perfected Strike")){
            realBaseDamage += card.magicNumber * PerfectedStrike.countCards();
        }
        DamageInfo dinfo = new DamageInfo(AbstractDungeon.player, realBaseDamage, card.damageTypeForTurn);
        dinfo.applyPowers(AbstractDungeon.player, target);

        int hits = getHits(card);
        return dinfo.output * hits;
    }

    /**
     * Returns the number of hits performed by the specified card. Non-attack cards perform 0 hits.
     *
     * @param c the card to check the number of hits of
     * @return  the number of hits performed by the specified card
     * */
    public static int getHits(AbstractCard c){
        if(c.type != AbstractCard.CardType.ATTACK){
            return 0;
        }
        switch(c.name){
            case "Twin Strike":
            case "Twin Strike+": return 2;
            case "Pummel": return 4;
            case "Pummel+": return 5;
            default: return 1;
        }
    }

    /**
     * Returns the amount of vulnerable applied by the specified card.
     *
     * @param c the card to check the vulnerable applied of
     * @return  the amount of vulnerable applied by the specified card
     * */
    public static int getVulnerable(AbstractCard c){
        switch(c.name){
            case "Bash+": return 3;
            case "Bash":
            case "Uppercut+":
                return 2;
            case "Thunderclap":
            case "Uppercut":
                return 1;
            default:
                return 0;
        }
    }

    /**
     * Returns the current amount of the specified power the specified creature.
     * Returns 0 if the creature does not own the power.
     *
     * @param c  the creature to check the power of
     * @param id the ID of the power to check the amount of
     * @return   the current amount of the specified power the specified creature
     * */
    public static int amountOfPower(AbstractCreature c, String id){
        return c.hasPower(id) ? c.getPower(id).amount : 0;
    }

    /**
     * Returns the alive monster with the lowest health left.
     *
     * @return the alive monster with the lowest health left, or null if none exists
     * */
    public static AbstractMonster getWeakestTarget(){
        int minHealth = 1000;
        AbstractMonster weakest = null;
        try{
            for(AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters){
                if(m.currentHealth > 0 && m.currentHealth < minHealth){
                    minHealth = m.currentHealth;
                    weakest = m;
                }
            }
        }catch(NullPointerException e){
            return null;
        }
        return weakest;
    }

    /** Represent an incoming attack by a monster. */
    public static class MonsterAttack{
        AbstractMonster monster; // attacking monster
        int baseDmg;             // amount of damage each strike deals, no modifiers
        int hitDamage;           // amount of damage each strike deals
        int hits;                // number of times monster strikes in the attack
        int strength;            // + or - for strength of monster
        boolean weakened;        // true if monster is weakened
        boolean vulnerable;      // true if player is vulnerable

        /** @param m the monster which is giving this attack. */
        public MonsterAttack(AbstractMonster m){
            monster = m;
            update();
        }

        public MonsterAttack(AbstractMonster m, boolean noDamage){
            if(noDamage){
                monster = m;
                baseDmg = -1;
                hitDamage = 0;
                hits = 0;
                strength = 0;
                weakened = false;
                vulnerable = false;
            }else{
                monster = m;
                update();
            }
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
            hitDamage = monster.getIntentDmg();
            hits = Math.max(1, moveInfo.multiplier);
            strength = amountOfPower(monster, StrengthPower.POWER_ID);
            weakened = monster.hasPower(WeakPower.POWER_ID);
            vulnerable = AbstractDungeon.player.hasPower(VulnerablePower.POWER_ID);
        }

        /**
         * Returns the amount of damage dealt by this attack per hit. Returns 0 if owner is not attacking.
         *
         * @return the amount of damage dealt by this attack.
         * */
        public int getHitDamage(){
            if(isNotAttack(monster.intent)){
                return 0;
            }
            return hitDamage;
        }

        /**
         * Returns the number of hits in this attack.
         *
         * @return the number of hits in this attack
         * */
        public int getHits(){
            return hits;
        }

        /**
         * Returns the amount of damage the would be dealt by this attack per hit if weakened were applied.
         *
         * @return the amount of damage that would be dealt by this attack if weakened were applied
         * */
        public int getWeakenedDamage(){
            if(isNotAttack(monster.intent)){
                return 0;
            }
            if(weakened){
                return getHitDamage();
            }
            else{
                double vFactor = getVulnerableFactor();
                double wFactor = AbstractDungeon.player.hasRelic(PaperCrane.ID) ? 0.6 : 0.75;
                int wBase = Math.max(0, (int)Math.floor((baseDmg + strength) * wFactor)); // damage per hit if weakened
                return (int)Math.floor(wBase * vFactor);
            }
        }

//        /** @param strength The amount of strength to be applied to monster, + or -
//         * @return int Return the amount of damage that would be dealt after strength is applied. */
//        public int getStrengthDamage(int strength){
//            // TODO
//            return -1;
//        }

        /**
         * Returns the factor of increased damage due to player being vulnerable.
         *
         * @return the factor of increased damage due to player being vulnerable
         * */
        public double getVulnerableFactor(){
            if(vulnerable){
                return AbstractDungeon.player.hasRelic(OddMushroom.ID) ? 1.25 : 1.5;
            }
            return 1;
        }

//        /** TODO (very fringe, 1 relic)
//         * @return int Return the amount of damage that would be dealt if vulnerable was removed. */
//        public int getNonVulnerableDamage(){
//            return -1;
//        }

        /**
         * Returns the owner of this attack.
         *
         * @return the owner of this attack.
         * */
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
                    "hitdmg: " + getHitDamage() + ", " +
                    "wDmg: " + getWeakenedDamage();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            MonsterAttack that = (MonsterAttack) o;
            return baseDmg == that.baseDmg &&
                    hitDamage == that.hitDamage &&
                    hits == that.hits &&
                    strength == that.strength &&
                    weakened == that.weakened &&
                    vulnerable == that.vulnerable &&
                    monster.equals(that.monster);
        }

        @Override
        public int hashCode() {
            return Objects.hash(monster, baseDmg, hitDamage, hits, strength, weakened, vulnerable);
        }
    }

    /** Represent a monster during combat. */
    public static class SimpleMonster{
        public MonsterAttack attack;
        public int health;
        public int block;
        public int vulnerable;
        public boolean intangible;
        // TODO add Louse armor thing
        // TODO artifact

        public SimpleMonster(MonsterAttack attack, int health, int block, int vulnerable, boolean intangible){
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

        /**
         * Updates after player plays an attack on this monster.
         *
         * @param player the player who plays the attack
         * @param attack the attack played
         * */
        public void takeAttack(SimplePlayer player, AbstractCard attack){
            if(attack.type != AbstractCard.CardType.ATTACK){
                throw new IllegalArgumentException("tried to attack with non-attack card");
            }

            // calculate damage dealt by attack
            int realBaseDamage = attack.baseDamage;
            if(attack.cardID.equals(PerfectedStrike.ID)){
                realBaseDamage += attack.magicNumber * PerfectedStrike.countCards();
            }else if (attack.cardID.equals(BodySlam.ID)){
                realBaseDamage = player.block;
            }
            double vFactor = vulnerable > 0 ? player.getVulnerableDealFactor() : 1;
            double wFactor = player.getWeakDealFactor();
            int strikeDamage = (int)Math.max(0, (realBaseDamage + player.strength) * wFactor * vFactor);
            if(intangible){
                strikeDamage = Math.min(1, strikeDamage);
            }
            int hits = getHits(attack);
            if(attack.cardID.equals(Whirlwind.ID)){
                hits = player.energy;
                if(AbstractDungeon.player.hasRelic(ChemicalX.ID)){
                    hits += 2;
                }
            }
            // take damage from attack
            takeDamage(strikeDamage * hits, false);
            // apply vulnerable
            vulnerable += getVulnerable(attack);
        }

        /**
         * Updates health and block after taking damage.
         *
         * @param amount      the amount of damage to take
         * @param ignoreBlock if true, deal all damage to health
         * */
        public void takeDamage(int amount, boolean ignoreBlock){
            if(ignoreBlock){
                health -= amount;
            }else{
                block -= amount;
                health += Math.min(0, block);
                block = Math.max(0, block);
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
        public int demonForm;
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
            strength = amountOfPower(p, StrengthPower.POWER_ID);
            metallicize = amountOfPower(p, MetallicizePower.POWER_ID);
            demonForm = amountOfPower(p, DemonFormPower.POWER_ID);
            weakened = p.hasPower(WeakPower.POWER_ID);
            vulnerable = p.hasPower(VulnerablePower.POWER_ID);
            intangible = p.hasPower(IntangiblePlayerPower.POWER_ID);
        }

        public SimplePlayer(SimplePlayer p){
            this.hand = new ArrayList<>();
            hand.addAll(p.hand);
            this.energy = p.energy;
            this.health = p.health;
            this.block = p.block;
            this.strength = p.strength;
            this.metallicize = p.metallicize;
            this.demonForm = p.demonForm;
            this.weakened = p.weakened;
            this.vulnerable = p.vulnerable;
            this.intangible = p.intangible;
        }

        /**
         * Update after player plays an attack on a monster.
         *
         * @param toPlay   the card played
         * @param target   the monster the card targets.
         * @param monsters the monsters the player is fighting
         * */
        public void playCard(AbstractCard toPlay, SimpleMonster target, ArrayList<SimpleMonster> monsters){
            hand.remove(toPlay);
            if(toPlay.costForTurn > 0){ // Whirlwind costs -1
                energy -= toPlay.costForTurn;
            }
            if(toPlay.type == AbstractCard.CardType.ATTACK){
                if(toPlay.cardID.equals(Whirlwind.ID)){
                    for(SimpleMonster m : monsters){
                        if(m.isAlive()){
                            m.takeAttack(this, toPlay);
                        }
                    }
                    energy = 0;
                }else if(toPlay.cardID.equals(Cleave.ID) || toPlay.cardID.equals(Reaper.ID)){
                    for(SimpleMonster m : monsters){
                        if(m.isAlive()){
                            m.takeAttack(this, toPlay);
                        }
                    }
                }else{
                    target.takeAttack(this, toPlay);
                    block += toPlay.block;
                }
            }else if(toPlay.type == AbstractCard.CardType.SKILL){
                block += toPlay.block;
            }else if(toPlay.type == AbstractCard.CardType.POWER){
                switch (toPlay.cardID) {
                    case Inflame.ID:
                        strength += toPlay.magicNumber;
                        break;
                    case Metallicize.ID:
                        metallicize += toPlay.magicNumber;
                        break;
                    case DemonForm.ID:
                        demonForm += toPlay.magicNumber;
                        break;
                }
            }
        }

        /**
         * Returns the factor of increased damage dealt due to monster vulnerable.
         *
         * @return the factor of increased damage dealt due to monster vulnerable.
         * */
        public double getVulnerableDealFactor(){
            return AbstractDungeon.player.hasRelic(PaperFrog.ID) ? 1.75 : 1.5;
        }

        /**
         * Returns the factor of decreased damage dealt due to weakened.
         *
         * @return the factor of decreased damage dealt due to weakened.
         * */
        public double getWeakDealFactor(){
            return weakened ? 0.75 : 1;
        }

        /**
         * Updates this player's health and block after taking damage.
         *
         * @param amount      the amount of damage this player takes
         * @param ignoreBlock whether all damage is dealt to health
         * */
        public void takeDamage(int amount, boolean ignoreBlock){
            if(ignoreBlock){
                health -= amount;
            }else{
                block -= amount;
                int deltaHP = Math.min(0, block);
                if(AbstractDungeon.player.hasRelic(Torii.ID) && -5 <= deltaHP && deltaHP < 0){
                    deltaHP = -1;
                }
                if(AbstractDungeon.player.hasRelic(TungstenRod.ID) && deltaHP < 0){
                    deltaHP += 1;
                }
                health += deltaHP;
                block = Math.max(0, block);
            }
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
                    metallicize == that.metallicize &&
                    demonForm == that.demonForm &&
                    weakened == that.weakened &&
                    vulnerable == that.vulnerable &&
                    intangible == that.intangible &&
                    handsEqual(hand, that.hand);
//                    hand.equals(that.hand);
        }

        /**
         * Returns whether two hands of cards contain the same multiset of cards.
         * Cards are only different if they have different names.
         *
         * @param h1 the base list of cards
         * @param h2 the list of cards to check equality with the base
         * @return   whether the specified hands contain the same cards
         * */
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
