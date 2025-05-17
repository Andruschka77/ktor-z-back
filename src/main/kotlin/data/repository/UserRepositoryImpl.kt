package com.example.data.repository

import com.example.data.model.FriendModel
import com.example.data.model.UserModel
import com.example.data.model.requests.FriendRequest
import com.example.data.model.tables.FriendTable
import com.example.data.model.tables.UserTable
import com.example.domain.repository.UserRepository
import com.example.plugins.DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class UserRepositoryImpl: UserRepository {

    override suspend fun getUserByEmail(email: String): UserModel? {
        return dbQuery {
            UserTable.select {UserTable.email.eq(email) }
                .map { rowToUser(row = it)  }
                .singleOrNull()
        }
    }

    override suspend fun insertUser(userModel: UserModel) {
        return dbQuery {
            UserTable.insert { table ->
                table[email] = userModel.email
                table[login] = userModel.login
                table[password] = userModel.password
                table[firstName] = userModel.firstName
                table[lastName] = userModel.lastName
                table[isActive] = userModel.isActive
                table[coordinates] = userModel.coordinates
            }
        }
    }

    private fun rowToUser(row: ResultRow?): UserModel? {
        if (row == null) {
            return null
        }
        return UserModel(
            id = row[UserTable.id],
            email = row[UserTable.email],
            login = row[UserTable.login],
            password = row[UserTable.password],
            firstName = row[UserTable.firstName],
            lastName = row[UserTable.lastName],
            isActive = row[UserTable.isActive],
            coordinates = row[UserTable.coordinates] ?: ""
        )
    }

    override suspend fun existsByLogin(login: String): Boolean = dbQuery {
        UserTable.select { UserTable.login eq login }.count() > 0
    }
}