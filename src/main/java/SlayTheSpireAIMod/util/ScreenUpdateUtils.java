package SlayTheSpireAIMod.util;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.ui.buttons.PeekButton;

public class ScreenUpdateUtils {
    /** Update the current screen. If a screen-loading/closing action has been made, load/close that screen. */
    public static void update(){
        AbstractDungeon.CurrentScreen screen = AbstractDungeon.screen;
        switch(screen){
//            case NO_INTERACT:
            case NONE:
                AbstractDungeon.dungeonMapScreen.update();
                AbstractDungeon.currMapNode.room.update();
                AbstractDungeon.scene.update();
                AbstractDungeon.currMapNode.room.eventControllerInput();
                break;
//            case FTUE:
//                ftue.update();
//                InputHelper.justClickedRight = false;
//                InputHelper.justClickedLeft = false;
//                currMapNode.room.update();
//                break;
//            case MASTER_DECK_VIEW:
//                deckViewScreen.update();
//                break;
//            case GAME_DECK_VIEW:
//                gameDeckViewScreen.update();
//                break;
//            case DISCARD_VIEW:
//                discardPileViewScreen.update();
//                break;
//            case EXHAUST_VIEW:
//                exhaustPileViewScreen.update();
//                break;
//            case SETTINGS:
//                settingsScreen.update();
//                break;
//            case INPUT_SETTINGS:
//                inputSettingsScreen.update();
//                break;
//            case MAP:
//                dungeonMapScreen.update();
//                break;
//            case GRID:
//                gridSelectScreen.update();
//                if (PeekButton.isPeeking) {
//                    currMapNode.room.update();
//                }
//                break;
//            case CARD_REWARD:
//                cardRewardScreen.update();
//                if (PeekButton.isPeeking) {
//                    currMapNode.room.update();
//                }
//                break;
            case COMBAT_REWARD:
                AbstractDungeon.combatRewardScreen.update();
                break;
//            case BOSS_REWARD:
//                bossRelicScreen.update();
//                currMapNode.room.update();
//                break;
//            case HAND_SELECT:
//                handCardSelectScreen.update();
//                currMapNode.room.update();
//                break;
            case SHOP:
                AbstractDungeon.shopScreen.update();
                break;
//            case DEATH:
//                deathScreen.update();
//                break;
//            case VICTORY:
//                victoryScreen.update();
//                break;
//            case UNLOCK:
//                unlockScreen.update();
//                break;
//            case NEOW_UNLOCK:
//                gUnlockScreen.update();
//                break;
//            case CREDITS:
//                creditsScreen.update();
//                break;
//            case DOOR_UNLOCK:
//                CardCrawlGame.mainMenuScreen.doorUnlockScreen.update();
//                break;
        }
        AbstractDungeon.overlayMenu.update();
    }
}
