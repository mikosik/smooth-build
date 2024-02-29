package org.smoothbuild.vm.bytecode.hashed;

import static com.google.common.truth.Truth.assertThat;
import static java.lang.Byte.MAX_VALUE;
import static java.lang.Byte.MIN_VALUE;
import static java.lang.String.format;
import static okio.ByteString.encodeUtf8;
import static okio.Okio.buffer;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.testing.StringCreators.illegalString;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.vm.bytecode.hashed.HashedDb.TEMP_DIR_PATH;
import static org.smoothbuild.vm.bytecode.hashed.HashedDb.dbPathTo;

import java.io.IOException;
import java.math.BigInteger;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import okio.Buffer;
import okio.ByteString;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.smoothbuild.common.Hash;
import org.smoothbuild.testing.TestContext;
import org.smoothbuild.vm.bytecode.hashed.exc.CorruptedHashedDbException;
import org.smoothbuild.vm.bytecode.hashed.exc.DecodeHashChainException;
import org.smoothbuild.vm.bytecode.hashed.exc.DecodeStringException;
import org.smoothbuild.vm.bytecode.hashed.exc.HashedDbException;
import org.smoothbuild.vm.bytecode.hashed.exc.NoSuchDataException;

public class HashedDbTest extends TestContext {
  private static final ByteString BYTE_STRING_1 = ByteString.encodeUtf8("aaa");
  private static final ByteString BYTE_STRING_2 = ByteString.encodeUtf8("bbb");

  @Test
  public void not_contains_not_written_data() throws CorruptedHashedDbException {
    assertThat(hashedDb().contains(Hash.of(33))).isFalse();
  }

  @Test
  public void contains_written_data() throws Exception {
    var hash = hashedDb().writeData(bufferedSink -> bufferedSink.write(BYTE_STRING_1));
    assertThat(hashedDb().contains(hash)).isTrue();
  }

  @Test
  public void reading_not_written_value_fails() {
    var hash = Hash.of("abc");
    assertCall(() -> hashedDb().source(hash)).throwsException(new NoSuchDataException(hash));
  }

  @Test
  public void written_data_can_be_read_back() throws Exception {
    var hash = hashedDb().writeData(bufferedSink -> bufferedSink.write(encodeUtf8("abc")));
    try (var source = hashedDb().source(hash)) {
      assertThat(source.readUtf8()).isEqualTo("abc");
    }
  }

  @Test
  public void written_zero_length_data_can_be_read_back() throws Exception {
    var hash = hashedDb().writeData(bufferedSink -> {});
    try (var source = hashedDb().source(hash)) {
      assertThat(source.readByteString()).isEqualTo(ByteString.of());
    }
  }

  @Test
  public void bytes_written_twice_can_be_read_back() throws Exception {
    var hash = hashedDb().writeData(sink -> sink.write(BYTE_STRING_1));
    hashedDb().writeData(sink -> sink.write(BYTE_STRING_1));

    try (var source = hashedDb().source(hash)) {
      assertThat(source.readByteString()).isEqualTo(BYTE_STRING_1);
    }
  }

  @Test
  public void hashes_for_different_data_are_different() throws Exception {
    var hash1 = hashedDb().writeData(sink -> sink.write(BYTE_STRING_1));
    var hash2 = hashedDb().writeData(sink -> sink.write(BYTE_STRING_2));

    assertThat(hash1).isNotEqualTo(hash2);
  }

  @Test
  public void written_data_is_not_visible_until_close_is_invoked() throws Exception {
    var byteString = ByteString.of(new byte[1024 * 1024]);
    var hash = Hash.of(byteString);
    hashedDb().writeData(bufferedSink -> {
      bufferedSink.write(byteString);
      assertCall(() -> hashedDb().source(hash)).throwsException(new NoSuchDataException(hash));
    });
  }

