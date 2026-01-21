# AMRO Movies

**Advanced Movie Recommendation Organisation** - A modern Android application for discovering trending and popular movies, with favorites and a detail experience built for future growth.

This repository is prepared as an MVP delivery and handover for the AMRO assignment.

## Overview

AMRO Movies is a feature-rich Android application that showcases trending and popular movies from TMDB (The Movie Database). Built with Jetpack Compose, Clean Architecture, and MVVM, it delivers a polished browsing experience with local caching, shared element transitions, and background sync to keep data fresh.

## Assignment Coverage

The MVP implements the requested scope:

- **Trending Movie List**: Shows the top 100 trending movies for the week
- **Required Fields**: Title, image/poster, and genre per list item
- **Filtering**: Genre filter applied within the top 100 results
- **Sorting**: Popularity (default), title, and release date; ascending/descending
- **Movie Detail Screen**: Displays title, tagline, image, genre, description, vote average/count, budget, revenue, status, IMDB link, runtime, and release date
- **UX/Design**: Clean, intuitive Compose UI with Material 3 and custom typography
- **Error Handling**: Basic API/network error states and empty state handling
- **Scalable Architecture**: Clean Architecture + modularization to support future feature teams
- **Automated Tests**: Unit tests for ViewModels/use cases/mappers with a clear strategy

## Features

- **Home Tabs**: Trending grid with filters/sorting and a full-screen Popular pager
- **Popular Screen**: Vertical swipe experience with backdrop imagery and quick detail entry
- **Movie Details**: Rich metadata, IMDB deep link, and favorite toggling
- **Favorites Library**: Local favorites saved in Room and shown as a grid
- **Shared Element Transitions**: Poster transitions from list to detail
- **Local Caching**: Room-backed persistence for lists and details
- **Nightly Sync**: WorkManager job refreshes trending and popular lists at 2:00 AM
- **Material 3 + Dynamic Color**: Theming supports system dynamic color on Android 12+

## Architecture

The app follows **Clean Architecture** with a multi-module structure and MVVM per feature:

```
app/
├── app/                     # App entry, navigation, and home shell
├── core/                    # Shared UI + utilities
│   ├── ui/                  # Theme, typography, shared components
│   └── util/                # Constants + Result wrapper
├── data/                    # Data layer (remote + local + sync)
│   ├── remote/              # Retrofit API + DTOs
│   ├── local/               # Room database, entities, DAOs, mappers
│   ├── work/                # WorkManager sync worker
│   └── repository/          # Repository implementations
├── domain/                  # Business logic and models
│   ├── model/               # Domain models
│   ├── repository/          # Repository interfaces
│   └── usecase/             # Use cases
└── feature/                 # UI features
    ├── trending/            # Trending screen + filter/sort
    ├── popular/             # Popular vertical pager
    ├── detail/              # Movie detail + favorites
    └── library/             # Favorites library
```

## Tech Stack

| Category | Technology | Justification |
|----------|------------|---------------|
| **Language** | Kotlin | Modern, concise, official Android language |
| **UI Framework** | Jetpack Compose + Material 3 | Declarative UI with theming support |
| **Architecture** | MVVM + Clean Architecture | Testable, scalable, separation of concerns |
| **DI** | Hilt | Official Android DI solution |
| **Networking** | Retrofit + OkHttp | Industry standard HTTP stack |
| **Serialization** | Kotlinx Serialization | Kotlin-native JSON parsing |
| **Image Loading** | Coil | Compose-first image loading |
| **Database** | Room | Local caching and favorites storage |
| **Background Work** | WorkManager | Nightly data refresh |
| **Async** | Coroutines + Flow | Lifecycle-aware async streams |
| **Navigation** | Navigation Compose | Type-safe routing |
| **Testing** | JUnit 5, MockK, Turbine | Unit testing for core logic |

## Setup Instructions

### Prerequisites

- Android Studio Hedgehog (2023.1.1) or later
- JDK 17
- Android SDK with minimum API 24 (Android 7.0)

### Getting Your TMDB API Key

1. Visit [TMDB Developer Portal](https://developer.themoviedb.org/)
2. Create an account or log in
3. Go to Settings > API
4. Request an API key (choose "Developer" option)
5. Copy your API Read Access Token (v4 auth)

### Configuration

1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/AbnaMoviesA.git
   cd AbnaMoviesA
   ```

2. Add your TMDB API key to `local.properties`:
   ```properties
   TMDB_API_KEY=your_api_read_access_token_here
   ```

3. Sync the project with Gradle files

4. Build and run the app

### Building the App

```bash
# Build debug APK
./gradlew assembleDebug

# Run unit tests
./gradlew test

# Run all tests
./gradlew check
```

## API Integration

The app uses the following TMDB API endpoints:

| Endpoint | Purpose |
|----------|---------|
| `GET /trending/movie/week` | Fetch weekly trending movies |
| `GET /movie/popular` | Fetch popular movies |
| `GET /genre/movie/list` | Get genre mappings |
| `GET /movie/{movie_id}` | Get detailed movie information |

Images are loaded from TMDB's image CDN using configured poster/backdrop sizes.

All API calls include basic error handling for:
- Network unavailability
- API errors (4xx/5xx)
- Timeout scenarios
- Invalid/missing data

## Data & Sync

- **Room cache** stores movie lists, details, and genres for fast startup and offline browsing
- **Favorites** are persisted locally and remain available offline
- **Eviction strategy** removes older non-favorite items to keep cache size bounded
- **Nightly sync** refreshes trending and popular lists via WorkManager at 2:00 AM

## Testing Strategy

The project includes tests for core logic and state management:

- **ViewModels**: Trending, Popular, Detail, and Library state handling
- **Use Cases**: Get/Refresh movie lists and details, favorites operations
- **Mappers**: DTO-to-domain and entity mapping validation
- **Instrumentation**: Basic app context verification

### Running Tests

```bash
# Run all unit tests
./gradlew test

# Run a specific test class
./gradlew test --tests "TrendingViewModelTest"
```

## Project Structure Highlights

### State Management

Compose screens collect `StateFlow` from ViewModels for reactive UI updates:

```kotlin
data class TrendingUiState(
    val isLoading: Boolean = true,
    val movies: List<Movie> = emptyList(),
    val filteredMovies: List<Movie> = emptyList(),
    val availableGenres: List<Genre> = emptyList(),
    val selectedGenre: Genre? = null,
    val sortOption: SortOption = SortOption.POPULARITY,
    val sortOrder: SortOrder = SortOrder.DESCENDING,
    val error: String? = null
)
```

### Result Wrapper

A custom `Result` sealed class handles success/error states:

```kotlin
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val exception: Throwable) : Result<Nothing>()
}
```

### Shared Element Transitions

Poster transitions between list and detail screens are driven by shared element keys for a smoother navigation experience.

## Known Limitations

- No search functionality yet
- No user authentication or cloud-synced favorites
- Pagination UI for loading beyond the initial cached lists is not wired in
- Limited instrumentation/UI test coverage

## Future Improvements

- Add search with query suggestions and recent history
- Introduce user accounts and cloud-synced favorites
- Expose load-more pagination in Trending/Popular lists
- Add richer offline behavior (staleness indicators, manual refresh controls)
- Expand UI testing with Compose testing APIs

## Handover Notes

- **Scope**: MVP focuses on browsing, details, and favorites, with local caching
- **Extensibility**: Modularized setup supports future features (actor info, profiles, streaming, social features)
- **Offline Readiness**: Room cache and background sync allow offline browsing after first load
- **Multi-API**: Repository layer can be extended to ingest additional sources
