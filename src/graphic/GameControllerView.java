package graphic;

import koporscho.GameController;
import koporscho.Virologist;

public class GameControllerView extends View{
    public void Redraw(IViewable obj){
        GameController gc = (GameController) obj;
        Virologist cur = gc.GetCurrentVirologist();
        GUI gui = GUI.getInstance();
        gui.getAttrPanel().update(cur.GetApCurrent(),cur.GetCurrentMaterials(),cur.GetStatusEffects());
        gui.getBgrPanel().update(cur.GetField());
        gui.getEqPanel().update(cur.GetEquipment());
        gui.getMuPanel().update(cur);
    }
}
