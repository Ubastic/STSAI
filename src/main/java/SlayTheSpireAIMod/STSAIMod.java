package SlayTheSpireAIMod;

import SlayTheSpireAIMod.AIs.*;
import SlayTheSpireAIMod.actions.FightAIAction;
import SlayTheSpireAIMod.commands.*;
import SlayTheSpireAIMod.communicationmod.ChoiceScreenUtils;
import SlayTheSpireAIMod.communicationmod.GameStateListener;
import SlayTheSpireAIMod.items.UseAIItem;
import SlayTheSpireAIMod.util.CombatUtils;
import basemod.*;
import basemod.devcommands.ConsoleCommand;
import basemod.interfaces.*;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.google.gson.Gson;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.relics.*;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import SlayTheSpireAIMod.util.IDCheckDontTouchPls;
import SlayTheSpireAIMod.util.TextureLoader;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Properties;

@SpireInitializer
public class STSAIMod implements PostInitializeSubscriber,
        PostDungeonUpdateSubscriber,
        OnStartBattleSubscriber,
        PostBattleSubscriber,
        PostDeathSubscriber,
        StartGameSubscriber {
    public static final Logger logger = LogManager.getLogger(STSAIMod.class.getName());
    private static String modID;

    public static Properties STSAISettings = new Properties();
    public static final String ENABLE_AUTO_COMBAT = "enableAutoCombat";
    public static final String ENABLE_AUTO_MAP = "enableAutoMap";
    public static final String ENABLE_AUTO_EVENT = "enableAutoEvent";
    public static final String ENABLE_AUTO_REWARD = "enableAutoReward";
    public static final String ENABLE_AUTO_REST = "enableAutoRest";
    public static final String ENABLE_AUTO_SHOP = "enableAutoShop";
    public static final String ENABLE_AUTO_CHEST = "enableAutoChest";

    public static boolean autoCombat = true;
    public static boolean autoMap = true;
    public static boolean autoEvent = true;
    public static boolean autoReward = true;
    public static boolean autoRest = true;
    public static boolean autoShop = true;
    public static boolean autoChest = true;

    //This is for the in-game mod settings panel.
    private static final String MODNAME = "SlayTheSpireAIMod";
    private static final String AUTHOR = "Ryan Xu";
    private static final String DESCRIPTION = "A mod which has an AI play Slay the Spire.";
    
    // =============== INPUT TEXTURE LOCATION =================

    //Mod Badge - A small icon that appears in the mod settings menu next to your mod.
    public static final String BADGE_IMAGE = "SlayTheSpireAIModResources/images/Badge.png";

    // =============== /INPUT TEXTURE LOCATION/ =================

    private static boolean inBattle = false;
    private static boolean gameEnded = false;
    private static int waitCounter = 0;
    private static boolean stateChanged = false;

    // =============== SUBSCRIBE, INITIALIZE =================
    
    public STSAIMod() {
        logger.info("Subscribe to BaseMod hooks");
        
        BaseMod.subscribe(this);

        setModID("SlayTheSpireAIMod");

        logger.info("Done subscribing");
        logger.info("Adding mod settings");
        STSAISettings.setProperty(ENABLE_AUTO_COMBAT, "FALSE"); // This is the default setting. It's actually set...
        STSAISettings.setProperty(ENABLE_AUTO_MAP, "FALSE");
        STSAISettings.setProperty(ENABLE_AUTO_EVENT, "FALSE");
        STSAISettings.setProperty(ENABLE_AUTO_REWARD, "FALSE");
        STSAISettings.setProperty(ENABLE_AUTO_REST, "FALSE");
        STSAISettings.setProperty(ENABLE_AUTO_SHOP, "FALSE");
        STSAISettings.setProperty(ENABLE_AUTO_CHEST, "FALSE");

        try {
            SpireConfig config = new SpireConfig("defaultMod", "theDefaultConfig", STSAISettings); // ...right here
            // the "fileName" parameter is the name of the file MTS will create where it will save our setting.
            config.load(); // Load the setting and set the boolean to equal it
            autoCombat = config.getBool(ENABLE_AUTO_COMBAT);
            autoMap = config.getBool(ENABLE_AUTO_MAP);
            autoEvent = config.getBool(ENABLE_AUTO_EVENT);
            autoReward = config.getBool(ENABLE_AUTO_REWARD);
            autoRest = config.getBool(ENABLE_AUTO_REST);
            autoShop = config.getBool(ENABLE_AUTO_SHOP);
            autoChest = config.getBool(ENABLE_AUTO_CHEST);
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info("Done adding mod settings");
        
    }
    
    // ====== NO EDIT AREA ======
    // DON'T TOUCH THIS STUFF. IT IS HERE FOR STANDARDIZATION BETWEEN MODS AND TO ENSURE GOOD CODE PRACTICES.
    // IF YOU MODIFY THIS I WILL HUNT YOU DOWN AND DOWNVOTE YOUR MOD ON WORKSHOP
    
    public static void setModID(String ID) { // DON'T EDIT
        Gson coolG = new Gson(); // EY DON'T EDIT THIS
        //   String IDjson = Gdx.files.internal("IDCheckStringsDONT-EDIT-AT-ALL.json").readString(String.valueOf(StandardCharsets.UTF_8)); // i hate u Gdx.files
        InputStream in = STSAIMod.class.getResourceAsStream("/IDCheckStringsDONT-EDIT-AT-ALL.json"); // DON'T EDIT THIS ETHER
        IDCheckDontTouchPls EXCEPTION_STRINGS = coolG.fromJson(new InputStreamReader(in, StandardCharsets.UTF_8), IDCheckDontTouchPls.class); // OR THIS, DON'T EDIT IT
        logger.info("You are attempting to set your mod ID as: " + ID); // NO WHY
        if (ID.equals(EXCEPTION_STRINGS.DEFAULTID)) { // DO *NOT* CHANGE THIS ESPECIALLY, TO EDIT YOUR MOD ID, SCROLL UP JUST A LITTLE, IT'S JUST ABOVE
            throw new RuntimeException(EXCEPTION_STRINGS.EXCEPTION); // THIS ALSO DON'T EDIT
        } else if (ID.equals(EXCEPTION_STRINGS.DEVID)) { // NO
            modID = EXCEPTION_STRINGS.DEFAULTID; // DON'T
        } else { // NO EDIT AREA
            modID = ID; // DON'T WRITE OR CHANGE THINGS HERE NOT EVEN A LITTLE
        } // NO
        logger.info("Success! ID is " + modID); // WHY WOULD U WANT IT NOT TO LOG?? DON'T EDIT THIS.
    } // NO
    
    public static String getModID() { // NO
        return modID; // DOUBLE NO
    } // NU-UH
    
    // ====== YOU CAN EDIT AGAIN ======

    public static void initialize() {
        logger.info("========================= Initializing Default Mod. Hi. =========================");
        STSAIMod mod = new STSAIMod();
        logger.info("========================= /Default Mod Initialized. Hello World./ =========================");
    }

    // ============== /SUBSCRIBE, INITIALIZE/ =================

    // =============== POST-INITIALIZE =================

    /**
     * Remove all relics from boss pool except the given one.
     * Precondition: player is Ironclad
     *
     * @param relicid String the sole relic kept in the boss pool
     * */
    public void focusBossRelic(String relicid){
        ArrayList<String> relics = new ArrayList<>();
        relics.add(Astrolabe.ID);
        relics.add(BlackStar.ID);
        relics.add(BustedCrown.ID);
        relics.add(CallingBell.ID);
        relics.add(CoffeeDripper.ID);
        relics.add(CursedKey.ID);
        relics.add(Ectoplasm.ID);
        relics.add(EmptyCage.ID);
        relics.add(FusionHammer.ID);
        relics.add(PandorasBox.ID);
        relics.add(PhilosopherStone.ID);
        relics.add(RunicDome.ID);
        relics.add(RunicPyramid.ID);
        relics.add(SacredBark.ID);
        relics.add(SlaversCollar.ID);
        relics.add(SneckoEye.ID);
        relics.add(Sozu.ID);
        relics.add(TinyHouse.ID);
        relics.add(VelvetChoker.ID);
        relics.add(BlackBlood.ID);
        relics.add(MarkOfPain.ID);
        relics.add(RunicCube.ID);
        relics.remove(relicid);
        for(String relic : relics){
            BaseMod.removeRelic(RelicLibrary.getRelic(relic));
        }
    }

    @Override
    public void receivePostInitialize() {
        logger.info("Loading badge image and mod options");
        
        // Load the Mod Badge
        Texture badgeTexture = TextureLoader.getTexture(BADGE_IMAGE);
        
        // Create the Mod Menu
        ModPanel settingsPanel = new ModPanel();
        
        // Create the on/off button:
        ModLabeledToggleButton enableAutoCombatButton = new ModLabeledToggleButton("Enable automatic combat.",
                350.0f, 700.0f, Settings.CREAM_COLOR, FontHelper.charDescFont, // Position (trial and error it), color, font
                autoCombat, // Boolean it uses
                settingsPanel, // The mod panel in which this button will be in
                (label) -> {}, // thing??????? idk
                (button) -> { // The actual button:
            
            autoCombat = button.enabled; // The boolean true/false will be whether the button is enabled or not
            try {
                // And based on that boolean, set the settings and save them
                SpireConfig config = new SpireConfig("defaultMod", "theDefaultConfig", STSAISettings);
                config.setBool(ENABLE_AUTO_COMBAT, autoCombat);
                config.save();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        ModLabeledToggleButton enableAutoMapButton = new ModLabeledToggleButton("Enable automatic path selection.",
                350.0f, 670.0f, Settings.CREAM_COLOR, FontHelper.charDescFont,
                autoMap, settingsPanel, (label) -> {}, (button) -> {

            autoMap = button.enabled;
            try {
                // And based on that boolean, set the settings and save them
                SpireConfig config = new SpireConfig("defaultMod", "theDefaultConfig", STSAISettings);
                config.setBool(ENABLE_AUTO_MAP, autoMap);
                config.save();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        ModLabeledToggleButton enableAutoEventButton = new ModLabeledToggleButton("Enable automatic event decisions.",
                350.0f, 640.0f, Settings.CREAM_COLOR, FontHelper.charDescFont,
                autoEvent, settingsPanel, (label) -> {}, (button) -> {

            autoEvent = button.enabled;
            try {
                // And based on that boolean, set the settings and save them
                SpireConfig config = new SpireConfig("defaultMod", "theDefaultConfig", STSAISettings);
                config.setBool(ENABLE_AUTO_EVENT, autoEvent);
                config.save();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        ModLabeledToggleButton enableAutoRewardButton = new ModLabeledToggleButton("Enable automatic reward selection.",
                350.0f, 610.0f, Settings.CREAM_COLOR, FontHelper.charDescFont,
                autoReward, settingsPanel, (label) -> {}, (button) -> {

            autoReward = button.enabled;
            try {
                // And based on that boolean, set the settings and save them
                SpireConfig config = new SpireConfig("defaultMod", "theDefaultConfig", STSAISettings);
                config.setBool(ENABLE_AUTO_REWARD, autoReward);
                config.save();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        ModLabeledToggleButton enableAutoRestButton = new ModLabeledToggleButton("Enable automatic rest site decisions.",
                350.0f, 580.0f, Settings.CREAM_COLOR, FontHelper.charDescFont,
                autoRest, settingsPanel, (label) -> {}, (button) -> {

            autoRest = button.enabled;
            try {
                // And based on that boolean, set the settings and save them
                SpireConfig config = new SpireConfig("defaultMod", "theDefaultConfig", STSAISettings);
                config.setBool(ENABLE_AUTO_REST, autoRest);
                config.save();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        ModLabeledToggleButton enableAutoShopButton = new ModLabeledToggleButton("Enable automatic shop decisions.",
                350.0f, 550.0f, Settings.CREAM_COLOR, FontHelper.charDescFont,
                autoShop, settingsPanel, (label) -> {}, (button) -> {

            autoShop = button.enabled;
            try {
                // And based on that boolean, set the settings and save them
                SpireConfig config = new SpireConfig("defaultMod", "theDefaultConfig", STSAISettings);
                config.setBool(ENABLE_AUTO_SHOP, autoShop);
                config.save();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        ModLabeledToggleButton enableAutoChestButton = new ModLabeledToggleButton("Enable automatic chest decisions.",
                350.0f, 520.0f, Settings.CREAM_COLOR, FontHelper.charDescFont,
                autoChest, settingsPanel, (label) -> {}, (button) -> {

            autoChest = button.enabled;
            try {
                // And based on that boolean, set the settings and save them
                SpireConfig config = new SpireConfig("defaultMod", "theDefaultConfig", STSAISettings);
                config.setBool(ENABLE_AUTO_CHEST, autoChest);
                config.save();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        settingsPanel.addUIElement(enableAutoCombatButton); // Add the button to the settings panel. Button is a go.
        settingsPanel.addUIElement(enableAutoMapButton);
        settingsPanel.addUIElement(enableAutoEventButton);
        settingsPanel.addUIElement(enableAutoRewardButton);
        settingsPanel.addUIElement(enableAutoRestButton);
        settingsPanel.addUIElement(enableAutoShopButton);
        settingsPanel.addUIElement(enableAutoChestButton);

        BaseMod.registerModBadge(badgeTexture, MODNAME, AUTHOR, DESCRIPTION, settingsPanel);

        ConsoleCommand.addCommand("tellhp", BasicCommand.class);
        ConsoleCommand.addCommand("aaa", InterestingCommand.class);
        ConsoleCommand.addCommand("cardinfo", CardInfoCommand.class);
        ConsoleCommand.addCommand("play", PlayCommand.class);
        ConsoleCommand.addCommand("dmg", CalculateDamageCommand.class);
        ConsoleCommand.addCommand("fightai", FightAICommand.class);
        ConsoleCommand.addCommand("mattack", MonsterAttackCommand.class);
        ConsoleCommand.addCommand("mapinfo", MapInfoCommand.class);
        ConsoleCommand.addCommand("einfo", EventInfoCommand.class);

        UseAIItem item = new UseAIItem();
        BaseMod.addTopPanelItem(item);

        // =============== /EVENTS/ =================
        logger.info("Done loading badge Image and mod options");
    }
    
    // =============== / POST-INITIALIZE/ =================

    public static void delayAction(int amt){
        waitCounter -= amt;
    }

    @Override
    public void receivePostDungeonUpdate() {
        if(gameEnded){
            logger.info("Processing gameEnded");
            if(!GameStateListener.checkForDungeonStateChange()){
                return;
            }
        }else{
            boolean change = GameStateListener.checkForDungeonStateChange();
            if(change && stateChanged){
                waitCounter -= 2;
            }
            stateChanged = stateChanged || change;
            if(!stateChanged && ChoiceScreenUtils.getCurrentChoiceType() != ChoiceScreenUtils.ChoiceType.NONE){
                return;
            }
            if(waitCounter < 2){ // wait a few update cycles so that actions don't skip over effects
                waitCounter += 1;
                return;        }
            waitCounter = 0;
            stateChanged = false;
        }

        ChoiceScreenUtils.ChoiceType type = ChoiceScreenUtils.getCurrentChoiceType();
        switch(type){
            case EVENT:
                if(!autoEvent) return;
                EventAI.execute();
                break;
            case CHEST:
                if(!autoChest) return;
                ChestAI.execute();
                break;
            case SHOP_ROOM:
            case SHOP_SCREEN:
                if(!autoShop) return;
                ShopAI.execute();
                break;
            case REST:
                if(!autoRest) return;
                RestSiteAI.execute();
                break;
            case CARD_REWARD:
                if(!autoReward) return;
                CardSelectAI.execute();
                break;
            case COMBAT_REWARD:
                if(!autoReward) return;
                CombatRewardAI.execute();
                break;
            case MAP:
                if(!autoMap) return;
                MapAI.execute();
                break;
            case BOSS_REWARD:
                if(!autoReward) return;
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
                if(ChoiceScreenUtils.isConfirmButtonAvailable())
                    ChoiceScreenUtils.pressConfirmButton();
                break;
            case NONE:
                if(!autoCombat) return;
                // at the start of combat, wait for intents to load in
                // without this, first turn of combat for first card play has always has monster intent at 0
                AbstractMonster m = CombatUtils.getWeakestTarget();
                if(m != null && m.intent == AbstractMonster.Intent.DEBUG){
                    return;
                }

                if(!AbstractDungeon.actionManager.turnHasEnded){
                    if (inBattle && AbstractDungeon.actionManager.phase.equals(GameActionManager.Phase.WAITING_ON_USER)
                            && AbstractDungeon.actionManager.cardQueue.isEmpty()
                            && AbstractDungeon.actionManager.actions.isEmpty()) {
                        AbstractDungeon.actionManager.addToBottom(new FightAIAction());
                        logger.info("FightAIAction added");
                    }
                }
        }
    }

    @Override
    public void receiveOnBattleStart(AbstractRoom abstractRoom) {
        logger.info("Battle Start received");
        inBattle = true;
    }

    @Override
    public void receivePostBattle(AbstractRoom abstractRoom) {
        // triggers only after not losing a combat
        logger.info("Post Battle received");
        inBattle = false;
    }

    @Override
    public void receivePostDeath() {
        logger.info("Post Death received");
        inBattle = false;
        gameEnded = true;
    }

    @Override
    public void receiveStartGame() {
        logger.info("Start Game received");
        inBattle = false;
        gameEnded = false;
    }
}
