package org.smoothbuild.lang.object.corrupt;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.common.AssertCall.assertCall;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.hashed.DecodingHashSequenceException;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.object.base.Struct;
import org.smoothbuild.lang.object.db.ObjectDbException;

public class CorruptedStructTest extends AbstractCorruptedTestCase {
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
    Struct struct = (Struct) objectDb().get(structHash);
    assertCall(() -> struct.get(0))
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
    Struct struct = (Struct) objectDb().get(structHash);
    assertCall(() -> struct.get(0))
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
    Struct struct = (Struct) objectDb().get(structHash);
    assertCall(() -> struct.get(0))
        .throwsException(new ObjectDbException(structHash, "Its type (Struct) specifies field " +
            "at index 1 with type Type(\"String\"):7561a6b22d5fe8e18dec31904e0e9cdf6644ca96 but " +
            "its data has object of type Type(\"Bool\"):912e97481a6f232997c26729f48c14d33540c9e1 " +
            "at that index."));
  }

  private static String errorReadingFieldHashes() {
    return "Error reading field hashes.";
  }
}
