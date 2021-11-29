package org.smoothbuild.exec.java;

import static java.util.Arrays.stream;
import static org.smoothbuild.util.Strings.q;
import static org.smoothbuild.util.reflect.Classes.loadClass;
import static org.smoothbuild.util.reflect.Methods.isPublic;
import static org.smoothbuild.util.reflect.Methods.isStatic;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.nio.file.Path;
import java.util.HashMap;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.smoothbuild.db.object.obj.val.NatFuncH;
import org.smoothbuild.db.object.type.base.TypeH;
import org.smoothbuild.exec.compute.Container;
import org.smoothbuild.io.fs.space.FilePath;
import org.smoothbuild.io.fs.space.JPathResolver;
import org.smoothbuild.plugin.NativeApi;

/**
 * This class is thread-safe.
 */
@Singleton
public class MethodLoader {
  private static final String NATIVE_METHOD_NAME = "func";
  private final JPathResolver jPathResolver;
  private final FileLoader fileLoader;
  private final HashMap<NatFuncH, Method> methodCache;

  @Inject
  public MethodLoader(JPathResolver jPathResolver, FileLoader fileLoader) {
    this.jPathResolver = jPathResolver;
    this.fileLoader = fileLoader;
    this.methodCache = new HashMap<>();
  }

  public synchronized Method load(String extendedName, NatFuncH natFuncH)
      throws LoadingMethodException {
    String quotedName = q(extendedName);
    String classBinaryName = natFuncH.classBinaryName().jValue();
    Method method = loadMethod(quotedName, natFuncH, classBinaryName);
    assertMethodMatchesFuncRequirements(quotedName, natFuncH, method, classBinaryName);
    return method;
  }

  private Method loadMethod(String extendedName, NatFuncH funcH,
      String classBinaryName) throws LoadingMethodException {
    return methodCache.computeIfAbsent(funcH,
        n -> findMethod(extendedName, funcH, classBinaryName));
  }

  private Method findMethod(String extendedName, NatFuncH funcH,
      String classBinaryName) throws LoadingMethodException {
    Method method = findClassMethod(extendedName, funcH, classBinaryName);
    if (!isPublic(method)) {
      throw newLoadingException(extendedName, classBinaryName, "Providing method is not public.");
    } else if (!isStatic(method)) {
      throw newLoadingException(extendedName, classBinaryName, "Providing method is not static.");
    } else if (!hasContainerParam(method)) {
      throw newLoadingException(extendedName, classBinaryName,
          "Providing method first parameter is not of type " + NativeApi.class.getCanonicalName()
              + ".");
    } else {
      return method;
    }
  }

  private Method findClassMethod(String extendedName, NatFuncH funcH,
      String classBinaryName) throws LoadingMethodException {
    Class<?> clazz = findClass(extendedName, funcH, classBinaryName);
    return stream(clazz.getDeclaredMethods())
        .filter(m -> m.getName().equals(NATIVE_METHOD_NAME))
        .findFirst()
        .orElseThrow(() -> newLoadingException(extendedName, classBinaryName, "Class '" +
            clazz.getCanonicalName() + "' does not have '" + NATIVE_METHOD_NAME + "' method."
        ));
  }

  private Class<?> findClass(String extendedName, NatFuncH funcH,
      String classBinaryName) throws LoadingMethodException {
    FilePath originalJarFile = fileLoader.filePathOf(funcH.jarFile().hash());
    Path jarPath = jPathResolver.resolve(originalJarFile);
    try {
      return loadClass(jarPath, classBinaryName);
    } catch (ClassNotFoundException e) {
      throw newLoadingException(extendedName, classBinaryName,
          "Class '" + classBinaryName + "' does not exist in jar '" + originalJarFile + "'."
      );
    }
  }

  private static boolean hasContainerParam(Method method) {
    Class<?>[] types = method.getParameterTypes();
    return types.length != 0 && (types[0] == NativeApi.class || types[0] == Container.class);
  }

  private void assertMethodMatchesFuncRequirements(String extendedName,
      NatFuncH func, Method method, String classBinaryName) throws LoadingMethodException {
    assertNativeResultMatchesDeclared(
        extendedName, method, func.spec().result(), classBinaryName);
    assertNativeParamTypesMatchesFuncParams(extendedName, method, func, classBinaryName);
  }

  private static void assertNativeResultMatchesDeclared(String extendedName, Method method,
      TypeH resultType, String classBinaryName) throws LoadingMethodException {
    var methodResultTypeJ = method.getReturnType();
    var resultTypeJ = resultType.typeJ();
    if (!resultTypeJ.equals(methodResultTypeJ)) {
      throw newLoadingException(extendedName, classBinaryName, extendedName + " declares type "
          + resultType.q() + " so its native implementation result type must be "
          + resultTypeJ.getCanonicalName() + " but it is "
          + methodResultTypeJ.getCanonicalName() + ".");
    }
  }

  private static void assertNativeParamTypesMatchesFuncParams(String extendedName,
      Method method, NatFuncH func, String classBinaryName) throws LoadingMethodException {
    Parameter[] nativeParams = method.getParameters();
    var params = func.spec().params();
    if (params.size() != nativeParams.length - 1) {
      throw newLoadingException(extendedName, classBinaryName, extendedName + " has "
          + params.size() + " parameter(s) but its native implementation has "
          + (nativeParams.length - 1) + " parameter(s).");
    }
    for (int i = 0; i < params.size(); i++) {
      var paramJ = nativeParams[i + 1];
      var typeH = params.get(i);
      var paramTypeJ = paramJ.getType();
      var expectedParamTypeJ = typeH.typeJ();
      if (!expectedParamTypeJ.equals(paramTypeJ)) {
        throw newLoadingException(extendedName, classBinaryName, extendedName
            + " parameter at index " + i + " has type " + typeH.q()
            + " so its native implementation type must be " + expectedParamTypeJ.getCanonicalName()
            + " but it is " + paramTypeJ.getCanonicalName() + ".");
      }
    }
  }

  private static LoadingMethodException newLoadingException(
      String extendedName, String classBinaryName, String message) {
    return newLoadingException(extendedName, classBinaryName, message, null);
  }

  private static LoadingMethodException newLoadingException(String extendedName,
      String classBinaryName, String message, Exception e) {
    return new LoadingMethodException("Error loading native implementation for "
        + extendedName + " specified as `" + classBinaryName + "`: " + message, e);
  }

  public static class LoadingMethodException extends RuntimeException {
    public LoadingMethodException(String message, Throwable e) {
      super(message, e);
    }
  }
}
