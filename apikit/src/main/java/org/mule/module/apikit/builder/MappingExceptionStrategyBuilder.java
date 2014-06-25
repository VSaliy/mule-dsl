package org.mule.module.apikit.builder;

import org.mule.api.MuleContext;
import org.mule.api.exception.MessagingExceptionHandler;
import org.mule.config.dsl.Builder;
import org.mule.module.apikit.MappingExceptionListener;
import org.mule.module.apikit.RestMappingExceptionStrategy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MappingExceptionStrategyBuilder implements Builder<MessagingExceptionHandler>
{

    @Override
    public MessagingExceptionHandler create(MuleContext muleContext)
    {

        //TODO we should expose this
        final RestMappingExceptionStrategy es = new RestMappingExceptionStrategy();
        es.setGlobalName(RestRouterBuilder.APIKIT_EXCEPTION_STRATEGY_NAME);
        final List<MappingExceptionListener> exceptionListeners = new ArrayList<MappingExceptionListener>();
        exceptionListeners.add(createExceptionListener(RestRouterBuilder.NOT_FOUND_STATUS_CODE, RestRouterBuilder.NOT_FOUND_EXCEPTION_CLASS_NAME));
        exceptionListeners.add(createExceptionListener(RestRouterBuilder.METHOD_NOT_ALLOWED_STATUS_CODE, RestRouterBuilder.METHOD_NOT_ALLOWED_EXCEPTION_CLASS_NAME));
        exceptionListeners.add(createExceptionListener(RestRouterBuilder.UNSOPORTED_MEDIA_TYPE_STATUS_CODE, RestRouterBuilder.UNSUPPORTED_MEDIA_TYPE_EXCEPTION_CLASS_NAME));
        exceptionListeners.add(createExceptionListener(RestRouterBuilder.NOT_ACCEPTABLE_STATUS_CODE, RestRouterBuilder.NOT_ACCEPTABLE_EXCEPTION_CLASS_NAME));
        exceptionListeners.add(createExceptionListener(RestRouterBuilder.BAD_REQUEST_STATUS, RestRouterBuilder.BAD_REQUEST_EXCEPTION_CLASS_NAME));
        es.setExceptionListeners(exceptionListeners);
        return es;
    }

    private MappingExceptionListener createExceptionListener(int status, String... exception)
    {
        final MappingExceptionListener mappingExceptionListener = new MappingExceptionListener();
        mappingExceptionListener.setStatusCode(status);
        mappingExceptionListener.setExceptions(Arrays.asList(exception));
        return mappingExceptionListener;
    }
}
