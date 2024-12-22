import java.io.*;
import java.util.*;

public class LanguageLearningApp {

    static class Flashcard {
        String word;
        String translation;
        int masteryLevel;
        String difficulty;

        Flashcard(String word, String translation, String difficulty) {
            this.word = word;
            this.translation = translation;
            this.masteryLevel = 0;
            this.difficulty = difficulty; 
        }

        void increaseMastery() {
            if (masteryLevel < 5) {
                masteryLevel++;
            }
        }

        boolean isMastered() {
            return masteryLevel == 5;
        }

        boolean isHard() {
            return difficulty.equals("Hard");
        }
    }

    static class UserProfile {
        String username;
        int score;
        int level;
        Map<String, Flashcard> masteredWords;

        UserProfile(String username) {
            this.username = username;
            this.score = 0;
            this.level = 1;
            this.masteredWords = new HashMap<>();
        }

        void increaseScore() {
            this.score++;
        }

        void levelUp() {
            this.level++;
        }

        void saveProfile() {
            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(username + ".ser"))) {
                out.writeObject(this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        static UserProfile loadProfile(String username) {
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(username + ".ser"))) {
                return (UserProfile) in.readObject();
            } catch (IOException | ClassNotFoundException e) {
                return null;
            }
        }
    }

    static class Leaderboard {
        Map<String, Integer> scores = new HashMap<>();

        void addUserScore(String username, int score) {
            scores.put(username, score);
        }

        void displayLeaderboard() {
            System.out.println("\nLeaderboard:");
            scores.entrySet().stream()
                    .sorted((entry1, entry2) -> entry2.getValue() - entry1.getValue())
                    .forEach(entry -> System.out.println(entry.getKey() + ": " + entry.getValue() + " points"));
        }
    }

    static class DailyChallenge {
        static int day = 1;

        static List<Flashcard> getChallenge() {
            List<Flashcard> challenge = new ArrayList<>();
            if (day == 1) {
                challenge.add(new Flashcard("Good morning", "Buenos días", "Easy"));
                challenge.add(new Flashcard("Thank you", "Gracias", "Easy"));
                challenge.add(new Flashcard("How are you?", "¿Cómo estás?", "Medium"));
                challenge.add(new Flashcard("Please", "Por favor", "Medium"));
            } else {
                challenge.add(new Flashcard("Dog", "Perro", "Easy"));
                challenge.add(new Flashcard("Cat", "Gato", "Medium"));
                challenge.add(new Flashcard("Elephant", "Elefante", "Hard"));
                challenge.add(new Flashcard("Bird", "Pájaro", "Hard"));
            }
            day++;
            return challenge;
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Leaderboard leaderboard = new Leaderboard();

        System.out.println("Welcome to the Language Learning App!");
        System.out.print("Enter your username: ");
        String username = scanner.nextLine();

        UserProfile user = UserProfile.loadProfile(username);
        if (user == null) {
            user = new UserProfile(username);
        }

        
        Map<String, List<Flashcard>> categories = new HashMap<>();
        categories.put("Greetings", Arrays.asList(
                new Flashcard("Hello", "Hola", "Easy"),
                new Flashcard("Goodbye", "Adiós", "Easy"),
                new Flashcard("Please", "Por favor", "Medium"),
                new Flashcard("Thank you", "Gracias", "Medium")
        ));
        categories.put("Food", Arrays.asList(
                new Flashcard("Apple", "Manzana", "Medium"),
                new Flashcard("Bread", "Pan", "Easy"),
                new Flashcard("Water", "Agua", "Easy"),
                new Flashcard("Cheese", "Queso", "Hard")
        ));
        categories.put("Animals", Arrays.asList(
                new Flashcard("Dog", "Perro", "Easy"),
                new Flashcard("Cat", "Gato", "Medium"),
                new Flashcard("Elephant", "Elefante", "Hard"),
                new Flashcard("Bird", "Pájaro", "Medium")
        ));

        System.out.println("Choose an option:");
        System.out.println("1. Start Flashcard Quiz");
        System.out.println("2. View Grammar Lessons");
        System.out.println("3. View Profile");
        System.out.println("4. View Leaderboard");
        System.out.println("5. Daily Challenge");
        System.out.println("6. Timed Challenge");
        System.out.println("7. Exit");

        int choice = scanner.nextInt();
        scanner.nextLine();  

        switch (choice) {
            case 1:
                startQuiz(scanner, categories, user);
                break;
            case 2:
                viewGrammarLessons();
                break;
            case 3:
                viewProfile(user);
                break;
            case 4:
                leaderboard.displayLeaderboard();
                break;
            case 5:
                dailyChallenge(scanner, user);
                break;
            case 6:
                timedChallenge(scanner, categories, user);
                break;
            case 7:
                System.out.println("Goodbye!");
                leaderboard.addUserScore(user.username, user.score);
                user.saveProfile();
                leaderboard.displayLeaderboard();
                break;
            default:
                System.out.println("Invalid choice. Exiting...");
        }

        scanner.close();
    }

    public static void startQuiz(Scanner scanner, Map<String, List<Flashcard>> categories, UserProfile user) {
        System.out.println("Choose a category to start the quiz:");
        int i = 1;
        for (String category : categories.keySet()) {
            System.out.println(i + ". " + category);
            i++;
        }

        int categoryChoice = scanner.nextInt();
        scanner.nextLine();  

        String selectedCategory = new ArrayList<>(categories.keySet()).get(categoryChoice - 1);
        List<Flashcard> flashcards = categories.get(selectedCategory);

        Collections.shuffle(flashcards); 
        int correctAnswers = 0;

        System.out.println("Starting quiz in " + selectedCategory + " category...");
        for (Flashcard flashcard : flashcards) {
            System.out.println("What is the translation of: " + flashcard.word);
            String answer = scanner.nextLine().trim();

            if (answer.equalsIgnoreCase(flashcard.translation)) {
                correctAnswers++;
                user.increaseScore();
                flashcard.increaseMastery();
                System.out.println("Correct!\n");
            } else {
                System.out.println("Incorrect! The correct answer is: " + flashcard.translation + "\n");
            }
        }

        System.out.println("Quiz finished!");
        System.out.println("You got " + correctAnswers + " out of " + flashcards.size() + " correct.");
        user.levelUp();
        System.out.println("You leveled up to level " + user.level);
    }

    public static void viewGrammarLessons() {
        System.out.println("Grammar Lessons:");
        System.out.println("1. Verb Conjugation");
        System.out.println("2. Sentence Structure");
        System.out.println("3. Gender in Nouns");

        
        System.out.println("\nLesson: Verb Conjugation");
        System.out.println("In Spanish, verbs are conjugated based on tense and subject.");
        System.out.println("Example: 'To eat' -> 'Comer'\n");
    }

    public static void viewProfile(UserProfile user) {
        System.out.println("\nUser Profile: " + user.username);
        System.out.println("Score: " + user.score);
        System.out.println("Level: " + user.level);
        System.out.println("Mastered Words:");
        user.masteredWords.forEach((word, flashcard) -> {
            if (flashcard.isMastered()) {
                System.out.println(word + ": Mastered!");
            }
        });
    }

    public static void dailyChallenge(Scanner scanner, UserProfile user) {
        List<Flashcard> challenge = DailyChallenge.getChallenge();
        Collections.shuffle(challenge);

        System.out.println("\nStarting Daily Challenge:");
        int correctAnswers = 0;

        for (Flashcard flashcard : challenge) {
            System.out.println("What is the translation of: " + flashcard.word);
            String answer = scanner.nextLine().trim();

            if (answer.equalsIgnoreCase(flashcard.translation)) {
                correctAnswers++;
                user.increaseScore();
                flashcard.increaseMastery();
                System.out.println("Correct!\n");
            } else {
                System.out.println("Incorrect! The correct answer is: " + flashcard.translation + "\n");
            }
        }

        System.out.println("Daily Challenge finished!");
        System.out.println("You got " + correctAnswers + " out of " + challenge.size() + " correct.");
        user.levelUp();
        System.out.println("You leveled up to level " + user.level);
    }

    public static void timedChallenge(Scanner scanner, Map<String, List<Flashcard>> categories, UserProfile user) {
        System.out.println("Choose a category for the timed challenge:");
        int i = 1;
        for (String category : categories.keySet()) {
            System.out.println(i + ". " + category);
            i++;
        }

        int categoryChoice = scanner.nextInt();
        scanner.nextLine();  

        String selectedCategory = new ArrayList<>(categories.keySet()).get(categoryChoice - 1);
        List<Flashcard> flashcards = categories.get(selectedCategory);

        Collections.shuffle(flashcards); 
        int correctAnswers = 0;

        long startTime = System.currentTimeMillis();
        long timeLimit = 30 * 1000; 

        System.out.println("Starting timed challenge...");
        for (Flashcard flashcard : flashcards) {
            long elapsedTime = System.currentTimeMillis() - startTime;
            if (elapsedTime > timeLimit) {
                break;
            }

            System.out.println("What is the translation of: " + flashcard.word);
            String answer = scanner.nextLine().trim();

            if (answer.equalsIgnoreCase(flashcard.translation)) {
                correctAnswers++;
                user.increaseScore();
                flashcard.increaseMastery();
                System.out.println("Correct!\n");
            } else {
                System.out.println("Incorrect! The correct answer is: " + flashcard.translation + "\n");
            }
        }

        System.out.println("Timed challenge finished!");
        System.out.println("You got " + correctAnswers + " correct.");
        user.levelUp();
        System.out.println("You leveled up to level " + user.level);
    }
}
