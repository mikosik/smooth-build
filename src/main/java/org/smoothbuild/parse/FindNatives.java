package org.smoothbuild.parse;

import static org.smoothbuild.io.util.JarFile.jarFile;
import static org.smoothbuild.lang.function.base.Name.isLegalName;
import static org.smoothbuild.util.Maybe.maybe;
import static org.smoothbuild.util.Maybe.value;
import static org.smoothbuild.util.Paths.openBufferedInputStream;
import static org.smoothbuild.util.reflect.ClassLoaders.jarClassLoader;
import static org.smoothbuild.util.reflect.Classes.CLASS_FILE_EXTENSION;
import static org.smoothbuild.util.reflect.Classes.binaryPathToBinaryName;
import static org.smoothbuild.util.reflect.Methods.canonicalName;
import static org.smoothbuild.util.reflect.Methods.isPublic;
import static org.smoothbuild.util.reflect.Methods.isStatic;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.smoothbuild.io.util.JarFile;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.function.nativ.Native;
import org.smoothbuild.lang.plugin.Container;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.task.exec.ContainerImpl;
import org.smoothbuild.util.Maybe;

public class FindNatives {
  public static Maybe<Map<Name, Native>> findNatives(Path jarPath) {
    if (!jarPath.toFile().exists()) {
      return value(new HashMap<>());
    }
    try {
      return find(jarFile(jarPath));
    } catch (FileNotFoundException e) {
      return value(new HashMap<>());
    } catch (IOException e) {
      return Maybe.error("Cannot read native implementation file '" + jarPath + "'.");
    }
  }

  private static Maybe<Map<Name, Native>> find(JarFile jarFile) throws IOException {
    List<String> errors = new ArrayList<>();
    Map<Name, Native> result = new HashMap<>();
    JarInputStream jarInputStream = newJarInputStream(jarFile.path());
    JarEntry entry;
    while ((entry = jarInputStream.getNextJarEntry()) != null) {
      String fileName = entry.getName();
      if (fileName.endsWith(CLASS_FILE_EXTENSION)) {
        String binaryName = binaryPathToBinaryName(fileName);
        Class<?> clazz = load(jarFile.path(), binaryName);
        if (clazz == null) {
          errors.add("Cannot load java bytecode of '" + binaryName + "' from '" + jarFile.path()
              + "'.");
        } else {
          for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(SmoothFunction.class)) {
              if (isLegalName(method.getName())) {
                Name name = new Name(method.getName());
                if (result.containsKey(name)) {
                  errors.add(error(jarFile, method,
                      "Function with the same name is also provided by "
                          + canonicalName(result.get(name).method()) + "."));
                } else if (!isPublic(method)) {
                  errors.add(error(jarFile, method, "Providing method must be public."));
                } else if (!isStatic(method)) {
                  errors.add(error(jarFile, method, "Providing method must be static."));
                } else if (!hasContainerParameter(method)) {
                  errors.add(error(jarFile, method,
                      "Providing method should have first parameter of type "
                          + Container.class.getCanonicalName() + "."));
                } else {
                  result.put(name, new Native(method, jarFile));
                }
              } else {
                errors.add(error(jarFile, method, "Name '" + method.getName() + "' is illegal."));
              }
            }
          }
        }
      }
    }
    return maybe(result, errors);
  }

  private static boolean hasContainerParameter(Method method) {
    Class<?>[] types = method.getParameterTypes();
    return types.length != 0 && (types[0] == Container.class || types[0] == ContainerImpl.class);
  }

  private static String error(JarFile jarFile, Method method, String message) {
    return "Invalid native function implementation in " + jarFile.path() + " provided by "
        + canonicalName(method) + ": " + message;
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
    return new JarInputStream(openBufferedInputStream(jarPath));
  }
}
