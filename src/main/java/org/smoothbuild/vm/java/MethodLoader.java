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

  public synchronized Method load(String name, MethodB methodB) throws LoadingMethodExc {
    String qName = q(name);
    String classBinaryName = methodB.classBinaryName().toJ();
    Method method = loadMethod(qName, methodB, classBinaryName);
    assertMethodMatchesFuncRequirements(qName, methodB, method, classBinaryName);
    return method;
  }

  private Method loadMethod(String qName, MethodB methodB, String classBinaryName)
      throws LoadingMethodExc {
    return methodCache.computeIfAbsent(methodB,
        n -> findAndVerifyMethod(qName, methodB, classBinaryName));
  }

  private Method findAndVerifyMethod(String qName, MethodB methodB, String classBinaryName)
      throws LoadingMethodExc {
    Method method = findMethod(qName, methodB, classBinaryName);
    if (!isPublic(method)) {
      throw newLoadingExc(qName, classBinaryName, "Providing method is not public.");
    } else if (!isStatic(method)) {
      throw newLoadingExc(qName, classBinaryName, "Providing method is not static.");
    } else if (!hasContainerParam(method)) {
      throw newLoadingExc(qName, classBinaryName, "Providing method first parameter is not of type "
          + NativeApi.class.getCanonicalName() + ".");
    } else {
      return method;
    }
  }

  private Method findMethod(String qName, MethodB methodB, String classBinaryName)
      throws LoadingMethodExc {
    Class<?> clazz = findClass(qName, methodB, classBinaryName);
    return stream(clazz.getDeclaredMethods())
        .filter(m -> m.getName().equals(NATIVE_METHOD_NAME))
        .findFirst()
        .orElseThrow(() -> newLoadingExc(qName, classBinaryName, "Class '" +
            clazz.getCanonicalName() + "' does not have '" + NATIVE_METHOD_NAME + "' method."
        ));
  }

  private Class<?> findClass(String qName, MethodB methodB, String classBinaryName)
      throws LoadingMethodExc {
    FilePath originalJarFilePath = fileLoader.filePathOf(methodB.jar().hash());
    Path jarPath = jPathResolver.resolve(originalJarFilePath);
    try {
      return loadClass(jarPath, classBinaryName);
    } catch (ClassNotFoundException e) {
      throw newLoadingExc(qName, classBinaryName,
          "Class '" + classBinaryName + "' does not exist in jar '" + originalJarFilePath + "'.");
    } catch (FileNotFoundException e) {
      throw newLoadingExc(qName, classBinaryName, e.getMessage(), e);
    }
  }

  private static boolean hasContainerParam(Method method) {
    Class<?>[] types = method.getParameterTypes();
    return types.length != 0 && (types[0] == NativeApi.class || types[0] == Container.class);
  }

  private void assertMethodMatchesFuncRequirements(String qName,
      MethodB methodB, Method method, String classBinaryName) throws LoadingMethodExc {
    assertNativeResMatchesDeclared(
        qName, method, methodB.type().res(), classBinaryName);
    assertNativeParamTsMatchesFuncParamTs(qName, method, methodB, classBinaryName);
  }

  private static void assertNativeResMatchesDeclared(String qName, Method method,
      TypeB resTB, String classBinaryName) throws LoadingMethodExc {
    var methodResTJ = method.getReturnType();
    var resTJ = resTB.typeJ();
    if (!resTJ.equals(methodResTJ)) {
      throw newLoadingExc(qName, classBinaryName, qName + " declares type "
          + resTB.q() + " so its native implementation result type must be "
          + resTJ.getCanonicalName() + " but it is "
          + methodResTJ.getCanonicalName() + ".");
    }
  }

  private static void assertNativeParamTsMatchesFuncParamTs(String qName,
      Method method, MethodB methodB, String classBinaryName) throws LoadingMethodExc {
    Parameter[] nativeParams = method.getParameters();
    var paramTBs = methodB.type().params();
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

  private static LoadingMethodExc newLoadingExc(
      String qName, String classBinaryName, String message) {
    return newLoadingExc(qName, classBinaryName, message, null);
  }

  private static LoadingMethodExc newLoadingExc(String qName, String classBinaryName,
      String message, Exception e) {
    return new LoadingMethodExc("Error loading native implementation for "
        + qName + " specified as `" + classBinaryName + "`: " + message, e);
  }

  private static class LoadingMethodExc extends RuntimeException {
    public LoadingMethodExc(String message, Throwable e) {
      super(message, e);
    }
  }
}
