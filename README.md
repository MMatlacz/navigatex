# Navigatex

> An Android prototype for RFID-assisted, short-distance navigation.

Navigatex was built for the **Facebook Hackathon Poland 2018**. The project pairs an Android app with tagged waypoints and a server-backed map so a user can search for a destination, follow directional prompts, and receive spoken progress updates while walking.

The prototype was designed around the smart-shoe concept described by the [Warsaw University of Technology Faculty of Electrical Engineering](https://www.ee.pw.edu.pl/en/blog/2019/05/05/student-wydzialu-elektrycznego-finalista-facebook-hackathon-2018/): an RFID reader identifies nearby waypoints, while the Android client presents the next turn and distance to the user.

## What the app does

- Searches a list of named destinations returned by the backend.
- Waits for the next detected waypoint/tag.
- Shows the available directions: ahead, left, right, or back.
- Estimates remaining steps using the Android step-counter sensor.
- Reads directions and distance aloud with Android Text-to-Speech.
- Announces when the destination has been reached.

## How it fits together

```text
RFID waypoint / reader
          │
          ▼
   navigation backend  ◄── destination and route data
          │  JSON over HTTP
          ▼
      Navigatex
   Android / Java app
          │
          ├── directional buttons
          ├── step counter
          └── Text-to-Speech
```

The Android client currently calls these historical endpoints:

| Endpoint | Purpose |
| --- | --- |
| `/api/nodes` | Load searchable destinations. |
| `/api/find_path?id=...` | Load route information for a destination. |
| `/api/last_step` | Read the latest detected waypoint and progress. |

## Project structure

```text
app/src/main/java/com/example/jkmiotek/navigatex/
├── MainActivity.java             Main menu
├── SearchActivity.java           Destination search
├── WaitingForTagActivity.java    Wait for the next waypoint
├── DirectionsActivity.java       Turn-by-turn directions and arrival state
├── DistanceActivity.java         Step counting and spoken distance updates
└── Connector/                    Volley request queue and callbacks
```

## Building the historical prototype

The project uses the Android Gradle plugin and support libraries from the 2018-era Android toolchain.

```bash
./gradlew assembleDebug
```

The debug APK is written to `app/build/outputs/apk/debug/app-debug.apk`.

Before running the app, update `app/src/main/res/values/strings.xml` with a reachable backend URL. The checked-in value points to the original hackathon service over plain HTTP and should be treated as historical configuration, not a production endpoint.

## Hackathon context

The team won **Facebook Hackathon Poland 2018** and travelled to the global finals at Facebook headquarters near San Francisco. The university’s account names the team as:

- Marcin Matłacz
- Jakub Kmiotek
- Szymon Stankiewicz
- Mateusz Cianciara

The public university article describes the wider prototype as an RFID reader, an Android/Java application, and a Python/Flask server for helping blind users navigate unfamiliar spaces.

## Provenance and status

This repository is a public archival mirror of the original private hackathon repository. Its Git history is intentionally preserved, including the original authors and timestamps. The code is a historical prototype and has not been modernized for current Android SDKs or production deployment.

The repository does not currently contain a license. Copyright and reuse permissions therefore remain with the project contributors; add an agreed license before redistributing or building on the code.

## Acknowledgements

- [Facebook Hackathon 2018 — Faculty of Electrical Engineering, Warsaw University of Technology](https://www.ee.pw.edu.pl/en/blog/2019/05/05/student-wydzialu-elektrycznego-finalista-facebook-hackathon-2018/)
- [Original Navigatex repository](https://github.com/kujbol/navigatex)
