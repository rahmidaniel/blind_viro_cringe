package graphic;

import koporscho.*;
import proto.Test;

import java.io.*;
import java.lang.Character;
import java.util.*;

public class SaveLoader {
    private static GameController gc;
    public static ArrayList<Test> menuOptions = new ArrayList<>();
    private static Scanner fileScanner;
    private static int selectedOption;
    private static boolean running = true;
    public static HashMap<Object, String> objectIDs = new HashMap<>();
    public static HashMap<String, Object> objectIDsInv = new HashMap<>();

    public static ArrayList<Field> fields = new ArrayList<>();
    public static ArrayDeque<koporscho.Character> virologists = new ArrayDeque<>();
    public static ArrayList<Agent> agents = new ArrayList<>();
    public static ArrayList<Equipment> equipment = new ArrayList<>();
    public static ArrayList<StatusEffect> statusEffects = new ArrayList<>();

    public static float diceRoll = 0.5f;

    private static String fileName = "output.txt";

    public static boolean random = false;

    public static void SetGc(GameController g){
        gc=g;
    }

    /**
     * A Prototípus futását megvalósító függvény, itt választhatóak ki a különböző tesztesetek futtatásra.
     */

    /**
     * A paraméterként kapott szöveges parancs felismerését végző függvény. Továbbítja a parancsat az azt tényleges végrehajtó függvénynek.
     * @param cmd
     * @throws IOException
     */
    public static void cmdProcess(String cmd) throws IOException {
        String[] proc = cmd.split(" ");
        switch (proc[0]) {
            case "move": move(cmd); break;
            case "virologist": createVirologist(cmd);  break;
            case "map": createMap(cmd);  break;
            case "materials": createMaterials(cmd);  break;
            case "statusEffect": createStatusEffect(cmd); break;
            case "agent":createAgent(cmd);  break;
            case "equipment":createEquipment(cmd);  break;
            case "placeEquipment":placeEquipment(cmd);  break;
            case "placeAgent":placeAgent(cmd);  break;
            case "placeMaterials":placeMaterials(cmd);  break;
        }
    }

    /**
     * A parancsban megadott virológust, a megadott mezőre lépteti.
     * @param cmd Szöveges parancs
     */
    private static void move(String cmd) {
        String[] proc = cmd.split(" ");
        Virologist v = (Virologist) objectIDsInv.get(proc[1]);
        Field f = (Field) objectIDsInv.get(proc[2]);
        v.Move(f);
    }

    /**
     * Létrehozza a parancsban megadott virológust.
     * @param cmd Szöveges parancs
     */
    private static  void createVirologist(String cmd) {
        String[] proc = cmd.split(" ");
        Virologist v = new Virologist(proc[1]);
        virologists.add((koporscho.Character)v);
        objectIDs.put(v, proc[1]);
        objectIDsInv.put(proc[1], v);
    }

    /**
     * Létrehozza a parancsban megadott pálya objektumot.
     * @param cmd
     */
    private static  void createMap(String cmd) {
        Scanner sc = fileScanner;
        String[] proc = cmd.split(" ");
        ArrayList<Field> temp = new ArrayList<>();
        for(int i = 1; i <= (proc.length-1); i+=2) {
            Field f = null;
            switch (proc[i]) {
                case "city" : f = new City();  break;
                case "lab" : f = new Lab(); break;
                case "shelter" : f =new Shelter(); break;
                case "storage" : f = new Storage(); break;
            };
            String id = proc[i+1];
            if(proc[i+1].endsWith(";"))
                id = proc[i+1].substring(0,proc[i+1].length()-1);
            objectIDs.put(f, id);
            objectIDsInv.put(id, f);
            temp.add(f);
            fields.add(f);
        }
        if(temp.size()>1) {
            for (Field f : temp) {
                String[] neighbors = sc.nextLine().split(" ");
                ArrayList<Field> neighborsArr = new ArrayList<>();
                for (String id : neighbors) {
                    neighborsArr.add((Field) objectIDsInv.get(id));
                }
                f.SetNeighbors(neighborsArr);
            }
        }
    }

    /**
     * Létrehozza a parancsban megadott Materials objektumot.
     * @param cmd Szöveges parancs
     */
    private static void createMaterials(String cmd) {
        String[] proc = cmd.split(" ");
        Materials m = new Materials(Integer.parseInt(proc[2]), Integer.parseInt(proc[3]));
        objectIDs.put(m, proc[1]);
        objectIDsInv.put(proc[1], m);
    }

    /**
     * Létrehozza a parancsban megadott StatusEffect objektumot.
     * @param cmd Szöveges parancs
     */
    private static void createStatusEffect(String cmd) {
        String[] proc = cmd.split(" ");
        StatusEffect s = new StatusEffect();
        for(int i = 2; i < proc.length; i++) {
            String[] eff = proc[i].split(":");
            switch (eff[0]) {
                case "immunity" : s.SetImmunity(Float.parseFloat(eff[1]));break;
                case "amnesia" : s.SetAmnesia(Boolean.parseBoolean(eff[1]));break;
                case "paralyzed" : s.SetParalyzed(Boolean.parseBoolean(eff[1]));break;
                case "chorea" : s.SetChorea(Boolean.parseBoolean(eff[1]));break;
                case "bagsize" : s.SetBagsize(Integer.parseInt(eff[1]));break;
                case "reflect" : s.SetReflect(Boolean.parseBoolean(eff[1]));break;
                case "duration" : s.SetDuration(Integer.parseInt(eff[1]));break;
                case "bear" : s.SetBear(Boolean.parseBoolean(eff[1]));break;
                case "dead" : s.SetDead(Boolean.parseBoolean(eff[1]));break;
            }
        }
        statusEffects.add(s);
        objectIDs.put(s, proc[1]);
        objectIDsInv.put(proc[1], s);
    }

