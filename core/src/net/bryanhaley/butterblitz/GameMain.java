package net.bryanhaley.butterblitz;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;

/* Entry point (sort-of) for the game. Might use nasty code and bad practices to keep things simple
 * and get this out in-time.
 */

public class GameMain extends ApplicationAdapter
{
	private static State state;
	
	public void create()
	{
		state = new MainMenu();
		state.create();
	}
	
	public void update()
	{
		state.update();
	}
	
	public void render()
	{
		this.update();
		
		state.render();
	}
	
	public static void switchState(State newState)
	{
		state.dispose();
		state = newState;
	}
	
	public void dispose()
	{
		state.dispose();
	}
}
