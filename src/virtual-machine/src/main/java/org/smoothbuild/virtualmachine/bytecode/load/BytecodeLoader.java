package org.smoothbuild.virtualmachine.bytecode.load;

import static org.smoothbuild.common.base.Strings.q;
import static org.smoothbuild.common.collect.Result.err;
import static org.smoothbuild.common.collect.Result.ok;

import jakarta.inject.Inject;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.smoothbuild.common.collect.Map;
import org.smoothbuild.common.collect.Result;
import org.smoothbuild.virtualmachine.bytecode.BytecodeFactory;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BExpr;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BMethod;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BType;

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

  public Result<BExpr> load(String name, BMethod bMethod, Map<String, BType> varMap)
      throws IOException {
    return methodLoader
        .load(bMethod)
        .flatMapOk(jMethod -> invoke(jMethod, varMap))
        .mapErr(e -> loadingError(name, bMethod.classBinaryName().toJavaString(), e));
  }

  private Result<BExpr> invoke(Method method, Map<String, BType> varMap) {
    try {
      return ok((BExpr) method.invoke(null, bytecodeFactory, varMap.asJdkMap()));
    } catch (IllegalAccessException e) {
      return err("Cannot access provider method: " + e);
    } catch (InvocationTargetException e) {
      return err("Providing method thrown exception: " + e.getCause());
    }
  }

  private static String loadingError(String name, String classBinaryName, String message) {
    return "Error loading bytecode for " + q(name) + " using provider specified as `"
        + classBinaryName + "`: " + message;
  }
}
