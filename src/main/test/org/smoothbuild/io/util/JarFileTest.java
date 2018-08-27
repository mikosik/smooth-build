package org.smoothbuild.io.util;

import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import java.io.File;

import org.junit.Test;
import org.smoothbuild.db.hashed.Hash;

import com.google.common.io.Files;

import okio.ByteString;

public class JarFileTest {
  private static final ByteString bytes = ByteString.encodeUtf8("abc");
  private File file;
  private JarFile jarFile;

  @Test
  public void jar_file_hash_is_equal_to_hash_of_file_bytes() throws Exception {
    given(file = File.createTempFile("temp", ".tmp"));
    Files.write(bytes.toByteArray(), file);
    given(jarFile = JarFile.jarFile(file.toPath()));
    when(jarFile).hash();
    thenReturned(Hash.bytes(bytes.toByteArray()));
  }

  @Test
  public void path_passed_during_construction_is_returned_by_path() throws Exception {
    given(file = File.createTempFile("temp", ".tmp"));
    given(jarFile = JarFile.jarFile(file.toPath()));
    when(jarFile).path();
    thenReturned(file.toPath());
  }
}
