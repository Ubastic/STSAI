package SlayTheSpireAIMod.AIs.CombatAIs;

import SlayTheSpireAIMod.STSAIMod;
import SlayTheSpireAIMod.util.CombatUtils;
import SlayTheSpireAIMod.util.Move;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;

/** Class which evaluates Moves to execute when in combat, based on the specific combat. */
public abstract class AbstractCAI {
    public static final Logger logger = LogManager.getLogger(STSAIMod.class.getName());
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
        return ai == null ? newGenericPickMove() : ai.pickMove();
    }

    /** @param combat name of current combat
     * @return AbstractCAI Return appropriate AI based on combat, null if none exists. */
    public static AbstractCAI getAI(String combat){
        switch (combat){
            case "Gremlin Nob": return new GremlinNobCAI();
            case "3 Sentries": return new SentriesCAI();
            case "Lagavulin": return new LagavulinCAI();
            case "Hexaghost": return new HexaghostCAI();
            case "Slime Boss": return new SlimeBossCAI();
            case "The Guardian": return new TheGuardianCAI();
            default: return null;
        }
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
            if(freeCards.contains(card.cardID) && card.costForTurn == 0){
                return new Move(Move.TYPE.CARD, cards.indexOf(card), null);
            }
        }
        return null;
    }

    public static Move newGenericPickMove(){
        // if a no-negative card can be played, play it
        Move tryFree = FreeCard();
        if(tryFree != null){
            return tryFree;
        }

        // play the card that leads to the best state
        // first, remove cards that cannot be played
        // looks only at monster health and damage player will take from attacks
        CardSequence start = new CardSequence();
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
            logger.info("Evaluated best state (from newGenericMove): " + bestState.toString());
            int bestIndex = AbstractDungeon.player.hand.group.indexOf(bestState.first);
            return new Move(Move.TYPE.CARD, bestIndex,
                    AbstractDungeon.getCurrRoom().monsters.monsters.get(bestState.firstTargetIndex));
        }
        return new Move(Move.TYPE.PASS);
    }

    /** @param state The state to be evaluated.
     * Evaluation of the given state (lower is better).
     * @return int Return a measure of how good a state is. */
    public static int heuristic(CardSequence state, int tolerance){
        int aliveMonsters = 0;
        int totalHealth = 0;
        int incomingDmg = 0;

        int extraBlock = 0;
        extraBlock += state.simplePlayer.metallicize;

        if(AbstractDungeon.player.hasPower("Plated Armor")){
            extraBlock += AbstractDungeon.player.getPower("Plated Armor").amount;
        }

        for(CombatUtils.SimpleMonster m : state.simpleMonsters){
            if(m.isAlive()){
                aliveMonsters += 1;
                totalHealth += m.health;
                incomingDmg += m.attack.getDamage();
            }
        }

        int willLoseHP = Math.max(0, incomingDmg - state.simplePlayer.block - extraBlock);
        int hpLossFactor =  3 * Math.max(0, willLoseHP - tolerance);
        int aliveMonstersFactor = 5 * aliveMonsters;

        if(aliveMonsters == 0){
            aliveMonstersFactor = -100;
        }

        int strength = state.simplePlayer.strength;
        int strengthA = -5; // strength is less important if little dmg needs to be dealt
        int strengthFactor = strength * strengthA;

        int metallicize = state.simplePlayer.metallicize;
        int metallicizeA = -3;
        int metallicizeFactor = metallicize * metallicizeA;

        return totalHealth + aliveMonstersFactor + hpLossFactor + strengthFactor + metallicizeFactor;
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
                        m.currentBlock, m.hasPower("Vulnerable"), m.hasPower("Intangible")));
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
            int bestEval = heuristic(this, 0);
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
