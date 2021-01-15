package SlayTheSpireAIMod.util;

import com.megacrit.cardcrawl.monsters.AbstractMonster;

/** Represents a single executable decision.
 * Decision types are:
 *  - Play card
 *  - Use potion
 *  - Pass
 *  - TODO discard potion
 * */
public class Move {
    public enum TYPE{CARD, POTION, PASS}
    public TYPE type;
    public int index; // index of the card/potion to be used
    public AbstractMonster target;

    public Move(TYPE t, int index, AbstractMonster target){
        type = t;
        this.index = index;
        this.target = target;
    }

    public Move(TYPE t){
        this(t, -1, null);
        if(t != TYPE.PASS) throw new IllegalArgumentException("Not enough Move information.");
    }

    @Override
    public String toString() {
        return "Move{" +
                "type=" + type +
                ", index=" + index +
                ", target=" + target +
                '}';
    }
}
