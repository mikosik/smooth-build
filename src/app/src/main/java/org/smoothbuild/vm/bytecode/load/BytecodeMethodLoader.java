package org.smoothbuild.vm.bytecode.load;

import static org.smoothbuild.common.reflect.Methods.isPublic;
import static org.smoothbuild.common.reflect.Methods.isStatic;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import org.smoothbuild.common.collect.Either;
import org.smoothbuild.vm.bytecode.BytecodeF;
import org.smoothbuild.vm.bytecode.expr.value.BlobB;
import org.smoothbuild.vm.bytecode.expr.value.ValueB;

/**
 * This class is thread-safe.
 */
@Singleton
public class BytecodeMethodLoader {
  static final String BYTECODE_METHOD_NAME = "bytecode";
  private final MethodLoader methodLoader;
  private final ConcurrentHashMap<MethodSpec, Either<String, Method>> cache;

  @Inject
  public BytecodeMethodLoader(MethodLoader methodLoader) {
    this.methodLoader = methodLoader;
    this.cache = new ConcurrentHashMap<>();
  }

  public Either<String, Method> load(BlobB jar, String classBinaryName) {
    var methodSpec = new MethodSpec(jar, classBinaryName, BYTECODE_METHOD_NAME);
    return cache.computeIfAbsent(methodSpec, this::loadImpl);
  }

  private Either<String, Method> loadImpl(MethodSpec methodSpec) {
    return methodLoader.load(methodSpec).flatMapRight(this::validateSignature);
  }

  private Either<String, Method> validateSignature(Method method) {
    if (!isPublic(method)) {
      return Either.left("Providing method is not public.");
    } else if (!isStatic(method)) {
      return Either.left("Providing method is not static.");
    } else if (!hasBytecodeFactoryParam(method)) {
      return Either.left(
          "Providing method parameter is not of type " + BytecodeF.class.getCanonicalName() + ".");
    } else if (method.getParameterTypes().length != 2) {
      return Either.left("Providing method parameter count is different than 2.");
    } else if (!method.getReturnType().equals(ValueB.class)) {
      return Either.left("Providing method result type is not " + ValueB.class.getName() + ".");
    } else {
      return Either.right(method);
    }
  }

  private static boolean hasBytecodeFactoryParam(Method method) {
    Class<?>[] types = method.getParameterTypes();
    return types.length != 0 && (types[0] == BytecodeF.class);
  }
}
