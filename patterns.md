# Użyte wzorce architektoniczne

## 1. Domain Model
**Zastosowanie**: Cała logika biznesowa systemu (użytkownicy, turnieje, ranking, itd.)

**Dlaczego**: Logika jest zlożona - turnieje z drabinkami, ranking oparty na wynikach, walidacja rezerwacji, zarządzanie płatnościami.
Domain Model pozwolił na wykorzystanie dziedziczenia (`User` -> `Admin`,`Player`) i asocjacji między obiektami.

**Klasy**: `User`, `Court`, `Match`, `Payment`, `Reservation`, `Tournament`, `Player`, `Admin`

## 2. Data Mapper
**Zastosowanie**: Mapowanie obiektów dziedziny (np. `User`, `Tournament`, `Court`) na tabele BD. 

**Dlaczego**: Złożony model dziedziny (relacje między użytkownikami, rezerwacjami, turniejami itd.), **Data Mapper** zapewnia niezaleźność modelu od struktury bazy danych.

**Klasy**: `UserMapper`, `MatchMapper`, `PaymentMapper`, `ReservationMapper`, `TournamentMapper`, `CourtMapper`

## 3. Repository

**Zastosowanie**: Służy do enkapsulacji logiki dostępu do danych, udostępniając serwisom prosty interfejs do operowania na obiektach domenowych zamiast surowych zapytań SQL. Repozytorium pośredniczy między bazą danych a aplikacją, wykorzystując mappery do przekształcania rekordów z tabel na gotowe obiekty klas.

**Dlaczego**: Odizolowanie logiki biznesowej od szczegółów technicznych bazy danych. Kod dzięki temu jest bardziej czytelny i pozwala na wielokrotne używanie tych samych zapytań SQL w wielu miejscach.

**Klasy**: `UserRepository`, `MatchRepository`, `PaymentRepository`, `ReservationRepository`, `TournamentRepository`, `CourtRepository`

## 4. Service Layer

**Zastosowanie**: Centralny punkt koordynacji logiki biznesowej, integrując operacje na wielu repozytoriach jednocześnie. Odpowiada ona za walidację danych wejściowych, zarządzanie transakcjami przez `UnitOfWork` oraz mapowanie encji na obiekty `DTO` przed wysłaniem odpowiedzi do frontendu.

**Dlaczego**: Uniknięcie skomplikowanej logiki w kontrolerach, czyni kod bardziej modularnym i łatwiejszym do testowania. Pozwala na zachowanie spójności danych.

**Klasy**: `CourtService`, `ReservationService`, `TournamentService`, `UserService`

## 5. Unit of Work

**Zastosowanie**: Służy do śledzenia wszystkich zmian wprowadzanych w obiektach (nowych, zmienionych lub usuniętych) podczas trwania jednej operacji biznesowej i zapewnia ich atomowy zapis w bazie danych.

**Dlaczego**: Gwarantuje spójność danych i uniknięcia sytuacji, w której tylko część operacji zostaje zapisana. Kod serwisu jest prostszy, bo `UnitOfWork` zarządza komendami typu `commit` i `rollback`

**Klasy**: `UnitOfWork`

## 6. Front Controller

**Zastosowanie**: Realizowany przy pomocy `DispatcherServlet` w Spring Boot, który odbiera każde żądanie HTTP wysyłane z frontendu i decyduje, do którego kontrolera je przydzielić.

**Dlaczego**: Jeden spójny mechanizm zarządzania ruchem w całej aplikacji. Uporządkowanie przetwarzania żądań HTTP, gdzie mamy skomplikowaną logikę.

**Klasy**: `CourtController`, `ReservationController`, `TournamentController`, `UserController`

## 7. DTO (Data Transfer Object)

**Zastosowanie**: Wzorzec ten służy do przesyłania danych między frontendem (React/Electron) a backendem (Java) w ustrukturyzowanej formie, która jest niezależna od tabel w bazie danych.

**Dlaczego**: Użyty, żeby zwiększyć bezpieczeństwo i uniknąć przesyłania całych obiektów domenowych, które mogą zawierać wrażliwe dane (np. hasła użytkowników w klasie `User`). Dzięki DTO można dowolnie kształtować strukturę danych wysyłanych do interfejsu, łącząc informacje z różnych tabel bez konieczności zmiany modelu bazy danych.

**Klasy**: np. `CreateReservationRequest`, `LoginRequest`, `TournamentDetailsDTO`

## 8. Pessimistic Offline Lock

**Zastosowanie**: Służy do blokowania dostępu do zasobu (np. konkretnego terminu na korcie) na czas trwania sesji biznesowej, która wykracza poza jedną transakcję bazy danych.

**Dlaczego**: Zastosowano to rozwiązanie, aby zapobiec konfliktom podczas krytycznych operacji, takich jak zapisywanie się graczy na turniej lub aktualizacja wyników meczów. Dzięki blokadzie na poziomie bazy danych jest pewność, że system zachowa spójność i nie pozwoli na jednoczesną edycję tego samego turnieju przez dwóch różnych administratorów lub użytkowników.

**Klasy**: `TournamentMapper`

## 9. Identity Field

**Zastosowanie**: Wzorzec ten polega na przechowywaniu klucza głównego z bazy danych (`id`) bezpośrednio w obiektach aplikacji. W bazie danych MySQL każda tabela posiada kolumnę id BIGINT AUTO_INCREMENT PRIMARY KEY, która jest mapowana na pole Long id w Javie.

**Dlaczego**: Zastosowano go, aby zachować spójność tożsamości między światem obiektowym a relacyjnym. Dzięki temu aplikacja dokładnie wie, który wiersz w bazie danych odpowiada konkretnemu obiektowi w pamięci,

**Klasy**: każda tabela w bazie danych MySQL (`init.sql`)

## 9. Foreign Key Mapping

**Zastosowanie**: Wzorzec ten mapuje relacje między obiektami za pomocą kluczy obcych w bazie. Powiązania (`Reservation` -> `User`,`Court`) - tabela `reservations` posiada `userId` i `courtId`

**Dlaczego**: Łatwe odszukiwanie powiązanych obiektów.

**Klasy**: tabele w bazie danych MySQL (`init.sql`), np. `reservations`, `payments`

## 10. Association Table Mapping

**Zastosowanie**: Wzorzec ten służy do obsługi relacji "wiele do wielu" poprzez stworzenie dodatkowej tabeli łączącej.

**Dlaczego**: Relacja wiele do wielu w bazach SQL bez dublowania danych. Elastyczne zarządzanie uczestnikami turniejów

**Klasy**: tabela `tournament_participants` w bazie danych

## 11. Single Table Inheritance

**Zastosowanie**: Wzorzec ten polega na przechowywaniu całej hierarchii klas (`User` -> `Admin`,`Player`) w jednej, wspólnej tabeli bazy danych o nazwie `users`. Rozróżnienie za pomocą kolumny typu discriminator – pole `user_type (ENUM: 'ADMIN', 'PLAYER')`.

**Dlaczego**: Struktura danych dla admina i gracza jest bardzo podobna, a różnią się oni głównie uprawnieniami i dodatkowymi polami (jak `ranking_points`).

**Klasy**: tabela `users` w bazie danych




