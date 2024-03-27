package org.smoothbuild.virtualmachine.bytecode.load;

import static org.smoothbuild.common.function.Function1.memoizer;
import static org.smoothbuild.common.reflect.Methods.isPublic;
import static org.smoothbuild.common.reflect.Methods.isStatic;

import jakarta.inject.Inject;
import java.lang.reflect.Method;
import org.smoothbuild.common.collect.Either;
import org.smoothbuild.common.function.Function1;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BInvoke;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTuple;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;
import org.smoothbuild.virtualmachine.evaluate.compute.Container;
import org.smoothbuild.virtualmachine.evaluate.plugin.NativeApi;

/**
 * Loads java methods as instances of {@link Method}.
 * Method to load is specified by providing {@link BInvoke}.
 * This class is thread-safe.
 */
public class NativeMethodLoader {
  public static final String NATIVE_METHOD_NAME = "func";
  private final MethodLoader methodLoader;
  private final Function1<BInvoke, Either<String, Method>, BytecodeException> memoizer;

  @Inject
  public NativeMethodLoader(MethodLoader methodLoader) {
    this.methodLoader = methodLoader;
    this.memoizer = memoizer(this::loadImpl);
  }

  public Either<String, Method> load(BInvoke invoke) throws BytecodeException {
    return memoizer.apply(invoke);
  }

  private Either<String, Method> loadImpl(BInvoke invoke) throws BytecodeException {
    var classBinaryName = invoke.classBinaryName().toJavaString();
    var methodSpec = new MethodSpec(invoke.jar(), classBinaryName, NATIVE_METHOD_NAME);
    return methodLoader
        .load(methodSpec)
        .flatMapRight(this::validateMethodSignature)
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
    if (!returnType.equals(BValue.class)) {
      return Either.left("Providing method should declare return type as "
          + BValue.class.getCanonicalName() + " but is " + returnType.getCanonicalName() + ".");
    }
    Class<?>[] types = method.getParameterTypes();
    boolean valid = types.length == 2
        && (types[0].equals(NativeApi.class) || types[0].equals(Container.class))
        && (types[1].equals(BTuple.class));
    if (valid) {
      return Either.right(method);
    } else {
      return Either.left("Providing method should have two parameters "
          + NativeApi.class.getCanonicalName() + " and " + BTuple.class.getCanonicalName() + ".");
    }
  }

  private static String loadingError(String classBinaryName, String message) {
    return "Error loading native implementation specified as `" + classBinaryName + "`: " + message;
  }
}
