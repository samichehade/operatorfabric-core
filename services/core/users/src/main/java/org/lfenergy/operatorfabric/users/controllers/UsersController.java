/* Copyright (c) 2018, RTE (http://www.rte-france.com)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.lfenergy.operatorfabric.users.controllers;

import org.lfenergy.operatorfabric.springtools.configuration.oauth.UpdatedUserEvent;
import org.lfenergy.operatorfabric.springtools.error.model.ApiError;
import org.lfenergy.operatorfabric.springtools.error.model.ApiErrorException;
import org.lfenergy.operatorfabric.users.model.*;
import org.lfenergy.operatorfabric.users.repositories.UserRepository;
import org.lfenergy.operatorfabric.users.repositories.UserSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.bus.ServiceMatcher;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * UsersController, documented at {@link UsersApi}
 *
 * @author David Binder
 */
@RestController
@RequestMapping("/users")
@Slf4j
public class UsersController implements UsersApi {

    public static final String USER_NOT_FOUND_MSG = "User %s not found";
    public static final String USER_SETTINGS_NOT_FOUND_MSG = "User setting for user %s not found";
    public static final String NO_MATCHING_USER_NAME_MSG = "Payload User login does not match URL User login";
    
    public static final String USER_CREATED = "User %s is created";
    public static final String USER_UPDATED = "User %s is updated";
    
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserSettingsRepository userSettingsRepository;

    /* These are Spring Cloud Bus beans used to fire an event (UpdatedUserEvent) every time a user is modified.
     *  Other services handle this event by clearing their user cache for the given user. See issue #64*/
    @Autowired
    private ServiceMatcher busServiceMatcher;

    @Autowired
    private ApplicationEventPublisher publisher;

    @Override
    public SimpleUser createUser(HttpServletRequest request, HttpServletResponse response, SimpleUser user) throws Exception {
        boolean created = false;
        String login = user.getLogin();
        
        if(userRepository.findById(user.getLogin()).orElse(null)==null){
            response.addHeader("Location", request.getContextPath() + "/users/" + user.getLogin());
            response.setStatus(201);
            created = true;
            log.debug(String.format(USER_CREATED, login));
        } else {
        	log.debug(String.format(USER_UPDATED, login));
        }
        
        userRepository.save(new UserData(user));
        if(!created)
            publisher.publishEvent(new UpdatedUserEvent(this,busServiceMatcher.getServiceId(),login));
        
        return user;
    }

    @Override
    public User fetchUser(HttpServletRequest request, HttpServletResponse response, String login) throws Exception {
        return userRepository.findById(login)
                .orElseThrow(()-> new ApiErrorException(
                        ApiError.builder()
                                .status(HttpStatus.NOT_FOUND)
                                .message(String.format(USER_NOT_FOUND_MSG,login))
                                .build()
                ));
    }

    @Override
    public UserSettings fetchUserSetting(HttpServletRequest request, HttpServletResponse response, String login) throws Exception {
        return userSettingsRepository.findById(login)
                .orElseThrow(()->new ApiErrorException(
                        ApiError.builder()
                                .status(HttpStatus.NOT_FOUND)
                                .message(String.format(USER_SETTINGS_NOT_FOUND_MSG,login)).build()
                ));
    }

    @Override
    public List<? extends User> fetchUsers(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return userRepository.findAll();
    }

    @Override
    public UserSettings patchUserSettings(HttpServletRequest request, HttpServletResponse response, String login, UserSettings userSettings) throws Exception {
        UserSettingsData settings = userSettingsRepository.findById(login)
                .orElse(UserSettingsData.builder().login(login).build());
        return userSettingsRepository.save(settings.patch(userSettings));
    }

    @Override
    public UserSettings updateUserSettings(HttpServletRequest request, HttpServletResponse response, String login, UserSettings userSettings) throws Exception {
        if(!userSettings.getLogin().equals(login)) {
            throw new ApiErrorException(
                    ApiError.builder()
                            .status(HttpStatus.BAD_REQUEST)
                            .message(NO_MATCHING_USER_NAME_MSG)
                            .build());
        }
        return userSettingsRepository.save(new UserSettingsData(userSettings));
    }

    @Override
    public SimpleUser updateUser(HttpServletRequest request, HttpServletResponse response, String login, SimpleUser user) throws Exception {
        //login from user body parameter should match login path parameter
        if(!user.getLogin().equals(login)){
            throw new ApiErrorException(
                    ApiError.builder()
                            .status(HttpStatus.BAD_REQUEST)
                            .message(NO_MATCHING_USER_NAME_MSG)
                            .build());
        }

        return createUser(request, response, user);

    }
}
