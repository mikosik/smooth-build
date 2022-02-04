package org.smoothbuild.vm.java;

import static com.google.common.truth.Truth.assertThat;
import static java.lang.ClassLoader.getPlatformClassLoader;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestingContext;

public class ClassLoaderProvTest extends TestingContext {
  @Test
  public void name() throws Exception {
    var jar = blobBJarWithJavaByteCode(MyClass.class);
    var classLoaderProv = new ClassLoaderProv(getPlatformClassLoader(), nativeApi());
    var classLoader = classLoaderProv.classLoaderForJar(jar);
    var clazz = classLoader.loadClass(MyClass.class.getName());
    assertThat(clazz)
        .isNotSameInstanceAs(MyClass.class);
    assertThat(clazz.getMethod("method").invoke(null))
        .isEqualTo("MyClass result");
  }

  public static class MyClass {
    public static String method() {
      return "MyClass result";
    }
  }
}
