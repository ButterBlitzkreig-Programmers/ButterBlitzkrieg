package net.bryanhaley.butterblitz;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class Killzone extends GameObject
{
	public Killzone(World world, Rectangle rectangle)
	{
		this.create(world, rectangle);
	}
	
	public void create(World world, Rectangle rectangle)
	{
		Gdx.app.log("Position", rectangle.x+"x"+rectangle.y);
		Gdx.app.log("Dimensions", rectangle.width+"x"+rectangle.height);
		
		BodyDef bodyDef = new BodyDef();
		bodyDef.position.set((rectangle.x+(rectangle.width/2))*Level.PIXELS_TO_METERS, (rectangle.y+(rectangle.height/2))*Level.PIXELS_TO_METERS);
		bodyDef.type = BodyType.KinematicBody;
		
		this.body = world.createBody(bodyDef);
		
		FixtureDef fixture = new FixtureDef();
		PolygonShape shape = new PolygonShape();
		shape.setAsBox((rectangle.width/2)*Level.PIXELS_TO_METERS, (rectangle.height/2)*Level.PIXELS_TO_METERS);
		fixture.shape = shape;
		fixture.isSensor = true;
		
		body.createFixture(fixture);
		body.setUserData(this);

		shape.dispose();
		
		this.tex = new TextureRegion(new Texture("killzone.png"));
		
		this.create(world, tex, body);
	}
	
	public void render(SpriteBatch batch) { }
}
