package net.bryanhaley.butterblitz;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

/* Everything here is temporary, just wanted to get a workable demo to show the group. */

public class Player extends GameObject
{
	private int health;
	//GUI stuff. Maybe put this in Level later?
	private TextureRegion healthText, healthOrb;

	public Player(World world)
	{
		this(world, new Vector2(500,500));
	}
	
	public Player(World world, Vector2 position)
	{
		super(world, "example_player.png", new Rectangle(position.x,position.y,43,64));
		
		healthText = new TextureRegion(new Texture("example_health_text.png"));
		healthOrb = new TextureRegion(new Texture("example_health.png"));
		health = 3;
		
		//Player is updated and rendered at a specific time, so we don't want it in
		//the same list as every other game object
		Level.gameObjs.remove(this);
	}

	public void update()
	{
		super.update(); //check for collisions
		
		//keep the body vertical
		body.setAngularVelocity(0);
		body.setTransform(body.getPosition(), 0);

		/* Temporary movement stuff, change later */
		if (isOnGround)
		{
			body.setLinearDamping(2);
		}
		else
		{
			body.setLinearDamping(0);
		}

		if (Gdx.input.isKeyPressed(Keys.RIGHT) || Gdx.input.isKeyPressed(Keys.D))
		{
			body.setLinearVelocity(new Vector2(15, body.getLinearVelocity().y));
		}
		else if (Gdx.input.isKeyPressed(Keys.LEFT) || Gdx.input.isKeyPressed(Keys.A))
		{
			body.setLinearVelocity(new Vector2(-15, body.getLinearVelocity().y));
		}

		if (Gdx.input.isKeyJustPressed(Keys.SPACE) && findIsOnGround())
		{
			body.setLinearVelocity(new Vector2(body.getLinearVelocity().x, 17));
		}
	}

	public void render(SpriteBatch batch)
	{
		//Draw the image
		batch.draw(tex, body.getPosition().x * Level.METERS_TO_PIXELS - (width / 2) + 10, body.getPosition().y
				* Level.METERS_TO_PIXELS - (height / 2), width / 2, height / 2, width, height, scaleX, scaleY, 0);
		
		//Draw GUI stuff. Move to Level later?
		batch.draw(healthText, 3, 704);
		
		switch (health)
		{
			case 3: batch.draw(healthOrb, 97, 704);
			case 2: batch.draw(healthOrb, 81, 704);
			case 1: batch.draw(healthOrb, 65, 704);
		}
	}
	
	protected void checkCollision(GameObject collision)
	{
		//If we hit an example enemy, take 1 damage
		if (collision instanceof Enemy)
		{
			damage();
		}
	}
	
	public void damage()
	{
		health--;
	}
	
	public void damage(int damage)
	{
		health -= damage;
	}
	
	public void heal()
	{
		health++;
	}
	
	public void heal(int heal)
	{
		health += heal;
	}
}
