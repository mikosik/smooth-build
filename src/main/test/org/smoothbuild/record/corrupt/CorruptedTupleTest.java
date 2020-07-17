package org.smoothbuild.record.corrupt;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.common.AssertCall.assertCall;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.hashed.DecodingHashSequenceException;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.record.base.Tuple;
import org.smoothbuild.record.db.RecordDbException;

public class CorruptedTupleTest extends AbstractCorruptedTestCase {
  @Test
  public void learning_test_create_tuple() throws Exception {
    /*
     * This test makes sure that other tests in this class use proper scheme to save smooth tuple
     * in HashedDb.
     */
    assertThat(
        hash(
            hash(personSpec()),
            hash(
                hash(string("John")),
                hash(string("Doe")))))
        .isEqualTo(person("John", "Doe").hash());
  }

  @Test
  public void tuple_with_too_few_elements_is_corrupted() throws Exception {
    Hash elementValuesHash =
        hash(
            hash(string("John")));
    Hash tupleHash =
        hash(
            hash(personSpec()),
            elementValuesHash);
    Tuple tuple = (Tuple) recordDb().get(tupleHash);
    assertCall(() -> tuple.get(0))
        .throwsException(new RecordDbException(tupleHash, errorReadingElementHashes()))
        .withCause(new DecodingHashSequenceException(elementValuesHash, 2, 1));
  }

  @Test
  public void tuple_with_too_many_elements_is_corrupted() throws Exception {
    Hash elementValuesHash =
        hash(
            hash(string("John")),
            hash(string("Doe")),
            hash(string("junk")));
    Hash tupleHash =
        hash(
            hash(personSpec()),
            elementValuesHash);
    Tuple tuple = (Tuple) recordDb().get(tupleHash);
    assertCall(() -> tuple.get(0))
        .throwsException(new RecordDbException(tupleHash, errorReadingElementHashes()))
        .withCause(new DecodingHashSequenceException(elementValuesHash, 2, 3));
  }

  @Test
  public void tuple_with_element_of_wrong_spec_is_corrupted() throws Exception {
    Hash tupleHash =
        hash(
            hash(personSpec()),
            hash(
                hash(string("John")),
                hash(bool(true))));
    Tuple tuple = (Tuple) recordDb().get(tupleHash);
    assertCall(() -> tuple.get(0))
        .throwsException(new RecordDbException(tupleHash, "Its TUPLE spec declares element 1 " +
            "to have STRING spec but its data has record with BOOL spec at that index."));
  }

  private static String errorReadingElementHashes() {
    return "Error reading element hashes.";
  }
}
