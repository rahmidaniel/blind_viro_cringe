package graphic;

import koporscho.StatusEffect;
import koporscho.Virologist;

public class VirologistView extends View{
    public void Redraw(IViewable obj){
        Virologist vir = (Virologist) obj;
        boolean bear = false;
        for (StatusEffect st: vir.GetStatusEffects()) {
            if(st.GetBear())
                bear = true;
        }
        if(bear)
            GUI.getInstance().getMapPanel().update(vir, true);
        else
            GUI.getInstance().getMapPanel().update(vir, false);

    }
}
