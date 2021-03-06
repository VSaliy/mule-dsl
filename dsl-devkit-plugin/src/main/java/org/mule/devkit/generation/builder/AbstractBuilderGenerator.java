package org.mule.devkit.generation.builder;

import org.mule.api.MuleContext;
import org.mule.api.MuleEvent;
import org.mule.api.MuleMessage;
import org.mule.api.callback.HttpCallback;
import org.mule.api.callback.SourceCallback;
import org.mule.devkit.generation.api.Context;
import org.mule.devkit.generation.api.ModuleGenerator;
import org.mule.devkit.generation.api.Product;
import org.mule.devkit.model.Method;
import org.mule.devkit.model.Parameter;
import org.mule.devkit.model.Type;
import org.mule.devkit.model.code.ExpressionFactory;
import org.mule.devkit.model.code.GeneratedBlock;
import org.mule.devkit.model.code.GeneratedClass;
import org.mule.devkit.model.code.GeneratedExpression;
import org.mule.devkit.model.code.GeneratedField;
import org.mule.devkit.model.code.GeneratedFieldReference;
import org.mule.devkit.model.code.GeneratedInvocation;
import org.mule.devkit.model.code.GeneratedMethod;
import org.mule.devkit.model.code.GeneratedPackage;
import org.mule.devkit.model.code.GeneratedVariable;
import org.mule.devkit.model.code.Modifier;
import org.mule.devkit.model.code.TypeReference;
import org.mule.devkit.model.code.builders.FieldBuilder;
import org.mule.devkit.model.module.Module;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.lang.model.type.TypeMirror;

import org.apache.commons.lang.StringUtils;

/**
 * Created by machaval on 2/25/14.
 */
public abstract class AbstractBuilderGenerator implements ModuleGenerator
{

    public static final String BUILDER_PACKAGE = "org.mule.module.builder";
    public static final String CONTEXT_ARG_NAME = "context";
    public static final String CREATE_METHOD_NAME = "create";
    public static final String RESULT_VARIABLE_NAME = "result";
    public static final String CONFIG_REF_VARIABLE_NAME = "configName";
    public static final String BUILDER_NAME = "Builder";

    private final static List<String> INTERNAL_PARAMETER = Arrays.asList(HttpCallback.class.getName(), SourceCallback.class.getName(), MuleMessage.class.getName(), MuleEvent.class.getName());

    public static final String CONFIG_REF_METHOD_NAME = "using";

    protected final GeneratedClass moduleFactoryClass;
    private Context context;

    public AbstractBuilderGenerator(GeneratedClass moduleFactoryClass)
    {
        this.moduleFactoryClass = moduleFactoryClass;
    }

    @Override
    public boolean shouldGenerate(Module module)
    {
        return true;
    }

    public GeneratedClass getModuleFactoryClass()
    {
        return moduleFactoryClass;
    }

    public org.mule.devkit.model.code.Type ref(TypeMirror typeMirror)
    {
        return ctx().getCodeModel().ref(typeMirror);
    }

    public TypeReference ref(Class<?> clazz)
    {
        return ctx().getCodeModel().ref(clazz);
    }

    public org.mule.devkit.model.code.Type ref(String fullyQualifiedClassName)
    {
        return ctx().getCodeModel().ref(fullyQualifiedClassName);
    }

    //Creates a builder based on all the parameters of the specified method
    protected void createBuilder(GeneratedClass moduleFactoryClass, Method<Type> processorMethod, org.mule.devkit.model.code.Type buildedObjectType, GeneratedClass builderClass)
    {
        final GeneratedMethod createMethod = builderClass.method(Modifier.PUBLIC, buildedObjectType, CREATE_METHOD_NAME);
        createMethod.param(MuleContext.class, CONTEXT_ARG_NAME);
        final GeneratedBlock createMethodBlock = createMethod.body();
        final GeneratedVariable resultVariable = createMethodBlock.decl(buildedObjectType, RESULT_VARIABLE_NAME);
        createMethodBlock.assign(resultVariable, ExpressionFactory._new(buildedObjectType).arg(processorMethod.getName()));
        GeneratedMethod constructor = builderClass.constructor(Modifier.PUBLIC);
        String name = processorMethod.getName();
        GeneratedMethod processorFactoryMethod = moduleFactoryClass.method(Modifier.STATIC | Modifier.PUBLIC, builderClass, name);
        GeneratedInvocation newBuilderExpression = ExpressionFactory._new(builderClass);

        for (Parameter<Method<Type>> variable : processorMethod.getParameters())
        {
            final String fieldName = variable.getName();

            if (!isInternalParameter(variable))
            {
                if (variable.isOptional())
                {
                    final String defaultValue = variable.getDefaultValue();
                    declareFieldOfTypeObject(fieldName, ref(variable.asTypeMirror()), builderClass, defaultValue);
                }
                else
                {
                    new FieldBuilder(builderClass).name(fieldName).type(Object.class).privateVisibility().build();
                    constructor.param(Object.class, fieldName);
                    constructor.body().assign(ExpressionFactory.refthis(fieldName), ExpressionFactory.ref(fieldName));
                    //Declare parameter in the static factory method
                    processorFactoryMethod.param(Object.class, fieldName);
                    //use it in the constructor
                    newBuilderExpression.arg(ExpressionFactory.ref(fieldName));
                }
                createMethodBlock.invoke(resultVariable, getSetterMethod(fieldName)).arg(ExpressionFactory.ref(fieldName));
            }
        }


        new FieldBuilder(builderClass).name(CONFIG_REF_VARIABLE_NAME).type(String.class).privateVisibility().build();

        final GeneratedMethod fieldExpressionMethod = builderClass.method(Modifier.PUBLIC, builderClass, CONFIG_REF_METHOD_NAME);
        fieldExpressionMethod.param(String.class, CONFIG_REF_VARIABLE_NAME);
        GeneratedBlock fieldMethodBody = fieldExpressionMethod.body();
        fieldMethodBody.assign(ExpressionFactory.refthis(CONFIG_REF_VARIABLE_NAME), ExpressionFactory.ref(CONFIG_REF_VARIABLE_NAME));
        fieldMethodBody._return(ExpressionFactory._this());


        GeneratedFieldReference configRefVariableRef = ExpressionFactory.ref(CONFIG_REF_VARIABLE_NAME);
        //context.getRegistry().lookup(configRef);
        GeneratedInvocation lookupObject = ExpressionFactory.ref(CONTEXT_ARG_NAME).invoke("getRegistry").invoke("lookupObject").arg(configRefVariableRef);
        createMethodBlock.invoke(resultVariable, "setModuleObject").arg(lookupObject);

        createMethodBlock._return(resultVariable);


        processorFactoryMethod.body()._return(newBuilderExpression);
    }

