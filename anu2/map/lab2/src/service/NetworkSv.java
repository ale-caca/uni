package service;

import domain.User;
import java.util.*;

/**
 * Clasa de service resposabila cu operatiile cu comunitati(grafuri)
 */
public class NetworkSv {
    private UserSv userService;

    public NetworkSv(UserSv userService) {
        this.userService = userService;
    }

    /**
     * Determina numarul de comunitati din retea (numarul de componente conexe din graf).
     * @return un numar intreg reprezentand numarul total de comunitati
     */
    public int getNumberOfCommunities() {
        Iterable<User> users = userService.findAll();
        Set<Long> visited = new HashSet<>();
        int communitiesCount = 0;

        for (User user : users) {
            if (!visited.contains(user.getId())) {
                communitiesCount++;
                bfs(user, visited);
            }
        }
        return communitiesCount;
    }

    // Parcurgere standard BFS pentru marcarea unei componente conexe

    /**
     * Parcurgere BFS pt gasirea unei componente conexe aka comunitati
     * @param startNode
     * @param visited
     */
    private void bfs(User startNode, Set<Long> visited) {
        Queue<User> queue = new LinkedList<>();
        queue.add(startNode);
        visited.add(startNode.getId());

        while (!queue.isEmpty()) {
            User current = queue.poll();
            List<User> friends = userService.getFriends(current.getId());
            for (User friend : friends) {
                if (!visited.contains(friend.getId())) {
                    visited.add(friend.getId());
                    queue.add(friend);
                }
            }
        }
    }

    /**
     * Determina cea mai sociabila comunitate (componenta conexa cu cel mai lung drum).
     * @return o lista cu utilizatorii care fac parte din cea mai sociabila comunitate
     */
    public List<User> getMostSociableCommunity() {
        Iterable<User> users = userService.findAll();
        Set<Long> visited = new HashSet<>();
        List<User> mostSociable = new ArrayList<>();
        int maxPathLength = -1;

        for (User user : users) {
            if (!visited.contains(user.getId())) {
                List<User> currentCommunity = new ArrayList<>();
                bfsCollect(user, visited, currentCommunity);

                //gasesc cel mai lung drum din comunitatea curenta
                int currentLongestPath = getLongestPathInCommunity(currentCommunity);

                if (currentLongestPath > maxPathLength) {
                    maxPathLength = currentLongestPath;
                    mostSociable = currentCommunity;
                }
            }
        }
        return mostSociable;
    }

    /**
     * Colecteaza toti userii dintr o comunitate prin BFS
     * @param startNode
     * @param visited
     * @param communityNodes
     */
    private void bfsCollect(User startNode, Set<Long> visited, List<User> communityNodes) {
        Queue<User> queue = new LinkedList<>();
        queue.add(startNode);
        visited.add(startNode.getId());
        communityNodes.add(startNode);

        while (!queue.isEmpty()) {
            User current = queue.poll();
            List<User> friends = userService.getFriends(current.getId());
            for (User friend : friends) {
                if (!visited.contains(friend.getId())) {
                    visited.add(friend.getId());
                    queue.add(friend);
                    communityNodes.add(friend);
                }
            }
        }
    }

    /**
     *Determina cel mai lung drum prnind BFS din fiecare nod al comunitatii
     * @param community
     * @return un numar intreg care reprezinta cel mai lung drum
     */
    private int getLongestPathInCommunity(List<User> community) {
        int maxPath = 0;
        for (User startNode : community) {
            int pathLength = bfsLongestPath(startNode);
            if (pathLength > maxPath) {
                maxPath = pathLength;
            }
        }
        return maxPath;
    }

    // BFS modificat pentru a returna distanța maximă de la startNode
    private int bfsLongestPath(User startNode) {
        Queue<User> queue = new LinkedList<>();
        Map<Long, Integer> distances = new HashMap<>();

        queue.add(startNode);
        distances.put(startNode.getId(), 0);
        int maxDist = 0;

        while (!queue.isEmpty()) {
            User current = queue.poll();
            int currentDist = distances.get(current.getId());

            List<User> friends = userService.getFriends(current.getId());
            for (User friend : friends) {
                if (!distances.containsKey(friend.getId())) {
                    distances.put(friend.getId(), currentDist + 1);
                    queue.add(friend);
                    maxDist = Math.max(maxDist, currentDist + 1);
                }
            }
        }
        return maxDist;
    }
}