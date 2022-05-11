package graphic;

import java.awt.*;
import java.awt.event.*;
import koporscho.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class GUI extends JFrame{
    private GameController gc = GameController.getInstance();
    private Color colorBGR = Color.black;
    private int wWIDTH = 1536;
    private int wHEIGHT = 552+350;
    private Font font1 = null;
    private Font font2 = null;
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
        try {
            font1 = Font.createFont(Font.TRUETYPE_FONT, new File("assets/VCR_OSD_MONO_1.001.ttf")).deriveFont(16f); //VCR_OSD_MONO_1.001.ttf
            font2 = Font.createFont(Font.TRUETYPE_FONT, new File("assets/3Dventure.ttf")).deriveFont(16f); //VCR_OSD_MONO_1.001.ttf
        } catch (Exception e) {
            e.printStackTrace();
        }
        JPanel UIPanel = new JPanel();

        UIPanel.setBackground(colorBGR);
        Background bgr = new Background();
        EquipmentPanel eq = new EquipmentPanel();
        AttributesPanel ap = new AttributesPanel();
        MultiUsePanel mu = new MultiUsePanel();
        Map map = new Map();
        Console con = new Console();
        JPanel filler = new JPanel();
        JPanel fill = new JPanel();
        filler.setBackground(Color.gray);
        filler.setLayout(new BorderLayout());
        filler.add(fill, BorderLayout.CENTER);
        fill.setBackground(Color.DARK_GRAY);

        //filler.setPreferredSize(new Dimension(bgr.getWidth(),350));//-eq.getWidth()-ap.getWidth(),350));
        contentPane.setPreferredSize(new Dimension(wWIDTH, wHEIGHT));
        contentPane.setBackground(colorBGR);
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
        contentPane.add(bgr);
        UIPanel.setLayout(new BoxLayout(UIPanel, BoxLayout.X_AXIS));
        UIPanel.add(ap);
        UIPanel.add(eq);
        UIPanel.add(mu);
        UIPanel.add(con);
        UIPanel.add(map);
        UIPanel.add(filler);
        contentPane.add(UIPanel);
        setContentPane(contentPane);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setResizable(false);
        setTitle("");
        addKeyListener(new KL());
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
                bgr = ImageIO.read(new File("assets/lab.png"));
                wWIDTH = bgr.getWidth(null);
                img = new BufferedImage(bgr.getWidth(null), bgr.getHeight(null), BufferedImage.TYPE_INT_ARGB);
                imgDim.put(name,new Dimension(img.getWidth(null),img.getHeight(null)));
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            init();
        }
        public void Update(Field f) {
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
    public class AttributesPanel extends InterfaceElement {
        BufferedImage bgr, portrait, status;
        StatusEffect active = new StatusEffect();
        int amino = 10, nucleo = 5, ap = 3;
        public void Update(int ap, Materials mat) {
        }
        public AttributesPanel() {
            active.SetBear(true);
            active.SetParalyzed(true);
            active.SetAmnesia(true);
            active.SetChorea(true);
            active.SetBagsize(10);
            active.SetDead(true);
            active.SetImmunity(1.0f);
            active.SetReflect(true);
            name = "attributesPanel";
            setOpaque(false);
            try {
                bgr = ImageIO.read(new File("assets/attrbgr.png"));
                portrait = ImageIO.read(new File("assets/portrait.png"));
                status = ImageIO.read(new File("assets/statuseffects.png"));
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
            Graphics gr = img.getGraphics();
            gr.setFont(font1);
            gr.drawImage(bgr,0,0,null);
            gr.drawImage(portrait,8,8,null);
            String aminoStr = String.format("Aminoacid count:  %d", amino);
            String nucleoStr = String.format("Nucleotide count: %d", nucleo);
            String apStr = String.format("Action Points:    %d", ap);
            gr.drawString(aminoStr, 5,230);
            gr.drawString(nucleoStr, 5,246);
            gr.drawString(apStr, 5,262);
            int i = 0;
            int xOffs = 8;
            int yOffs = 8+128+32;
            if(active.GetParalyzed()) {
                BufferedImage image = ((BufferedImage) status).getSubimage(0,0,32,32);
                gr.drawImage(image,xOffs+i%6*32,yOffs-i/6*32,32,32, null);
                i++;
            }
            if(active.GetDead()) {
                BufferedImage image = ((BufferedImage) status).getSubimage(32,0,32,32);
                gr.drawImage(image,xOffs+i%6*32,yOffs-i/6*32,32,32, null);
                i++;
            }
            if(active.GetChorea()) {
                BufferedImage image = ((BufferedImage) status).getSubimage(64,0,32,32);
                gr.drawImage(image,xOffs+i%6*32,yOffs-i/6*32,32,32, null);
                i++;
            }
            if(active.GetReflect()) {
                BufferedImage image = ((BufferedImage) status).getSubimage(96,0,32,32);
                gr.drawImage(image,xOffs+i%6*32,yOffs-i/6*32,32,32, null);
                i++;
            }
            if(active.GetBagsize()>0) {
                BufferedImage image = ((BufferedImage) status).getSubimage(0,32,32,32);
                gr.drawImage(image,xOffs+i%6*32,yOffs-i/6*32,32,32, null);
                i++;
            }
            if(active.GetAmnesia()) {
                BufferedImage image = ((BufferedImage) status).getSubimage(32,32,32,32);
                gr.drawImage(image,xOffs+i%6*32,yOffs-i/6*32,32,32, null);
                i++;
            }
            if(active.GetImmunity()>0) {
                BufferedImage image = ((BufferedImage) status).getSubimage(64,32,32,32);
                gr.drawImage(image,xOffs+i%6*32,yOffs-i/6*32,32,32, null);
                i++;
            }
            if(active.GetBear()) {
                BufferedImage image = ((BufferedImage) status).getSubimage(96,32,32,32);
                gr.drawImage(image,xOffs+i%6*32,yOffs-i/6*32,32,32, null);
                i++;
            }
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
                e1 = ImageIO.read(new File("assets/axe_16.png")).getScaledInstance(64, 64, Image.SCALE_DEFAULT);
                e2 = ImageIO.read(new File("assets/cloak_16.png")).getScaledInstance(64, 64, Image.SCALE_DEFAULT);
                e3 = ImageIO.read(new File("assets/glove_16.png")).getScaledInstance(64, 64, Image.SCALE_DEFAULT);
                img = new BufferedImage(bgr.getWidth(null), bgr.getHeight(null), BufferedImage.TYPE_INT_ARGB);
                imgDim.put(name,new Dimension(img.getWidth(null),img.getHeight(null)));
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            init();
        }
        public void Update(ArrayList<Equipment> eq) {}
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics gr = img.getGraphics();
            gr.setFont(font1);
            gr.drawImage(bgr,0,0,null);
            gr.drawImage(e1,15,350-3*95-10-16,null);
            gr.drawImage(e2,15,350-2*95-10-8,null);
            gr.drawImage(e3,15,350-95-10,null);
            gr.drawString("Equipment:", 8, 25);
            g.drawImage(img, 0, 0, this);
            this.repaint();
        }
    }
    public class MultiUsePanel extends InterfaceElement {
        Image bgr;
        int state = 0;
        public MultiUsePanel() {
            name = "multiUsePanel";
            setOpaque(false);
            try {
                bgr = ImageIO.read(new File("assets/multibgr.png"));
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
            Graphics gr = img.getGraphics();
            gr.setFont(font1);
            gr.drawImage(bgr,0,0,null);
            gr.drawString("<-Q-              -E->", 1,20);
            switch (state) {
                case 0:
                    gr.drawString("Known Agents", 45,20);
                    break;
                case 1:
                    gr.drawString("Owned Agents", 5,40);
                    break;
                case 2:
                    gr.drawString("Virologists on field", 5,40);
                    break;
                    //... TODO
            }
            g.drawImage(img, 0, 0, this);
            this.repaint();
        }
    }
    public class Console extends InterfaceElement {
        Image bgr;
        public Console() {
            name = "console";
            setOpaque(false);
            try {
                bgr = ImageIO.read(new File("assets/consolebgr.png"));
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
            Graphics gr = img.getGraphics();
            gr.setFont(font1);
            gr.drawImage(bgr,0,0,null);
            gr.drawString("This is the Console :)))", 95,60);

            g.drawImage(img, 0, 0, this);
            this.repaint();
        }
    }
    public class Map extends InterfaceElement {
        ArrayList<Point> virLoc = new ArrayList<>();
        Image bgr;
        public Map() {
            name = "map";
            virLoc.add(new Point(40,40));
            virLoc.add(new Point(80,195));
            virLoc.add(new Point(260,195));
            setOpaque(false);
            try {
                bgr = ImageIO.read(new File("assets/mapbgr.png"));
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
            Graphics gr = img.getGraphics();
            gr.setFont(font2);
            gr.setColor(Color.BLACK);
            gr.drawImage(bgr,0,0,null);
            gr.drawString("This is the map OwO", 0,350);
            int i = 1;
            for(Point p: virLoc) {
                String vStr = String.format("V%d", i++);
                gr.drawString(vStr, p.x, p.y);
            }
            g.drawImage(img, 0, 0, this);
            this.repaint();
        }
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
