package indigo.Skill;

import indigo.Entity.Player;
import indigo.GameState.PlayState;
import indigo.Manager.InputManager;
import indigo.Phase.Phase;

public abstract class Skill
{
	protected PlayState playState;
	protected InputManager input;
	protected Phase phase;
	
	protected Player player;
	protected int id; // Initialize in each skill constructor
	protected int position;
	
	protected int castTime;

	public static final int EMPTY = 0;
	public static final int GEYSER = 1;
	public static final int PULSE = 2;
	public static final int CHANNEL = 3;
	public static final int WHIRLWIND = 4;
	public static final int CHAINS = 5;
	public static final int ARMOR = 6;
	
	
	public Skill(Phase phase, int position)
	{
		playState = phase.getPlayState();
		input = phase.getInput();
		this.phase = phase;
		player = phase.getPlayer();
		
		this.position = position;
		castTime = -1;
	}
	
	public void update()
	{
		castTime++;
	}
	
	public abstract boolean canCast();
	
	public void endCast()
	{
		castTime = -1;
		phase.endCast(position); // Resets skill icon
	}
	
	public int id()
	{
		return id;
	}
}