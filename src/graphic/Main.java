package graphic;

import koporscho.GameController;

public class Main {
    public static void main(String[] args) {
        GameController gc = GameController.getInstance();
        gc.StartGame("saves/base.txt");
        System.out.println("asdas");
        GUI g = GUI.getInstance();
        g.setVisible(true);
        gc.NotifyViews();

    }
}
