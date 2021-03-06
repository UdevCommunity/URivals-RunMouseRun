package run_mouse_run;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class DrawEngine
{

	private ArrayList<DE_Frame> frames;	// All open frames

    private ArrayList<Map> maps;

    DrawEngine(Map map)
	{
        maps = new ArrayList<>();
        updateMapsList(map);

		frames = new ArrayList<>();
		frames.add(new DE_Frame(this, map));
	}

	void printMessage(String message)
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
	void update()
	{
        //updateMapsList(maps.get(0));

        for (DE_Frame frame : frames)
        {
            frame.update();
        }
	}

	/**
	 * Set visibility of all frames
	 * @param visible (boolean)
	 */
	void setVisible(boolean visible)
	{
		for(DE_Frame frame : frames)
		{
			frame.setVisible(visible);
			frame.update();
		}
	}

	/**
	 * Functions for button Action, call gameManager method and change all frames
	 */
	void startGame()
	{
		GameManager.gameManager.startGame();
		for(DE_Frame frame : frames)
		{
			frame.changeState("Start Game");
            //frame.updateCmBox();
		}

		update();
	}

	void pauseGame()
	{
		GameManager.gameManager.pauseGame();
		for(DE_Frame frame: frames)
		{
			frame.changeState("Pause Game");
		}
	}

	void resumeGame()
	{
		GameManager.gameManager.resumeGame();
		for(DE_Frame frame: frames)
		{
			frame.changeState("Resume Game");
		}
	}

	void displayEndGameScreen(String result)
	{
		for(int i = 1; i < frames.size(); i++)
			frames.get(i).dispose();

		frames.get(0).displayEndGameScreen(result);
	}

	void explodeMine(int x, int y)
	{
		for (DE_Frame frame : frames) frame.explodeMine(x, y);
	}

	void showStun(int x, int y)
	{
		for (DE_Frame frame : frames) frame.showStun(x, y);
	}

	/**
	 * Add a new frame, open at small size
	 * apply changes depending on state
	 * @param maps
	 */
	void addNewFrame(ArrayList<Map> maps)
	{
		DrawEngine drawEngine = this;
		EventQueue.invokeLater(() ->
		{
            DE_Frame newFrame = new DE_Frame(drawEngine, maps.get(0));
            newFrame.startGameButton.setText(frames.get(0).startGameButton.getText());

            // We only need one control panel
            newFrame.hideControlPanel();
            newFrame.setBounds(100,100, 500,500);
            newFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            newFrame.setAlwaysOnTop(false);

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
        });

	}


	public ArrayList<Mouse> getMouses()
	{
		return GameManager.gameManager.getMouses();
	}

	public ArrayList<Cat> getCats() {
		return GameManager.gameManager.getCats();
	}

	ArrayList<Map> getMaps() {
		return maps;
	}

	void createNewLevel(int w, int h)
	{
		maps.remove(GameManager.gameManager.getLevelGenerator().getMap()); // removeLevelMap
		GameManager.gameManager.getLevelGenerator().setMap(w, h);

		updateMapsList(GameManager.gameManager.getLevelGenerator().getMap()); // add new LevelMap

		for(DE_Frame frame: frames)
			frame.changeMapSize(maps.get(0));
	}

	CustomTimer getTimer() {
		return GameManager.gameManager.getTimer();
	}

    void startNewGame()
	{
		GameManager.gameManager.stopGame("");
		// Close Everything
		for(DE_Frame frame: frames)
		{
			frame.dispose();
		}

		EventQueue.invokeLater(() ->
        {
            GameManager gameManager = new GameManager();
            gameManager.getDrawEngine().setVisible(true);
        });

    }
}   // End Of DrawEngine