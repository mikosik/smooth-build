package org.smoothbuild.vm.java;

import static java.util.Arrays.stream;
import static org.smoothbuild.util.Strings.q;
import static org.smoothbuild.util.collect.Maps.computeIfAbsent;
import static org.smoothbuild.util.reflect.Methods.isPublic;
import static org.smoothbuild.util.reflect.Methods.isStatic;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.smoothbuild.bytecode.obj.val.BlobB;
import org.smoothbuild.bytecode.obj.val.MethodB;
import org.smoothbuild.bytecode.type.base.TypeB;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.vm.compute.Container;

/**
 * This class is thread-safe.
 */
@Singleton
public class MethodLoader {
  static final String NATIVE_METHOD_NAME = "func";
  private final HashMap<MethodB, Method> methodCache;
  private final HashMap<BlobB, ClassLoader> classLoaderCache;

  @Inject
  public MethodLoader() {
    this.methodCache = new HashMap<>();
    this.classLoaderCache = new HashMap<>();
  }

  public synchronized Method load(String name, MethodB methodB, ClassLoaderProv classLoaderProv)
      throws MethodLoaderExc {
    String qName = q(name);
    String classBinaryName = methodB.classBinaryName().toJ();
    Method method = loadMethod(qName, methodB, classBinaryName, classLoaderProv);
    assertMethodMatchesFuncRequirements(qName, methodB, method, classBinaryName);
    return method;
  }

  private Method loadMethod(
      String qName, MethodB methodB, String classBinaryName, ClassLoaderProv classLoaderProv)
      throws MethodLoaderExc {
    return computeIfAbsent(methodCache, methodB,
        n -> findAndVerifyMethod(qName, methodB, classBinaryName, classLoaderProv));
  }

  private Method findAndVerifyMethod(
      String qName, MethodB methodB, String classBinaryName, ClassLoaderProv classLoaderProv)
      throws MethodLoaderExc {
    var method = findMethod(qName, methodB, classBinaryName, classLoaderProv);
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

  private Method findMethod(
      String qName, MethodB methodB, String classBinaryName, ClassLoaderProv classLoaderProv)
      throws MethodLoaderExc {
    var clazz = findClass(qName, methodB, classBinaryName, classLoaderProv);
    return stream(clazz.getDeclaredMethods())
        .filter(m -> m.getName().equals(NATIVE_METHOD_NAME))
        .findFirst()
        .orElseThrow(() -> newLoadingExc(qName, classBinaryName, "Class '" +
            clazz.getCanonicalName() + "' does not have '" + NATIVE_METHOD_NAME + "' method."
        ));
  }

  private Class<?> findClass(
      String qName, MethodB methodB, String classBinaryName, ClassLoaderProv classLoaderProv)
      throws MethodLoaderExc {
    try {
      return findClassLoader(methodB, classLoaderProv)
          .loadClass(classBinaryName);
    } catch (ClassNotFoundException e) {
      throw newLoadingExc(qName, classBinaryName, "Class not found in jar.");
    } catch (FileNotFoundException | ClassLoaderProvExc e) {
      throw newLoadingExc(qName, classBinaryName, e.getMessage(), e);
    } catch (IOException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  private ClassLoader findClassLoader(MethodB methodB, ClassLoaderProv classLoaderProv)
      throws ClassLoaderProvExc, IOException {
    var jar = methodB.jar();
    var classLoader = classLoaderCache.get(jar);
    if (classLoader == null) {
      classLoader = classLoaderProv.classLoaderForJar(jar);
      classLoaderCache.put(jar, classLoader);
    }
    return classLoader;
  }

  private static boolean hasContainerParam(Method method) {
    Class<?>[] types = method.getParameterTypes();
    return types.length != 0 && (types[0] == NativeApi.class || types[0] == Container.class);
  }

  private void assertMethodMatchesFuncRequirements(String qName,
      MethodB methodB, Method method, String classBinaryName) throws MethodLoaderExc {
    assertNativeResMatchesDeclared(
        qName, method, methodB.type().res(), classBinaryName);
    assertNativeParamTsMatchesFuncParamTs(qName, method, methodB, classBinaryName);
  }

  private static void assertNativeResMatchesDeclared(String qName, Method method,
      TypeB resTB, String classBinaryName) throws MethodLoaderExc {
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
      Method method, MethodB methodB, String classBinaryName) throws MethodLoaderExc {
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

  private static MethodLoaderExc newLoadingExc(
      String qName, String classBinaryName, String message) {
    return newLoadingExc(qName, classBinaryName, message, null);
  }

  private static MethodLoaderExc newLoadingExc(String qName, String classBinaryName,
      String message, Exception e) {
    return new MethodLoaderExc("Error loading native implementation for "
        + qName + " specified as `" + classBinaryName + "`: " + message, e);
  }
}
