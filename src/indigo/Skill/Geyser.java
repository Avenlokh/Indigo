package indigo.Skill;

import indigo.Phase.Phase;
import indigo.Projectile.GeyserParticle;
import indigo.Projectile.GeyserBase;

public class Geyser extends Skill
{
	private GeyserBase geyser;
	
	public Geyser(Phase phase, int position)
	{
		super(phase, position);
		id = Skill.GEYSER;
	}
	
	public void update()
	{
		super.update();
		
		if(player.getMana() >= 2 && (input.mouseLeftDown() || castTime < 10))
		{
			if(castTime == 0)
			{
				player.canAttack(false);
				geyser = new GeyserBase(player, playState.getMouseX(), playState.getMapY() - GeyserBase.HEIGHT / 2, 0, 0, 0);
				playState.getProjectiles().add(geyser);
			}
			
			playState.getProjectiles().add(0, new GeyserParticle(player, geyser.getX(), playState.getMapY() - GeyserParticle.HEIGHT / 2 + GeyserParticle.SPEED, 0, -GeyserParticle.SPEED, GeyserParticle.DAMAGE));
			player.setMana(player.getMana() - 0);
		}
		else
		{
			playState.getProjectiles().remove(geyser);
			geyser = null;
			endCast();
		}
	}
	
	public boolean canCast()
	{
		return (player.getMana() >= 20 && player.canAttack());
	}
	
	public void endCast()
	{
		super.endCast();
		
		player.canAttack(true);
	}
}
