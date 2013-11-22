package org.smoothbuild.lang.builtin.java.junit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.testing.common.StreamTester.inputStreamToBytes;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Map;

import org.junit.Test;
import org.smoothbuild.lang.type.SFile;
import org.smoothbuild.testing.lang.type.FakeFile;

import com.google.common.collect.ImmutableMap;

public class FileClassLoaderTest {

  @Test
  public void testClassLoading() throws Exception {
    // Do not use MyClass.class literal to avoid loading MyClass by current
    // ClassLoader.
    String klassBinaryName = FileClassLoaderTest.class.getName() + "$MyClass";
    String filePath = klassBinaryName.replace('.', '/') + ".class";

    InputStream classByteCode = this.getClass().getClassLoader().getResourceAsStream(filePath);

    FakeFile file = new FakeFile(path("this/path/is/ignored/anyway"),
        inputStreamToBytes(classByteCode));

    Map<String, SFile> binaryNameToFile = ImmutableMap.<String, SFile> of(klassBinaryName, file);
    FileClassLoader fileClassLoader = new FileClassLoader(binaryNameToFile);

    Class<?> klass = fileClassLoader.findClass(klassBinaryName);
    assertThat(klass.getClassLoader()).isSameAs(fileClassLoader);

    Method method = klass.getMethod("myMethod");
    Object result = method.invoke(null);

    assertThat(result).isEqualTo("myResult");
  }

  public static class MyClass {
    public static String myMethod() {
      return "myResult";
    }
  }
}
