package run_mouse_run;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;


public class SecurityManager
{
    private ArrayList<String> classNotToUse = new ArrayList<String>()
    {{
        add("GameManager");
        add("CharacterController");
        add("CustomTimer");
        add("DE_Animation");
        add("DE_GameSprites");
        add("DE_MapPanel");
        add("DE_TileImage");
        add("SecurityManager");
        add("DE_Frame");
        add("GameMode");
        add("LevelGenerator");
        add("PhysicsEngine");
        add("PathFinder");
        add("DrawEngine");
    }};

    public void checkForImportGameManager(String directoryPath) throws IOException
    {
        directoryPath = directoryPath.replace("%20", " ");

        File[] files = new File(directoryPath).listFiles();

        for (File file : files)
        {
            if (!file.isDirectory())
            {
                String content = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
                for(String forbiddenClass: classNotToUse)
                {
                    if(content.contains(forbiddenClass))
                        GameManager.gameManager.stopGame(file.getName() + " contain cheat code !");
                }

            }
        }
    }
}
