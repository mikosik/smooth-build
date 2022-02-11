package org.smoothbuild.vm.job.algorithm;

import static org.smoothbuild.util.Strings.q;
import static org.smoothbuild.util.reflect.Methods.isPublic;
import static org.smoothbuild.util.reflect.Methods.isStatic;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import javax.inject.Inject;

import org.smoothbuild.bytecode.obj.val.MethodB;
import org.smoothbuild.bytecode.type.base.TypeB;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.vm.compute.Container;
import org.smoothbuild.vm.java.MethodProv;
import org.smoothbuild.vm.java.MethodProvExc;

import com.google.common.collect.ImmutableList;

public class MethodLoader {
  private static final String NATIVE_METHOD_NAME = "func";
  private final MethodProv methodProv;

  @Inject
  public MethodLoader(MethodProv methodProv) {
    this.methodProv = methodProv;
  }

  public Method load(String name, MethodB methodB) throws MethodLoaderExc {
    String classBinaryName = methodB.classBinaryName().toJ();
    try {
      var method = methodProv.provide(methodB.jar(), classBinaryName, NATIVE_METHOD_NAME);
      validate(methodB, q(name), classBinaryName, method);
      return method;
    } catch (MethodProvExc e) {
      throw newLoadingExc(q(name), classBinaryName, e.getMessage(), e);
    }
  }

  private void validate(MethodB methodB, String qName, String classBinaryName, Method method)
      throws MethodLoaderExc {
    if (!isPublic(method)) {
      throw newLoadingExc(qName, classBinaryName, "Providing method is not public.");
    } else if (!isStatic(method)) {
      throw newLoadingExc(qName, classBinaryName, "Providing method is not static.");
    } else if (!hasContainerParam(method)) {
      throw newLoadingExc(qName, classBinaryName, "Providing method first parameter is not of type "
          + NativeApi.class.getCanonicalName() + ".");
    }
    assertMethodMatchesFuncRequirements(qName, methodB, method, classBinaryName);
  }

  private static boolean hasContainerParam(Method method) {
    Class<?>[] types = method.getParameterTypes();
    return types.length != 0 && (types[0] == NativeApi.class || types[0] == Container.class);
  }

  private void assertMethodMatchesFuncRequirements(String qName, MethodB methodB, Method method,
      String classBinaryName) throws MethodLoaderExc {
    assertNativeResMatchesDeclared(qName, method, classBinaryName, methodB.type().res());
    assertNativeParamTsMatchesFuncParamTs(qName, method, classBinaryName, methodB.type().params());
  }

  private static void assertNativeResMatchesDeclared(String qName, Method method,
      String classBinaryName, TypeB resTB) throws MethodLoaderExc {
    var methodResTJ = method.getReturnType();
    var resTJ = resTB.typeJ();
    if (!resTJ.equals(methodResTJ)) {
      throw newLoadingExc(qName, classBinaryName, qName + " declares type "
          + resTB.q() + " so its native implementation result type must be "
          + resTJ.getCanonicalName() + " but it is "
          + methodResTJ.getCanonicalName() + ".");
    }
  }

  private static void assertNativeParamTsMatchesFuncParamTs(String qName, Method method,
      String classBinaryName, ImmutableList<TypeB> paramTBs) throws MethodLoaderExc {
    Parameter[] nativeParams = method.getParameters();
    if (paramTBs.size() != nativeParams.length - 1) {
      throw newLoadingExc(qName, classBinaryName, qName + " has "
          + paramTBs.size() + " parameter(s) but its native implementation has "
          + (nativeParams.length - 1) + " parameter(s).");
    }
    for (int i = 0; i < paramTBs.size(); i++) {
      var paramJ = nativeParams[i + 1];
      var paramTB = paramTBs.get(i);
      var paramTJ = paramJ.getType();
      var expectedParamTJ = paramTB.typeJ();
      if (!expectedParamTJ.equals(paramTJ)) {
        throw newLoadingExc(qName, classBinaryName, qName
            + " parameter at index " + i + " has type " + paramTB.q()
            + " so its native implementation type must be " + expectedParamTJ.getCanonicalName()
            + " but it is " + paramTJ.getCanonicalName() + ".");
      }
    }
  }

  private static MethodLoaderExc newLoadingExc(
      String qName, String classBinaryName, String message) {
    return newLoadingExc(qName, classBinaryName, message, null);
  }

  private static MethodLoaderExc newLoadingExc(String qName, String classBinaryName,
      String message, Exception e) {
    return new MethodLoaderExc("Error loading native implementation for "
        + qName + " specified as `" + classBinaryName + "`: " + message, e);
  }

  public static class MethodLoaderExc extends Exception {
    public MethodLoaderExc(String message, Throwable e) {
      super(message, e);
    }
  }
}
