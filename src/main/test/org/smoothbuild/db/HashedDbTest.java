package org.smoothbuild.db;

import static com.google.common.truth.Truth.assertThat;
import static java.lang.Byte.MAX_VALUE;
import static java.lang.Byte.MIN_VALUE;
import static java.lang.String.format;
import static okio.ByteString.encodeUtf8;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.testing.StringCreators.illegalString;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.io.Okios.writeAndClose;

import java.io.IOException;
import java.math.BigInteger;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.smoothbuild.db.exc.CorruptedHashedDbExc;
import org.smoothbuild.db.exc.DecodeHashSeqExc;
import org.smoothbuild.db.exc.DecodeStringExc;
import org.smoothbuild.db.exc.NoSuchDataExc;
import org.smoothbuild.testing.TestingContext;

import okio.ByteString;

public class HashedDbTest extends TestingContext {
  private final ByteString bytes1 = ByteString.encodeUtf8("aaa");
  private final ByteString bytes2 = ByteString.encodeUtf8("bbb");
  private HashingBufferedSink sink;
  private Hash hash;
  private ByteString byteString;

  @Test
  public void db_doesnt_contain_not_written_data() throws CorruptedHashedDbExc {
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
        .throwsException(new NoSuchDataExc(hash));
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
        .throwsException(new NoSuchDataExc(hash));
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
        .throwsException(new CorruptedHashedDbExc(
            "Corrupted HashedDb. '" + hash + "' is a directory not a data file."));
  }

  @Test
  public void when_hash_points_to_directory_then_source_causes_corrupted_exception()
      throws IOException {
    hash = Hash.of(33);
    hashedDbFileSystem().createDir(path(hash.toString()));

    assertCall(() -> hashedDb().source(hash))
        .throwsException(new CorruptedHashedDbExc(
            format("Corrupted HashedDb at %s. '%s' is a directory not a data file.", hash, hash)));
  }

  @Test
  public void when_hash_points_to_directory_then_sink_causes_corrupted_exception()
      throws IOException {
    hash = Hash.of(bytes1);
    hashedDbFileSystem().createDir(path(hash.toHexString()));

    assertCall(() -> hashedDb().sink().write(bytes1).close())
        .throwsException(new IOException(
            "Corrupted HashedDb. Cannot store data at '" + hash + "' as it is a directory."));
  }

  @Nested
  class _big_integer {
    @ParameterizedTest
    @MethodSource("allByteValues")
    public void with_single_byte_value_can_be_read_back(int value) throws Exception {
      hash = hashedDb().writeByte((byte) value);
      assertThat(hashedDb().readBigInteger(hash))
          .isEqualTo(BigInteger.valueOf(value));
    }

    @ParameterizedTest
    @ValueSource(ints = {Integer.MIN_VALUE, 1_000_000, Integer.MAX_VALUE})
    public void with_given_value_can_be_read_back(int value) throws Exception {
      BigInteger bigInteger = BigInteger.valueOf(value);
      hash = hashedDb().writeBigInteger(bigInteger);
      assertThat(hashedDb().readBigInteger(hash))
          .isEqualTo(bigInteger);
    }

    private static Stream<Arguments> allByteValues() {
      return IntStream.rangeClosed(Byte.MIN_VALUE, Byte.MAX_VALUE)
          .mapToObj(Arguments::of);
    }
  }

  @Nested
  class _boolean {
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
  class _byte {
    @ParameterizedTest
    @MethodSource("allByteValues")
    public void with_given_value_can_be_read_back(int value) throws Exception {
      hash = hashedDb().writeByte((byte) value);
      assertThat(hashedDb().readByte(hash))
          .isEqualTo(value);
    }

    private static Stream<Arguments> allByteValues() {
      return IntStream.rangeClosed(MIN_VALUE, MAX_VALUE)
          .mapToObj(Arguments::of);
    }
  }

  @Nested
  class _string {
    @ParameterizedTest
    @ValueSource(strings = {"", "a", "abc", "!@#$"})
    public void with_given_value_can_be_read_back() throws Exception {
      hash = hashedDb().writeString("abc");
      assertThat(hashedDb().readString(hash))
          .isEqualTo("abc");
    }

    @Test
    public void illegal_string_causes_decode_exception() throws Exception {
      hash = Hash.of("abc");
      writeAndClose(hashedDbFileSystem().sink(path(hash.toHexString())), s -> s.write(illegalString()));
      assertCall(() -> hashedDb().readString(hash))
          .throwsException(new DecodeStringExc(hash, null));
    }
  }

  @Nested
  class _sequence {
    @Test
    public void with_no_elems_can_be_read_back() throws Exception {
      hash = hashedDb().writeSeq();
      assertThat(hashedDb().readSeq(hash))
          .isEqualTo(list());
    }

    @Test
    public void with_one_elem_can_be_read_back() throws Exception {
      hash = hashedDb().writeSeq(Hash.of("abc"));
      assertThat(hashedDb().readSeq(hash))
          .isEqualTo(list(Hash.of("abc")));
    }

    @Test
    public void with_two_elems_can_be_read_back() throws Exception {
      hash = hashedDb().writeSeq(Hash.of("abc"), Hash.of("def"));
      assertThat(hashedDb().readSeq(hash))
          .isEqualTo(list(Hash.of("abc"), Hash.of("def")));
    }

    @Test
    public void not_written_seq_of_hashes_cannot_be_read_back() {
      hash = Hash.of("abc");
      assertCall(() -> hashedDb().readSeq(hash))
          .throwsException(new NoSuchDataExc(hash));
    }

    @Test
    public void corrupted_seq_of_hashes_cannot_be_read_back() throws Exception {
      hash = hashedDb().writeString("12345");
      assertCall(() -> hashedDb().readSeq(hash))
          .throwsException(new DecodeHashSeqExc(hash, 5));
    }
  }
}
