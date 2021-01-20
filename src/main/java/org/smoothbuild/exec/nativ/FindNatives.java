package org.smoothbuild.exec.nativ;

import static okio.Okio.buffer;
import static okio.Okio.source;
import static org.smoothbuild.io.util.JarFile.jarFile;
import static org.smoothbuild.lang.base.define.Names.isLegalName;
import static org.smoothbuild.util.reflect.ClassLoaders.jarClassLoader;
import static org.smoothbuild.util.reflect.Classes.CLASS_FILE_EXTENSION;
import static org.smoothbuild.util.reflect.Classes.binaryPathToBinaryName;
import static org.smoothbuild.util.reflect.Methods.canonicalName;
import static org.smoothbuild.util.reflect.Methods.isPublic;
import static org.smoothbuild.util.reflect.Methods.isStatic;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.smoothbuild.exec.compute.Container;
import org.smoothbuild.io.util.JarFile;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.plugin.NativeImplementation;

public class FindNatives {
  public static Map<String, Native> findNatives(Path jarPath) throws LoadingNativeJarException {
    if (!jarPath.toFile().exists()) {
      throw new LoadingNativeJarException("Cannot find '" + jarPath + "'.");
    }
    try {
      return find(jarFile(jarPath));
    } catch (IOException e) {
      throw new LoadingNativeJarException("Cannot read '" + jarPath + "'.", e);
    }
  }

  private static Map<String, Native> find(JarFile jarFile) throws IOException, LoadingNativeJarException {
    Map<String, Native> result = new HashMap<>();
    JarInputStream jarInputStream = newJarInputStream(jarFile.path());
    JarEntry entry;
    while ((entry = jarInputStream.getNextJarEntry()) != null) {
      String fileName = entry.getName();
      if (fileName.endsWith(CLASS_FILE_EXTENSION)) {
        String binaryName = binaryPathToBinaryName(fileName);
        Class<?> clazz = load(jarFile.path(), binaryName);
        for (Method method : clazz.getDeclaredMethods()) {
          if (method.isAnnotationPresent(NativeImplementation.class)) {
            NativeImplementation annotation = method.getAnnotation(NativeImplementation.class);
            String name = annotation.value();
            if (isLegalName(name)) {
              if (result.containsKey(name)) {
              throw error(jarFile, method, "Implementation for the same name is also provided by "
                  + canonicalName(result.get(name).method()) + ".");
              } else if (!isPublic(method)) {
                throw error(jarFile, method, "Providing method must be public.");
              } else if (!isStatic(method)) {
                throw error(jarFile, method, "Providing method must be static.");
              } else if (!hasContainerParameter(method)) {
                throw error(jarFile, method, "Providing method should have first parameter of type "
                    + NativeApi.class.getCanonicalName() + ".");
              } else {
                result.put(name,
                    new Native(name, method, annotation.cacheable(), jarFile));
              }
            } else {
              throw error(jarFile, method, "Name '" + method.getName() + "' is illegal.");
            }
          }
        }
      }
    }
    return result;
  }

  private static boolean hasContainerParameter(Method method) {
    Class<?>[] types = method.getParameterTypes();
    return types.length != 0 && (types[0] == NativeApi.class || types[0] == Container.class);
  }

  private static LoadingNativeJarException error(JarFile jarFile, Method method, String message) {
    return new LoadingNativeJarException("Invalid native implementation in " + jarFile.path()
        + " provided by " + canonicalName(method) + ": " + message);
  }

  private static ClassLoader classLoader(Path jar) {
    ClassLoader parentClassLoader = FindNatives.class.getClassLoader();
    return jarClassLoader(parentClassLoader, jar);
  }

  private static Class<?> load(Path jarPath, String binaryName) throws LoadingNativeJarException {
    try {
      return classLoader(jarPath).loadClass(binaryName);
    } catch (ClassNotFoundException e) {
      throw new LoadingNativeJarException(
          "Cannot load java bytecode of '" + binaryName + "' from '" + jarPath + "'.");
    }
  }

  private static JarInputStream newJarInputStream(Path jarPath) throws IOException {
    return new JarInputStream(buffer(source(jarPath)).inputStream());
  }
}
