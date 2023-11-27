package com.example.cw
import android.annotation.SuppressLint
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.Context
import android.content.ContentValues
import kotlin.random.Random


class AppDatabase(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_VERSION = 12
        private const val DATABASE_NAME = "AppDatabase.db"
        private const val TABLE_RESTAURANT = "restaurant"
        private const val TABLE_REVIEW = "review"
        private const val TABLE_USER = "user"
        private const val COLUMN_RESTAURANT_ID = "restaurantID"
        private const val COLUMN_REVIEW_ID = "reviewID"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_TEXT = "text"
        private const val COLUMN_IMAGE_PATH = "imagePath"
        private const val COLUMN_RATING = "rating"
        private const val COLUMN_USER_ID = "userID"
        private const val COLUMN_USERNAME = "username"
        private const val COLUMN_DESCRIPTION = "description"
        private const val COLUMN_PASSWORD = "password"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createRestaurantTableQuery = ("CREATE TABLE $TABLE_RESTAURANT " +
                "($COLUMN_RESTAURANT_ID INTEGER PRIMARY KEY, $COLUMN_NAME TEXT, " +
                "$COLUMN_IMAGE_PATH TEXT, $COLUMN_DESCRIPTION TEXT)") // Add COLUMN_DESCRIPTION
        db.execSQL(createRestaurantTableQuery)

        val createUserTableQuery = ("CREATE TABLE $TABLE_USER " +
                "($COLUMN_USER_ID INTEGER PRIMARY KEY, $COLUMN_USERNAME TEXT, $COLUMN_IMAGE_PATH TEXT, " +
                "$COLUMN_PASSWORD TEXT)")
        db.execSQL(createUserTableQuery)

        val createReviewTableQuery = ("CREATE TABLE $TABLE_REVIEW " +
                "($COLUMN_REVIEW_ID INTEGER PRIMARY KEY, $COLUMN_RESTAURANT_ID INTEGER, " +
                "$COLUMN_TEXT TEXT, $COLUMN_RATING INTEGER, $COLUMN_USER_ID INTEGER, " +
                "FOREIGN KEY($COLUMN_RESTAURANT_ID) REFERENCES $TABLE_RESTAURANT($COLUMN_RESTAURANT_ID), " +
                "FOREIGN KEY($COLUMN_USER_ID) REFERENCES $TABLE_USER($COLUMN_USER_ID))")
        db.execSQL(createReviewTableQuery)
    }



    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_REVIEW")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_RESTAURANT")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USER")
        onCreate(db)
    }

    fun addRestaurant(restaurant: Restaurant): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COLUMN_NAME, restaurant.name)
        contentValues.put(COLUMN_IMAGE_PATH, restaurant.imagePath)
        contentValues.put(COLUMN_DESCRIPTION, restaurant.description) // Add description field
        val insertedId = db.insert(TABLE_RESTAURANT, null, contentValues)
        db.close()
        return insertedId != -1L
    }


    fun addReview(review: Review, user: User): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COLUMN_RESTAURANT_ID, review.restaurant.restaurantID)
        contentValues.put(COLUMN_TEXT, review.text)
        contentValues.put(COLUMN_RATING, review.rating)
        contentValues.put(COLUMN_USER_ID, user.userID)
        val insertedId = db.insert(TABLE_REVIEW, null, contentValues)
        db.close()
        return insertedId != -1L
    }

    fun addUser(user: User): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COLUMN_USERNAME, user.username)
        contentValues.put(COLUMN_USER_ID, user.userID)
        contentValues.put(COLUMN_IMAGE_PATH, user.imagePath)
        contentValues.put(COLUMN_PASSWORD, user.password)
        val insertedId = db.insert(TABLE_USER, null, contentValues)
        db.close()
        return insertedId != -1L
    }

    fun getUserById(userID: Int): User {
        val db = this.readableDatabase

        val query = "SELECT * FROM $TABLE_USER WHERE $COLUMN_USER_ID = ?"
        val cursor = db.rawQuery(query, arrayOf(userID.toString()))

        var user = User("", 0, "", "")
        if (cursor.moveToFirst()) {
            val username = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME))
            val imgPath = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_PATH))
            val password = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD))
            user = User(username, userID, imgPath, password)
        }

        cursor.close()
        db.close()

        return user
    }

    fun getRestaurantById(restaurantID: Int): Restaurant {
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_RESTAURANT WHERE $COLUMN_RESTAURANT_ID = ?"
        val cursor = db.rawQuery(query, arrayOf(restaurantID.toString()))

        var restaurant = Restaurant("", 0, "", "")
        if (cursor.moveToFirst()) {
            val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME))
            val imagePath = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_PATH))
            val description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION))
            restaurant = Restaurant(name, restaurantID, imagePath, description)
        }

        cursor.close()
        db.close()

        return restaurant
    }
    fun reviewsByRestaurant(restaurantID: Int): ArrayList<Review> {
        val reviewsForRestaurant = ArrayList<Review>()
        val db = this.readableDatabase

        val query = "SELECT * FROM review WHERE restaurantID = ?"
        val cursor = db.rawQuery(query, arrayOf(restaurantID.toString()))

        while (cursor.moveToNext()) {
            val reviewID = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_REVIEW_ID))
            val text = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TEXT))
            val rating = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_RATING))
            val restaurant = getRestaurantById(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_RESTAURANT_ID)))
            val user = getUserById(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID)))
            val review = Review(text, reviewID, restaurant, rating, user)
            reviewsForRestaurant.add(review)
        }

        cursor.close()
        db.close()

        return reviewsForRestaurant
    }

    fun reviewsByUser(userID: Int): ArrayList<Review> {
        val reviewsByUser = ArrayList<Review>()
        val db = this.readableDatabase

        val query = "SELECT * FROM $TABLE_REVIEW WHERE $COLUMN_USER_ID = ?"
        val cursor = db.rawQuery(query, arrayOf(userID.toString()))

        while (cursor.moveToNext()) {
            val reviewID = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_REVIEW_ID))
            val text = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TEXT))
            val restaurantID = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_RESTAURANT_ID))
            val rating = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_RATING))

            val restaurant = getRestaurantById(restaurantID)
            val user = getUserById(userID)

            val review = Review(text, reviewID, restaurant, rating, user)
            reviewsByUser.add(review)
        }

        cursor.close()
        db.close()

        return reviewsByUser
    }

    fun getAllRestaurants(): ArrayList<Restaurant> {
        val restaurantList = ArrayList<Restaurant>()
        val db = this.readableDatabase

        val query = "SELECT * FROM $TABLE_RESTAURANT"
        val cursor = db.rawQuery(query, null)

        while (cursor.moveToNext()) {
            val restaurantID = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_RESTAURANT_ID))
            val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME))
            val imagePath = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_PATH))
            val description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION))

            val restaurant = Restaurant(name, restaurantID, imagePath, description)
            restaurantList.add(restaurant)
        }

        cursor.close()
        db.close()

        return restaurantList
    }

    fun populateDummyRestaurants() {
        val db = this.writableDatabase

        try {
            db.beginTransaction()

            // Dummy restaurant data
            val dummyRestaurants = listOf(
                Restaurant("Restaurant A", 11, "","f00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00df00d"),
                Restaurant("Restaurant B", 12, "","f00dzzz"),
                Restaurant("Restaurant C", 13, "","f00d"),
                Restaurant("Restaurant D", 14, "","f00d"),
                Restaurant("Restaurant E", 15, "","f00d")
            )
            val dummyUser = User("troll",1,"", "")
            for (restaurant in dummyRestaurants) {
                val contentValues = ContentValues()
                contentValues.put(COLUMN_RESTAURANT_ID, restaurant.restaurantID)
                contentValues.put(COLUMN_NAME, restaurant.name)
                contentValues.put(COLUMN_IMAGE_PATH, restaurant.imagePath)
                contentValues.put(COLUMN_DESCRIPTION, restaurant.description)

                val restaurantId = db.insert(TABLE_RESTAURANT, null, contentValues)
                val userContentValues = ContentValues()
                userContentValues.put(COLUMN_USERNAME, dummyUser.username)
                userContentValues.put(COLUMN_USER_ID, dummyUser.userID)
                userContentValues.put(COLUMN_IMAGE_PATH, dummyUser.imagePath)
                db.insert(TABLE_USER, null, userContentValues)
                val dummyReviews = listOf(
                    Review("TEST", Random.nextInt(0, 9999), restaurant, Random.nextInt(0, 6), dummyUser),
                    Review("TEST", Random.nextInt(0, 9999), restaurant, Random.nextInt(0, 6), dummyUser),
                    Review("TEST", Random.nextInt(0, 9999), restaurant, Random.nextInt(0, 6), dummyUser),
                    Review("TEST", Random.nextInt(0, 9999), restaurant, Random.nextInt(0, 6), dummyUser),
                    Review("TEST", Random.nextInt(0, 9999), restaurant, Random.nextInt(0, 6), dummyUser),
                    Review("TEST", Random.nextInt(0, 9999), restaurant, Random.nextInt(0, 6), dummyUser),
                    Review("TEST", Random.nextInt(0, 9999), restaurant, Random.nextInt(0, 6), dummyUser),
                    Review("TEST", Random.nextInt(0, 9999), restaurant, Random.nextInt(0, 6), dummyUser),
                    Review("TEST", Random.nextInt(0, 9999), restaurant, Random.nextInt(0, 6), dummyUser),
                    Review("TEST", Random.nextInt(0, 9999), restaurant, Random.nextInt(0, 6), dummyUser),
                    Review("TEST", Random.nextInt(0, 9999), restaurant, Random.nextInt(0, 6), dummyUser),
                    Review("TEST", Random.nextInt(0, 9999), restaurant, Random.nextInt(0, 6), dummyUser),
                    Review("OMG SO BAD OMG SO BAD OMG SO BAD OMG SO BAD OMG SO BAD OMG SO BAD OMG SO BAD OMG SO BAD OMG SO BAD OMG SO BAD OMG SO BAD OMG SO BAD OMG SO BAD OMG SO BAD OMG SO BAD OMG SO BAD OMG SO BAD OMG SO BAD OMG SO BAD OMG SO BAD OMG SO BAD OMG SO BAD OMG SO BAD OMG SO BAD OMG SO BAD OMG SO BAD OMG SO BAD OMG SO BAD OMG SO BAD OMG SO BAD OMG SO BAD OMG SO BAD OMG SO BAD OMG SO BAD OMG SO BAD OMG SO BAD OMG SO BAD OMG SO BAD OMG SO BAD OMG SO BAD OMG SO BAD OMG SO BAD ", Random.nextInt(0, 9999), restaurant, Random.nextInt(0, 6), dummyUser),
                    Review("TEST", Random.nextInt(0, 9999), restaurant, Random.nextInt(0, 6), dummyUser),
                )

                for (review in dummyReviews) {
                    val reviewValues = ContentValues()
                    reviewValues.put(COLUMN_RESTAURANT_ID, restaurantId)
                    reviewValues.put(COLUMN_TEXT, review.text)
                    reviewValues.put(COLUMN_RATING, review.rating)
                    reviewValues.put(COLUMN_USER_ID, review.user.userID)

                    db.insert(TABLE_REVIEW, null, reviewValues)
                }
            }

            db.setTransactionSuccessful()
        } catch (e: Exception) {
            // Handle exceptions or logging if necessary
        } finally {
            db.endTransaction()
            db.close()
        }
    }

    fun getRestaurantScore(restaurant: Restaurant): Double {
        val reviews = reviewsByRestaurant(restaurant.restaurantID)

        if (reviews.isEmpty()) {
            return 0.0
        }

        var totalRating = 0.0
        for (review in reviews) {
            totalRating += review.rating
        }

        return totalRating / reviews.size
    }

    fun getFreeUserID(): Int {
        val db = this.readableDatabase
        var freeUserID = 1

        val query = "SELECT MAX($COLUMN_USER_ID) AS maxUserID FROM $TABLE_USER"
        val cursor = db.rawQuery(query, null)

        if (cursor != null && cursor.moveToFirst()) {
            freeUserID = cursor.getInt(cursor.getColumnIndexOrThrow("maxUserID")) + 1
        }

        cursor?.close()
        db.close()

        return freeUserID
    }

    fun getFreeRestaurantID(): Int {
        val db = this.readableDatabase
        var freeRestaurantID = 1

        val query = "SELECT MAX($COLUMN_RESTAURANT_ID) AS maxRestaurantID FROM $TABLE_RESTAURANT"
        val cursor = db.rawQuery(query, null)

        if (cursor != null && cursor.moveToFirst()) {
            freeRestaurantID = cursor.getInt(cursor.getColumnIndexOrThrow("maxRestaurantID")) + 1
        }

        cursor?.close()
        db.close()

        return freeRestaurantID
    }

    fun getFreeReviewID(): Int {
        val db = this.readableDatabase
        var freeReviewID = 1

        val query = "SELECT MAX($COLUMN_REVIEW_ID) AS maxReviewID FROM $TABLE_REVIEW"
        val cursor = db.rawQuery(query, null)

        if (cursor != null && cursor.moveToFirst()) {
            freeReviewID = cursor.getInt(cursor.getColumnIndexOrThrow("maxReviewID")) + 1
        }

        cursor?.close()
        db.close()

        return freeReviewID
    }

    fun checkUsernameTaken(username: String): Boolean {
        val db = this.readableDatabase
        var isTaken = false

        val query = "SELECT EXISTS (SELECT 1 FROM $TABLE_USER WHERE $COLUMN_USERNAME = ?)"
        val cursor = db.rawQuery(query, arrayOf(username))

        cursor?.let {
            if (it.moveToFirst()) {
                val exists = it.getInt(0)
                isTaken = exists == 1
            }
        }

        cursor?.close()
        db.close()

        return isTaken
    }


}