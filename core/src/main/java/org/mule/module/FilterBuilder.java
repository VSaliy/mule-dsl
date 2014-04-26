package org.mule.module;

import org.mule.api.routing.filter.Filter;
import org.mule.module.core.filter.EmptyPayloadFilter;
import org.mule.module.core.filter.NullPayloadFilter;
import org.mule.routing.filters.ExpressionFilter;
import org.mule.routing.filters.logic.AndFilter;
import org.mule.routing.filters.logic.NotFilter;
import org.mule.routing.filters.logic.OrFilter;


public class FilterBuilder
{

    public static Filter and(Filter... filters)
    {
        return new AndFilter(filters);
    }

    public static Filter not(Filter filter)
    {
        return new NotFilter(filter);
    }

    public static Filter expression(String expression)
    {
        return new ExpressionFilter(expression);
    }

    public static Filter or(Filter... filters)
    {
        return new OrFilter(filters);
    }

    public static Filter nullPayload()
    {
        return new NullPayloadFilter();
    }

    public static Filter emptyPayload()
    {
        return new EmptyPayloadFilter();
    }

}
