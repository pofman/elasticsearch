/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.elasticsearch.search.aggregations;


import org.elasticsearch.action.support.ToXContentToBytes;
import org.elasticsearch.common.ParseField;
import org.elasticsearch.common.io.stream.NamedWriteable;
import org.elasticsearch.common.xcontent.ToXContent;
import org.elasticsearch.index.query.QueryParseContext;
import org.elasticsearch.search.internal.SearchContext;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * A factory that knows how to create an {@link Aggregator} of a specific type.
 */
public abstract class AggregationBuilder
    extends ToXContentToBytes
    implements NamedWriteable, ToXContent, BaseAggregationBuilder {

    protected final String name;
    protected AggregatorFactories.Builder factoriesBuilder = AggregatorFactories.builder();

    /**
     * Constructs a new aggregation builder.
     *
     * @param name  The aggregation name
     */
    protected AggregationBuilder(String name) {
        if (name == null) {
            throw new IllegalArgumentException("[name] must not be null: [" + name + "]");
        }
        this.name = name;
    }

    /** Return this aggregation's name. */
    public String getName() {
        return name;
    }

    /** Internal: build an {@link AggregatorFactory} based on the configuration of this builder. */
    protected abstract AggregatorFactory<?> build(SearchContext context, AggregatorFactory<?> parent) throws IOException;

    /** Associate metadata with this {@link AggregationBuilder}. */
    @Override
    public abstract AggregationBuilder setMetaData(Map<String, Object> metaData);

    /** Return any associated metadata with this {@link AggregationBuilder}. */
    public abstract Map<String, Object> getMetaData();

    /** Add a sub aggregation to this builder. */
    public abstract AggregationBuilder subAggregation(AggregationBuilder aggregation);

    /** Add a sub aggregation to this builder. */
    public abstract AggregationBuilder subAggregation(PipelineAggregationBuilder aggregation);

    /** Return the configured set of subaggregations **/
    public List<AggregationBuilder> getSubAggregations() {
        return factoriesBuilder.getAggregatorFactories();
    }

    /** Return the configured set of pipeline aggregations **/
    public List<PipelineAggregationBuilder> getPipelineAggregations() {
        return factoriesBuilder.getPipelineAggregatorFactories();
    }

    /**
     * Internal: Registers sub-factories with this factory. The sub-factory will be
     * responsible for the creation of sub-aggregators under the aggregator
     * created by this factory. This is only for use by {@link AggregatorFactories#parseAggregators(QueryParseContext)}.
     *
     * @param subFactories
     *            The sub-factories
     * @return this factory (fluent interface)
     */
    @Override
    public abstract AggregationBuilder subAggregations(AggregatorFactories.Builder subFactories);

    /** Common xcontent fields shared among aggregator builders */
    public static final class CommonFields extends ParseField.CommonFields {
        public static final ParseField VALUE_TYPE = new ParseField("value_type");
    }
}
