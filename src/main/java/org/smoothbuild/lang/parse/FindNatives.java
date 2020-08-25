package org.smoothbuild.lang.parse;

import static okio.Okio.buffer;
import static okio.Okio.source;
import static org.smoothbuild.io.util.JarFile.jarFile;
import static org.smoothbuild.lang.base.Name.isLegalName;
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

import org.smoothbuild.cli.console.Log;
import org.smoothbuild.cli.console.Logger;
import org.smoothbuild.exec.compute.Container;
import org.smoothbuild.io.util.JarFile;
import org.smoothbuild.lang.base.Native;
import org.smoothbuild.nativ.Natives;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.plugin.SmoothFunction;

public class FindNatives {
  public static Natives findNatives(Path jarPath, Logger logger) {
    if (!jarPath.toFile().exists()) {
      return empty();
    }
    try {
      return find(jarFile(jarPath), logger);
    } catch (IOException e) {
      logger.error("Cannot read native implementation file '" + jarPath + "'.");
      return null;
    }
  }

  private static Natives empty() {
    return new Natives(new HashMap<>());
  }

  private static Natives find(JarFile jarFile, Logger logger) throws IOException {
    Map<String, Native> result = new HashMap<>();
    JarInputStream jarInputStream = newJarInputStream(jarFile.path());
    JarEntry entry;
    while ((entry = jarInputStream.getNextJarEntry()) != null) {
      String fileName = entry.getName();
      if (fileName.endsWith(CLASS_FILE_EXTENSION)) {
        String binaryName = binaryPathToBinaryName(fileName);
        Class<?> clazz = load(jarFile.path(), binaryName);
        if (clazz == null) {
          logger.error("Cannot load java bytecode of '" + binaryName + "' from '" + jarFile.path()
              + "'.");
        } else {
          for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(SmoothFunction.class)) {
              SmoothFunction smoothFunctionAnnotation = method.getAnnotation(SmoothFunction.class);
              String name = smoothFunctionAnnotation.value();
              if (isLegalName(name)) {
                if (result.containsKey(name)) {
                  logger.log(error(jarFile, method,
                      "Function with the same name is also provided by "
                          + canonicalName(result.get(name).method()) + "."));
                } else if (!isPublic(method)) {
                  logger.log(error(jarFile, method, "Providing method must be public."));
                } else if (!isStatic(method)) {
                  logger.log(error(jarFile, method, "Providing method must be static."));
                } else if (!hasContainerParameter(method)) {
                  logger.log(error(jarFile, method,
                      "Providing method should have first parameter of type "
                          + NativeApi.class.getCanonicalName() + "."));
                } else {
                  result.put(name,
                      new Native(method, smoothFunctionAnnotation.cacheable(), jarFile));
                }
              } else {
                logger.log(error(jarFile, method, "Name '" + method.getName() + "' is illegal."));
              }
            }
          }
        }
      }
    }
    return new Natives(result);
  }

  private static boolean hasContainerParameter(Method method) {
    Class<?>[] types = method.getParameterTypes();
    return types.length != 0 && (types[0] == NativeApi.class || types[0] == Container.class);
  }

  private static Log error(JarFile jarFile, Method method, String message) {
    return Log.error("Invalid native implementation in " + jarFile.path() + " provided by "
        + canonicalName(method) + ": " + message);
  }

  private static ClassLoader classLoader(Path jar) {
    ClassLoader parentClassLoader = FindNatives.class.getClassLoader();
    return jarClassLoader(parentClassLoader, jar);
  }

  private static Class<?> load(Path jarPath, String binaryName) {
    try {
      return classLoader(jarPath).loadClass(binaryName);
    } catch (ClassNotFoundException e) {
      return null;
    }
  }

  private static JarInputStream newJarInputStream(Path jarPath) throws IOException {
    return new JarInputStream(buffer(source(jarPath)).inputStream());
  }
}
