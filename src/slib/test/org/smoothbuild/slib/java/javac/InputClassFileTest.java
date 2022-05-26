package org.smoothbuild.slib.java.javac;

import static com.google.common.truth.Truth.assertThat;
import static okio.Okio.buffer;
import static okio.Okio.source;
import static org.smoothbuild.fs.base.PathS.path;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.io.Okios.readAndClose;

import java.net.URI;

import org.junit.jupiter.api.Test;
import org.smoothbuild.bytecode.obj.cnst.TupleB;
import org.smoothbuild.fs.base.PathS;
import org.smoothbuild.testing.TestingContext;

import okio.BufferedSource;
import okio.ByteString;

public class InputClassFileTest extends TestingContext {
  private final ByteString bytes = ByteString.encodeUtf8("abc");
  private final PathS path = path("a/b/MyClass.class");

  @Test
  public void file_without_class_extension_is_forbidden() {
    assertCall(() -> new InputClassFile(fileB(path("abc"))))
        .throwsException(IllegalArgumentException.class);
  }

  @Test
  public void input_class_files_with_equal_paths_but_different_content_are_equal() {
    assertThat(new InputClassFile(fileB(path, ByteString.encodeUtf8("abc"))))
        .isEqualTo(new InputClassFile(fileB(path, ByteString.encodeUtf8("def"))));
  }

  @Test
  public void input_class_files_with_different_paths_are_not_equal() {
    assertThat(new InputClassFile(fileB(path("a/b/MyClass.class"), bytes)))
        .isNotEqualTo(new InputClassFile(fileB(path("a/b/OtherClass.class"), bytes)));
  }

  @Test
  public void class_in_default_package_has_empty_package() {
    InputClassFile inputClassFile = new InputClassFile(fileB(path("Myclass.class")));
    assertThat(inputClassFile.aPackage())
        .isEqualTo("");
  }

  @Test
  public void aPackage_returns_class_package() {
    InputClassFile inputClassFile = new InputClassFile(fileB(path("a/b/Myclass.class")));
    assertThat(inputClassFile.aPackage())
        .isEqualTo("a.b");
  }

  @Test
  public void aPackage_for_inner_class_returns_package_of_enclosing_class() {
    InputClassFile inputClassFile = new InputClassFile(fileB(path("a/b/MyClass$Inner.class")));
    assertThat(inputClassFile.aPackage())
        .isEqualTo("a.b");
  }

  @Test
  public void binary_name_for_default_class_returns_class_name() {
    InputClassFile inputClassFile = new InputClassFile(fileB(path("MyClass.class")));
    assertThat(inputClassFile.binaryName())
        .isEqualTo("MyClass");
  }

  @Test
  public void binary_name_for_class_returns_package_plus_class_name() {
    InputClassFile inputClassFile = new InputClassFile(fileB(path("a/b/MyClass.class")));
    assertThat(inputClassFile.binaryName())
        .isEqualTo("a.b.MyClass");
  }

  @Test
  public void binary_name_for_inner_class_returns_package_plus_outer_class_plus_inner_class_name()
      {
    InputClassFile inputClassFile = new InputClassFile(fileB(path("a/b/MyClass$Inner.class")));
    assertThat(inputClassFile.binaryName())
        .isEqualTo("a.b.MyClass$Inner");
  }

  @Test
  public void to_uri() {
    InputClassFile inputClassFile = new InputClassFile(fileB(path("a/b/MyClass.class")));
    assertThat(inputClassFile.toUri())
        .isEqualTo(URI.create("jar:///:a/b/MyClass.class"));
  }

  @Test
  public void open_input_stream_returns_file_content() throws Exception {
    TupleB file = fileB(path, bytes);
    InputClassFile inputClassFile = new InputClassFile(file);
    BufferedSource buffer = buffer(source(inputClassFile.openInputStream()));
    ByteString byteString = readAndClose(buffer, BufferedSource::readByteString);
    assertThat(byteString)
        .isEqualTo(bytes);
  }
}
