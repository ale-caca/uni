README - Laborator 3 - Retea de Socializare

1.Mecanismul Factory si Trecerea la Baza de Date
Clasa RepositoryFactory(implementata dupa modelul Simple Factory) este locul din aplicatie responsabil de instantierea repo-urilor. Tipul de persistenta(fisier,baze de date,memorie) este citit in constructorul factory-ului din fisierul config.properties.
Pt trecerea la baza de date va trebui modificat doar fisierul, unde setez "repository.type=DATABAS".Restul componentelor (Service-urile,Consola Main-ul) vor ramane complet neschimbate, demonstrand decuplarea codului.

2.Tratarea fisierului lipsa si a liniilor corupte
- Fisier lipsa:in clasa AbstractFileRepository, daca la rulare fisierul .txt nu exista (FileNotFoundException), aplicatia nu se opreste, ci ignora eroarea. Repository-ul va porni fara date, iar fisierul text va fi creat automat la prima operatie de save.
- Linii corupte:in interiorul metodei loadData, citirea se face linie cu linie, iar logica de parsare este inclusa intr-un bloc try-catch. Daca o linie este corupta(de ex: un fisier are caractere in loc de un id numeric pentru o prietenie), exceptia este prinsa,dar aplicatia nu se opreste. In schimb, este printat un mesaj de eroare la consola (cu numarul liniei corupte si textul ei prin System.err.println), iar bucla trece la urmatoarea linie (continue). Am ales aceasta abordare deoarece o singura eroare minora introdusa de utilizator manual in fisierul text nu ar trebui sa impiedice incarcarea restului de date valide in aplicatie.

3. Sabloane de Proiectare (Design Patterns) utilizate
- Factory: Folosit in clasa `RepositoryFactory` pentru a centraliza crearea instantelor de Repository. Service-ul doar cere un repository si il primeste gata configurat, ascunzand logica de initializare.
- Template Method: Prezent in `AbstractFileRepository`. Metoda `loadData()` defineste scheletul incarcarii datelor din fisier (deschide fisierul, citeste, prinde exceptii), in timp ce sub-pasii de transformare (extractEntity, createEntityAsString) sunt abstractizati si lasati spre implementare subclaselor concrete `UtilizatorFileRepository` si `PrietenieFileRepository`.
- Decorator: Regasit in operatiile de I/O prin fluxurile de intrare/iesire. De exemplu, cand am folosit `new BufferedReader(new FileReader(fileName))`, BufferedReader este clasa care "decoreaza" FileReader pentru a-i oferi o noua functionalitate (citirea pe linii in loc de caractere), fara a modifica implementarea de baza.
- Strategy: Validatoarele (UserValidation, FriendshipValidation) care extind interfata Validation actioneaza precum niste "strategii" ce pot fi injectate la constructia obiectelor repository in memorie, decupland algoritmul de validare de logica de stocare.

4. Principii SOLID respectate
- SRP (Single Responsibility Principle): Clasele au responsabilitati unice si clare. De exemplu: validarile (Validation) doar valideaza, Repositories doar stocheaza si extrag date, Services contin doar logica de retea (grafuri, BFS), iar consola face exclusiv interactiunea I/O.
- OCP (Open-Closed Principle): Interfetele Repository si Service permit extinderea aplicatiei fara a altera codul curent existent. 
- LSP (Liskov Substitution Principle): Subclasele (`InMemoryRepository`, `AbstractFileRepository` sau implementarea finala de fisiere) pot fi substituite fara nicio problema prin tipul generic Repository<ID, E>. Service-ul functioneaza perfect indiferent ce instanta i se paseaza sub umbrela acelei interfete.
- DIP (Dependency Inversion Principle): Modulele de nivel inalt (clasa Console sau NetworkSv) nu depind de clase concrete (nu fac `new UtilizatorFileRepository`), ci depind doar de o abstractizare (interfata Service sau interfata Repository). Injectarea dependentelor se face de catre Main.
