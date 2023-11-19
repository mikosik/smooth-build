package org.smoothbuild.vm.bytecode.load;

import static org.smoothbuild.common.Strings.q;

import io.vavr.control.Either;
import jakarta.inject.Inject;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import org.smoothbuild.vm.bytecode.BytecodeF;
import org.smoothbuild.vm.bytecode.expr.ExprB;
import org.smoothbuild.vm.bytecode.expr.value.BlobB;
import org.smoothbuild.vm.bytecode.type.value.TypeB;

public class BytecodeLoader {
  private final BytecodeMethodLoader methodLoader;
  private final BytecodeF bytecodeF;

  @Inject
  public BytecodeLoader(BytecodeMethodLoader methodLoader, BytecodeF bytecodeF) {
    this.methodLoader = methodLoader;
    this.bytecodeF = bytecodeF;
  }

  public Either<String, ExprB> load(
      String name, BlobB jar, String classBinaryName, Map<String, TypeB> varMap) {
    return methodLoader
        .load(jar, classBinaryName)
        .flatMap(method -> invoke(method, varMap))
        .mapLeft(e -> loadingError(name, classBinaryName, e));
  }

  private Either<String, ExprB> invoke(Method method, Map<String, TypeB> varMap) {
    try {
      return Either.right((ExprB) method.invoke(null, bytecodeF, varMap));
    } catch (IllegalAccessException e) {
      return Either.left("Cannot access provider method: " + e);
    } catch (InvocationTargetException e) {
      return Either.left("Providing method thrown exception: " + e.getCause());
    }
  }

  private static String loadingError(String name, String classBinaryName, String message) {
    return "Error loading bytecode for " + q(name) + " using provider specified as `"
        + classBinaryName + "`: " + message;
  }
}
