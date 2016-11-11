package log;

import java.io.File;
import java.io.IOException;

/**
 * Created by Paul Lancaster on 31/10/2016
 */
public class LanFile {
    // TODO FIXME let this handle creating files that are not in a directory
    public static File createFile(String path) throws IOException { // Creates a file adding a increasing number to stop the file conflicting with existing files
        File f = new File(path.substring(0,path.lastIndexOf("/"))); // Gets the all of the string before the last / (eg the directory of the file)
        if (f.mkdirs()){
            System.out.println("New folder @ " + f.getPath());
        }
        
        File file = new File(path);
        if (!file.createNewFile()) {
            String[] splitName = path.split("\\.");
            String name = splitName[0];
            String type = splitName[1];
            splitName = null;
            int i = 0;
            do {
                i++;
                file = new File(name + "#" +i + "." + type);
            } while (!file.createNewFile());
        }
        return file;
    }
}
