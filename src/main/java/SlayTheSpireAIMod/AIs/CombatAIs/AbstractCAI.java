package SlayTheSpireAIMod.AIs.CombatAIs;

import SlayTheSpireAIMod.util.CombatUtils;
import SlayTheSpireAIMod.util.Move;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import java.util.ArrayList;

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
     *  - If you can win combat this turn, win (ignores damage taken, potential heal (e.g. Feed))
     * @return Move Return the determined best next move.  */
    public static Move genericPickMove(){
        ArrayList<AbstractMonster> monsters = AbstractDungeon.getCurrRoom().monsters.monsters;
        int[] monsterHPs = new int[monsters.size()];
        CombatUtils.MonsterAttack[] monsterAttacks = new CombatUtils.MonsterAttack[monsters.size()];
        int aliveMonsters = 0;
        for(int i = 0; i < monsters.size(); i++){
            AbstractMonster m = monsters.get(i);
            if(m.currentHealth > 0){
                aliveMonsters++;
            }
            monsterHPs[i] = m.currentHealth;
            monsterAttacks[i] = new CombatUtils.MonsterAttack(m);
        }

        // check if combat can be won this turn
        if(aliveMonsters == 1){
            Move tryKill = toKill(getRandomTarget());
            if(tryKill != null) return tryKill;
        }

        int monsterDamage = 0;
        for(CombatUtils.MonsterAttack attack : monsterAttacks){
            monsterDamage += attack.getDamage();
        }

        ArrayList<AbstractCard> cards = AbstractDungeon.player.hand.group;

        return CombatUtils.playLeft();
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
            if(toKillHelper(target, target.currentHealth, energy, attack, attacks)){
                return new Move(Move.TYPE.CARD, cards.indexOf(attack), target);
            }
        }
        return null;
    }

    /** @param target Monster we are attempting to kill.
     * @param health Amount of damage that needs to be dealt to kill target.
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

}
