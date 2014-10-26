package org.smoothbuild.lang.function.nativ;

import static org.smoothbuild.lang.base.Types.paramJTypeToType;
import static org.smoothbuild.lang.base.Types.resultJTypeToType;
import static org.smoothbuild.lang.function.base.Name.name;

import java.lang.reflect.Method;
import java.util.List;

import org.smoothbuild.lang.base.Type;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.function.base.Param;
import org.smoothbuild.lang.function.base.Signature;
import org.smoothbuild.lang.function.nativ.err.IllegalFunctionNameException;
import org.smoothbuild.lang.function.nativ.err.IllegalParamTypeException;
import org.smoothbuild.lang.function.nativ.err.IllegalReturnTypeException;
import org.smoothbuild.lang.function.nativ.err.NativeImplementationException;
import org.smoothbuild.lang.function.nativ.err.ParamMethodHasArgumentsException;
import org.smoothbuild.lang.function.nativ.err.ParamsIsNotInterfaceException;
import org.smoothbuild.lang.plugin.Required;

import com.google.common.collect.Lists;
import com.google.inject.TypeLiteral;

public class SignatureFactory {

  public static Signature<?> create(Method functionMethod, Class<?> paramsInterface) throws
      NativeImplementationException {
    Type<?> type = functionType(functionMethod);
    Name name = functionSName(functionMethod);
    Iterable<Param> params = functionSParams(functionMethod, paramsInterface);

    return new Signature<>(type, name, params);
  }

  private static Name functionSName(Method functionMethod) throws NativeImplementationException {
    try {
      return name(functionMethod.getName());
    } catch (IllegalArgumentException e) {
      throw new IllegalFunctionNameException(functionMethod, e.getMessage());
    }
  }

  private static Iterable<Param> functionSParams(Method functionMethod,
      Class<?> paramsInterface) throws NativeImplementationException {
    if (!paramsInterface.isInterface()) {
      throw new ParamsIsNotInterfaceException(functionMethod);
    }
    Method[] methods = paramsInterface.getMethods();
    List<Param> params = Lists.newArrayList();
    for (Method paramMethod : methods) {
      params.add(paramMethodToSParam(functionMethod, paramMethod));
    }
    return params;
  }

  private static Param paramMethodToSParam(Method functionMethod, Method paramMethod) throws
      NativeImplementationException {
    if (paramMethod.getParameterTypes().length != 0) {
      throw new ParamMethodHasArgumentsException(functionMethod, paramMethod);
    }

    Type<?> type = paramMethodType(functionMethod, paramMethod);
    String name = paramMethod.getName();
    boolean isRequired = paramMethod.getAnnotation(Required.class) != null;
    return Param.param(type, name, isRequired);
  }

  private static Type<?> functionType(Method functionMethod) throws
      NativeImplementationException {
    TypeLiteral<?> jType = methodJType(functionMethod);
    Type<?> type = resultJTypeToType(jType);
    if (type == null) {
      throw new IllegalReturnTypeException(functionMethod, jType);
    }
    return type;
  }

  private static Type<?> paramMethodType(Method functionMethod, Method paramMethod) throws
      IllegalParamTypeException {
    TypeLiteral<?> jType = methodJType(paramMethod);
    Type<?> type = paramJTypeToType(jType);
    if (type == null) {
      throw new IllegalParamTypeException(functionMethod, paramMethod, jType);
    }
    return type;
  }

  private static TypeLiteral<?> methodJType(Method paramMethod) {
    Class<?> paramsClass = paramMethod.getDeclaringClass();
    return TypeLiteral.get(paramsClass).getReturnType(paramMethod);
  }
}
