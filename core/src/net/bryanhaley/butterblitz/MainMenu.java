package net.bryanhaley.butterblitz;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;

public class MainMenu extends Game implements State
{
	public MainMenu()
	{
		this.create();
	}

	@Override
	public void create()
	{
		// TODO Auto-generated method stub
		this.setScreen(new MenuScreen(this));
	}

	@Override
	public void create(String[] args)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void create(String arg)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void update()
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void render()
	{
		Gdx.gl.glClearColor(0.5f, 0.5f, 0.5f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		this.getScreen().render(Gdx.graphics.getDeltaTime());
	}

	@Override
	public void dispose()
	{
		// TODO Auto-generated method stub
		this.getScreen().dispose();
	}

}
