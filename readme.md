**Disclaimer: program działa na systemie Windows**

# 1. Dokumentacja instalacyjno-konfiguracyjna

## Infrastruktura:

**Wymagania:**

* Docker & Docker Desktop

Administrator odpowiada za uruchomienie kontenera z bazą danych MySQL, która jest niezbędna do działania programu. Domyślnie uruchamiana na `localhost`.

Wykonaj komende `docker-compose up -d` w folderze `/docker` w celu budowy kontenera Docker z bazą danych MySQL.

`docker-compose` zawarty w pliku posiada wszelkie parametry do utworzenia bazy danych z wstępnymi danymi (`init.sql`) i dodanym `ADMIN user` w bazie, do obsługi z poziomu aplikacji.

## Ewentualny rozwój i modyfikacje

**Wymagania:**

* JDK21
* Apache Maven

**Ważne!!**
Wymagane jest pobranie i wypakowanie JDK21 do katalogu `src/main/frontend/resources`, tak żeby ścieżka całkowita wyglądała tak `src/main/frontend/resources/jre/bin/java.exe`.
Umożliwi to "spakowanie" całego programu do jednego pliku wykonywalnego `.exe`.

Po edycji kodu należy wykonać komendę `mvn clean package` w folderze głównym. 

W jej wyniku zostanie zwrócony plik `Tennis Club Manager.exe` w `src/main/frontend/dist-electron`, który jest końcową aplikacją.


# 2. Dokumentacja użytkowa

## Uruchomienie aplikacji:

* Uruchom plik `Tennis Club Manager.exe`
* Nie musisz instalować Javy ani bazy danych – aplikacja połączy się z serwerem automatycznie.

## Główne funkcje aplikacji:

### a) Logowanie i Profil

1. ***Rejestracja*** - przy pierwszym uruchomieniu załóż konto, podając wymagane dane (prawy górny róg)
2. ***Profil*** - po kliknięciu w swoje imię na pasku nawigacyjnym, wyświetlą ci się twoje rezerwacje, mecze turniejowe i statystyki z tych meczy. Dodatkowo będziesz mieć tam opcje przejścia do zmiany swoich danych osobowych.

### b) Rezerwacja kortu

1. Przejdź do zakładki `Courts`
2. Skorzystaj z filtrów do wyboru oczekiwanych kortów
3. Kliknij `MAKE RESERVATION` na korcie, na którym chcesz utworzyć rezerwacje.
4. Wybierz wolne godziny wyświetlane przez system i zatwierdź rezerwacje, status Twojej rezerwacji zmieni się na `ACTIVE` po przetworzeniu płatności.

### c) Udział w turniejach

1. Przejdź do zakładki `Tournaments`
2. Wybierz interesujący Ciebie turniej, możesz użyć filtrów po lewej stronie do ograniczenia listy.
3. Zwróć uwagę na pole `Points required` - jeśli twoje punkty są niższe, system nie pozwoli na zapis.
4. Kliknij `REGISTER`, jeśli turniej ma wolne miejsca, zostaniesz dodany do listy.
5. Po zamknięciu zapisów zostanie wygenerowana drabinka, gdzie sprawdzisz, z kim, gdzie i kiedy grasz pierwszy mecz.

### d) Ranking graczy
1. Przejdź do zakładki `Players`
2. Wyświetli się ranking posortowany malejąco punktami
3. Możesz szukać graczy po ich imieniu i nazwisku w wyszukiwarce na górze
4. Po kliknięciu w gracza, przeniesie Cie do jego profilu ze statystykami.