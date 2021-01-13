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

        for(int i = 0; i < monsters.size(); i++){
            AbstractMonster m = monsters.get(i);
            monsterHPs[i] = m.currentHealth;
            monsterAttacks[i] = new CombatUtils.MonsterAttack(m);
        }

        ArrayList<AbstractCard> cards = AbstractDungeon.player.hand.group;

        return CombatUtils.playLeft();
//        // For now, use the first alive monster as the target
//        AbstractMonster target = null;
//        for (AbstractMonster m : monsters) {
//            if (!m.isDeadOrEscaped()) {
//                target = m;
//                break;
//            }
//        }
//
//        // For now, play the leftmost playable card
//        ArrayList<AbstractCard> cards = AbstractDungeon.player.hand.group;
//        AbstractCard toPlay = null;
//        for (AbstractCard c : cards) {
//            if (c.canUse(AbstractDungeon.player, target)) {
//                toPlay = c;
//                break;
//            }
//        }
//        if(toPlay == null){
//            return new Move(Move.TYPE.PASS);
//        }
//        return new Move(Move.TYPE.CARD, cards.indexOf(toPlay), target);
    }

    /** @return AbstractMonster Return a random alive monster. */
    public static AbstractMonster getRandomTarget(){
        return AbstractDungeon.getRandomMonster();
    }
}
