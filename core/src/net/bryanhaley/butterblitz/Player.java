package net.bryanhaley.butterblitz;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

/* Everything here is temporary, just wanted to get a workable demo to show the group. */

public class Player extends GameObject
{
	private World world;
	private boolean isOnGround;

	public Player(World world)
	{
		super(world, "example_player.png", new Rectangle(500, 500, 43, 64));
		this.world = world;
	}

	public void create(World world, String img, Rectangle collisionBox)
	{
		tex = new TextureRegion(new Texture(Gdx.files.internal(img)));
		this.width = tex.getRegionWidth();
		this.height = tex.getRegionHeight();

		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.position.set(collisionBox.x * Level.PIXELS_TO_METERS, collisionBox.y * Level.PIXELS_TO_METERS);

		Body body = world.createBody(bodyDef);

		PolygonShape shape = new PolygonShape();
		shape.setAsBox(collisionBox.width / 2 * Level.PIXELS_TO_METERS, collisionBox.height / 2
				* Level.PIXELS_TO_METERS);

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.friction = 0f;
		fixtureDef.density = 1f;

		body.createFixture(fixtureDef);

		shape.dispose();

		this.body = body;

		originX = 0;
		originY = 0;
		scaleX = 1;
		scaleY = 1;
		rotation = 0;
	}

	public void update()
	{
		findIsOnGround();
		body.setAngularVelocity(0);
		body.setTransform(body.getPosition(), 0);

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

		if (Gdx.input.isKeyJustPressed(Keys.SPACE) && isOnGround)
		{
			body.setLinearVelocity(new Vector2(0, 17));
		}
	}

	private void findIsOnGround()
	{
		isOnGround = false;

		RayCastCallback callback = new RayCastCallback()
		{
			@Override
			public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction)
			{
				Gdx.app.log("Body Y pos", "" + body.getPosition().y * Level.METERS_TO_PIXELS);
				Gdx.app.log("Collision Point", point.x * Level.METERS_TO_PIXELS + "x" + point.y
						* Level.METERS_TO_PIXELS);
				if (body.getPosition().y - point.y < 40 * Level.PIXELS_TO_METERS)
				{
					isOnGround = true;
					Gdx.app.log("Is on ground", "true");
				}
				return 0;
			}
		};

		Vector2 point1 = new Vector2(body.getPosition().x, body.getPosition().y
				+ ((height / 2) * Level.PIXELS_TO_METERS));
		Vector2 point2 = new Vector2(point1.x, -200);

		world.rayCast(callback, point1, point2);

		point1 = point1.set(body.getPosition().x + (30 * Level.PIXELS_TO_METERS), point1.y);
		point2 = point2.set(point1.x, point2.y);

		world.rayCast(callback, point1, point2);
	}

	public void render(SpriteBatch batch)
	{
		batch.draw(tex, body.getPosition().x * Level.METERS_TO_PIXELS - (width / 2) + 10, body.getPosition().y
				* Level.METERS_TO_PIXELS - (height / 2), width / 2, height / 2, width, height, scaleX, scaleY, 0);
	}
}
