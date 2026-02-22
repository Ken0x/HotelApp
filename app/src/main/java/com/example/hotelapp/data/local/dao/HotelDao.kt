package com.example.hotelapp.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.hotelapp.data.local.entity.HotelEntity
import kotlinx.coroutines.flow.Flow

/** DAO za hotele; suspend za pisanje, Flow za reaktivno čitanje, PagingSource za paginaciju. */
@Dao
interface HotelDao {

    @Query("SELECT * FROM hotels WHERE city = :city ORDER BY name")
    fun getHotelsByCity(city: String): Flow<List<HotelEntity>>

    /**
     * Paginirana lista hotela u gradu. [minPrice]/[maxPrice]: -1 = bez filtra po cijeni.
     * Za min: (minPrice &lt; 0) OR (pricePerNight IS NOT NULL AND pricePerNight &gt;= minPrice).
     * Za max: (maxPrice &lt; 0) OR pricePerNight IS NULL OR pricePerNight &lt;= maxPrice.
     */
    @Query(
        "SELECT * FROM hotels WHERE city = :city " +
            "AND (:minPrice < 0 OR (pricePerNight IS NOT NULL AND pricePerNight >= :minPrice)) " +
            "AND (:maxPrice < 0 OR pricePerNight IS NULL OR pricePerNight <= :maxPrice) " +
            "ORDER BY name"
    )
    fun getHotelsByCityPaged(
        city: String,
        minPrice: Double,
        maxPrice: Double
    ): PagingSource<Int, HotelEntity>

    /**
     * Paginirana lista hotela u gradu dostupnih u rasponu [checkInDay]..[checkOutDay] (epoch day),
     * s opcionalnim filterom po cijeni. [minPrice]/[maxPrice]: -1 = bez filtra.
     */
    @Query(
        "SELECT * FROM hotels WHERE city = :city " +
            "AND availableFromDay IS NOT NULL AND availableToDay IS NOT NULL " +
            "AND availableFromDay <= :checkOutDay AND availableToDay >= :checkInDay " +
            "AND (:minPrice < 0 OR (pricePerNight IS NOT NULL AND pricePerNight >= :minPrice)) " +
            "AND (:maxPrice < 0 OR pricePerNight IS NULL OR pricePerNight <= :maxPrice) " +
            "ORDER BY name"
    )
    fun getHotelsByCityAndDateRangePaged(
        city: String,
        checkInDay: Long,
        checkOutDay: Long,
        minPrice: Double,
        maxPrice: Double
    ): PagingSource<Int, HotelEntity>

    @Query("SELECT * FROM hotels WHERE id = :id")
    fun getHotelById(id: String): Flow<HotelEntity?>

    @Query("SELECT * FROM hotels WHERE city = :city")
    suspend fun getHotelsByCityOnce(city: String): List<HotelEntity>

    @Query("SELECT * FROM hotels WHERE id = :id")
    suspend fun getHotelByIdOnce(id: String): HotelEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<HotelEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplace(entity: HotelEntity)

    @Query("DELETE FROM hotels")
    suspend fun deleteAll()

    @Query("DELETE FROM hotels WHERE city = :city")
    suspend fun deleteByCity(city: String)

    @Query("DELETE FROM hotels WHERE id = :id")
    suspend fun deleteById(id: String)

    /** Vraća sve hotele označene kao favoriti. */
    @Query("SELECT * FROM hotels WHERE isFavorite = 1 ORDER BY name")
    fun getFavoriteHotels(): Flow<List<HotelEntity>>

    /** Prebacuje isFavorite za hotel s [id]; nova vrijednost je negacija trenutne. */
    @Query("UPDATE hotels SET isFavorite = NOT isFavorite WHERE id = :id")
    suspend fun toggleFavorite(id: String)

    /** Postavlja isFavorite za hotel [id] na [value]. */
    @Query("UPDATE hotels SET isFavorite = :value WHERE id = :id")
    suspend fun setFavorite(id: String, value: Boolean)
}
