package UI;

import domain.Friendship;
import domain.User;
import domain.validator.ValidationException;
import repository.RepositoryException;
import service.FriendshipSv;
import service.NetworkSv;
import service.UserSv;
import util.paging.Page;
import util.paging.Pageable;
import java.util.function.Function;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.function.Predicate;
import java.util.stream.StreamSupport;

public class Console {
    private final UserSv userService;
    private final FriendshipSv friendshipService;
    private final NetworkSv networkService;
    private final int pageSize;
    private final Scanner scanner=new Scanner(System.in);

    public Console(UserSv userService, FriendshipSv friendshipService, NetworkSv networkService,int pageSize) {
        this.userService = userService;
        this.friendshipService = friendshipService;
        this.networkService = networkService;
        this.pageSize=pageSize;
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

    /**
     * Displays all users in a paginated interactive console view.
     * Falls back to full listing if the configured repository does not support pagination.
     */
    public void showUsers() {
        try {
            paginateUsers("Lista utilizatorilor", userService::findAllOnPage);
        } catch (UnsupportedOperationException e) {
            System.out.println("Lista tuturor utilizatorilor (fara paginare):");
            StreamSupport.stream(userService.findAll().spliterator(), false)
                    .forEach(System.out::println);
            System.out.println();
        }
    }

    /**
     * Prompts for a user ID and displays that user's friends in a paginated interactive view.
     */
    public void showUserFriends() {
        System.out.print("Introdu ID-ul utilizatorului: ");
        String input = scanner.nextLine().trim();
        try {
            Long userId = Long.parseLong(input);
            User targetUser = userService.findOne(userId)
                    .orElseThrow(() -> new ValidationException("Utilizatorul cu ID " + userId + " nu exista!"));

            String title = "Prietenii lui " + targetUser.getFirstName() + " " + targetUser.getLastName();
            paginateUsers(title, pageable -> userService.findFriendsOnPage(userId, pageable));
        } catch (NumberFormatException e) {
            System.out.println("ID invalid! Te rog introdu un numar.\n");
        } catch (ValidationException | RepositoryException | IllegalArgumentException | UnsupportedOperationException e) {
            System.out.println("Eroare: " + e.getMessage() + "\n");
        }
    }

    /**
     * Runs an interactive pagination loop supporting forward/backward navigation and direct page jumping.
     *
     * @param title      the header displayed above each page
     * @param pageLoader function that loads a {@link Page} of users for a given {@link Pageable}
     */
    private void paginateUsers(String title, Function<Pageable, Page<User>> pageLoader) {
        int currentPage = 0;

        while (true) {
            Page<User> page;
            try {
                page = pageLoader.apply(new Pageable(currentPage, pageSize));
            } catch (IllegalArgumentException e) {
                System.out.println("Eroare paginare: " + e.getMessage());
                currentPage = 0;
                continue;
            }

            int totalElements = page.getTotalNumberOfElements();
            int totalPages = (int) Math.ceil((double) totalElements / pageSize);

            System.out.println("\n=== " + title + " ===");
            if (totalElements == 0) {
                System.out.println("Nu exista inregistrari de afisat. (Pagina 0 din 0)\n");
                return;
            }

            if (page.getElementsOnPage().isEmpty()) {
                System.out.println("(Pagina este goala)");
            } else {
                page.getElementsOnPage().forEach(System.out::println);
            }

            System.out.println("Pagina " + (currentPage + 1) + " din " + totalPages + " (Total: " + totalElements + ")");
            System.out.print("[n] Inainte | [p] Inapoi | [g] Mergi la pagina | [q] Iesire: ");
            String option = scanner.nextLine().trim().toLowerCase();

            switch (option) {
                case "n":
                    if (currentPage + 1 < totalPages) {
                        currentPage++;
                    } else {
                        System.out.println("Esti deja pe ultima pagina!");
                    }
                    break;
                case "p":
                    if (currentPage > 0) {
                        currentPage--;
                    } else {
                        System.out.println("Esti deja pe prima pagina!");
                    }
                    break;
                case "g":
                    System.out.print("Introdu numarul paginii (1-" + totalPages + "): ");
                    try {
                        int targetPage = Integer.parseInt(scanner.nextLine().trim());
                        currentPage = targetPage - 1;
                    } catch (NumberFormatException e) {
                        System.out.println("Numar de pagina invalid!");
                    }
                    break;
                case "q":
                    System.out.println();
                    return;
                default:
                    System.out.println("Optiune invalida!");
                    break;
            }
        }
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
        System.out.println("9. Show User's Friends (Paginated)");
        System.out.println("10. Exit");
    }
}