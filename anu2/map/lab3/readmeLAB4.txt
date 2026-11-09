RAPORT REFACTORIZARE-LABORATORUL 4 MAP

1. COMPARATII INAINTE VS DUPA

Aici sunt metodele unde am inlocuit for-urile si while-urile cu operatii pe stream-uri:

Clasa si metoda: UserSv.genererateID()
Inainte:
for (User user : users)
if (user.getId() > id)
id = user.getId();
Dupa:
StreamSupport.stream(findAll().spliterator())
.map(Entity::getId)
.max(Long::compareTo)
.map(id -> id + 1L).orElse(1L)
In loc sa parcurg lista pas cu pas si sa retin id-ul maxim intr-o variabila,
am transformat totul intr-un stream, am extras id-urile cu map, am luat maximul si
am rezolvat si cazul in care nu exista useri prin orElse.

Clasa si metoda: UserSv.delete(userId)
Inainte:
for (Friendship f : all)
if (f.contains(userId))
list.add(f.getId());
for (Long fid : list) del;
Dupa:
StreamSupport.stream(friendshipRepo.findAll())
.filter(f -> f.hasUser(userId))
.map(Entity::getId)
.collect(Collectors.toList())
.forEach(friendshipRepo::delete);
Inainte colectam id-urile prieteniilor intr-o lista auxiliara cu un for si
apoi le stergeam cu alt for. Acum filtrez direct prieteniile userului, le iau id-ul si sterg
totul mai curat.

