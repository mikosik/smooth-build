package org.smoothbuild.db.values.corrupt;

import static org.smoothbuild.db.values.ValuesDbException.corruptedValueException;
import static org.smoothbuild.testing.common.ExceptionMatcher.exception;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.value.Struct;

public class CorruptedStructTest extends AbstractCorruptedTestCase {
  private Hash structHash;
  private Struct struct;

  @Test
  public void learning_test_create_struct() throws Exception {
    /*
     * This test makes sure that other tests in this class use proper scheme to save smooth struct
     * in HashedDb.
     */
    when(() ->
        hash(
            hash(personType()),
            hash(
                hash(string("John")),
                hash(string("Doe")))));
    thenReturned(person("John", "Doe").hash());
  }

  @Test
  public void struct_with_too_few_fields_is_corrupted() throws Exception {
    given(structHash =
        hash(
            hash(personType()),
            hash(
                hash(string("John")))));
    given(struct = (Struct) valuesDb().get(structHash));
    when(() -> struct.get("firstName"));
    thenThrown(exception(corruptedValueException(structHash, "Its type is "
        + "Type(\"Person\"):e0eb7c56051fe435ef4b5de175ff78599ea65186 with 2 fields but "
        + "its data hash Merkle tree contains 1 children.")));
  }

  @Test
  public void struct_with_too_many_fields_is_corrupted() throws Exception {
    given(structHash =
        hash(
            hash(personType()),
            hash(
                hash(string("John")),
                hash(string("Doe")),
                hash(string("junk")))));
    given(struct = (Struct) valuesDb().get(structHash));
    when(() -> struct.get("firstName"));
    thenThrown(exception(corruptedValueException(structHash, "Its type is "
        + "Type(\"Person\"):e0eb7c56051fe435ef4b5de175ff78599ea65186 with 2 fields but "
        + "its data hash Merkle tree contains 3 children.")));
  }

  @Test
  public void struct_with_field_of_wrong_type_is_corrupted() throws Exception {
    given(structHash =
        hash(
            hash(personType()),
            hash(
                hash(string("John")),
                hash(bool(true)))));
    given(struct = (Struct) valuesDb().get(structHash));
    when(() -> struct.get("firstName"));
    thenThrown(exception(corruptedValueException(structHash, "Its type specifies field 'lastName' "
        + "with type Type(\"String\"):7561a6b22d5fe8e18dec31904e0e9cdf6644ca96 but its data "
        + "has value of type Type(\"Bool\"):912e97481a6f232997c26729f48c14d33540c9e1 assigned "
        + "to that field.")));
  }
}
