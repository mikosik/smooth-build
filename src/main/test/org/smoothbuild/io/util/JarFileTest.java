package org.smoothbuild.io.util;

import static com.google.common.truth.Truth.assertThat;

import java.io.File;

import org.junit.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.io.fs.base.Path;

import com.google.common.io.Files;
import com.google.common.truth.Truth;

import okio.ByteString;

public class JarFileTest {
  private static final ByteString bytes = ByteString.encodeUtf8("abc");
  private File file;
  private JarFile jarFile;

  @Test
  public void jar_file_hash_is_equal_to_hash_of_file_bytes() throws Exception {
    file = File.createTempFile("temp", ".tmp");
    Files.write(bytes.toByteArray(), file);
    jarFile = JarFile.jarFile(file.toPath());
    assertThat(jarFile.hash())
        .isEqualTo(Hash.of(bytes));
  }

  @Test
  public void path_passed_during_construction_is_returned_by_path() throws Exception {
    file = File.createTempFile("temp", ".tmp");
    jarFile = JarFile.jarFile(file.toPath());
    Truth.<Path>assertThat(jarFile.path())
        .isEqualTo(file.toPath());
  }
}
