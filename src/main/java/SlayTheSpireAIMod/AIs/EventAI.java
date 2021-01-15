package SlayTheSpireAIMod.AIs;

import SlayTheSpireAIMod.communicationmod.ChoiceScreenUtils;
import SlayTheSpireAIMod.util.ScreenUpdateUtils;
import basemod.ReflectionHacks;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.events.beyond.*;
import com.megacrit.cardcrawl.events.city.*;
import com.megacrit.cardcrawl.events.exordium.*;
import com.megacrit.cardcrawl.events.shrines.*;

import java.util.*;

/** Class which decides what to during an event. */
public class EventAI {
    /** Make first decision. */
    public static void execute(){
        if(ChoiceScreenUtils.getCurrentChoiceType() != ChoiceScreenUtils.ChoiceType.EVENT) return;
        ArrayList<String> choices = ChoiceScreenUtils.getCurrentChoiceList();
        AbstractEvent event = AbstractDungeon.getCurrRoom().event;

        // Select the only option if there is only one, e.g. [Leave]
        if(choices.size() == 1){
            String choice = choices.get(0);
            ChoiceScreenUtils.makeEventChoice(0);
            // events where such a selection leads to other screen
            if(event instanceof Bonfire && choice.equals("offer")){
                ScreenUpdateUtils.update();
                GridSelectAI.execute();
            }else if(event instanceof Lab){
                // TODO test this
                ScreenUpdateUtils.update();
                CombatRewardAI.execute();
            }else if(event instanceof GremlinWheelGame){
                // TODO
            }
            return;
        }


//        ChoiceScreenUtils.makeEventChoice(0);


        if(event instanceof AccursedBlacksmith){
            // upgrade if possible, leave otherwise (never relic/curse)
            int chosen = tryChoose("forge", "leave");
            if(chosen == 1){ // skip upgrade selection screen
                ScreenUpdateUtils.update();
                GridSelectAI.execute();
            }
        }else if(event instanceof Addict){
            // pay for relic if possible, leave otherwise (never relic/curse)
            tryChoose("offer gold", "leave");
        }else if(event instanceof BackToBasics){
            // upgrade strikes and defends if there are at least 3 to be upgraded, remove card otherwise
            int toUpgrade = 0;
            for(AbstractCard card : AbstractDungeon.player.masterDeck.group){
                if((card.isStarterStrike() || card.isStarterDefend()) && !card.upgraded){
                    toUpgrade += 1;
                }
            }
            if(toUpgrade > 2){
                choose("simplicity");
            }else{
                choose("elegance");
            }
        }else if(event instanceof Beggar){
            // pay to remove card if possible, leave otherwise
            int chosen = tryChoose("offer gold", "leave");
            if(chosen == 1){ // skip remove selection screen
                // TODO check
                choose("continue");
                ScreenUpdateUtils.update();
                GridSelectAI.execute();
            }
        }else if(event instanceof BigFish){
            // gain 5 max HP unless current health is below 40 (never relic/curse)
            if(AbstractDungeon.player.currentHealth < 40){
                choose("banana");
            }else{
                choose("donut");
            }
        }else if(event instanceof Colosseum){
            // always escape
            choose("cowardice");
        }else if(event instanceof CursedTome){
            // never read the book
            choose("leave");
        }else if(event instanceof DeadAdventurer){
            // never search the adventurer
            choose("leave");
        }else if(event instanceof Designer){
            // preference with removable curse: Cheapest card removal - Adjustments - Punch
            // without: Adjustments - Clean Up - Punch

//            boolean adjustmentUpgradesOne = ReflectionHacks.getPrivate(event, Designer.class, "adjustmentUpgradesOne");
            boolean cleanUpRemovesCards = ReflectionHacks.getPrivate(event, Designer.class, "cleanUpRemovesCards");
            if(countRemovableCurses() > 0){
                if(cleanUpRemovesCards){
                    tryChoose("clean up", "adjustments", "punch");
                }else{
                    tryChoose("full service", "adjustments", "punch");
                }
            }else{
                tryChoose("adjustments", "clean up", "punch");
            }
        }else if(event instanceof DrugDealer){
            // always get a Mutagenic Strength
            choose("ingest mutagens");
        }else if(event instanceof Duplicator){
            // never duplicate card
            choose("leave");
        }else if(event instanceof FaceTrader){
            // lose hp to gain gold with more than 60 health, leave otherwise (never trade faces)
            if(AbstractDungeon.player.currentHealth > 60){
                choose("touch");
            }else{
                choose("leave");
            }
        }else if(event instanceof Falling){
            // lose the attack if possible, skill otherwise
            tryChoose("strike", "land");
        }else if(event instanceof ForgottenAltar){
            // take damage for max hp if healthy, desecrate if weak, exchange idol if possible, sacrifice otherwise
            int health = AbstractDungeon.player.currentHealth;
            if(health > 60){
                choose("sacrifice");
            }else if(health < 40){
                choose("desecrate");
            }else{
                tryChoose("offer: golden idol", "sacrifice");
            }
        }else if(event instanceof FountainOfCurseRemoval){
            // always remove curses
            tryChoose("drink", "leave");
        }else if(event instanceof Ghosts){
            // never take apparitions
            choose("refuse");
        }else if(event instanceof GoldenIdolEvent){ // multi-choice event
            // take the idol and take damage if above 60 health
            if(choices.contains("take")){ // first choice
                if(AbstractDungeon.player.currentHealth > 60){
                    choose("take");
                }else{
                    choose("leave");
                }
            }else{ // second choice
                choose("smash");
            }
        }else if(event instanceof GoldShrine){
            // always take gold for no curse
            choose("pray");
        }else if(event instanceof GoldenWing){
            // remove a card if above 40 health, get gold if possible, leave otherwise
            if(AbstractDungeon.player.currentHealth > 40){
                choose("pray");
            }else{
                tryChoose("destroy", "leave");
            }
        }else if(event instanceof KnowingSkull){
            // always leave instantly
            choose("how do i leave?");
        }else if(event instanceof Sssserpent){
            // always refuse gold/curse
            choose("disagree");
        }else if(event instanceof LivingWall){
            // always remove card
            choose("forget");
        }else if(event instanceof MaskedBandits){
            // Fight only if player has >100 gold and >= 50 health
            if(AbstractDungeon.player.gold < 100 || AbstractDungeon.player.currentHealth < 50){
                choose("pay");
            }else{
                choose("fight");
            }
        }else if(event instanceof GremlinMatchGame){
            // keep picking the first cards until the event ends
            for(int i = 0; i < 5; i++){
                ChoiceScreenUtils.makeEventChoice(0);
                ChoiceScreenUtils.makeEventChoice(1);
            }
        }else if(event instanceof MindBloom){
            // always fight act 1 boss
            choose("i am war");
        }else if(event instanceof Mushrooms){
            // take the fight unless below 25 hp
            if(AbstractDungeon.player.currentHealth < 25){
                choose("eat");
            }else{
                choose("stomp");
            }
        }else if(event instanceof MysteriousSphere){
            // never take fight for relic
            choose("leave");
        }else if(event instanceof Nloth){
            // never exchange relic
            choose("leave");
        }else if(event instanceof Nest){
            // never get the dagger
            choose("smash and grab");
        }else if(event instanceof NoteForYourself){
            // never swap card
            choose("ignore");
        }else if(event instanceof PurificationShrine){
            // always remove card
            choose("pray");
            ScreenUpdateUtils.update(); // skip purge selection screen
            GridSelectAI.execute();
        }else if(event instanceof ScrapOoze){
            // never go for the relic
            choose("leave");
        }else if(event instanceof SecretPortal){
            // never jump to the boss
            choose("leave");
        }else if(event instanceof SensoryStone){
            // always go for 1 card reward
            ChoiceScreenUtils.makeEventChoice(0);
//            ScreenUpdateUtils.update();
//            GridSelectAI.execute();
        }else if(event instanceof ShiningLight){
            // upgrade cards with more than 60 health
            if(AbstractDungeon.player.currentHealth > 60){
                choose("enter");
            }else{
                choose("leave");
            }
        }else if(event instanceof Cleric){
            // heal if below 40 health, remove if possible otherwise
            if(AbstractDungeon.player.currentHealth < 40){
                tryChoose("heal", "leave");
            }else{
                int chosen = tryChoose("purify", "leave");
                if(chosen == 1){
                    ScreenUpdateUtils.update(); // skip purge selection screen
                    GridSelectAI.execute();
                }
            }
        }else if(event instanceof TheJoust){
            // always bet on the murderer
            choose("murderer");
        }else if(event instanceof TheLibrary){
            // always heal
            choose("sleep");
        }else if(event instanceof TheMausoleum){
            // never risk curse for relic
            choose("leave");
        }else if(event instanceof MoaiHead){
            // take heal for minus max hp if below 30 health, otherwise sell idol if possible
            if(AbstractDungeon.player.currentHealth < 30){
                choose("jump inside");
            }else{
                tryChoose("offer: golden idol", "leave");
            }
        }else if(event instanceof WomanInBlue){
            // never buy potions
            choose("leave");
        }else if(event instanceof TombRedMask){
            if(choices.get(0).equals("don the red mask")){
                choose("don the red mask");
            }else if(AbstractDungeon.player.gold < 50){
                choose("offer: " + AbstractDungeon.player.gold + " gold");
            }else{
                choose("leave");
            }
        }else if(event instanceof Transmogrifier){
            // always transform a card
            choose("pray");
        }else if(event instanceof UpgradeShrine){
            // always upgrade a card if possible
            int chosen = tryChoose("pray", "leave");
            if(chosen == 1){
                ScreenUpdateUtils.update(); // skip upgrade selection screen
                GridSelectAI.execute();
            }
        }else if(event instanceof Vampires){
            // refuse bites unless you have blood vial
            if(choices.contains("lose blood vial")){
                choose("lose blood vial");
            }else{
                choose("refuse");
            }
        }else if(event instanceof WeMeetAgain){
            // never get relic
            choose("attack");
        }else if(event instanceof WindingHalls){
            // take madness with enough health, take curse only if very low
            if(AbstractDungeon.player.currentHealth > 40){
                choose("embrace madness");
            }else if(AbstractDungeon.player.currentHealth < 10){
                choose("focus");
            }else{
                choose("retrace your steps");
            }
        }else if(event instanceof GoopPuddle){
            // take gold only with at least 40 health
            if(AbstractDungeon.player.currentHealth < 40){
                choose("leave it");
            }else{
                choose("gather gold");
            }
        }
        else{
            ChoiceScreenUtils.makeEventChoice(0);
        }

    }

