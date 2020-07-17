package org.smoothbuild.slib.java.junit;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.util.reflect.Classes.binaryPath;

import java.io.IOException;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.smoothbuild.record.base.Tuple;
import org.smoothbuild.testing.TestingContext;
import org.smoothbuild.util.reflect.Classes;

public class FileClassLoaderTest extends TestingContext {
  @Test
  public void loads_class_from_binary() throws Exception {
    Class<MyClass> klass = MyClass.class;
    Tuple file = createByteCodeFile(klass);
    FileClassLoader fileClassLoader = new FileClassLoader(Map.of(klass.getName(), file));
    assertThat(fileClassLoader.findClass(klass.getName()).getClassLoader())
        .isSameInstanceAs(fileClassLoader);
  }

  private Tuple createByteCodeFile(Class<?> klass) throws IOException {
    return file(path(binaryPath(klass)), Classes.bytecode(klass));
  }

  public static class MyClass {
    public static String myMethod() {
      return "myResult";
    }
  }
}
