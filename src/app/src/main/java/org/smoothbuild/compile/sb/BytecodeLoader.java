package org.smoothbuild.compile.sb;

import static org.smoothbuild.util.Strings.q;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import javax.inject.Inject;

import org.smoothbuild.util.collect.Try;
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

  public Try<ExprB> load(String name, BlobB jar, String classBinaryName, Map<String, TypeB> varMap) {
    return methodLoader.load(jar, classBinaryName)
        .flatMap(method -> invoke(method, varMap))
        .mapError(e -> loadingError(name, classBinaryName, e));
  }

  private Try<ExprB> invoke(Method method, Map<String, TypeB> varMap) {
    try {
      return Try.result((ExprB) method.invoke(null, bytecodeF, varMap));
    } catch (IllegalAccessException e) {
      return Try.error("Cannot access provider method: " + e);
    } catch (InvocationTargetException e) {
      return Try.error("Providing method thrown exception: " + e.getCause());
    }
  }

  private static String loadingError(String name, String classBinaryName, String message) {
    return "Error loading bytecode for " + q(name) + " using provider specified as `"
        + classBinaryName + "`: " + message;
  }
}
