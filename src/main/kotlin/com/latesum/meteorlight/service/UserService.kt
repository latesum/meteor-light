package com.latesum.meteorlight.service


import com.latesum.meteorlight.dao.ActivateCodeDao
import com.latesum.meteorlight.dao.ResetPasswordCodeDao
import com.latesum.meteorlight.dao.UserDao
import com.latesum.meteorlight.model.ActivateCode
import com.latesum.meteorlight.model.ResetPasswordCode
import com.latesum.meteorlight.model.User
import com.latesum.meteorlight.util.UserServiceUtil
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
class UserService(private val userDao: UserDao,
                  private val activateCodeDao: ActivateCodeDao,
                  private val resetPasswordCodeDao: ResetPasswordCodeDao,
                  private val userServiceUtil: UserServiceUtil) {
//
//    @Transactional(rollbackFor = arrayOf(Exception::class))
//    fun postLogin(user: User) {
//        // Update last login time.
//        user.lastLoginAt = Instant.now()
//        userDao.save(user)
//    }
//
//    @Throws(StatusException::class)
//    @Transactional(rollbackFor = arrayOf(Exception::class))
//    fun addUser(request: UserServiceProtos.AddUserRequest): UserServiceProtos.AddUserResponse {
//        // Construct plain user and validate.
//        var user = User(
//                email = request.email,
//                password = userServiceUtil.hashPassword(request.password),
//                nickname = request.nickname,
//                activated = false)
//
//        try {
//            // Save user to database.
//            user = userDao.saveAndFlush(user)
//        } catch(e: DataIntegrityViolationException) {
//            throw Status.INVALID_ARGUMENT
//                    .withDescription(.USER_SERVICE_DUPLICATED_EMAIL.name)
//                    .asException()
//        }
//
//        // Construct activate code.
//        var activateCode = ActivateCode(user = user)
//
//        // Save activate code to database.
//        activateCode = activateCodeDao.save(activateCode)
//
//        return AddUserResponse.newBuilder()
//                .setUser(UserProfile.newBuilder()
//                        .setId(user.id)
//                        .setEmail(user.email)
//                        .setNickname(user.nickname)
//                        .setFake(user.fake).build())
//                .setActivateCode(activateCode.code).build()
//    }
//
//    @Throws(StatusException::class)
//    @Transactional(rollbackFor = arrayOf(Exception::class))
//    fun activateUser(request: ActivateUserRequest): ActivateUserResponse {
//        // Find activate code.
//        val activateCode = activateCodeDao.findWithUserByCodeAndUserId(request.activateCode, request.userId) ?:
//                throw Status.INVALID_ARGUMENT
//                        .withDescription(CoreError.USER_SERVICE_INVALID_ACTIVATE_CODE.name)
//                        .asException()
//
//        // Check if ActivateCode has been used.
//        val user = activateCode.user!!
//        if (user.activated) {
//            throw Status.INVALID_ARGUMENT
//                    .withDescription(CoreError.USER_SERVICE_ACTIVATED_USER.name)
//                    .asException()
//        }
//
//        // Activate user.
//        user.activated = true
//        userDao.save(user)
//
//        // Initialize user.
//        initializeUser(user)
//
//        return ActivateUserResponse.newBuilder()
//                .setUserId(user.id).build()
//    }
//
//    @Throws(StatusException::class)
//    @ValidatedRequest(UserServiceRequestValidators.GetUserActivateCodeRequestValidator::class)
//    @Transactional(readOnly = true)
//    fun getUserActivateCode(request: GetUserActivateCodeRequest): GetUserActivateCodeResponse {
//        // Get code.
//        val result = activateCodeDao.findWithUserByUserEmail(request.email) ?:
//                throw Status.INVALID_ARGUMENT
//                        .withDescription(CoreError.USER_SERVICE_NONEXISTENT_EMAIL.name)
//                        .asException()
//
//        // Check if the user is activated.
//        if (result.user!!.activated) {
//            throw Status.INVALID_ARGUMENT
//                    .withDescription(CoreError.USER_SERVICE_ACTIVATED_USER.name)
//                    .asException()
//        }
//
//        return GetUserActivateCodeResponse.newBuilder()
//                .setUserId(result.user!!.id)
//                .setActivateCode(result.code).build()
//    }
//
//    @Throws(StatusException::class)
//    @ValidatedRequest(UserServiceRequestValidators.GenerateResetPasswordCodeRequestValidator::class)
//    @Transactional(rollbackFor = arrayOf(Exception::class))
//    fun generateResetPasswordCode(request: GenerateResetPasswordCodeRequest): GenerateResetPasswordCodeResponse {
//        // Gets user entity by email.
//        val user = userDao.findByEmail(request.email) ?:
//                throw Status.INVALID_ARGUMENT
//                        .withDescription(CoreError.USER_SERVICE_NONEXISTENT_EMAIL.name)
//                        .asException()
//
//        // If valid reset password exists.
//        val oldResetPasswordCode = resetPasswordCodeDao.findFirstByUserIdOrderByIdDesc(user.id)
//        if (oldResetPasswordCode != null &&
//                !userServiceUtil.isExpiredResetPasswordCode(oldResetPasswordCode.createAt)) {
//            return GenerateResetPasswordCodeResponse.newBuilder()
//                    .setUserId(user.id)
//                    .setResetPasswordCode(oldResetPasswordCode.code).build()
//        }
//
//        // Generate reset password code.
//        val resetPasswordCode = ResetPasswordCode(user = user)
//        resetPasswordCodeDao.saveAndFlush(resetPasswordCode)
//
//        return GenerateResetPasswordCodeResponse.newBuilder()
//                .setUserId(user.id)
//                .setResetPasswordCode(resetPasswordCode.code).build()
//    }
//
//    @Throws(StatusException::class)
//    @Transactional(rollbackFor = arrayOf(Exception::class))
//    @ValidatedRequest(UserServiceRequestValidators.ResetPasswordRequestValidator::class)
//    fun resetPassword(request: ResetPasswordRequest): ResetPasswordResponse {
//        // Find by userId and resetPasswordCode.
//        val resetPasswordCode = resetPasswordCodeDao.findFirstByUserIdAndCodeOrderByIdDesc(request.userId,
//                request.resetPasswordCode)
//
//        // TODO: Test this branch.
//        // Check if the code is expired (in 1 hour).
//        if (resetPasswordCode == null || userServiceUtil.isExpiredResetPasswordCode(resetPasswordCode.createAt)) {
//            throw Status.INVALID_ARGUMENT
//                    .withDescription(CoreError.USER_SERVICE_INVALID_RESET_PASSWORD_CODE.name)
//                    .asException()
//        }
//
//        // Update user password.
//        val user = userDao.findOne(request.userId)!!
//        user.password = userServiceUtil.hashPassword(request.newPassword)
//        userDao.save(user)
//
//        return ResetPasswordResponse.newBuilder()
//                .setUserId(user.id).build()
//    }
//
//    @Throws(StatusException::class)
//    @Transactional(rollbackFor = arrayOf(Exception::class))
//    @ValidatedRequest(UserServiceRequestValidators.LoginRequestValidator::class)
//    fun login(request: LoginRequest): LoginResponse {
//        // Find user by email.
//        val user = userDao.findByEmail(request.email) ?:
//                throw Status.INVALID_ARGUMENT
//                        .withDescription(CoreError.USER_SERVICE_NONEXISTENT_EMAIL.name)
//                        .asException()
//
//        // Wrong password.
//        if (!userServiceUtil.checkPassword(request.password, user.password)) {
//            throw Status.INVALID_ARGUMENT
//                    .withDescription(CoreError.USER_SERVICE_WRONG_PASSWORD.name)
//                    .asException()
//        }
//
//        // Unactivated user.
//        if (!user.activated) {
//            throw Status.INVALID_ARGUMENT
//                    .withDescription(CoreError.USER_SERVICE_UNACTIVATED_USER.name)
//                    .asException()
//        }
//
//        // Get user roles.
//        val roles = permissionService.getUserRoles(user, request.productId)
//
//        // Do some job after user login (e.g. update last login time).
//        postLogin(user)
//
//        return LoginResponse.newBuilder()
//                .setUser(UserProfileAndRoles.newBuilder()
//                        .setId(user.id)
//                        .setEmail(user.email)
//                        .setNickname(user.nickname)
//                        .setFake(user.fake)
//                        .addAllRoles(roles)).build()
//    }
//
//    @Throws(StatusException::class)
//    @Transactional(readOnly = true)
//    fun getUser(request: GetUserRequest): GetUserResponse {
//        // Find user by id.
//        val user = userDao.findOne(request.userId) ?:
//                throw Status.INVALID_ARGUMENT
//                        .withDescription(CoreError.USER_NOT_FOUND.name)
//                        .asException()
//
//        // Get user roles.
//        val roles = permissionService.getUserRoles(user, request.productId)
//
//        return GetUserResponse.newBuilder()
//                .setUser(UserProfileAndRoles.newBuilder()
//                        .setId(user.id)
//                        .setEmail(user.email)
//                        .setNickname(user.nickname)
//                        .setFake(user.fake)
//                        .addAllRoles(roles)).build()
//    }

}
