package org.smoothbuild.builtin.java.junit;

import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.util.reflect.Classes.binaryPath;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;
import static org.testory.common.Matchers.same;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.smoothbuild.lang.object.base.Struct;
import org.smoothbuild.testing.TestingContext;
import org.smoothbuild.util.reflect.Classes;

public class FileClassLoaderTest extends TestingContext {
  private FileClassLoader fileClassLoader;
  private Struct file;
  private Class<MyClass> klass;

  @Test
  public void loads_class_from_binary() throws Exception {
    given(klass = FileClassLoaderTest.MyClass.class);
    given(file = createByteCodeFile(klass));
    given(fileClassLoader = new FileClassLoader(map(klass.getName(), file)));
    when(fileClassLoader.findClass(klass.getName()).getClassLoader());
    thenReturned(same(fileClassLoader));
  }

  private static Map<String, Struct> map(String name, Struct file) {
    HashMap<String, Struct> result = new HashMap<>();
    result.put(name, file);
    return result;
  }

  private Struct createByteCodeFile(Class<?> klass) throws IOException {
    return file(path(binaryPath(klass)), Classes.bytecode(klass));
  }

  public static class MyClass {
    public static String myMethod() {
      return "myResult";
    }
  }
}
