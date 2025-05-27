package com.example.data.repository

import com.example.data.model.FriendModel
import com.example.data.model.requests.FriendRequest
import com.example.data.model.tables.FriendTable
import com.example.data.model.tables.UserTable
import com.example.domain.repository.FriendRepository
import com.example.plugins.DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like

class FriendRepositoryImpl : FriendRepository {

    override suspend fun sendRequest(senderLogin: String, receiverLogin: String): Boolean = dbQuery {
        if (senderLogin == receiverLogin) return@dbQuery false

        val existing = FriendTable.select {
            (FriendTable.coordinates like "PENDING:$senderLogin|$receiverLogin%") or
                    (FriendTable.coordinates like "PENDING:$receiverLogin|$senderLogin%")
        }.firstOrNull()

        if (existing != null) return@dbQuery false

        FriendTable.insert {
            it[login_sender_receiver] = receiverLogin
            it[coordinates] = "PENDING:$senderLogin|$receiverLogin"
            it[status] = false
        }.insertedCount > 0
    }

    // Ответ на запрос
    override suspend fun respondToRequest(senderLogin: String, receiverLogin: String, accept: Boolean): Boolean = dbQuery {
        val request = FriendTable.select {
            (FriendTable.coordinates eq "PENDING:$senderLogin|$receiverLogin") and
                    (FriendTable.status eq false)
        }.firstOrNull() ?: return@dbQuery false

        if (accept) {
            val senderCoords = UserTable.select { UserTable.login eq senderLogin }
                .firstOrNull()?.get(UserTable.coordinates) ?: return@dbQuery false
            val receiverCoords = UserTable.select { UserTable.login eq receiverLogin }
                .firstOrNull()?.get(UserTable.coordinates) ?: return@dbQuery false

            listOf(
                senderLogin to "$receiverLogin|$receiverCoords",
                receiverLogin to "$senderLogin|$senderCoords"
            ).forEach { (login, data) ->
                FriendTable.insert {
                    it[login_sender_receiver] = login
                    it[coordinates] = data
                    it[status] = true
                }
            }
            FriendTable.deleteWhere { FriendTable.id eq request[FriendTable.id] }
            true
        } else {
            FriendTable.deleteWhere { FriendTable.id eq request[FriendTable.id] } > 0
        }
    }

    // Получение друзей
    override suspend fun getFriends(userLogin: String): List<FriendModel> = dbQuery {
        FriendTable.select {
            (FriendTable.login_sender_receiver eq userLogin) and
                    (FriendTable.status eq true)
        }.map { row ->
            val parts = row[FriendTable.coordinates].split("|")
            val friendLogin = parts.getOrElse(0) { "" }
            val friendCoordinates = parts.getOrElse(1) { "" }

            UserTable.select { UserTable.login eq friendLogin }.first().let { userRow ->
                FriendModel(
                    id = row[FriendTable.id].toString(),
                    login = friendLogin,
                    firstName = userRow[UserTable.firstName],
                    lastName = userRow[UserTable.lastName],
                    coordinates = friendCoordinates
                )
            }
        }
    }

    // Удаление друга
    override suspend fun removeFriend(userLogin: String, friendLogin: String): Boolean = dbQuery {
        val deleteCurrentUser = FriendTable.deleteWhere {
            (login_sender_receiver eq userLogin) and
                    (coordinates like "$friendLogin|%")
        }
        val deleteFriend = FriendTable.deleteWhere {
            (login_sender_receiver eq friendLogin) and
                    (coordinates like "$userLogin|%")
        }
        (deleteCurrentUser > 0) || (deleteFriend > 0)
    }

    // Получение списка запросов в друзья
    override suspend fun getPendingRequests(userLogin: String): List<FriendRequest> = dbQuery {
        FriendTable.select {
            (FriendTable.coordinates like "PENDING:%|$userLogin") and
                    (FriendTable.status eq false)
        }.map { row ->
            val senderLogin = row[FriendTable.coordinates]
                .substringAfter("PENDING:")
                .substringBefore("|")
            FriendRequest(senderLogin = senderLogin)
        }
    }

}
