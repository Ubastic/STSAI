package SlayTheSpireAIMod.AIs;

import SlayTheSpireAIMod.communicationmod.ChoiceScreenUtils;

import java.util.ArrayList;

/** Class which decides what to during an event. */
public class EventAI {
    /** Make first decision. */
    public static void execute(){
        if(ChoiceScreenUtils.getCurrentChoiceType() != ChoiceScreenUtils.ChoiceType.EVENT) return;
        ArrayList<String> choices = ChoiceScreenUtils.getCurrentChoiceList();
        ChoiceScreenUtils.makeEventChoice(0);
    }
}
