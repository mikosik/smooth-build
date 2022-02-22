package org.smoothbuild.load;

import static java.util.Arrays.asList;
import static org.smoothbuild.util.collect.Lists.filter;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.smoothbuild.util.collect.Try;

/**
 * This class is thread-safe.
 */
@Singleton
public class MethodLoader {
  private final JarClassLoaderProv jarClassLoaderProv;
  private final ConcurrentHashMap<MethodSpec, Try<Method>> cache;

  @Inject
  public MethodLoader(JarClassLoaderProv jarClassLoaderProv) {
    this.jarClassLoaderProv = jarClassLoaderProv;
    this.cache = new ConcurrentHashMap<>();
  }

  public Try<Method> provide(MethodSpec methodSpec) {
    return cache.computeIfAbsent(methodSpec, this::findMethod);
  }

  private Try<Method> findMethod(MethodSpec methodSpec) {
    return findClass(methodSpec)
        .flatMap(c -> findMethod(methodSpec, c));
  }

  private Try<Class<?>> findClass(MethodSpec methodSpec) {
    try {
      return jarClassLoaderProv.classLoaderFor(methodSpec.jar())
          .flatMap(classLoader -> loadClass(classLoader, methodSpec));
    } catch (IOException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  private Try<Class<?>> loadClass(ClassLoader classLoader, MethodSpec methodSpec) {
    try {
      return Try.result(classLoader.loadClass(methodSpec.classBinaryName()));
    } catch (ClassNotFoundException e) {
      return Try.error("Class not found in jar.");
    }
  }

  private static Try<Method> findMethod(MethodSpec methodSpec, Class<?> clazz) {
    var declaredMethods = asList(clazz.getDeclaredMethods());
    var methods = filter(declaredMethods, m -> m.getName().equals(methodSpec.methodName()));
    return switch (methods.size()) {
      case 0 -> missingMethodError(methodSpec);
      case 1 -> Try.result(methods.get(0));
      default -> overloadedMethodError(methodSpec);
    };
  }

  private static Try<Method> missingMethodError(MethodSpec methodSpec) {
    return Try.error("Class '%s' does not have '%s' method."
        .formatted(methodSpec.classBinaryName(), methodSpec.methodName()));
  }

  private static Try<Method> overloadedMethodError(MethodSpec methodSpec) {
    return Try.error("Class '%s' has more than one '%s' method."
        .formatted(methodSpec.classBinaryName(), methodSpec.methodName()));
  }
}
