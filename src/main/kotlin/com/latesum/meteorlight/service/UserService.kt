package com.latesum.meteorlight.service

import com.latesum.meteorlight.dao.ActivateCodeDao
import com.latesum.meteorlight.dao.ResetPasswordCodeDao
import com.latesum.meteorlight.dao.UserDao
import com.latesum.meteorlight.dao.UserFavouriteDao
import com.latesum.meteorlight.exception.ServiceException
import com.latesum.meteorlight.exception.ServiceException.ExceptionType
import com.latesum.meteorlight.model.ActivateCode
import com.latesum.meteorlight.model.ResetPasswordCode
import com.latesum.meteorlight.model.User
import com.latesum.meteorlight.model.UserFavourite
import com.latesum.meteorlight.proto.ErrorProtos.Error
import com.latesum.meteorlight.proto.NewsModelProtos
import com.latesum.meteorlight.proto.UserModelProtos.UserProfile
import com.latesum.meteorlight.proto.UserServiceProtos.*
import com.latesum.meteorlight.util.UserServiceUtil
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
class UserService(private val userDao: UserDao,
                  private val userFavouriteDao: UserFavouriteDao,
                  private val activateCodeDao: ActivateCodeDao,
                  private val resetPasswordCodeDao: ResetPasswordCodeDao,
                  private val userServiceUtil: UserServiceUtil) {

    @Transactional(rollbackFor = arrayOf(Exception::class))
    fun postLogin(user: User) {
        // Update last login time.
        user.lastLoginAt = Instant.now()
        userDao.save(user)
    }

    @Throws(ServiceException::class)
    @Transactional(rollbackFor = arrayOf(Exception::class))
    fun addUser(request: AddUserRequest): AddUserResponse {
        if (!userServiceUtil.isValidEmail(request.email))
            throw ServiceException.newBuilder()
                    .setType(ExceptionType.INVALID_ARGUMENT)
                    .setMessage(Error.USER_SERVICE_INVALID_EMAIL.name).build()

        if (!userServiceUtil.isValidPassword(request.password))
            throw ServiceException.newBuilder()
                    .setType(ExceptionType.INVALID_ARGUMENT)
                    .setMessage(Error.USER_SERVICE_INVALID_PASSWORD.name).build()

        if (!userServiceUtil.isValidNickname(request.nickname))
            throw ServiceException.newBuilder()
                    .setType(ExceptionType.INVALID_ARGUMENT)
                    .setMessage(Error.USER_SERVICE_INVALID_NICKNAME.name).build()

        if (userDao.findByNickname(request.nickname) != null)
            throw ServiceException.newBuilder()
                    .setType(ExceptionType.INVALID_ARGUMENT)
                    .setMessage(Error.USER_SERVICE_DUPLICATED_NICKNAME.name).build()

        // Construct plain user and validate.
        var user = User(
                email = request.email,
                password = userServiceUtil.hashPassword(request.password),
                nickname = request.nickname,
                activated = false)
        try {
            // Save user to database.
            user = userDao.saveAndFlush(user)
        } catch(e: DataIntegrityViolationException) {
            throw ServiceException.newBuilder()
                    .setType(ExceptionType.INVALID_ARGUMENT)
                    .setMessage(Error.USER_SERVICE_DUPLICATED_EMAIL.name).build()
        }

        // Construct activate code.
        var activateCode = ActivateCode(user = user)

        // Save activate code to database.
        activateCode = activateCodeDao.save(activateCode)

        return AddUserResponse.newBuilder()
                .setUser(UserProfile.newBuilder()
                        .setId(user.id)
                        .setEmail(user.email)
                        .setNickname(user.nickname).build())
                .setActivateCode(activateCode.code).build()
    }

    @Throws(ServiceException::class)
    @Transactional(rollbackFor = arrayOf(Exception::class))
    fun activateUser(request: ActivateUserRequest): ActivateUserResponse {
        // Find activate code.
        val activateCode = activateCodeDao.findWithUserByCodeAndUserId(request.activateCode, request.userId) ?:
                throw ServiceException.newBuilder()
                        .setType(ExceptionType.INVALID_ARGUMENT)
                        .setMessage(Error.USER_SERVICE_INVALID_ACTIVATE_CODE.name).build()

        // Check if ActivateCode has been used.
        val user = activateCode.user!!
        if (user.activated) {
            throw ServiceException.newBuilder()
                    .setType(ExceptionType.INVALID_ARGUMENT)
                    .setMessage(Error.USER_SERVICE_ACTIVATED_USER.name).build()
        }

        // Activate user.
        user.activated = true
        userDao.save(user)

        return ActivateUserResponse.newBuilder()
                .setUserId(user.id).build()
    }

    @Throws(ServiceException::class)
    @Transactional(readOnly = true)
    fun getUserActivateCode(request: GetUserActivateCodeRequest): GetUserActivateCodeResponse {
        if (!userServiceUtil.isValidEmail(request.email))
            throw ServiceException.newBuilder()
                    .setType(ExceptionType.INVALID_ARGUMENT)
                    .setMessage(Error.USER_SERVICE_NONEXISTENT_EMAIL.name).build()

        // Get code.
        val result = activateCodeDao.findWithUserByUserEmail(request.email) ?:
                throw ServiceException.newBuilder()
                        .setType(ExceptionType.INVALID_ARGUMENT)
                        .setMessage(Error.USER_SERVICE_NONEXISTENT_EMAIL.name).build()

        // Check if the user is activated.
        if (result.user!!.activated) {
            throw ServiceException.newBuilder()
                    .setType(ExceptionType.INVALID_ARGUMENT)
                    .setMessage(Error.USER_SERVICE_ACTIVATED_USER.name).build()
        }

        return GetUserActivateCodeResponse.newBuilder()
                .setUserId(result.user!!.id)
                .setActivateCode(result.code).build()
    }

    @Throws(ServiceException::class)
    @Transactional(rollbackFor = arrayOf(Exception::class))
    fun generateResetPasswordCode(request: GenerateResetPasswordCodeRequest): GenerateResetPasswordCodeResponse {
        if (!userServiceUtil.isValidEmail(request.email))
            throw ServiceException.newBuilder()
                    .setType(ExceptionType.INVALID_ARGUMENT)
                    .setMessage(Error.USER_SERVICE_NONEXISTENT_EMAIL.name).build()

        // Gets user entity by email.
        val user = userDao.findByEmail(request.email) ?:
                throw ServiceException.newBuilder()
                        .setType(ExceptionType.INVALID_ARGUMENT)
                        .setMessage(Error.USER_SERVICE_NONEXISTENT_EMAIL.name).build()

        // If valid reset password exists.
        val oldResetPasswordCode = resetPasswordCodeDao.findFirstByUserIdOrderByIdDesc(user.id)
        if (oldResetPasswordCode != null &&
                !userServiceUtil.isExpiredResetPasswordCode(oldResetPasswordCode.createAt)) {
            return GenerateResetPasswordCodeResponse.newBuilder()
                    .setUserId(user.id)
                    .setResetPasswordCode(oldResetPasswordCode.code).build()
        }

        // Generate reset password code.
        val resetPasswordCode = ResetPasswordCode(user = user)
        resetPasswordCodeDao.saveAndFlush(resetPasswordCode)

        return GenerateResetPasswordCodeResponse.newBuilder()
                .setUserId(user.id)
                .setResetPasswordCode(resetPasswordCode.code).build()
    }

    @Throws(ServiceException::class)
    @Transactional(rollbackFor = arrayOf(Exception::class))
    fun resetPassword(request: ResetPasswordRequest): ResetPasswordResponse {
        if (!userServiceUtil.isValidPassword(request.newPassword))
            throw ServiceException.newBuilder()
                    .setType(ExceptionType.INVALID_ARGUMENT)
                    .setMessage(Error.USER_SERVICE_INVALID_PASSWORD.name).build()

        // Find by userId and resetPasswordCode.
        val resetPasswordCode = resetPasswordCodeDao.findFirstByUserIdAndCodeOrderByIdDesc(request.userId,
                request.resetPasswordCode)

        // Check if the code is expired (in 1 hour).
        if (resetPasswordCode == null || userServiceUtil.isExpiredResetPasswordCode(resetPasswordCode.createAt)) {
            throw ServiceException.newBuilder()
                    .setType(ExceptionType.INVALID_ARGUMENT)
                    .setMessage(Error.USER_SERVICE_INVALID_RESET_PASSWORD_CODE.name).build()
        }

        // Update user password.
        val user = userDao.findOne(request.userId)!!
        user.password = userServiceUtil.hashPassword(request.newPassword)
        userDao.save(user)

        return ResetPasswordResponse.newBuilder()
                .setUserId(user.id).build()
    }

    @Throws(ServiceException::class)
    @Transactional(rollbackFor = arrayOf(Exception::class))
    fun login(request: LoginRequest): LoginResponse {
        if (!userServiceUtil.isValidEmail(request.email))
            throw ServiceException.newBuilder()
                    .setType(ExceptionType.INVALID_ARGUMENT)
                    .setMessage(Error.USER_SERVICE_NONEXISTENT_EMAIL.name).build()

        if (!userServiceUtil.isValidPassword(request.password))
            throw ServiceException.newBuilder()
                    .setType(ExceptionType.INVALID_ARGUMENT)
                    .setMessage(Error.USER_SERVICE_WRONG_PASSWORD.name).build()

        // Find user by email.
        val user = userDao.findByEmail(request.email) ?:
                throw ServiceException.newBuilder()
                        .setType(ExceptionType.INVALID_ARGUMENT)
                        .setMessage(Error.USER_SERVICE_NONEXISTENT_EMAIL.name).build()

        // Wrong password.
        if (!userServiceUtil.checkPassword(request.password, user.password)) {
            throw ServiceException.newBuilder()
                    .setType(ExceptionType.INVALID_ARGUMENT)
                    .setMessage(Error.USER_SERVICE_WRONG_PASSWORD.name).build()
        }

        // Unactivated user.
        if (!user.activated) {
            throw ServiceException.newBuilder()
                    .setType(ExceptionType.INVALID_ARGUMENT)
                    .setMessage(Error.USER_SERVICE_UNACTIVATED_USER.name).build()
        }

        // Do some job after user login (e.g. update last login time).
        postLogin(user)

        val favourite = userFavouriteDao.findByUserId(user.id)
        val userProfile = UserProfile.newBuilder()
                .setId(user.id)
                .setEmail(user.email)
                .setNickname(user.nickname)
                .addAllFavourite(favourite.map {
                    it.favourite
                })

        return LoginResponse.newBuilder()
                .setUser(userProfile).build()
    }

    @Throws(ServiceException::class)
    @Transactional(rollbackFor = arrayOf(Exception::class))
    fun addUserFavourite(request: AddUserFavouriteRequest): AddUserFavouriteResponse {
        val user = userDao.findOne(request.userId) ?:
                throw ServiceException.newBuilder()
                        .setType(ExceptionType.INVALID_ARGUMENT)
                        .setMessage(Error.USER_NOT_FOUND.name).build()

        if (request.favourite == NewsModelProtos.NewsType.UNRECOGNIZED)
            throw ServiceException.newBuilder()
                    .setType(ExceptionType.INVALID_ARGUMENT)
                    .setMessage(Error.USER_SERVICE_INVALID_FAVOURITE.name).build()

        val userFavourite = userFavouriteDao.findByUserIdAndFavourite(user.id, request.favourite)
        if (userFavourite != null)
            throw ServiceException.newBuilder()
                    .setType(ExceptionType.INVALID_ARGUMENT)
                    .setMessage(Error.USER_SERVICE_DUPLICATED_FAVOURITE.name).build()

        userFavouriteDao.save(UserFavourite(
                user = user,
                favourite = request.favourite
        ))

        val favourite = userFavouriteDao.findByUserId(user.id)
        val userProfile = UserProfile.newBuilder()
                .setId(user.id)
                .setEmail(user.email)
                .setNickname(user.nickname)
                .addAllFavourite(favourite.map {
                    it.favourite
                })

        return AddUserFavouriteResponse.newBuilder()
                .setUser(userProfile).build()
    }

    @Throws(ServiceException::class)
    @Transactional(rollbackFor = arrayOf(Exception::class))
    fun removeUserFavourite(request: DeleteUserFavouriteRequest): DeleteUserFavouriteResponse {
        val user = userDao.findOne(request.userId) ?:
                throw ServiceException.newBuilder()
                        .setType(ExceptionType.INVALID_ARGUMENT)
                        .setMessage(Error.USER_NOT_FOUND.name).build()

        if (request.favourite == NewsModelProtos.NewsType.UNRECOGNIZED)
            throw ServiceException.newBuilder()
                    .setType(ExceptionType.INVALID_ARGUMENT)
                    .setMessage(Error.USER_SERVICE_INVALID_FAVOURITE.name).build()

        val userFavourite = userFavouriteDao.findByUserIdAndFavourite(user.id, request.favourite) ?:
                throw ServiceException.newBuilder()
                        .setType(ExceptionType.INVALID_ARGUMENT)
                        .setMessage(Error.USER_SERVICE_DUPLICATED_FAVOURITE.name).build()

        userFavouriteDao.delete(userFavourite)

        val favourite = userFavouriteDao.findByUserId(user.id)
        val userProfile = UserProfile.newBuilder()
                .setId(user.id)
                .setEmail(user.email)
                .setNickname(user.nickname)
                .addAllFavourite(favourite.map {
                    it.favourite
                })

        return DeleteUserFavouriteResponse.newBuilder()
                .setUser(userProfile).build()
    }

    @Throws(ServiceException::class)
    @Transactional(readOnly = true)
    fun getUser(request: GetUserRequest): GetUserResponse {
        val user = userDao.findOne(request.userId) ?:
                throw ServiceException.newBuilder()
                        .setType(ExceptionType.INVALID_ARGUMENT)
                        .setMessage(Error.USER_NOT_FOUND.name).build()

        val favourite = userFavouriteDao.findByUserId(user.id)
        val userProfile = UserProfile.newBuilder()
                .setId(user.id)
                .setEmail(user.email)
                .setNickname(user.nickname)
                .addAllFavourite(favourite.map {
                    it.favourite
                })

        return GetUserResponse.newBuilder()
                .setUser(userProfile).build()
    }

}
