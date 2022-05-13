package koporscho;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.ObjectInputStream;
import java.util.*;
//
//
//  Generated by StarUML(tm) Java Add-In
//
//  @ Project : Koporscho csapat Projlab 
//  @ File Name : GameMap.java
//  @ Date : 2022. 03. 25.
//  @ Authors : Szab� Egon, Bir� Ferenc, T�th B�lint, Ferge M�t�, Rahmi D�niel
//
//


/** A játékteret reprezentáló osztály */
public class GameMap {
	/** A pályán szereplő mezőket tárolja*/
	private ArrayList<Field> fields;

	/**
	 * Létrehozza a pályát a paraméterként kapott karakterekből, ágensekből és felszerelésekből.		//TODO
	 * @param c Karakterek tömbje
	 * @param a Ágensek tömbje
	 * @param e Felszerelések tömbje
	 */
	public GameMap(ArrayList<Field> f) {
		fields=f;
	}

	/**
	 * Létrehozza a pályát a paraméterként kapott karakterekből, ágensekből és felszerelésekből.
	 * @param c Karakterek tömbje
	 * @param a Ágensek tömbje
	 * @param e Felszerelések tömbje
	 */
	public void Generate(ArrayDeque<Character> c, ArrayList<Agent> a, ArrayList<Equipment> e) {
		try {
			HashMap<String, Field> ids = new HashMap<>();
			File myObj = new File("map.txt");
			Scanner myReader = new Scanner(myObj);
			boolean done = false;
			ArrayList<Field> fields = new ArrayList<>();
			ArrayList<Lab> labs = new ArrayList<>();
			ArrayList<Storage> storages = new ArrayList<>();
			ArrayList<Shelter> shelters = new ArrayList<>();
			ArrayList<City> cities = new ArrayList<>();
			while (myReader.hasNextLine()) {
				String read = myReader.nextLine();
				String[] data = read.split(" ");
				if (!done) {
					for (int i = 1; i < (data.length - 1) / 2; i += 2) {
						Field f = null;
						switch (data[i]) {
							case "city":
								f = new City();
								cities.add((City) f);
								break;
							case "lab":
								f = new Lab();
								labs.add((Lab) f);
								break;
							case "shelter":
								f = new Shelter();
								shelters.add((Shelter) f);
								break;
							case "storage":
								f = new Storage();
								storages.add((Storage) f);
								break;
						}
						fields.add(f);
						String id = data[i + 1].substring(0, data[i + 1].length() - 1);
						ids.put(id, f);
					}
					done = true;
				} else {
					for (Field f : fields) {
						ArrayList<Field> neighborsArr = new ArrayList<>();
						for (String id : data) {
							neighborsArr.add(ids.get(id));
						}
						f.SetNeighbors(neighborsArr);
					}
				}
			}
			myReader.close();
			for (Character ch : c)
			{
				for(City city: cities){
					if(city.GetCharacters().isEmpty()) {
						city.Accept(ch);
						break;
					}
				}
			}
			for(Agent ag: a){
				for(Lab lab: labs){
					if(lab.GetRecipe()==null){
						lab.AddGeneticCode(ag);
						break;
					}
				}
			}
			for(Equipment eq: e){
				for(Shelter sh: shelters){
					if(sh.GetEquipment()==null) {
						sh.AddEquipment(eq);
						break;
					}
				}
			}
			for(Storage st: storages){
				st.SetTotalSupply(new Materials(10,10));
				st.SetSupply(new Materials(10,10));
			}
		} catch (FileNotFoundException exc) {
			exc.printStackTrace();
		}

	}

	public ArrayList<Field> GetFields() {
		return fields;
	}
}
