package SlayTheSpireAIMod.AIs;

import SlayTheSpireAIMod.communicationmod.ChoiceScreenUtils;
import com.megacrit.cardcrawl.map.MapRoomNode;

import java.util.ArrayList;

/** Class which decides what room to travel to. */
public class MapAI {
    /** Travel the leftmost node. */
    public static void execute(){
        if(ChoiceScreenUtils.getCurrentChoiceType() != ChoiceScreenUtils.ChoiceType.MAP) return;
        ArrayList<String> choices = ChoiceScreenUtils.getCurrentChoiceList();
        ChoiceScreenUtils.makeMapChoice(0);
    }

    // TODO
    /** Class which represents a possible path to the boss. */
    private static class Path{
        int events;
        int shops;
        int chests;
        int rests;
        int monsters;
        int elites;
        enum startType {EVENT, SHOP, CHEST, REST, MONSTER, ELITE}

        /** Path starting from node root. */
        public Path(MapRoomNode root){
            int events = 0;
            int shops = 0;
            int chests = 0;
            int rests = 0;
            int monsters = 0;
            int elites = 0;
        }

    }

}
