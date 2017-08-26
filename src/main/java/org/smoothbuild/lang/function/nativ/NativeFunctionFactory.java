package org.smoothbuild.lang.function.nativ;

import static org.smoothbuild.lang.function.nativ.NativeParameterFactory.createParameter;
import static org.smoothbuild.lang.type.Types.jTypeToType;
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
import org.smoothbuild.lang.plugin.Container;
import org.smoothbuild.lang.plugin.NotCacheable;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.task.exec.ContainerImpl;

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
      throw new NativeFunctionImplementationException(method, "It should be static.");
    }
    if (!isPublic(method)) {
      throw new NativeFunctionImplementationException(method, "It should be public.");
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
    if (types.length == 0 || (types[0] != Container.class && types[0] != ContainerImpl.class)) {
      throw new NativeFunctionImplementationException(method, "Its first parameter should have '"
          + Container.class.getCanonicalName() + "' type.");
    }

    java.lang.reflect.Parameter[] parameters = method.getParameters();
    Annotation[][] annotations = method.getParameterAnnotations();
    Builder<Parameter> builder = ImmutableList.builder();
    for (int i = 1; i < parameters.length; i++) {
      Parameter parameter = createParameter(method, parameters[i], annotations[i]);
      builder.add(parameter);
    }
    return builder.build();
  }

  private static Name createName(Method method) throws NativeFunctionImplementationException {
    String name = method.getName();
    if (Name.isLegalName(name)) {
      return new Name(name);
    } else {
      throw new NativeFunctionImplementationException(method, "Its name " + name + " is illegal.");
    }
  }

  private static Type functionType(Method functionMethod)
      throws NativeFunctionImplementationException {
    TypeLiteral<?> jType = methodJType(functionMethod);
    Type type = jTypeToType(jType);
    if (type == null) {
      throw new NativeFunctionImplementationException(functionMethod,
          "It has is illegal result type '" + jType + "'.");
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
    hasher.putString(signature.name().toString(), SmoothConstants.CHARSET);
    return hasher.hash();
  }
}
