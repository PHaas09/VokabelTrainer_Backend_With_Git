

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Minimal file-per-quiz storage helper.
 * - Stores quizzes as UTF-8 text files under the "quizzes/" folder.
 * - Each line in a quiz file is one entry (e.g., a word).
 */
public class QuickStore {
    private static final Path QUIZ_DIR = Paths.get("quizzes");

    /** Ensure the quizzes directory exists. */
    public static void ensureDir() throws IOException {
        if (!Files.exists(QUIZ_DIR)) {
            Files.createDirectories(QUIZ_DIR);
        }
    }

    /** List existing quizzes (base names without .txt), sorted alphabetically. */
    public static List<String> listQuizzes() throws IOException {
        ensureDir();
        try (DirectoryStream<Path> ds = Files.newDirectoryStream(QUIZ_DIR, "*.txt")) {
            List<String> names = new ArrayList<>();
            for (Path p : ds) {
                names.add(stripExt(p.getFileName().toString()));
            }
            Collections.sort(names);
            return names;
        }
    }

    /** Load a quiz (returns empty list if it does not exist yet). */
    public static List<String> loadQuiz(String name) throws IOException {
        Path file = quizPath(name);
        if (!Files.exists(file)) return new ArrayList<>();
        return Files.readAllLines(file, StandardCharsets.UTF_8);
    }

    /** Save/overwrite a quiz atomically (write temp + move). */
    public static void saveQuiz(String name, List<String> lines) throws IOException {
        ensureDir();
        Path file = quizPath(name);
        Path tmp = Files.createTempFile(QUIZ_DIR, ".tmp-" + safe(name), ".txt");
        Files.write(tmp, lines, StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING);
        Files.move(tmp, file, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
    }

    /** Delete a quiz file (returns true if something was deleted). */
    public static boolean deleteQuiz(String name) throws IOException {
        return Files.deleteIfExists(quizPath(name));
    }

    private static Path quizPath(String name) {
        return QUIZ_DIR.resolve(safe(name) + ".txt");
    }

    /** Keep filenames safe and simple. */
    private static String safe(String name) {
        return name.replaceAll("[^a-zA-Z0-9-_]", "_");
    }

    private static String stripExt(String fn) {
        int i = fn.lastIndexOf('.');
        return (i > 0) ? fn.substring(0, i) : fn;
    }
}
