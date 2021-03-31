package SlayTheSpireAIMod.AIs.CombatAIs;

import com.megacrit.cardcrawl.potions.AbstractPotion;

interface PotionEval {
    /**
     * Returns an evaluation of the usage of a potion.
     *
     * @param p the potion to evaluate
     * @return  the evaluation of how good it is to use a potion. The larger the better.
     *          0 indicates not useful enough to use.
     * */
    int evaluation(AbstractPotion p);
}