package indigo.GameState;

import indigo.Manager.ContentManager;
import indigo.Manager.GameStateManager;
import indigo.Manager.Manager;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

import org.json.simple.JSONObject;

/**
 * The state that allows the player to select a stage to play. Accessed when they player selects "play" from the main
 * menu.
 */
public class StageSelectState extends GameState
{
	private String[] levels;
	private int levelIndex;

	/**
	 * Sets up the stage selection state.
	 * 
	 * @param gsm The game state manager.
	 */
	public StageSelectState(GameStateManager gsm)
	{
		super(gsm);

		JSONObject index = ContentManager.load("/index.json");
		levels = (String[])index.values().toArray(new String[0]);
		levelIndex = 0;
	}

	@Override
	public void update()
	{
		handleInput();
	}

	@Override
	public void render(Graphics2D g)
	{
		g.drawImage(ContentManager.getImage(ContentManager.MENU_BACKGROUND), 0, 0, null);
		if(Manager.input.mouseInRect(100, 874, 358, 106))
		{
			g.drawImage(ContentManager.getImage(Manager.input.mouseLeftDown()? ContentManager.GLOW_RECTANGLE_CLICK
					: ContentManager.GLOW_RECTANGLE_HOVER), 70, 844, null);
		}
		g.drawImage(ContentManager.getImage(ContentManager.BUTTON_BACK), 100, 874, null);

		// Draw selection box
		g.drawImage(ContentManager.getImage(ContentManager.SELECTION_BOX), 600, 100, null);
		if(levelIndex != 0)
		{
			g.drawImage(ContentManager.getImage(ContentManager.ARROW_LEFT_ACTIVE), 600, 100, null);
		}
		else
		{
			g.drawImage(ContentManager.getImage(ContentManager.ARROW_LEFT_INACTIVE), 600, 100, null);
		}
		if(levelIndex != levels.length - 1)
		{
			g.drawImage(ContentManager.getImage(ContentManager.ARROW_RIGHT_ACTIVE), 840, 100, null);
		}
		else
		{
			g.drawImage(ContentManager.getImage(ContentManager.ARROW_RIGHT_INACTIVE), 840, 100, null);
		}

		// Draw tool type text
		g.setColor(Color.BLACK);
		g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
		FontMetrics fontMetrics = g.getFontMetrics();
		String text = levels[levelIndex];
		g.drawString(text, 750 - fontMetrics.stringWidth(text) / 2, 138 + fontMetrics.getHeight() / 4);
	}

	/**
	 * Handles the mouse interactions with the stage selections.
	 */
	public void handleInput()
	{
		if(Manager.input.mouseLeftRelease())
		{
			if(Manager.input.mouseInRect(600, 100, 60, 75) && levelIndex != 0)
			{
				levelIndex--;
			}
			else if(Manager.input.mouseInRect(840, 100, 60, 75) && levelIndex != levels.length - 1)
			{
				levelIndex++;
			}
			else if(Manager.input.mouseInRect(660, 100, 180, 75))
			{
				String levelName = levels[levelIndex].replace(" ", "_").toLowerCase();
				data.setStage(ContentManager.load("/levels/" + levelName + ".json"));
				gsm.setState(GameStateManager.PLAY);
			}
			else if(Manager.input.mouseInRect(100, 874, 360, 106))
			{
				gsm.setState(GameStateManager.MENU);
			}
		}
	}
}