package org.smoothbuild.util.reflect;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;

public class ClassLoaders {
  public static URLClassLoader jarClassLoader(ClassLoader parent, Path jarPath)
      throws FileNotFoundException {
    Path absolutePath = jarPath.toAbsolutePath();
    if (!Files.exists(absolutePath)) {
      throw new FileNotFoundException("Cannot find '" + absolutePath + "' file.");
    }

    return new URLClassLoader(new URL[] {toUrl(absolutePath)}, parent);
  }

  private static URL toUrl(Path path) {
    String urlString = "file://" + path.toString();
    try {
      return new URL(urlString);
    } catch (MalformedURLException e) {
      throw new IllegalArgumentException("Cannot convert '" + urlString + "' to URL.");
    }
  }
}
