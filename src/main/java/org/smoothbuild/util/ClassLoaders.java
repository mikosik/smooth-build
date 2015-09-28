package org.smoothbuild.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;

public class ClassLoaders {
  public static URLClassLoader jarClassLoader(ClassLoader parent, Path jarPath) {
    Path absolutePath = jarPath.toAbsolutePath();
    if (!Files.exists(absolutePath)) {
      throw new RuntimeException("Cannot find '" + absolutePath + "' file");
    }

    return new URLClassLoader(new URL[] { toUrl(absolutePath) }, parent);
  }

  private static URL toUrl(Path funcsJarPath) {
    String urlString = "file://" + funcsJarPath.toString();
    try {
      return new URL(urlString);
    } catch (MalformedURLException e) {
      throw new RuntimeException("Cannot convert '" + urlString + "' to URL.");
    }
  }
}
