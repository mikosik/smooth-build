package org.smoothbuild.lang.object.db;

import static org.smoothbuild.testing.common.ExceptionMatcher.exception;
import static org.smoothbuild.util.Lists.list;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.NoSuchDataException;
import org.smoothbuild.testing.TestingContext;

public class ValuesDbTest extends TestingContext {
  private Hash hash;

  @Test
  public void written_true_boolean_can_be_read_back() throws Exception {
    given(hash = valuesDb().writeBoolean(true));
    when(() -> valuesDb().readBoolean(hash));
    thenReturned(true);
  }

  @Test
  public void written_false_boolean_can_be_read_back() throws Exception {
    given(hash = valuesDb().writeBoolean(false));
    when(() -> valuesDb().readBoolean(hash));
    thenReturned(false);
  }

  @Test
  public void written_string_can_be_read_back() throws Exception {
    given(hash = valuesDb().writeString("abc"));
    when(() -> valuesDb().readString(hash));
    thenReturned("abc");
  }

  // readHashes(hash)

  @Test
  public void written_empty_sequence_of_hashes_can_be_read_back() throws Exception {
    given(hash = valuesDb().writeHashes());
    when(() -> valuesDb().readHashes(hash));
    thenReturned(list());
  }

  @Test
  public void written_one_element_sequence_of_hashes_can_be_read_back() throws Exception {
    given(hash = valuesDb().writeHashes(Hash.of("abc")));
    when(() -> valuesDb().readHashes(hash));
    thenReturned(list(Hash.of("abc")));
  }

  @Test
  public void written_two_elements_sequence_of_hashes_can_be_read_back() throws Exception {
    given(hash = valuesDb().writeHashes(Hash.of("abc"), Hash.of("def")));
    when(() -> valuesDb().readHashes(hash));
    thenReturned(list(Hash.of("abc"), Hash.of("def")));
  }

  @Test
  public void not_written_sequence_of_hashes_cannot_be_read_back() {
    given(() -> hash = Hash.of("abc"));
    when(() -> valuesDb().readHashes(hash));
    thenThrown(exception(new ValuesDbException(hash, new NoSuchDataException(hash))));
  }

  @Test
  public void corrupted_sequence_of_hashes_cannot_be_read_back() throws Exception {
    given(hash = valuesDb().writeString("12345"));
    when(() -> valuesDb().readHashes(hash));
    thenThrown(exception(new DecodingHashSequenceException(hash)));
  }

  // readHashes(hash, expectedSize)

  @Test
  public void reading_0_hashes_with_expect_size_0_succeeds() throws Exception {
    given(hash = valuesDb().writeHashes());
    when(() -> valuesDb().readHashes(hash, 0));
    thenReturned(list());
  }

  @Test
  public void reading_0_hashes_with_expect_size_1_causes_exception() throws Exception {
    given(hash = valuesDb().writeHashes());
    when(() -> valuesDb().readHashes(hash, 1));
    thenThrown(exception(new DecodingHashSequenceException(hash, 1, 0)));
  }

  @Test
  public void reading_1_hashes_with_expect_size_0_causes_exception() throws Exception {
    given(hash = valuesDb().writeHashes(Hash.of("a")));
    when(() -> valuesDb().readHashes(hash, 0));
    thenThrown(exception(new DecodingHashSequenceException(hash, 0, 1)));
  }

  @Test
  public void reading_1_hashes_with_expect_size_1_succeeds() {
    given(() -> hash = valuesDb().writeHashes(Hash.of("a")));
    when(() -> valuesDb().readHashes(hash, 1));
    thenReturned(list(Hash.of("a")));
  }

  @Test
  public void reading_1_hashes_with_expect_size_2_causes_exception() throws Exception {
    given(hash = valuesDb().writeHashes(Hash.of("a")));
    when(() -> valuesDb().readHashes(hash, 2));
    thenThrown(exception(new DecodingHashSequenceException(hash, 2, 1)));
  }

  @Test
  public void reading_not_written_sequence_of_hashes_with_expect_size_0_throws_exception() {
    given(() -> hash = Hash.of("abc"));
    when(() -> valuesDb().readHashes(hash, 0));
    thenThrown(exception(new ValuesDbException(hash, new NoSuchDataException(hash))));
  }

  @Test
  public void reading_corrupted_sequence_of_hashes_with_expect_size_causes_exception() throws Exception {
    given(hash = valuesDb().writeString("12345"));
    when(() -> valuesDb().readHashes(hash, 0));
    thenThrown(exception(new DecodingHashSequenceException(hash)));
  }

  // readHashes(hash, minExpectedSize, maxExpectedSize)

  @Test
  public void reading_less_hashes_than_expected_range_causes_exception() throws Exception {
    given(hash = valuesDb().writeHashes(Hash.of("a")));
    when(() -> valuesDb().readHashes(hash, 2, 4));
    thenThrown(exception(new DecodingHashSequenceException(hash, 2, 4, 1)));
  }

  @Test
  public void reading_exactly_min_expected_hashes_succeeds() throws Exception {
    given(hash = valuesDb().writeHashes(Hash.of("a"), Hash.of("b")));
    when(() -> valuesDb().readHashes(hash, 2, 4));
    thenReturned(list(Hash.of("a"), Hash.of("b")));
  }

  @Test
  public void reading_exactly_max_expected_hashes_succeeds() throws Exception {
    given(hash = valuesDb().writeHashes(Hash.of("a"), Hash.of("b"), Hash.of("c")));
    when(() -> valuesDb().readHashes(hash, 2, 3));
    thenReturned(list(Hash.of("a"), Hash.of("b"), Hash.of("c")));
  }

  @Test
  public void reading_more_hashes_than_expected_range_causes_exception() throws Exception {
    given(hash = valuesDb().writeHashes(Hash.of("a"), Hash.of("b"), Hash.of("c")));
    when(() -> valuesDb().readHashes(hash, 0, 2));
    thenThrown(exception(new DecodingHashSequenceException(hash, 0, 2, 3)));
  }

  @Test
  public void reading_not_written_sequence_of_hashes_with_expect_range_with_min_0_throws_exception() {
    given(() -> hash = Hash.of("abc"));
    when(() -> valuesDb().readHashes(hash, 0, 2));
    thenThrown(exception(new ValuesDbException(hash, new NoSuchDataException(hash))));
  }

  @Test
  public void reading_corrupted_sequence_of_hashes_with_expect_range_causes_exception() throws Exception {
    given(hash = valuesDb().writeString("12345"));
    when(() -> valuesDb().readHashes(hash, 0, 2));
    thenThrown(exception(new DecodingHashSequenceException(hash)));
  }
}
