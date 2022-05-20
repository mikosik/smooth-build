package org.smoothbuild.compile;

import static org.smoothbuild.util.Strings.q;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import javax.inject.Inject;

import org.smoothbuild.bytecode.BytecodeF;
import org.smoothbuild.bytecode.obj.base.ObjB;
import org.smoothbuild.bytecode.obj.cnst.BlobB;
import org.smoothbuild.bytecode.type.cnst.TypeB;
import org.smoothbuild.util.collect.Try;

public class BytecodeLoader {
  private final BytecodeMethodLoader methodLoader;
  private final BytecodeF bytecodeF;

  @Inject
  public BytecodeLoader(BytecodeMethodLoader methodLoader, BytecodeF bytecodeF) {
    this.methodLoader = methodLoader;
    this.bytecodeF = bytecodeF;
  }

  public Try<ObjB> load(String name, BlobB jar, String classBinaryName, Map<String, TypeB> varMap) {
    return methodLoader.load(jar, classBinaryName)
        .flatMap(method -> invoke(method, varMap))
        .mapError(e -> loadingError(name, classBinaryName, e));
  }

  private Try<ObjB> invoke(Method method, Map<String, TypeB> varMap) {
    try {
      return Try.result((ObjB) method.invoke(null, bytecodeF, varMap));
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
