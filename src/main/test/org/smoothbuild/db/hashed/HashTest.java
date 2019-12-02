package org.smoothbuild.db.hashed;

import static org.hamcrest.Matchers.not;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import java.io.EOFException;
import java.io.File;

import org.junit.Test;

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
    when(() -> Hash.read(new Buffer()));
    thenThrown(EOFException.class);
  }

  @Test
  public void read_from_source_having_less_bytes_than_needed_throws_eof_exception() {
    given(buffer = new Buffer());
    given(() -> buffer.write(new byte[Hash.hashesSize() - 1]));
    when(() -> Hash.read(buffer));
    thenThrown(EOFException.class);
  }

  @Test
  public void read_hash_from_source() {
    given(buffer = new Buffer());
    given(hash = Hash.of("abc"));
    given(() -> buffer.write(hash));
    when(() -> Hash.read(buffer));
    thenReturned(hash);
  }

  @Test
  public void hash_of_file_is_equal_to_hash_of_its_bytes() throws Exception {
    given(file = File.createTempFile("tmp", ".tmp"));
    Files.write(byteString.toByteArray(), file);
    when(Hash.of(file.toPath()));
    thenReturned(Hash.of(byteString));
  }

  @Test
  public void hash_of_given_array_of_hashes_is_always_the_same() {
    when(Hash.of(Hash.of(1), Hash.of(2)));
    thenReturned(Hash.of(Hash.of(1), Hash.of(2)));
  }

  @Test
  public void hash_of_different_array_of_hashes_are_different() {
    when(Hash.of(Hash.of(1), Hash.of(2)));
    thenReturned(not(Hash.of(Hash.of(1), Hash.of(3))));
  }

  @Test
  public void hash_of_given_string_is_always_the_same() {
    when(Hash.of(string));
    thenReturned(Hash.of(string));
  }

  @Test
  public void hashes_of_different_strings_are_different() {
    when(Hash.of(string));
    thenReturned(not(Hash.of(string2)));
  }

  @Test
  public void hash_of_given_integer_is_always_the_same() {
    when(Hash.of(33));
    thenReturned(Hash.of(33));
  }

  @Test
  public void hashes_of_different_integers_are_different() {
    when(Hash.of(33));
    thenReturned(not(Hash.of(34)));
  }

  @Test
  public void hash_of_given_bytestring_is_always_the_same() {
    when(Hash.of(byteString));
    thenReturned(Hash.of(byteString));
  }

  @Test
  public void hashes_of_different_bytes_are_different() {
    when(Hash.of(byteString));
    thenReturned(not(byteString.substring(1)));
  }

  @Test
  public void decode_from_string() {
    when(() -> Hash.decode("010A"));
    thenReturned(new Hash(ByteString.of(new byte[] {1, 10})));
  }
}
