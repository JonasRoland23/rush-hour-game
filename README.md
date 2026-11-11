# Rush Hour Game (Project feladat)

Egy Java alapon megvalósított, parancssori/konzolos Rush Hour táblajáték projekt a Debreceni Egyetem Informatikai Karának kurzusához.

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
