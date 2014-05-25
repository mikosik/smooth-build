package org.smoothbuild.io.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;

import javax.inject.Singleton;

import org.smoothbuild.db.hashed.Hash;

import com.google.common.hash.HashCode;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public class ReleaseJarModule extends AbstractModule {

  @Override
  protected void configure() {}

  @Provides
  @SmoothJar
  @Singleton
  public HashCode provideSmoothJarHash() {
    URLClassLoader classLoader = (URLClassLoader) Thread.currentThread().getContextClassLoader();
    URL[] urls = classLoader.getURLs();
    if (urls.length != 1) {
      throw new RuntimeException(
          "Too many jars in classpath of URLClassLoader. Expected exactly one.");
    }
    File smoothJarFile = new File(urls[0].getPath());
    return calculateHash(smoothJarFile);
  }

  private static HashCode calculateHash(File smoothJarFile) {
    try {
      return Hash.file(smoothJarFile);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
