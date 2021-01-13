package MyFirstMod.commands;

import basemod.DevConsole;
import basemod.devcommands.ConsoleCommand;
import com.megacrit.cardcrawl.actions.utility.NewQueueCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PotionHelper;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/** Dev console command for playing a card from hand. */
public class PlayCommand extends ConsoleCommand {
    public PlayCommand(){
        maxExtraTokens = 2;
        minExtraTokens = 2;
        requiresPlayer = true;
//        simpleCheck = true;
    }

    /** With tokens: [card #] [target #]
     * plays the selected card (numbered from left to right starting at 1) at the target if applicable
     * (numbered from left to right starting at 1, ignoring dead monsters) */
    public void execute(String[] tokens, int depth){
        if(tokens.length != 3){
            DevConsole.log("3 tokens not given");
            errorMsg();
            return;
        }
        String cardNumToken = tokens[depth];
        String targetToken = tokens[depth + 1];

        ArrayList<AbstractCard> Cards = AbstractDungeon.player.hand.group;
        ArrayList<AbstractMonster> monsters = AbstractDungeon.getCurrRoom().monsters.monsters;
        try{
            int targetNum = Integer.parseInt(targetToken);
            AbstractMonster targetMonster = null;
            int current = 1;
            for(AbstractMonster m : monsters){
                if(!m.isDead){
                    if(current == targetNum){
                        targetMonster = m;
                        break;
                    }
                    current += 1;
                }
            }
            int cardNum = Integer.parseInt(cardNumToken) - 1;
            AbstractCard card = Cards.get(cardNum);
            AbstractDungeon.actionManager.addToBottom(new NewQueueCardAction(card, targetMonster));
        }catch(NumberFormatException e){
            DevConsole.log("Card # not given as int");
            errorMsg();
        }catch(IndexOutOfBoundsException e){
            DevConsole.log("Invalid #");
            errorMsg();
        }catch(Exception e){
            DevConsole.log("unexpected error");
        }
    }

    public ArrayList<String> extraOptions(String[] tokens, int depth) {
        ArrayList<String> result = new ArrayList<>();
        int numCards = AbstractDungeon.player.hand.size();

        for(int i = 1; i <= numCards; ++i) {
            result.add(String.valueOf(i));
        }

        if (result.contains(tokens[depth]) && tokens.length > depth + 1) {
            result.clear();
            int current = 1;
            for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
                if(!m.isDead){
                    result.add(String.valueOf(current));
                    current += 1;
                }
//                result.add(m.name.replace(' ', '_'));
            }

            if (result.contains(tokens[depth + 1])) {
                complete = true;
            }
        }

        return result;
    }
//    public ArrayList<String> extraOptions(String[] tokens, int depth) {
//        ArrayList<String> result = new ArrayList<>();
//        int num_cards = AbstractDungeon.player.hand.size();
//        for(int i = 1; i <= num_cards; i++){
//            result.add("" + i);
//        }
//
////        if(tokens[depth].equals("add") || tokens[depth].equals("lose")) {
////            complete = true;
////        }
//
//        return result;
//    }

    @Override
    public void errorMsg() {
        DevConsole.couldNotParse();
//        DevConsole.log("options are:");
//        DevConsole.log("* [amt]");
//        DevConsole.log("* [amt]");
    }
}
