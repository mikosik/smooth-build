package org.smoothbuild.virtualmachine.bytecode.load;

import static com.google.common.truth.Truth.assertThat;
import static java.lang.ClassLoader.getPlatformClassLoader;

import org.junit.jupiter.api.Test;
import org.smoothbuild.virtualmachine.dagger.VmTestContext;
import org.smoothbuild.virtualmachine.testing.func.nativ.ReturnAbc;

public class JarClassLoaderFactoryTest extends VmTestContext {
  @Test
  void provided_classloader_can_load_class_and_its_method() throws Exception {
    var jar = blobBJarWithJavaByteCode(MyClass.class);
    var classLoaderFactory =
        new JarClassLoaderFactory(provide().bytecodeFactory(), getPlatformClassLoader());
    var classLoaderTry = classLoaderFactory.classLoaderFor(jar);
    var clazz = classLoaderTry.ok().loadClass(MyClass.class.getName());
    assertThat(clazz).isNotSameInstanceAs(MyClass.class);
    assertThat(clazz.getMethod("method").invoke(null)).isEqualTo("MyClass result");
  }

  public static class MyClass {
    public static String method() {
      return "MyClass result";
    }
  }

  @Test
  void classloader_is_cached() throws Exception {
    var jar = blobBJarWithJavaByteCode(ReturnAbc.class);
    var classLoaderFactory =
        new JarClassLoaderFactory(provide().bytecodeFactory(), getPlatformClassLoader());
    var classLoader1 = classLoaderFactory.classLoaderFor(jar);
    var classLoader2 = classLoaderFactory.classLoaderFor(jar);
    assertThat(classLoader1).isSameInstanceAs(classLoader2);
  }
}