    /** If possible, choose the first option. Otherwise choose the second option.
     * @return int Return the priority of the choice that was made. */
    public static int tryChoose(String first, String second){
        ArrayList<String> choices = ChoiceScreenUtils.getCurrentChoiceList();
//        DevConsole.log("First: " + first + ", Second: " + second + ", c:" + choices.toString());
        int firstIndex = choices.indexOf(first);
        if(firstIndex != -1){
            ChoiceScreenUtils.makeEventChoice(firstIndex);
            return 1;
        }
        ChoiceScreenUtils.makeEventChoice(choices.indexOf(second));
        return 2;
    }

    /** If possible, choose the first option. Otherwise if possible choose the second option.
     * Otherwise choose the third option.
     * @return int Return the priority of the choice that was made. */
    public static int tryChoose(String first, String second, String third){
        ArrayList<String> choices = ChoiceScreenUtils.getCurrentChoiceList();
        int firstIndex = choices.indexOf(first);
        if(firstIndex != -1){
            ChoiceScreenUtils.makeEventChoice(firstIndex);
            return 1;
        }
        int secondIndex = choices.indexOf(second);
        if(secondIndex != -1){
            ChoiceScreenUtils.makeEventChoice(choices.indexOf(second));
            return 2;
        }
        ChoiceScreenUtils.makeEventChoice(choices.indexOf(third));
        return 3;
    }

    /** Precondition: choice is valid.
     * @param choice The option to be chosen.*/
    public static void choose(String choice){
        ArrayList<String> choices = ChoiceScreenUtils.getCurrentChoiceList();
        ChoiceScreenUtils.makeEventChoice(choices.indexOf(choice));
    }

    /** @return int Return the number of removable curses. */
    public static int countRemovableCurses(){
        int count = 0;
        String[] permCurses = {"AscendersBane", "CurseOfTheBell", "Necronomicurse"};
        HashSet<String> permCurseSet = new HashSet<>(Arrays.asList(permCurses));
        for(AbstractCard card : AbstractDungeon.player.masterDeck.group){
            if(card.type == AbstractCard.CardType.CURSE){
                if(!permCurseSet.contains(card.cardID)){
                    count += 1;
                }
            }
        }
        return count;
    }


}
