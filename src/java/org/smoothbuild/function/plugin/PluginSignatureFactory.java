package org.smoothbuild.function.plugin;

import static org.smoothbuild.function.base.Name.qualifiedName;

import java.lang.reflect.Method;

import org.smoothbuild.function.base.Name;
import org.smoothbuild.function.base.Param;
import org.smoothbuild.function.base.Signature;
import org.smoothbuild.function.base.Type;
import org.smoothbuild.function.plugin.exc.ForbiddenParamTypeException;
import org.smoothbuild.function.plugin.exc.IllegalFunctionNameException;
import org.smoothbuild.function.plugin.exc.IllegalReturnTypeException;
import org.smoothbuild.function.plugin.exc.MissingNameException;
import org.smoothbuild.function.plugin.exc.ParamMethodHasArgumentsException;
import org.smoothbuild.function.plugin.exc.ParamsIsNotInterfaceException;
import org.smoothbuild.function.plugin.exc.PluginImplementationException;
import org.smoothbuild.plugin.SmoothFunction;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public class PluginSignatureFactory {

  public Signature create(Method method, Class<?> paramsInterface)
      throws PluginImplementationException {
    Type type = getReturnType(method);
    Name name = getFunctionName(method);
    ImmutableMap<String, Param> params = getParams(method, paramsInterface);

    return new Signature(type, name, params);
  }

  private static Name getFunctionName(Method method) throws PluginImplementationException {
    SmoothFunction annotation = method.getAnnotation(SmoothFunction.class);
    if (annotation == null) {
      throw new MissingNameException(method);
    }
    try {
      return qualifiedName(annotation.value());
    } catch (IllegalArgumentException e) {
      throw new IllegalFunctionNameException(method, e.getMessage());
    }
  }

  private static Type getReturnType(Method method) throws PluginImplementationException {
    Class<?> javaType = method.getReturnType();
    Type type = Type.javaResultTypetoType(javaType);
    if (type == null) {
      throw new IllegalReturnTypeException(method, javaType);
    }
    return type;
  }

  private static ImmutableMap<String, Param> getParams(Method method, Class<?> paramsInterface)
      throws PluginImplementationException {
    if (!paramsInterface.isInterface()) {
      throw new ParamsIsNotInterfaceException(method);
    }
    Method[] methods = paramsInterface.getMethods();
    Builder<String, Param> builder = ImmutableMap.builder();
    for (Method paramMethod : methods) {
      Param param = methodToParam(method, paramMethod);
      builder.put(param.name(), param);
    }
    return builder.build();
  }

  private static Param methodToParam(Method method, Method paramMethod)
      throws PluginImplementationException {
    if (paramMethod.getParameterTypes().length != 0) {
      throw new ParamMethodHasArgumentsException(method, paramMethod);
    }
    Class<?> javaType = paramMethod.getReturnType();
    Type type = Type.javaParamTypetoType(javaType);
    if (type == null) {
      throw new ForbiddenParamTypeException(method, paramMethod, javaType);
    }

    return Param.param(type, paramMethod.getName());
  }
}
