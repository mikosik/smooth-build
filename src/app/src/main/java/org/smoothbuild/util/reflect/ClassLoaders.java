package org.smoothbuild.util.reflect;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Function;

public class ClassLoaders {
  public static ClassLoader mapClassLoader(Function<String, InputStream> filesMap) {
    return mapClassLoader(ClassLoaders.class.getClassLoader(), filesMap);
  }

  public static ClassLoader mapClassLoader(
      ClassLoader parentClassLoader, Function<String, InputStream> filesMap) {
    try {
      var uri = new URI("x-buffer", "ssp", "/", "");
      var url = URL.of(uri, urlStreamHandler(filesMap));
      return new URLClassLoader(new URL[] {url}, parentClassLoader);
    } catch (MalformedURLException | URISyntaxException e) {
      // shouldn't happen
      throw new RuntimeException(e);
    }
  }

  private static URLStreamHandler urlStreamHandler(Function<String, InputStream> inputStreams) {
    return new URLStreamHandler() {
      @Override
      protected URLConnection openConnection(URL url) throws IOException {
        // remove leading "/" character
        String path = url.getFile().substring(1);
        var inputStream = inputStreams.apply(path);
        if (inputStream == null) {
          throw new FileNotFoundException(path);
        }
        return new URLConnection(url) {
          @Override
          public void connect() {
          }

          @Override
          public InputStream getInputStream() {
            return inputStream;
          }
        };
      }
    };
  }

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
      return new URI(urlString).toURL();
    } catch (MalformedURLException | URISyntaxException e) {
      throw new IllegalArgumentException("Cannot convert '" + urlString + "' to URL.");
    }
  }
}
