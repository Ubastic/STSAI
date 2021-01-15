package SlayTheSpireAIMod.AIs.CombatAIs;

import SlayTheSpireAIMod.util.CombatUtils;
import SlayTheSpireAIMod.util.Move;
import basemod.DevConsole;
import basemod.ReflectionHacks;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import java.util.ArrayList;
import java.util.HashSet;

/** Class which evaluates Moves to execute when in combat, based on the specific combat. */
public abstract class AbstractCAI {
    public abstract String getCombat(); // name of the combat

    /** Precondition: player is in combat getCombat().
     * Evaluate the combat state and return the best valid next move.
     * Move types:
     *  - Play card
     *  - Use potion
     *  - Pass
     * @return Move Returns the determined best next move.  */
    public abstract Move pickMove();

    /** Evaluate the combat state and return the best valid next move.
     * Use an combat specific AI if possible.
     * @param combat name of current combat
     * @return Move Returns the determined best next move.  */
    public static Move pickMove(String combat){
        AbstractCAI ai = getAI(combat);
        // if no AI for the combat exists, use generic AI
        return ai == null ? genericPickMove() : ai.pickMove();
    }

    /** @param combat name of current combat
     * @return AbstractCAI Return appropriate AI based on combat, null if none exists. */
    public static AbstractCAI getAI(String combat){
        switch (combat){
            case "Gremlin Nob":
                return new GremlinNobCAI();
            case "3 Sentries":
                return new SentriesCAI();
            default:
                return null;
        }
    }

    /** Evaluate the combat state and return the best valid next move.
     * Generic AI:
     *  - Look at health and incoming damage from monsters
     *  - If player can kill only alive monster, do it (ignores damage taken, potential heal (e.g. Feed))
     * @return Move Return the determined best next move.  */
    public static Move genericPickMove(){
        ArrayList<AbstractMonster> monsters = AbstractDungeon.getCurrRoom().monsters.monsters;
        CombatUtils.MonsterAttack[] monsterAttacks = new CombatUtils.MonsterAttack[monsters.size()];
        int aliveMonsters = 0;
        AbstractMonster soleAlive = null;
        for(int i = 0; i < monsters.size(); i++){
            AbstractMonster m = monsters.get(i);
            if(m.currentHealth > 0){
                aliveMonsters++;
                soleAlive = m;
            }
            monsterAttacks[i] = new CombatUtils.MonsterAttack(m);
        }

        // if player can kill only alive monster, do it
        if(aliveMonsters == 1){
            Move tryKill = toKill(soleAlive);
            if(tryKill != null) return tryKill;
        }

        int monsterDamage = 0; // total incoming damage
        AbstractMonster soleAttacker = null; // null if 0 or 2+ attackers
        for(CombatUtils.MonsterAttack attack : monsterAttacks){
            if(monsterDamage == 0 && attack.getDamage() > 0){
                soleAttacker = attack.getMonster();
            }else if(monsterDamage > 0 && attack.getDamage() > 0){
                soleAttacker = null;
            }
            monsterDamage += attack.getDamage();
        }

        // if only one monster is threatening to deal damage, kill it if possible
        if(soleAttacker != null){
            Move tryKill = toKill(soleAttacker);
            if(tryKill != null) return tryKill;
        }

        // if a no-negative card can be played, play it
        Move tryFree = FreeCard();
        if(tryFree != null){
            return tryFree;
        }

        int extraBlock = 0;
        if(AbstractDungeon.player.hasPower("Metallicize")){
            extraBlock += AbstractDungeon.player.getPower("Metallicize").amount;
        }
        if(AbstractDungeon.player.hasPower("Plated Armor")){
            extraBlock += AbstractDungeon.player.getPower("Plated Armor").amount;
        }

        // if no health will be lost from incoming attacks, play like it
        int damageTaken = Math.max(0, monsterDamage - AbstractDungeon.player.currentBlock - extraBlock);
        if(damageTaken == 0){
            DevConsole.log("107 reached");
            return withFullBlockCard();
        }

        int tolerance = 5;
        if(damageTaken > tolerance){
            Move tryBlock = bestBlockCard();
            if(tryBlock != null){
                return tryBlock;
            }else{
                return withFullBlockCard();
            }
        }
        return withFullBlockCard();
    }

    /** Determine if a monster can be killed this turn with attacks from hand. (Ironclad skills deal no damage)
     * Ignores poison, monster armor gain effects, relics.
     * Ignores Time Warp, Velvet Choker
     * If multiple, best kill option is not guaranteed.
     * @param target Monster to be killed this turn.
     * @return Move Return a Move which lets the player kill m this turn, null if none exists. */
    public static Move toKill(AbstractMonster target){
        // Cannot kill with attacks when Entangled
        if(AbstractDungeon.player.hasPower("Entangled")){
            return null;
        }
        ArrayList<AbstractCard> cards = AbstractDungeon.player.hand.group;
        ArrayList<AbstractCard> attacks = new ArrayList<>();
        for(AbstractCard card : cards){
            if(card.type == AbstractCard.CardType.ATTACK){
                attacks.add(card);
            }
        }
        int energy = CombatUtils.usableEnergy();
        for(AbstractCard attack : attacks){
            if(toKillHelper(target, target.currentHealth + target.currentBlock, energy, attack, attacks)){
                if(attack.canUse(AbstractDungeon.player, target)){ // Entangled is checked for, but maybe other problems
                    return new Move(Move.TYPE.CARD, cards.indexOf(attack), target);
                }else{
                    return null;
                }
            }
        }
        return null;
    }

