package org.smoothbuild.lang.module;

import static org.smoothbuild.lang.function.nativ.NativeFunctionFactory.createNativeFunctions;
import static org.smoothbuild.util.Classes.CLASS_FILE_EXTENSION;
import static org.smoothbuild.util.Classes.binaryPathToBinaryName;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.function.nativ.NativeFunction;
import org.smoothbuild.lang.function.nativ.err.NativeImplementationException;
import org.smoothbuild.util.ClassLoaders;

import com.google.common.hash.HashCode;

public class NativeModuleFactory {
  public static Module createNativeModule(File jar) throws NativeImplementationException {
    try {
      return createNativeModuleImpl(jar, Hash.file(jar));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static Module createNativeModuleImpl(File jar, HashCode jarHash) throws IOException,
      NativeImplementationException {
    ModuleBuilder builder = new ModuleBuilder();
    ClassLoader classLoader = classLoader(jar);
    try (JarInputStream jarInputStream = newJarInputStream(jar)) {
      JarEntry entry = null;
      while ((entry = jarInputStream.getNextJarEntry()) != null) {
        String fileName = entry.getName();
        if (fileName.endsWith(CLASS_FILE_EXTENSION)) {
          Class<?> clazz = load(classLoader, binaryPathToBinaryName(fileName));
          for (NativeFunction<?> function : createNativeFunctions(jarHash, clazz)) {
            builder.addFunction(function);
          }
        }
      }
    }
    return builder.build();
  }

  private static ClassLoader classLoader(File jar) {
    ClassLoader parentClassLoader = NativeModuleFactory.class.getClassLoader();
    return ClassLoaders.jarClassLoader(parentClassLoader, jar.toPath());
  }

  private static Class<?> load(ClassLoader classLoader, String binaryName) {
    try {
      return classLoader.loadClass(binaryName);
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  private static JarInputStream newJarInputStream(File jar) throws IOException {
    return new JarInputStream(new BufferedInputStream(new FileInputStream(jar)));
  }
}
