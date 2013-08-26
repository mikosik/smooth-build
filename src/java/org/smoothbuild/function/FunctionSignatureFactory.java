package org.smoothbuild.function;

import static org.smoothbuild.function.FullyQualifiedName.fullyQualifiedName;

import java.lang.reflect.Method;

import org.smoothbuild.function.exc.ForbiddenParamTypeException;
import org.smoothbuild.function.exc.IllegalFunctionNameException;
import org.smoothbuild.function.exc.IllegalReturnTypeException;
import org.smoothbuild.function.exc.MissingNameException;
import org.smoothbuild.function.exc.ParamMethodHasArgumentsException;
import org.smoothbuild.function.exc.ParamsIsNotInterfaceException;
import org.smoothbuild.function.exc.PluginImplementationException;
import org.smoothbuild.plugin.FunctionName;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public class FunctionSignatureFactory {

  public FunctionSignature create(Class<?> klass, Method method, Class<?> paramsInterface)
      throws PluginImplementationException {
    Type type = getReturnType(method);
    FullyQualifiedName name = getFunctionName(klass);
    ImmutableMap<String, Param> params = getParams(klass, method, paramsInterface);

    return new FunctionSignature(type, name, params);
  }

  private static FullyQualifiedName getFunctionName(Class<?> klass)
      throws PluginImplementationException {
    FunctionName annotation = klass.getAnnotation(FunctionName.class);
    if (annotation == null) {
      throw new MissingNameException(klass);
    }
    try {
      return fullyQualifiedName(annotation.value());
    } catch (IllegalArgumentException e) {
      throw new IllegalFunctionNameException(klass, e.getMessage());
    }
  }

  private static Type getReturnType(Method method) throws PluginImplementationException {
    Class<?> javaType = method.getReturnType();
    Type type = Type.javaResultTypetoType(javaType);
    if (type == null) {
      throw new IllegalReturnTypeException(method.getDeclaringClass(), javaType);
    }
    return type;
  }

  private static ImmutableMap<String, Param> getParams(Class<?> klass, Method method,
      Class<?> paramsInterface) throws PluginImplementationException {
    if (!paramsInterface.isInterface()) {
      throw new ParamsIsNotInterfaceException(klass, method);
    }
    Method[] methods = paramsInterface.getMethods();
    Builder<String, Param> builder = ImmutableMap.builder();
    for (Method method2 : methods) {
      Param param = methodToParam(klass, method2);
      builder.put(param.name(), param);
    }
    return builder.build();
  }

  private static Param methodToParam(Class<?> klass, Method method)
      throws PluginImplementationException {
    if (method.getParameterTypes().length != 0) {
      throw new ParamMethodHasArgumentsException(klass, method);
    }
    Class<?> javaType = method.getReturnType();
    Type type = Type.javaParamTypetoType(javaType);
    if (type == null) {
      throw new ForbiddenParamTypeException(klass, method, javaType);
    }

    return Param.param(type, method.getName());
  }
}
