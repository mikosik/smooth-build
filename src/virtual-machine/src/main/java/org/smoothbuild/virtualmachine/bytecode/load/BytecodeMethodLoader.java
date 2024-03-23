package org.smoothbuild.virtualmachine.bytecode.load;

import static org.smoothbuild.common.collect.Either.left;
import static org.smoothbuild.common.collect.Either.right;
import static org.smoothbuild.common.function.Function1.memoizer;
import static org.smoothbuild.common.reflect.Methods.isPublic;
import static org.smoothbuild.common.reflect.Methods.isStatic;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.lang.reflect.Method;
import org.smoothbuild.common.collect.Either;
import org.smoothbuild.common.function.Function1;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.BytecodeFactory;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BBlob;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BNativeFunc;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BValue;

/**
 * Loads java methods as instances of {@link Method}.
 * Method to load is specified by providing {@link BNativeFunc}.
 * This class is thread-safe.
 */
@Singleton
public class BytecodeMethodLoader {
  static final String BYTECODE_METHOD_NAME = "bytecode";
  private final MethodLoader methodLoader;
  private final Function1<MethodSpec, Either<String, Method>, BytecodeException> memoizer;

  @Inject
  public BytecodeMethodLoader(MethodLoader methodLoader) {
    this.methodLoader = methodLoader;
    this.memoizer = memoizer(this::loadImpl);
  }

  public Either<String, Method> load(BBlob jar, String classBinaryName) throws BytecodeException {
    var methodSpec = new MethodSpec(jar, classBinaryName, BYTECODE_METHOD_NAME);
    return memoizer.apply(methodSpec);
  }

  private Either<String, Method> loadImpl(MethodSpec methodSpec) throws BytecodeException {
    return methodLoader.load(methodSpec).flatMapRight(this::validateSignature);
  }

  private Either<String, Method> validateSignature(Method method) {
    if (!isPublic(method)) {
      return left("Providing method is not public.");
    }
    if (!isStatic(method)) {
      return left("Providing method is not static.");
    }
    if (!hasBytecodeFactoryParam(method)) {
      return left("Providing method parameter is not of type "
          + BytecodeFactory.class.getCanonicalName() + ".");
    }
    if (method.getParameterTypes().length != 2) {
      return left("Providing method parameter count is different than 2.");
    }
    if (!method.getReturnType().equals(BValue.class)) {
      return left("Providing method result type is not " + BValue.class.getName() + ".");
    }
    return right(method);
  }

  private static boolean hasBytecodeFactoryParam(Method method) {
    Class<?>[] types = method.getParameterTypes();
    return types.length != 0 && (types[0] == BytecodeFactory.class);
  }
}
