package org.smoothbuild.virtualmachine.bytecode.load;

import static org.smoothbuild.common.base.Strings.q;
import static org.smoothbuild.common.collect.Either.left;
import static org.smoothbuild.common.collect.Either.right;

import jakarta.inject.Inject;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import org.smoothbuild.common.collect.Either;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.BytecodeFactory;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BBlob;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BExpr;
import org.smoothbuild.virtualmachine.bytecode.type.base.BType;

/**
 * Loads smooth bytecode (ExprB) by executing java method that returns instance of ExprB.
 * This class is thread-safe.
 */
public class BytecodeLoader {
  private final BytecodeMethodLoader methodLoader;
  private final BytecodeFactory bytecodeFactory;

  @Inject
  public BytecodeLoader(BytecodeMethodLoader methodLoader, BytecodeFactory bytecodeFactory) {
    this.methodLoader = methodLoader;
    this.bytecodeFactory = bytecodeFactory;
  }

  public Either<String, BExpr> load(
      String name, BBlob jar, String classBinaryName, Map<String, BType> varMap)
      throws BytecodeException {
    return methodLoader
        .load(jar, classBinaryName)
        .flatMapRight(method -> invoke(method, varMap))
        .mapLeft(e -> loadingError(name, classBinaryName, e));
  }

  private Either<String, BExpr> invoke(Method method, Map<String, BType> varMap) {
    try {
      return right((BExpr) method.invoke(null, bytecodeFactory, varMap));
    } catch (IllegalAccessException e) {
      return left("Cannot access provider method: " + e);
    } catch (InvocationTargetException e) {
      return left("Providing method thrown exception: " + e.getCause());
    }
  }

  private static String loadingError(String name, String classBinaryName, String message) {
    return "Error loading bytecode for " + q(name) + " using provider specified as `"
        + classBinaryName + "`: " + message;
  }
}
