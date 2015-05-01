package net.bryanhaley.butterblitz;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

public class MenuScreen implements Screen
{
	private Game game;
	private Stage stage;
	private TextButton btnStart;
	private TextField text;
	private Label intro;
	
	public MenuScreen(Game game)
	{
		this.game = game;
		stage = new Stage();
		Gdx.input.setInputProcessor(stage);
		
		Skin skin = new Skin(Gdx.files.internal("uiskin.json"));
		
		text = new TextField("map.tmx here", skin);
		text.setSize(400, 40);
		text.setPosition(440, 300);
		
		intro = new Label("", skin);
		intro.setAlignment(Align.center);
		intro.setText("WASD to move.\n"
					+ "Space to jump.\n"
					+ "Control to slow-walk.\n"
					+ "F to shoot pop-gun.\n"
					+ "Hold P to see collision boxes.\n"
					+ "Escape to return to map loader.\n"
					+ "\n"
					+ "Check command prompt window for debug info.\n"
					+ "Enter a map file below. It should be located in ButterBlitz\\MAPS\n"
					+ "test.tmx is an example level in the MAPS folder. All resources (map file, tilesets, etc) go there.");
		intro.setSize(400, 350);
		intro.setPosition(440, 350);
		
		btnStart = new TextButton("Start", skin);
		btnStart.setPosition(490, 200);
		btnStart.setSize(300, 60);
		btnStart.addListener(new ClickListener()
		{
			public void touchUp(InputEvent e, float x, float y, int point, int button)
			{
				try
				{
				String mapName = text.getText();
				
				if (mapName.equals("map.tmx here"))
				{ mapName = "example.tmx"; }
				else
				{ mapName = "MAPS/"+mapName; }
				
				GameState gameState = new GameState();
				gameState.create("-map " + mapName);
				GameMain.switchState(gameState);
				} catch (Exception excep) { Gdx.app.log("Map " + text.getText(), "Not found!\n"); reset();}
			}
		});
		
		stage.addActor(intro);
		stage.addActor(text);
		stage.addActor(btnStart);
	}
	
	public void reset()
	{
		GameMain.switchState(new MainMenu());
	}
	
	@Override
	public void show()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void render(float delta)
	{
		stage.act(delta);
		stage.draw();
	}

	@Override
	public void resize(int width, int height)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void pause()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void resume()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void hide()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose()
	{
		// TODO Auto-generated method stub

	}

}
