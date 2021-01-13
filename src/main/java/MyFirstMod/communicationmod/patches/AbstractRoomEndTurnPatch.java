package MyFirstMod.communicationmod.patches;

import MyFirstMod.communicationmod.EndOfTurnAction;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import MyFirstMod.communicationmod.GameStateListener;

public class AbstractRoomEndTurnPatch {

    @SpirePatch(
            clz= AbstractRoom.class,
            method="endTurn"
    )
    public static class EndTurnPatch {
        public static void Postfix(AbstractRoom _instance) {
            AbstractDungeon.actionManager.addToBottom(new EndOfTurnAction());
        }
    }
}
