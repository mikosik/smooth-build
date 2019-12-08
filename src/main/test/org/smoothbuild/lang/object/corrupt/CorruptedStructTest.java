package org.smoothbuild.lang.object.corrupt;

import static org.smoothbuild.testing.common.ExceptionMatcher.exception;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.db.hashed.DecodingHashSequenceException;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.object.base.Struct;
import org.smoothbuild.lang.object.db.ObjectDbException;

public class CorruptedStructTest extends AbstractCorruptedTestCase {
  private Hash structHash;
  private Struct struct;
  private Hash fieldValuesHash;

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
  public void struct_with_too_few_fields_is_corrupted() {
    given(() -> fieldValuesHash =
        hash(
          hash(string("John"))));
    given(() -> structHash =
        hash(
            hash(personType()),
            fieldValuesHash));
    given(struct = (Struct) objectDb().get(structHash));
    when(() -> struct.get("firstName"));
    thenThrown(exception(new ObjectDbException(structHash,
        new DecodingHashSequenceException(fieldValuesHash, 2, 1))));
  }

  @Test
  public void struct_with_too_many_fields_is_corrupted() throws Exception {
    given(() -> fieldValuesHash =
        hash(
            hash(string("John")),
            hash(string("Doe")),
            hash(string("junk"))));
    given(() -> structHash =
        hash(
            hash(personType()),
            fieldValuesHash));
    given(struct = (Struct) objectDb().get(structHash));
    when(() -> struct.get("firstName"));
    thenThrown(exception(new ObjectDbException(structHash,
        new DecodingHashSequenceException(fieldValuesHash, 2, 3))));
  }

  @Test
  public void struct_with_field_of_wrong_type_is_corrupted() throws Exception {
    given(structHash =
        hash(
            hash(personType()),
            hash(
                hash(string("John")),
                hash(bool(true)))));
    given(struct = (Struct) objectDb().get(structHash));
    when(() -> struct.get("firstName"));
    thenThrown(exception(new ObjectDbException(structHash, "Its type specifies field 'lastName' "
        + "with type Type(\"String\"):7561a6b22d5fe8e18dec31904e0e9cdf6644ca96 but its data "
        + "has object of type Type(\"Bool\"):912e97481a6f232997c26729f48c14d33540c9e1 assigned "
        + "to that field.")));
  }
}
