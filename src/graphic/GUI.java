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
import java.util.*;

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
        DEFAULT, MOVE, APPLY_AGENT_STEP1, APPLY_AGENT_STEP2, CRAFT_AGENT, DROP_EQUIPMENT, CHOP, STEAL_EQUIPMENT_STEP1, STEAL_EQUIPMENT_STEP2, END_GAME
    }
    private HashMap<String, Dimension> imgDim= new HashMap<>();
    private JPanel contentPane = new JPanel();
    private GUIState state = GUIState.DEFAULT;
    private static GUI instance = null;
    public static GUI getInstance() {
        if (instance == null) instance = new GUI();
        return instance;
    }
    private Background bgrPanel;
    private EquipmentPanel eqPanel;
    private AttributesPanel attrPanel;
    private MultiUsePanel muPanel;
    private Map mapPanel;
    private Console conPanel = new Console();
    private GUI() {
        try {
            font1 = Font.createFont(Font.TRUETYPE_FONT, new File("assets/VCR_OSD_MONO_1.001.ttf")).deriveFont(16f); //VCR_OSD_MONO_1.001.ttf
            font2 = Font.createFont(Font.TRUETYPE_FONT, new File("assets/3Dventure.ttf")).deriveFont(32f); //VCR_OSD_MONO_1.001.ttf
            int i = 1;
            for (Character c: gc.GetChQueue()) {
                String fname = String.format("assets/virologist%d.png",i++);
                BufferedImage img = ImageIO.read(new File(fname));
                imgMap.put((IViewable) c, img);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        bgrPanel = new Background();
        eqPanel = new EquipmentPanel();
        attrPanel = new AttributesPanel();
        muPanel = new MultiUsePanel();
        mapPanel = new Map();
        gc.AddView(gcView);
        for (koporscho.Character v: gc.GetChQueue()) {
            ((Virologist)v).AddView(new VirologistView());
        }
        JPanel UIPanel = new JPanel();

        UIPanel.setBackground(colorBGR);

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
            repaint();
        }
        public void update() {
            img.getGraphics().clearRect(0, 0, img.getWidth(null), img.getHeight(null));
        };
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
        };
    }
    public class Background extends InterfaceElement{
        Image lab, storage, shelter, city, fieldImage;
        public Background() {
            name = "background";
            setBackground(Color.RED);//colorBGR);
            try {
                lab = ImageIO.read(new File("assets/lab.png"));
                shelter = ImageIO.read(new File("assets/shelter.png"));
                storage = ImageIO.read(new File("assets/storage.png"));
                city = ImageIO.read(new File("assets/city.png"));
                wWIDTH = lab.getWidth(null);
                img = new BufferedImage(lab.getWidth(null), lab.getHeight(null), BufferedImage.TYPE_INT_ARGB);
                imgDim.put(name,new Dimension(img.getWidth(null),img.getHeight(null)));
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            init();
        }
        public void update(Field f) {
            update();
            if(gc.objectIDs.get(f).contains("City")){
                fieldImage = city;
            }
            if(gc.objectIDs.get(f).contains("Shelter")){
                fieldImage = shelter;
            }
            if(gc.objectIDs.get(f).contains("Lab")){
                fieldImage = lab;
            }
            if(gc.objectIDs.get(f).contains("Storage")){
                fieldImage = storage;
            }

            if(fieldImage!=null) img.getGraphics().drawImage(fieldImage,0,0,null);

            repaint();
            }
            @Override
            public void update() {
            super.update();
            //img.getGraphics().drawImage(bgr,0,0,null);
        }
        @Override
        protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(img, 0, 0, this);
                this.repaint();
        }
    }
    public class AttributesPanel extends InterfaceElement {
        BufferedImage bgr, status, portrait;
        public void update() {
            super.update();
            Graphics gr = img.getGraphics();
            gr.drawImage(bgr,0,0,null);
            gr.drawImage(portrait,8,8,null);
        }
        public void update(int ap, Materials currMat, Materials maxMat, ArrayList<StatusEffect> statuses, Virologist v) {
            portrait = (BufferedImage) imgMap.get((IViewable) v);
            update();
            Graphics gr = img.getGraphics();
            gr.setFont(font1);
            String aminoStr = String.format("AminoAcid:  %d/%d", currMat.GetAminoAcid(), maxMat.GetAminoAcid());
            String nucleoStr = String.format("Nucleotide: %d/%d", currMat.GetNucleotide(), maxMat.GetNucleotide());
            String apStr = String.format("Action Points:    %d", ap);
            gr.drawString(aminoStr, 5,230+16);
            gr.drawString(nucleoStr, 5,246+16);
            gr.drawString(apStr, 5,262+16);
            int i = 0;
            int xOffs = 8;
            int yOffs = 8+128+32;
            Boolean[] drawn = {false,false,false,false,false,false,false,false};
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
            repaint();
        }
        public AttributesPanel() {
            name = "attributesPanel";
            setOpaque(false);
            try {
                bgr = ImageIO.read(new File("assets/attrbgr.png"));
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
        Image bgr, slot; BufferedImage eqImg;
        public EquipmentPanel() {
            name = "equipmentPanel";
            setOpaque(false);
            try {
                bgr = ImageIO.read(new File("assets/eqbgr.png"));
                slot = ImageIO.read(new File("assets/equipmentSlot.png"));
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
            super.update();
            Graphics gr = img.getGraphics();
            gr.setFont(font1);
            gr.drawString("Equipments:", 8, 25);
        }
        public void update(ArrayList<Equipment> eq) {
            update();
            Graphics gr = img.getGraphics();

            int xOffs = 15;
            int yOffs = 65;
            for (int j = 0; j < 3; j++) {
                gr.drawImage(slot,xOffs-4, yOffs-4 + j * 96,null);
            }
            for (int i= 0; i < eq.size(); i++) {
                Equipment e = eq.get(i);
                if (e.GetName().equals("axe")) {
                    BufferedImage image;
                    if(e.GetDurability() > 0) image = eqImg.getSubimage(0, 0, 64, 64);
                    else image = eqImg.getSubimage(128+2*64, 0, 64, 64);
                    gr.drawImage(image, xOffs, yOffs + i * 96, null);
                    i++;
                }
                if (e.GetName().equals("gloves")) {
                    BufferedImage image = eqImg.getSubimage(64, 0, 64, 64);
                    gr.drawImage(image, xOffs, yOffs + i * 96, null);
                    i++;
                }
                if (e.GetName().equals("cloak")) {
                    BufferedImage image = eqImg.getSubimage(128, 0, 64, 64);
                    gr.drawImage(image, xOffs, yOffs + i * 96, null);
                    i++;
                }
                if (e.GetName().equals("bag")) {
                    BufferedImage image = eqImg.getSubimage(128 + 64, 0, 64, 64);
                    gr.drawImage(image, xOffs, yOffs + i * 96, null);
                    i++;
                }
            }
            repaint();
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(img, 0, 0, this);
            this.repaint();
        }
    }
    public class MultiUsePanel extends InterfaceElement {
        Image bgr, slot;
        BufferedImage inv ;
        BufferedImage rec;
        BufferedImage field;
        int state = 0;
        public MultiUsePanel() {
            name = "multiUsePanel";
            setOpaque(false);
            try {
                bgr = ImageIO.read(new File("assets/multipanel.png"));
                slot = ImageIO.read(new File("assets/slot.png"));
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
            super.update();

            Graphics gInv = inv.getGraphics();
            Graphics gRec = rec.getGraphics();
            Graphics gField = field.getGraphics();

            Font big = font1.deriveFont(22.0f);

            gInv.setFont(big);
            gRec.setFont(big);
            gField.setFont(big);
            int xPadding = 4, yPadding = 2;
            int xText = 45, yText = 20;
            gInv.drawImage(bgr,0,0,null);
            gInv.drawString("Q", (int) (xPadding * 2.5), yPadding + yText);
            gInv.drawString("E", this.getWidth() - xPadding * 5, yPadding + yText);
            gInv.drawString("Inventory", xText, yText);

            gRec.drawImage(bgr,0,0,null);
            gRec.drawString("Q", (int) (xPadding * 2.5), yPadding + yText);
            gRec.drawString("E", this.getWidth() - xPadding * 5, yPadding + yText);
            gRec.drawString("Recipes", xText,yText);

            gField.drawImage(bgr,0,0,null);
            gField.drawString("Q",(int) (xPadding * 2.5), yPadding + yText);
            gField.drawString("E", this.getWidth() - xPadding * 5, yPadding + yText);
            gField.drawString("Targets", xText, yText);
        }
        public void update(Virologist v) {
            update();
            Graphics gInv = inv.getGraphics();
            Graphics gRec = rec.getGraphics();
            Graphics gField = field.getGraphics();

            Font big = font1.deriveFont(22.0f);
            gInv.setFont(big);
            gRec.setFont(big);
            gField.setFont(big);
            int xOffset = 30;
            int yOffset = 60;
            int yPadding = 30;
            ArrayList<Agent> getAgentInventory = v.GetAgentInventory();
            for (int i = 0; i < getAgentInventory.size(); i++) {
                gInv.drawImage(slot,xOffset - 2, (int) (yOffset + i * yPadding - yPadding * 0.7),null);
                gInv.drawString(getAgentInventory.get(i).GetName(), xOffset, yOffset + i * yPadding);
            }

            ArrayList<Agent> getRecipes = v.GetRecipes();
            for (int i = 0; i < getRecipes.size(); i++) {
                gRec.drawImage(slot,xOffset - 2,(int) (yOffset + i * yPadding - yPadding * 0.7),null);
                gRec.drawString(getRecipes.get(i).GetName(), xOffset, yOffset + i * yPadding);
            }

            ArrayList<Character> getCharacters = v.GetField().GetCharacters();
            for (int i = 0; i < getCharacters.size(); i++) {
                gField.drawImage(slot,xOffset - 2, (int) (yOffset + i * yPadding - yPadding * 0.7),null);
                gField.drawString(((Virologist) getCharacters.get(i)).GetName(), xOffset, yOffset + i * yPadding);
            }
            repaint();
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics gr = img.getGraphics();
            switch(state) {
                case 0:
                    gr.drawImage(inv,0,0,null);break;
                case 1:
                    gr.drawImage(rec,0,0,null);break;
                case 2:
                    gr.drawImage(field,0,0,null);break;
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
            update();
            super.paintComponent(g);
            Graphics gr = img.getGraphics();
            Font big = font1.deriveFont(22.0f);
            gr.setFont(big);
            renderOptions(gr);
            g.drawImage(img, 0, 0, this);
            this.repaint();
        }
        public void update() {
            super.update();
            Graphics gr = img.getGraphics();
            gr.drawImage(bgr,0,0,null);
        }
        // Console element test

        void renderOptions(Graphics g){
            if(!gc.GameRunning()) return;
            int xBase = 10, yBase = 35;
            int xPadding = 5, yPadding = 22;

            Virologist virologist = GameController.getInstance().GetCurrentVirologist();

            ArrayList<Field> fields = virologist.GetField().GetNeighbors();
            ArrayList<koporscho.Character> characters = virologist.GetField().GetCharacters();
            ArrayList<Agent> agentInventory = virologist.GetAgentInventory();
            ArrayList<Agent> agentRecipes = virologist.GetRecipes();
            ArrayList<Equipment> equipmentInventory = virologist.GetEquipment();
            ArrayList<Equipment> targetInventory = new ArrayList<>();
            if(targetStep1 > 0 && targetStep1 < characters.size())
                targetInventory = ((Virologist)virologist.GetField().GetCharacters().get(targetStep1)).GetEquipment();

            g.drawString(gc.GetCurrentVirologist().GetName()+"'s turn.", xBase + xPadding, yBase);

            if(gc.GetCurrentVirologist().GetApCurrent()==0) {
                g.drawString("No action points left.", xBase + xPadding, yBase+yPadding);
                g.drawString("Press any key to end turn.", xBase + xPadding, yBase+yPadding*2);
                return;
            }
            int c = 1;

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
                        g.drawString((i+1) + ". field " + gc.objectIDs.get(fields.get(i)), xBase + xPadding, yBase + yPadding * c++); //allFields.indexOf(fields.get(i))+1
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
                case END_GAME: g.drawString("Game over. Press any key to exit.", xBase + xPadding, yBase + yPadding * c++);
                    break;
            }
        }
    }
    public class Map extends InterfaceElement {
        HashMap<Virologist, Point> virLoc = new HashMap<>();
        HashSet<Virologist> bears = new HashSet<>();
        Image bgr;
        String currID;
        public Map() {
            name = "map";
            currID = "";
            setOpaque(false);
            try {
                bgr = ImageIO.read(new File("assets/mapbgr.png"));
                img = new BufferedImage(bgr.getWidth(null), bgr.getHeight(null), BufferedImage.TYPE_INT_ARGB);
                img.getGraphics().setFont(font2);
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
            gr.setColor(Color.WHITE);
            gr.setFont(font2);
            HashMap<Point, Integer> pointOffSets = new HashMap<>();
            for(Virologist v: virLoc.keySet()) {
                Point p = virLoc.get(v);
                Integer offset = pointOffSets.get(p);
                int offs = offset == null ? 0 : offset;
                if(bears.contains(v)) {
                    gr.drawString("B", p.x, p.y + 18 * offs++);
                }
                else if(Objects.equals(currID, gc.objectIDs.get(v))){
                    gr.drawString("V", p.x, p.y + 18 * offs++);
                }
                pointOffSets.put(p, offs);
            }
            if(state == GUIState.MOVE) {
                Virologist v = (Virologist) gc.objectIDsInv.get(currID);
                ArrayList<Field> neighbors = v.GetField().GetNeighbors();
                for(int i = 0; i < neighbors.size();i++) {
                    String str = String.format("%d",i+1);
                    Point pt = fieldCentres.get(neighbors.get(i));
                    Integer offset = pointOffSets.get(pt);
                    int offs = offset == null ? 0 : offset;
                    gr.drawString(str, pt.x, pt.y+18*offs);
                }
            }
            repaint();
        }
        public void update(Virologist v, boolean bear) {
            currID = gc.objectIDs.get(v);
            virLoc.put(v, fieldCentres.get(v.GetField()));
            if(bear) {
                bears.add(v);
            }
            update();
            repaint();
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

            while(sc.hasNextLine()) {
                parts.addAll(Arrays.stream(sc.nextLine().split(",")).toList());
                fieldCentres.put((Field)gc.objectIDsInv.get(parts.get(0)), new Point(Integer.parseInt(parts.get(1)),Integer.parseInt(parts.get(2))));
                parts = new ArrayList<>();
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

            char input = e.getKeyChar();
            if (input == 'e') {
                muPanel.state++;
                if (muPanel.state == 3) {
                    muPanel.state = 0;
                }
            } else if (input == 'q') {
                muPanel.state--;
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
                            case '7': state = GUIState.STEAL_EQUIPMENT_STEP1;break;
                            default:
                                break;
                        }break;
                    case MOVE:
                        switch (input) {
                            case '0': state = GUIState.DEFAULT; break;
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
                        }break;
                    case APPLY_AGENT_STEP1:
                        switch (input) {
                            case '0':
                                state = GUIState.DEFAULT;
                                break;
                            case '1':
                                targetStep1 = 1;
                                state = GUIState.APPLY_AGENT_STEP2;
                                break;
                            case '2':
                                targetStep1 = 2;
                                state = GUIState.APPLY_AGENT_STEP2;
                                break;
                            case '3':
                                targetStep1 = 3;
                                state = GUIState.APPLY_AGENT_STEP2;
                                break;
                            case '4':
                                targetStep1 = 4;
                                state = GUIState.APPLY_AGENT_STEP2;
                                break;
                            case '5':
                                targetStep1 = 5;
                                state = GUIState.APPLY_AGENT_STEP2;
                                break;
                            case '6':
                                targetStep1 = 6;
                                state = GUIState.APPLY_AGENT_STEP2;
                                break;
                            case '7':
                                targetStep1 = 7;
                                state = GUIState.APPLY_AGENT_STEP2;
                                break;
                            case '8':
                                targetStep1 = 8;
                                state = GUIState.APPLY_AGENT_STEP2;
                                break;
                            case '9':
                                targetStep1 = 9;
                                state = GUIState.APPLY_AGENT_STEP2;
                                break;
                            default:
                                break;
                        }break;
                    case APPLY_AGENT_STEP2:
                        switch (input) {
                            case '0':
                                state = GUIState.APPLY_AGENT_STEP1;
                                break;
                            case '1':
                                gc.ApplyAgent(targetStep1, 1);
                                state = GUIState.DEFAULT;
                                break;
                            case '2':
                                gc.ApplyAgent(targetStep1, 2);
                                state = GUIState.DEFAULT;
                                break;
                            case '3':
                                gc.ApplyAgent(targetStep1, 3);
                                state = GUIState.DEFAULT;
                                break;
                            case '4':
                                gc.ApplyAgent(targetStep1, 4);
                                state = GUIState.DEFAULT;
                                break;
                            case '5':
                                gc.ApplyAgent(targetStep1, 5);
                                state = GUIState.DEFAULT;
                                break;
                            case '6':
                                gc.ApplyAgent(targetStep1, 6);
                                state = GUIState.DEFAULT;
                                break;
                            case '7':
                                gc.ApplyAgent(targetStep1, 7);
                                state = GUIState.DEFAULT;
                                break;
                            case '8':
                                gc.ApplyAgent(targetStep1, 8);
                                state = GUIState.DEFAULT;
                                break;
                            case '9':
                                gc.ApplyAgent(targetStep1, 9);
                                state = GUIState.DEFAULT;
                                break;
                            default:
                                break;
                        }break;
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
                        }break;
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
                        }break;
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
                        }break;
                    case STEAL_EQUIPMENT_STEP1:
                        switch (input) {
                            case '0':
                                state = GUIState.DEFAULT;
                                break;
                            case '1':
                                targetStep1 = 1;
                                state = GUIState.STEAL_EQUIPMENT_STEP2;
                                break;
                            case '2':
                                targetStep1 = 2;
                                state = GUIState.STEAL_EQUIPMENT_STEP2;
                                break;
                            case '3':
                                targetStep1 = 3;
                                state = GUIState.STEAL_EQUIPMENT_STEP2;
                                break;
                            case '4':
                                targetStep1 = 4;
                                state = GUIState.STEAL_EQUIPMENT_STEP2;
                                break;
                            case '5':
                                targetStep1 = 5;
                                state = GUIState.STEAL_EQUIPMENT_STEP2;
                                break;
                            case '6':
                                targetStep1 = 6;
                                state = GUIState.STEAL_EQUIPMENT_STEP2;
                                break;
                            case '7':
                                targetStep1 = 7;
                                state = GUIState.STEAL_EQUIPMENT_STEP2;
                                break;
                            case '8':
                                targetStep1 = 8;
                                state = GUIState.STEAL_EQUIPMENT_STEP2;
                                break;
                            case '9':
                                targetStep1 = 9;
                                state = GUIState.STEAL_EQUIPMENT_STEP2;
                                break;
                            default:
                                break;
                        }break;
                    case STEAL_EQUIPMENT_STEP2:
                        switch (input) {
                            case '0':
                                state = GUIState.STEAL_EQUIPMENT_STEP1;
                                break;
                            case '1':
                                gc.StealEquipment(targetStep1, 1);
                                state = GUIState.DEFAULT;
                                break;
                            case '2':
                                gc.StealEquipment(targetStep1, 2);
                                state = GUIState.DEFAULT;
                                break;
                            case '3':
                                gc.StealEquipment(targetStep1, 3);
                                state = GUIState.DEFAULT;
                                break;
                            case '4':
                                gc.StealEquipment(targetStep1, 4);
                                state = GUIState.DEFAULT;
                                break;
                            case '5':
                                gc.StealEquipment(targetStep1, 5);
                                state = GUIState.DEFAULT;
                                break;
                            case '6':
                                gc.StealEquipment(targetStep1, 6);
                                state = GUIState.DEFAULT;
                                break;
                            case '7':
                                gc.StealEquipment(targetStep1, 7);
                                state = GUIState.DEFAULT;
                                break;
                            case '8':
                                gc.StealEquipment(targetStep1, 8);
                                state = GUIState.DEFAULT;
                                break;
                            case '9':
                                gc.StealEquipment(targetStep1, 9);
                                state = GUIState.DEFAULT;
                                break;
                            default:
                                break;
                        }break;
                    default:
                        break;
                }
            }
            mapPanel.update();
        }

        @Override
        public void keyReleased(KeyEvent e) {

        }
    }
    public void setState(GUIState _state) {
        state = _state;
    }
}
