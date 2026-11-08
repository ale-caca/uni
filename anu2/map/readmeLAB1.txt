LAB1 map readme

Single Responsibility Principle
Fiecare clasa din proiectul meu arev o singura responsabilitate clara. In diagrama UML acest lucru este vizibil pentru ca casutele  sunt bine delimitate, iar in cod de exemplu clasele de operatii ( AddOp,SubOp,etc) contin strict logica de calcul pt operatorul lor.

Open/Closed Principle
Aplicatia este deschisa pentru extindere, dar inchisa pentru modificare. Interfata Operation actioneaza ca un punct de extensie cum se vede si in UML.
Daca doresc sa adaug o operatie noua nu trebuie sa modific Main-ul sau parser-ul, trebuie doar sa creez o clasa noua (sa desenez o casuta noua in UML cu sageata punctata catre Operation) si sa adaug cazul respectiv in OperationFactory. Clasa Main si restul codului raman nemodificate.

Dependency Inversion Principle
In clasa Main, nu depind de implementari concrete. Folosesc interfata exprParser pentru a obtine datele si interfata Operation pentru a evalua expresia. In diagrama, acest principiu reiese clar din directia sagetilor. Asa Main-ul este decuplat de detaliile de implementare (SimpleParser sau AddOp) si asa pot schimba lucruri in main ( de ex cum citesc datele) fara sa afectez logica din spate.

Sablonul Factory
Am construit clasa OperationFactory pentru a nu aglomera parser-ul cu instantierea claselor de operatii.In diagrama UML clasa contine metoda createOperation care este responssabila sa preia semnul (+,-,*,/) si sa creeze un obiect pe care sa-l returneze. Astfel in Main sau in parsel nu se stie nimic despre clase ca AddOp sau SubOp.

Sablonul Strategy
Am o interfata Operation cu metoda execute() si strategii precum AddOp si SubOp ( ca in diagrama).In timpul itetarii prin vectorul de operatii in Main se apeleaza metoda execute() si ,idiferent de operatie, codul se executa fara sa fie nevoie de mai multe detalii (sper ca are sens).Fiecare clasa stie ce sa faca,are "strategia" proprie si nu mai e nevoie in Main sa dau toate delatiile de ce si cum trebuie facut.