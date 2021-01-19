package SlayTheSpireAIMod.util;

import com.megacrit.cardcrawl.map.MapRoomNode;

import java.util.ArrayList;

public class MapUtils {
    /** @return ArrayList Return list of nodes that player can choose to start at at the start of the dungeon. */
    public static ArrayList<MapRoomNode> getRootNodes(ArrayList<ArrayList<MapRoomNode>> map){
        ArrayList<MapRoomNode> roots = new ArrayList<>();
        for(MapRoomNode node : map.get(0)){
            if(node.hasEdges()){
                roots.add(node);
            }
        }
        return roots;
    }

    /** @return MapRoomNode Return node in map at specific coordinates, null if does not exist. */
    public static MapRoomNode getNode(int x, int y, ArrayList<ArrayList<MapRoomNode>> map) {
        try{
            return map.get(y).get(x);
        }catch (IndexOutOfBoundsException e){
            return null;
        }

    }
}
