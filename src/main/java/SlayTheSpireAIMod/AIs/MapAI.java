package SlayTheSpireAIMod.AIs;

import SlayTheSpireAIMod.communicationmod.ChoiceScreenUtils;
import SlayTheSpireAIMod.util.MapUtils;
import basemod.ReflectionHacks;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.map.MapEdge;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

/** Class which decides what room to travel to. */
public class MapAI {
    public static final Logger logger = LogManager.getLogger(MapAI.class.getName());

    /** Travel the leftmost node. */
    public static void execute(){
        logger.info("Executing...");
        if(ChoiceScreenUtils.getCurrentChoiceType() != ChoiceScreenUtils.ChoiceType.MAP) return;
        ArrayList<String> choices = ChoiceScreenUtils.getCurrentChoiceList();
        logger.info("Choosing between: " + choices.toString());
        if(choices.size() == 0){
            logger.info("Done: no choices");
            return;
        }
        if(choices.size() == 1){
            // will handle when there is only one child, like final rest -> boss
            choose(choices.get(0));
            logger.info("Done");
            return;
        }

        // create all possible paths from this node
        MapRoomNode current = AbstractDungeon.currMapNode;
        HashSet<Path> paths = new HashSet<>();
        paths.add(new Path(current));
        int y = current.y;
        int boss_y = AbstractDungeon.map.size();

        // For act 3 you start at y = 15, others -1
        if(y == -1 || y == boss_y){
            HashSet<Path> toReplace = new HashSet<>();
            for(Path p : paths){
                for(MapRoomNode child : MapUtils.getRootNodes(AbstractDungeon.map)){
                    toReplace.add(p.travel(child));
                }
            }
            paths = toReplace;
            y = 0;
        }

        while(y < boss_y - 1){
            HashSet<Path> toReplace = new HashSet<>();
            for(Path p : paths){
                for(MapEdge edge : p.end.getEdges()){
                    y = edge.dstY;
                    MapRoomNode child = MapUtils.getNode(edge.dstX, edge.dstY, AbstractDungeon.map);
                    toReplace.add(p.travel(child));
                }
            }
            paths = toReplace;
        }

        ArrayList<Path> pathsList = new ArrayList<>(paths);
        Collections.sort(pathsList);
        Path bestPath = pathsList.get(pathsList.size() - 1);

        logger.info("Number of paths found: " + paths.size());
        logger.info("Best Path: " + bestPath.toString());
        choose("x=" + bestPath.towards.x);
        logger.info("Done");
//        ChoiceScreenUtils.makeMapChoice(choices.indexOf("x=" + bestPath.towards.x));
    }

    public static void choose(String choice){
        try{
            ArrayList<String> choices = ChoiceScreenUtils.getCurrentChoiceList();
            logger.info("Making choice: " + choice);
            ChoiceScreenUtils.makeMapChoice(choices.indexOf(choice));
        }catch(Exception e){
            logger.info("Failed to make choice: " + choice + ". Error: " + e.getMessage());
        }
    }

    /** Class which represents a possible path to the boss. */
    private static class Path implements Comparable<Path>{
        MapRoomNode end; // the node this path ends on
        MapRoomNode towards; // end was reached by travelling this node after current (null if end=current)
        int unknowns; // # event nodes on path after root
        int shops; // # shop nodes
        int treasures; // # treasure nodes
        int rests; // # rest site nodes
        int monsters; // # regular monster fight nodes
        int elites; // # elite monster fight nodes

        /** Path starting from node root. */
        public Path(MapRoomNode end){
            this.end = end;
            towards = null;
            unknowns = 0;
            shops = 0;
            treasures = 0;
            rests = 0;
            monsters = 0;
            elites = 0;
        }

        public Path(MapRoomNode end, MapRoomNode towards, int unknowns, int shops, int treasures, int rests, int monsters, int elites){
            this.end = end;
            this.towards = towards;
            this.unknowns = unknowns;
            this.shops = shops;
            this.treasures = treasures;
            this.rests = rests;
            this.monsters = monsters;
            this.elites = elites;
        }

        /** @return Path Return the Path after travelling to the given node. */
        public Path travel(MapRoomNode dest){
            MapRoomNode newTowards = towards == null ? dest : towards;
            String symbol = ReflectionHacks.getPrivate(dest.room, AbstractRoom.class, "mapSymbol");
            switch(symbol){
                case "?": return new Path(dest, newTowards, unknowns+1, shops, treasures, rests, monsters, elites);
                case "$": return new Path(dest, newTowards, unknowns, shops+1, treasures, rests, monsters, elites);
                case "T": return new Path(dest, newTowards, unknowns, shops, treasures +1, rests, monsters, elites);
                case "R": return new Path(dest, newTowards, unknowns, shops, treasures, rests+1, monsters, elites);
                case "M": return new Path(dest, newTowards, unknowns, shops, treasures, rests, monsters+1, elites);
                case "E": return new Path(dest, newTowards, unknowns, shops, treasures, rests, monsters, elites+1);
            }
            return this;
        }

        /** @return int positive if this path is better, negative if worse, 0 if equal to the other path. */
        @Override
        public int compareTo(Path p) {
            if(AbstractDungeon.player.currentHealth > 40){
                if(elites == 0 && p.elites > 0){
                    return -1;
                }
                if(elites > 0 && p.elites == 0){
                    return 1;
                }
            }
            if(rests == p.rests){
                if(AbstractDungeon.player.gold > 200){
                    if(shops == 0 && p.shops > 0){
                        return -1;
                    }
                    if(shops > 0 && p.shops == 0){
                        return 1;
                    }
                }
                return unknowns - p.unknowns;
            }else{
                return rests - p.rests;
            }
        }

        @Override
        public String toString() {
            return "Path{" +
                    "end=" + end +
                    ", towards=" + towards +
                    ", unknowns=" + unknowns +
                    ", shops=" + shops +
                    ", treasures=" + treasures +
                    ", rests=" + rests +
                    ", monsters=" + monsters +
                    ", elites=" + elites +
                    '}';
        }
    }

}
