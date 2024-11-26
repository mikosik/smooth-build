package org.smoothbuild.virtualmachine.bytecode.load;

import static org.smoothbuild.common.collect.Either.left;
import static org.smoothbuild.common.collect.Either.right;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.function.Function1.memoizer;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.io.IOException;
import java.lang.reflect.Method;
import org.smoothbuild.common.collect.Either;
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
  private final Function1<BMethod, Either<String, Method>, IOException> memoizer;

  @Inject
  public MethodLoader(JarClassLoaderFactory jarClassLoaderFactory) {
    this.jarClassLoaderFactory = jarClassLoaderFactory;
    this.memoizer = memoizer(this::findMethod);
  }

  public Either<String, Method> load(BMethod bMethod) throws IOException {
    return memoizer.apply(bMethod);
  }

  private Either<String, Method> findMethod(BMethod bMethod) throws IOException {
    return findClass(bMethod).flatMapRight(c -> findMethodInClass(bMethod, c));
  }

  private Either<String, Class<?>> findClass(BMethod bMethod) throws IOException {
    return jarClassLoaderFactory
        .classLoaderFor(bMethod.jar())
        .flatMapRight(classLoader -> loadClass(classLoader, bMethod));
  }

  private Either<String, Class<?>> loadClass(ClassLoader classLoader, BMethod bMethod)
      throws BytecodeException {
    try {
      return right(classLoader.loadClass(bMethod.classBinaryName().toJavaString()));
    } catch (ClassNotFoundException e) {
      return left("Class not found in jar.");
    }
  }

  private static Either<String, Method> findMethodInClass(BMethod bMethod, Class<?> clazz)
      throws BytecodeException {
    var declaredMethods = list(clazz.getDeclaredMethods());
    var methods =
        declaredMethods.filter(m -> m.getName().equals(bMethod.methodName().toJavaString()));
    return switch (methods.size()) {
      case 0 -> left(missingMethodError(bMethod));
      case 1 -> right(methods.get(0));
      default -> left(overloadedMethodError(bMethod));
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
