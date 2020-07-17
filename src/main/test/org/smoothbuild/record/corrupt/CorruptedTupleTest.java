package org.smoothbuild.record.corrupt;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.common.AssertCall.assertCall;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.hashed.DecodingHashSequenceException;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.record.base.Tuple;
import org.smoothbuild.record.db.ObjectDbException;

public class CorruptedTupleTest extends AbstractCorruptedTestCase {
  @Test
  public void learning_test_create_struct() throws Exception {
    /*
     * This test makes sure that other tests in this class use proper scheme to save smooth struct
     * in HashedDb.
     */
    assertThat(
        hash(
            hash(personType()),
            hash(
                hash(string("John")),
                hash(string("Doe")))))
        .isEqualTo(person("John", "Doe").hash());
  }

  @Test
  public void struct_with_too_few_fields_is_corrupted() throws Exception {
    Hash fieldValuesHash =
        hash(
            hash(string("John")));
    Hash structHash =
        hash(
            hash(personType()),
            fieldValuesHash);
    Tuple tuple = (Tuple) objectDb().get(structHash);
    assertCall(() -> tuple.get(0))
        .throwsException(new ObjectDbException(structHash, errorReadingFieldHashes()))
        .withCause(new DecodingHashSequenceException(fieldValuesHash, 2, 1));
  }

  @Test
  public void struct_with_too_many_fields_is_corrupted() throws Exception {
    Hash fieldValuesHash =
        hash(
            hash(string("John")),
            hash(string("Doe")),
            hash(string("junk")));
    Hash structHash =
        hash(
            hash(personType()),
            fieldValuesHash);
    Tuple tuple = (Tuple) objectDb().get(structHash);
    assertCall(() -> tuple.get(0))
        .throwsException(new ObjectDbException(structHash, errorReadingFieldHashes()))
        .withCause(new DecodingHashSequenceException(fieldValuesHash, 2, 3));
  }

  @Test
  public void struct_with_field_of_wrong_type_is_corrupted() throws Exception {
    Hash structHash =
        hash(
            hash(personType()),
            hash(
                hash(string("John")),
                hash(bool(true))));
    Tuple tuple = (Tuple) objectDb().get(structHash);
    assertCall(() -> tuple.get(0))
        .throwsException(new ObjectDbException(structHash, "Its type (Struct) specifies field " +
            "at index 1 with type STRING but its data has object of type BOOL at that index."));
  }

  private static String errorReadingFieldHashes() {
    return "Error reading field hashes.";
  }
}
