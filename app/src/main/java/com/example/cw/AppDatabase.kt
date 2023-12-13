package com.example.cw
import android.annotation.SuppressLint
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.Context
import android.content.ContentValues
import org.mindrot.jbcrypt.BCrypt
import kotlin.random.Random


class AppDatabase(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_VERSION = 48
        private const val DATABASE_NAME = "AppDatabase.db"
        private const val TABLE_RESTAURANT = "restaurant"
        private const val TABLE_REVIEW = "review"
        private const val TABLE_USER = "user"
        private const val TABLE_BLOCKS = "blocks"
        private const val COLUMN_RESTAURANT_ID = "restaurantID"
        private const val COLUMN_REVIEW_ID = "reviewID"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_TEXT = "text"
        private const val COLUMN_IMAGE_PATH = "imagePath"
        private const val COLUMN_RATING = "rating"
        private const val COLUMN_USER_ID = "userID"
        private const val COLUMN_USERNAME = "username"
        private const val COLUMN_DESCRIPTION = "description"
        private const val COLUMN_IMAGES = "images"
        private const val TABLE_REPORT = "report"
        private const val COLUMN_REPORT_ID = "reportID"
        private const val COLUMN_BLOCKER_ID = "blockerID"
        private const val COLUMN_BLOCKED_ID= "blockedID"

    }

    override fun onCreate(db: SQLiteDatabase) {
        val createRestaurantTableQuery = ("CREATE TABLE $TABLE_RESTAURANT " +
                "($COLUMN_RESTAURANT_ID INTEGER PRIMARY KEY, $COLUMN_NAME TEXT, " +
                "$COLUMN_IMAGE_PATH TEXT, $COLUMN_DESCRIPTION TEXT)")
        db.execSQL(createRestaurantTableQuery)

        val createUserTableQuery = ("CREATE TABLE $TABLE_USER " +
                "($COLUMN_USER_ID TEXT PRIMARY KEY, $COLUMN_USERNAME TEXT, $COLUMN_IMAGE_PATH TEXT)")
        db.execSQL(createUserTableQuery)

        val createReviewTableQuery = ("CREATE TABLE $TABLE_REVIEW " +
                "($COLUMN_REVIEW_ID INTEGER PRIMARY KEY, $COLUMN_RESTAURANT_ID INTEGER, " +
                "$COLUMN_TEXT TEXT, $COLUMN_RATING INTEGER, $COLUMN_USER_ID TEXT, $COLUMN_IMAGES TEXT, " +
                "FOREIGN KEY($COLUMN_RESTAURANT_ID) REFERENCES $TABLE_RESTAURANT($COLUMN_RESTAURANT_ID), " +
                "FOREIGN KEY($COLUMN_USER_ID) REFERENCES $TABLE_USER($COLUMN_USER_ID))")
        db.execSQL(createReviewTableQuery)

        val createReportTableQuery = ("CREATE TABLE $TABLE_REPORT " +
                "($COLUMN_REPORT_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_REVIEW_ID INTEGER, $COLUMN_USER_ID TEXT, " +
                "FOREIGN KEY($COLUMN_REVIEW_ID) REFERENCES $TABLE_REVIEW($COLUMN_REVIEW_ID), " +
                "FOREIGN KEY($COLUMN_USER_ID) REFERENCES $TABLE_USER($COLUMN_USER_ID))")
        db.execSQL(createReportTableQuery)

        val createBlockTableQuery = ("CREATE TABLE $TABLE_BLOCKS " +
                "($COLUMN_BLOCKER_ID TEXT, $COLUMN_BLOCKED_ID TEXT)")

        db.execSQL(createBlockTableQuery)


    }


    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_REVIEW")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_RESTAURANT")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USER")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_REPORT")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_BLOCKS")
        onCreate(db)
    }

    fun addBlockedUser(blockerID: String, blockedID: String): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COLUMN_BLOCKER_ID, blockerID)
        contentValues.put(COLUMN_BLOCKED_ID, blockedID)

        val insertedId = db.insert(TABLE_BLOCKS, null, contentValues)
        db.close()
        return insertedId != -1L
    }

    fun getUserBlocks(userID: String): ArrayList<String> {
        val blockedUsers = ArrayList<String>()
        val db = this.readableDatabase

        val query = "SELECT $COLUMN_BLOCKED_ID FROM $TABLE_BLOCKS WHERE $COLUMN_BLOCKER_ID = ?"
        val cursor = db.rawQuery(query, arrayOf(userID))

        while (cursor.moveToNext()) {
            val blockedUserID = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BLOCKED_ID))
            blockedUsers.add(blockedUserID)
        }

        cursor.close()
        db.close()

        return blockedUsers
    }

    fun removeBlock(blockerID: String, blockedID: String): Boolean {
        val db = this.writableDatabase

        val deletedRows = db.delete(
            TABLE_BLOCKS,
            "$COLUMN_BLOCKER_ID = ? AND $COLUMN_BLOCKED_ID = ?",
            arrayOf(blockerID, blockedID)
        )

        db.close()

        return deletedRows > 0
    }

    fun addRestaurant(restaurant: Restaurant): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COLUMN_NAME, restaurant.name)
        contentValues.put(COLUMN_IMAGE_PATH, restaurant.imagePath)
        contentValues.put(COLUMN_DESCRIPTION, restaurant.description)
        val insertedId = db.insert(TABLE_RESTAURANT, null, contentValues)
        db.close()
        return insertedId != -1L
    }


    fun addReview(review: Review): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COLUMN_RESTAURANT_ID, review.restaurant.restaurantID)
        contentValues.put(COLUMN_TEXT, review.text)
        contentValues.put(COLUMN_RATING, review.rating)
        contentValues.put(COLUMN_USER_ID, review.user.userID)
        contentValues.put(COLUMN_IMAGES, review.images)

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
        val insertedId = db.insert(TABLE_USER, null, contentValues)
        db.close()
        return insertedId != -1L
    }

    fun addReport(report: Report): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COLUMN_REVIEW_ID, report.review.reviewID)
        contentValues.put(COLUMN_USER_ID, report.user.userID)
        contentValues.put(COLUMN_REPORT_ID, report.reportID)

        val insertedId = db.insert(TABLE_REPORT, null, contentValues)
        db.close()
        return insertedId != -1L
    }

    fun removeReport(reviewID: Int): Boolean {
        val db = this.writableDatabase

        val deletedRows = db.delete(TABLE_REPORT, "$COLUMN_REVIEW_ID = ?", arrayOf(reviewID.toString()))

        db.close()

        return deletedRows > 0
    }



    fun getUserById(userID: String): User {
        val db = this.readableDatabase

        val query = "SELECT * FROM $TABLE_USER WHERE $COLUMN_USER_ID = ?"
        val cursor = db.rawQuery(query, arrayOf(userID))

        var user = User("", "", "")
        if (cursor.moveToFirst()) {
            val username = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME))
            val imgPath = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_PATH))
            val safeUsername = username ?: ""
            val safeImgPath = imgPath ?: ""
            user = User(safeUsername, userID, safeImgPath)
        }

        cursor.close()
        db.close()

        return user
    }

    fun getUserByName(userName: String): User {
        val db = this.readableDatabase

        val query = "SELECT * FROM $TABLE_USER WHERE $COLUMN_USERNAME = ?"
        val cursor = db.rawQuery(query, arrayOf(userName))

        var user = User("", "", "")
        if (cursor.moveToFirst()) {
            val userID = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_ID))
            val imgPath = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_PATH))
            user = User(userName, userID, imgPath)
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

    fun getReviewById(reviewID: Int): Review? {
        val db = this.readableDatabase
        var review: Review? = null
        val query = "SELECT * FROM $TABLE_REVIEW WHERE $COLUMN_REVIEW_ID = ?"
        val cursor = db.rawQuery(query, arrayOf(reviewID.toString()))

        if (cursor.moveToFirst()) {
            val text = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TEXT))
            val rating = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_RATING))
            val restaurantID = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_RESTAURANT_ID))
            val userID = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_ID))
            val images = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGES))
            val restaurant = getRestaurantById(restaurantID)
            val user = getUserById(userID)
            review = Review(text, reviewID, restaurant, rating, user, images)
        }

        cursor.close()
        db.close()

        return review
    }

    fun getReportById(reportID: Int): Report? {
        val db = this.readableDatabase
        var report: Report? = null

        val query = "SELECT * FROM $TABLE_REPORT WHERE $COLUMN_REPORT_ID = ?"
        val cursor = db.rawQuery(query, arrayOf(reportID.toString()))

        if (cursor.moveToFirst()) {
            val reviewID = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_REVIEW_ID))
            val userID = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_ID))

            val review = getReviewById(reviewID)
            val user = getUserById(userID)

            report = Report(reportID, review!!, user)
        }

        cursor.close()
        db.close()

        return report
    }

    fun getAllReports(): ArrayList<Report> {
        val reportList = ArrayList<Report>()
        val db = this.readableDatabase

        val query = "SELECT * FROM $TABLE_REPORT"
        val cursor = db.rawQuery(query, null)

        while (cursor.moveToNext()) {
            val reviewID = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_REVIEW_ID))
            val userID = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_ID))
            val reportID = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_REPORT_ID))

            val review = getReviewById(reviewID)
            val user = getUserById(userID)

            val report = Report(reportID, review!!, user)
            reportList.add(report)
        }

        cursor.close()
        db.close()

        return reportList
    }

    fun deleteUserByID(userID: String): Boolean {
        val db = this.writableDatabase
        val deletedRows = db.delete(TABLE_USER, "$COLUMN_USER_ID = ?", arrayOf(userID))
        db.close()
        return deletedRows > 0
    }

    fun deleteReviewByID(reviewID: Int): Boolean {
        val db = this.writableDatabase
        val deletedRows = db.delete(TABLE_REVIEW, "$COLUMN_REVIEW_ID = ?", arrayOf(reviewID.toString()))
        db.close()
        return deletedRows > 0
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
            val user = getUserById(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_ID)))
            val images = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGES))
            val review = Review(text, reviewID, restaurant, rating, user, images)
            reviewsForRestaurant.add(review)
        }

        cursor.close()
        db.close()

        return reviewsForRestaurant
    }

    fun reviewsByUser(userID: String): ArrayList<Review> {
        val reviewsByUser = ArrayList<Review>()
        val db = this.readableDatabase

        val query = "SELECT * FROM $TABLE_REVIEW WHERE $COLUMN_USER_ID = ?"
        val cursor = db.rawQuery(query, arrayOf(userID.toString()))

        while (cursor.moveToNext()) {
            val reviewID = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_REVIEW_ID))
            val text = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TEXT))
            val restaurantID = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_RESTAURANT_ID))
            val rating = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_RATING))
            val images = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGES))

            val restaurant = getRestaurantById(restaurantID)
            val user = getUserById(userID)

            val review = Review(text, reviewID, restaurant, rating, user, images)
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

    fun getFreeReportID(): Int {
        val db = this.readableDatabase
        var freeReportID = 1

        val query = "SELECT MAX($COLUMN_REPORT_ID) AS maxReportID FROM $TABLE_REPORT"
        val cursor = db.rawQuery(query, null)

        if (cursor != null && cursor.moveToFirst()) {
            freeReportID = cursor.getInt(cursor.getColumnIndexOrThrow("maxReportID")) + 1
        }

        cursor?.close()
        db.close()

        return freeReportID
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

    fun updateReview(review: Review): Boolean {
        val db = this.writableDatabase

        val existingReview = getReviewById(review.reviewID)

        if (existingReview != null) {
            val deleted = deleteReviewByID(review.reviewID)

            if (deleted) {
                val user = existingReview.user
                return addReview(review)
            }
        }

        db.close()
        return false
    }

    fun updateUser(user: User): Boolean {
        val db = this.writableDatabase

        val existingUser = getUserById(user.userID)

        if (existingUser != null) {
            val deleted = deleteUserByID(user.userID)

            if (deleted) {
                return addUser(user)
            }
        }

        db.close()
        return false
    }


}