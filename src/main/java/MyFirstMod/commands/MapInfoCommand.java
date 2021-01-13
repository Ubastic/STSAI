package MyFirstMod.commands;

import basemod.DevConsole;
import basemod.devcommands.ConsoleCommand;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.map.MapRoomNode;

import java.util.ArrayList;

/** Very simple Dev Console command to become acquainted with the process of making them. */
public class MapInfoCommand extends ConsoleCommand {
    public MapInfoCommand(){
        minExtraTokens = 0;
        maxExtraTokens = 0;
        requiresPlayer = true;
        simpleCheck = true;
    }

    public void execute(String[] tokens, int depth){
        MapRoomNode current = AbstractDungeon.getCurrMapNode();
//        current.
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