package SlayTheSpireAIMod.items;

import SlayTheSpireAIMod.AIs.*;
import SlayTheSpireAIMod.actions.FightAIAction;
import SlayTheSpireAIMod.communicationmod.ChoiceScreenUtils;
import basemod.TopPanelItem;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.TipHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

/** Clickable panel item which has an AI execute the current decision. */
public class UseAIItem extends TopPanelItem {
    public static final Logger logger = LogManager.getLogger(UseAIItem.class.getName());
    private static final Texture IMG = new Texture("SlayTheSpireAIModResources/images/ui/robotimage-small.png");
    public static final String ID = "slaythespireai:UseAIItem";

    public UseAIItem() {
        super(IMG, ID);
    }

    @Override
    protected void onClick() {
        try{
            logger.info("Use AI Button clicked");
            logger.info("Current Screen: " + AbstractDungeon.screen.toString());

            ChoiceScreenUtils.ChoiceType type = ChoiceScreenUtils.getCurrentChoiceType();
            ArrayList<String> choices = ChoiceScreenUtils.getCurrentChoiceList();

            logger.info("Choice type: " + type.toString());
            logger.info("Choices: " + choices.toString());
            switch(type){
                case EVENT:
                    EventAI.execute();
                    break;
                case CHEST:
                    ChestAI.execute();
                    break;
                case SHOP_ROOM:
                case SHOP_SCREEN:
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
                case GRID:
                    GridSelectAI.execute();
                    break;
                case HAND_SELECT:
                    HandSelectAI.execute();
                    break;
                case GAME_OVER:
                case COMPLETE:
                    ChoiceScreenUtils.pressConfirmButton();
                    break;
                case NONE:
                    if(!AbstractDungeon.actionManager.turnHasEnded){
                        AbstractDungeon.actionManager.addToBottom(new FightAIAction());
                        break;
                    }
            }
        }catch(Exception e){
            logger.info("Error occurred on click of item");
            logger.info(e.toString());
        }
    }

    @Override
    protected void onHover() {
        super.onHover();
        // x and y values gotten from TopPanel
        TipHelper.renderGenericTip(1550.0F * Settings.scale, (float)Settings.HEIGHT - 120.0F * Settings.scale, "AI", "Have an AI decide what to do.");
    }
}
