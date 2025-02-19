/* Copyright (c) 2018, RTE (http://www.rte-france.com)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.lfenergy.operatorfabric.cards.publication.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.Accessors;
import org.lfenergy.operatorfabric.cards.model.CardOperationTypeEnum;

import java.time.Instant;
import java.util.List;

/**
 * <p>Please use builder to instantiate</p>
 *
 * <p>Card Operation Model, documented at {@link CardOperation}</p>
 *
 * {@inheritDoc}
 *
 * @author David Binder
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardOperationData implements CardOperation {

    private Long number;
    private Instant publishDate;
    private CardOperationTypeEnum type;
    @Singular
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<String> cardIds;
    @Singular
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<? extends LightCard> cards;

    /**
     * Class used to encapsulate builder in order to bypass javadoc inability to handle annotation processor generated classes
     */
    @AllArgsConstructor
    public static class BuilderEncapsulator{
        @Accessors(fluent = true) @Getter
        private CardOperationData.CardOperationDataBuilder builder;
    }

    /**
     * Used to bypass javadoc inability to handle annotation processor generated classes
     * @return an encapsulated builder
     */
    public static BuilderEncapsulator encapsulatedBuilder(){
        return new BuilderEncapsulator(builder());
    }
}
