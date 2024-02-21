package org.smoothbuild.vm.bytecode.load;

import static org.smoothbuild.common.Strings.q;
import static org.smoothbuild.common.collect.Either.left;
import static org.smoothbuild.common.collect.Either.right;

import jakarta.inject.Inject;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import org.smoothbuild.common.collect.Either;
import org.smoothbuild.vm.bytecode.BytecodeException;
import org.smoothbuild.vm.bytecode.BytecodeF;
import org.smoothbuild.vm.bytecode.expr.ExprB;
import org.smoothbuild.vm.bytecode.expr.value.BlobB;
import org.smoothbuild.vm.bytecode.type.value.TypeB;

/**
 * Loads smooth bytecode (ExprB) by executing java method that returns instance of ExprB.
 * This class is thread-safe.
 */
public class BytecodeLoader {
  private final BytecodeMethodLoader methodLoader;
  private final BytecodeF bytecodeF;

  @Inject
  public BytecodeLoader(BytecodeMethodLoader methodLoader, BytecodeF bytecodeF) {
    this.methodLoader = methodLoader;
    this.bytecodeF = bytecodeF;
  }

  public Either<String, ExprB> load(
      String name, BlobB jar, String classBinaryName, Map<String, TypeB> varMap)
      throws BytecodeException {
    return methodLoader
        .load(jar, classBinaryName)
        .flatMapRight(method -> invoke(method, varMap))
        .mapLeft(e -> loadingError(name, classBinaryName, e));
  }

  private Either<String, ExprB> invoke(Method method, Map<String, TypeB> varMap) {
    try {
      return right((ExprB) method.invoke(null, bytecodeF, varMap));
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
