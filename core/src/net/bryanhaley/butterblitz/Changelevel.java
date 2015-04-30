package net.bryanhaley.butterblitz;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class Changelevel extends GameObject
{
	private Level level;
	private String newLevel;
	
	public Changelevel(World world, Level level, String newLevel, Rectangle rectangle)
	{
		this.create(world, level, newLevel, rectangle);
	}
	
	public void create(World world, Level level, String newLevel, Rectangle rectangle)
	{
		BodyDef bodyDef = new BodyDef();
		bodyDef.position.set((rectangle.x+(rectangle.width/2))*Level.PIXELS_TO_METERS, (rectangle.y+(rectangle.height/2))*Level.PIXELS_TO_METERS);
		bodyDef.type = BodyType.KinematicBody;
		
		this.body = world.createBody(bodyDef);
		
		PolygonShape shape = new PolygonShape();
		shape.setAsBox((rectangle.width/2)*Level.PIXELS_TO_METERS, (rectangle.height/2)*Level.PIXELS_TO_METERS);
		
		body.createFixture(shape, 1);
		body.setUserData(this);
		
		shape.dispose();
		
		this.tex = new TextureRegion(new Texture("killzone.png"));
		this.level = level;
		this.newLevel = newLevel;
		
		this.create(world, tex, body);
	}
	
	public void checkCollision(GameObject collision)
	{
		if (collision instanceof Player)
		{
			level.setNextMap(newLevel);
		}
	}

	public void render(SpriteBatch batch) { }
}
