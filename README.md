# Rush Hour Game (Project feladat)

Egy Java alapon megvalósított, parancssori/konzolos Rush Hour táblajáték projekt a Nyiregyhazi Egyetem Informatikai Karának kurzusához.

## 📦 Technológiai Stack és Követelmények

* **Nyelv:** Java 21
* **Build tool:** Apache Maven 3.8+
* **Tesztelés:** JUnit 5, Mockito
* **Kódlefedettség:** JaCoCo Maven Plugin (minimum 80% lefedettség a `hu.unideb.inf.state` csomagra)
* **Kódstílus:** Checkstyle Maven Plugin (`checkstyle.xml`)
* **Adatbázis:** H2 (Embedded Database a High Score / Dicsőséglista tárolására)
* **Adatkezelés:** Jackson Databind (JSON adatformátum kezeléséhez)
* **Naplózás:** Logback Classic

---

## Fordítás és indítás

A parancsokat a projekt gyökérmappájában kell futtatni, ott, ahol a `pom.xml` fájl található.

**1. A projekt tisztítása, fordítása és telepítése:**

```bash
mvn clean install
```

**2. A játék indítása:**

```bash
java -Dfile.encoding=UTF-8 -jar target/rush-hour-game-1.0-SNAPSHOT-jar-with-dependencies.jar
```

Az első parancs elkészíti a futtatható JAR-fájlt, a második pedig elindítja a játékot. A `-Dfile.encoding=UTF-8` beállítás biztosítja az UTF-8 karakterkódolás használatát.

## Pálya betöltése TXT-fájlból

A játék indításakor két lehetőség közül lehet választani:

- `1` – az alapértelmezett pálya betöltése;
- `2` – egy TXT-fájlból megadott pálya betöltése.

A pályafájl elérési útját a projekt gyökérmappájához képest kell megadni. A projektben található `map2.txt` fájl például így adható meg:

```text
src/main/resources/map2.txt
```

**Fontos:** az elérési útban a mappákat `/` jellel kell elválasztani. A fájlnak a megadott helyen kell lennie.


## 📑 Projekt Struktúra

```text
rush-hour-game/
├── src/
│   ├── main/
│   │   └── java/
│   │       └── hu/unideb/inf/
│   │           ├── Main.java          # Belépési pont
│   │           ├── db/                # H2 Adatbázis DAO és High Score kezelés
│   │           ├── game/              # Játékciklus és gépi ellenfél (SimpleAI)
│   │           ├── io/                # Pálya beolvasás és mentés (JSON/Text)
│   │           ├── model/             # Modell osztályok (Coordinate, Orientation)
│   │           └── state/             # Üzleti logika (Board, Vehicle)
│   │   └── resources/                 # Konfigurációs fájlok és alapértelmezett pályák
│   └── test/
│       └── java/
│           └── hu/unideb/inf/state/   # Egységtesztek a state csomagra (JUnit5)
├── checkstyle.xml                     # Checkstyle kódformázási szabályok
├── mvnw / mvnw.cmd                    # Maven Wrapper scriptek
└── pom.xml                            # Maven projektleíró


