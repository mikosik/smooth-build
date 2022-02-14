package org.smoothbuild.vm.job.algorithm;

import static org.smoothbuild.util.Strings.q;
import static org.smoothbuild.util.reflect.Methods.isPublic;
import static org.smoothbuild.util.reflect.Methods.isStatic;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;

import org.smoothbuild.bytecode.obj.val.MethodB;
import org.smoothbuild.bytecode.type.base.TypeB;
import org.smoothbuild.load.MethodLoader;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.util.collect.Result;
import org.smoothbuild.vm.compute.Container;

import com.google.common.collect.ImmutableList;

public class NativeMethodLoader {
  static final String NATIVE_METHOD_NAME = "func";
  private final MethodLoader methodLoader;
  private final ConcurrentHashMap<MethodB, Result<Method>> cache;

  @Inject
  public NativeMethodLoader(MethodLoader methodLoader) {
    this.methodLoader = methodLoader;
    this.cache = new ConcurrentHashMap<>();
  }

  public Result<Method> load(String name, MethodB methodB) {
    return cache.computeIfAbsent(methodB, m -> loadImpl(name, m));
  }

  private Result<Method> loadImpl(String name, MethodB methodB) {
    String classBinaryName = methodB.classBinaryName().toJ();
    String qName = q(name);
    return methodLoader.provide(methodB.jar(), classBinaryName, NATIVE_METHOD_NAME)
        .validate(m -> validateSignature(m))
        .validate(m -> validateNativeResT(m, qName, methodB.type().res()))
        .validate(m -> validateNativeParamTs(m, qName, methodB.type().params()))
        .mapError(e -> loadingError(qName, classBinaryName, e));
  }

  private String validateSignature(Method method) {
    if (!isPublic(method)) {
      return "Providing method is not public.";
    } else if (!isStatic(method)) {
      return "Providing method is not static.";
    } else if (!hasContainerParam(method)) {
      return "Providing method first parameter is not of type "
          + NativeApi.class.getCanonicalName() + ".";
    } else {
      return null;
    }
  }

  private static boolean hasContainerParam(Method method) {
    Class<?>[] types = method.getParameterTypes();
    return types.length != 0 && (types[0] == NativeApi.class || types[0] == Container.class);
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

  private static String validateNativeParamTs(
      Method method, String qName, ImmutableList<TypeB> paramTBs) {
    Parameter[] nativeParams = method.getParameters();
    if (paramTBs.size() != nativeParams.length - 1) {
      return qName + " has " + paramTBs.size() + " parameter(s) but its native implementation has "
          + (nativeParams.length - 1) + " parameter(s).";
    }
    for (int i = 0; i < paramTBs.size(); i++) {
      var paramJ = nativeParams[i + 1];
      var paramTB = paramTBs.get(i);
      var paramTJ = paramJ.getType();
      var expectedParamTJ = paramTB.typeJ();
      if (!expectedParamTJ.equals(paramTJ)) {
        return qName + " parameter at index " + i + " has type " + paramTB.q()
            + " so its native implementation type must be " + expectedParamTJ.getCanonicalName()
            + " but it is " + paramTJ.getCanonicalName() + ".";
      }
    }
    return null;
  }

  private static String loadingError(String qName, String classBinaryName, String message) {
    return "Error loading native implementation for "
        + qName + " specified as `" + classBinaryName + "`: " + message;
  }
}