Inainte:UserSv.getFriends(id)
for (Friendship f : all)
if (f.friend1.equals(id))
friends.add(f.friend2);
Dupa:
StreamSupport.stream(friendshipRepo.findAll())
.filter(f -> f.has(id))
.map(f -> getOtherId(f, id)
.map(repository::findOne)
.filter(Objects::nonNull)
.collect(Collectors.toList());
Am facut un pipeline intreg: filtrez prieteniile relevante, iau id-ul celuilalt utilizator,
caut entitatea in repository, filtrez eventualele valori null si adun rezultatele intr-o lista.

FriendshipSv.deleteByNames(...)
Inainte:
for (User u : userRepo)
if (u.matchName) ...
for (Friendship f : all)
if (f.matchUsers) del;
Dupa:
StreamSupport.stream(userRepo.findAll())
.filter(u -> match(u, name1))
.findFirst()
.orElseThrow(...)
Am scapat de iteratiile manuale pentru gasirea userilor dupa nume. Caut primul element care indeplineste conditia, iar daca nu exista arunc exceptie direct.

NetworkSv.getLongestPathIn...()
Inainte:
for (User start : community)
if (dist > max) max=dist;
Dupa:
community.stream()
.mapToInt(this::bfsLongestPath)
.max()
.orElse(0);
Am mapat direct fiecare user din comunitate la distanta maxima calculata cu BFS si am extras valoarea maxima folosind metodele din IntStream.

NetworkSv.getMostSociable...()
Inainte:
int maxPath = -1;
for (User user : users)
if (len > max) max=len;
Dupa:
getAllCommunities().stream()
.max(Comparator.comparingInt(
this::getLongestPathIn...))
.orElseGet(ArrayList::new);
Am inlocuit logica clasica cu o variabila temporara maxPath printr-un stream simplu peste comunitati, gasind maximul cu ajutorul unui comparator.

FileRepository.loadData()
Inainte:
while ((line = reader.readLine())
super.save(extract(l));
Dupa:
try (Stream s = Files.lines(path))
IntStream.range(0, lines.size())
.forEach(i -> parse(lines.get(i)));
Am renuntat la citirea clasica linie cu linie intr-un while si am folosit Files.lines
combinat cu IntStream pentru procesare.

Console.showUsers()
Inainte:
for (User u : allUsers)
System.out.println(u);
Dupa:
StreamSupport.stream(findAll().spliterator())
.forEach(System.out::println);
Am inlocuit bucla for clasica de afisare cu un forEach direct pe stream,
trimitand referinta catre println.

2.INTERFETE FUNCTIONALE UTILIZATE SI LOCATIILE LOR

 Validation (@FunctionalInterface):
Interfata proprie pentru validari, la care am pus si o metoda default andThen() ca sa pot inlantui verificari.
De exemplu, in Main am facut un validator printr-o expresie lambda scurta:
user -> user.getFirstName().chars().allMatch(Character::isLetter)
 Predicate:
* L am pus ca parametru in Repository.findAll(Predicate filter) ca sa putem filtra datele usor
* In clasa UserCriteria am metode statice precum nameStartsWith, hasLastName, hasFirstName care intorc un Predicate,
pe care le combin dupa cum am nevoie cu and, or sau negate.
* In operatiile pe stream-uri unde trebuiau puse conditii: filter(Predicate), anyMatch(Predicate), allMatch(Predicate).


* Function<T, R>:
* In FileRepository primim la initializare functiile lineToEntity (Function<String, E>) si entityToLine (Function<E, String>) pentru parsare si scriere.
* In RepositoryFactory avem Map<RepositoryType, Function<Validation, Repository<ID, E>>> ca sa instantiem depozitul potrivit.
* In stream-uri, la transformarile de date prin map(Function).


* Consumer:
* La finalul stream-urilor cand facem operatii finale: forEach(Consumer).
* Pe obiecte de tip Optional, folosind ifPresent(Consumer).


* Supplier:
* La metodele de pe Optional care cer o valoare de rezerva sau o exceptie: orElseGet(Supplier), orElseThrow(Supplier).
* In RepositoryFactory cand avem nevoie de instante noi de depozite.


* Comparator:
* In NetworkSv.getMostSociableCommunity() cand am folosit Comparator.comparingInt(...) ca sa compar comunitatile dupa lungimea drumului.


3. REFERINTE LA METODE

1. Metoda statica:
* Long::compareTo (in UserSv.genererateID)
* Objects::nonNull (in UserSv.getFriends)
* Character::isLetter (in lambda-ul de validare din Main)
* UserCriteria::hasLongName (in Console.filterUsersByCriteria)
* User::fromString, User::toString (in RepositoryFactory)


2. Metoda de instanta a unui obiect oarecare:
* Entity::getId (in UserSv.genererateID si UserSv.delete)
* User::getFirstName (in predicatele de comparare)


3. Metoda de instanta a unui obiect specific:
* System.out::println (in Console.showUsers, Console.showBiggestCommunity)
* friendshipRepository::delete (in UserSv.delete)
* this::getLongestPathInCommunity (in NetworkSv.getMostSociableCommunity)
* this::bfsLongestPath (in NetworkSv.getLongestPathInCommunity)


4. Constructor:
* InMemoryRepository::new (in RepositoryFactory)
* ArrayList::new (in NetworkSv.getMostSociableCommunity apelat in orElseGet)


4. DECIZII DE PROIECTARE


a) Optional in Repository vs. Service:
Am ales sa las metodele din interfata Repository<ID, E> sa returneze direct entitatea sau null, iar impachetarea in Optional sa se faca doar la nivelul clasei Service (cum ar fi Optional.ofNullable(repository.findOne(id))).
De ce am facut asa:

* Separarea clara a rolurilor: repo-ul e gandit doar pentru operatiile de baza CRUD pe stocare.
* Nivelul de Service este cel care stie regulile aplicatiei (business logic). Aici lipsa unei valori inseamna ceva anume si tot aici decidem daca aruncam o exceptie cu orElseThrow sau dam o valoare implicita prin orElseGet.
* Evitam sa cream obiecte Optional in plus la fiecare apel intern din interiorul depozitului daca nu e nevoie de ele acolo.

b) Compozitie (Function) vs. Mostenire (Template Method) la salvarea in fisier:
Am sters AbstractFileRepository si clasele copil care mosteneau din el si am facut o singura clasa generica FileRepository<ID, E>. Aceasta primeste direct prin constructor doua functii: Function<String, E> si Function<E, String>.
De ce am facut asa:

* E o aplicare clara a principiului "favor composition over inheritance".
* Nu mai umplem proiectul cu clase derivate doar pentru a da override la doua metode simple de citire si scriere a liniilor.
* Putem pasa direct referinte la metode la creare (cum sunt User::fromString si User::toString), codul fiind mult mai scurt si usor de citit.


5. BUCLE PASTRATE SI JUSTIFICAREA LOR


Am lasat bucle clasice doar in doua locuri unde chiar aveau sens:

1. `while (condition)` in Main: este bucla clasica pentru meniul din consola, unde asteptam comenzi de la tastatura. Aici nu parcurgem o colectie din memorie, ci gestionam interactiunea cu utilizatorul.
2. `while (!queue.isEmpty())` in algoritmul BFS din NetworkSv: este bucla de baza pentru explorarea grafului cu o coada. Deoarece coada se modifica continuu cat timp parcurgem graful, bucla while e mult mai clara si mai naturala decat daca am fi incercat sa fortam o recursivitate artificiala sau un stream complicat. Totusi, parcurgerea vecinilor din interiorul acestei bucle a fost trecuta pe Stream API.
================================================================================