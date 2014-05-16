package org.smoothbuild.io.util;

import static org.smoothbuild.util.Streams.inputStreamToByteArray;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import java.io.File;

import org.junit.Test;
import org.smoothbuild.db.hashed.Hash;

import com.google.common.io.Files;

public class JarFileTest {
  private File file;
  private byte[] bytes;
  private JarFile jarFile;

  @Test
  public void jar_file_hash_is_equal_to_hash_of_file_bytes() throws Exception {
    given(file = File.createTempFile("temp", ".tmp"));
    given(bytes = new byte[] { 1, 2, 3, 4 });
    Files.write(bytes, file);
    given(jarFile = JarFile.jarFile(file.toPath()));
    when(jarFile).hash();
    thenReturned(Hash.bytes(bytes));
  }

  @Test
  public void path_passed_during_construction_is_returned_by_path() throws Exception {
    given(file = File.createTempFile("temp", ".tmp"));
    given(bytes = new byte[] { 1, 2, 3, 4 });
    given(jarFile = JarFile.jarFile(file.toPath()));
    when(jarFile).path();
    thenReturned(file.toPath());
  }

  @Test
  public void output_stream_reads_data_from_file() throws Exception {
    given(file = File.createTempFile("temp", ".tmp"));
    given(bytes = new byte[] { 1, 2, 3, 4 });
    given(jarFile = JarFile.jarFile(file.toPath()));
    Files.write(bytes, file);
    when(inputStreamToByteArray(jarFile.openInputStream()));
    thenReturned(bytes);
  }
}
