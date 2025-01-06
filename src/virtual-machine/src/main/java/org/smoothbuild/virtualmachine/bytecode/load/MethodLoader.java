package org.smoothbuild.virtualmachine.bytecode.load;

import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.Result.err;
import static org.smoothbuild.common.collect.Result.ok;
import static org.smoothbuild.common.function.Function1.memoizer;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.io.IOException;
import java.lang.reflect.Method;
import org.smoothbuild.common.collect.Result;
import org.smoothbuild.common.function.Function1;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BMethod;

/**
 * Loads java methods as instances of {@link Method}.
 * Method to load is specified by providing {@link BMethod}.
 * This class is thread-safe.
 */
@Singleton
public class MethodLoader {
  private final JarClassLoaderFactory jarClassLoaderFactory;
  private final Function1<BMethod, Result<Method>, IOException> memoizer;

  @Inject
  public MethodLoader(JarClassLoaderFactory jarClassLoaderFactory) {
    this.jarClassLoaderFactory = jarClassLoaderFactory;
    this.memoizer = memoizer(this::findMethod);
  }

  public Result<Method> load(BMethod bMethod) throws IOException {
    return memoizer.apply(bMethod);
  }

  private Result<Method> findMethod(BMethod bMethod) throws IOException {
    return findClass(bMethod).flatMapOk(c -> findMethodInClass(bMethod, c));
  }

  private Result<Class<?>> findClass(BMethod bMethod) throws IOException {
    return jarClassLoaderFactory
        .classLoaderFor(bMethod.jar())
        .flatMapOk(classLoader -> loadClass(classLoader, bMethod));
  }

  private Result<Class<?>> loadClass(ClassLoader classLoader, BMethod bMethod)
      throws BytecodeException {
    try {
      return ok(classLoader.loadClass(bMethod.classBinaryName().toJavaString()));
    } catch (ClassNotFoundException e) {
      return err("Class not found in jar.");
    }
  }

  private static Result<Method> findMethodInClass(BMethod bMethod, Class<?> clazz)
      throws BytecodeException {
    var declaredMethods = list(clazz.getDeclaredMethods());
    var methods =
        declaredMethods.filter(m -> m.getName().equals(bMethod.methodName().toJavaString()));
    return switch (methods.size()) {
      case 0 -> err(missingMethodError(bMethod));
      case 1 -> ok(methods.get(0));
      default -> err(overloadedMethodError(bMethod));
    };
  }

  private static String missingMethodError(BMethod bMethod) throws BytecodeException {
    var classBinaryName = bMethod.classBinaryName().toJavaString();
    var methodName = bMethod.methodName().toJavaString();
    return "Class '%s' does not have '%s' method.".formatted(classBinaryName, methodName);
  }

  private static String overloadedMethodError(BMethod bMethod) throws BytecodeException {
    var classBinaryName = bMethod.classBinaryName().toJavaString();
    var methodName = bMethod.methodName().toJavaString();
    return "Class '%s' has more than one '%s' method.".formatted(classBinaryName, methodName);
  }
}
