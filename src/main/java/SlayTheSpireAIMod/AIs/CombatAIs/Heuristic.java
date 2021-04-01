package SlayTheSpireAIMod.AIs.CombatAIs;

interface Heuristic {
    /**
     * Returns an evaluation of a state. Lower is better.
     *
     * @param state the state to be evaluated
     * @return      an evaluation of a state. Lower is better
     * */
    double evaluation(AbstractCAI.CardSequence state);
}
