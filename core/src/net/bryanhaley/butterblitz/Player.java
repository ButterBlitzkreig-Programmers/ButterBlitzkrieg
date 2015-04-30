package net.bryanhaley.butterblitz;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

/* Everything here is temporary, just wanted to get a workable demo to show the group. */

public class Player extends GameObject
{
	private int health;
	private Enemy lastEnemyHit;

	public Player(World world)
	{
		this(world, new Vector2(500, 500));
	}

	public Player(World world, Vector2 position)
	{
		// super(world, "example_player.png", new Rectangle(position.x,position.y,43,64));

		tex = new TextureRegion(new Texture(Gdx.files.internal("example_player.png")));
		this.width = 43;
		this.height = 64;

		BodyDef def = new BodyDef();
		def.type = BodyType.DynamicBody;
		def.position.set((position.x+(width/2))*Level.PIXELS_TO_METERS, (position.y+(height/2))*Level.PIXELS_TO_METERS);
		Body body = world.createBody(def);
		
		CircleShape circle = new CircleShape();
		circle.setRadius((width/2)*Level.PIXELS_TO_METERS);
		circle.setPosition(new Vector2(0,-circle.getRadius()/2));
		body.createFixture(circle, 1);
		
		PolygonShape shape = new PolygonShape();
		shape.setAsBox((width / 2) * Level.PIXELS_TO_METERS, ((height / 2) * Level.PIXELS_TO_METERS)-circle.getRadius()/2, new Vector2(0, circle.getRadius()/2), 0);
		body.createFixture(shape, 1);
		
		shape.dispose();
		circle.dispose();

		body.setBullet(true);
		body.setUserData(this);
		body.setFixedRotation(true);
		
		health = 3;
		
		this.body = body;
		this.world = world;

		originX = 0;
		originY = 0;
		scaleX = 1;
		scaleY = 1;
		rotation = 0;

		collidingWith = new ArrayList<GameObject>();
	}

	public void update()
	{
		super.update(); // check for collisions

		// keep the body vertical
		body.setAngularVelocity(0);
		body.setTransform(body.getPosition(), 0);
		findIsOnGround();

		for (Fixture fixture : body.getFixtureList())
		{ fixture.setFriction(0.2f); }
		
		float startingVelocity = 10;
		float moveForce = 50;
		float maxVelocity = 50;
		float jumpVelocity = 18;
		
		if (Gdx.input.isKeyPressed(Keys.CONTROL_LEFT))
		{
			startingVelocity *= 0.5f;
			moveForce *= 0.5f;
			maxVelocity *= 0.5f;
			jumpVelocity *= 1.1f;
		}
		
		else if (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT))
		{
			startingVelocity *= 1.5f;
			moveForce *= 1.5f;
			maxVelocity *= 1.5f;
			jumpVelocity *= 1.3f;
		}
		
		if (isOnGround)
		{
			if (Gdx.input.isKeyPressed(Keys.D))
			{
				if (body.getLinearVelocity().x <= startingVelocity && body.getLinearVelocity().x >= -3)
				{
					body.setLinearVelocity(startingVelocity, body.getLinearVelocity().y);
				}
				
				body.applyLinearImpulse(new Vector2(moveForce,0), body.getPosition(), true);
			}
			else if (Gdx.input.isKeyPressed(Keys.A))
			{
				if (body.getLinearVelocity().x >= -startingVelocity && body.getLinearVelocity().x <= 3)
				{
					body.setLinearVelocity(-startingVelocity, body.getLinearVelocity().y);
				}
				
				body.applyLinearImpulse(new Vector2(-moveForce,0), body.getPosition(), true);
			}
			
			if (Gdx.input.isKeyPressed(Keys.SPACE))
			{
				body.setLinearVelocity(body.getLinearVelocity().x, body.getLinearVelocity().y+jumpVelocity);
			}
		}
		
		else
		{
			if (Gdx.input.isKeyPressed(Keys.D) && body.getLinearVelocity().x <= 5)
			{
				body.applyLinearImpulse(new Vector2(50,0), body.getPosition(), true);
			}
			else if (Gdx.input.isKeyPressed(Keys.A) && body.getLinearVelocity().x >= -5)
			{
				body.applyLinearImpulse(new Vector2(-50,0), body.getPosition(), true);
			}
		}
		
		if (!Gdx.input.isKeyPressed(Keys.A) && !Gdx.input.isKeyPressed(Keys.D) && isOnGround)
		{
			body.setLinearVelocity(body.getLinearVelocity().x*0.95f, body.getLinearVelocity().y);
		}
		
		if (Math.abs(body.getLinearVelocity().x) > maxVelocity)
		{
			body.setLinearVelocity(Math.signum(body.getLinearVelocity().x)*maxVelocity, body.getLinearVelocity().y);
		}
		
		/*if (body.getLinearVelocity().x >= -1 && body.getLinearVelocity().x <= 1 &&
				!Gdx.input.isKeyPressed(Keys.A) && !Gdx.input.isKeyPressed(Keys.D) && !isOnGround)
		{
			for (Fixture fixture : body.getFixtureList())
			{ fixture.setFriction(100f); }
		}*/
		
		Gdx.app.log("Player is on ground?",""+isOnGround);
	}
	
	public boolean findIsOnGround()
	{
		Array<Contact> contactList = world.getContactList();
		
		for (Contact contact : contactList)
		{
			if (contact.isTouching() &&
					((contact.getFixtureA() == body.getFixtureList().get(0) && contact.getFixtureB() != body.getFixtureList().get(1)) ||
					(contact.getFixtureB() == body.getFixtureList().get(0) && contact.getFixtureA() != body.getFixtureList().get(1))))
			{
				isOnGround = true;
				return true;
			}
		}
		
		isOnGround = false;
		return false;
	}

	public void render(SpriteBatch batch)
	{
		// Draw the image
		batch.draw(tex, body.getPosition().x * Level.METERS_TO_PIXELS - (width / 2), body.getPosition().y
				* Level.METERS_TO_PIXELS - (height / 2), width / 2, height / 2, tex.getRegionWidth(), tex.getRegionHeight(), scaleX, scaleY, 0);
	}

	protected void checkCollision(GameObject collision)
	{
		Gdx.app.log("check collision", "called");
		// If we hit an example enemy, take 1 damage
		if (collision instanceof Enemy && !collision.equals(lastEnemyHit))
		{
			lastEnemyHit = (Enemy) collision;
			damage();
		}
		else if (collision instanceof Killzone)
		{
			kill();
		}
	}
	
	public int getHealth()
	{
		return health;
	}

	public void damage()
	{
		health--;
	}

	public void damage(int damage)
	{
		health -= damage;
	}
	
	public void kill()
	{
		health = 0;
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