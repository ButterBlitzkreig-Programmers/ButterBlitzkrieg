package net.bryanhaley.butterblitz;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.TimeUtils;

public class Projectile extends GameObject
{
	private long launchTime;
	
	public Projectile(World world, String img, Vector3 dimensions, Vector3 velocityPlusRot)
	{
		TextureRegion tex = new TextureRegion(new Texture(img));
		
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.position.set((dimensions.x-dimensions.z)*Level.PIXELS_TO_METERS,
				(dimensions.y-dimensions.z)*Level.PIXELS_TO_METERS);
		Body body = world.createBody(bodyDef);
		
		CircleShape circle = new CircleShape();
		circle.setRadius(dimensions.z*Level.PIXELS_TO_METERS);
		body.createFixture(circle, 1);
		circle.dispose();
		
		body.setBullet(true);
		body.setUserData(this);
		
		this.create(world, tex, body);
		
		body.setSleepingAllowed(true);
		body.setTransform(body.getPosition(), velocityPlusRot.z);
		body.setLinearVelocity(velocityPlusRot.x, velocityPlusRot.y);
		
		launchTime = TimeUtils.nanoTime();
	}
	
	public void update()
	{
		super.update();
		
		if (Math.abs(body.getLinearVelocity().x) < 0.001 && Math.abs(body.getLinearVelocity().y) < 0.001 && Math.abs(body.getAngularVelocity()) < 3)
		{
			this.destroy();
		}
		
		else if (!body.isAwake())
		{
			this.destroy();
		}
		
		else if (TimeUtils.nanoTime() - launchTime > 10000000000l)
		{
			this.destroy();
		}
	}
	
	public void checkCollision(GameObject collision)
	{
		if (!(collision instanceof Player) && !(collision instanceof Projectile))
		{
			this.destroy();
		}
	}
}
