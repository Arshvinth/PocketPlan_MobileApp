package com.example.pocketplan_2
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "app_database"
        private const val DATABASE_VERSION = 1
        const val TABLE_USERS = "users"
        const val COLUMN_ID = "id"
        const val COLUMN_USERNAME = "username"
        const val COLUMN_EMAIL = "email"
        const val COLUMN_PASSWORD = "password"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery = "CREATE TABLE $TABLE_USERS (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_USERNAME TEXT, " +
                "$COLUMN_EMAIL TEXT, " +
                "$COLUMN_PASSWORD TEXT)"
        db?.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }

    fun addUser(username: String, email: String, password: String) {
        val db = writableDatabase
        val insertQuery = "INSERT INTO $TABLE_USERS ($COLUMN_USERNAME, $COLUMN_EMAIL, $COLUMN_PASSWORD) VALUES (?, ?, ?)"
        val statement = db.compileStatement(insertQuery)
        statement.bindString(1, username)
        statement.bindString(2, email)
        statement.bindString(3, password)
        statement.executeInsert()
    }

    fun checkUser(username: String, password: String): Boolean {
        val db = readableDatabase
        val selectQuery = "SELECT * FROM $TABLE_USERS WHERE $COLUMN_USERNAME = ? AND $COLUMN_PASSWORD = ?"
        val cursor = db.rawQuery(selectQuery, arrayOf(username, password))
        return cursor.count > 0
    }
}


