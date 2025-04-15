package com.example.data.model.tables

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object FriendTable : Table(name = "friend_table") {
    val id: Column<Int> = integer("id").autoIncrement()
    val login: Column<String> = varchar("login", 50).uniqueIndex()
    val firstName: Column<String?> = varchar("first_name", 50).nullable()
    val lastName: Column<String?> = varchar("last_name", 50).nullable()
    val coordinates = varchar("coordinates", 100)

    override val primaryKey = PrimaryKey(id)
}