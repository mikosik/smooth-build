package org.smoothbuild.db.hashed;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.common.AssertCall.assertCall;

import java.io.EOFException;
import java.io.File;

import org.junit.jupiter.api.Test;

import com.google.common.io.Files;

import okio.Buffer;
import okio.ByteString;

public class HashTest {
  private final String string = "some string";
  private final String string2 = "some other string";
  private final ByteString byteString = ByteString.of(new byte[] {1, 2, 3, 4});
  private File file;
  private Hash hash;
  private Buffer buffer;

  @Test
  public void read_from_empty_source_throws_eof_exception() {
    assertCall(() -> Hash.read(new Buffer()))
        .throwsException(EOFException.class);
  }

  @Test
  public void read_from_source_having_less_bytes_than_needed_throws_eof_exception() {
    buffer = new Buffer();
    buffer.write(new byte[Hash.hashesSize() - 1]);
    assertCall(() -> Hash.read(buffer))
        .throwsException(EOFException.class);
  }

  @Test
  public void read_hash_from_source() throws Exception {
    buffer = new Buffer();
    hash = Hash.of("abc");
    buffer.write(hash);
    assertThat(Hash.read(buffer))
        .isEqualTo(hash);
  }

  @Test
  public void hash_of_file_is_equal_to_hash_of_its_bytes() throws Exception {
    file = File.createTempFile("tmp", ".tmp");
    Files.write(byteString.toByteArray(), file);
    assertThat(Hash.of(file.toPath()))
        .isEqualTo(Hash.of(byteString));
  }

  @Test
  public void hash_of_given_array_of_hashes_is_always_the_same() {
    assertThat(Hash.of(Hash.of(1), Hash.of(2)))
        .isEqualTo(Hash.of(Hash.of(1), Hash.of(2)));
  }

  @Test
  public void hash_of_different_array_of_hashes_are_different() {
    assertThat(Hash.of(Hash.of(1), Hash.of(2)))
        .isNotEqualTo(Hash.of(Hash.of(1), Hash.of(3)));
  }

  @Test
  public void hash_of_given_string_is_always_the_same() {
    assertThat(Hash.of(string))
        .isEqualTo(Hash.of(string));
  }

  @Test
  public void hashes_of_different_strings_are_different() {
    assertThat(Hash.of(string))
        .isNotEqualTo(Hash.of(string2));
  }

  @Test
  public void hash_of_given_integer_is_always_the_same() {
    assertThat(Hash.of(33))
        .isEqualTo(Hash.of(33));
  }

  @Test
  public void hashes_of_different_integers_are_different() {
    assertThat(Hash.of(33))
        .isNotEqualTo(Hash.of(34));
  }

  @Test
  public void hash_of_given_bytestring_is_always_the_same() {
    assertThat(Hash.of(byteString))
        .isEqualTo(Hash.of(byteString));
  }

  @Test
  public void hashes_of_different_bytes_are_different() {
    assertThat(Hash.of(byteString))
        .isNotEqualTo(byteString.substring(1));
  }

  @Test
  public void decode_from_string() {
    assertThat(Hash.decode("010A"))
        .isEqualTo(new Hash(ByteString.of(new byte[] {1, 10})));
  }
}
