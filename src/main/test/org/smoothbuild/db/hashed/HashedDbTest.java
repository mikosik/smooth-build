package org.smoothbuild.db.hashed;

import static java.lang.String.format;
import static okio.ByteString.encodeUtf8;
import static org.hamcrest.Matchers.not;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.testing.common.ExceptionMatcher.exception;
import static org.smoothbuild.util.Lists.list;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import java.io.IOException;

import org.junit.Test;
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
    when(hashedDb().contains(Hash.of(33)));
    thenReturned(false);
  }

  @Test
  public void db_contains_written_data() throws Exception {
    given(sink = hashedDb().sink());
    given(sink).close();
    when(hashedDb().contains(sink.hash()));
    thenReturned(true);
  }

  @Test
  public void reading_not_written_value_fails() {
    given(hash = Hash.of("abc"));
    when(() -> hashedDb().source(hash));
    thenThrown(exception(new NoSuchDataException(hash)));
  }

  @Test
  public void written_data_can_be_read_back() throws IOException {
    given(() -> sink = hashedDb().sink());
    given(byteString = encodeUtf8("abc"));
    given(sink.write(byteString));
    given(sink).close();
    when(() -> hashedDb().source(sink.hash()).readUtf8());
    thenReturned("abc");
  }

  @Test
  public void written_zero_length_data_can_be_read_back() throws IOException {
    given(() -> sink = hashedDb().sink());
    given(sink).close();
    when(() -> hashedDb().source(sink.hash()).readByteString());
    thenReturned(ByteString.of());
  }

  @Test
  public void bytes_written_twice_can_be_read_back() {
    given(() -> sink = hashedDb().sink());
    given(() -> sink.write(bytes1));
    given(() -> sink.close());
    given(() -> sink = hashedDb().sink());
    given(() -> sink.write(bytes1));
    given(() -> sink.close());
    when(() -> hashedDb().source(sink.hash()).readByteString());
    thenReturned(bytes1);
  }

  @Test
  public void hashes_for_different_data_are_different() {
    given(() -> sink = hashedDb().sink());
    given(() -> sink.write(bytes1));
    given(() -> sink.close());
    given(() -> hash = sink.hash());
    given(() -> sink = hashedDb().sink());
    given(() -> sink.write(bytes2));
    given(() -> sink.close());
    when(sink.hash());
    thenReturned(not(hash));
  }

  @Test
  public void written_data_is_not_visible_until_close_is_invoked() {
    given(() -> byteString = ByteString.of(new byte[1024 * 1024]));
    given(() -> hash = Hash.of(byteString));
    given(() -> sink = hashedDb().sink());
    given(() -> sink.write(byteString));
    when(() -> hashedDb().source(hash));
    thenThrown(exception(new NoSuchDataException(hash)));
  }

  @Test
  public void getting_hash_when_sink_is_not_closed_causes_exception() throws Exception {
    given(sink = hashedDb().sink());
    when(() -> sink.hash());
    thenThrown(IllegalStateException.class);
  }

  // tests for corrupted db

  @Test
  public void when_hash_points_to_directory_then_contains_causes_corrupted_exception() {
    given(() -> hash = Hash.of(33));
    given(() -> hashedDbFileSystem().createDir(path(hash.toString())));
    when(() -> hashedDb().contains(hash));
    thenThrown(exception(new CorruptedHashedDbException(
        "Corrupted HashedDb. '" + hash + "' is a directory not a data file.")));
  }

  @Test
  public void when_hash_points_to_directory_then_source_causes_corrupted_exception() {
    given(() -> hash = Hash.of(33));
    given(() -> hashedDbFileSystem().createDir(path(hash.toString())));
    when(() -> hashedDb().source(hash));
    thenThrown(exception(new CorruptedHashedDbException(
        format("Corrupted HashedDb at %s. '%s' is a directory not a data file.", hash, hash))));
  }

  @Test
  public void when_hash_points_to_directory_then_sink_causes_corrupted_exception() {
    given(() -> hash = Hash.of(bytes1));
    given(() -> hashedDbFileSystem().createDir(path(hash.hex())));
    when(() -> hashedDb().sink().write(bytes1).close());
    thenThrown(exception(new IOException(
        "Corrupted HashedDb. Cannot store data at '" + hash + "' as it is a directory.")));
  }


  @Test
  public void written_true_boolean_can_be_read_back() throws Exception {
    given(hash = hashedDb().writeBoolean(true));
    when(() -> hashedDb().readBoolean(hash));
    thenReturned(true);
  }

  @Test
  public void written_false_boolean_can_be_read_back() throws Exception {
    given(hash = hashedDb().writeBoolean(false));
    when(() -> hashedDb().readBoolean(hash));
    thenReturned(false);
  }

  @Test
  public void written_string_can_be_read_back() throws Exception {
    given(hash = hashedDb().writeString("abc"));
    when(() -> hashedDb().readString(hash));
    thenReturned("abc");
  }

  // readHashes(hash)

  @Test
  public void written_empty_sequence_of_hashes_can_be_read_back() throws Exception {
    given(hash = hashedDb().writeHashes());
    when(() -> hashedDb().readHashes(hash));
    thenReturned(list());
  }

  @Test
  public void written_one_element_sequence_of_hashes_can_be_read_back() throws Exception {
    given(hash = hashedDb().writeHashes(Hash.of("abc")));
    when(() -> hashedDb().readHashes(hash));
    thenReturned(list(Hash.of("abc")));
  }

  @Test
  public void written_two_elements_sequence_of_hashes_can_be_read_back() throws Exception {
    given(hash = hashedDb().writeHashes(Hash.of("abc"), Hash.of("def")));
    when(() -> hashedDb().readHashes(hash));
    thenReturned(list(Hash.of("abc"), Hash.of("def")));
  }

  @Test
  public void not_written_sequence_of_hashes_cannot_be_read_back() {
    given(() -> hash = Hash.of("abc"));
    when(() -> hashedDb().readHashes(hash));
    thenThrown(exception(new NoSuchDataException(hash)));
  }

  @Test
  public void corrupted_sequence_of_hashes_cannot_be_read_back() throws Exception {
    given(hash = hashedDb().writeString("12345"));
    when(() -> hashedDb().readHashes(hash));
    thenThrown(exception(new DecodingHashSequenceException(hash)));
  }

  // readHashes(hash, expectedSize)

  @Test
  public void reading_0_hashes_with_expect_size_0_succeeds() throws Exception {
    given(hash = hashedDb().writeHashes());
    when(() -> hashedDb().readHashes(hash, 0));
    thenReturned(list());
  }

  @Test
  public void reading_0_hashes_with_expect_size_1_causes_exception() throws Exception {
    given(hash = hashedDb().writeHashes());
    when(() -> hashedDb().readHashes(hash, 1));
    thenThrown(exception(new DecodingHashSequenceException(hash, 1, 0)));
  }

  @Test
  public void reading_1_hashes_with_expect_size_0_causes_exception() throws Exception {
    given(hash = hashedDb().writeHashes(Hash.of("a")));
    when(() -> hashedDb().readHashes(hash, 0));
    thenThrown(exception(new DecodingHashSequenceException(hash, 0, 1)));
  }

  @Test
  public void reading_1_hashes_with_expect_size_1_succeeds() {
    given(() -> hash = hashedDb().writeHashes(Hash.of("a")));
    when(() -> hashedDb().readHashes(hash, 1));
    thenReturned(list(Hash.of("a")));
  }

  @Test
  public void reading_1_hashes_with_expect_size_2_causes_exception() throws Exception {
    given(hash = hashedDb().writeHashes(Hash.of("a")));
    when(() -> hashedDb().readHashes(hash, 2));
    thenThrown(exception(new DecodingHashSequenceException(hash, 2, 1)));
  }

  @Test
  public void reading_not_written_sequence_of_hashes_with_expect_size_0_throws_exception() {
    given(() -> hash = Hash.of("abc"));
    when(() -> hashedDb().readHashes(hash, 0));
    thenThrown(exception(new NoSuchDataException(hash)));
  }

  @Test
  public void reading_corrupted_sequence_of_hashes_with_expect_size_causes_exception() throws Exception {
    given(hash = hashedDb().writeString("12345"));
    when(() -> hashedDb().readHashes(hash, 0));
    thenThrown(exception(new DecodingHashSequenceException(hash)));
  }

  // readHashes(hash, minExpectedSize, maxExpectedSize)

  @Test
  public void reading_less_hashes_than_expected_range_causes_exception() throws Exception {
    given(hash = hashedDb().writeHashes(Hash.of("a")));
    when(() -> hashedDb().readHashes(hash, 2, 4));
    thenThrown(exception(new DecodingHashSequenceException(hash, 2, 4, 1)));
  }

  @Test
  public void reading_exactly_min_expected_hashes_succeeds() throws Exception {
    given(hash = hashedDb().writeHashes(Hash.of("a"), Hash.of("b")));
    when(() -> hashedDb().readHashes(hash, 2, 4));
    thenReturned(list(Hash.of("a"), Hash.of("b")));
  }

  @Test
  public void reading_exactly_max_expected_hashes_succeeds() throws Exception {
    given(hash = hashedDb().writeHashes(Hash.of("a"), Hash.of("b"), Hash.of("c")));
    when(() -> hashedDb().readHashes(hash, 2, 3));
    thenReturned(list(Hash.of("a"), Hash.of("b"), Hash.of("c")));
  }

  @Test
  public void reading_more_hashes_than_expected_range_causes_exception() throws Exception {
    given(hash = hashedDb().writeHashes(Hash.of("a"), Hash.of("b"), Hash.of("c")));
    when(() -> hashedDb().readHashes(hash, 0, 2));
    thenThrown(exception(new DecodingHashSequenceException(hash, 0, 2, 3)));
  }

  @Test
  public void reading_not_written_sequence_of_hashes_with_expect_range_with_min_0_throws_exception() {
    given(() -> hash = Hash.of("abc"));
    when(() -> hashedDb().readHashes(hash, 0, 2));
    thenThrown(exception(new NoSuchDataException(hash)));
  }

  @Test
  public void reading_corrupted_sequence_of_hashes_with_expect_range_causes_exception() throws Exception {
    given(hash = hashedDb().writeString("12345"));
    when(() -> hashedDb().readHashes(hash, 0, 2));
    thenThrown(exception(new DecodingHashSequenceException(hash)));
  }
}
