package com.example.data.repository

import com.example.data.model.FriendModel
import com.example.data.model.tables.FriendTable
import com.example.data.model.tables.UserTable
import com.example.domain.repository.FriendRepository
import com.example.plugins.DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like

class FriendRepositoryImpl : FriendRepository {

    override suspend fun sendRequest(senderLogin: String, receiverLogin: String): Boolean = dbQuery {
        val sender = UserTable.select { UserTable.login eq senderLogin }.singleOrNull()
            ?: return@dbQuery false
        val receiver = UserTable.select { UserTable.login eq receiverLogin }.singleOrNull()
            ?: return@dbQuery false

        FriendTable.insert {
            it[login] = senderLogin
            it[coordinates] = "PENDING:$receiverLogin|${sender[UserTable.coordinates]}|${receiver[UserTable.coordinates]}"
        }.insertedCount > 0
    }

    // Ответ на запрос
    override suspend fun respondToRequest(senderLogin: String, receiverLogin: String, accept: Boolean): Boolean = dbQuery {
        val request = FriendTable.select {
            (FriendTable.login eq senderLogin) and
                    (FriendTable.coordinates like "PENDING:$receiverLogin|%")
        }.firstOrNull() ?: return@dbQuery false

        FriendTable.update({ FriendTable.id eq request[FriendTable.id] }) {
            it[coordinates] = if (accept) {
                "ACCEPTED:$receiverLogin|${request[FriendTable.coordinates].split("|")[1]}|${request[FriendTable.coordinates].split("|")[2]}"
            } else {
                "REJECTED"
            }
        } > 0
    }

    // Получение друзей
    override suspend fun getFriends(userLogin: String): List<FriendModel> = dbQuery {
        FriendTable.select {
            (FriendTable.login eq userLogin) and
                    (FriendTable.coordinates like "ACCEPTED:%")
        }.mapNotNull { row ->
            val parts = row[FriendTable.coordinates].split("|")
            if (parts.size < 3) return@mapNotNull null

            FriendModel(
                id = row[FriendTable.id].toString(),
                login = parts[0].substringAfter("ACCEPTED:"),
                firstName = row[FriendTable.firstName] ?: "",
                lastName = row[FriendTable.lastName] ?: "",
                coordinates = parts[2]
            )
        }
    }

    // Удаление друга (сбрасываем координаты)
    override suspend fun removeFriend(userLogin: String, friendLogin: String): Boolean = dbQuery {
        FriendTable.deleteWhere {
            (login eq userLogin) and
                    (coordinates like "ACCEPTED:$friendLogin|%")
        } > 0
    }
}
