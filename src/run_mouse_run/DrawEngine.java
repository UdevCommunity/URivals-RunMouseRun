package run_mouse_run;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class DrawEngine {

	private PathFinder pathFinder;

	private ArrayList<DE_Frame> frames;	// All open frames

    private ArrayList<Map> maps;

    public DrawEngine(Map map)
	{
        maps = new ArrayList<>();
        updateMapsList(map);

		frames = new ArrayList<>();
		frames.add(new DE_Frame(this, map));

		pathFinder = new PathFinder(map);
	}

	public void printMessage(String message)
	{
		for(DE_Frame frame : frames)
		{
			frame.printLog(message);
		}
	}

    private synchronized void updateMapsList(Map map)
    {
        maps.clear();
        maps.add(map);
        // add mouse Maps
        for(Mouse m: GameManager.gameManager.getMouses())
            maps.add(m.getViewedMap());
        // add cat Maps
        for(Cat c : GameManager.gameManager.getCats())
            maps.add(c.getViewedMap());
    }

    /**
	 * Update all frames
	 */
	public void update()
	{
        //updateMapsList(maps.get(0));

        for(int i = 0; i < frames.size(); i++)
		{
			frames.get(i).update();
		}
	}

	/**
	 * Set visibility of all frames
	 * @param visible (boolean)
	 */
	public void setVisible(boolean visible)
	{
		for(DE_Frame frame : frames)
			frame.setVisible(visible);
	}

	/**
	 * Functions for button Action, call gameManager method and change all frames
	 */
	public void startGame()
	{
		GameManager.gameManager.startGame();
		for(DE_Frame frame : frames)
		{
			frame.changeState("Start Game");
            //frame.updateCmBox();
		}

		update();
	}

	public void pauseGame()
	{
		GameManager.gameManager.pauseGame();
		for(DE_Frame frame: frames)
		{
			frame.changeState("Pause Game");
		}
	}

	public void resumeGame()
	{
		GameManager.gameManager.resumeGame();
		for(DE_Frame frame: frames)
		{
			frame.changeState("Resume Game");
		}
	}

	public void displayEndGameScreen(String result)
	{
		for(int i = 1; i < frames.size(); i++)
			frames.get(i).dispose();

		frames.get(0).displayEndGameScreen(result);
	}

	public void explodeMine(int x, int y)
	{
		for(int i = 0; i < frames.size(); i++)
			frames.get(i).explodeMine(x, y);
	}

	/**
	 * Add a new frame, open at small size
	 * apply changes depending on state
	 * @param maps
	 */
	public void addNewFrame(ArrayList<Map> maps)
	{
		DrawEngine drawEngine = this;
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				DE_Frame newFrame = new DE_Frame(drawEngine, maps.get(0));
				newFrame.startGameButton.setText(frames.get(0).startGameButton.getText());

				// We only need one control panel
				newFrame.hideControlPanel();
				newFrame.setBounds(100,100, 500,500);
				newFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				newFrame.setAlwaysOnTop(true);

				// Set state to the same state of her
				if(newFrame.startGameButton.getText().equals("Resume Game")) // if game Paused
				{
					newFrame.changeState("Pause Game");
					newFrame.hideControlPanel();
				}
				else if(newFrame.startGameButton.getText().equals("Pause Game")) // if game running
				{
					newFrame.changeState("Resume Game");
					newFrame.hideControlPanel();
				}

				newFrame.setVisible(true);
				frames.add(newFrame);
			}
		});

	}


	public ArrayList<Mouse> getMouses()
	{
		return GameManager.gameManager.getMouses();
	}

	public ArrayList<Cat> getCats() {
		return GameManager.gameManager.getCats();
	}

	public ArrayList<Map> getMaps() {
		return maps;
	}

	public void createNewLevel(int w, int h)
	{
		/// TODO : Bug fix
		maps.remove(GameManager.gameManager.getLevelGenerator().getMap()); // removeLevelMap
		GameManager.gameManager.getLevelGenerator().setMap(w, h);
		maps.add(0, GameManager.gameManager.getLevelGenerator().getMap()); // add new LevelMap
	}

	public CustomTimer getTimer() {
		return GameManager.gameManager.getTimer();
	}
}   // End Of DrawEngine