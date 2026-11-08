import domain.Friendship;
import domain.User;
import domain.validator.FriendshipValidation;
import domain.validator.UserValidation;
import domain.validator.Validation;
import domain.validator.ValidationException;
import repository.Repository;
import repository.RepositoryException;
import repository.RepositoryFactory;
import service.FriendshipSv;
import service.NetworkSv;
import service.UserSv;
import UI.Console;

import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // Validatorul de baza pentru User
        Validation<User> baseUserValidator = new UserValidation();

        // Cerinta 1: Regula noua scrisa ca lambda si compusa prin andThen, fara modificarea clasei UserValidation
        Validation<User> nameLetterValidator = user -> {
            boolean firstNameValid = user.getFirstName().chars().allMatch(Character::isLetter);
            boolean lastNameValid = user.getLastName().chars().allMatch(Character::isLetter);
            if (!firstNameValid || !lastNameValid) {
                throw new ValidationException("Numele si prenumele trebuie sa contina exclusiv litere!");
            }
        };

        // Compunerea validatorilor
        Validation<User> userValidator = baseUserValidator.andThen(nameLetterValidator);

        // Validatorul pentru Friendship
        Validation<Friendship> friendshipValidator = new FriendshipValidation();

        // RepositoryFactory cu Map si Supplier / Function (fara switch)
        RepositoryFactory factory = new RepositoryFactory();

        Repository<Long, User> userRepository = factory.createUtilizatorRepository(userValidator);
        Repository<Long, Friendship> friendshipRepository = factory.createPrietenieRepository(friendshipValidator);

        UserSv userService = new UserSv(userRepository, friendshipRepository);
        FriendshipSv friendshipService = new FriendshipSv(friendshipRepository, userRepository);
        NetworkSv networkSv = new NetworkSv(userService);

        Console console = new Console(userService, friendshipService, networkSv);

        Scanner scanner = new Scanner(System.in);
        boolean condition = true;
        while (condition) {
            console.menu();
            System.out.print("Introduce your option: ");
            try {
                int option = scanner.nextInt();
                scanner.nextLine();
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
                    case 9:
                        console.filterUsersByCriteria();
                        break;
                    case 10:
                        condition = false;
                        break;
                    default:
                        System.out.print("Introduce a valid option!\n");
                        break;
                }
            } catch (InputMismatchException e) {
                System.out.println("Please enter a valid option!\n");
                scanner.nextLine();
            } catch (RepositoryException e) {
                System.out.println(e.getMessage());
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }
}