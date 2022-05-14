package graphic;

import koporscho.GameController;

/** A fő szál megvalósítására szolgáló osztály.*/
public class Main {
    /** A main függvény megvalósítása*/
    public static void main(String[] args) {
        GameController gc = GameController.getInstance();
        gc.StartGame("saves/map.txt");
        GUI g = GUI.getInstance();
        g.setVisible(true);
        gc.NotifyViews();
        gc.GetCurrentVirologist().NotifyViews();
    }
}
