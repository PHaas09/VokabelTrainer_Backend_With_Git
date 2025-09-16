import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static java.util.Collections.replaceAll;

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

    public static List<String>loadquizzes(String name) throws IOException {
        Path file = quizPath(name);
        if(!Files.exists(file)){
            return new ArrayList<>();
        }
        return Files.readAllLines(file, StandardCharsets.UTF_8);
    }

    public static void saveQuiz(String name, List<String> lines)throws IOException{
        ensureDir();
        Path file = quizPath(name);
        Path tmp = Files.createTempFile(QUIZ_DIR, ".tmp-"+safe(name),".txt");
        Files.write(tmp, lines, StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING);
        Files.move(tmp, file, StandardCopyOption.REPLACE_EXISTING,StandardCopyOption.ATOMIC_MOVE);
    }

    public static Path quizPath(String name) throws IOException {
        return QUIZ_DIR.resolve(safe(name) + ".txt");
    }

    public static String safe(String name) {
        return name.replaceAll("[^a-zA-Z0-9-_]","_");
    }

    private static String striptExt(String fn) {
        int i = fn.indexOf('.');
        return (i>0)?fn.substring(0,i):fn;
    }
}