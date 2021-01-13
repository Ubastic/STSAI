package SlayTheSpireAIMod.commands;

import SlayTheSpireAIMod.util.CombatUtils;
import basemod.DevConsole;
import basemod.devcommands.ConsoleCommand;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import java.util.ArrayList;

public class MonsterAttackCommand extends ConsoleCommand {
    public MonsterAttackCommand(){
        maxExtraTokens = 1;
        minExtraTokens = 1;
        requiresPlayer = true;
        simpleCheck = true;
    }

    @Override
    protected void execute(String[] tokens, int depth) {
        try{
            DevConsole.log(tokens[depth]);
            int mIndex = Integer.parseInt(tokens[depth]);
            ArrayList<AbstractMonster> monsters = AbstractDungeon.getCurrRoom().monsters.monsters;
            CombatUtils.MonsterAttack attack = new CombatUtils.MonsterAttack(monsters.get(mIndex));
            DevConsole.log(attack.toString());
        }catch(Exception e){
            errorMsg();
        }
    }

    @Override
    protected ArrayList<String> extraOptions(String[] tokens, int depth) {
        ArrayList<String> result = new ArrayList<>();
        int num_monsters = AbstractDungeon.getCurrRoom().monsters.monsters.size();
        for(int i = 1; i <= num_monsters; i++){
            result.add("" + i);
        }
        return result;
    }

    @Override
    protected void errorMsg() {
        DevConsole.couldNotParse();
    }
}
