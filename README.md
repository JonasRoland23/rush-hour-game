Rush Hour Game (Számítógépes Rush Hour Játék)
Egy Java alapon megvalósított, parancssori/konzolos Rush Hour (Csúcsforgalom) táblajáték projekt a Debreceni Egyetem Informatikai Karának kurzusához.
📦 Technológiai Stack és Követelmények
Nyelv: Java 21
Build tool: Apache Maven 3.8+
Tesztelés: JUnit 5, Mockito
Kódlefedettség: JaCoCo Maven Plugin (minimum 80% lefedettség a hu.unideb.inf.state csomagra)
Kódstílus: Checkstyle Maven Plugin (checkstyle.xml)
Adatbázis: H2 (Embedded Database a High Score / Dicsőséglista tárolására)
Adatkezelés: Jackson Databind (JSON adatformátum kezeléséhez)
Naplózás: Logback Classic

🏗️ Projekt Struktúra
rush-hour-game/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── hu/unideb/inf/
│   │   │       ├── Main.java          # Belépési pont
│   │   │       ├── db/                # H2 Adatbázis DAO és High Score kezelés
│   │   │       ├── game/              # Játékciklus és gépi ellenfél (SimpleAI)
│   │   │       ├── io/                # Pálya beolvasás és mentés (JSON/Text)
│   │   │       ├── model/             # Modell osztályok (Coordinate, Orientation)
│   │   │       └── state/             # Üzleti logika (Board, Vehicle)
│   │   └── resources/                 # Konfigurációs fájlok és alapértelmezett pályák
│   └── test/
│       └── java/
│           └── hu/unideb/inf/state/   # Egységtesztek a state csomagra (JUnit5)
├── checkstyle.xml                     # Checkstyle kódformázási szabályok
├── mvnw / mvnw.cmd                    # Maven Wrapper scriptek
└── pom.xml                            # Maven projektleíró

⚙️ Építés és Tesztelés
A projekt fordítása, a kódellenőrzés (Checkstyle), az egységtesztek futtatása és a kódlefedettség mérése a következő paranccsal végezhető el:
Telepített Maven használatával:
mvn clean install
Maven Wrapper használatával (ha nincs Maven telepítve):
Windows (PowerShell): .\mvnw clean install
Linux / macOS: ./mvnw clean install
Sikeres build esetén a target/ mappában létrejön a függőségeket is tartalmazó futtatható JAR fájl:
rush-hour-game-1.0-SNAPSHOT-jar-with-dependencies.jar

🚀 A Játék Futtatása
A játék indítása a lefordított fat-JAR fájlból:
java -jar target/rush-hour-game-1.0-SNAPSHOT-jar-with-dependencies.jar

🎮 Játékszabályok és Vezérlés
Játékos és AI Szerepek:
Emberi játékos: Az A jelű járművet irányítja. A cél az A autó kijuttatása a tábla jobb szélére.
Gépi ellenfél (AI): A B jelű járművet célozza meg. Az AI célja a B autó kijuttatása a tábla bal szélére.
Lépés megadása:
A konzolra írt lépések formátuma: <JÁRMŰ_ID> <LÉPÉSSZÁM>
Példa:
A 2  az A autó előremozdítása 2 mezővel (jobbra vagy lefelé).
C -1  a C autó hátramozdítása 1 mezővel (balra vagy felfelé).
Kilépés: Írd be a exit szót a játék feladásához és a kilépéshez.
Eredmények:
A győzelmek automatikusan elmentésre kerülnek a beépített H2 adatbázisban, a játék indításakor pedig megjelenik az aktuális Dicsőséglista.
