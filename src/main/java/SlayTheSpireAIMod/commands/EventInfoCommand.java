package SlayTheSpireAIMod.commands;

import SlayTheSpireAIMod.communicationmod.ChoiceScreenUtils;
import basemod.DevConsole;
import basemod.ReflectionHacks;
import basemod.devcommands.ConsoleCommand;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.events.AbstractImageEvent;

import java.util.ArrayList;

/** Dev Console command which gives information about the current event. */
public class EventInfoCommand extends ConsoleCommand {
    public EventInfoCommand(){
        minExtraTokens = 0;
        maxExtraTokens = 0;
        requiresPlayer = true;
        simpleCheck = true;
    }

    public void execute(String[] tokens, int depth){
        AbstractEvent event = AbstractDungeon.getCurrRoom().event;
        DevConsole.log("Event class: " + event.getClass());
        ArrayList<String> options = ChoiceScreenUtils.getCurrentChoiceList();
        DevConsole.log(options.toString());

    }

    @Override
    protected void errorMsg() {
        DevConsole.couldNotParse();
    }
}
