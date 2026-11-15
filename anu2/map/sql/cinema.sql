-- Seminar 7: baza de date cinema
-- 1. In pgAdmin: Databases -> Create -> Database..., numele: cinema
-- 2. Click dreapta pe baza cinema -> Query Tool, deschideti acest fisier si rulati-l (F5).
-- Scriptul nu sterge nimic si poate fi rulat de mai multe ori.
-- id-ul il genereaza baza de date (GENERATED ALWAYS AS IDENTITY): nu il dam in INSERT.

CREATE TABLE IF NOT EXISTS filme (
                                     id      BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                     titlu   VARCHAR(100) NOT NULL,
    regizor VARCHAR(60)  NOT NULL,
    an      INT          NOT NULL,
    durata  INT          NOT NULL   -- in minute
    );

-- filmele de test, adaugate doar daca nu exista deja (acelasi titlu)
INSERT INTO filme (titlu, regizor, an, durata)
SELECT v.titlu, v.regizor, v.an, v.durata
FROM (VALUES (1, 'Pulp Fiction', 'Quentin Tarantino', 1994, 154),
             (2, 'Moartea domnului Lazarescu', 'Cristi Puiu', 2005, 153),
             (3, '4 luni, 3 saptamani si 2 zile', 'Cristian Mungiu', 2007, 113),
             (4, 'Inception', 'Christopher Nolan', 2010, 148),
             (5, 'Django Unchained', 'Quentin Tarantino', 2012, 165),
             (6, 'Interstellar', 'Christopher Nolan', 2014, 169),
             (7, 'Parasite', 'Bong Joon-ho', 2019, 132),
             (8, 'Oppenheimer', 'Christopher Nolan', 2023, 180)) AS v(nr, titlu, regizor, an, durata)
WHERE NOT EXISTS (SELECT 1 FROM filme f WHERE f.titlu = v.titlu)
ORDER BY v.nr;   -- nr: doar ordinea inserarii, nu este id-ul

SELECT * FROM filme ORDER BY id;