  @Test
  public void getting_hash_when_sink_is_not_closed_closes_sink() throws Exception {
    try (HashingSink hashingSink = hashedDb().sink()) {
      try (var source = new Buffer()) {
        source.write(BYTE_STRING_1);
        source.readAll(hashingSink);
        assertThat(hashingSink.hash()).isEqualTo(Hash.of(BYTE_STRING_1));
      }
    }
  }

  // tests for corrupted db

  @Test
  public void when_hash_points_to_directory_then_contains_fails_with_corrupted_exception()
      throws Exception {
    var hash = Hash.of(33);
    var path = dbPathTo(hash);
    hashedDbFileSystem().createDir(path);

    assertCall(() -> hashedDb().contains(hash))
        .throwsException(new CorruptedHashedDbException(
            "Corrupted HashedDb. " + path.q() + " is a directory not a data file."));
  }

  @Test
  public void when_hash_points_to_directory_then_source_fails_with_corrupted_exception()
      throws IOException {
    var hash = Hash.of(33);
    var path = dbPathTo(hash);
    hashedDbFileSystem().createDir(path);

    assertCall(() -> hashedDb().source(hash))
        .throwsException(new CorruptedHashedDbException(format(
            "Corrupted HashedDb at %s. %s is a directory not a data file.", hash, path.q())));
  }

  @Test
  public void when_hash_points_to_directory_then_sink_fails_with_corrupted_exception()
      throws Exception {
    var hash = Hash.of(BYTE_STRING_1);
    var path = dbPathTo(hash);
    hashedDbFileSystem().createDir(path);

    assertCall(() -> hashedDb().writeData(bufferedSink -> bufferedSink.write(BYTE_STRING_1)))
        .throwsException(
            new HashedDbException("java.io.IOException: Corrupted HashedDb. Cannot store data at "
                + path.q() + " as it is a directory."));
  }

  @Test
  public void temporary_file_is_deleted_when_sink_is_closed() throws Exception {
    var fileSystem = hashedDbFileSystem();
    var hashedDb = new HashedDb(fileSystem);

    hashedDb.writeString("abc");

    assertThat(fileSystem.files(TEMP_DIR_PATH)).isEmpty();
  }

  @Test
  public void temporary_file_is_deleted_when_sink_is_closed_even_when_hashed_valued_exists_in_db()
      throws Exception {
    var fileSystem = hashedDbFileSystem();
    var hashedDb = new HashedDb(fileSystem);

    hashedDb.writeString("abc");
    hashedDb.writeString("abc");

    assertThat(fileSystem.files(TEMP_DIR_PATH)).isEmpty();
  }

  @Nested
  class _big_integer {
    @ParameterizedTest
    @MethodSource("allByteValues")
    public void with_single_byte_value_can_be_read_back(int value) throws Exception {
      var hash = hashedDb().writeByte((byte) value);
      assertThat(hashedDb().readBigInteger(hash)).isEqualTo(BigInteger.valueOf(value));
    }

    @ParameterizedTest
    @ValueSource(ints = {Integer.MIN_VALUE, 1_000_000, Integer.MAX_VALUE})
    public void with_given_value_can_be_read_back(int value) throws Exception {
      var bigInteger = BigInteger.valueOf(value);
      var hash = hashedDb().writeBigInteger(bigInteger);
      assertThat(hashedDb().readBigInteger(hash)).isEqualTo(bigInteger);
    }

    private static Stream<Arguments> allByteValues() {
      return IntStream.rangeClosed(Byte.MIN_VALUE, Byte.MAX_VALUE).mapToObj(Arguments::of);
    }
  }

  @Nested
  class _boolean {
    @Test
    public void with_true_value_can_be_read_back() throws Exception {
      var hash = hashedDb().writeBoolean(true);
      assertThat(hashedDb().readBoolean(hash)).isTrue();
    }

    @Test
    public void with_false_value_can_be_read_back() throws Exception {
      var hash = hashedDb().writeBoolean(false);
      assertThat(hashedDb().readBoolean(hash)).isFalse();
    }
  }

