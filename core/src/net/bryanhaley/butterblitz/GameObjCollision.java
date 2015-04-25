package net.bryanhaley.butterblitz;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;

/* This object defines what happens when two physics objects
 * collide.
 */
public class GameObjCollision implements ContactListener
{
	@Override
	public void beginContact(Contact contact)
	{
		//We only care about GameObjects colliding at the moment
		if (contact.getFixtureA().getBody().getUserData() instanceof GameObject &&
				contact.getFixtureB().getBody().getUserData() instanceof GameObject)
		{
			GameObject gameObjOne = (GameObject) contact.getFixtureA().getBody().getUserData();
			GameObject gameObjTwo = (GameObject) contact.getFixtureB().getBody().getUserData();
		
			//Send both objects a message that they've collided with something
			gameObjOne.collisionWith(gameObjTwo);
			gameObjTwo.collisionWith(gameObjOne);
		}
	}

	@Override
	public void endContact(Contact contact)
	{
		//This method is called when the two objects stop colliding
		if (contact.getFixtureA().getBody().getUserData() instanceof GameObject &&
				contact.getFixtureB().getBody().getUserData() instanceof GameObject)
		{
			GameObject gameObjOne = (GameObject) contact.getFixtureA().getBody().getUserData();
			GameObject gameObjTwo = (GameObject) contact.getFixtureB().getBody().getUserData();
		
			gameObjOne.stopCollisionWith(gameObjTwo);
			gameObjTwo.stopCollisionWith(gameObjOne);
		}
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse)
	{
		// TODO Auto-generated method stub
		
	}
	
}