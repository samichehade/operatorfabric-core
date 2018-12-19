/* Copyright (c) 2018, RTE (http://www.rte-france.com)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
import {
    AcceptLogIn,
    PayloadForSuccessfulAuthentication,
    RejectLogIn
} from '@ofActions/authentication.actions';

import {TestBed, inject, async} from '@angular/core/testing';
import {provideMockActions} from '@ngrx/effects/testing';
import {Observable} from 'rxjs';

import {AuthenticationEffects} from './authentication.effects';
import {Actions} from '@ngrx/effects';
import SpyObj = jasmine.SpyObj;
import {AuthenticationService, CheckTokenResponse} from '@ofServices/authentication.service';
import createSpyObj = jasmine.createSpyObj;
import {Guid} from 'guid-typescript';
import {Store} from "@ngrx/store";
import {AppState} from "@ofStore/index";

describe('AuthenticationEffects', () => {
    let actions$: Observable<any>;
    let effects: AuthenticationEffects;
    let mockStore: Store<AppState>;
    let authenticationService: SpyObj<AuthenticationService>;

    beforeEach(async(() => {
        const authenticationServiceSpy = createSpyObj('authenticationService'
            , ['extractToken'
                , 'verifyExpirationDate'
                , 'clearAuthenticationInformation'
                ,'extractIdentificationInformation'
            ]);
        const storeSpy = createSpyObj('Store', ['dispatch']);
        TestBed.configureTestingModule({
            providers: [
                AuthenticationEffects,
                provideMockActions(() => actions$),
                {provide: AuthenticationService, useValue: authenticationServiceSpy},
                {provide: Store, useValue: storeSpy}
            ]
        });

        effects = TestBed.get(AuthenticationEffects);

    }));

    beforeEach(() => {
        actions$ = TestBed.get(Actions);
        authenticationService = TestBed.get(AuthenticationService);
        mockStore = TestBed.get(Store);
    });

    it('should be created', () => {
        expect(effects).toBeTruthy();
    });

    it('should send accept loginAction when handling successful login attempt', () => {
        const mockCheckTokenResponse = {sub:"",exp:0,client_id:""} as CheckTokenResponse;
        const mockIdInfo = new PayloadForSuccessfulAuthentication("",Guid.create(),"",new Date());
        authenticationService.extractIdentificationInformation.and.callFake(() => mockIdInfo);
        const expectedAction = new AcceptLogIn(mockIdInfo);
        expect(effects.handleLogInAttempt(mockCheckTokenResponse)).toEqual(expectedAction);
        expect(authenticationService.extractIdentificationInformation).toHaveBeenCalled();

    })

    it('should clear local storage of auth information when handling a failing login attempt', () => {
        expect(effects.handleLogInAttempt(null)).toEqual(new RejectLogIn( { denialReason: 'invalid token'}));
        expect(authenticationService.clearAuthenticationInformation).toHaveBeenCalled();
    })

    it('should clear local storage of auth information when sending RejectLogIn Action', () => {
        const errorMsg = 'test';
        expect(effects.handleRejectedLogin(errorMsg)).toEqual(new RejectLogIn( { denialReason: errorMsg}));
        expect(authenticationService.clearAuthenticationInformation).toHaveBeenCalled()
    })

});