  @Nested
  class _byte {
    @ParameterizedTest
    @MethodSource("allByteValues")
    public void with_given_value_can_be_read_back(int value) throws Exception {
      var hash = hashedDb().writeByte((byte) value);
      assertThat(hashedDb().readByte(hash)).isEqualTo(value);
    }

    private static Stream<Arguments> allByteValues() {
      return IntStream.rangeClosed(MIN_VALUE, MAX_VALUE).mapToObj(Arguments::of);
    }
  }

  @Nested
  class _string {
    @ParameterizedTest
    @ValueSource(strings = {"", "a", "abc", "!@#$"})
    public void with_given_value_can_be_read_back() throws Exception {
      var hash = hashedDb().writeString("abc");
      assertThat(hashedDb().readString(hash)).isEqualTo("abc");
    }

    @Test
    public void illegal_string_causes_decode_exception() throws Exception {
      var hash = Hash.of("abc");
      var path = dbPathTo(hash);

      try (var sink = buffer(hashedDbFileSystem().sink(path))) {
        sink.write(illegalString());
      }
      assertCall(() -> hashedDb().readString(hash))
          .throwsException(new DecodeStringException(hash, null));
    }
  }

  @Nested
  class _hash_chain {
    @Test
    public void with_no_elems_can_be_read_back() throws Exception {
      var hash = hashedDb().writeHashChain();
      assertThat(hashedDb().readHashChain(hash)).isEqualTo(list());
    }

    @Test
    public void with_one_elem_can_be_read_back() throws Exception {
      var hash = hashedDb().writeHashChain(Hash.of("abc"));
      assertThat(hashedDb().readHashChain(hash)).isEqualTo(list(Hash.of("abc")));
    }

    @Test
    public void with_two_elems_can_be_read_back() throws Exception {
      var hash = hashedDb().writeHashChain(Hash.of("abc"), Hash.of("def"));
      assertThat(hashedDb().readHashChain(hash)).isEqualTo(list(Hash.of("abc"), Hash.of("def")));
    }

    @Test
    public void not_written_hash_chain_cannot_be_read_back() {
      var hash = Hash.of("abc");
      assertCall(() -> hashedDb().readHashChain(hash))
          .throwsException(new NoSuchDataException(hash));
    }

    @Test
    public void corrupted_hash_chain_cannot_be_read_back() throws Exception {
      var hash = hashedDb().writeString("12345");
      assertCall(() -> hashedDb().readHashChain(hash))
          .throwsException(new DecodeHashChainException(hash, 5));
    }
  }

  @Nested
  class _hash_chain_size {
    @Test
    public void with_no_elems_has_zero_size() throws Exception {
      var hash = hashedDb().writeHashChain();
      assertThat(hashedDb().readHashChainSize(hash)).isEqualTo(0);
    }

    @Test
    public void with_one_elem_has_size_one() throws Exception {
      var hash = hashedDb().writeHashChain(Hash.of("1"));
      assertThat(hashedDb().readHashChainSize(hash)).isEqualTo(1);
    }

    @Test
    public void with_three_elem_has_size_three() throws Exception {
      var hash = hashedDb().writeHashChain(Hash.of("1"), Hash.of("2"), Hash.of("3"));
      assertThat(hashedDb().readHashChainSize(hash)).isEqualTo(3);
    }

    @Test
    public void reading_size_of_not_written_hash_chain_causes_exception() {
      var hash = Hash.of("abc");
      assertCall(() -> hashedDb().readHashChainSize(hash))
          .throwsException(new NoSuchDataException(hash));
    }

    @Test
    public void reading_size_of_corrupted_hash_chain_causes_exception() throws Exception {
      var hash = hashedDb().writeString("12345");
      assertCall(() -> hashedDb().readHashChainSize(hash))
          .throwsException(new DecodeHashChainException(hash, 5));
    }
  }
}
