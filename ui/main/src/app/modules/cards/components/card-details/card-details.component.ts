/* Copyright (c) 2018, RTE (http://www.rte-france.com)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import {Component, OnInit} from '@angular/core';
import {Card, Detail} from '@ofModel/card.model';
import {Store} from '@ngrx/store';
import {AppState} from '@ofStore/index';
import * as cardSelectors from '@ofStore/selectors/card.selectors';
import {ThirdsService} from "@ofServices/thirds.service";
import { ClearLightCardSelection } from '@ofStore/actions/light-card.actions';
import { Router } from '@angular/router';
import {selectCurrentUrl} from '@ofStore/selectors/router.selectors';
@Component({
    selector: 'of-card-details',
    templateUrl: './card-details.component.html',
})
export class CardDetailsComponent implements OnInit {

    card: Card;
    details: Detail[];
    currentPath: any;

    constructor(private store: Store<AppState>,
        private thirdsService: ThirdsService,
        private router: Router) {
    }

    ngOnInit() {
        this.store.select(cardSelectors.selectCardStateSelected)
            .subscribe(card => {
                this.card = card;
                if (card) {
                    if (card.details) {
                        this.details = [...card.details];
                    } else {
                        this.details = [];
                    }
                    this.thirdsService.queryThird(this.card.publisher, this.card.publisherVersion).subscribe(third => {
                            if (third) {
                                const state = third.extractState(this.card);
                                if (state != null) {
                                    this.details.push(...state.details);
                                }
                            }
                        },
                        error => console.log(`something went wrong while trying to fetch third for ${this.card.publisher} with ${this.card.publisherVersion} version.`))
                    ;
                }
            });
            this.store.select(selectCurrentUrl).subscribe(url => {
                if (url) {
                    const urlParts = url.split('/');
                    this.currentPath = urlParts[1];
                }
            });
    }
    closeDetails() {
        this.store.dispatch(new ClearLightCardSelection());
        this.router.navigate(['/' + this.currentPath, 'cards']);
    }


}
