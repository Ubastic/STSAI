package SlayTheSpireAIMod.commands;

import SlayTheSpireAIMod.util.MapUtils;
import basemod.DevConsole;
import basemod.devcommands.ConsoleCommand;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.map.MapEdge;
import com.megacrit.cardcrawl.map.MapRoomNode;

import java.util.ArrayList;
import java.util.Map;

/** Dev Console command to give information about the Map. */
public class MapInfoCommand extends ConsoleCommand {
    public MapInfoCommand(){
        minExtraTokens = 0;
        maxExtraTokens = 0;
        requiresPlayer = true;
        simpleCheck = true;
    }

    public void execute(String[] tokens, int depth){
        MapRoomNode current = AbstractDungeon.getCurrMapNode();
        DevConsole.log("Current node x: " + current.x + ", y: " + current.y + ", type: " + current.room.getClass());
        ArrayList<MapRoomNode> parents = current.getParents();
        for(MapRoomNode n : parents){
            DevConsole.log("Parent node x: " + n.x + ", y: " + n.y + ", type: " + n.room.getClass());
        }
        for(MapEdge edge : current.getEdges()){
            MapRoomNode child = MapUtils.getNode(edge.dstX, edge.dstY, AbstractDungeon.map);
            DevConsole.log("Child node x: " + child.x + ", y: " + child.y + ", type: " + child.room.getClass());
        }
        for(MapRoomNode n : MapUtils.getRootNodes(AbstractDungeon.map)){
            DevConsole.log("Root node x: " + n.x + ", y: " + n.y + ", type: " + n.room.getClass());
        }

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