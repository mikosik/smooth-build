package org.smoothbuild.virtualmachine.bytecode.load;

import static org.smoothbuild.common.collect.Result.err;
import static org.smoothbuild.common.collect.Result.ok;
import static org.smoothbuild.common.function.Function1.memoizer;
import static org.smoothbuild.common.reflect.Methods.isPublic;
import static org.smoothbuild.common.reflect.Methods.isStatic;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.io.IOException;
import java.lang.reflect.Method;
import org.smoothbuild.common.collect.Result;
import org.smoothbuild.common.function.Function1;
import org.smoothbuild.virtualmachine.bytecode.BytecodeFactory;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BInvoke;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BMethod;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;

/**
 * Loads java methods as instances of {@link Method}.
 * Method to load is specified by providing {@link BInvoke}.
 * This class is thread-safe.
 */
@Singleton
public class BytecodeMethodLoader {
  public static final String BYTECODE_METHOD_NAME = "bytecode";
  private final MethodLoader methodLoader;
  private final Function1<BMethod, Result<Method>, IOException> memoizer;

  @Inject
  public BytecodeMethodLoader(MethodLoader methodLoader) {
    this.methodLoader = methodLoader;
    this.memoizer = memoizer(this::loadImpl);
  }

  public Result<Method> load(BMethod bMethod) throws IOException {
    return memoizer.apply(bMethod);
  }

  private Result<Method> loadImpl(BMethod bMethod) throws IOException {
    return methodLoader.load(bMethod).flatMapOk(this::validateSignature);
  }

  private Result<Method> validateSignature(Method method) {
    if (!isPublic(method)) {
      return err("Providing method is not public.");
    }
    if (!isStatic(method)) {
      return err("Providing method is not static.");
    }
    if (!hasBytecodeFactoryParam(method)) {
      return err("Providing method parameter is not of type "
          + BytecodeFactory.class.getCanonicalName() + ".");
    }
    if (method.getParameterTypes().length != 2) {
      return err("Providing method parameter count is different than 2.");
    }
    if (!method.getReturnType().equals(BValue.class)) {
      return err("Providing method result type is not " + BValue.class.getName() + ".");
    }
    return ok(method);
  }

  private static boolean hasBytecodeFactoryParam(Method method) {
    Class<?>[] types = method.getParameterTypes();
    return types.length != 0 && (types[0] == BytecodeFactory.class);
  }
}
