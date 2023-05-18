package com.example.myapplication

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.util.ArrayList

class RoutineHandler(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "MyDatabase"
        private const val TABLE_NAME = "Routines"
        private const val KEY_ID = "_id"
        private const val KEY_NAME = "routine_name"
        private const val KEY_LAST_RUN = "last_run"

        private const val CREATE_TABLE = """
        CREATE TABLE $TABLE_NAME (
            $KEY_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $KEY_NAME TEXT,
            $KEY_LAST_RUN TEXT
             )
    """
    }

    override fun onCreate(db: SQLiteDatabase?) {
        //creating table with fields
        db?.execSQL(CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }

    fun addRoutine(routine: RoutineModelClass): Long {
        //db is going to be used to perform writing operations on the database
        val db = this.writableDatabase

        //Storing the data in key-value format
        val contentValues = ContentValues()
        contentValues.put(KEY_NAME, routine.routineName)
        contentValues.put(KEY_LAST_RUN, routine.lastRun)

        val success = db.insert(TABLE_NAME, null, contentValues)

        db.close()

        return success
    }

    //Method to read the records from database in form of ArrayList
    fun viewRoutine(): ArrayList<RoutineModelClass> {
        val routineList: ArrayList<RoutineModelClass> = ArrayList<RoutineModelClass>()

        //Query to select all the records from the table
        val selectQuery = "SELECT * FROM $TABLE_NAME"

        //used to perform reading operations on the database
        val db = this.readableDatabase

        //Cursor is used to read the record one by one. Add them to data model class.
        var cursor: Cursor? = null

        try {
            //rawQuery method is used to execute SELECT statements
            cursor = db.rawQuery(selectQuery, null)
        } catch (e: SQLiteException) {
            Log.e("Database Error", "Error executing query: $selectQuery", e)
            return ArrayList()
        }

        var id: Int
        var routineName: String
        var lastRun: String

        //cursor used to traverse through a result set
        if (cursor.moveToFirst()) {
            do {
                id = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID))
                routineName = cursor.getString(cursor.getColumnIndexOrThrow(KEY_NAME))
                lastRun = cursor.getString(cursor.getColumnIndexOrThrow(KEY_LAST_RUN))


                val model = RoutineModelClass(
                    id = id,
                    routineName = routineName,
                    lastRun = lastRun,
                )

                routineList.add(model)
            } while (cursor.moveToNext())
        }
        return routineList
    }

    fun updateRoutine(routine: RoutineModelClass): Int {
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(KEY_NAME, routine.routineName)
        contentValues.put(KEY_LAST_RUN, routine.lastRun)

        val success = db.update(TABLE_NAME, contentValues, KEY_ID + "=" + routine.id, null)

        db.close()
        return success
    }

    fun deleteRoutine(routine: RoutineModelClass): Int {
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(KEY_ID, routine.id)

        val success = db.delete(TABLE_NAME, KEY_ID + "=" + routine.id, null)

        db.close()
        return success
    }
}