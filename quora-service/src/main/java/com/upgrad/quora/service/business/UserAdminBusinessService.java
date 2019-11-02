package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UnauthorizedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

@Service
public class UserAdminBusinessService {

    @Autowired
    private UserDao userDao;

    public UserEntity getUser(final String userUuid, final String authorizationToken) throws
            UnauthorizedException, AuthorizationFailedException, UserNotFoundException {

        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(authorizationToken);
        UserEntity userEntity = userDao.getUserByUuid(userUuid);
        if(userAuthTokenEntity == null)
        {
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");
        }

        ZonedDateTime loggedOutStatus = userAuthTokenEntity.getLogoutAt();
        ZonedDateTime loggedInStatus = userAuthTokenEntity.getLoginAt();
        if(loggedOutStatus == null || loggedOutStatus.isBefore(loggedInStatus) )
        {
            throw new UnauthorizedException("ATHR-002","User is signed out.Sign in first to get user details");
        }
        if(userEntity == null)
        {
            throw new UserNotFoundException("USR-001", "User with entered uuid does not exist");
        }

        return userEntity;
    }
}