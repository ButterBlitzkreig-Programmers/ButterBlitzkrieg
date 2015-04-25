package net.bryanhaley.butterblitz;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

/* This is an example enemy. Extend the class and override its behavior */
public class Enemy extends GameObject
{
	//Enenmy needs to be able to check player's position
	protected Player player;
	
	public Enemy(World world, Player player)
	{
		this(world, player, "example_enemy.png", new Rectangle(832,400,32,32));
	}
	
	public Enemy(World world, Player player, String img, Rectangle collision)
	{
		super(world, img, collision);
		this.player = player;
	}
	
	public void update()
	{
		//update in GameObject checks for collisions
		super.update();
		
		//Behavior defined for this object
		doBehavior();
	}
	
	public void doBehavior()
	{
		//This example enemy just jumps when the player gets close enough.
		float distanceFromPlayer =
				(float) Math.abs(player.getPositionPixels().x - this.getPositionPixels().x);
		
		if (distanceFromPlayer < 140 && findIsOnGround())
		{
			body.setLinearVelocity(new Vector2(body.getLinearVelocity().x, 10));
		}
	}
	
	//NOTE: Only define this object's behavior in a collision! Both objects
	//		will have this method called, and they should define their own
	//		behavior in response.
	public void checkCollision(GameObject collision)
	{
		//The enemy only reacts to a collision with a player
		if (collision instanceof Player)
		{
			this.destroy();
		}
	}
}
