package org.smoothbuild.vm.task;

import static org.smoothbuild.util.reflect.Methods.isPublic;
import static org.smoothbuild.util.reflect.Methods.isStatic;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;

import org.smoothbuild.bytecode.expr.inst.InstB;
import org.smoothbuild.bytecode.expr.inst.NatFuncB;
import org.smoothbuild.bytecode.expr.inst.TupleB;
import org.smoothbuild.load.MethodLoader;
import org.smoothbuild.load.MethodSpec;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.util.collect.Try;
import org.smoothbuild.vm.compute.Container;

/**
 * This class is thread-safe.
 */
public class NativeMethodLoader {
  public static final String NATIVE_METHOD_NAME = "func";
  private final MethodLoader methodLoader;
  private final ConcurrentHashMap<NatFuncB, Try<Method>> cache;

  @Inject
  public NativeMethodLoader(MethodLoader methodLoader) {
    this.methodLoader = methodLoader;
    this.cache = new ConcurrentHashMap<>();
  }

  public Try<Method> load(NatFuncB natFuncB) {
    return cache.computeIfAbsent(natFuncB, this::loadImpl);
  }

  private Try<Method> loadImpl(NatFuncB natFuncB) {
    var classBinaryName = natFuncB.classBinaryName().toJ();
    var methodSpec = new MethodSpec(natFuncB.jar(), classBinaryName, NATIVE_METHOD_NAME);
    return methodLoader.provide(methodSpec)
        .validate(this::validateMethodSignature)
        .mapError(e -> loadingError(classBinaryName, e));
  }

  private String validateMethodSignature(Method method) {
    if (!isPublic(method)) {
      return "Providing method is not public.";
    } else if (!isStatic(method)) {
      return "Providing method is not static.";
    } else {
      return validateMethodParams(method);
    }
  }

  private String validateMethodParams(Method method) {
    Class<?> returnType = method.getReturnType();
    if (!returnType.equals(InstB.class)) {
      return "Providing method should declare return type as " + InstB.class.getCanonicalName()
          + " but is " + returnType.getCanonicalName() + ".";
    }
    Class<?>[] types = method.getParameterTypes();
    boolean valid = types.length == 2
        && (types[0].equals(NativeApi.class) || types[0].equals(Container.class))
        && (types[1].equals(TupleB.class));
    if (valid) {
      return null;
    } else {
      return "Providing method should have two parameters " + NativeApi.class.getCanonicalName()
          + " and " + TupleB.class.getCanonicalName() + ".";
    }
  }

  private static String loadingError(String classBinaryName, String message) {
    return "Error loading native implementation specified as `" + classBinaryName + "`: " + message;
  }
}
