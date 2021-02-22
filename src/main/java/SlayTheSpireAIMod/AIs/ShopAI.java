package SlayTheSpireAIMod.AIs;

import SlayTheSpireAIMod.STSAIMod;
import SlayTheSpireAIMod.communicationmod.ChoiceScreenUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

/** Class which decides what to do at a shop (in room or shop screen). */
public class ShopAI {
    public static final Logger logger = LogManager.getLogger(STSAIMod.class.getName());

    /** Buy nothing. */
    public static void execute(){
        logger.info("Executing ShopAI");
        // TODO make basic AI
        ChoiceScreenUtils.ChoiceType type = ChoiceScreenUtils.getCurrentChoiceType();
        if(type != ChoiceScreenUtils.ChoiceType.SHOP_ROOM && type != ChoiceScreenUtils.ChoiceType.SHOP_SCREEN) return;
        ArrayList<String> choices = ChoiceScreenUtils.getCurrentChoiceList();
        if(type == ChoiceScreenUtils.ChoiceType.SHOP_ROOM){
            ChoiceScreenUtils.pressConfirmButton();
        }
        else{
            ChoiceScreenUtils.pressCancelButton(); // exit the shop screen
        }
    }
}
