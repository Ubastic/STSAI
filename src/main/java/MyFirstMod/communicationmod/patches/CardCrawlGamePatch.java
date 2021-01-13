package MyFirstMod.communicationmod.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import MyFirstMod.communicationmod.CommunicationMod;

@SpirePatch(
        clz=CardCrawlGame.class,
        method="dispose"
)
public class CardCrawlGamePatch {

    public static void Prefix(CardCrawlGame _instance) {
        CommunicationMod.dispose();
    }
}
