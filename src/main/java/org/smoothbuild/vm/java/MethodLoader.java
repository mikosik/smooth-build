package org.smoothbuild.vm.java;

import static java.util.Arrays.stream;
import static org.smoothbuild.util.Strings.q;
import static org.smoothbuild.util.reflect.Classes.loadClass;
import static org.smoothbuild.util.reflect.Methods.isPublic;
import static org.smoothbuild.util.reflect.Methods.isStatic;

import java.io.FileNotFoundException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.nio.file.Path;
import java.util.HashMap;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.smoothbuild.bytecode.obj.val.MethodB;
import org.smoothbuild.bytecode.type.base.TypeB;
import org.smoothbuild.io.fs.space.FilePath;
import org.smoothbuild.io.fs.space.JPathResolver;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.vm.compute.Container;

/**
 * This class is thread-safe.
 */
@Singleton
public class MethodLoader {
  private static final String NATIVE_METHOD_NAME = "func";
  private final JPathResolver jPathResolver;
  private final FileLoader fileLoader;
  private final HashMap<MethodB, Method> methodCache;

  @Inject
  public MethodLoader(JPathResolver jPathResolver, FileLoader fileLoader) {
    this.jPathResolver = jPathResolver;
    this.fileLoader = fileLoader;
    this.methodCache = new HashMap<>();
  }

  public synchronized Method load(String extendedName, MethodB methodB) throws LoadingMethodExc {
    String quotedName = q(extendedName);
    String classBinaryName = methodB.classBinaryName().toJ();
    Method method = loadMethod(quotedName, methodB, classBinaryName);
    assertMethodMatchesFuncRequirements(quotedName, methodB, method, classBinaryName);
    return method;
  }

  private Method loadMethod(String extendedName, MethodB methodB, String classBinaryName)
      throws LoadingMethodExc {
    return methodCache.computeIfAbsent(methodB,
        n -> findMethod(extendedName, methodB, classBinaryName));
  }

  private Method findMethod(String extendedName, MethodB methodB, String classBinaryName)
      throws LoadingMethodExc {
    Method method = findClassMethod(extendedName, methodB, classBinaryName);
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

  private Method findClassMethod(String extendedName, MethodB methodB, String classBinaryName)
      throws LoadingMethodExc {
    Class<?> clazz = findClass(extendedName, methodB, classBinaryName);
    return stream(clazz.getDeclaredMethods())
        .filter(m -> m.getName().equals(NATIVE_METHOD_NAME))
        .findFirst()
        .orElseThrow(() -> newLoadingException(extendedName, classBinaryName, "Class '" +
            clazz.getCanonicalName() + "' does not have '" + NATIVE_METHOD_NAME + "' method."
        ));
  }

  private Class<?> findClass(String extendedName, MethodB methodB, String classBinaryName)
      throws LoadingMethodExc {
    FilePath originalJarFilePath = fileLoader.filePathOf(methodB.jar().hash());
    Path jarPath = jPathResolver.resolve(originalJarFilePath);
    try {
      return loadClass(jarPath, classBinaryName);
    } catch (ClassNotFoundException e) {
      throw newLoadingException(extendedName, classBinaryName,
          "Class '" + classBinaryName + "' does not exist in jar '" + originalJarFilePath + "'.");
    } catch (FileNotFoundException e) {
      throw newLoadingException(extendedName, classBinaryName, e.getMessage(), e);
    }
  }

  private static boolean hasContainerParam(Method method) {
    Class<?>[] types = method.getParameterTypes();
    return types.length != 0 && (types[0] == NativeApi.class || types[0] == Container.class);
  }

  private void assertMethodMatchesFuncRequirements(String extendedName,
      MethodB methodB, Method method, String classBinaryName) throws LoadingMethodExc {
    assertNativeResMatchesDeclared(
        extendedName, method, methodB.type().res(), classBinaryName);
    assertNativeParamTsMatchesFuncParamTs(extendedName, method, methodB, classBinaryName);
  }

  private static void assertNativeResMatchesDeclared(String extendedName, Method method,
      TypeB resTH, String classBinaryName) throws LoadingMethodExc {
    var methodResTJ = method.getReturnType();
    var resTJ = resTH.typeJ();
    if (!resTJ.equals(methodResTJ)) {
      throw newLoadingException(extendedName, classBinaryName, extendedName + " declares type "
          + resTH.q() + " so its native implementation result type must be "
          + resTJ.getCanonicalName() + " but it is "
          + methodResTJ.getCanonicalName() + ".");
    }
  }

  private static void assertNativeParamTsMatchesFuncParamTs(String extendedName,
      Method method, MethodB methodB, String classBinaryName) throws LoadingMethodExc {
    Parameter[] nativeParams = method.getParameters();
    var paramTHs = methodB.type().params();
    if (paramTHs.size() != nativeParams.length - 1) {
      throw newLoadingException(extendedName, classBinaryName, extendedName + " has "
          + paramTHs.size() + " parameter(s) but its native implementation has "
          + (nativeParams.length - 1) + " parameter(s).");
    }
    for (int i = 0; i < paramTHs.size(); i++) {
      var paramJ = nativeParams[i + 1];
      var paramTH = paramTHs.get(i);
      var paramTJ = paramJ.getType();
      var expectedParamTJ = paramTH.typeJ();
      if (!expectedParamTJ.equals(paramTJ)) {
        throw newLoadingException(extendedName, classBinaryName, extendedName
            + " parameter at index " + i + " has type " + paramTH.q()
            + " so its native implementation type must be " + expectedParamTJ.getCanonicalName()
            + " but it is " + paramTJ.getCanonicalName() + ".");
      }
    }
  }

  private static LoadingMethodExc newLoadingException(
      String extendedName, String classBinaryName, String message) {
    return newLoadingException(extendedName, classBinaryName, message, null);
  }

  private static LoadingMethodExc newLoadingException(String extendedName,
      String classBinaryName, String message, Exception e) {
    return new LoadingMethodExc("Error loading native implementation for "
        + extendedName + " specified as `" + classBinaryName + "`: " + message, e);
  }

  private static class LoadingMethodExc extends RuntimeException {
    public LoadingMethodExc(String message, Throwable e) {
      super(message, e);
    }
  }
}
