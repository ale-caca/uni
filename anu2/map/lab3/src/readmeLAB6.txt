Proiect Social Network-Laborator 6

In aceasta etapa am adaugat mecanismul de paginare pentru datele din baza de date PostgreSQL,
respectand cerintele de arhitectura din laborator:

Pachetul util.paging:
Pageable: Clasa care retine parametrii cererii de paginare (pageNumber - indexat de la 0, pageSize).
Page<E>: Container pentru raspuns care retine lista de elemente de pe pagina curenta si
totalNumberOfElements(numarul total de inregistrari existente in baza de date).

Interfata PagingRepository<ID, E>:
Extinde Repository<ID, E> si adauga metoda Page<E> findAllOnPage(Pageable pageable).
Repository SQL (UtilizatorDbRepository & PrietenieDbRepository):
Interogari SQL care folosesc LIMIT si OFFSET.
Pentru prieteni se foloseste o singura interogare SQL cu JOIN si CASE pentru
a extrage utilizatorii prieteni paginat.

Service si UI:
Validare a parametrilor de paginare in Service(pageNumber >= 0 si pageSize > 0).
Dimensiunea paginii (page.size) este citita din config.properties.

In meniu la optiunile de paginare pot naviga intre pagini:
n pentru inainte, p pentru inapoi, g pentru o pagina anume, q pentru iesire

In fisierul config.properties se configureaza accesul la baza de date si marimea paginii:
page.size=3 ( sau cat se doreste)

Validari si verificari:

Navigarea inainte (n) si inapoi (p) schimba corect elementele si updateaza
numarul paginii in formatul Pagina x din y.

Daca adaugi sau stergi utilizatori/prietenii, la urmatoarea afisare numarul total
de pagini (y) si numarul total de elemente se recalculeaza automat.

Un utilizator fara prieteni afiseaza mesajul: Nu exista inregistrari de afisat. (Pagina 0 din 0).

Parametrii invalizi (numar pagina negativ sau dimensiune <= 0) arunca excepții de
tip IllegalArgumentException din Service.