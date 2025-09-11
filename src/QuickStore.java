import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class QuickStore {
    private static final Path QUIZ_DIR = Paths.get("quizzes");

    public static void ensureDir() throws IOException {
        if(!Files.exists(QUIZ_DIR)){
            Files.createDirectories(QUIZ_DIR);
        }
    }
    public static List<String> listquizzes() throws IOException {
        ensureDir();
        try(DirectoryStream<Path> ds = Files.newDirectoryStream(QUIZ_DIR, "*.txt")){
            List<String> names = new ArrayList<>();
            for(Path p : ds){
                names.add(striptExt(p.getFileName().toString()));
            }
            Collections.sort(names);
            return names;
        }
    }

    private static String striptExt(String fn) {
        int i = fn.indexOf('.');
        return (i>0)?fn.substring(0,i):fn;
    }
}