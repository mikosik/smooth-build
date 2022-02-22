package org.smoothbuild.vm.job.algorithm;

import static org.smoothbuild.util.Strings.q;
import static org.smoothbuild.util.reflect.Methods.isPublic;
import static org.smoothbuild.util.reflect.Methods.isStatic;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;

import org.smoothbuild.bytecode.obj.val.MethodB;
import org.smoothbuild.bytecode.obj.val.TupleB;
import org.smoothbuild.bytecode.type.base.TypeB;
import org.smoothbuild.load.MethodLoader;
import org.smoothbuild.load.MethodSpec;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.util.collect.Try;
import org.smoothbuild.vm.compute.Container;

/**
 * This class is thread-safe.
 */
public class NativeMethodLoader {
  static final String NATIVE_METHOD_NAME = "func";
  private final MethodLoader methodLoader;
  private final ConcurrentHashMap<MethodB, Try<Method>> cache;

  @Inject
  public NativeMethodLoader(MethodLoader methodLoader) {
    this.methodLoader = methodLoader;
    this.cache = new ConcurrentHashMap<>();
  }

  public Try<Method> load(String name, MethodB methodB) {
    return cache.computeIfAbsent(methodB, m -> loadImpl(name, m));
  }

  private Try<Method> loadImpl(String name, MethodB methodB) {
    var classBinaryName = methodB.classBinaryName().toJ();
    var qName = q(name);
    var methodSpec = new MethodSpec(methodB.jar(), classBinaryName, NATIVE_METHOD_NAME);
    return methodLoader.provide(methodSpec)
        .validate(m -> validateMethodSignature(m))
        .validate(m -> validateNativeResT(m, qName, methodB.type().res()))
        .mapError(e -> loadingError(qName, classBinaryName, e));
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

  private static String validateNativeResT(Method method, String qName, TypeB resTB) {
    var methodResTJ = method.getReturnType();
    var resTJ = resTB.typeJ();
    if (!resTJ.equals(methodResTJ)) {
      return qName + " declares type " + resTB.q()
          + " so its native implementation result type must be " + resTJ.getCanonicalName()
          + " but it is " + methodResTJ.getCanonicalName() + ".";
    }
    return null;
  }

  private static String loadingError(String qName, String classBinaryName, String message) {
    return "Error loading native implementation for "
        + qName + " specified as `" + classBinaryName + "`: " + message;
  }
}
