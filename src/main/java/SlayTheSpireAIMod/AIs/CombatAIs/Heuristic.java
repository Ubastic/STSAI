package SlayTheSpireAIMod.AIs.CombatAIs;

interface Heuristic {
    /**
     * Returns an evaluation of the specified state. Greater is better.
     *
     * @param state the state to be evaluated
     * @return      the evaluation of the state. Greater is better
     * */
    double evaluation(AbstractCAI.CardSequence state);
}
