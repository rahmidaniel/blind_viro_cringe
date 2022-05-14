package graphic;

import koporscho.GameController;
import koporscho.Virologist;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class GameControllerView extends View{
    public void Redraw(IViewable obj){
        GameController gc = (GameController) obj;
        if(!gc.GameRunning()) {
            try {
                BufferedImage image;
                String title;
                if(gc.win) {
                    image = ImageIO.read(new File("assets/winner.png"));
                    title = "The winner is " + gc.GetCurrentVirologist().GetName();
                }
                else {
                    title = "Bears win!";
                    image = ImageIO.read(new File("assets/loser.png"));
                }
                JLabel picLabel = new JLabel(new ImageIcon(image));
                JOptionPane.showMessageDialog(null, picLabel, title, JOptionPane.PLAIN_MESSAGE, null);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            GUI.getInstance().setState(GUI.GUIState.END_GAME);
            System.exit(1);
            return;
        }
        Virologist cur = gc.GetCurrentVirologist();
        GUI gui = GUI.getInstance();
        gui.getAttrPanel().update(cur.GetApCurrent(), cur.GetCurrentMaterials(), cur.GetMaxMaterials(), cur.GetStatusEffects(), cur);
        gui.getBgrPanel().update(cur.GetField());
        gui.getEqPanel().update(cur.GetEquipment());
        gui.getMuPanel().update(cur);
    }
}
