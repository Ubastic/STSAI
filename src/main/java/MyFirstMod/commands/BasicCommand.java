// Command info: https://github.com/daviscook477/BaseMod/wiki/Console
package MyFirstMod.commands;

import basemod.DevConsole;
import basemod.devcommands.ConsoleCommand;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import java.util.ArrayList;

/** Very simple Dev Console command to become acquainted with the process of making them. */
public class BasicCommand extends ConsoleCommand {
    public BasicCommand(){
        minExtraTokens = 0;
        maxExtraTokens = 0;
        requiresPlayer = true;
        simpleCheck = true;
    }

    public void execute(String[] tokens, int depth){
        int health = AbstractDungeon.player.currentHealth;
        DevConsole.log(String.format("You currently have %s health", health));
    }

    @Override
    protected ArrayList<String> extraOptions(String[] tokens, int depth) {
        return super.extraOptions(tokens, depth);
    }

    @Override
    protected void errorMsg() {
        DevConsole.couldNotParse();
    }
}
