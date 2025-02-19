/* Copyright (c) 2018, RTE (http://www.rte-france.com)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import {Injectable} from '@angular/core';
import {Actions, Effect, ofType} from '@ngrx/effects';
import {Action, Store} from '@ngrx/store';
import {Observable, of, zip} from 'rxjs';
import {catchError, map, switchMap} from 'rxjs/operators';
import {AppState} from "@ofStore/index";
import {ThirdsService} from "@ofServices/thirds.service";
import {
    LoadMenu,
    LoadMenuFailure,
    LoadMenuSuccess,
    MenuActionTypes,
    SelectMenuLink,
    SelectMenuLinkFailure,
    SelectMenuLinkSuccess
} from "@ofActions/menu.actions";
import {Router} from "@angular/router";

@Injectable()
export class MenuEffects {

    /* istanbul ignore next */
    constructor(private store: Store<AppState>,
                private actions$: Actions,
                private service: ThirdsService,
                private router: Router
    ) {
    }

    @Effect()
    load: Observable<Action> = this.actions$
        .pipe(
            ofType<LoadMenu>(MenuActionTypes.LoadMenu),
            switchMap(action =>  this.service.computeThirdsMenu()),
            map(menu =>
                new LoadMenuSuccess({menu: menu})
            ),
            catchError((err, caught) => {
                console.error(err);
                this.store.dispatch(new LoadMenuFailure(err));
                return caught;
            })
        );

    /**
     * This {Observable} listens for {MenuActionTypes.SelectMenuLink} type.
     * It then tries to get the corresponding menu link from the {ThirdsService}.
     * If successful, it fires a {SelectMenuLinkSuccess} action with the result as payload.
     * If not, it fires a {SelectMenuLinkFailure} action with the error as payload and navigates back to the index page
     *
     * @name resolveThirdPartyLink
     */
    @Effect()
    resolveThirdPartyLink: Observable<Action> = this.actions$
        .pipe(
            ofType<SelectMenuLink>(MenuActionTypes.SelectMenuLink),
            switchMap(action => this.service.queryMenuEntryURL(action.payload.menu_id, action.payload.menu_version, action.payload.menu_entry_id)),
            map(url =>
                new SelectMenuLinkSuccess({iframe_url: url})
            ),
            catchError(err => {
                console.error(err);
                this.router.navigate(['/']); //On error, redirect to index page
                return of(new SelectMenuLinkFailure(err));
            })
        );

}
