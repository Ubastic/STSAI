package MyFirstMod.AIs;

import MyFirstMod.communicationmod.ChoiceScreenUtils;
import MyFirstMod.communicationmod.CommandExecutor;
import MyFirstMod.communicationmod.InvalidCommandException;

import java.util.ArrayList;

/** Class which decides what to do at a rest site. */
public class RestSiteAI {
    public static void execute() throws InvalidCommandException {
        if(ChoiceScreenUtils.getCurrentChoiceType() != ChoiceScreenUtils.ChoiceType.REST) return;
        ArrayList<String> choices = ChoiceScreenUtils.getCurrentChoiceList();
        if(choices.size() > 0){
            ChoiceScreenUtils.makeRestRoomChoice(0);
            ChoiceScreenUtils.pressConfirmButton();
        }
    }
}
