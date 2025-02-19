/* Copyright (c) 2018, RTE (http://www.rte-france.com)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.lfenergy.operatorfabric.springtools.configuration.oauth;

import feign.Client;
import feign.Feign;
import feign.FeignException;
import feign.RequestInterceptor;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import org.lfenergy.operatorfabric.users.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;

//import org.springframework.boot.autoconfigure.security.oauth2.resource.AuthoritiesExtractor;
//import org.springframework.boot.autoconfigure.security.oauth2.resource.JwtAccessTokenConverterConfigurer;
//import org.springframework.boot.autoconfigure.security.oauth2.resource.PrincipalExtractor;
//import org.springframework.security.oauth2.client.OAuth2ClientContext;
//import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
//import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordResourceDetails;
//import org.springframework.security.oauth2.common.OAuth2AccessToken;
//import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
//import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
//import org.springframework.security.oauth2.provider.OAuth2Authentication;
//import org.springframework.security.oauth2.provider.token.AccessTokenConverter;
//import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
//import org.springframework.security.oauth2.provider.token.DefaultUserAuthenticationConverter;
//import org.springframework.security.oauth2.provider.token.UserAuthenticationConverter;
//import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

/**
 * Common configuration (MVC and Webflux)
 *
 * @author David Binder
 */
@Configuration
@EnableFeignClients
@EnableCaching
@EnableDiscoveryClient
@Import({UserServiceCache.class,BusConfiguration.class,UpdateUserEventListener.class,UpdatedUserEvent.class})
public class OAuth2GenericConfiguration {

    @Autowired
    protected UserServiceCache userServiceCache;
    @Autowired
    private ApplicationContext context;

    /**
     * Generates a converter that converts {@link Jwt} to {@link OpFabJwtAuthenticationToken} whose principal is  a
     * {@link User} model object
     *
     * @return Converter from {@link Jwt} to {@link OpFabJwtAuthenticationToken}
     */
    @Bean
    public Converter<Jwt, AbstractAuthenticationToken> opfabJwtConverter() {

        return new Converter<Jwt, AbstractAuthenticationToken>(){
            @Override
            public AbstractAuthenticationToken convert(Jwt jwt) throws FeignException {
                String principalId = jwt.getClaimAsString("sub");
                OAuth2JwtProcessingUtilities.token.set(jwt);
                User user = userServiceCache.fetchUserFromCacheOrProxy(principalId);
                OAuth2JwtProcessingUtilities.token.remove();
                List<GrantedAuthority> authorities = OAuth2JwtProcessingUtilities.computeAuthorities(user);
                return new OpFabJwtAuthenticationToken(jwt, user, authorities);
            }
        };
    }

    /**
     * Declares a Feign interceptor that adds oauth2 user authentication to headers
     * @return oauth2 feign interceptor
     */
    @Bean
    public RequestInterceptor oauth2FeignRequestInterceptor() {
        return new OAuth2FeignRequestInterceptor();
    }

    @Bean
    public UserServiceProxy userServiceProxy(Client client /*Encoder encoder, Decoder decoder, Contract contract,*/){
        return Feign.builder()
                .client(client)
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                .contract(new SpringMvcContract())
                .requestInterceptor(new OAuth2FeignRequestInterceptor())
                .target(UserServiceProxy.class,"http://USERS");

    }

}
