package graphic;

import java.awt.*;
import java.awt.event.*;
import koporscho.*;
import koporscho.Character;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

public class GUI extends JFrame{
    private HashMap<Field, Point> fieldCentres = new HashMap<>();
    private GameController gc = GameController.getInstance();
    private GameControllerView gcView = new GameControllerView();
    private ArrayList<VirologistView> virViews = new ArrayList<>();
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
    private Background bgrPanel = new Background();
    private EquipmentPanel eqPanel = new EquipmentPanel();
    private AttributesPanel attrPanel = new AttributesPanel();
    private MultiUsePanel muPanel = new MultiUsePanel();
    private Map mapPanel = new Map();
    private Console conPanel = new Console();
    private GUI() {
        gc.AddView(gcView);
        for (koporscho.Character v: gc.GetChQueue()) {
            ((Virologist)v).AddView(new VirologistView());
        }
        try {
            font1 = Font.createFont(Font.TRUETYPE_FONT, new File("assets/VCR_OSD_MONO_1.001.ttf")).deriveFont(16f); //VCR_OSD_MONO_1.001.ttf
            font2 = Font.createFont(Font.TRUETYPE_FONT, new File("assets/3Dventure.ttf")).deriveFont(16f); //VCR_OSD_MONO_1.001.ttf
        } catch (Exception e) {
            e.printStackTrace();
        }
        JPanel UIPanel = new JPanel();

        UIPanel.setBackground(colorBGR);
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
        contentPane.add(bgrPanel);
        UIPanel.setLayout(new BoxLayout(UIPanel, BoxLayout.X_AXIS));
        UIPanel.add(attrPanel);
        UIPanel.add(eqPanel);
        UIPanel.add(muPanel);
        UIPanel.add(conPanel);
        UIPanel.add(mapPanel);
        UIPanel.add(filler);
        contentPane.add(UIPanel);
        setContentPane(contentPane);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setResizable(false);
        setTitle("");
        addKeyListener(new KL());
    }
    public Background getBgrPanel() {return bgrPanel;}
    public EquipmentPanel getEqPanel() {return eqPanel;}
    public AttributesPanel getAttrPanel() {return attrPanel;}
    public MultiUsePanel getMuPanel() {return muPanel;}
    public Map getMapPanel() {return mapPanel;}
    private abstract class InterfaceElement extends JPanel {
        protected String name;
        protected Image img = new BufferedImage(wWIDTH, wHEIGHT, BufferedImage.TYPE_INT_ARGB);
        public void init() {
            setPreferredSize(imgDim.get(name));
            update();
        }
        public abstract void update();
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
        public void update(Field f) {
            update();
            fieldImage = imgMap.get(f);
            img.getGraphics().drawImage(fieldImage,0,0,null);
        }
        public void update() {
            img.getGraphics().drawImage(bgr,0,0,null);
        }
        @Override
        protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(img, 0, 0, this);
                this.repaint();
        }
    }
    public class AttributesPanel extends InterfaceElement {
        BufferedImage bgr, portrait, status;
        public void update() {
            Graphics gr = img.getGraphics();
            gr.setFont(font1);
            gr.drawImage(bgr,0,0,null);
            gr.drawImage(portrait,8,8,null);
        }
        public void update(int ap, Materials mat, ArrayList<StatusEffect> statuses) {
            update();
            Graphics gr = img.getGraphics();
            String aminoStr = String.format("Aminoacid count:  %d", mat.GetAminoAcid());
            String nucleoStr = String.format("Nucleotide count: %d", mat.GetNucleotide());
            String apStr = String.format("Action Points:    %d", ap);
            gr.drawString(aminoStr, 5,230);
            gr.drawString(nucleoStr, 5,246);
            gr.drawString(apStr, 5,262);
            int i = 0;
            int xOffs = 8;
            int yOffs = 8+128+32;
            Boolean[] drawn = new Boolean[8];
            for(StatusEffect s: statuses) {
                if (s.GetParalyzed() && !drawn[0]) {
                    BufferedImage image = status.getSubimage(0, 0, 32, 32);
                    gr.drawImage(image, xOffs + i % 6 * 32, yOffs - i / 6 * 32, 32, 32, null);
                    i++;
                    drawn[0] = true;
                }
                if (s.GetDead() && !drawn[1]) {
                    BufferedImage image = status.getSubimage(32, 0, 32, 32);
                    gr.drawImage(image, xOffs + i % 6 * 32, yOffs - i / 6 * 32, 32, 32, null);
                    i++;
                    drawn[1] = true;
                }
                if (s.GetChorea() && !drawn[2]) {
                    BufferedImage image = status.getSubimage(64, 0, 32, 32);
                    gr.drawImage(image, xOffs + i % 6 * 32, yOffs - i / 6 * 32, 32, 32, null);
                    i++;
                    drawn[2] = true;
                }
                if (s.GetReflect() && !drawn[3]) {
                    BufferedImage image = status.getSubimage(96, 0, 32, 32);
                    gr.drawImage(image, xOffs + i % 6 * 32, yOffs - i / 6 * 32, 32, 32, null);
                    i++;
                    drawn[3] = true;
                }
                if (s.GetBagsize() > 0 && !drawn[4]) {
                    BufferedImage image = status.getSubimage(0, 32, 32, 32);
                    gr.drawImage(image, xOffs + i % 6 * 32, yOffs - i / 6 * 32, 32, 32, null);
                    i++;
                    drawn[4] = true;
                }
                if (s.GetAmnesia() && !drawn[5]) {
                    BufferedImage image = status.getSubimage(32, 32, 32, 32);
                    gr.drawImage(image, xOffs + i % 6 * 32, yOffs - i / 6 * 32, 32, 32, null);
                    i++;
                    drawn[5] = true;
                }
                if (s.GetImmunity() > 0 && !drawn[6]) {
                    BufferedImage image = status.getSubimage(64, 32, 32, 32);
                    gr.drawImage(image, xOffs + i % 6 * 32, yOffs - i / 6 * 32, 32, 32, null);
                    i++;
                    drawn[6] = true;
                }
                if (s.GetBear() && !drawn[7]) {
                    BufferedImage image = status.getSubimage(96, 32, 32, 32);
                    gr.drawImage(image, xOffs + i % 6 * 32, yOffs - i / 6 * 32, 32, 32, null);
                    i++;
                    drawn[7] = true;
                }
            }
        }
        public AttributesPanel() {
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
            g.drawImage(img, 0, 0, this);
            this.repaint();
        }
    }
    public class EquipmentPanel extends InterfaceElement {
        Image bgr; BufferedImage eqImg;
        public EquipmentPanel() {
            name = "equipmentPanel";
            setOpaque(false);
            try {
                bgr = ImageIO.read(new File("assets/eqbgr.png"));
                eqImg = ImageIO.read(new File("assets/equipments.png"));
                img = new BufferedImage(bgr.getWidth(null), bgr.getHeight(null), BufferedImage.TYPE_INT_ARGB);
                imgDim.put(name,new Dimension(img.getWidth(null),img.getHeight(null)));
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            init();
        }
        public void update() {
            Graphics gr = img.getGraphics();
            gr.setFont(font1);
            gr.drawImage(bgr,0,0,null);
            gr.drawString("Equipment:", 8, 25);
        }
        public void update(ArrayList<Equipment> eq) {
            update();
            Graphics gr = img.getGraphics();
            int i = 0;
            int xOffs = 15;
            int yOffs = 65;
            for(Equipment e: eq) {
                if (e.GetName() == "axe") {
                    BufferedImage image = eqImg.getSubimage(0, 0, 64, 64);
                    gr.drawImage(image,xOffs,yOffs+i*98,null);
                    i++;
                }
                if (e.GetName() == "glove") {
                    BufferedImage image = eqImg.getSubimage(64, 0, 64, 64);
                    gr.drawImage(image,xOffs,yOffs+i*98,null);
                    i++;
                }
                if (e.GetName() == "cloak") {
                    BufferedImage image = eqImg.getSubimage(128, 0, 64, 64);
                    gr.drawImage(image,xOffs,yOffs+i*98,null);
                    i++;
                }
            }
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(img, 0, 0, this);
            this.repaint();
        }
    }
    public class MultiUsePanel extends InterfaceElement {
        Image bgr;
        BufferedImage inv ;
        BufferedImage rec;
        BufferedImage field;
        int state = 0;
        public MultiUsePanel() {
            name = "multiUsePanel";
            setOpaque(false);
            try {
                bgr = ImageIO.read(new File("assets/multibgr.png"));
                img = new BufferedImage(bgr.getWidth(null), bgr.getHeight(null), BufferedImage.TYPE_INT_ARGB);
                inv = new BufferedImage(bgr.getWidth(null), bgr.getHeight(null), BufferedImage.TYPE_INT_ARGB);
                rec = new BufferedImage(bgr.getWidth(null), bgr.getHeight(null), BufferedImage.TYPE_INT_ARGB);
                field = new BufferedImage(bgr.getWidth(null), bgr.getHeight(null), BufferedImage.TYPE_INT_ARGB);
                imgDim.put(name,new Dimension(img.getWidth(null),img.getHeight(null)));
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            init();
        }
        public void update() {
            Graphics gInv = inv.getGraphics();
            Graphics gRec = rec.getGraphics();
            Graphics gField = field.getGraphics();

            gInv.setFont(font1);
            gInv.drawImage(bgr,0,0,null);
            gInv.drawString("<-Q-              -E->", 1,20);
            gInv.drawString("Owned Agents", 45,20);

            gRec.setFont(font1);
            gRec.drawImage(bgr,0,0,null);
            gRec.drawString("<-Q-              -E->", 1,20);
            gRec.drawString("Known Agents", 45,20);

            gField.setFont(font1);
            gField.drawImage(bgr,0,0,null);
            gField.drawString("<-Q-              -E->", 1,20);
            gField.drawString("Virologists on field", 45,40);
        }
        public void update(Virologist v) {
            update();
            Graphics gInv = inv.getGraphics();
            Graphics gRec = rec.getGraphics();
            Graphics gField = field.getGraphics();

            int xOffset = 5;
            int yOffset = 60;
            ArrayList<Agent> getAgentInventory = v.GetAgentInventory();
            for (int i = 0; i < getAgentInventory.size(); i++) {
                gInv.drawString(getAgentInventory.get(i).GetName(), xOffset, yOffset + i * 10);
            }

            ArrayList<Agent> getRecipes = v.GetRecipes();
            for (int i = 0; i < getRecipes.size(); i++) {
                gRec.drawString(getRecipes.get(i).GetName(), xOffset, yOffset + i * 10);
            }

            ArrayList<Character> getCharacters = v.GetField().GetCharacters();
            for (int i = 0; i < getCharacters.size(); i++) {
                gField.drawString(((Virologist) getCharacters.get(i)).GetName(), xOffset, yOffset + i * 10);
            }
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(img, 0, 0, this);
            Graphics gr = img.getGraphics();
            switch(state) {
                case 0:
                    gr.drawImage(inv,0,0,null);break;
                case 1:
                    gr.drawImage(rec,0,0,null);break;
                case 2:
                    gr.drawImage(field,0,0,null);break;
            }
            this.repaint();
        }
    }
    public class Console extends InterfaceElement {
        Image bgr;
        static int targetID;
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
            renderOptions(gr);

            g.drawImage(img, 0, 0, this);
            this.repaint();
        }
        public void update() {
            Graphics gr = img.getGraphics();
            gr.drawImage(bgr,0,0,null);
            gr.drawString("This is the Console :)))", 95,60);
        }
        // Console element test

        void renderOptions(Graphics g){
            if(!gc.GameRunning()) return;
            int xBase = 15, yBase = 5;
            int xPadding = 5, yPadding = 1;

            Virologist virologist = GameController.getInstance().GetCurrentVirologist();

            ArrayList<Field> fields = virologist.GetField().GetNeighbors();
            ArrayList<Field> allFields = GameController.getInstance().GetGameMap().GetFields();
            ArrayList<koporscho.Character> characters = virologist.GetField().GetCharacters();
            ArrayList<Agent> agentInventory = virologist.GetAgentInventory();
            ArrayList<Agent> agentRecipes = virologist.GetRecipes();
            ArrayList<Equipment> equipmentInventory = virologist.GetEquipment();
            ArrayList<Equipment> targetInventory = new ArrayList<>();
            if(targetID != -1)
                targetInventory = ((Virologist)virologist.GetField().GetCharacters().get(targetID)).GetEquipment();

            int c = 0;
            switch (state) {
                case DEFAULT:
                    g.drawString("1. Move", xBase + xPadding, yBase + yPadding * c++);
                    g.drawString("2. Interact", xBase + xPadding, yBase + yPadding * c++);
                    g.drawString("3. Apply Agent", xBase + xPadding, yBase + yPadding * c++);
                    g.drawString("4. Craft Agent", xBase + xPadding, yBase + yPadding * c++);
                    g.drawString("5. Drop Equipment", xBase + xPadding, yBase + yPadding * c++);
                    g.drawString("6. Chop", xBase + xPadding, yBase + yPadding * c++);
                    g.drawString("7. Steal Equipment", xBase + xPadding, yBase + yPadding * c);
                    break;
                case MOVE:
                    g.drawString("0. Cancel", xBase + xPadding, yBase + yPadding * c++);

                    for(int i=0; i < fields.size();i++){
                        g.drawString((i+1) + ". field " + allFields.indexOf(fields.get(i))+1, xBase + xPadding, yBase + yPadding * c++);
                    }
                    break;
                case APPLY_AGENT_STEP1, CHOP, STEAL_EQUIPMENT_STEP1:
                    g.drawString("0. Cancel", xBase + xPadding, yBase + yPadding * c++);
                    if(characters.size()==0){
                        g.drawString("No characters found.", xBase + xPadding, yBase + yPadding * c++);
                        break;
                    }
                    for(int i=0; i < characters.size();i++){
                        String name = ((Virologist)characters.get(i)).GetName();
                        name = name.isEmpty() ? "<UNIDENTIFIED>" : name;
                        g.drawString((i+1) + ". " + name, xBase + xPadding, yBase + yPadding * c++);
                    }
                    break;
                case APPLY_AGENT_STEP2:
                    g.drawString("0. Cancel", xBase + xPadding, yBase + yPadding * c++);
                    for(int i=0; i < agentInventory.size();i++){
                        String name = agentInventory.get(i).GetName();
                        name = name.isEmpty() ? "<UNIDENTIFIED>" : name;
                        g.drawString((i+1) + ". " + name, xBase + xPadding, yBase + yPadding * c++);
                    }
                    break;
                case CRAFT_AGENT:
                    g.drawString("0. Cancel", xBase + xPadding, yBase + yPadding * c++);
                    for(int i=0; i < agentRecipes.size();i++){
                        String name = agentRecipes.get(i).GetName();
                        name = name.isEmpty() ? "<UNIDENTIFIED>" : name;
                        g.drawString((i+1) + ". " + name, xBase + xPadding, yBase + yPadding * c++);
                    }
                    break;
                case DROP_EQUIPMENT:
                    g.drawString("0. Cancel", xBase + xPadding, yBase + yPadding * c++);
                    if(characters.size()==0){
                        g.drawString("Equipment inventory is empty.", xBase + xPadding, yBase + yPadding * c++);
                        break;
                    }
                    for(int i=0; i<equipmentInventory.size(); i++){
                        String name= equipmentInventory.get(i).GetName();
                        name = name.isEmpty() ? "<UNIDENTIFIED>" : name;
                        g.drawString((i+1) + ". " + name, xBase + xPadding, yBase + yPadding * c++);
                    }
                    break;
                case STEAL_EQUIPMENT_STEP2:
                    g.drawString("0. Cancel", xBase + xPadding, yBase + yPadding * c++);
                    if(targetInventory.size()==0){
                        g.drawString("Target inventory is empty.", xBase + xPadding, yBase + yPadding * c++);
                        break;
                    }
                    for(int i=0; i < targetInventory.size(); i++){
                        String name= targetInventory.get(i).GetName();
                        name = name.isEmpty() ? "<UNIDENTIFIED>" : name;
                        g.drawString((i+1) + ". " + name, xBase + xPadding, yBase + yPadding * c++);
                    }
                    break;
            }
        }
    }
    public class Map extends InterfaceElement {
        HashMap<Virologist,Point> bearLoc = new HashMap<>();
        Image bgr;
        public Map() {
            name = "map";
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
            fieldCentersFill("saves/fieldCenters.txt");
        }
        public void update() {
            Graphics gr = img.getGraphics();
            gr.drawImage(bgr,0,0,null);

            gr.setFont(font2);
            gr.setColor(Color.BLACK);
            for(Point p: bearLoc.values()) {
                gr.drawString("B", p.x, p.y);
            }
        }
        public void update(Virologist bear) {
            if(bearLoc.containsKey(bear)) {
                bearLoc.get(bear).setLocation(fieldCentres.get(bear.GetField()));
            }
            else {
                bearLoc.put(bear, fieldCentres.get(bear.GetField()));
            }
            update();
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics gr = img.getGraphics();
            g.drawImage(img, 0, 0, this);
            this.repaint();
        }
    }
    private int targetStep1 = 0;
    private HashMap<IViewable, Image> imgMap = new HashMap<>();

    public void fieldCentersFill(String fname){
        try {
            File f = new File(fname);
            Scanner sc = new Scanner(f);
            ArrayList<String> parts = new ArrayList<>();

            while(sc.hasNextLine()){
                parts.addAll(Arrays.stream(sc.nextLine().split(",")).toList());
                fieldCentres.put((Field)gc.objectIDsInv.get(parts.get(0)), new Point(Integer.parseInt(parts.get(1)),Integer.parseInt(parts.get(2))));
            }
            sc.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    public class KL implements KeyListener {
        @Override
        public void keyTyped(KeyEvent e) {

        }

        @Override
        public void keyPressed(KeyEvent e) {
            int targetID = -1;
            char input = e.getKeyChar();
            if (input == 'q') {
                muPanel.state++;
                if (muPanel.state == 3) {
                    muPanel.state = 0;
                }
            } else if (input == 'e') {
                muPanel.state++;
                if (muPanel.state == -1) {
                    muPanel.state = 2;
                }
            } else if(gc.EndTurn()) {
                return;
            } else {
                switch (state) {
                    case DEFAULT:
                        switch (input) {
                            case '1': state = GUIState.MOVE; break;
                            case '2': gc.Interact(); break;
                            case '3': state = GUIState.APPLY_AGENT_STEP1; break;
                            case '4': state = GUIState.CRAFT_AGENT; break;
                            case '5': state = GUIState.DROP_EQUIPMENT; break;
                            case '6': state = GUIState.CHOP; break;
                            case '7':
                                state = GUIState.STEAL_EQUIPMENT_STEP1;
                                break;
                            default:
                                break;
                        }
                    case MOVE:
                        switch (input) {
                            case '0':
                                state = GUIState.DEFAULT;
                                break;
                            case '1':
                                gc.Move(1);
                                state = GUIState.DEFAULT;
                                break;
                            case '2':
                                gc.Move(2);
                                state = GUIState.DEFAULT;
                                break;
                            case '3':
                                gc.Move(3);
                                state = GUIState.DEFAULT;
                                break;
                            case '4':
                                gc.Move(4);
                                state = GUIState.DEFAULT;
                                break;
                            case '5':
                                gc.Move(5);
                                state = GUIState.DEFAULT;
                                break;
                            case '6':
                                gc.Move(6);
                                state = GUIState.DEFAULT;
                                break;
                            case '7':
                                gc.Move(7);
                                state = GUIState.DEFAULT;
                                break;
                            case '8':
                                gc.Move(8);
                                state = GUIState.DEFAULT;
                                break;
                            case '9':
                                gc.Move(9);
                                state = GUIState.DEFAULT;
                                break;
                            default:
                                break;
                        }
                    case APPLY_AGENT_STEP1:
                        switch (input) {
                            case '0':
                                state = GUIState.DEFAULT;
                                break;
                            case '1':
                                targetID = 1;
                                state = GUIState.APPLY_AGENT_STEP2;
                                break;
                            case '2':
                                targetID = 2;
                                state = GUIState.APPLY_AGENT_STEP2;
                                break;
                            case '3':
                                targetID = 3;
                                state = GUIState.APPLY_AGENT_STEP2;
                                break;
                            case '4':
                                targetID = 4;
                                state = GUIState.APPLY_AGENT_STEP2;
                                break;
                            case '5':
                                targetID = 5;
                                state = GUIState.APPLY_AGENT_STEP2;
                                break;
                            case '6':
                                targetID = 6;
                                state = GUIState.APPLY_AGENT_STEP2;
                                break;
                            case '7':
                                targetID = 7;
                                state = GUIState.APPLY_AGENT_STEP2;
                                break;
                            case '8':
                                targetID = 8;
                                state = GUIState.APPLY_AGENT_STEP2;
                                break;
                            case '9':
                                targetID = 9;
                                state = GUIState.APPLY_AGENT_STEP2;
                                break;
                            default:
                                break;
                        }
                    case APPLY_AGENT_STEP2:
                        switch (input) {
                            case '0':
                                state = GUIState.APPLY_AGENT_STEP1;
                                break;
                            case '1':
                                gc.ApplyAgent(targetID, 1);
                                state = GUIState.DEFAULT;
                                break;
                            case '2':
                                gc.ApplyAgent(targetID, 2);
                                state = GUIState.DEFAULT;
                                break;
                            case '3':
                                gc.ApplyAgent(targetID, 3);
                                state = GUIState.DEFAULT;
                                break;
                            case '4':
                                gc.ApplyAgent(targetID, 4);
                                state = GUIState.DEFAULT;
                                break;
                            case '5':
                                gc.ApplyAgent(targetID, 5);
                                state = GUIState.DEFAULT;
                                break;
                            case '6':
                                gc.ApplyAgent(targetID, 6);
                                state = GUIState.DEFAULT;
                                break;
                            case '7':
                                gc.ApplyAgent(targetID, 7);
                                state = GUIState.DEFAULT;
                                break;
                            case '8':
                                gc.ApplyAgent(targetID, 8);
                                state = GUIState.DEFAULT;
                                break;
                            case '9':
                                gc.ApplyAgent(targetID, 9);
                                state = GUIState.DEFAULT;
                                break;
                            default:
                                break;
                        }
                    case CRAFT_AGENT:
                        switch (input) {
                            case '0':
                                state = GUIState.DEFAULT;
                                break;
                            case '1':
                                gc.CraftAgent(1);
                                state = GUIState.DEFAULT;
                                break;
                            case '2':
                                gc.CraftAgent(2);
                                state = GUIState.DEFAULT;
                                break;
                            case '3':
                                gc.CraftAgent(3);
                                state = GUIState.DEFAULT;
                                break;
                            case '4':
                                gc.CraftAgent(4);
                                state = GUIState.DEFAULT;
                                break;
                            case '5':
                                gc.CraftAgent(5);
                                state = GUIState.DEFAULT;
                                break;
                            case '6':
                                gc.CraftAgent(6);
                                state = GUIState.DEFAULT;
                                break;
                            case '7':
                                gc.CraftAgent(7);
                                state = GUIState.DEFAULT;
                                break;
                            case '8':
                                gc.CraftAgent(8);
                                state = GUIState.DEFAULT;
                                break;
                            case '9':
                                gc.CraftAgent(9);
                                state = GUIState.DEFAULT;
                                break;
                            default:
                                break;
                        }
                    case DROP_EQUIPMENT:
                        switch (input) {
                            case '0':
                                state = GUIState.DEFAULT;
                                break;
                            case '1':
                                gc.DropEquipment(1);
                                state = GUIState.DEFAULT;
                                break;
                            case '2':
                                gc.DropEquipment(2);
                                state = GUIState.DEFAULT;
                                break;
                            case '3':
                                gc.DropEquipment(3);
                                state = GUIState.DEFAULT;
                                break;
                            case '4':
                                gc.DropEquipment(4);
                                state = GUIState.DEFAULT;
                                break;
                            case '5':
                                gc.DropEquipment(5);
                                state = GUIState.DEFAULT;
                                break;
                            case '6':
                                gc.DropEquipment(6);
                                state = GUIState.DEFAULT;
                                break;
                            case '7':
                                gc.DropEquipment(7);
                                state = GUIState.DEFAULT;
                                break;
                            case '8':
                                gc.DropEquipment(8);
                                state = GUIState.DEFAULT;
                                break;
                            case '9':
                                gc.DropEquipment(9);
                                state = GUIState.DEFAULT;
                                break;
                            default:
                                break;
                        }
                    case CHOP:
                        switch (input) {
                            case '0':
                                state = GUIState.DEFAULT;
                                break;
                            case '1':
                                gc.Chop(1);
                                state = GUIState.DEFAULT;
                                break;
                            case '2':
                                gc.Chop(2);
                                state = GUIState.DEFAULT;
                                break;
                            case '3':
                                gc.Chop(3);
                                state = GUIState.DEFAULT;
                                break;
                            case '4':
                                gc.Chop(4);
                                state = GUIState.DEFAULT;
                                break;
                            case '5':
                                gc.Chop(5);
                                state = GUIState.DEFAULT;
                                break;
                            case '6':
                                gc.Chop(6);
                                state = GUIState.DEFAULT;
                                break;
                            case '7':
                                gc.Chop(7);
                                state = GUIState.DEFAULT;
                                break;
                            case '8':
                                gc.Chop(8);
                                state = GUIState.DEFAULT;
                                break;
                            case '9':
                                gc.Chop(9);
                                state = GUIState.DEFAULT;
                                break;
                            default:
                                break;
                        }
                    case STEAL_EQUIPMENT_STEP1:
                        switch (input) {
                            case '0':
                                state = GUIState.DEFAULT;
                                break;
                            case '1':
                                targetID = 1;
                                state = GUIState.STEAL_EQUIPMENT_STEP2;
                                break;
                            case '2':
                                targetID = 2;
                                state = GUIState.STEAL_EQUIPMENT_STEP2;
                                break;
                            case '3':
                                targetID = 3;
                                state = GUIState.STEAL_EQUIPMENT_STEP2;
                                break;
                            case '4':
                                targetID = 4;
                                state = GUIState.STEAL_EQUIPMENT_STEP2;
                                break;
                            case '5':
                                targetID = 5;
                                state = GUIState.STEAL_EQUIPMENT_STEP2;
                                break;
                            case '6':
                                targetID = 6;
                                state = GUIState.STEAL_EQUIPMENT_STEP2;
                                break;
                            case '7':
                                targetID = 7;
                                state = GUIState.STEAL_EQUIPMENT_STEP2;
                                break;
                            case '8':
                                targetID = 8;
                                state = GUIState.STEAL_EQUIPMENT_STEP2;
                                break;
                            case '9':
                                targetID = 9;
                                state = GUIState.STEAL_EQUIPMENT_STEP2;
                                break;
                            default:
                                break;
                        }
                    case STEAL_EQUIPMENT_STEP2:
                        switch (input) {
                            case '0':
                                state = GUIState.STEAL_EQUIPMENT_STEP1;
                                break;
                            case '1':
                                gc.StealEquipment(targetID, 1);
                                state = GUIState.DEFAULT;
                                break;
                            case '2':
                                gc.StealEquipment(targetID, 2);
                                state = GUIState.DEFAULT;
                                break;
                            case '3':
                                gc.StealEquipment(targetID, 3);
                                state = GUIState.DEFAULT;
                                break;
                            case '4':
                                gc.StealEquipment(targetID, 4);
                                state = GUIState.DEFAULT;
                                break;
                            case '5':
                                gc.StealEquipment(targetID, 5);
                                state = GUIState.DEFAULT;
                                break;
                            case '6':
                                gc.StealEquipment(targetID, 6);
                                state = GUIState.DEFAULT;
                                break;
                            case '7':
                                gc.StealEquipment(targetID, 7);
                                state = GUIState.DEFAULT;
                                break;
                            case '8':
                                gc.StealEquipment(targetID, 8);
                                state = GUIState.DEFAULT;
                                break;
                            case '9':
                                gc.StealEquipment(targetID, 9);
                                state = GUIState.DEFAULT;
                                break;
                            default:
                                break;
                        }
                    default:
                        break;
                }
                Console.targetID = targetID;
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {

        }
    }

}
