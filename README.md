# Hotel App

Android app for hotel search, bookings, and comparison. Offline-first architecture with Room, Jetpack Compose, and Material 3.

## Screenshots

| Search & list | Hotel details | Booking success |
|---------------|---------------|-----------------|
| ![Search](screenshots/screen_search.png) | ![Details](screenshots/screen_details.png) | ![Success](screenshots/screen_success.png) |

Screenshots go in the `screenshots/` folder. From the emulator: **Extended controls → Camera** (or `Ctrl+S` / `Cmd+S`) to save a screenshot, then rename and place the files above.

## Tech stack

| Layer | Technologies |
|-------|--------------|
| **UI** | Jetpack Compose, Material 3 (dynamic color, light/dark) |
| **Architecture** | Clean Architecture (domain, data, ui), ViewModel, unidirectional flow |
| **DI** | Hilt |
| **Local DB** | Room (hotels, bookings) |
| **Network** | Retrofit, OkHttp |
| **Paging** | Paging 3 |
| **Navigation** | Navigation Compose |
| **Preferences** | DataStore (Preferences) |
| **Background** | WorkManager, Kotlin Coroutines & Flow |

## Requirements

- Android Studio (Ladybug or newer)
- JDK 17
- Android SDK 26+ (min), 35 (target)
- API key: optional in `local.properties` — see [API_KEY_SETUP.md](API_KEY_SETUP.md)

## Getting started

```bash
git clone https://github.com/YOUR_USERNAME/HotelApp.git
cd HotelApp
```

1. Open the project in Android Studio.
2. Optionally add `HOTELS_API_KEY` to `local.properties` ([instructions](API_KEY_SETUP.md)).
3. Run on an emulator or device (Run ▶).

## Project structure

```
app/src/main/java/com/example/hotelapp/
├── data/          Room, Retrofit, DataStore, repositories
├── domain/        Models, use cases, repository interfaces
├── ui/            Compose screens, ViewModels, theme
├── navigation/    NavGraph, bottom bar, routes
├── di/            Hilt modules
└── work/          WorkManager workers
```

## Features

- Hotel search by city and dates (check-in/out)
- Paginated hotel list with filter and sort by price
- Hotel details, booking flow, success screen with booking ID
- Favorites (local), compare up to 3 hotels
- Multi-language (en, bs, de), currency, profile
- Offline-first: data from Room, background sync with API

## License

[MIT](LICENSE)
