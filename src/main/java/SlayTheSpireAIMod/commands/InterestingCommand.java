package SlayTheSpireAIMod.commands;

import SlayTheSpireAIMod.util.CombatUtils;
import basemod.DevConsole;
import basemod.devcommands.ConsoleCommand;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import java.util.ArrayList;

/** Dev console command for experimenting and gaining experience with available information during a run. */
public class InterestingCommand extends ConsoleCommand {
    public InterestingCommand(){
        maxExtraTokens = 0;
        minExtraTokens = 0;
        requiresPlayer = true;
        simpleCheck = true;
    }

    public void execute(String[] tokens, int depth){
//        String s = AbstractDungeon.getCurrRoom().event.toString();
//        if(AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.EVENT){
//            DevConsole.log("Currently in event phase.");
//
//            DevConsole.log("Big Fish? " + (AbstractDungeon.getCurrRoom().event instanceof BigFish));
//            DevConsole.log("Cleric? " + (AbstractDungeon.getCurrRoom().event instanceof Cleric));
//            if(AbstractDungeon.getCurrRoom().event instanceof Cleric){
//
//                ArrayList<LargeDialogOptionButton> buttons = AbstractDungeon.getCurrRoom().event.imageEventText.optionList;
//                DevConsole.log("oL size: " + buttons.size());
//                if(buttons.size() > 1){
//                    buttons.get(0).pressed = true;
//                }
//            }
//        };
//        for(AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters){
//            DevConsole.log("Name: " + m.name + ", health: " + m.currentHealth);
//        }
//        AbstractMonster weak = CombatUtils.getWeakestTarget();
//        DevConsole.log(CombatUtils.getWeakestTarget().toString());
//        DevConsole.log(weak.name + " h:" + weak.currentHealth);
//        testFightName();
        DevConsole.log("" + AbstractDungeon.actionManager.currentAction.getClass());
    }

    public ArrayList<String> extraOptions(String[] tokens, int depth) {
        ArrayList<String> result = new ArrayList<>();
        int num_cards = AbstractDungeon.player.hand.size();
        for(int i = 1; i <= num_cards; i++){
            result.add("" + i);
        }

//        if(tokens[depth].equals("add") || tokens[depth].equals("lose")) {
//            complete = true;
//        }

        return result;
    }

    public void testFightName(){
        String s = AbstractDungeon.lastCombatMetricKey;
        DevConsole.log(s);
    }


    @Override
    public void errorMsg() {
        DevConsole.couldNotParse();
        DevConsole.log("options are:");
        DevConsole.log("* [amt]");
        DevConsole.log("* [amt]");
    }
}
