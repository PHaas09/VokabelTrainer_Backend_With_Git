import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class QuickStore {
    private static final Path QUIZ_DIR = Paths.get("quizzes");

    public static void insureDir() throws IOException {
        if(!Files.exists(QUIZ_DIR)){
            Files.createDirectories(QUIZ_DIR);
        }
    }
}