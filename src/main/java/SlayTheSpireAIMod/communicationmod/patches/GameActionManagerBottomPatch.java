package SlayTheSpireAIMod.communicationmod.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.GameActionManager;
import SlayTheSpireAIMod.communicationmod.GameStateListener;

@SpirePatch(
        clz= GameActionManager.class,
        method="addToBottom"
)
public class GameActionManagerBottomPatch {
    public static void Postfix(GameActionManager _instance, AbstractGameAction _arg) {
        GameStateListener.registerStateChange();
    }
}
