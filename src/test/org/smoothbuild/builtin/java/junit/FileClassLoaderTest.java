package org.smoothbuild.builtin.java.junit;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.Map;

import org.junit.Test;
import org.smoothbuild.fs.base.Path;
import org.smoothbuild.testing.type.impl.TestFile;
import org.smoothbuild.type.api.File;

import com.google.common.collect.ImmutableMap;
import com.google.common.io.ByteStreams;

public class FileClassLoaderTest {

  @Test
  public void testClassLoading() throws Exception {
    // Do not use MyClass.class literal to avoid loading MyClass by current
    // ClassLoader.
    String klassBinaryName = FileClassLoaderTest.class.getName() + "$MyClass";
    String filePath = klassBinaryName.replace('.', '/') + ".class";

    TestFile file = new TestFile(Path.path("this/path/is/ignored/anyway"));
    InputStream classByteCode = this.getClass().getClassLoader().getResourceAsStream(filePath);

    OutputStream outputStream = file.openOutputStream();
    ByteStreams.copy(classByteCode, outputStream);
    outputStream.close();

    Map<String, File> binaryNameToFile = ImmutableMap.<String, File> of(klassBinaryName, file);
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
