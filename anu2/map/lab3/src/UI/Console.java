package UI;

import domain.Friendship;
import domain.User;
import domain.validator.ValidationException;
import repository.RepositoryException;
import service.FriendshipSv;
import service.NetworkSv;
import service.UserCriteria;
import service.UserSv;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.function.Predicate;
import java.util.stream.StreamSupport;

public class Console {
    private final UserSv userService;
    private final FriendshipSv friendshipService;
    private final NetworkSv networkService;

    public Console(UserSv userService, FriendshipSv friendshipService, NetworkSv networkService) {
        this.userService = userService;
        this.friendshipService = friendshipService;
        this.networkService = networkService;
    }

    public void showNrCommunities() {
        int nr = networkService.getNumberOfCommunities();
        System.out.println("Numarul total de comunitati este: " + nr + "\n");
    }

    public void showBiggestCommunity() {
        List<User> community = networkService.getMostSociableCommunity();
        System.out.println("Cea mai sociabila comunitate este formata din:");
        community.forEach(System.out::println);
        System.out.println();
    }

    public void addUser() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Please enter user's first name: ");
        String firstName = scanner.nextLine();
        System.out.print("Please enter user's last name: ");
        String lastName = scanner.nextLine();

        try {
            User savedUser = userService.save(firstName, lastName);
            System.out.println("User was added successfully with ID: " + savedUser.getId() + "\n");
        } catch (ValidationException | RepositoryException | IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    public void addFriendship() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Please enter first user's first name: ");
        String fn1 = scanner.nextLine();
        System.out.print("Please enter first user's last name: ");
        String ln1 = scanner.nextLine();

        System.out.print("Please enter second user's first name: ");
        String fn2 = scanner.nextLine();
        System.out.print("Please enter second user's last name: ");
        String ln2 = scanner.nextLine();

        try {
            friendshipService.saveByNames(fn1, ln1, fn2, ln2);
            System.out.println("Friendship added successfully!");
        } catch (ValidationException | RepositoryException | IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    public void removeUser() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Please enter user's first name: ");
        String firstName = scanner.nextLine();
        System.out.print("Please enter user's last name: ");
        String lastName = scanner.nextLine();

        try {
            userService.deleteByName(firstName, lastName).ifPresentOrElse(
                    u -> System.out.print("User " + u.getFirstName() + " " + u.getLastName() + " was deleted successfully!\n"),
                    () -> System.out.println("Utilizatorul " + firstName + " " + lastName + " nu a fost gasit!")
            );
        } catch (ValidationException | RepositoryException | IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    public void removeFriendship() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Please enter first user's first name: ");
        String fn1 = scanner.nextLine();
        System.out.print("Please enter first user's last name: ");
        String ln1 = scanner.nextLine();

        System.out.print("Please enter second user's first name: ");
        String fn2 = scanner.nextLine();
        System.out.print("Please enter second user's last name: ");
        String ln2 = scanner.nextLine();

        try {
            friendshipService.deleteByNames(fn1, ln1, fn2, ln2);
            System.out.print("Friendship between " + fn1 + " " + ln1 + " and " + fn2 + " " + ln2 + " was deleted successfully!\n");
        } catch (ValidationException | RepositoryException e) {
            System.out.println(e.getMessage());
        }
    }

    public void showUsers() {
        System.out.println("Lista tuturor utilizatorilor:");
        StreamSupport.stream(userService.findAll().spliterator(), false)
                .forEach(System.out::println);
        System.out.println();
    }

    public void showFriendships() {
        System.out.println("Lista tuturor prieteniilor:");
        StreamSupport.stream(friendshipService.findAll().spliterator(), false)
                .forEach(f -> {
                    User u1 = userService.findOne(f.getFriend1().getId()).orElse(f.getFriend1());
                    User u2 = userService.findOne(f.getFriend2().getId()).orElse(f.getFriend2());
                    System.out.println("Id: " + f.getId() +
                            " | " + u1.getFirstName() + " " + u1.getLastName() +
                            " <-> " + u2.getFirstName() + " " + u2.getLastName() +
                            " | Data: " + f.getDate());
                });
        System.out.println();
    }


    public void menu() {
        System.out.println("1. Add User");
        System.out.println("2. Add Friendship");
        System.out.println("3. Remove User");
        System.out.println("4. Remove Friendship");
        System.out.println("5. Show Users");
        System.out.println("6. Show Friendships");
        System.out.println("7. Nr communities");
        System.out.println("8. Show biggest community");
        System.out.println("10. Exit");
    }
}