    protected String getSetterMethod(String fieldName)
    {
        return "set" + StringUtils.capitalize(fieldName);
    }

    protected GeneratedField declareFieldOfTypeObject(String fieldName, org.mule.devkit.model.code.Type realType, GeneratedClass processorBuilderClass, String defaultValue)
    {
        final GeneratedExpression defaultValueGeneratedExpression = getDefaultValueExpression(realType, defaultValue);
        final GeneratedField field = new FieldBuilder(processorBuilderClass).name(fieldName).type(Object.class).privateVisibility().initialValue(defaultValueGeneratedExpression).build();
        final GeneratedMethod fieldExpressionMethod = processorBuilderClass.method(Modifier.PUBLIC, processorBuilderClass, fieldName);
        fieldExpressionMethod.param(Object.class, fieldName);
        final GeneratedBlock fieldMethodBody = fieldExpressionMethod.body();
        fieldMethodBody.assign(ExpressionFactory.refthis(fieldName), ExpressionFactory.ref(fieldName));
        fieldMethodBody._return(ExpressionFactory._this());
        return field;
    }

    protected GeneratedField declareField(String fieldName, org.mule.devkit.model.code.Type type, GeneratedClass processorBuilderClass, String defaultValue)
    {

        final GeneratedExpression defaultValueGeneratedExpression = getDefaultValueExpression(type, defaultValue);
        final GeneratedField field = new FieldBuilder(processorBuilderClass).name(fieldName).type(type).privateVisibility().initialValue(defaultValueGeneratedExpression).build();
        final GeneratedMethod fieldExpressionMethod = processorBuilderClass.method(Modifier.PUBLIC, processorBuilderClass, fieldName);

        final GeneratedBlock fieldMethodBody = fieldExpressionMethod.body();
        if (isMap(type))
        {
            fieldExpressionMethod.param(refMapBuilderType(), fieldName);
            fieldMethodBody.assign(ExpressionFactory.refthis(fieldName), ExpressionFactory.ref(fieldName).invoke("build"));
        }
        else
        {
            fieldExpressionMethod.param(type, fieldName);
            fieldMethodBody.assign(ExpressionFactory.refthis(fieldName), ExpressionFactory.ref(fieldName));
        }
        fieldMethodBody._return(ExpressionFactory._this());
        return field;
    }

    protected org.mule.devkit.model.code.Type refMapBuilderType()
    {
        return ref("org.mule.module.core.builder.MapBuilder");
    }

    protected boolean isMap(org.mule.devkit.model.code.Type type)
    {
        return type.fullName().contains(Map.class.getName()) || type.fullName().contains(HashMap.class.getName());
    }

    private GeneratedExpression getDefaultValueExpression(org.mule.devkit.model.code.Type fieldType, String defaultValue)
    {
        GeneratedExpression defaultValueGeneratedExpression = null;
        if (!StringUtils.isEmpty(defaultValue))
        {
            String defaultValueExpression = defaultValue;
            if (fieldType.fullName().equals(String.class.getName()) || defaultValue.startsWith("#["))
            {
                defaultValueExpression = "\"" + defaultValue + "\"";
                defaultValueGeneratedExpression = ExpressionFactory.direct(defaultValueExpression);
            }
            else
            {
                defaultValueGeneratedExpression = ExpressionFactory.cast(fieldType, ExpressionFactory.direct(defaultValueExpression));
            }
        }
        return defaultValueGeneratedExpression;
    }

    protected boolean isInternalParameter(Parameter variable)
    {
        if (variable.asType().isNestedProcessor() || (variable.asType().isArrayOrList() && variable.getTypeArguments().size() > 0 && variable.getTypeArguments().get(0).isNestedProcessor()))
        {
            return false;
        }
        String parameterType = variable.asTypeMirror().toString();
        for (String internalParameter : INTERNAL_PARAMETER)
        {
            if (parameterType.startsWith(internalParameter))
            {
                return true;
            }
        }
        return false;
    }

    protected GeneratedClass createClass(String packageName, String className)
    {
        GeneratedPackage pkg = ctx().getCodeModel()._package(packageName);
        GeneratedClass clazz;
        clazz = pkg._class(className);
        return clazz;
    }

    @Override
    public List<Product> consumes()
    {
        return null;
    }

    @Override
    public List<Product> produces()
    {
        return null;
    }

    @Override
    public void setCtx(Context context)
    {

        this.context = context;
    }

    @Override
    public Context ctx()
    {
        return context;
    }
}
