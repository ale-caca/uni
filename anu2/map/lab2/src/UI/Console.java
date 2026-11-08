package UI;

import domain.User;
import domain.validator.ValidationException;
import service.FriendshipSv;
import service.UserSv;
import domain.Friendship;
import service.NetworkSv;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class Console {
    private UserSv userService;
    private FriendshipSv friendshipService;
    private NetworkSv networkService;

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
        for (User u : community) {
            System.out.println(u.getId() + " " + u.getFirstName() + " " + u.getLastName());
        }
        System.out.println();
    }

    public void addUser() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Please enter user's first name: ");
        String firstName = scanner.nextLine();
        System.out.print("Please enter user's last name: ");
        String lastName = scanner.nextLine();

        try {
            userService.save(firstName, lastName);
            System.out.print("User was added successfully!\n");
        } catch (ValidationException e) {
            System.out.println(e.getMessage());
        }
    }

    public void addFriendship() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Please enter first's user ID: ");
        Long userId1 = scanner.nextLong();
        System.out.print("Please enter second's user ID: ");
        Long userId2 = scanner.nextLong();
        try {
            friendshipService.save(userId1, userId2);
        } catch (ValidationException e) {
            System.out.println(e.getMessage());
        }
    }

    public void removeUser() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Please enter user's ID: ");
        Long userId1 = scanner.nextLong();
        try {
            userService.delete(userId1);
            System.out.print("User was deleted successfully!\n");
        } catch (ValidationException e) {
            System.out.println(e.getMessage());
        }
    }

    public void removeFriendship() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter friendship's ID: ");
        Long friendshipID = scanner.nextLong();
        try {
            friendshipService.delete(friendshipID);
            System.out.print("Friendship with ID " + friendshipID + " was deleted successfully!\n");
        } catch (ValidationException e) {
            System.out.println(e.getMessage());
        }
    }

    public void showUsers() {
        Iterable<User> allUsers = userService.findAll();
        for (User user : allUsers) {
            System.out.println(user.getId() + " " + user.getFirstName() + " " + user.getLastName() + "\n");
        }

    }

    public void showFriendships() {
        Iterable<Friendship> allFriendships = friendshipService.findAll();
        System.out.println("Lista tuturor prieteniilor:");
        for (Friendship f : allFriendships) {
            System.out.println("Id: " + f.getId() +
                " | " + f.getFriend1().getFirstName() + " " + f.getFriend1().getLastName() +
                " <-> " + f.getFriend2().getFirstName() + " " + f.getFriend2().getLastName());
    }
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
        ;
    }
}