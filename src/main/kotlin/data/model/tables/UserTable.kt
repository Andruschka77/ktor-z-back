package com.example.data.model.tables

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object UserTable: Table(name = "user_table") {
    val id: Column<Int> = integer("id").autoIncrement()
    val email: Column<String> = varchar("email", 100).uniqueIndex()
    val login: Column<String> = varchar("login", 50).uniqueIndex()
    val password: Column<String> = varchar("password", 50)
    val firstName: Column<String> = varchar("first_name", 50)
    val lastName: Column<String> = varchar("last_name", 50)
    val isActive: Column<Boolean> = bool("is_active")

    override val primaryKey: PrimaryKey = PrimaryKey(id)
}