package SlayTheSpireAIMod.AIs;

import SlayTheSpireAIMod.communicationmod.ChoiceScreenUtils;
import SlayTheSpireAIMod.communicationmod.patches.AbstractRelicUpdatePatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.relics.*;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

/** Class which decides what to do given boss rewards. */
public class BossRewardAI {
    public static final Logger logger = LogManager.getLogger(BossRewardAI.class.getName());

    /** Selects relic according to a fixed ranking. */
    public static void execute(){
        logger.info("Executing...");
        if(ChoiceScreenUtils.getCurrentChoiceType() != ChoiceScreenUtils.ChoiceType.BOSS_REWARD){
            logger.info("Done: choice type not suitable");
            return;
        }
        if(ChoiceScreenUtils.isConfirmButtonAvailable()){
            ChoiceScreenUtils.pressConfirmButton();
        }

        ArrayList<AbstractRelic> choices = AbstractDungeon.bossRelicScreen.relics;
        logger.info("Choosing between: " + choices.toString());

        // ranked from worst -> best
        String[] bossRelics = { RunicDome.ID, RunicPyramid.ID, PandorasBox.ID, Astrolabe.ID, BlackStar.ID, CallingBell.ID,
                RunicCube.ID, BustedCrown.ID, SacredBark.ID, BlackBlood.ID, TinyHouse.ID,
                EmptyCage.ID, PhilosopherStone.ID, CoffeeDripper.ID, FusionHammer.ID, SlaversCollar.ID, SneckoEye.ID,
                MarkOfPain.ID, Sozu.ID,  CursedKey.ID, VelvetChoker.ID, Ectoplasm.ID, };

        int bestIndex = 0;
        AbstractRelic best = null;
        for(AbstractRelic choice : choices){
            int index = ArrayUtils.indexOf(bossRelics, choice.relicId);
            if(index > bestIndex){
                bestIndex = index;
                best = choice;
            }
        }
        choose(best);
        logger.info("Done");
    }

    /**
     * Make the given boss reward choice if it is valid.
     *
     * @param chosenRelic the relic to be chosen.
     * */
    public static void choose(AbstractRelic chosenRelic){
        try{
            logger.info("Making choice: " + chosenRelic);
            AbstractRelicUpdatePatch.doHover = true;
            AbstractRelicUpdatePatch.hoverRelic = chosenRelic;
            InputHelper.justClickedLeft = true;
        }catch(Exception e){
            logger.info("Failed to make choice: " + chosenRelic + ". Error: " + e.getMessage());
        }
    }
}
