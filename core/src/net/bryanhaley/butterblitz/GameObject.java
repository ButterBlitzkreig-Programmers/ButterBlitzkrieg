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
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

/* Every non-level object is a gameobject. Game Objects have an image,
 * and a box2d body. Every gameobject can be created, updated,
 * and rendered.
 */

public class GameObject
{
	protected TextureRegion tex;
	protected Body body;
	protected float width, height, originX, originY, scaleX, scaleY, rotation;
	
	public GameObject(World world)
	{
		this(world, "example_object.png", new Rectangle(200,200,32,32));
	}
	
	public GameObject(World world, String img, Rectangle collisionBox)
	{
		tex = new TextureRegion(new Texture(Gdx.files.internal(img)));
		this.width = tex.getRegionWidth();
		this.height = tex.getRegionHeight();
		
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.position.set(collisionBox.x*Level.PIXELS_TO_METERS, collisionBox.y*Level.PIXELS_TO_METERS);
		
		Body body = world.createBody(bodyDef);
		
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(collisionBox.width/2*Level.PIXELS_TO_METERS, collisionBox.height/2*Level.PIXELS_TO_METERS);
		
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.friction = 0.3f;
		fixtureDef.density = 1f;
		
		body.createFixture(fixtureDef);
		
		shape.dispose();
		
		this.create(tex, body);
	}
	
	public GameObject(TextureRegion tex, Body body)
	{
		this.create(tex, body);
	}
	
	public void create(TextureRegion tex, Body body)
	{
		this.tex = tex;
		this.body = body;
		
		originX = 0; originY = 0;
		scaleX = 1; scaleY = 1;
		rotation = 0;
	}
	
	public void update()
	{
		//These are for testing!!
		if (Gdx.input.isKeyPressed(Keys.I))
		{
			body.setLinearVelocity(new Vector2(body.getLinearVelocity().x,5));
		}
		else if (Gdx.input.isKeyPressed(Keys.K))
		{
			body.setLinearVelocity(new Vector2(body.getLinearVelocity().x,-5));
		}
		
		if (Gdx.input.isKeyPressed(Keys.J))
		{
			body.setLinearVelocity(new Vector2(-5,body.getLinearVelocity().y));
		}
		else if (Gdx.input.isKeyPressed(Keys.L))
		{
			body.setLinearVelocity(new Vector2(5,body.getLinearVelocity().y));
		}
		
		if (Gdx.input.isKeyPressed(Keys.O))
		{
			body.setAngularVelocity(0);
			body.setTransform(body.getPosition(), 0);
		}
		//Testing over
	}
	
	public void render(SpriteBatch batch)
	{
		batch.draw(tex, body.getPosition().x*Level.METERS_TO_PIXELS-(width/2),
				body.getPosition().y*Level.METERS_TO_PIXELS-(height/2),
				width/2, height/2, width, height, scaleX, scaleY,
				body.getTransform().getRotation()*57.2957795f);
	}
	
	public Vector2 getPositionPixels()
	{
		Vector2 newPos = new Vector2(body.getPosition());
		newPos.set(newPos.x*Level.METERS_TO_PIXELS, newPos.y*Level.METERS_TO_PIXELS);
		return newPos;
	}
	
	public Vector2 getPositionMeters()
	{
		return body.getPosition();
	}
	
	public void dispose()
	{
		tex.getTexture().dispose();
	}
}
