# Fake Eventówki (Fabric 1.21.4)

Klientowy mod: INSERT otwiera menu (klawisz do zmiany w Controls -> "Fake Eventówki").
Weź dowolny przedmiot do ręki, kliknij nazwę eventówki - przedmiot zmieni się wizualnie
(tylko u Ciebie, serwer i inni gracze nic nie widzą).

## Budowanie .jar
Wymagane: Java 21 + Gradle 8.12 (albo IntelliJ, który zrobi to sam).

    gradle build

Gotowy plik: build/libs/fake-eventowki-1.0.0.jar (NIE ten z "-sources").
Wrzuć do .minecraft/mods razem z Fabric API 0.119.4+1.21.4.

Alternatywa bez instalowania czegokolwiek: wrzuć folder na GitHub, zakładka Actions ->
"Build mod" -> Run workflow -> pobierz .jar z Artifacts.
