package graphic;

import java.awt.*;
import java.awt.event.*;
import koporscho.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class GUI extends JFrame{
    private GameController gc = GameController.getInstance();
    private Color colorBGR = Color.black;
    private int wWIDTH = 1200;
    private int wHEIGHT = 600;
    enum GUIState {
        DEFAULT, MOVE, APPLY_AGENT_STEP1, APPLY_AGENT_STEP2, CRAFT_AGENT, DROP_EQUIPMENT, CHOP, STEAL_EQUIPMENT_STEP1, STEAL_EQUIPMENT_STEP2
    }
    private HashMap<String, Dimension> imgDim= new HashMap<>();
    private JPanel contentPane = new JPanel();
    private GUIState state = GUIState.DEFAULT;
    private static GUI instance = null;
    public static GUI getInstance() {
        if (instance == null) instance = new GUI();
        return instance;
    }
    private GUI() {
        JPanel UIPanel = new JPanel();

        UIPanel.setBackground(colorBGR);
        Background bgr = new Background();
        EquipmentPanel eq = new EquipmentPanel();
        EquipmentPanel eq1 = new EquipmentPanel();
        EquipmentPanel eq2 = new EquipmentPanel();
        EquipmentPanel eq3 = new EquipmentPanel();
        EquipmentPanel eq4 = new EquipmentPanel();
        //contentPane.setPreferredSize(new Dimension(wWIDTH, wHEIGHT));
        contentPane.setBackground(colorBGR);
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
        contentPane.add(bgr);
        UIPanel.setLayout(new BoxLayout(UIPanel, BoxLayout.X_AXIS));
        UIPanel.add(eq);
        UIPanel.add(eq1);
        UIPanel.add(eq2);
        UIPanel.add(eq3);
        UIPanel.add(eq4);
        contentPane.add(UIPanel);
        setContentPane(contentPane);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setResizable(false);
        setTitle("");
        addKeyListener(new KL());
        imgDim.put("playerIcon",new Dimension(50,50));
        imgDim.put("attributesPanel",new Dimension(50,40));
        imgDim.put("equipmentIcon",new Dimension(30,30));
        imgDim.put("multiUseContainer",new Dimension(50,100));
        imgDim.put("console", new Dimension(250,100));
        imgDim.put("map",new Dimension(100,100));
    }
    private abstract class InterfaceElement extends JPanel {
        protected String name;
        protected Image img = new BufferedImage(wWIDTH, wHEIGHT, BufferedImage.TYPE_INT_ARGB);
        public void init() {setPreferredSize(imgDim.get(name));
        }
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            img.getGraphics().clearRect(0, 0, img.getWidth(null), img.getHeight(null));
        };
    }

    private ArrayList<InterfaceElement> interfaceElements = new ArrayList<>();
    public class Background extends InterfaceElement{
        Image bgr, fieldImage;
        public Background() {
            name = "background";
            setBackground(colorBGR);
            try {
                bgr = ImageIO.read(new File("assets/xdddd.png"));
                img = new BufferedImage(bgr.getWidth(null), bgr.getHeight(null), BufferedImage.TYPE_INT_ARGB);
                imgDim.put(name,new Dimension(img.getWidth(null),img.getHeight(null)));
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            init();
        }
        public Background(Field f) {
            fieldImage = imgMap.get(f);
        }
        @Override
        protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                img.getGraphics().drawImage(bgr,0,0,null);
                g.drawImage(img, 0, 0, this);
                this.repaint();
        }
    }
    public class EquipmentPanel extends InterfaceElement {
        Image bgr, e1, e2, e3;
        public EquipmentPanel() {
            name = "equipmentPanel";
            setOpaque(false);
            try {
                bgr = ImageIO.read(new File("assets/eqbgr.png"));
                e1 = ImageIO.read(new File("assets/eq1.png"));
                e2 = ImageIO.read(new File("assets/eq2.png"));
                e3 = ImageIO.read(new File("assets/eq3.png"));
                img = new BufferedImage(bgr.getWidth(null), bgr.getHeight(null), BufferedImage.TYPE_INT_ARGB);
                imgDim.put(name,new Dimension(img.getWidth(null),img.getHeight(null)));
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            init();
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            img.getGraphics().drawImage(bgr,0,0,null);
            img.getGraphics().drawImage(e1,5,5,null);
            img.getGraphics().drawImage(e2,5,40,null);
            img.getGraphics().drawImage(e3,5,75,null);
            g.drawImage(img, 0, 0, this);
            this.repaint();
        }
    }
    public class AttributesPanel extends InterfaceElement {
        Image ap, amino, nucleo, bgr;
        void Draw(Graphics g) {}
        public AttributesPanel(int ap, Materials mat) {
        }
    }
    public class MultiUse extends InterfaceElement {
        Image bgr, text;
        int state = 0;
        void Draw(Graphics g) {}
    }
    public class Console extends InterfaceElement {
        Image bgr, text;
        void Draw(Graphics g) {}
    }
    public class Map extends InterfaceElement {
        Image map;
        ArrayList<Image> vir;
        void Draw(Graphics g) {}
    }
    private int targetStep1 = 0;
    private HashMap<IViewable, Image> imgMap = new HashMap<>();
    private HashMap<String, Image> equipMap = new HashMap<>();
    private HashMap<Field, Point> fieldCentres = new HashMap<>();

    public class KL implements KeyListener {
        @Override
        public void keyTyped(KeyEvent e) {

        }

        @Override
        public void keyPressed(KeyEvent e) {
            int targetID=-1;
            char input=e.getKeyChar();
            //if(q or e) multiUsePanel ...
            switch (state) {
                case DEFAULT: switch (input){
                    case '1': state=GUIState.MOVE; break;
                    case '2': gc.Interact(); break;
                    case '3': state=GUIState.APPLY_AGENT_STEP1;  break;
                    case '4': state=GUIState.CRAFT_AGENT; break;
                    case '5': state=GUIState.DROP_EQUIPMENT; break;
                    case '6': state=GUIState.CHOP; break;
                    case '7': state=GUIState.STEAL_EQUIPMENT_STEP1; break;
                    default: break;
                }
                case MOVE:switch (input){
                    case '0': state=GUIState.DEFAULT; break;
                    case '1': gc.Move(1); state=GUIState.DEFAULT;break;
                    case '2': gc.Move(2); state=GUIState.DEFAULT;break;
                    case '3': gc.Move(3); state=GUIState.DEFAULT;break;
                    case '4': gc.Move(4); state=GUIState.DEFAULT;break;
                    case '5': gc.Move(5); state=GUIState.DEFAULT;break;
                    case '6': gc.Move(6); state=GUIState.DEFAULT;break;
                    case '7': gc.Move(7); state=GUIState.DEFAULT;break;
                    case '8': gc.Move(8); state=GUIState.DEFAULT;break;
                    case '9': gc.Move(9); state=GUIState.DEFAULT;break;
                    default: break;
                }
                case APPLY_AGENT_STEP1: switch(input){
                    case '0': state=GUIState.DEFAULT; break;
                    case '1': targetID=1; state=GUIState.APPLY_AGENT_STEP2; break;
                    case '2': targetID=2; state=GUIState.APPLY_AGENT_STEP2; break;
                    case '3': targetID=3; state=GUIState.APPLY_AGENT_STEP2; break;
                    case '4': targetID=4; state=GUIState.APPLY_AGENT_STEP2; break;
                    case '5': targetID=5; state=GUIState.APPLY_AGENT_STEP2; break;
                    case '6': targetID=6; state=GUIState.APPLY_AGENT_STEP2; break;
                    case '7': targetID=7; state=GUIState.APPLY_AGENT_STEP2; break;
                    case '8': targetID=8; state=GUIState.APPLY_AGENT_STEP2; break;
                    case '9': targetID=9; state=GUIState.APPLY_AGENT_STEP2; break;
                    default: break;
                }
                case APPLY_AGENT_STEP2:switch (input){
                    case '0': state=GUIState.APPLY_AGENT_STEP1; break;
                    case '1': gc.ApplyAgent(targetID, 1); state=GUIState.DEFAULT; break;
                    case '2': gc.ApplyAgent(targetID, 2); state=GUIState.DEFAULT; break;
                    case '3': gc.ApplyAgent(targetID, 3); state=GUIState.DEFAULT; break;
                    case '4': gc.ApplyAgent(targetID, 4); state=GUIState.DEFAULT; break;
                    case '5': gc.ApplyAgent(targetID, 5); state=GUIState.DEFAULT; break;
                    case '6': gc.ApplyAgent(targetID, 6); state=GUIState.DEFAULT; break;
                    case '7': gc.ApplyAgent(targetID, 7); state=GUIState.DEFAULT; break;
                    case '8': gc.ApplyAgent(targetID, 8); state=GUIState.DEFAULT; break;
                    case '9': gc.ApplyAgent(targetID, 9); state=GUIState.DEFAULT; break;
                    default: break;
                }
                case CRAFT_AGENT:switch (input){
                    case '0': state=GUIState.DEFAULT; break;
                    case '1': gc.CraftAgent(1); state=GUIState.DEFAULT; break;
                    case '2': gc.CraftAgent(2); state=GUIState.DEFAULT; break;
                    case '3': gc.CraftAgent(3); state=GUIState.DEFAULT; break;
                    case '4': gc.CraftAgent(4); state=GUIState.DEFAULT; break;
                    case '5': gc.CraftAgent(5); state=GUIState.DEFAULT; break;
                    case '6': gc.CraftAgent(6); state=GUIState.DEFAULT; break;
                    case '7': gc.CraftAgent(7); state=GUIState.DEFAULT; break;
                    case '8': gc.CraftAgent(8); state=GUIState.DEFAULT; break;
                    case '9': gc.CraftAgent(9); state=GUIState.DEFAULT; break;
                    default: break;
                }
                case DROP_EQUIPMENT:switch (input){
                    case '0': state=GUIState.DEFAULT; break;
                    case '1': gc.DropEquipment(1);  state=GUIState.DEFAULT; break;
                    case '2': gc.DropEquipment(2);  state=GUIState.DEFAULT; break;
                    case '3': gc.DropEquipment(3);  state=GUIState.DEFAULT; break;
                    case '4': gc.DropEquipment(4);  state=GUIState.DEFAULT; break;
                    case '5': gc.DropEquipment(5);  state=GUIState.DEFAULT; break;
                    case '6': gc.DropEquipment(6);  state=GUIState.DEFAULT;break;
                    case '7': gc.DropEquipment(7);  state=GUIState.DEFAULT;break;
                    case '8': gc.DropEquipment(8);  state=GUIState.DEFAULT; break;
                    case '9': gc.DropEquipment(9);  state=GUIState.DEFAULT; break;
                    default: break;
                }
                case CHOP:switch (input){
                    case '0': state=GUIState.DEFAULT; break;
                    case '1': gc.Chop(1); state=GUIState.DEFAULT; break;
                    case '2': gc.Chop(2); state=GUIState.DEFAULT; break;
                    case '3': gc.Chop(3); state=GUIState.DEFAULT; break;
                    case '4': gc.Chop(4); state=GUIState.DEFAULT; break;
                    case '5': gc.Chop(5); state=GUIState.DEFAULT; break;
                    case '6': gc.Chop(6); state=GUIState.DEFAULT; break;
                    case '7': gc.Chop(7); state=GUIState.DEFAULT; break;
                    case '8': gc.Chop(8); state=GUIState.DEFAULT; break;
                    case '9': gc.Chop(9); state=GUIState.DEFAULT;break;
                    default: break;
                }
                case STEAL_EQUIPMENT_STEP1:switch (input){
                    case '0': state=GUIState.DEFAULT; break;
                    case '1': targetID=1; state=GUIState.STEAL_EQUIPMENT_STEP2; break;
                    case '2': targetID=2; state=GUIState.STEAL_EQUIPMENT_STEP2; break;
                    case '3': targetID=3; state=GUIState.STEAL_EQUIPMENT_STEP2; break;
                    case '4': targetID=4; state=GUIState.STEAL_EQUIPMENT_STEP2; break;
                    case '5': targetID=5; state=GUIState.STEAL_EQUIPMENT_STEP2; break;
                    case '6': targetID=6; state=GUIState.STEAL_EQUIPMENT_STEP2; break;
                    case '7': targetID=7; state=GUIState.STEAL_EQUIPMENT_STEP2; break;
                    case '8': targetID=8; state=GUIState.STEAL_EQUIPMENT_STEP2; break;
                    case '9': targetID=9; state=GUIState.STEAL_EQUIPMENT_STEP2; break;
                    default: break;
                }
                case STEAL_EQUIPMENT_STEP2:switch (input){
                    case '0': state=GUIState.STEAL_EQUIPMENT_STEP1; break;
                    case '1': gc.StealEquipment(targetID, 1); state=GUIState.DEFAULT; break;
                    case '2': gc.StealEquipment(targetID, 2); state=GUIState.DEFAULT; break;
                    case '3': gc.StealEquipment(targetID, 3); state=GUIState.DEFAULT; break;
                    case '4': gc.StealEquipment(targetID, 4); state=GUIState.DEFAULT; break;
                    case '5': gc.StealEquipment(targetID, 5); state=GUIState.DEFAULT; break;
                    case '6': gc.StealEquipment(targetID, 6); state=GUIState.DEFAULT; break;
                    case '7': gc.StealEquipment(targetID, 7); state=GUIState.DEFAULT; break;
                    case '8': gc.StealEquipment(targetID, 8); state=GUIState.DEFAULT; break;
                    case '9': gc.StealEquipment(targetID, 9); state=GUIState.DEFAULT; break;
                    default: break;
                }
                default: break;
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {

        }
    }

}
