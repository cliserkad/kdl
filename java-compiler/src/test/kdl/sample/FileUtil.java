package test.kdl.sample;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class FileUtil {

    public static boolean writeOut(final File f, final String s) {
        try {
            Files.write(f.toPath(), s.getBytes());
            return true;
        } catch (IOException e) {
            return false;
        }
    }

}
