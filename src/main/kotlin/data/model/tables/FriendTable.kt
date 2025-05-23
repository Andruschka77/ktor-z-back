package com.example.data.model.tables

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.substring

object FriendTable : Table(name = "friend_table") {
    val id: Column<Int> = integer("id").autoIncrement()
    val login_sender_receiver: Column<String> = varchar("login_sender_receiver", 50)
    val coordinates = varchar("coordinates", 100)
    val status = bool("status").default(false)

    override val primaryKey = PrimaryKey(id)

    init {
        index(
            isUnique = true,
            columns = arrayOf(login_sender_receiver, coordinates)
        )
    }
}
