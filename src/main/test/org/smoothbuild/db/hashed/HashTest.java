package org.smoothbuild.db.hashed;

import static org.hamcrest.Matchers.not;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import java.io.EOFException;
import java.io.File;

import org.junit.Test;
import org.smoothbuild.io.fs.base.Path;

import com.google.common.hash.HashCode;
import com.google.common.io.Files;

import okio.Buffer;
import okio.ByteString;

public class HashTest {
  private final String string = "some string";
  private final String string2 = "some other string";
  private final ByteString bytes = ByteString.encodeUtf8("abc");
  private File file;
  private HashCode hash;
  private Buffer buffer;

  @Test
  public void read_from_empty_source_throws_eof_exception() {
    when(() -> Hash.read(new Buffer()));
    thenThrown(EOFException.class);
  }

  @Test
  public void read_from_source_having_less_bytes_than_needed_throws_eof_exception() {
    given(buffer = new Buffer());
    given(() -> buffer.write(new byte[Hash.size() - 1]));
    when(() -> Hash.read(buffer));
    thenThrown(EOFException.class);
  }

  @Test
  public void read_hash_from_source() {
    given(buffer = new Buffer());
    given(hash = Hash.string("abc"));
    given(() -> buffer.write(hash.asBytes()));
    when(() -> Hash.read(buffer));
    thenReturned(hash);
  }

  @Test
  public void hash_of_given_array_of_hashes_is_always_the_same() {
    when(Hash.hashes(HashCode.fromInt(1), HashCode.fromInt(2)));
    thenReturned(Hash.hashes(HashCode.fromInt(1), HashCode.fromInt(2)));
  }

  @Test
  public void hash_of_different_array_of_hashes_are_different() {
    when(Hash.hashes(HashCode.fromInt(1), HashCode.fromInt(2)));
    thenReturned(not(Hash.hashes(HashCode.fromInt(1), HashCode.fromInt(3))));
  }

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
    given(file = File.createTempFile("tmp", ".tmp"));
    Files.write(bytes.toByteArray(), file);
    when(Hash.file(file.toPath()));
    thenReturned(Hash.bytes(bytes.toByteArray()));
  }

  @Test
  public void toPath() {
    given(hash = HashCode.fromInt(0xAB));
    when(Hash.toPath(hash));
    thenReturned(Path.path("ab000000"));
  }
}
