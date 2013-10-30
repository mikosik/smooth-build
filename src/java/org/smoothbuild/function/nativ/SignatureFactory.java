package org.smoothbuild.function.nativ;

import static org.smoothbuild.function.base.Name.simpleName;

import java.lang.reflect.Method;
import java.util.List;

import org.smoothbuild.function.base.Name;
import org.smoothbuild.function.base.Param;
import org.smoothbuild.function.base.Signature;
import org.smoothbuild.function.base.Type;
import org.smoothbuild.function.nativ.exc.ForbiddenParamTypeException;
import org.smoothbuild.function.nativ.exc.IllegalFunctionNameException;
import org.smoothbuild.function.nativ.exc.IllegalReturnTypeException;
import org.smoothbuild.function.nativ.exc.MissingNameException;
import org.smoothbuild.function.nativ.exc.NativeImplementationException;
import org.smoothbuild.function.nativ.exc.ParamMethodHasArgumentsException;
import org.smoothbuild.function.nativ.exc.ParamsIsNotInterfaceException;
import org.smoothbuild.plugin.Required;
import org.smoothbuild.plugin.SmoothFunction;

import com.google.common.collect.Lists;
import com.google.inject.TypeLiteral;

public class SignatureFactory {

  public static Signature create(Method method, Class<?> paramsInterface)
      throws NativeImplementationException {
    Type type = getReturnType(method);
    Name name = getFunctionName(method);
    Iterable<Param> params = getParams(method, paramsInterface);

    return new Signature(type, name, params);
  }

  private static Name getFunctionName(Method method) throws NativeImplementationException {
    SmoothFunction annotation = method.getAnnotation(SmoothFunction.class);
    if (annotation == null) {
      throw new MissingNameException(method);
    }
    try {
      return simpleName(annotation.value());
    } catch (IllegalArgumentException e) {
      throw new IllegalFunctionNameException(method, e.getMessage());
    }
  }

  private static Type getReturnType(Method method) throws NativeImplementationException {
    Class<?> klass = method.getReturnType();
    TypeLiteral<?> javaType = TypeLiteral.get(klass);
    Type type = Type.javaResultTypetoType(javaType);
    if (type == null) {
      throw new IllegalReturnTypeException(method, javaType);
    }
    return type;
  }

  private static Iterable<Param> getParams(Method method, Class<?> paramsInterface)
      throws NativeImplementationException {
    if (!paramsInterface.isInterface()) {
      throw new ParamsIsNotInterfaceException(method);
    }
    Method[] methods = paramsInterface.getMethods();
    List<Param> params = Lists.newArrayList();
    for (Method paramMethod : methods) {
      params.add(methodToParam(method, paramMethod));
    }
    return params;
  }

  private static Param methodToParam(Method method, Method paramMethod)
      throws NativeImplementationException {
    if (paramMethod.getParameterTypes().length != 0) {
      throw new ParamMethodHasArgumentsException(method, paramMethod);
    }
    Class<?> klass = paramMethod.getReturnType();
    TypeLiteral<?> javaType = TypeLiteral.get(klass);
    Type type = Type.javaParamTypetoType(javaType);
    if (type == null) {
      throw new ForbiddenParamTypeException(method, paramMethod, javaType);
    }

    boolean isRequired = paramMethod.getAnnotation(Required.class) != null;
    String name = paramMethod.getName();
    return Param.param(type, name, isRequired);
  }
}
