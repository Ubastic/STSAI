package MyFirstMod.commands;

import MyFirstMod.util.CombatUtils;
import basemod.DevConsole;
import basemod.devcommands.ConsoleCommand;
import com.megacrit.cardcrawl.actions.utility.NewQueueCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;

import java.lang.reflect.Field;

import java.util.ArrayList;

/** Dev console command to tell maximum single target damage this turn. */
public class CalculateDamageCommand extends ConsoleCommand {
    public CalculateDamageCommand(){
        maxExtraTokens = 0;
        minExtraTokens = 0;
        requiresPlayer = true;
        simpleCheck = true;
    }

    public void execute(String[] tokens, int depth){

        int energy = AbstractDungeon.player.energy.energy;
        energy = EnergyPanel.totalCount;
        DevConsole.log("Current energy: " + energy);

        ArrayList<AbstractMonster> monsters = AbstractDungeon.getCurrRoom().monsters.monsters;
//        int totalDmg = 0;
//        for(AbstractMonster m : monsters){
//            DevConsole.log(m.name);
//            DevConsole.log(m.intent.toString());
//            if(isAttack(m.intent)){
//                DevConsole.log("Attacking for: " + m.getIntentDmg());
//                try {
//                    Field f = AbstractMonster.class.getDeclaredField("move");
//                    f.setAccessible(true);
//                    EnemyMoveInfo move = (EnemyMoveInfo)f.get(m);
//                    int multiplier = Math.max(1, move.multiplier);
//                    DevConsole.log("Attacking " + multiplier + " times");
//                    totalDmg += m.getIntentDmg() * multiplier;
//                } catch (NoSuchFieldException | IllegalAccessException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//
//        DevConsole.log("Total Dmg: " + totalDmg);

        ArrayList<AbstractCard> Cards = AbstractDungeon.player.hand.group;
        AbstractCard firstAttack = null;
        for(AbstractCard card : Cards){
            if(card.type == AbstractCard.CardType.ATTACK){
                firstAttack = card;
                break;
            }
        }
        AbstractCard firstSkill = null;
        for(AbstractCard card : Cards){
            if(card.type == AbstractCard.CardType.SKILL){
                firstSkill = card;
                break;
            }
        }

        if(firstAttack != null){
            for(AbstractMonster m : monsters){
//                DamageInfo dinfo = new DamageInfo(AbstractDungeon.player, firstAttack.baseDamage, firstAttack.damageTypeForTurn);
//                dinfo.applyPowers(AbstractDungeon.player, m);
//                int damage = dinfo.output;
                int damage = CombatUtils.getDamage(firstAttack, m);
                DevConsole.log("Using card " + firstAttack.name + " on " + m.name +  " deals " + damage + " damage with type " + firstAttack.damageTypeForTurn);
                int d2 = CombatUtils.getDamage(firstSkill, m);
                DevConsole.log("Using card " + firstSkill.name + " on " + m.name +  " deals " + d2 + " damage with type " + firstSkill.damageTypeForTurn);
            }
        }
        else{
            DevConsole.log("No attacks in hand");
        }
    }

    public boolean isAttack(AbstractMonster.Intent intent){
        return intent == AbstractMonster.Intent.ATTACK || intent == AbstractMonster.Intent.ATTACK_BUFF
                || intent == AbstractMonster.Intent.ATTACK_DEBUFF || intent == AbstractMonster.Intent.ATTACK_DEFEND;
    }


    @Override
    public void errorMsg() {
        DevConsole.couldNotParse();
//        DevConsole.log("options are:");
//        DevConsole.log("* [amt]");
//        DevConsole.log("* [amt]");
    }
}
