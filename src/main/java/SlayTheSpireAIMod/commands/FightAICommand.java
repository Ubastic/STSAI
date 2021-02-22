package SlayTheSpireAIMod.commands;

import SlayTheSpireAIMod.actions.FightAIAction;
import basemod.DevConsole;
import basemod.devcommands.ConsoleCommand;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

/** Dev console command which executes a turn during a fight. */
public class FightAICommand extends ConsoleCommand {
    public FightAICommand(){
        maxExtraTokens = 0;
        minExtraTokens = 0;
        requiresPlayer = true;
        simpleCheck = true;
    }

    /** Execute a turn if the player is in a fight. */
    public void execute(String[] tokens, int depth){
        try{
            AbstractDungeon.actionManager.addToBottom(new FightAIAction());
        }catch(Exception e){
            DevConsole.log("error");
            DevConsole.log(e.toString());
        }

    }




    @Override
    public void errorMsg() {
        DevConsole.couldNotParse();
//        DevConsole.log("no extra tokens accepted");
    }
}
