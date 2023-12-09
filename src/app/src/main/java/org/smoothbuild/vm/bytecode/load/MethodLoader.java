package org.smoothbuild.vm.bytecode.load;

import static org.smoothbuild.common.collect.Either.left;
import static org.smoothbuild.common.collect.Either.right;
import static org.smoothbuild.common.collect.List.list;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import org.smoothbuild.common.collect.Either;

/**
 * This class is thread-safe.
 */
@Singleton
public class MethodLoader {
  private final JarClassLoaderProv jarClassLoaderProv;
  private final ConcurrentHashMap<MethodSpec, Either<String, Method>> cache;

  @Inject
  public MethodLoader(JarClassLoaderProv jarClassLoaderProv) {
    this.jarClassLoaderProv = jarClassLoaderProv;
    this.cache = new ConcurrentHashMap<>();
  }

  public Either<String, Method> provide(MethodSpec methodSpec) {
    return cache.computeIfAbsent(methodSpec, this::findMethod);
  }

  private Either<String, Method> findMethod(MethodSpec methodSpec) {
    return findClass(methodSpec).flatMapRight(c -> findMethod(methodSpec, c));
  }

  private Either<String, Class<?>> findClass(MethodSpec methodSpec) {
    try {
      return jarClassLoaderProv
          .classLoaderFor(methodSpec.jar())
          .flatMapRight(classLoader -> loadClass(classLoader, methodSpec));
    } catch (IOException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  private Either<String, Class<?>> loadClass(ClassLoader classLoader, MethodSpec methodSpec) {
    try {
      return right(classLoader.loadClass(methodSpec.classBinaryName()));
    } catch (ClassNotFoundException e) {
      return left("Class not found in jar.");
    }
  }

  private static Either<String, Method> findMethod(MethodSpec methodSpec, Class<?> clazz) {
    var declaredMethods = list(clazz.getDeclaredMethods());
    var methods = declaredMethods.filter(m -> m.getName().equals(methodSpec.methodName()));
    return switch (methods.size()) {
      case 0 -> left(missingMethodError(methodSpec));
      case 1 -> right(methods.get(0));
      default -> left(overloadedMethodError(methodSpec));
    };
  }

  private static String missingMethodError(MethodSpec methodSpec) {
    return "Class '%s' does not have '%s' method."
        .formatted(methodSpec.classBinaryName(), methodSpec.methodName());
  }

  private static String overloadedMethodError(MethodSpec methodSpec) {
    return "Class '%s' has more than one '%s' method."
        .formatted(methodSpec.classBinaryName(), methodSpec.methodName());
  }
}
