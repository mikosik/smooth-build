package org.smoothbuild.db.hashed;

import static org.hamcrest.Matchers.not;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import java.io.ByteArrayInputStream;
import java.io.File;

import org.junit.Test;
import org.smoothbuild.io.fs.base.Path;

import com.google.common.hash.HashCode;
import com.google.common.io.Files;

public class HashTest {
  private final String string = "some string";
  private final String string2 = "some other string";
  private byte[] bytes;
  private ByteArrayInputStream inputStream;
  private File file;
  private HashCode hash;

  @Test
  public void hash_of_given_string_is_always_the_same() {
    when(Hash.string(string));
    thenReturned(Hash.string(string));
  }

  @Test
  public void hashes_of_different_strings_are_different() {
    when(Hash.string(string));
    thenReturned(not(Hash.string(string2)));
  }

  @Test
  public void hash_of_given_integer_is_always_the_same() {
    when(Hash.integer(33));
    thenReturned(Hash.integer(33));
  }

  @Test
  public void hashes_of_different_integers_are_different() {
    when(Hash.integer(33));
    thenReturned(not(Hash.integer(34)));
  }

  @Test
  public void hash_of_given_bytes_is_always_the_same() {
    when(Hash.bytes(string.getBytes()));
    thenReturned(Hash.bytes(string.getBytes()));
  }

  @Test
  public void hashes_of_different_bytes_are_different() {
    when(Hash.bytes(string.getBytes()));
    thenReturned(not(Hash.bytes(string2.getBytes())));
  }

  @Test
  public void hash_of_file_is_equal_to_hash_of_its_bytes() throws Exception {
    given(bytes = new byte[] { 1, 2, 3, 4, 5 });
    given(file = File.createTempFile("tmp", ".tmp"));
    Files.write(bytes, file);
    when(Hash.file(file.toPath()));
    thenReturned(Hash.bytes(bytes));
  }

  @Test
  public void hash_of_input_stream_is_equal_to_hash_of_its_bytes() throws Exception {
    given(bytes = new byte[] { 1, 2, 3, 4, 5 });
    given(inputStream = new ByteArrayInputStream(bytes));
    when(Hash.stream(inputStream));
    thenReturned(Hash.bytes(bytes));
  }

  @Test
  public void hash_of_empty_input_stream_is_equal_to_hash_of_zero_bytes() throws Exception {
    given(bytes = new byte[] {});
    given(inputStream = new ByteArrayInputStream(bytes));
    when(Hash.stream(inputStream));
    thenReturned(Hash.bytes(bytes));
  }

  @Test
  public void toPath() {
    given(hash = HashCode.fromInt(0xAB));
    when(Hash.toPath(hash));
    thenReturned(Path.path("ab000000"));
  }
}
