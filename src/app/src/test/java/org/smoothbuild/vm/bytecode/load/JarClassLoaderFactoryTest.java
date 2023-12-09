package org.smoothbuild.vm.bytecode.load;

import static com.google.common.truth.Truth.assertThat;
import static java.lang.ClassLoader.getPlatformClassLoader;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestContext;
import org.smoothbuild.testing.func.nativ.ReturnAbc;

public class JarClassLoaderFactoryTest extends TestContext {
  @Test
  public void provided_classloader_can_load_class_and_its_method() throws Exception {
    var jar = blobBJarWithJavaByteCode(MyClass.class);
    var classLoaderFactory = new JarClassLoaderFactory(bytecodeF(), getPlatformClassLoader());
    var classLoaderTry = classLoaderFactory.classLoaderFor(jar);
    var clazz = classLoaderTry.right().loadClass(MyClass.class.getName());
    assertThat(clazz).isNotSameInstanceAs(MyClass.class);
    assertThat(clazz.getMethod("method").invoke(null)).isEqualTo("MyClass result");
  }

  public static class MyClass {
    public static String method() {
      return "MyClass result";
    }
  }

  @Test
  public void classloader_is_cached() throws Exception {
    var jar = blobBJarWithJavaByteCode(ReturnAbc.class);
    var classLoaderFactory = new JarClassLoaderFactory(bytecodeF(), getPlatformClassLoader());
    var classLoader1 = classLoaderFactory.classLoaderFor(jar);
    var classLoader2 = classLoaderFactory.classLoaderFor(jar);
    assertThat(classLoader1).isSameInstanceAs(classLoader2);
  }
}
