package org.smoothbuild.db.hashed;

import static com.google.common.truth.Truth.assertThat;
import static java.lang.Byte.MAX_VALUE;
import static java.lang.Byte.MIN_VALUE;
import static java.lang.String.format;
import static okio.ByteString.encodeUtf8;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.Lists.list;

import java.io.IOException;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.smoothbuild.testing.TestingContext;

import okio.ByteString;

public class HashedDbTest extends TestingContext {
  private final ByteString bytes1 = ByteString.encodeUtf8("aaa");
  private final ByteString bytes2 = ByteString.encodeUtf8("bbb");
  private HashingBufferedSink sink;
  private Hash hash;
  private ByteString byteString;

  @Test
  public void db_doesnt_contain_not_written_data() throws CorruptedHashedDbException {
    assertThat(hashedDb().contains(Hash.of(33)))
        .isFalse();
  }

  @Test
  public void db_contains_written_data() throws Exception {
    sink = hashedDb().sink();
    sink.close();

    assertThat(hashedDb().contains(sink.hash()))
        .isTrue();
  }

  @Test
  public void reading_not_written_value_fails() {
    hash = Hash.of("abc");
    assertCall(() -> hashedDb().source(hash))
        .throwsException(new NoSuchDataException(hash));
  }

  @Test
  public void written_data_can_be_read_back() throws Exception {
    sink = hashedDb().sink();
    byteString = encodeUtf8("abc");
    sink.write(byteString);
    sink.close();

    assertThat(hashedDb().source(sink.hash()).readUtf8())
        .isEqualTo("abc");
  }

  @Test
  public void written_zero_length_data_can_be_read_back() throws Exception {
    sink = hashedDb().sink();
    sink.close();

    assertThat(hashedDb().source(sink.hash()).readByteString())
        .isEqualTo(ByteString.of());
  }

  @Test
  public void bytes_written_twice_can_be_read_back() throws Exception {
    sink = hashedDb().sink();
    sink.write(bytes1);
    sink.close();
    sink = hashedDb().sink();
    sink.write(bytes1);
    sink.close();

    assertThat(hashedDb().source(sink.hash()).readByteString())
        .isEqualTo(bytes1);
  }

  @Test
  public void hashes_for_different_data_are_different() throws Exception {
    sink = hashedDb().sink();
    sink.write(bytes1);
    sink.close();
    hash = sink.hash();
    sink = hashedDb().sink();
    sink.write(bytes2);
    sink.close();

    assertThat(sink.hash())
        .isNotEqualTo(hash);
  }

  @Test
  public void written_data_is_not_visible_until_close_is_invoked() throws Exception {
    byteString = ByteString.of(new byte[1024 * 1024]);
    hash = Hash.of(byteString);
    sink = hashedDb().sink();
    sink.write(byteString);

    assertCall(() -> hashedDb().source(hash))
        .throwsException(new NoSuchDataException(hash));
  }

  @Test
  public void getting_hash_when_sink_is_not_closed_causes_exception() throws Exception {
    sink = hashedDb().sink();
    assertCall(() -> sink.hash())
        .throwsException(IllegalStateException.class);
  }

  // tests for corrupted db

  @Test
  public void when_hash_points_to_directory_then_contains_causes_corrupted_exception()
      throws Exception {
    hash = Hash.of(33);
    hashedDbFileSystem().createDir(path(hash.toString()));

    assertCall(() -> hashedDb().contains(hash))
        .throwsException(new CorruptedHashedDbException(
            "Corrupted HashedDb. '" + hash + "' is a directory not a data file."));
  }

  @Test
  public void when_hash_points_to_directory_then_source_causes_corrupted_exception()
      throws IOException {
    hash = Hash.of(33);
    hashedDbFileSystem().createDir(path(hash.toString()));

    assertCall(() -> hashedDb().source(hash))
        .throwsException(new CorruptedHashedDbException(
            format("Corrupted HashedDb at %s. '%s' is a directory not a data file.", hash, hash)));
  }

  @Test
  public void when_hash_points_to_directory_then_sink_causes_corrupted_exception()
      throws IOException {
    hash = Hash.of(bytes1);
    hashedDbFileSystem().createDir(path(hash.hex()));

    assertCall(() -> hashedDb().sink().write(bytes1).close())
        .throwsException(new IOException(
            "Corrupted HashedDb. Cannot store data at '" + hash + "' as it is a directory."));
  }

  @Nested
  class aBool {
    @Test
    public void with_true_value_can_be_read_back() throws Exception {
      hash = hashedDb().writeBoolean(true);
      assertThat(hashedDb().readBoolean(hash))
          .isTrue();
    }

    @Test
    public void with_false_value_can_be_read_back() throws Exception {
      hash = hashedDb().writeBoolean(false);
      assertThat(hashedDb().readBoolean(hash))
          .isFalse();
    }
  }

  @Nested
  @TestInstance(PER_CLASS) // workaround that allows non-static allByteValues
  class aByte {
    @ParameterizedTest
    @MethodSource("allByteValues")
    public void with_given_value_can_be_read_back(int value) throws Exception {
      hash = hashedDb().writeByte((byte) value);
      assertThat(hashedDb().readByte(hash))
          .isEqualTo(value);
    }

    private Stream<Arguments> allByteValues() {
      return IntStream.range(MIN_VALUE, MAX_VALUE)
          .mapToObj(Arguments::of);
    }
  }

  @Nested
  class aString {
    @ParameterizedTest
    @ValueSource(strings = {"", "a", "abc", "!@#$"})
    public void with_given_value_can_be_read_back() throws Exception {
      hash = hashedDb().writeString("abc");
      assertThat(hashedDb().readString(hash))
          .isEqualTo("abc");
    }
  }

