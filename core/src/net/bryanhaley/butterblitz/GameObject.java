package net.bryanhaley.butterblitz;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

/* Every non-level object is a gameobject. Game Objects have an image,
 * a box2d body, and a position. Every gameobject can be created, updated,
 * and rendered.
 */

public class GameObject
{
	protected TextureRegion tex;
	protected Body body;
	protected Vector2 position;
	
	public GameObject(String img, Rectangle collisionBox, Vector2 position)
	{
		
	}
	
	public void create(TextureRegion tex, Body body, Vector2 position)
	{
		this.tex = tex;
		this.body = body;
		this.position = position;
	}
	
	public Vector2 getPosition()
	{
		return position;
	}
}
