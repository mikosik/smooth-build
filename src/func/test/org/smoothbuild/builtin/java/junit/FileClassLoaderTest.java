package org.smoothbuild.builtin.java.junit;

import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.testing.common.StreamTester.inputStreamToBytes;
import static org.smoothbuild.util.Classes.binaryPath;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;
import static org.testory.common.Matchers.same;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.smoothbuild.lang.value.SFile;
import org.smoothbuild.testing.db.objects.FakeObjectsDb;
import org.smoothbuild.util.Classes;

public class FileClassLoaderTest {
  private final FakeObjectsDb objectsDb = new FakeObjectsDb();
  private FileClassLoader fileClassLoader;
  private SFile file;
  private Class<MyClass> klass;

  @Test
  public void loads_class_from_binary() throws Exception {
    given(klass = FileClassLoaderTest.MyClass.class);
    given(file = createByteCodeFile(klass));
    given(fileClassLoader = new FileClassLoader(map(klass.getName(), file)));
    when(fileClassLoader.findClass(klass.getName()).getClassLoader());
    thenReturned(same(fileClassLoader));
  }

  private static Map<String, SFile> map(String name, SFile file) {
    HashMap<String, SFile> result = new HashMap<>();
    result.put(name, file);
    return result;
  }

  private SFile createByteCodeFile(Class<?> klass) throws IOException {
    byte[] byteCode = inputStreamToBytes(Classes.byteCodeAsInputStream(klass));
    return objectsDb.file(path(binaryPath(klass)), byteCode);
  }

  public static class MyClass {
    public static String myMethod() {
      return "myResult";
    }
  }
}
