# Rush Hour Game (Projektfeladat)

Egy Java nyelven megvalósított, konzolos Rush Hour táblajáték. A játékos egy járművet irányít, miközben a gépi ellenfél is lépéseket tesz a táblán. A projekt tartalmaz egyedi pályák betöltését, játékállás-mentést és -visszatöltést, valamint győzelmi statisztikát tároló dicsőséglistát.

## 📦 Technológiák és követelmények

- **Programozási nyelv:** Java 21
- **Build eszköz:** Apache Maven
- **Tesztelés:** JUnit 5, Mockito
- **Kódlefedettség:** JaCoCo Maven Plugin – legalább 80%-os sorlefedettség a teljes projektkódra (`BUNDLE` szinten)
- **Kódstílus ellenőrzése:** Checkstyle Maven Plugin (`checkstyle.xml` alapján)
- **Adatbázis:** H2 – a győzelmek és a ranglista tárolására
- **JSON-kezelés:** Jackson Databind – játékállások JSON-formátumú mentéséhez és betöltéséhez
- **Naplózás:** Logback Classic

A fordításhoz Java 21 JDK-ra és Mavenre van szükség.

---

## Fordítás és indítás

A parancsokat a projekt gyökérmappájában kell futtatni, abban a mappában, ahol a `pom.xml` fájl található.

**1. A projekt tisztítása, fordítása, tesztelése és telepítése:**

```bash
mvn clean install
```

A parancs lefuttatja a Checkstyle ellenőrzést és a teszteket, elkészíti a JaCoCo lefedettségi jelentést, ellenőrzi a 80%-os lefedettségi követelményt, majd létrehozza a futtatható JAR-fájlt.

**2. A játék indítása:**

```bash
java -Dfile.encoding=UTF-8 -jar target/rush-hour-game-1.0-SNAPSHOT-jar-with-dependencies.jar
```

A `-Dfile.encoding=UTF-8` paraméter az UTF-8 karakterkódolás használatát állítja be, így a magyar ékezetes karakterek megfelelően jelennek meg.

**A tesztek külön futtatása:**

```bash
mvn test
```

A JaCoCo HTML-jelentése a fordítás után a `target/site/jacoco/index.html` fájlban tekinthető meg.

---

## 🎮 A játék használata

A program indításakor a következő lehetőségek közül lehet választani:

- `1` – az alapértelmezett pálya betöltése (`map1.txt`);
- `2` – egyedi pálya vagy korábban elmentett játékállás betöltése.

Ezután meg kell adni a játékos nevét. A játék a körök előtt megjeleníti a dicsőséglistát, majd kirajzolja a pályát. Az oszlopokat betűk (`a`, `b`, `c` stb.), a sorokat számok jelölik.

### Lépések megadása

A lépés formátuma:

```text
<jármű_azonosító vagy mezőkoordináta> <lépésszám>
```

Példák:

```text
1 2
```

Az `1` azonosítójú jármű két mezőt lép.

```text
a 2
```

A program az `a` mezőn álló járművet mozgatja két mezővel. A koordináta oszlopbetűből és sorszámból áll, szóköz nélkül.

A lépésszám egész szám lehet:

- pozitív érték: vízszintes járműnél jobbra, függőlegesnél lefelé;
- negatív érték: vízszintes járműnél balra, függőlegesnél felfelé;
- `0`: nem megengedett.

A program ellenőrzi, hogy a lépés szabályos-e, tehát a jármű nem ütközhet másik járművel, és nem hagyhatja el szabálytalanul a táblát. Sikeres játékoslépés után a gépi ellenfél is végrehajt egy szabályos lépést. Az ellenfél egyszerű, véletlenszerűen választ a lehetséges lépések közül.

### Mentés és kilépés

Az aktuális játékállás mentése:

```text
save <fájlnév>
```

Például:

```text
save allapot
```

Ez a `saves/allapot.txt` fájlba menti a pályát. Ha a fájlnév `.json` kiterjesztésű, a mentés JSON-formátumban készül:

```text
save allapot.json
```

A program a `saves/` mappát szükség esetén automatikusan létrehozza. Megadható teljes vagy relatív elérési út is, például `save saves/mentes.txt`.

A játékból az alábbi paranccsal lehet kilépni:

```text
exit
```

### Korábban elmentett állás betöltése

Indításkor válaszd a `2` lehetőséget, majd add meg a mentés nevét vagy elérési útját. Például:

```text
allapot.txt
```

vagy:

```text
saves/allapot.txt
```

A `saves/` mappában található mentések nevét a program kilistázza. A mentések TXT- és JSON-formátumban is visszatölthetők.

---

## 🗺️ Pálya betöltése TXT-fájlból

Egyedi pálya betöltéséhez indításkor a `2` lehetőséget kell választani, majd meg kell adni a fájl elérési útját. A projektben található második pálya például így adható meg:

```text
map2.txt
```

A `map2.txt` fájlnév önmagában is megadható, mivel a program a projekt erőforrásai között is megkeresi.

