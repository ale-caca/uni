
import domain.validator.FriendshipValidation;
import domain.validator.UserValidation;
import service.FriendshipSv;
import service.NetworkSv;
import service.UserSv;
import UI.Console;
import repository.InMemoryRepository;
import domain.User;
import domain.Friendship;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try {
            UserValidation userValidator = new UserValidation();
            FriendshipValidation friendshipValidator = new FriendshipValidation();

            InMemoryRepository<Long, User> userRepository = new InMemoryRepository<>(userValidator);
            InMemoryRepository<Long, Friendship> friendshipRepository = new InMemoryRepository<>(friendshipValidator);

            UserSv userService = new UserSv(userRepository, friendshipRepository);
            FriendshipSv friendshipService = new FriendshipSv(friendshipRepository, userRepository);
            NetworkSv networkSv = new NetworkSv(userService);

            Console console = new Console(userService, friendshipService, networkSv);

            initTestEntities(userService, friendshipService);

            Scanner scanner = new Scanner(System.in);
            boolean condition = true;
            while (condition) {
                console.menu();
                System.out.print("Introduce your option: ");
                int option = scanner.nextInt();
                switch (option) {
                    case 1:
                        console.addUser();
                        break;
                    case 2:
                        console.addFriendship();
                        break;
                    case 3:
                        console.removeUser();
                        break;
                    case 4:
                        console.removeFriendship();
                        break;
                    case 5:
                        console.showUsers();
                        break;
                    case 6:
                        console.showFriendships();
                        break;
                    case 7:
                        console.showNrCommunities();
                        break;
                    case 8:
                        console.showBiggestCommunity();
                        break;
                    case 10:
                        condition=false;
                        break;

                    default:
                        System.out.print("Introduce a valid option!\n");
                        break;

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void initTestEntities(UserSv userService, FriendshipSv friendshipService) {
        try {
            userService.save("Alice", "Popescu"); // va primi ID 1
            userService.save("Bob", "Ionescu");   // va primi ID 2
            userService.save("Charlie", "Stan");  // va primi ID 3
            userService.save("Diana", "Radu");    // va primi ID 4

            friendshipService.save(1L, 2L);
            friendshipService.save(2L, 3L);
            friendshipService.save(3L, 4L);
            userService.save("Eve", "Marin");     // va primi ID 5
            userService.save("Frank", "Toma");    // va primi ID 6

            friendshipService.save(5L, 6L);

            userService.save("George", "Rusu");   // va primi ID 7

            System.out.println("Datele de test au fost initializate cu succes!\n");
        } catch (Exception e) {
            System.out.println("Eroare la initializarea datelor: " + e.getMessage());
        }
    }

}
