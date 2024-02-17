package org.smoothbuild.vm.bytecode.load;

import static org.smoothbuild.common.collect.Either.left;
import static org.smoothbuild.common.collect.Either.right;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.function.Function0.memoize;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import org.smoothbuild.common.collect.Either;
import org.smoothbuild.common.function.Function0;
import org.smoothbuild.vm.bytecode.BytecodeException;

/**
 * This class is thread-safe.
 */
@Singleton
public class MethodLoader {
  private final JarClassLoaderFactory jarClassLoaderFactory;
  private final ConcurrentHashMap<MethodSpec, Function0<Either<String, Method>, BytecodeException>>
      cache;

  @Inject
  public MethodLoader(JarClassLoaderFactory jarClassLoaderFactory) {
    this.jarClassLoaderFactory = jarClassLoaderFactory;
    this.cache = new ConcurrentHashMap<>();
  }

  public Either<String, Method> load(MethodSpec methodSpec) throws BytecodeException {
    var memoizer = cache.computeIfAbsent(methodSpec, (ms) -> memoize(() -> findMethod(ms)));
    return memoizer.apply();
  }

  private Either<String, Method> findMethod(MethodSpec methodSpec) throws BytecodeException {
    return findClass(methodSpec).flatMapRight(c -> findMethodInClass(methodSpec, c));
  }

  private Either<String, Class<?>> findClass(MethodSpec methodSpec) throws BytecodeException {
    return jarClassLoaderFactory
        .classLoaderFor(methodSpec.jar())
        .flatMapRight(classLoader -> loadClass(classLoader, methodSpec));
  }

  private Either<String, Class<?>> loadClass(ClassLoader classLoader, MethodSpec methodSpec) {
    try {
      return right(classLoader.loadClass(methodSpec.classBinaryName()));
    } catch (ClassNotFoundException e) {
      return left("Class not found in jar.");
    }
  }

  private static Either<String, Method> findMethodInClass(MethodSpec methodSpec, Class<?> clazz) {
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
