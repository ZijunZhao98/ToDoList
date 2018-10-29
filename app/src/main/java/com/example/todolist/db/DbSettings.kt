package com.example.todolist.db

import android.provider.BaseColumns

// This essentially holds static variables that we will need to reference when working with
// the database

class DbSettings {
    companion object {
        const val DB_NAME = "todo.db"
        const val DB_VERSION = 1
    }

    class DBEntry: BaseColumns {
        companion object {
            const val TABLE = "todos"
            const val ID = BaseColumns._ID
            const val COL_TITLE = "title"
            const val COL_CATEGORY = "category"
            const val COL_DETAIL = "detail"
            const val COL_DUE = "due"
            const val COL_IMPORTANCE = "importance"
            const val COL_DONE = "done"
            const val COL_DATE_COMPLETED = "time_completed"
        }
    }
}