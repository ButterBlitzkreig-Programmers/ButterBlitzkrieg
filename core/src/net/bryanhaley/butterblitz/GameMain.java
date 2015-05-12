package net.bryanhaley.butterblitz;

import com.badlogic.gdx.ApplicationAdapter;
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
