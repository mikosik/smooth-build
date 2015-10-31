package org.smoothbuild.builtin.java.javac;

import static org.hamcrest.Matchers.not;
import static org.smoothbuild.db.values.ValuesDb.valuesDb;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.testing.db.values.ValueCreators.file;
import static org.smoothbuild.util.Streams.inputStreamToString;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import java.net.URI;

import org.junit.Test;
import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.value.SFile;

public class InputClassFileTest {
  private final ValuesDb valuesDb = valuesDb();
  private InputClassFile inputClassFile;
  private final Path path = path("a/b/MyClass.class");
  private String content;
  private SFile file;

  @Test(expected = IllegalArgumentException.class)
  public void file_without_class_extension_is_forbidden() throws Exception {
    inputClassFile("abc");
  }

  @Test
  public void input_class_files_with_equal_paths_are_equal() throws Exception {
    when(new InputClassFile(file(valuesDb, path, "content")));
    thenReturned(new InputClassFile(file(valuesDb, path, "other content")));
  }

  @Test
  public void input_class_files_with_different_paths_are_not_equal() throws Exception {
    when(new InputClassFile(file(valuesDb, path("a/b/MyClass.class"), "content")));
    thenReturned(not(new InputClassFile(file(valuesDb, path("a/b/OtherClass.class"), "content"))));
  }

  @Test
  public void class_in_default_package_has_empty_package() throws Exception {
    given(inputClassFile = new InputClassFile(file(valuesDb, path("Myclass.class"))));
    when(inputClassFile).aPackage();
    thenReturned("");
  }

  @Test
  public void aPackage_returns_class_package() throws Exception {
    given(inputClassFile = new InputClassFile(file(valuesDb, path("a/b/Myclass.class"))));
    when(inputClassFile).aPackage();
    thenReturned("a.b");
  }

  @Test
  public void aPackage_for_inner_class_returns_package_of_enclosing_class() throws Exception {
    given(inputClassFile = new InputClassFile(file(valuesDb, path("a/b/MyClass$Inner.class"))));
    when(inputClassFile).aPackage();
    thenReturned("a.b");
  }

  @Test
  public void binary_name_for_default_class_returns_class_name() throws Exception {
    given(inputClassFile = new InputClassFile(file(valuesDb, path("MyClass.class"))));
    when(inputClassFile).binaryName();
    thenReturned("MyClass");
  }

  @Test
  public void binary_name_for_class_returns_package_plus_class_name() throws Exception {
    given(inputClassFile = new InputClassFile(file(valuesDb, path("a/b/MyClass.class"))));
    when(inputClassFile).binaryName();
    thenReturned("a.b.MyClass");
  }

  @Test
  public void binary_name_for_inner_class_returns_package_plus_outer_class_plus_inner_class_name()
      throws Exception {
    given(inputClassFile = new InputClassFile(file(valuesDb, path("a/b/MyClass$Inner.class"))));
    when(inputClassFile).binaryName();
    thenReturned("a.b.MyClass$Inner");
  }

  @Test
  public void to_uri() throws Exception {
    given(inputClassFile = new InputClassFile(file(valuesDb, path("a/b/MyClass.class"))));
    when(inputClassFile).toUri();
    thenReturned(URI.create("jar:///:a/b/MyClass.class"));
  }

  @Test
  public void open_input_stream_returns_file_content() throws Exception {
    given(content = "some content");
    given(file = file(valuesDb, path, content));
    given(inputClassFile = new InputClassFile(file));
    when(inputStreamToString(inputClassFile.openInputStream()));
    thenReturned(content);
  }

  private InputClassFile inputClassFile(String path) {
    return new InputClassFile(file(valuesDb, path(path)));
  }
}
