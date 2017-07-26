package run_mouse_run;


import java.io.File;
import java.io.IOException;

public class FileManager
{
    public static File getRessourceFile(String fileName) throws IOException
    {
        File file = new File("res/" + fileName);
        return file;
    }
}
