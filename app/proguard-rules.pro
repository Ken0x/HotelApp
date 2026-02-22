# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Preserve line numbers for crash stack traces (R8/ProGuard).
-keepattributes SourceFile,LineNumberTable

# ---------------------------------------------------------------------------
# Gson: keep DTOs and deserializers used by Retrofit (field names for JSON).
# ---------------------------------------------------------------------------
-keep class com.example.hotelapp.data.remote.dto.HotelDto { *; }
-keep class com.example.hotelapp.data.remote.dto.HotelsSearchResponse { *; }
-keep class com.example.hotelapp.data.remote.dto.HotelsSearchResponseDeserializer { *; }

# Gson generic type token (TypeToken).
-keepattributes Signature
-keep class com.google.gson.reflect.TypeToken { *; }

# ---------------------------------------------------------------------------
# Room: entities and DAOs (Room AAR brings consumer rules; extra keeps if needed).
# ---------------------------------------------------------------------------
-keep class com.example.hotelapp.data.local.entity.HotelEntity { *; }
-keep class com.example.hotelapp.data.local.entity.BookingEntity { *; }
-keep interface com.example.hotelapp.data.local.dao.HotelDao { *; }
-keep interface com.example.hotelapp.data.local.dao.BookingDao { *; }

# ---------------------------------------------------------------------------
# Kotlin: metadata for serialization/reflection if used.
# ---------------------------------------------------------------------------
-keepattributes *Annotation*
-dontwarn kotlin.**
-dontwarn kotlinx.**