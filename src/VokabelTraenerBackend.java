

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

/**
 * Konsolenprogramm: Verwalte mehrere Quizzes (je Quiz eine .txt-Datei).
 * Befehle innerhalb eines Quizzes:
 *  a = Eintrag aendern
 *  w = Eintrag loeschen
 *  l = komplettes Quiz loeschen
 *  n = neue Eintraege hinzufuegen (wenn du noch mehr willst)
 *  q = zur Quiz-Auswahl zurueck
 */
public class VokabelTraenerBackend {

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                // ===== Quiz-Auswahl =====
                String quizName = pickQuiz(scanner);
                if (quizName == null) {
                    System.out.println("Tschuess!");
                    return;
                }

                // ===== Quiz laden (neu oder vorhanden) =====
                List<String> words = QuickStore.loadQuiz(quizName);
                if (words.isEmpty()) {
                    System.out.println("Neues Quiz \"" + quizName + "\". Fuege Eintraege hinzu.");
                    inputWords(scanner, words);
                    QuickStore.saveQuiz(quizName, words);
                    System.out.println("Gespeichert.");
                }

                // ===== Hauptmenue fuer ein Quiz =====
                boolean backToMenu = false;
                while (!backToMenu) {
                    printList(words);
                    System.out.println("\nWas moechten Sie tun?");
                    System.out.println(" (a) Eintrag aendern");
                    System.out.println(" (w) Eintrag loeschen");
                    System.out.println(" (n) Neue Eintraege hinzufuegen");
                    System.out.println(" (l) Ganzes Quiz loeschen");
                    System.out.println(" (q) Zurueck zur Quiz-Auswahl");
                    System.out.print("Antwort: ");
                    String wahl = scanner.nextLine().trim();

                    switch (wahl.toLowerCase()) {
                        case "a":
                            if (words.isEmpty()) {
                                System.out.println("Keine Eintraege vorhanden.");
                                break;
                            }
                            int idx = askIndex(scanner, words.size(), "Nummer des Eintrags zum Aendern");
                            if (idx >= 0) {
                                System.out.print("Neuer Text: ");
                                String neu = scanner.nextLine();
                                words.set(idx, neu);
                                QuickStore.saveQuiz(quizName, words);
                                System.out.println("Eintrag aktualisiert.");
                            }
                            break;
                        case "w":
                            if (words.isEmpty()) {
                                System.out.println("Keine Eintraege vorhanden.");
                                break;
                            }
                            int delIdx = askIndex(scanner, words.size(), "Nummer des Eintrags zum Loeschen");
                            if (delIdx >= 0) {
                                String removed = words.remove(delIdx);
                                QuickStore.saveQuiz(quizName, words);
                                System.out.println("Geloescht: " + removed);
                            }
                            break;
                        case "n":
                            inputWords(scanner, words);
                            QuickStore.saveQuiz(quizName, words);
                            System.out.println("Gespeichert.");
                            break;
                        case "l":
                            System.out.print("Sind Sie sicher, dass Sie das Quiz \"" + quizName + "\" loeschen wollen? (j/n): ");
                            String conf = scanner.nextLine();
                            if (conf.equalsIgnoreCase("j")) {
                                if (QuickStore.deleteQuiz(quizName)) {
                                    System.out.println("Quiz geloescht.");
                                } else {
                                    System.out.println("Loeschen fehlgeschlagen oder Quiz existiert nicht.");
                                }
                                backToMenu = true; // zurueck in die Uebersicht
                            }
                            break;
                        case "q":
                            backToMenu = true;
                            break;
                        default:
                            System.out.println("Ungueltige Auswahl.");
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Dateifehler: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // === Hilfsfunktionen ===

    /** Zeigt vorhandene Quizzes und fragt nach Auswahl oder neuem Namen. */
    private static String pickQuiz(Scanner scanner) throws IOException {
        while (true) {
            System.out.println("\n==== Verfuegbare Quizzes ====");
            List<String> quizzes = QuickStore.listQuizzes();
            if (quizzes.isEmpty()) {
                System.out.println("(Keine vorhanden)");
            } else {
                for (int i = 0; i < quizzes.size(); i++) {
                    System.out.printf(" %2d. %s%n", i + 1, quizzes.get(i));
                }
            }
            System.out.println("\nOptionen:");
            System.out.println(" [Nummer] Quiz oeffnen");
            System.out.println(" n       Neues Quiz anlegen");
            System.out.println(" x       Beenden");
            System.out.print("Eingabe: ");
            String in = scanner.nextLine().trim();

            if (in.equalsIgnoreCase("x")) return null;
            if (in.equalsIgnoreCase("n")) {
                System.out.print("Name fuer neues Quiz: ");
                String name = scanner.nextLine().trim();
                if (name.isEmpty()) {
                    System.out.println("Name darf nicht leer sein.");
                    continue;
                }
                return name;
            }
            // Nummer?
            try {
                int num = Integer.parseInt(in);
                if (num >= 1 && num <= quizzes.size()) {
                    return quizzes.get(num - 1);
                } else {
                    System.out.println("Ungueltige Nummer.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Bitte Nummer, 'n' oder 'x' eingeben.");
            }
        }
    }

    /** Fuegt interaktiv Eintraege hinzu (eine Zeile pro Eintrag). */
    private static void inputWords(Scanner scanner, List<String> words) {
        System.out.println("Eintraege hinzufuegen. Leere Zeile beendet die Eingabe.");
        while (true) {
            System.out.print("Eintrag: ");
            String line = scanner.nextLine();
            if (line.trim().isEmpty()) break;
            words.add(line);
        }
    }

    /** Druckt Liste mit Indizes ab 1. */
    private static void printList(List<String> list) {
        System.out.println("\n==== Eintraege ====");
        if (list.isEmpty()) {
            System.out.println("(leer)");
            return;
        }
        for (int i = 0; i < list.size(); i++) {
            System.out.printf("%2d. %s%n", i + 1, list.get(i));
        }
    }

    /** Fragt eine gueltige 1-basierte Indexnummer ab und liefert 0-basierte Position; -1 bei Abbruch. */
    private static int askIndex(Scanner scanner, int size, String prompt) {
        while (true) {
            System.out.print(prompt + " (1-" + size + ", oder 0 zum Abbrechen): ");
            String s = scanner.nextLine().trim();
            try {
                int n = Integer.parseInt(s);
                if (n == 0) return -1;
                if (n >= 1 && n <= size) return n - 1;
            } catch (NumberFormatException ignored) {}
            System.out.println("Ungueltige Eingabe.");
        }
    }
}
