package SlayTheSpireAIMod.AIs.CombatAIs;

import SlayTheSpireAIMod.util.Move;

public class BookOfStabbingCAI extends AbstractCAI {
    public static final String KEY = "Book of Stabbing";

    @Override
    public Move pickMove() {
        return GenericCAI.pickMove(GenericCAI::heuristic);
    }
}
