package org.mule.module.apikit.builder;

import static org.mule.module.Core.inbound;
import static org.mule.module.Core.flow;
import static org.mule.module.Core.process;
import static org.mule.module.core.builder.PropertiesBuilder.properties;

import org.mule.api.MuleContext;
import org.mule.api.exception.MessagingExceptionHandler;
import org.mule.api.processor.MessageProcessor;
import org.mule.config.dsl.Builder;
import org.mule.construct.Flow;
import org.mule.module.Core;
import org.mule.module.apikit.Configuration;
import org.mule.module.apikit.MappingExceptionListener;
import org.mule.module.apikit.RestMappingExceptionStrategy;
import org.mule.module.apikit.Router;
import org.mule.module.core.builder.PrivateFlowBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.raml.model.ActionType;


public class RestRouterBuilder implements Builder<Flow>
{

    //constants
    public static final String NOT_FOUND_EXCEPTION_CLASS_NAME = "org.mule.module.apikit.exception.NotFoundException";
    public static final String METHOD_NOT_ALLOWED_EXCEPTION_CLASS_NAME = "org.mule.module.apikit.exception.MethodNotAllowedException";
    public static final String UNSUPPORTED_MEDIA_TYPE_EXCEPTION_CLASS_NAME = "org.mule.module.apikit.exception.UnsupportedMediaTypeException";
    public static final String NOT_ACCEPTABLE_EXCEPTION_CLASS_NAME = "org.mule.module.apikit.exception.NotAcceptableException";
    public static final String BAD_REQUEST_EXCEPTION_CLASS_NAME = "org.mule.module.apikit.exception.BadRequestException";
    public static final int NOT_FOUND_STATUS_CODE = 404;
    public static final int METHOD_NOT_ALLOWED_STATUS_CODE = 405;
    public static final int UNSOPORTED_MEDIA_TYPE_STATUS_CODE = 415;
    public static final int NOT_ACCEPTABLE_STATUS_CODE = 406;
    public static final int BAD_REQUEST_STATUS = 400;
    public static final String APIKIT_EXCEPTION_STRATEGY_NAME = "rest-router-es";
    public static final String APIKIT_FLOW_NAME = "RestRouterFlow";


    private String ramlPath;
    private List<PrivateFlowBuilder> resourceActionBuilders = new ArrayList<PrivateFlowBuilder>();
    private String baseUri = "http://0.0.0.0:8081/api";
    private boolean consoleEnabled = true;
    private String consolePath = "console";

    public RestRouterBuilder(String ramlPath)
    {
        this.ramlPath = ramlPath;
    }


    public RestRouterBuilder baseUri(String baseUri)
    {
        this.baseUri = baseUri;
        return this;
    }

    public RestRouterBuilder enableConsole()
    {
        this.consoleEnabled = true;
        return this;
    }

    public RestRouterBuilder disableConsole()
    {
        this.consoleEnabled = false;
        return this;
    }

    public RestRouterBuilder consolePath(String consolePath)
    {
        this.consolePath = consolePath;
        return this;
    }

    public RestRouterBuilder onGet(String resource)
    {
        return on(resource, ActionType.GET);
    }

    public RestRouterBuilder onPost(String resource)
    {
        return on(resource, ActionType.POST);
    }

    public RestRouterBuilder onDelete(String resource)
    {
        return on(resource, ActionType.DELETE);
    }

    public RestRouterBuilder onHead(String resource)
    {
        return on(resource, ActionType.HEAD);
    }

    public RestRouterBuilder onTrace(String resource)
    {
        return on(resource, ActionType.TRACE);
    }

    public RestRouterBuilder onPut(String resource)
    {
        return on(resource, ActionType.PUT);
    }

    public RestRouterBuilder onPatch(String resource)
    {
        return on(resource, ActionType.PATCH);
    }


    public RestRouterBuilder onOptions(String resource)
    {
        return on(resource, ActionType.OPTIONS);
    }

    public RestRouterBuilder on(String resource, ActionType action)
    {
        resourceActionBuilders.add(Core.flow(action.name().toLowerCase() + ":" + resource));
        return this;
    }


    public RestRouterBuilder then(Builder<? extends MessageProcessor>... messageProcessorBuilder)
    {
        if (resourceActionBuilders.isEmpty())
        {
            throw new IllegalStateException("'on(String resource, ActionType action)' method must be called before then().");
        }
        resourceActionBuilders.get(resourceActionBuilders.size() - 1).then(messageProcessorBuilder);
        return this;
    }

    public Flow create(MuleContext muleContext)
    {

        for (PrivateFlowBuilder resourceActionBuilder : resourceActionBuilders)
        {
            resourceActionBuilder.create(muleContext);
        }
        String address = getAddress();
        final Configuration config = new Configuration();
        config.setConsoleEnabled(consoleEnabled);
        config.setConsolePath(consolePath);
        config.setRaml(ramlPath);

        final PrivateFlowBuilder restRouter = flow(APIKIT_FLOW_NAME)
                .on(inbound(address).requestResponse())
                .then(process(Router.class).using(properties("config", config)))
                .onException(getExceptionBuilder());
        return restRouter.create(muleContext);
    }

    private Builder<MessagingExceptionHandler> getExceptionBuilder()
    {
        return new Builder<MessagingExceptionHandler>()
        {
            @Override
            public MessagingExceptionHandler create(MuleContext muleContext)
            {
                RestMappingExceptionStrategy es = new RestMappingExceptionStrategy();
                es.setGlobalName(APIKIT_EXCEPTION_STRATEGY_NAME);
                List<MappingExceptionListener> exceptionListeners = new ArrayList<MappingExceptionListener>();
                exceptionListeners.add(createExceptionListener(NOT_FOUND_STATUS_CODE, NOT_FOUND_EXCEPTION_CLASS_NAME));
                exceptionListeners.add(createExceptionListener(METHOD_NOT_ALLOWED_STATUS_CODE, METHOD_NOT_ALLOWED_EXCEPTION_CLASS_NAME));
                exceptionListeners.add(createExceptionListener(UNSOPORTED_MEDIA_TYPE_STATUS_CODE, UNSUPPORTED_MEDIA_TYPE_EXCEPTION_CLASS_NAME));
                exceptionListeners.add(createExceptionListener(NOT_ACCEPTABLE_STATUS_CODE, NOT_ACCEPTABLE_EXCEPTION_CLASS_NAME));
                exceptionListeners.add(createExceptionListener(BAD_REQUEST_STATUS, BAD_REQUEST_EXCEPTION_CLASS_NAME));
                es.setExceptionListeners(exceptionListeners);
                return es;
            }

            private MappingExceptionListener createExceptionListener(int status, String... exception)
            {
                MappingExceptionListener mappingExceptionListener = new MappingExceptionListener();
                mappingExceptionListener.setStatusCode(status);
                mappingExceptionListener.setExceptions(Arrays.asList(exception));
                return mappingExceptionListener;
            }
        };
    }

    private String getAddress()
    {
        return baseUri;
    }


}
