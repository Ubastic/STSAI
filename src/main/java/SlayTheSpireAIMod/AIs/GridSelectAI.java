package SlayTheSpireAIMod.AIs;

import SlayTheSpireAIMod.communicationmod.ChoiceScreenUtils;

import java.util.ArrayList;

/** Class which decides what to do at a grid choice. */
public class GridSelectAI {
    /** Select the first option. */
    public static void execute(){
        if(ChoiceScreenUtils.getCurrentChoiceType() != ChoiceScreenUtils.ChoiceType.GRID) return;
        ArrayList<String> choices = ChoiceScreenUtils.getCurrentChoiceList();
        ChoiceScreenUtils.makeGridScreenChoice(0);
    }
}
