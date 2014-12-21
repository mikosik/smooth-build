package org.smoothbuild.builtin.java.javac;

import static java.util.Arrays.asList;
import static org.smoothbuild.builtin.java.javac.PackagedJavaFileObjects.packagedJavaFileObjects;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.testing.common.JarTester.jar;
import static org.smoothbuild.util.Streams.inputStreamToString;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import java.util.List;

import javax.tools.JavaFileObject;

import org.junit.Test;
import org.smoothbuild.builtin.java.javac.err.DuplicateClassFileError;
import org.smoothbuild.lang.base.Blob;
import org.smoothbuild.lang.base.SFile;
import org.smoothbuild.testing.task.exec.FakeNativeApi;
import org.testory.Closure;

import com.google.common.collect.Multimap;

public class PackagedJavaFileObjectsTest {
  private final FakeNativeApi nativeApi = new FakeNativeApi();
  private SFile file1;
  private SFile file2;
  private Blob jar;
  private Multimap<String, JavaFileObject> objects;
  private JavaFileObject fileObject;

  @Test
  public void files_from_library_jars_are_accessible_as_java_objects() throws Exception {
    given(file1 = nativeApi.file(path("my/package/MyKlass.class")));
    given(file2 = nativeApi.file(path("my/package/MyKlass2.class")));
    given(jar = jar(file1, file2));
    given(objects = packagedJavaFileObjects(nativeApi, asList(jar)));
    given(fileObject = objects.get("my.package").iterator().next());

    when(inputStreamToString(fileObject.openInputStream()));
    thenReturned("my/package/MyKlass2.class");

    when(fileObject).getName();
    thenReturned("/:my/package/MyKlass2.class");
  }

  @Test
  public void duplicateClassFileException() throws Exception {
    given(file1 = nativeApi.file(path("my/package/MyKlass.class")));
    given(jar = jar(file1));
    when(javaFileObjects(asList(jar, jar)));
    thenThrown(DuplicateClassFileError.class);
  }

  private Closure javaFileObjects(final List<Blob> libraryJars) {
    return new Closure() {
      @Override
      public Object invoke() throws Throwable {
        return packagedJavaFileObjects(nativeApi, libraryJars);
      }
    };
  }
}