### A TXT-pályafájl formátuma

A fájl első sora a pálya méretét tartalmazza: először a sorok, majd az oszlopok számát. Ezt követően minden sor egy jármű adatait tartalmazza a következő sorrendben:

```text
<azonosító> <hossz> <irány> <kezdősor> <kezdőoszlop>
```

Példa:

```text
6 12
1 2 H 2 0
2 2 H 3 10
A 3 V 1 2
B 2 H 0 4
C 2 V 3 5
D 3 H 1 7
```

A mezők jelentése:

- **Azonosító:** a jármű egyedi neve vagy betűjele. Az `1` és a `2` speciális céljárművek.
- **Hossz:** a jármű által elfoglalt mezők száma; legalább `2` lehet.
- **Irány:** `H` a vízszintes, `V` a függőleges elhelyezkedést jelenti.
- **Kezdősor:** a jármű bal felső (vízszintes), illetve legfelső (függőleges) mezőjének sorszáma.
- **Kezdőoszlop:** a jármű bal szélső mezőjének oszlopszáma.

**Fontos:** a fájlban a sor- és oszlopindexelés `0`-tól kezdődik. A terminálban megjelenő koordináták ezzel szemben `1`-től számozott sorokat és betűkkel jelölt oszlopokat használnak.

A pályán belül a járművek nem fedhetik egymást, és egyik jármű sem lóghat ki a tábláról. Az azonosítóknak egyedinek kell lenniük. A játék céljárművei vízszintesek: az `1` azonosítójú jármű a jobb oldali kijáraton, a `2` azonosítójú jármű a bal oldali kijáraton tud nyerni.

Ha a megadott fájl nem található vagy nem tölthető be, a program az alapértelmezett pályát tölti be.

---

## 🏆 LeaderBoard és adatbázis

A játék a győzelmek számát H2 adatbázisban tárolja. A játékos győzelme, illetve a gépi ellenfél győzelme is rögzítésre kerül. A dicsőséglista a győzelmek száma szerint csökkenő sorrendben jelenik meg.

Az adatbázis fájlja a projekt futtatási könyvtárában automatikusan létrejövő `database.mv.db`. A projekt `.gitignore` fájlja kizárja a futás közben létrejövő adatbázisfájlokat a Git-verziókezelésből.

---

## 📑 Projektstruktúra

```text
rush-hour-game/
├── src/
│   ├── main/
│   │   ├── java/hu/unideb/inf/
│   │   │   ├── Main.java                 # A program belépési pontja
│   │   │   ├── db/
│   │   │   │   └── HighScoreDao.java     # Dicsőséglista és adatbázis-kezelés
│   │   │   ├── game/
│   │   │   │   ├── Game.java             # Játékfolyamat és játékoslépések
│   │   │   │   └── SimpleAI.java          # Egyszerű gépi ellenfél
│   │   │   ├── io/
│   │   │   │   ├── BoardNotation.java    # Mezőkoordináták kezelése
│   │   │   │   └── GameLoader.java       # Pályák és mentések betöltése/mentése
│   │   │   ├── model/
│   │   │   │   ├── Coordinate.java       # Koordináta-rekord
│   │   │   │   └── Orientation.java      # Járműirányok
│   │   │   └── state/
│   │   │       ├── Board.java            # Játéktábla és lépésszabályok
│   │   │       └── Vehicle.java          # Járműmodell
│   │   └── resources/
│   │       ├── logback.xml               # Naplózási konfiguráció
│   │       ├── map1.txt                  # Alapértelmezett pálya
│   │       └── map2.txt                  # Második minta pálya
│   └── test/
│       └── java/hu/unideb/inf/
│           ├── MainTest.java
│           ├── db/HighScoreDaoTest.java
│           ├── game/                     # Játék- és AI-tesztek
│           ├── io/                       # Betöltési és koordinátakezelési tesztek
│           └── state/                    # Tábla- és járműtesztek
├── saves/                                # Elmentett játékállások
├── checkstyle.xml                        # Kódstílus-szabályok
├── mvnw / mvnw.cmd                       # Maven Wrapper indítófájlok
└── pom.xml                               # Maven projektleíró és függőségek
```

## 🧪 Tesztek

A projekt JUnit 5 alapú egységteszteket tartalmaz. A tesztek többek között az alábbi működéseket ellenőrzik:

- a járművek létrehozását, mozgatását és a táblán elfoglalt mezőit;
- az ütközések, érvénytelen lépések és kijáratok ellenőrzését;
- a pályák TXT- és JSON-formátumú betöltését, mentését;
- a játékos és a gépi ellenfél győzelmének kezelését;
- a dicsőséglista adatbázis-műveleteit;
- a koordináták és oszlopjelölések átalakítását.

A tesztek a következő paranccsal futtathatók:

```bash
mvn test
```

A teljes ellenőrzéshez és a futtatható JAR elkészítéséhez használd a `mvn clean install` parancsot.
