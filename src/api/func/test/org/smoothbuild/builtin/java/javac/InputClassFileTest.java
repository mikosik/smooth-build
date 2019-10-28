package org.smoothbuild.builtin.java.javac;

import static okio.Okio.buffer;
import static okio.Okio.source;
import static org.hamcrest.Matchers.not;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.util.Okios.readAndClose;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import java.net.URI;

import org.junit.Test;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.object.base.Struct;
import org.smoothbuild.testing.TestingContext;

import okio.ByteString;

public class InputClassFileTest extends TestingContext {
  private final ByteString bytes = ByteString.encodeUtf8("abc");
  private InputClassFile inputClassFile;
  private final Path path = path("a/b/MyClass.class");
  private Struct file;

  @Test(expected = IllegalArgumentException.class)
  public void file_without_class_extension_is_forbidden() throws Exception {
    inputClassFile("abc");
  }

  @Test
  public void input_class_files_with_equal_paths_but_different_content_are_equal()
      throws Exception {
    when(new InputClassFile(file(path, ByteString.encodeUtf8("abc"))));
    thenReturned(new InputClassFile(file(path, ByteString.encodeUtf8("def"))));
  }

  @Test
  public void input_class_files_with_different_paths_are_not_equal() throws Exception {
    when(new InputClassFile(file(path("a/b/MyClass.class"), bytes)));
    thenReturned(not(new InputClassFile(file(path("a/b/OtherClass.class"), bytes))));
  }

  @Test
  public void class_in_default_package_has_empty_package() throws Exception {
    given(inputClassFile = new InputClassFile(file(path("Myclass.class"))));
    when(inputClassFile).aPackage();
    thenReturned("");
  }

  @Test
  public void aPackage_returns_class_package() throws Exception {
    given(inputClassFile = new InputClassFile(file(path("a/b/Myclass.class"))));
    when(inputClassFile).aPackage();
    thenReturned("a.b");
  }

  @Test
  public void aPackage_for_inner_class_returns_package_of_enclosing_class() throws Exception {
    given(inputClassFile = new InputClassFile(file(path("a/b/MyClass$Inner.class"))));
    when(inputClassFile).aPackage();
    thenReturned("a.b");
  }

  @Test
  public void binary_name_for_default_class_returns_class_name() throws Exception {
    given(inputClassFile = new InputClassFile(file(path("MyClass.class"))));
    when(inputClassFile).binaryName();
    thenReturned("MyClass");
  }

  @Test
  public void binary_name_for_class_returns_package_plus_class_name() throws Exception {
    given(inputClassFile = new InputClassFile(file(path("a/b/MyClass.class"))));
    when(inputClassFile).binaryName();
    thenReturned("a.b.MyClass");
  }

  @Test
  public void binary_name_for_inner_class_returns_package_plus_outer_class_plus_inner_class_name()
      throws Exception {
    given(inputClassFile = new InputClassFile(file(path("a/b/MyClass$Inner.class"))));
    when(inputClassFile).binaryName();
    thenReturned("a.b.MyClass$Inner");
  }

  @Test
  public void to_uri() throws Exception {
    given(inputClassFile = new InputClassFile(file(path("a/b/MyClass.class"))));
    when(inputClassFile).toUri();
    thenReturned(URI.create("jar:///:a/b/MyClass.class"));
  }

  @Test
  public void open_input_stream_returns_file_content() throws Exception {
    given(file = file(path, bytes));
    given(inputClassFile = new InputClassFile(file));
    when(() -> readAndClose(buffer(source(inputClassFile.openInputStream())),
        s -> s.readByteString()));
    thenReturned(bytes);
  }

  private InputClassFile inputClassFile(String path) {
    return new InputClassFile(file(path(path)));
  }
}
