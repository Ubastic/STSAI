package SlayTheSpireAIMod.AIs.CombatAIs;

import SlayTheSpireAIMod.util.CombatUtils;
import SlayTheSpireAIMod.util.Move;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.potions.*;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;

/** Class which evaluates Moves to execute when in combat, based on the specific combat. */
public abstract class AbstractCAI {
    public static final Logger logger = LogManager.getLogger(AbstractCAI.class.getName());
    public abstract String getCombat(); // name of the combat

    /**
     * Precondition: player is in combat getCombat().
     * Evaluates the combat state and returns the best valid next move.
     *
     * @return the determined best next move
     * */
    public abstract Move pickMove();

    /**
     * Returns a CAI corresponding to the specified combat.
     * Returns a generic CAI if no specific CAI exists for the combat.
     *
     * @param combat the name of the combat
     * @return       the CAI based on specified combat */
    public static AbstractCAI getAI(String combat){
        switch (combat){
            case "Gremlin Nob": return new GremlinNobCAI();
            case "3 Sentries": return new SentriesCAI();
            case "Lagavulin": return new LagavulinCAI();
            case "Hexaghost": return new HexaghostCAI();
            case "Slime Boss": return new SlimeBossCAI();
            case "The Guardian": return new TheGuardianCAI();
            default: return new GenericCAI();
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

    /**
     * Returns an evaluation of the usage of a potion.
     *
     * @param potion the ID of the potion to evaluate
     * @return       the evaluation of how good it is to use a potion. The larger the better.
     *               0 indicates not useful enough to use.
     * */
    public static int potionEval(String potion){
        if (FruitJuice.POTION_ID.equals(potion)) {
            return 10;
        }
        return 0;
    }

    /** Represent the state of the game after playing a sequence of cards. */
    public static class CardSequence{
        AbstractCard first; // first card played in this sequence, null if none
        int firstTargetIndex; // index in monster list of the target of first
        ArrayList<CombatUtils.SimpleMonster> simpleMonsters; //
        CombatUtils.SimplePlayer simplePlayer;

        /** Current game state. */
        public CardSequence(){
            first = null;
            firstTargetIndex = 0;
            simpleMonsters = new ArrayList<>();
            ArrayList<AbstractMonster> monsters = AbstractDungeon.getCurrRoom().monsters.monsters;
            for (AbstractMonster m : monsters) {
                simpleMonsters.add(new CombatUtils.SimpleMonster(new CombatUtils.MonsterAttack(m), m.currentHealth,
                        m.currentBlock, CombatUtils.amountOfPower(m, VulnerablePower.POWER_ID), m.hasPower("Intangible")));
            }
            simplePlayer = new CombatUtils.SimplePlayer();
        }

        public CardSequence(ArrayList<CombatUtils.SimpleMonster> simpleMonsters){
            first = null;
            firstTargetIndex = 0;
            this.simpleMonsters = simpleMonsters;
            simplePlayer = new CombatUtils.SimplePlayer();
        }

        public CardSequence(CardSequence s){
            first = s.first;
            firstTargetIndex = s.firstTargetIndex;
            simpleMonsters = new ArrayList<>();
            for(CombatUtils.SimpleMonster m : s.simpleMonsters){
                simpleMonsters.add(m.copy());
            }
            simplePlayer = new CombatUtils.SimplePlayer(s.simplePlayer);
        }

        /** Return a new CardSequence representing the state after playing a card.
         * @param toPlay The card to be played.
         * @return CardSequence Return the state of the game after playing a card, null if not allowed. */
        public CardSequence playCard(AbstractCard toPlay, CombatUtils.SimpleMonster target){
            if(toPlay.costForTurn > simplePlayer.energy){
                return null;
            }
            CardSequence toRet = new CardSequence(this);
            if(toRet.first == null){
                toRet.first = toPlay;
                toRet.firstTargetIndex = simpleMonsters.indexOf(target);
            }
            toRet.simplePlayer.playCard(toPlay, toRet.simpleMonsters.get(simpleMonsters.indexOf(target)), toRet.simpleMonsters);
            return toRet;
        }

        /** Return a list of possible states after playing 1 card from this one, empty list if no cards can be played. */
        public ArrayList<CardSequence> getPossibilities(){
            ArrayList<CardSequence> possibilities = new ArrayList<>();
            ArrayList<CombatUtils.SimpleMonster> aliveMonsters = new ArrayList<>();
            for(CombatUtils.SimpleMonster m : simpleMonsters){
                if(m.isAlive()){
                    aliveMonsters.add(m);
                }
            }
            if(aliveMonsters.size() == 0){
                return possibilities;
            }

            for(AbstractCard c : simplePlayer.hand){
                if(c.type == AbstractCard.CardType.ATTACK || c.type == AbstractCard.CardType.SKILL){
                    for(CombatUtils.SimpleMonster m : aliveMonsters){
                        CardSequence pos = playCard(c, m);
                        if(pos != null){
                            possibilities.add(pos);
                        }
                    }
                }else{
                    CardSequence pos = playCard(c, aliveMonsters.get(0));
                    if(pos != null){
                        possibilities.add(pos);
                    }
                }

            }
            return possibilities;
        }

        /** Return a set of possible states 1 or more cards from this one, set has this state if no cards can be played. */
        public HashSet<CardSequence> getDistantPossibilities(){
            HashSet<CardSequence> ongoing = new HashSet<>();
            ongoing.add(this);
            HashSet<CardSequence> ended = new HashSet<>();
            for(int i = 0; i < 10; i++){
                HashSet<CardSequence> toReplace = new HashSet<>();
                for(CardSequence c : ongoing){
                    ArrayList<CardSequence> poss = c.getPossibilities();
                    if(poss.size() == 0){
                        ended.add(c);
                    }else{
                        toReplace.addAll(poss);
                    }
                }
                ongoing = toReplace;
            }
            return ended;
        }

        public CardSequence getBestPossibility(Heuristic h){
            HashSet<CardSequence> ended = getDistantPossibilities();
            logger.info("Current State: " + this.toString());
            logger.info("Number of Distant Pos: " + ended.size());
            int bestEval = h.evaluation(this);
            CardSequence bestState = this;
            for(CardSequence state : ended){
                int eval = h.evaluation(state);
                if(eval < bestEval){
                    bestEval = eval;
                    bestState = state;
                }
            }
            logger.info("Best evaluation: " + bestEval);
            return bestState;
        }

        @Override
        public String toString() {
            return "CardSequence{" +
                    "first=" + first +
                    ", firstTargetIndex=" + firstTargetIndex +
                    ", simpleMonsters=" + simpleMonsters +
                    ", simplePlayer=" + simplePlayer +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CardSequence that = (CardSequence) o;
            return simpleMonsters.equals(that.simpleMonsters) &&
                    simplePlayer.equals(that.simplePlayer);
        }

        @Override
        public int hashCode() {
            return Objects.hash(first, firstTargetIndex, simpleMonsters, simplePlayer);
        }
    }

}