    /**
     * Létrehozza a parancsban megadott Agent objektumot.
     * @param cmd Szöveges parancs
     */
    private static void createAgent(String cmd) {
        String[] proc = cmd.split(" ");
        Agent a = new Agent((StatusEffect) objectIDsInv.get(proc[2]),(Materials) objectIDsInv.get(proc[3]), proc[1]);
        agents.add(a);
        objectIDs.put(a, proc[1]);
        objectIDsInv.put(proc[1], a);
    }

    /**
     * Létrehozza a parancsban megadott Equipment objektumot.
     * @param cmd Szöveges parancs
     */
    private static void createEquipment(String cmd) {
        String[] proc = cmd.split(" ");
        Equipment e = new Equipment((StatusEffect) objectIDsInv.get(proc[2]), Integer.parseInt(proc[3]), proc[1]);
        equipment.add(e);
        objectIDs.put(e, proc[1]);
        objectIDsInv.put(proc[1], e);
    }

    /**
     * Elhelyez egy eszközt a parancsban megadott virológuson vagy óvóhelyen. -v/-f
     * @param cmd Szöveges parancs
     */
    private static void placeEquipment(String cmd) {
        String[] proc = cmd.split(" ");
        Equipment eq = (Equipment) objectIDsInv.get(proc[1]);
        switch (proc[2]) {
            case "-v" : {
                Virologist v = (Virologist) objectIDsInv.get(proc[3]);
                v.AddEquipment(eq);
                break;
            }
            case "-f" : {
                Shelter f = (Shelter) objectIDsInv.get(proc[3]);
                f.AddEquipment(eq);
                break;
            }
        }
    }

    /**
     * Elhelyez egy ágenst a parancsban megadott virológuson vagy laborban. -v/-f
     * @param cmd Szöveges parancs
     */
    private static void placeAgent(String cmd) {
        String[] proc = cmd.split(" ");
        Agent a = (Agent) objectIDsInv.get(proc[1]);
        switch (proc[2]) {
            case "-v" : {
                Virologist v = (Virologist) objectIDsInv.get(proc[3]);
                if (Objects.equals(proc[4], "agentInventory"))
                    v.AddAgent(a);
                else
                    v.LearnRecipe(a);
                break;
            }
            case "-f" : {
                Lab f = (Lab) objectIDsInv.get(proc[3]);
                if (proc.length > 4 && Objects.equals(proc[4], "-i")){
                    f.setInfected(a, true);
                }
                else
                    f.AddGeneticCode(a);
                break;
            }
        }
    }

    /**
     * Elhelyez egy anyagot a parancsban megadott virológuson vagy raktáron. -v/-f
     * @param cmd Szöveges parancs
     */
    private static void placeMaterials(String cmd) {
        String[] proc = cmd.split(" ");
        Materials m = (Materials) objectIDsInv.get(proc[1]);
        switch (proc[2]) {
            case "-v" : {
                Virologist v = (Virologist) objectIDsInv.get(proc[proc.length - 1]);
                if (Objects.equals(proc[3], "-m"))
                    v.SetMaxMaterials(m);
                else
                    v.SetMaterials(m);
                break;
            }
            case "-f" : {
                Storage f = (Storage) objectIDsInv.get(proc[proc.length - 1]);
                if (Objects.equals(proc[3], "-m"))
                    f.SetTotalSupply(m);
                else
                    f.SetSupply(m);
                break;
            }
        }
    }


    public static void LoadGame(String fname) {
        try {
            File myObj = new File(fname);
            fileScanner = new Scanner(myObj);
            while (fileScanner.hasNextLine()) {
                String data = fileScanner.nextLine();
                if(!Objects.equals(data, ""))
                    cmdProcess(data);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        GameMap map=new GameMap(fields);
        gc.SetGameMap(map);
        gc.SetChQueue(virologists);
        gc.SetEquipment(equipment);
        gc.SetAgents(agents);
        gc.SetObjectIDs(objectIDs);
        gc.SetObjectIDsInv(objectIDsInv);
    }

//    /**
//     * A parancs kimenetét logoló függvény, amely a standard oututra és fájlba dokumentál.
//     * @param cmd Szöveges parancs
//     * @throws IOException
//     */
//    private static void log(String cmd) throws IOException {
//        String[] proc = cmd.split(" ");
//        if(proc.length>1 && Objects.equals(proc[1], "-a")) {
//            for (Field f : fields) f.log();
//            for (Virologist v : virologists) v.log();
//            for (Agent a : agents) a.log();
//            for (Equipment e : equipment) e.log();
//            for (StatusEffect s : statusEffects) s.log();
//        }
//        else{
//            switch (proc[1]){
//                case "-f":((Field)objectIDsInv.get(proc[2])).log(); break;
//                case "-v":((Virologist)objectIDsInv.get(proc[2])).log(); break;
//                case "-a":((Agent)objectIDsInv.get(proc[2])).log(); break;
//                case "-e":((Equipment)objectIDsInv.get(proc[2])).log(); break;
//                case "-s":((StatusEffect)objectIDsInv.get(proc[2])).log(); break;
//            }
//
//        }
//    }
}
