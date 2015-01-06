package org.smoothbuild.lang.function.nativ;

import static org.smoothbuild.lang.function.nativ.NativeParameterFactory.createParameter;
import static org.smoothbuild.lang.type.Types.resultJTypeToType;
import static org.smoothbuild.util.ReflexiveUtils.isPublic;
import static org.smoothbuild.util.ReflexiveUtils.isStatic;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import org.smoothbuild.SmoothConstants;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.function.base.Parameter;
import org.smoothbuild.lang.function.base.Signature;
import org.smoothbuild.lang.function.nativ.err.DuplicatedParameterException;
import org.smoothbuild.lang.function.nativ.err.IllegalFunctionNameException;
import org.smoothbuild.lang.function.nativ.err.IllegalReturnTypeException;
import org.smoothbuild.lang.function.nativ.err.MissingNativeApiParameterException;
import org.smoothbuild.lang.function.nativ.err.NativeFunctionImplementationException;
import org.smoothbuild.lang.function.nativ.err.NonPublicSmoothFunctionException;
import org.smoothbuild.lang.function.nativ.err.NonStaticSmoothFunctionException;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.NotCacheable;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.task.exec.NativeApiImpl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hasher;
import com.google.inject.TypeLiteral;

public class NativeFunctionFactory {

  public static Set<NativeFunction> nativeFunctions(Class<?> clazz, HashCode jarHash)
      throws NativeFunctionImplementationException {
    HashSet<NativeFunction> result = new HashSet<>();
    for (Method method : clazz.getDeclaredMethods()) {
      if (method.isAnnotationPresent(SmoothFunction.class)) {
        result.add(nativeFunction(method, jarHash));
      }
    }
    return result;
  }

  public static NativeFunction nativeFunction(Method method, HashCode jarHash)
      throws NativeFunctionImplementationException {
    if (!isStatic(method)) {
      throw new NonStaticSmoothFunctionException(method);
    }
    if (!isPublic(method)) {
      throw new NonPublicSmoothFunctionException(method);
    }

    Signature signature = createSignature(method);
    boolean cacheable = !method.isAnnotationPresent(NotCacheable.class);
    HashCode hash = createHash(jarHash, signature);

    return new NativeFunction(method, signature, cacheable, hash);
  }

  private static Signature createSignature(Method method)
      throws NativeFunctionImplementationException {
    Type returnType = functionType(method);
    return new Signature(returnType, createName(method), createParameters(method));
  }

  private static ImmutableList<Parameter> createParameters(Method method)
      throws NativeFunctionImplementationException {
    Class<?>[] types = method.getParameterTypes();
    if (types.length == 0 || (types[0] != NativeApi.class && types[0] != NativeApiImpl.class)) {
      throw new MissingNativeApiParameterException(method);
    }

    java.lang.reflect.Type[] parameterTypes = method.getGenericParameterTypes();
    Annotation[][] annotations = method.getParameterAnnotations();
    Builder<Parameter> builder = ImmutableList.builder();
    HashSet<String> names = new HashSet<String>();
    for (int i = 1; i < parameterTypes.length; i++) {
      Parameter parameter = createParameter(method, parameterTypes[i], annotations[i]);
      String name = parameter.name();
      if (names.contains(name)) {
        throw new DuplicatedParameterException(method, name);
      }
      names.add(name);
      builder.add(parameter);
    }
    return builder.build();
  }

  private static Name createName(Method method) throws IllegalFunctionNameException {
    String name = method.getName();
    if (Name.isLegalName(name)) {
      return Name.name(name);
    } else {
      throw new IllegalFunctionNameException(method, name);
    }
  }

  private static Type functionType(Method functionMethod)
      throws NativeFunctionImplementationException {
    TypeLiteral<?> jType = methodJType(functionMethod);
    Type type = resultJTypeToType(jType);
    if (type == null) {
      throw new IllegalReturnTypeException(functionMethod, jType);
    }
    return type;
  }

  private static TypeLiteral<?> methodJType(Method paramMethod) {
    Class<?> paramsClass = paramMethod.getDeclaringClass();
    return TypeLiteral.get(paramsClass).getReturnType(paramMethod);
  }

  private static HashCode createHash(HashCode jarHash, Signature signature) {
    Hasher hasher = Hash.newHasher();
    hasher.putBytes(jarHash.asBytes());
    hasher.putString(signature.name().value(), SmoothConstants.CHARSET);
    return hasher.hash();
  }
}
