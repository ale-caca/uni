package service;

import domain.User;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Service class responsible for graph and community network analytics.
 */
public class NetworkSv {
    private final UserSv userService;

    public NetworkSv(UserSv userService) {
        this.userService = userService;
    }

    /**
     * Determines all connected components (communities) in the user friendship graph.
     *
     * @return a list of communities, each community being a list of users
     */
    public List<List<User>> getAllCommunities() {
        List<User> allUsers = StreamSupport.stream(userService.findAll().spliterator(), false)
                .collect(Collectors.toList());
        Set<Long> visited = new HashSet<>();
        List<List<User>> communities = new ArrayList<>();

        allUsers.stream()
                .filter(user -> !visited.contains(user.getId()))
                .forEach(user -> {
                    if (!visited.contains(user.getId())) {
                        communities.add(bfsCollect(user, visited));
                    }
                });

        return communities;
    }

    /**
     * Determines the total number of communities.
     *
     * @return total community count
     */
    public int getNumberOfCommunities() {
        return getAllCommunities().size();
    }

    /**
     * Collects all users in a connected component using BFS with stream neighbor processing.
     */
    private List<User> bfsCollect(User startNode, Set<Long> visited) {
        Queue<User> queue = new LinkedList<>();
        List<User> community = new ArrayList<>();

        queue.add(startNode);
        visited.add(startNode.getId());
        community.add(startNode);

        while (!queue.isEmpty()) {
            User current = queue.poll();
            userService.getFriends(current.getId()).stream()
                    .filter(friend -> !visited.contains(friend.getId()))
                    .forEach(friend -> {
                        visited.add(friend.getId());
                        queue.add(friend);
                        community.add(friend);
                    });
        }
        return community;
    }

    /**
     * Determines the most sociable community (the one containing the longest path).
     *
     * @return the list of users forming the most sociable community
     */
    public List<User> getMostSociableCommunity() {
        return getAllCommunities().stream()
                .max(Comparator.comparingInt(this::getLongestPathInCommunity))
                .orElseGet(ArrayList::new);
    }

    private int getLongestPathInCommunity(List<User> community) {
        return community.stream()
                .mapToInt(this::bfsLongestPath)
                .max()
                .orElse(0);
    }

    private int bfsLongestPath(User startNode) {
        Queue<User> queue = new LinkedList<>();
        Map<Long, Integer> distances = new HashMap<>();

        queue.add(startNode);
        distances.put(startNode.getId(), 0);

        while (!queue.isEmpty()) {
            User current = queue.poll();
            int currentDist = distances.get(current.getId());

            userService.getFriends(current.getId()).stream()
                    .filter(friend -> !distances.containsKey(friend.getId()))
                    .forEach(friend -> {
                        distances.put(friend.getId(), currentDist + 1);
                        queue.add(friend);
                    });
        }

        return distances.values().stream()
                .mapToInt(Integer::intValue)
                .max()
                .orElse(0);
    }
}