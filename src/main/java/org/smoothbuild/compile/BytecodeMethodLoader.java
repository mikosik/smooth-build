package org.smoothbuild.compile;

import static org.smoothbuild.util.Strings.q;
import static org.smoothbuild.util.reflect.Methods.isPublic;
import static org.smoothbuild.util.reflect.Methods.isStatic;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.smoothbuild.bytecode.BytecodeF;
import org.smoothbuild.bytecode.obj.base.ObjB;
import org.smoothbuild.bytecode.obj.val.BlobB;
import org.smoothbuild.load.MethodLoader;
import org.smoothbuild.load.MethodSpec;
import org.smoothbuild.util.collect.Result;

/**
 * This class is thread-safe.
 */
@Singleton
public class BytecodeMethodLoader {
  static final String BYTECODE_METHOD_NAME = "bytecode";
  private final MethodLoader methodLoader;
  private final ConcurrentHashMap<MethodSpec, Result<Method>> cache;

  @Inject
  public BytecodeMethodLoader(MethodLoader methodLoader) {
    this.methodLoader = methodLoader;
    this.cache = new ConcurrentHashMap<>();
  }

  public Result<Method> load(String name, BlobB jar, String classBinaryName) {
    var methodSpec = new MethodSpec(jar, classBinaryName, BYTECODE_METHOD_NAME);
    return cache.computeIfAbsent(methodSpec, m -> loadImpl(name, m));
  }

  private Result<Method> loadImpl(String name, MethodSpec methodSpec) {
    String classBinaryName = methodSpec.classBinaryName();
    var qName = q(name);
    return methodLoader.provide(methodSpec)
        .validate(m -> validateSignature(m))
        .mapError(e -> loadingError(qName, classBinaryName, e));
  }

  private String validateSignature(Method method) {
    if (!isPublic(method)) {
      return "Providing method is not public.";
    } else if (!isStatic(method)) {
      return "Providing method is not static.";
    } else if (!hasBytecodeFactoryParam(method)) {
      return "Providing method parameter is not of type "
          + BytecodeF.class.getCanonicalName() + ".";
    } else if (method.getParameterTypes().length != 1) {
      return "Providing method has more than one parameter.";
    } else if (!method.getReturnType().equals(ObjB.class)) {
      return "Providing method result type is not " + ObjB.class.getName() + ".";
    } else {
      return null;
    }
  }

  private static boolean hasBytecodeFactoryParam(Method method) {
    Class<?>[] types = method.getParameterTypes();
    return types.length != 0 && (types[0] == BytecodeF.class);
  }

  private static String loadingError(String qName, String classBinaryName, String message) {
    return "Error loading bytecode provider for " + qName + " specified as `" + classBinaryName
        + "`: " + message;
  }
}