  @Nested
  class aHashSequence {
    @Test
    public void with_no_elements_can_be_read_back() throws Exception {
      hash = hashedDb().writeHashes();
      assertThat(hashedDb().readHashes(hash))
          .isEqualTo(list());
    }

    @Test
    public void with_one_element_can_be_read_back() throws Exception {
      hash = hashedDb().writeHashes(Hash.of("abc"));
      assertThat(hashedDb().readHashes(hash))
          .isEqualTo(list(Hash.of("abc")));
    }

    @Test
    public void with_two_elements_can_be_read_back() throws Exception {
      hash = hashedDb().writeHashes(Hash.of("abc"), Hash.of("def"));
      assertThat(hashedDb().readHashes(hash))
          .isEqualTo(list(Hash.of("abc"), Hash.of("def")));
    }

    @Test
    public void not_written_sequence_of_hashes_cannot_be_read_back() {
      hash = Hash.of("abc");
      assertCall(() -> hashedDb().readHashes(hash))
          .throwsException(new NoSuchDataException(hash));
    }

    @Test
    public void corrupted_sequence_of_hashes_cannot_be_read_back() throws Exception {
      hash = hashedDb().writeString("12345");
      assertCall(() -> hashedDb().readHashes(hash))
          .throwsException(new DecodingHashSequenceException(hash));
    }
  }

  @Nested
  class read_hashes_with_expected_size {
    @Test
    public void reading_0_hashes_with_expect_size_0_succeeds() throws Exception {
      hash = hashedDb().writeHashes();
      assertThat(hashedDb().readHashes(hash, 0))
          .isEqualTo(list());
    }

    @Test
    public void reading_0_hashes_with_expect_size_1_causes_exception() throws Exception {
      hash = hashedDb().writeHashes();
      assertCall(() -> hashedDb().readHashes(hash, 1))
          .throwsException(new DecodingHashSequenceException(hash, 1, 0));
    }

    @Test
    public void reading_1_hashes_with_expect_size_0_causes_exception() throws Exception {
      hash = hashedDb().writeHashes(Hash.of("a"));
      assertCall(() -> hashedDb().readHashes(hash, 0))
          .throwsException(new DecodingHashSequenceException(hash, 0, 1));
    }

    @Test
    public void reading_1_hashes_with_expect_size_1_succeeds() throws HashedDbException {
      hash = hashedDb().writeHashes(Hash.of("a"));
      assertThat(hashedDb().readHashes(hash, 1))
          .isEqualTo(list(Hash.of("a")));
    }

    @Test
    public void reading_1_hashes_with_expect_size_2_causes_exception() throws Exception {
      hash = hashedDb().writeHashes(Hash.of("a"));
      assertCall(() -> hashedDb().readHashes(hash, 2))
          .throwsException(new DecodingHashSequenceException(hash, 2, 1));
    }

    @Test
    public void reading_not_written_sequence_of_hashes_with_expect_size_0_throws_exception() {
      hash = Hash.of("abc");
      assertCall(() -> hashedDb().readHashes(hash, 0))
          .throwsException(new NoSuchDataException(hash));
    }

    @Test
    public void reading_corrupted_sequence_of_hashes_with_expect_size_causes_exception()
        throws Exception {
      hash = hashedDb().writeString("12345");
      assertCall(() -> hashedDb().readHashes(hash, 0))
          .throwsException(new DecodingHashSequenceException(hash));
    }
  }

  @Nested
  class read_hashes_with_expected_min_and_max_size {
    @Test
    public void reading_less_hashes_than_expected_range_causes_exception() throws Exception {
      hash = hashedDb().writeHashes(Hash.of("a"));
      assertCall(() -> hashedDb().readHashes(hash, 2, 4))
          .throwsException(new DecodingHashSequenceException(hash, 2, 4, 1));
    }

    @Test
    public void reading_exactly_min_expected_hashes_succeeds() throws Exception {
      hash = hashedDb().writeHashes(Hash.of("a"), Hash.of("b"));
      assertThat(hashedDb().readHashes(hash, 2, 4))
          .isEqualTo(list(Hash.of("a"), Hash.of("b")));
    }

    @Test
    public void reading_exactly_max_expected_hashes_succeeds() throws Exception {
      hash = hashedDb().writeHashes(Hash.of("a"), Hash.of("b"), Hash.of("c"));
      assertThat(hashedDb().readHashes(hash, 2, 3))
          .isEqualTo(list(Hash.of("a"), Hash.of("b"), Hash.of("c")));
    }

    @Test
    public void reading_more_hashes_than_expected_range_causes_exception() throws Exception {
      hash = hashedDb().writeHashes(Hash.of("a"), Hash.of("b"), Hash.of("c"));
      assertCall(() -> hashedDb().readHashes(hash, 0, 2))
          .throwsException(new DecodingHashSequenceException(hash, 0, 2, 3));
    }

    @Test
    public void reading_not_written_sequence_of_hashes_with_expect_range_with_min_0_throws_exception() {
      hash = Hash.of("abc");
      assertCall(() -> hashedDb().readHashes(hash, 0, 2))
          .throwsException(new NoSuchDataException(hash));
    }

    @Test
    public void reading_corrupted_sequence_of_hashes_with_expect_range_causes_exception() throws
        Exception {
      hash = hashedDb().writeString("12345");
      assertCall(() -> hashedDb().readHashes(hash, 0, 2))
          .throwsException(new DecodingHashSequenceException(hash));
    }
  }
}
