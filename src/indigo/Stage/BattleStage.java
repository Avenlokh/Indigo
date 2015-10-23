package indigo.Stage;

import java.io.File;
import java.lang.reflect.Constructor;

import javax.imageio.ImageIO;

import indigo.Entity.Entity;
import indigo.Entity.Player;
import indigo.GameState.PlayState;
import indigo.Interactive.Interactive;
import indigo.Landscape.Land;
import indigo.Landscape.Platform;
import indigo.Landscape.SkyBounds;
import indigo.Landscape.Wall;
import indigo.Manager.ContentManager;
import indigo.Projectile.Projectile;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class BattleStage extends Stage
{
	private Entity lastEnemy;

	private int enemiesToDefeat;
	private int enemiesDefeated = 0;

	private Respawnable[] respawnables;
	private JSONObject[] respawnInfo;
	private int[] respawnTimers;

	public BattleStage(PlayState playState, JSONObject json)
	{
		super(playState);

		player = new Player(this, (int)(long)json.get("startingX"), (int)(long)json.get("startingY"),
				Player.BASE_HEALTH, Player.BASE_MANA, Player.BASE_STAMINA);
		entities.add(0, player);

		background = ContentManager.getImage(ContentManager.BACKGROUND);
		try
		{
			String fileName = ((String)json.get("name")).replace(" ", "_").toLowerCase();
			foreground = ImageIO.read(new File(new File("").getAbsolutePath() + "/resources/images/stages/" + fileName
					+ ".png"));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		setOffsets((int)(long)json.get("mapX"), (int)(long)json.get("mapY"));

		enemiesToDefeat = (int)(long)json.get("enemiesToDefeat");

		// Bounding walls
		walls.add(new Wall(this, 0, SKY_LIMIT, 0, mapY));
		walls.add(new Wall(this, mapX, SKY_LIMIT, mapX, mapY));
		walls.add(new SkyBounds(this, 0, SKY_LIMIT, mapX, SKY_LIMIT));

		JSONArray array = (JSONArray)json.get("landscape");
		if(array == null)
		{
			array = new JSONArray();
		}
		for(int count = 0; count < array.size(); count++)
		{
			JSONObject object = (JSONObject)array.get(count);
			createLand(object);
		}

		array = (JSONArray)json.get("spawns");
		if(array == null)
		{
			array = new JSONArray();
		}
		respawnables = new Respawnable[array.size()];
		respawnInfo = new JSONObject[array.size()];
		respawnTimers = new int[array.size()];
		for(int count = 0; count < array.size(); count++)
		{
			JSONObject object = (JSONObject)array.get(count);
			respawnInfo[count] = object;
			respawnables[count] = spawnObject(object);
		}

		// SoundManager.play(ContentManager.BACKGROUND_1);
	}

	public void update()
	{
		super.update();

		if(lastEnemy != null && !entities.contains(lastEnemy))
		{
			playState.endGame(true);
		}

		// Check for dead respawnables and respawn them when time is up
		for(int count = 0; count < respawnables.length; count++)
		{
			// If object dies, set the timer
			if(respawnables[count] != null && respawnables[count].isDead())
			{
				respawnables[count] = null;
				respawnTimers[count] = (int)(long)respawnInfo[count].get("respawnTime");
			}
			// If the timer hits zero, spawn the object
			if(respawnables[count] == null && respawnTimers[count] == 0)
			{
				respawnables[count] = spawnObject(respawnInfo[count]);
			}
			// Decrement the timer
			if(respawnTimers[count] > 0)
			{
				respawnTimers[count]--;
			}
		}
	}

	public void trackDeath(String killer, Entity killed)
	{
		if(killed.equals(player))
		{
			data.setKiller(killer);
		}
		else if(killed.isMarked())
		{
			enemiesDefeated++;
			// TODO Gain experienced - Add experience variable to Entity class
		}
		if(enemiesDefeated >= enemiesToDefeat)
		{
			lastEnemy = killed;
		}
	}

	public int getEnemiesDefeated()
	{
		return enemiesDefeated;
	}

	public int getEnemiesToDefeat()
	{
		return enemiesToDefeat;
	}

	public void createLand(JSONObject info)
	{
		Land object = null;
		String type = ((String)info.get("type")).replace(" ", "");
		double x1 = Integer.parseInt(info.get("x1") + "");
		double y1 = Integer.parseInt(info.get("y1") + "");
		double x2 = Integer.parseInt(info.get("x2") + "");
		double y2 = Integer.parseInt(info.get("y2") + "");

		try
		{
			String className = "indigo.Landscape." + type;
			Class<?> varClass = Class.forName(className);
			Constructor<?> varConstructor = varClass.getConstructor(Stage.class, double.class, double.class,
					double.class, double.class);
			object = (Land)varConstructor.newInstance(new Object[] {this, x1, y1, x2, y2});
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		if(object instanceof Platform)
		{
			platforms.add((Platform)object);
		}
		else
		{
			walls.add((Wall)object);
		}
	}

	public Respawnable spawnObject(JSONObject info)
	{
		Respawnable object = null;
		String category = (String)info.get("category");
		String type = ((String)info.get("type")).replace(" ", "");
		double x = Integer.parseInt(info.get("x") + "");
		double y = Integer.parseInt(info.get("y") + "");

		try
		{
			String className = "indigo." + info.get("category") + "." + type;
			Class<?> varClass = Class.forName(className);
			Constructor<?> varConstructor = varClass.getConstructor(Stage.class, double.class, double.class);
			object = (Respawnable)varConstructor.newInstance(new Object[] {this, x, y});
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		switch(category)
		{
			case "Entity":
				entities.add((Entity)object);
				break;
			case "Projectile":
				projectiles.add((Projectile)object);
				break;
			case "Interactive":
				interactives.add((Interactive)object);
				break;
		}

		return object;
	}
}