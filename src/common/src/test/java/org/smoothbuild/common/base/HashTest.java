package org.smoothbuild.common.base;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.commontesting.AssertCall.assertCall;

import com.google.common.io.Files;
import com.google.common.testing.EqualsTester;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import okio.Buffer;
import okio.ByteString;
import org.junit.jupiter.api.Test;

public class HashTest {
  @Test
  void read_from_empty_source_throws_eof_exception() {
    assertCall(() -> Hash.read(new Buffer())).throwsException(EOFException.class);
  }

  @Test
  void read_from_source_having_less_bytes_than_needed_throws_eof_exception() {
    Buffer buffer = new Buffer();
    buffer.write(new byte[Hash.lengthInBytes() - 1]);
    assertCall(() -> Hash.read(buffer)).throwsException(EOFException.class);
  }

  @Test
  void read_hash_from_source() throws Exception {
    Buffer buffer = new Buffer();
    Hash hash = Hash.of("abc");
    buffer.write(hash.toByteString());
    assertThat(Hash.read(buffer)).isEqualTo(hash);
  }

  @Test
  void hash_of_file_is_equal_to_hash_of_its_bytes() throws Exception {
    File file = File.createTempFile("tmp", ".tmp");
    Files.write(someByteString().toByteArray(), file);
    assertThat(Hash.of(file.toPath())).isEqualTo(Hash.of(someByteString()));
  }

  @Test
  void hash_of_source_is_equal_to_hash_of_its_bytes() throws IOException {
    var source = new Buffer().write(someByteString());
    assertThat(Hash.of(source)).isEqualTo(Hash.of(someByteString()));
  }

  @Test
  void hash_of_given_array_of_hashes_is_always_the_same() {
    assertThat(Hash.of(list(Hash.of(1), Hash.of(2))))
        .isEqualTo(Hash.of(list(Hash.of(1), Hash.of(2))));
  }

  @Test
  void hash_of_different_array_of_hashes_are_different() {
    assertThat(Hash.of(list(Hash.of(1), Hash.of(2))))
        .isNotEqualTo(Hash.of(list(Hash.of(1), Hash.of(3))));
  }

  @Test
  void hash_of_given_string_is_always_the_same() {
    assertThat(Hash.of("some string")).isEqualTo(Hash.of("some string"));
  }

  @Test
  void hashes_of_different_strings_are_different() {
    String string2 = "some other string";
    assertThat(Hash.of("some string")).isNotEqualTo(Hash.of(string2));
  }

  @Test
  void hash_of_given_integer_is_always_the_same() {
    assertThat(Hash.of(33)).isEqualTo(Hash.of(33));
  }

  @Test
  void hashes_of_different_integers_are_different() {
    assertThat(Hash.of(33)).isNotEqualTo(Hash.of(34));
  }

  @Test
  void hash_of_given_bytestring_is_always_the_same() {
    assertThat(Hash.of(someByteString())).isEqualTo(Hash.of(someByteString()));
  }

  @Test
  void hashes_of_different_bytes_are_different() {
    assertThat(Hash.of(someByteString())).isNotEqualTo(someByteString().substring(1));
  }

  @Test
  void decode_from_string() {
    assertThat(Hash.decode("010A")).isEqualTo(new Hash(ByteString.of(new byte[] {1, 10})));
  }

  @Test
  void equal_and_hashCode() {
    byte b1 = 1;
    byte b2 = 2;
    byte b3 = 3;
    new EqualsTester()
        .addEqualityGroup(newHash(b1), newHash(b1))
        .addEqualityGroup(newHash(b2), newHash(b2))
        .addEqualityGroup(newHash(b3), newHash(b3))
        .addEqualityGroup(newHash(b1, b2), newHash(b1, b2))
        .testEquals();
  }

  private Hash newHash(byte... bytes) {
    return new Hash(ByteString.of(bytes));
  }

  private static ByteString someByteString() {
    return ByteString.of(new byte[] {1, 2, 3, 4});
  }
}