    /** @param target Monster we are attempting to kill.
     * @param health Amount of damage that needs to be dealt to kill target (hp + block).
     * @param energy Amount of energy the player has left to use.
     * @param use Card to be played.
     * @param attacks The attack cards the player has left to use (must include use).
     * @return boolean Return true if playing use allows the player to kill target.  */
    private static boolean toKillHelper(AbstractMonster target, int health, int energy, AbstractCard use, ArrayList<AbstractCard> attacks){
        energy -= use.costForTurn;
        if(energy < 0){ return false; } // use cannot even be played

        health -= CombatUtils.getDamage(use, target);
        if(health <= 0){ return true; }

        ArrayList<AbstractCard> remaining = new ArrayList<>(attacks);
        remaining.remove(use);
        for(AbstractCard attack : remaining){
            if(toKillHelper(target, health, energy, attack, remaining)){
                return true;
            }
        }
        return false;
    }

    /** Determine if there are any "safe" cards to play.
     * Ignores negative effects that trigger on playing a card.
     * Ignores no-draw from Battle Trance(+).
     * @return Move Return a Move which costs no energy which can only help the player, null if none exists. */
    public static Move FreeCard(){
        ArrayList<AbstractCard> cards = AbstractDungeon.player.hand.group;
        HashSet<String> freeCards = new HashSet<>();
        freeCards.add("Flex");
        freeCards.add("Flex+");
        freeCards.add("Havoc+");
        freeCards.add("Warcry+");
        freeCards.add("Battle Trance");
        freeCards.add("Battle Trance+");
        freeCards.add("Infernal Blade+");
        freeCards.add("Intimidate");
        freeCards.add("Intimidate+");
        freeCards.add("Rage");
        freeCards.add("Rage+");
        for(AbstractCard card : cards){
            if(freeCards.contains(card.cardID)){
                return new Move(Move.TYPE.CARD, cards.indexOf(card), null);
            }
        }
        return null;
    }

    /** Play the best available attack if possible. Otherwise play playable power cards.
     * Otherwise play playable block cards. Otherwise pass.
     * @return Move Return the Move to make when the player already blocks all incoming damage.  */
    public static Move withFullBlockCard(){
        Move tryAttack = bestAttackCard();
        if(tryAttack != null){
            DevConsole.log("tryattack");
            return tryAttack;
        }
        ArrayList<AbstractCard> cards = AbstractDungeon.player.hand.group;
        AbstractMonster target = CombatUtils.getRandomTarget();
        for(AbstractCard card : cards){
            if(!card.canUse(AbstractDungeon.player, target)){
                continue;
            }
            if(card.type == AbstractCard.CardType.POWER){
                return new Move(Move.TYPE.CARD, cards.indexOf(card), target);
            }
        }
        Move tryBlock = bestBlockCard();
        if(tryBlock != null){
            return tryBlock;
        }
        return new Move(Move.TYPE.PASS);
    }

    /** Determine the best block-gaining card to play. TODO Armaments tiebreaker
     * @return Move Return the Move which gains the most block-per-energy, null if no cards gain block. */
    public static Move bestBlockCard(){
        ArrayList<AbstractCard> cards = AbstractDungeon.player.hand.group;
        double maxBPE = 0;
        AbstractCard maxBPECard = null;
        AbstractMonster target = CombatUtils.getWeakestTarget();
        for(AbstractCard card : cards){
            if(!card.canUse(AbstractDungeon.player, target)){
                continue;
            }
            // block per energy, with damage as a tiebreaker (Iron Wave vs Defend)
            double bpe = card.costForTurn == 0 && card.block > 0 ?
                    999 : (double) card.block / card.costForTurn + card.damage * 0.0001;
            if(bpe > maxBPE){
                maxBPE = bpe;
                maxBPECard = card;
            }
        }
        if(maxBPE < 1){
            return null;
        }
        return new Move(Move.TYPE.CARD, cards.indexOf(maxBPECard), CombatUtils.getWeakestTarget());
    }

    /** Determine the best attack card to play.
     * @return Move Return the Move which deals the most damage-per-energy, null if no cards deal damage. */
    public static Move bestAttackCard(){
        ArrayList<AbstractCard> cards = AbstractDungeon.player.hand.group;
        double maxDPE = 0;
        AbstractCard maxDPECard = null;
        AbstractMonster target = CombatUtils.getWeakestTarget();
        for(AbstractCard card : cards){
            if(!card.canUse(AbstractDungeon.player, target)){
                continue;
            }
            // damage per energy
            double dpe = 0;
            if(card.cardID.equals("Whirlwind") || card.cardID.equals("Whirlwind+")){
                for(AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters){
                    dpe += CombatUtils.getDamage(card, m);
                }
            }else{
                boolean isMultiDamage = ReflectionHacks.getPrivate(card, AbstractCard.class, "isMultiDamage");
                if(isMultiDamage){
                    for(AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters){
                        dpe += CombatUtils.getDamage(card, m);
                    }
                    dpe = dpe / card.costForTurn;
                }else{
                    int damage = CombatUtils.getDamage(card, target);
                    dpe = card.costForTurn == 0 && damage > 0 ?
                            999 : (double) damage / card.costForTurn;
                }
            }
            if(dpe > maxDPE){
                maxDPE = dpe;
                maxDPECard = card;
            }
        }
        if(maxDPE == 0){
            return null;
        }
        return new Move(Move.TYPE.CARD, cards.indexOf(maxDPECard), target);
    }
}
