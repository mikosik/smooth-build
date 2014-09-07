package org.smoothbuild.lang.function.nativ;

import static org.smoothbuild.lang.base.STypes.paramJTypeToSType;
import static org.smoothbuild.lang.base.STypes.resultJTypeToSType;
import static org.smoothbuild.lang.function.base.Name.name;

import java.lang.reflect.Method;
import java.util.List;

import org.smoothbuild.lang.base.SType;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.function.base.Param;
import org.smoothbuild.lang.function.base.Signature;
import org.smoothbuild.lang.function.nativ.err.ForbiddenParamTypeException;
import org.smoothbuild.lang.function.nativ.err.IllegalFunctionNameException;
import org.smoothbuild.lang.function.nativ.err.IllegalReturnTypeException;
import org.smoothbuild.lang.function.nativ.err.MissingNameException;
import org.smoothbuild.lang.function.nativ.err.NativeImplementationException;
import org.smoothbuild.lang.function.nativ.err.ParamMethodHasArgumentsException;
import org.smoothbuild.lang.function.nativ.err.ParamsIsNotInterfaceException;
import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.SmoothFunction;

import com.google.common.collect.Lists;
import com.google.inject.TypeLiteral;

public class SignatureFactory {

  public static Signature<?> create(Method method, Class<?> paramsInterface)
      throws NativeImplementationException {
    SType<?> type = getReturnType(method);
    Name name = getFunctionName(method);
    Iterable<Param> params = getParams(method, paramsInterface);

    return new Signature<>(type, name, params);
  }

  private static Name getFunctionName(Method method) throws NativeImplementationException {
    SmoothFunction annotation = method.getAnnotation(SmoothFunction.class);
    if (annotation == null) {
      throw new MissingNameException(method);
    }
    try {
      return name(annotation.name());
    } catch (IllegalArgumentException e) {
      throw new IllegalFunctionNameException(method, e.getMessage());
    }
  }

  private static SType<?> getReturnType(Method method) throws NativeImplementationException {
    TypeLiteral<?> jType = methodReturnJType(method);
    SType<?> type = resultJTypeToSType(jType);
    if (type == null) {
      throw new IllegalReturnTypeException(method, jType);
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

    TypeLiteral<?> jType = methodReturnJType(paramMethod);
    SType<?> type = paramJTypeToSType(jType);
    if (type == null) {
      throw new ForbiddenParamTypeException(method, paramMethod, jType);
    }

    boolean isRequired = paramMethod.getAnnotation(Required.class) != null;
    String name = paramMethod.getName();
    return Param.param(type, name, isRequired);
  }

  private static TypeLiteral<?> methodReturnJType(Method paramMethod) {
    Class<?> paramsClass = paramMethod.getDeclaringClass();
    return TypeLiteral.get(paramsClass).getReturnType(paramMethod);
  }
}
