/* Copyright (c) 2018, RTE (http://www.rte-france.com)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import {createEntityAdapter, EntityAdapter, EntityState} from '@ngrx/entity';
import {LightCard, severityOrdinal} from '@ofModel/light-card.model';
import {Filter} from "@ofModel/feed-filter.model";
import {FilterType} from "@ofServices/filter.service";

/**
 * The Feed State consist of:
 *  * EntityState of LightCard
 *  * selectedCardId: the currently selected card id
 *  * lastCards the last cards added / updated to the feed
 *  * loading: whether there is an ongoing state modification
 *  * message: last message during state processing
 *  * filters: a collection of filter to apply to the rendered feed
 */
export interface CardFeedState extends EntityState<LightCard> {
    selectedCardId: string;
    lastCards: LightCard[];
    loading: boolean;
    error: string;
    filters: Map<FilterType,Filter>;
}

export function compareByStartDate(card1: LightCard, card2: LightCard){
    return card1.startDate - card2.startDate
}

export function compareBySeverity(card1: LightCard, card2: LightCard){
    return severityOrdinal(card1.severity) - severityOrdinal(card2.severity);
}

export function compareByLttd(card1: LightCard, card2: LightCard){
    return card1.lttd - card2.lttd;
}

export function compareByPublishDate(card1: LightCard, card2: LightCard){
    return card1.publishDate - card2.publishDate;
}

export function compareBySeverityLttdPublishDate(card1: LightCard, card2: LightCard){
    let result = compareBySeverity(card1,card2);
    if(result==0)
        result = compareByLttd(card1,card2);
    if(result == 0)
        result = compareByPublishDate(card1,card2);
    return result;
}

export const LightCardAdapter: EntityAdapter<LightCard> = createEntityAdapter<LightCard>({
    sortComparer:compareBySeverityLttdPublishDate
});

export const feedInitialState: CardFeedState = LightCardAdapter.getInitialState(
    {
        selectedCardId: null,
        lastCards: [],
        loading: false,
        error: '',
        filters: new Map()
    });
