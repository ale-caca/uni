================================================================================
                         RAPORT REFACTORIZARE - LABORATORUL 4 MAP
================================================================================

1. TABEL COMPARATIV ÎNAINTE / DUPĂ (BUCLE ÎNLOCUITE CU STREAM API)
--------------------------------------------------------------------------------
Clasa & Metoda          | Înainte (Lab 3)              | După (Lab 4) & Operații Stream folosite
------------------------+------------------------------+------------------------------------------------
UserSv                  | for (User user : users)      | StreamSupport.stream(findAll().spliterator())
.genererateID()         |   if (user.getId() > id)     |   .map(Entity::getId)              [interm]
                        |     id = user.getId();       |   .max(Long::compareTo)            [term]
                        |                              |   .map(id -> id + 1L).orElse(1L)
------------------------+------------------------------+------------------------------------------------
UserSv                  | for (Friendship f : all)     | StreamSupport.stream(friendshipRepo.findAll())
.delete(userId)         |   if (f.contains(userId))    |   .filter(f -> f.hasUser(userId))  [interm]
                        |     list.add(f.getId());     |   .map(Entity::getId)              [interm]
                        | for (Long fid : list) del;   |   .collect(Collectors.toList())    [term]
                        |                              | .forEach(friendshipRepo::delete);
------------------------+------------------------------+------------------------------------------------
UserSv                  | for (Friendship f : all)     | StreamSupport.stream(friendshipRepo.findAll())
.getFriends(id)         |   if (f.friend1.equals(id))  |   .filter(f -> f.has(id))          [interm]
                        |     friends.add(f.friend2);  |   .map(f -> getOtherId(f, id))     [interm]
                        |                              |   .map(repository::findOne)        [interm]
                        |                              |   .filter(Objects::nonNull)        [interm]
                        |                              |   .collect(Collectors.toList());   [term]
------------------------+------------------------------+------------------------------------------------
FriendshipSv            | for (User u : userRepo)      | StreamSupport.stream(userRepo.findAll())
.deleteByNames(...)     |   if (u.matchName) ...       |   .filter(u -> match(u, name1))   [interm]
                        | for (Friendship f : all)     |   .findFirst()                     [term]
                        |   if (f.matchUsers) del;     |   .orElseThrow(...)
------------------------+------------------------------+------------------------------------------------
NetworkSv               | for (User start : community) | community.stream()
.getLongestPathIn...()  |   if (dist > max) max=dist;  |   .mapToInt(this::bfsLongestPath)  [interm]
                        |                              |   .max()                           [term]
                        |                              |   .orElse(0);
------------------------+------------------------------+------------------------------------------------
NetworkSv               | int maxPath = -1;            | getAllCommunities().stream()
.getMostSociable...()   | for (User user : users)      |   .max(Comparator.comparingInt(   [term]
                        |   if (len > max) max=len;    |       this::getLongestPathIn...))
                        |                              |   .orElseGet(ArrayList::new);
