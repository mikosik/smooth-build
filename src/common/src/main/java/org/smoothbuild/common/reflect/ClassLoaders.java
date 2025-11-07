package org.smoothbuild.common.reflect;

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
import org.jspecify.annotations.Nullable;
import org.smoothbuild.common.function.Function1;

public class ClassLoaders {
  public static ClassLoader mappingClassLoader(
      Function1<String, InputStream, IOException> inputStreams) {
    return mappingClassLoader(ClassLoaders.class.getClassLoader(), inputStreams);
  }

  /**
   * Creates mapping class loader.
   * @param parentClassLoader if null is passed then bootstrap classloader should be used as parent
   */
  public static ClassLoader mappingClassLoader(
      @Nullable ClassLoader parentClassLoader,
      Function1<String, InputStream, IOException> inputStreams) {
    try {
      var uri = new URI("x-buffer", "ssp", "/", "");
      var url = URL.of(uri, urlStreamHandler(inputStreams));
      return new URLClassLoader(new URL[] {url}, parentClassLoader);
    } catch (MalformedURLException | URISyntaxException e) {
      // shouldn't happen
      throw new RuntimeException(e);
    }
  }

  private static URLStreamHandler urlStreamHandler(
      Function1<String, InputStream, IOException> inputStreams) {
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
          public void connect() {}

          @Override
          public InputStream getInputStream() {
            return inputStream;
          }
        };
      }
    };
  }
}
