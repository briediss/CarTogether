# CarTogether

CarTogether - kelionių dalijimosi sistema, skirta padėti žmonėms rasti bendrakeleivius, sumažinti kelionių išlaidas ir efektyviau išnaudoti laisvas vietas automobilyje.

## 1. Projekto funkcionalumas

II etape įgyvendintas veikiantis kelionių paieškos ir rezervavimo prototipas.

Pagrindinės funkcijos:
- Kelionių paieška pagal išvykimo ir atvykimo miestus bei laiką.
- Kelionių filtravimas pagal laisvas vietas, vairuotojo įvertinimą ir leidžiamą maršruto nukrypimą.
- Kelionės kainos vienam keleiviui apskaičiavimas pagal atstumą, degalų sąnaudas ir kainą.
- Laisvos vietos rezervavimas.
- Rezervacijų peržiūra ir atšaukimas.
- Klaidų ir konfliktinių situacijų apdorojimas.
- Dviejų organizacijų duomenų atskyrimas (multitenancy).

## 2. Naudojamos technologijos

- Java 21
- Spring Boot 4.0.9
- Spring Data JPA / Hibernate
- H2 Database
- Maven
- HTML, CSS, JavaScript
- JUnit 5 ir Mockito

## 3. Sistemos paleidimas

Reikalingos priemonės:
- JDK 21
- Git
- Interneto ryšys Maven priklausomybėms atsisiųsti

Atsisiųskite projektą:

```bash
git clone https://github.com/briediss/CarTogether.git
cd CarTogether
```

Paleidimas Windows aplinkoje:

```powershell
.\mvnw.cmd spring-boot:run
```

Paleidus programą, interneto naršyklėje atidarykite:

http://localhost:8080

Programa naudoja failinę H2 duomenų bazę, kuri saugoma `data/` aplanke ir išlieka tarp paleidimų.

## 4. Demonstraciniai klientai

Prototipe paruoštos dvi organizacijos:

| Organizacija | API raktas | Tenant ID |
|---|---|---|
| Vilnius Group | `demo-vilnius-key` | 1 |
| Kaunas Group | `demo-kaunas-key` | 2 |

API raktai naudojami tik demonstraciniam organizacijos konteksto pasirinkimui. Tai nėra produkcinė autentifikavimo sistema.

Kiekvienos organizacijos kelionės ir rezervacijos atskiriamos pagal `tenantId`.

## 5. Demonstraciniai duomenys

Pradiniai kelionių duomenys sukuriami automatiškai pirmą kartą paleidus programą su tuščia duomenų baze.

| Vairuotojas | Organizacija | Maršrutas |
|---|---|---|
| Titas | Vilnius Group | Vilnius → Kaunas |
| Lukas | Vilnius Group | Vilnius → Kaunas |
| Edvinas | Kaunas Group | Kaunas → Vilnius |
| Klemensas | Kaunas Group | Vilnius → Klaipėda |

Kelionių laikai nustatomi pagal pirmojo paleidimo datą.

Duomenys saugomi H2 duomenų bazėje, todėl pakartotinai paleidus programą jau egzistuojantys įrašai išlieka.

## 6. Demonstravimo scenarijus

1. Paleisti programą ir atidaryti `http://localhost:8080`.
2. Pasirinkti „Vilnius Group“.
3. Ieškoti kelionės iš Vilniaus į Kauną, naudojant demonstracinės kelionės išvykimo datą.
4. Peržiūrėti rastas keliones, kainas ir laisvas vietas.
5. Paspausti „Reserve“ ir rezervuoti vietą.
6. Skiltyje „My Bookings“ patikrinti sukurtą rezervaciją.
7. Paspausti „Cancel Booking“ ir patikrinti, ar laisva vieta grąžinta.
8. Pakeisti organizaciją į „Kaunas Group“ ir parodyti, kad pirmos organizacijos kelionės bei rezervacijos nepasiekiamos.
9. Pademonstruoti klaidos arba konfliktinės situacijos apdorojimą.
10. Paleisti automatinius testus.

## 7. API

Visoms šioms API užklausoms reikalinga `X-API-Key` antraštė.

| Metodas | Adresas | Paskirtis |
|---|---|---|
| GET | `/api/trips/search` | Kelionių paieška |
| POST | `/api/bookings?tripId=1` | Vietos rezervavimas |
| GET | `/api/bookings` | Organizacijos rezervacijos |
| PATCH | `/api/bookings/1/cancel` | Rezervacijos atšaukimas |

Kelionių paieškai naudojami parametrai: `departureCity`, `arrivalCity` ir `departureTime` (ISO datos ir laiko formatu, pvz., `2026-11-11T15:10:00`).

## 8. Automatiniai testai

Testai tikrina:

- Sėkmingą kelionių paiešką.
- Ribines ir neteisingos įvesties situacijas.
- Kelionės kainos apskaičiavimą.
- Vietų rezervavimą ir konfliktus.
- Rezervacijų atšaukimą.
- Duomenų izoliavimą tarp organizacijų.
- Transakcijos atšaukimą nepavykus grąžinti vietos.
- Spring Boot programos konteksto paleidimą.

Testams naudojama atskira H2 duomenų bazė.

Windows aplinkoje:

```powershell
.\mvnw.cmd clean test
```

Sėkmingai atlikus testus, terminale pateikiamas pranešimas `BUILD SUCCESS`.

## 9. Projekto struktūra

- `model` - duomenų modeliai (`Trip`, `Booking`).
- `repository` - duomenų saugojimas ir gavimas.
- `service` - kelionių paieškos, kainos skaičiavimo, rezervavimo ir organizacijų logika.
- `controller` - REST API.
- `dto` - API atsakymų duomenų struktūros.
- `exception` - klaidų apdorojimas.
- `config` - pradinių demonstracinių duomenų sukūrimas.
- `resources/static` - HTML, CSS ir JavaScript naudotojo sąsaja.

## 10. Tolesnis vystymas

III etape planuojama įgyvendinti naudotojų prisijungimą, roles, saugesnį organizacijų konteksto valdymą, klientų konfigūraciją ir konteinerizuotą programos paleidimą naudojant Docker.