------------------------+------------------------------+------------------------------------------------
FileRepository          | while ((line = reader.readLine())| try (Stream<String> s = Files.lines(path))
.loadData()             |     super.save(extract(l));  | IntStream.range(0, lines.size())   [interm]
                        |                              |   .forEach(i -> parse(lines.get(i))); [term]
------------------------+------------------------------+------------------------------------------------
Console                 | for (User u : allUsers)      | StreamSupport.stream(findAll().spliterator())
.showUsers()            |   System.out.println(u);     |   .forEach(System.out::println);   [term]
--------------------------------------------------------------------------------

2. INTERFEȚE FUNCȚIONALE UTILIZATE ȘI LOCAȚIILE LOR
--------------------------------------------------------------------------------
- Validation<T> (@FunctionalInterface): interfață funcțională custom; extinsă cu
  metoda default andThen(). În Main, s-a creat un validator suplimentar ca expresie
  lambda: user -> user.getFirstName().chars().allMatch(Character::isLetter).
- Predicate<T>:
  * În Repository.findAll(Predicate<E> filter).
  * În UserCriteria: metodele statice nameStartsWith, hasLastName, hasFirstName
    întorc Predicate<User> și sunt combinate cu and/or/negate.
  * În Stream API: filter(Predicate), anyMatch(Predicate), allMatch(Predicate).
- Function<T, R>:
  * În FileRepository: lineToEntity (Function<String, E>) și entityToLine (Function<E, String>).
  * În RepositoryFactory: Map<RepositoryType, Function<Validation<E>, Repository<ID, E>>>.
  * În Stream API: map(Function).
- Consumer<T>:
  * În Stream API: forEach(Consumer).
  * În Optional: ifPresent(Consumer).
- Supplier<T>:
  * În Optional: orElseGet(Supplier), orElseThrow(Supplier).
  * În RepositoryFactory (la instanțierea depozitelor).
- Comparator<T>:
  * În NetworkSv.getMostSociableCommunity(): Comparator.comparingInt(...).

3. REFERINȚE LA METODE (TOATE CELE 4 TIPURI)
--------------------------------------------------------------------------------
1. Metodă statică:
   - Long::compareTo (în UserSv.genererateID)
   - Objects::nonNull (în UserSv.getFriends)
   - Character::isLetter (în lambda-ul de validare din Main)
   - UserCriteria::hasLongName (în Console.filterUsersByCriteria)
   - User::fromString, User::toString (în RepositoryFactory)
2. Metodă de instanță a unui obiect oarecare:
   - Entity::getId (în UserSv.genererateID și UserSv.delete)
   - User::getFirstName (în predicatele de comparare)
3. Metodă de instanță a unui obiect specific:
   - System.out::println (în Console.showUsers, Console.showBiggestCommunity)
   - friendshipRepository::delete (în UserSv.delete)
   - this::getLongestPathInCommunity (în NetworkSv.getMostSociableCommunity)
   - this::bfsLongestPath (în NetworkSv.getLongestPathInCommunity)
4. Constructor:
   - InMemoryRepository::new (în RepositoryFactory)
   - ArrayList::new (în NetworkSv.getMostSociableCommunity prin orElseGet)

4. DECIZII DE PROIECTARE
--------------------------------------------------------------------------------
a) Optional în Repository vs. Service:
   S-a decis păstrarea returnării de valori directe/null în interfața Repository<ID, E>,
   iar împachetarea consecventă în Optional se realizează la nivelul stratului Service
   (ex. Optional.ofNullable(repository.findOne(id))).
   Justificare:
   - Separarea responsabilităților: Repository-ul este un mecanism intern de persistență CRUD.
   - Stratul Service reprezintă logica de domeniu (business logic), unde noțiunea de
     "valoare absentă" are semnificație semantică directă și este tratată prin
     aruncarea de excepții (orElseThrow) sau acțiuni alternative (ifPresentOrElse, orElseGet).
   - Se evită alocarea inutilă a obiectelor wrapper Optional în operațiile interne din repo.

b) Compoziție (Function) vs. Moștenire (Template Method) în persistența pe fișier:
   S-a renunțat la AbstractFileRepository și la subclasele sale în favoarea unei
   singure clase FileRepository<ID, E>, care primește prin constructor două funcții:
   Function<String, E> și Function<E, String>.
   Justificare:
   - Respectă principiul fundamental OOP "Favor composition over inheritance".
   - Elimină duplicarea de clase derivate (class explosion): nu mai este necesară
     crearea de clase separate doar pentru a suprascrie două metode mici de parsare.
   - Permite transmiterea directă a referințelor la metode (User::fromString, User::toString).

5. BUCLE PĂSTRATE ȘI JUSTIFICAREA LOR
--------------------------------------------------------------------------------
Singurele bucle clasice menținute sunt:
1. `while (condition)` în Main: meniul interactiv de citire de la tastatură (consolă),
   care nu reprezintă parcurgerea unei colecții de date, ci un loop clasic de evenimente.
2. `while (!queue.isEmpty())` în algoritmul BFS din NetworkSv: o buclă necesară pentru
   gestionarea cozii de explorare dinamică a grafului. Coada se populează în timpul
   parcurgerii, astfel încât o structură iterativă clasică este incomparabil mai curată
   și eficientă decât forțarea unei recursivități artificiale. Toate parcurgerile de
   vecini din interiorul acestui while au fost însă refactorizate cu Stream API.
================================================================================