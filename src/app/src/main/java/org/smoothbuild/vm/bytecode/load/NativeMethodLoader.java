package org.smoothbuild.vm.bytecode.load;

import static org.smoothbuild.common.reflect.Methods.isPublic;
import static org.smoothbuild.common.reflect.Methods.isStatic;

import io.vavr.control.Either;
import jakarta.inject.Inject;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import org.smoothbuild.vm.bytecode.expr.value.NativeFuncB;
import org.smoothbuild.vm.bytecode.expr.value.TupleB;
import org.smoothbuild.vm.bytecode.expr.value.ValueB;
import org.smoothbuild.vm.evaluate.compute.Container;
import org.smoothbuild.vm.evaluate.plugin.NativeApi;

/**
 * This class is thread-safe.
 */
public class NativeMethodLoader {
  public static final String NATIVE_METHOD_NAME = "func";
  private final MethodLoader methodLoader;
  private final ConcurrentHashMap<NativeFuncB, Either<String, Method>> cache;

  @Inject
  public NativeMethodLoader(MethodLoader methodLoader) {
    this.methodLoader = methodLoader;
    this.cache = new ConcurrentHashMap<>();
  }

  public Either<String, Method> load(NativeFuncB nativeFuncB) {
    return cache.computeIfAbsent(nativeFuncB, this::loadImpl);
  }

  private Either<String, Method> loadImpl(NativeFuncB nativeFuncB) {
    var classBinaryName = nativeFuncB.classBinaryName().toJ();
    var methodSpec = new MethodSpec(nativeFuncB.jar(), classBinaryName, NATIVE_METHOD_NAME);
    return methodLoader
        .provide(methodSpec)
        .flatMap(this::validateMethodSignature)
        .mapLeft(e -> loadingError(classBinaryName, e));
  }

  private Either<String, Method> validateMethodSignature(Method method) {
    if (!isPublic(method)) {
      return Either.left("Providing method is not public.");
    } else if (!isStatic(method)) {
      return Either.left("Providing method is not static.");
    } else {
      return validateMethodParams(method);
    }
  }

  private Either<String, Method> validateMethodParams(Method method) {
    Class<?> returnType = method.getReturnType();
    if (!returnType.equals(ValueB.class)) {
      return Either.left("Providing method should declare return type as "
          + ValueB.class.getCanonicalName() + " but is " + returnType.getCanonicalName() + ".");
    }
    Class<?>[] types = method.getParameterTypes();
    boolean valid = types.length == 2
        && (types[0].equals(NativeApi.class) || types[0].equals(Container.class))
        && (types[1].equals(TupleB.class));
    if (valid) {
      return Either.right(method);
    } else {
      return Either.left("Providing method should have two parameters "
          + NativeApi.class.getCanonicalName() + " and " + TupleB.class.getCanonicalName() + ".");
    }
  }

  private static String loadingError(String classBinaryName, String message) {
    return "Error loading native implementation specified as `" + classBinaryName + "`: " + message;
  }
}
