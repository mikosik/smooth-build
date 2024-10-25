package org.smoothbuild.stdlib.java.javac;

import static com.google.common.truth.Truth.assertThat;
import static okio.Okio.buffer;
import static okio.Okio.source;
import static org.smoothbuild.common.bucket.base.Path.path;
import static org.smoothbuild.commontesting.AssertCall.assertCall;

import java.net.URI;
import okio.ByteString;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.bucket.base.Path;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTuple;
import org.smoothbuild.virtualmachine.testing.BytecodeTestContext;

public class InputClassFileTest extends BytecodeTestContext {
  private final ByteString bytes = ByteString.encodeUtf8("abc");
  private final Path path = path("a/b/MyClass.class");

  @Test
  void file_without_class_extension_is_forbidden() {
    assertCall(() -> new InputClassFile(bFile(path("abc"))))
        .throwsException(IllegalArgumentException.class);
  }

  @Test
  void input_class_files_with_equal_paths_but_different_content_are_equal() throws Exception {
    assertThat(new InputClassFile(bFile(path, ByteString.encodeUtf8("abc"))))
        .isEqualTo(new InputClassFile(bFile(path, ByteString.encodeUtf8("def"))));
  }

  @Test
  void input_class_files_with_different_paths_are_not_equal() throws Exception {
    assertThat(new InputClassFile(bFile(path("a/b/MyClass.class"), bytes)))
        .isNotEqualTo(new InputClassFile(bFile(path("a/b/OtherClass.class"), bytes)));
  }

  @Test
  void class_in_default_package_has_empty_package() throws Exception {
    InputClassFile inputClassFile = new InputClassFile(bFile(path("Myclass.class")));
    assertThat(inputClassFile.aPackage()).isEqualTo("");
  }

  @Test
  void aPackage_returns_class_package() throws Exception {
    InputClassFile inputClassFile = new InputClassFile(bFile(path("a/b/Myclass.class")));
    assertThat(inputClassFile.aPackage()).isEqualTo("a.b");
  }

  @Test
  void aPackage_for_inner_class_returns_package_of_enclosing_class() throws BytecodeException {
    InputClassFile inputClassFile = new InputClassFile(bFile(path("a/b/MyClass$Inner.class")));
    assertThat(inputClassFile.aPackage()).isEqualTo("a.b");
  }

  @Test
  void binary_name_for_default_class_returns_class_name() throws Exception {
    InputClassFile inputClassFile = new InputClassFile(bFile(path("MyClass.class")));
    assertThat(inputClassFile.binaryName()).isEqualTo("MyClass");
  }

  @Test
  void binary_name_for_class_returns_package_plus_class_name() throws Exception {
    InputClassFile inputClassFile = new InputClassFile(bFile(path("a/b/MyClass.class")));
    assertThat(inputClassFile.binaryName()).isEqualTo("a.b.MyClass");
  }

  @Test
  void binary_name_for_inner_class_returns_package_plus_outer_class_plus_inner_class_name()
      throws BytecodeException {
    InputClassFile inputClassFile = new InputClassFile(bFile(path("a/b/MyClass$Inner.class")));
    assertThat(inputClassFile.binaryName()).isEqualTo("a.b.MyClass$Inner");
  }

  @Test
  void to_uri() throws Exception {
    InputClassFile inputClassFile = new InputClassFile(bFile(path("a/b/MyClass.class")));
    assertThat(inputClassFile.toUri()).isEqualTo(URI.create("jar:///:a/b/MyClass.class"));
  }

  @Test
  void open_input_stream_returns_file_content() throws Exception {
    BTuple file = bFile(path, bytes);
    InputClassFile inputClassFile = new InputClassFile(file);
    try (var source = buffer(source(inputClassFile.openInputStream()))) {
      assertThat(source.readByteString()).isEqualTo(bytes);
    }
  }
}
