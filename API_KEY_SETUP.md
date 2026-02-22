# API key setup

The app uses an optional API key for the hotels backend.

1. Create or edit `local.properties` in the project root (it is gitignored).
2. Add a line:
   ```
   HOTELS_API_KEY=your_api_key_here
   ```
3. Rebuild the project. The value is exposed via `BuildConfig.HOTELS_API_KEY`.

If the key is missing or empty, the app may still run with mock or cached data depending on the API implementation.
