package koporscho;
import proto.Prototype;
import java.util.*;
//
//
//  Generated by StarUML(tm) Java Add-In
//
//  @ Project : Koporscho csapat Projlab 
//  @ File Name : Character.java
//  @ Date : 2022. 03. 25.
//  @ Authors : Szab� Egon, Bir� Ferenc, T�th B�lint, Ferge M�t�, Rahmi D�niel
//
//


/**
 * A játékban szereplő karakterek megvalósítására szolgáló absztrakt osztály
 */
public abstract class Character {
	/** A karakteren szereplő aktív effektek tárolásáért felelős.*/
	protected ArrayList<StatusEffect> activeEffects = new ArrayList<>();

	/** A karakter jelenlegi mezőjét tárolja, azt amelyen áll.*/
	protected Field currentField;

	/**
	 * Mozgatja a karaktert a paraméterként megadott mezőre.
	 * @param field Célmező, amelyre a charactert mozgatjuk.
	 */
	public void Move(Field field) {
		if(currentField!= null)
			currentField.Remove(this);
		if(field!=null)
			field.Accept(this);
		currentField = field;
	}

	/**
	 * A karakterekre kerülő ágenseket kezeli. Paraméterei az ágenst felkenő karakter, és maga az ágens.
	 * @param source Az ágenst felkenő character.
	 * @param agent A felkent ágens.
	 */
	public void HandleAgent(Character source, Agent agent, boolean reflected) {
		boolean reflect = false;
		if (!reflected)
			for (StatusEffect e : activeEffects) {
				if (e.GetReflect()) {
					reflect = true;
					break;
				}
			}

		if (reflect) {
			for(Equipment e: ((Virologist)this).GetEquipment()){
				if(e.GetEffect().GetReflect()){
					e.DecreaseDurability();
				}
			}
			source.HandleAgent(this, agent, true);
		}
		else {
			float infectionChance = 1;
			for (StatusEffect e: activeEffects) {
				infectionChance *= 1 - e.GetImmunity();
			}
			float immunity = 1-infectionChance;
			float diceRoll;

			if(!Prototype.random) diceRoll = Prototype.diceRoll;
			else diceRoll = (float) Math.random();

			if (diceRoll > immunity) {
				StatusEffect e = new StatusEffect(agent.GetEffect());
				activeEffects.add(e);
				// Bear Agent
				if(e.GetBear()){
					((Virologist)this).GetRecipes().clear();
					((Virologist)this).GetAgentInventory().clear();
					for (Equipment eq: ((Virologist)this).GetEquipment()) {
						((Virologist)this).RemoveEquipment(eq);
					}
				}
			}
		}
	}

	/**
	 * A mezőn végezhető művelet meghívása.
	 */
	abstract public void Interact();

	/**
	 *  Hozzáadja a paraméterként kapott effektet a karakter aktív effektjei közé.
	 * @param e Az újonnan felvett effekt
	 */
	public void AddEffect(StatusEffect e) {
		activeEffects.add(e);
	}

	/**
	 * Eltávolítja a paraméterként kapott effektet a karakterről.
	 * @param e
	 */
	public void RemoveEffect(StatusEffect e) {
		activeEffects.remove(e);
	}

	/**
	 * Az idő múlását szimuláló függvény.
	 */
	public void Tick() {
		Iterator<StatusEffect> i = activeEffects.iterator();
		while(i.hasNext()) {
			StatusEffect e = i.next();
			if (e.GetParalyzed()) {
				((Virologist)this).SetApCurrent(0);
			}
			if (e.reduceDuration() == 0) {
				i.remove();
			}
		}
	}

	/**
	 * Visszaadja azt a mezőt, amelyen a karakter éppen áll.
	 * @return A mező, amelyen a karakter éppen áll
	 */
	public Field GetField() {
		return currentField;
	}

	/**
	 * Beállítja a paraméterként kapott mezőt karakter aktuális tartózkodási mezőjének.
	 * @param field Új mező, amelyen a karkater éppen áll
	 */
	public void SetField(Field field) {
		currentField = field;
	}

	/**
	* Visszatér a karakter által ismert ágensek számával. A győztes kihirdetésénél használt függvény.
	 * @return karakter által ismert ágensek száma
	 */
	public int GetRecipeCount() {
		return -1;
	}
}
