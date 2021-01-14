package SlayTheSpireAIMod.items;

import SlayTheSpireAIMod.AIs.*;
import SlayTheSpireAIMod.actions.FightAIAction;
import SlayTheSpireAIMod.communicationmod.ChoiceScreenUtils;
import SlayTheSpireAIMod.communicationmod.CommandExecutor;
import SlayTheSpireAIMod.communicationmod.InvalidCommandException;
import basemod.DevConsole;
import basemod.TopPanelItem;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.TipHelper;

import java.util.ArrayList;

/** When clicked during combat, execute the current turn. */
public class UseAIItem extends TopPanelItem {
    private static final Texture IMG = new Texture("SlayTheSpireAIModResources/images/ui/robotimage-small.png");
    public static final String ID = "slaythespireai:UseAIItem";

    public UseAIItem() {
        super(IMG, ID);
    }

    @Override
    protected void onClick() {
        try{
            DevConsole.log("clicked");
            DevConsole.log("Current Screen: " + AbstractDungeon.screen.toString());
            DevConsole.log("before:" + ChoiceScreenUtils.getCurrentChoiceType().toString());
            ChoiceScreenUtils.ChoiceType type = ChoiceScreenUtils.getCurrentChoiceType();
            ArrayList<String> choices = ChoiceScreenUtils.getCurrentChoiceList();
            switch(type){
                case EVENT:
                    EventAI.execute();
                    break;
                case CHEST:
                    ChestAI.execute();
                    break;
                case SHOP_ROOM:
                    ShopAI.execute();
                    break;
                case REST:
                    RestSiteAI.execute();
                    break;
                case CARD_REWARD:
                    CardSelectAI.execute();
                    break;
                case COMBAT_REWARD:
                    CombatRewardAI.execute();
                    break;
                case MAP:
                    MapAI.execute();
                    break;
                case BOSS_REWARD:
                    BossRewardAI.execute();
                    break;
                case SHOP_SCREEN:
                    ShopAI.execute();
                    break;
                case GRID:
                    GridSelectAI.execute();
                    break;
                case HAND_SELECT:
                    HandSelectAI.execute();
                    break;
                case GAME_OVER:
                    break;
                case COMPLETE:
                    break;
                case NONE:
                    AbstractDungeon.actionManager.addToBottom(new FightAIAction(0));
                    break;
            }
            DevConsole.log("after:" + ChoiceScreenUtils.getCurrentChoiceType().toString());
            DevConsole.log(choices.toString());
        }catch(Exception e){
            DevConsole.log(e.toString());
        }

    }

    @Override
    protected void onHover() {
        super.onHover();
        // x and y values gotten from TopPanel
        TipHelper.renderGenericTip(1550.0F * Settings.scale, (float)Settings.HEIGHT - 120.0F * Settings.scale, "AI", "Have an AI decide what to do.");

    }


}
