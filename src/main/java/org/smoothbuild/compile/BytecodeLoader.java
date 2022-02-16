package org.smoothbuild.compile;

import static org.smoothbuild.util.Strings.q;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.inject.Inject;

import org.smoothbuild.bytecode.BytecodeF;
import org.smoothbuild.bytecode.obj.base.ObjB;
import org.smoothbuild.bytecode.obj.val.BlobB;
import org.smoothbuild.util.collect.Result;

public class BytecodeLoader {
  private final BytecodeMethodLoader methodLoader;
  private final BytecodeF bytecodeF;

  @Inject
  public BytecodeLoader(BytecodeMethodLoader methodLoader, BytecodeF bytecodeF) {
    this.methodLoader = methodLoader;
    this.bytecodeF = bytecodeF;
  }

  public Result<ObjB> load(String name, BlobB jar, String classBinaryName) {
    return methodLoader.load(name, jar, classBinaryName)
        .flatMap(this::invoke)
        .mapError(e -> loadingError(name, classBinaryName, e));
  }

  private Result<ObjB> invoke(Method method) {
    try {
      return Result.of((ObjB) method.invoke(null, bytecodeF));
    } catch (IllegalAccessException e) {
      return Result.error("Cannot access provider method: " + e);
    } catch (InvocationTargetException e) {
      return Result.error("Providing method thrown exception: " + e.getCause());
    }
  }

  private static String loadingError(String name, String classBinaryName, String message) {
    return "Error loading bytecode for " + q(name) + " using provider specified as `"
        + classBinaryName + "`: " + message;
  }
}
