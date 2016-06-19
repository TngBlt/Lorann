package model;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Observable;

import contract.*;
import model.LorannWorld.Element;
import model.LorannWorld.Map;
import model.LorannWorld.MobileElement.Hero;
import model.LorannWorld.MobileElement.MobileElement;
import model.LorannWorld.MobileElement.Monster;
import model.LorannWorld.MobileElement.Spell;
import model.LorannWorld.MotionlessElement.*;
import model.dataBase.DAOGetMap;

/**
 * The Class Model.
 *
 * @author Tanguy Blochet
 */
public class Model extends Observable implements IModel {

	/**
	 * The map.
	 */
	private Map map;
	/**
	 * The connection to the database and the query to execute
	 */
	private DAOGetMap daoGetMap;
	/**
	 * The message to print
	 */
	private String message;

	/**
	 * Instantiates a new model.
	 */
	public Model() {
		this.map =null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see contract.IModel#getMap()
	 */
	public Map getMap() {
		return this.map;
	}

	/**
	 * Sets the map.
	 *
	 * @param map the new map
	 */
	private void setMap(final Map map) {
		this.map = map;
		this.setChanged();
		this.notifyObservers();
	}

	/**
	 * Load the elements of the map from the database
	 * @param ID
     */
	public void loadMap(final int ID) {


		map=new Map(20,12);
		map.setID(ID);
		daoGetMap=new DAOGetMap();
		ResultSet resultSet=daoGetMap.query("{call getElementMapByID(?)}",ID);
		int monsterPlus=1;
		try {
			while (resultSet.next())
			{

				String name=resultSet.getString("name");
				if(name.equals("boneH"))
				{
					MotionlessElement e = new BoneH(name);
					map.addElementToMap(e,resultSet.getInt("PosX"),resultSet.getInt("PosY"));
				}
				else if(name.equals("boneC"))
				{
					MotionlessElement e=new BoneC(name);
					map.addElementToMap(e,resultSet.getInt("PosX"),resultSet.getInt("PosY"));
				}
				else if(name.equals("boneV"))
				{
					MotionlessElement e=new BoneV(name);
					map.addElementToMap(e,resultSet.getInt("PosX"),resultSet.getInt("PosY"));
				}
				else if(name.equals("purse"))
				{
					MotionlessElement e=new Purse(name);
					map.addElementToMap(e,resultSet.getInt("PosX"),resultSet.getInt("PosY"));
				}
				else if(name.equals("key"))
				{
					MotionlessElement e=new Key(name);
					map.addElementToMap(e,resultSet.getInt("PosX"),resultSet.getInt("PosY"));
				}
				else if(name.equals("lorann_b"))
				{
					Hero e=new Hero(name);
					map.setHero(e);
					map.setHeroPosition(resultSet.getInt("PosX"),resultSet.getInt("PosY"));
					map.getHero().setStateElement(StateElement.WEAK);
				}
				else if(name.equals("monster"))
				{
					switch(monsterPlus)
					{
						case(1):
						{
							MobileElement e=new Monster("monster_1");
							map.getMobiles().add(e);
							e.setX(resultSet.getInt(("PosX")));
							e.setY(resultSet.getInt("PosY"));
							monsterPlus++;
							break;
						}
						case(2):
						{
							MobileElement e=new Monster(name+"_2");
							map.getMobiles().add(e);
							e.setX(resultSet.getInt(("PosX")));
							e.setY(resultSet.getInt("PosY"));
							monsterPlus++;
							break;
						}
						case(3):
						{
							MobileElement e=new Monster(name+"_3");
							map.getMobiles().add(e);
							e.setX(resultSet.getInt(("PosX")));
							e.setY(resultSet.getInt("PosY"));
							monsterPlus++;
							break;
						}
						case(4):
						{
							MobileElement e=new Monster(name+"_4");
							e.setX(resultSet.getInt(("PosX")));
							e.setY(resultSet.getInt("PosY"));
							map.getMobiles().add(e);
							monsterPlus++;
							break;
						}

					}

				}
				else if (name.equals("door"))
				{
					MotionlessElement e=new Door(name);
					map.addElementToMap(e,resultSet.getInt("PosX"),resultSet.getInt("PosY"));
				}

			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see contract.IModel#getObservable()
	 */
	public Observable getObservable() {
		return this;
	}

	/**
	 * notify the changes to the view
	 */
	public synchronized void  flush(){
		if(map.getScore()>=100 && map.getHero()!=null)
			map.getHero().setStateElement(StateElement.STRONG);


		setChanged();
		notifyObservers();
	}

	/**
	 *test the type of element
	 * @param element
	 * @return int representing an element
     */
	public int testType(IElement element)
	{
		if(element instanceof Door)
			return 1;

		if(element instanceof Purse)
			return 2;

		if(element instanceof Monster)
			return 3;

		if(element instanceof Key)
			return 4;

		return 0;
	}

	/**
	 * get the message
	 * @return message
     */
	public String getMessage() {
		return message;
	}

	/**
	 * sets the message
	 * @param message
     */
	public void setMessage(String message) {
		this.message = message;
		flush();
	}

	/**
	 * calls the instantiation of  a spell
	 * @param path
	 * 			path of the spell image
	 * @param direction
	 * 			direction that the spell will go
	 * @throws IOException
     */
	public void createSpell(String path, ControllerOrder direction) throws IOException {
		MobileElement spell=new Spell(path,direction);

		map.setSpell(spell);
		switch (map.getHero().getDirection())
		{
			case UP:
				map.getSpell().setY(map.getHero().getY()-1);
				map.getSpell().setX(map.getHero().getX());
				break;

			case DOWN:
				map.getSpell().setY(map.getHero().getY()+1);
				map.getSpell().setX(map.getHero().getX());
				break;

			case RIGHT:
				map.getSpell().setY(map.getHero().getY());
				map.getSpell().setX(map.getHero().getX()+1);
				break;

			case LEFT:
				map.getSpell().setY(map.getHero().getY());
				map.getSpell().setX(map.getHero().getX()-1);
				break;
		}

		map.getSpell().setDirection(direction);
		System.out.println("Spell Created at "+map.getHero().getX()+"-"+map.getHero().getY());
	}
}