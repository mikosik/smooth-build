package org.smoothbuild.lang.type;

import static org.smoothbuild.db.values.ValuesDbException.corruptedValueException;
import static org.smoothbuild.testing.common.ExceptionMatcher.exception;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.values.ValuesDbException;
import org.smoothbuild.db.values.corrupt.AbstractCorruptedTestCase;

import okio.ByteString;

public class CorruptedTypeTest extends AbstractCorruptedTestCase {
  protected ConcreteType type;
  protected Hash hash;
  private Hash instanceHash;

  @Test
  public void learning_test_create_string_type() throws Exception {
    /*
     * This test makes sure that other tests in this class use proper scheme to save smooth type in
     * HashedDb.
     */
    when(() ->
        hash(
            hash(typeType()),
            hash(
                hash("String"))));
    thenReturned(stringType().hash());
  }

  @Test
  public void learning_test_create_type_type() throws Exception {
    /*
     * This test makes sure that other tests in this class use proper scheme to save smooth type in
     * HashedDb.
     */
    when(() ->
        hash(
            hash(
                hash("Type"))));
    thenReturned(typeType().hash());
  }

  @Test
  public void learning_test_create_struct_type() throws Exception {
    /*
     * This test makes sure that other tests in this class use proper scheme to save smooth type in
     * HashedDb.
     */
    when(() ->
        hash(
            hash(typeType()),
            hash(
                hash("Person"),
                hash(
                    hash(
                        hash("firstName"),
                        hash(stringType())
                    ),
                    hash(
                        hash("lastName"),
                        hash(stringType()))
                ))));
    thenReturned(personType().hash());
  }

  // testing Merkle tree of type root node

  @Test
  public void merkle_root_with_three_children_causes_exception() throws Exception {
    given(instanceHash =
        hash(
            hash(typeType()),
            hash(
                hash("String")),
            hash("corrupted")
        ));
    when(() -> valuesDb().get(instanceHash));
    thenThrown(
        exception(corruptedValueException(instanceHash, "Its Merkle tree root has 3 children.")));
  }

  @Test
  public void merkle_root_with_two_children_when_first_one_is_not_type_causes_exception()
      throws Exception {
    given(instanceHash =
        hash(
            hash("corrupted"),
            hash(
                hash("String"))));
    when(() -> valuesDb().get(instanceHash));
    thenThrown(ValuesDbException.class);
  }

  @Test
  public void merkle_root_with_one_children_causes_exception() throws Exception {
    given(instanceHash =
        hash(
            hash(
                hash("String"))));
    when(() -> valuesDb().get(instanceHash));
    thenThrown(exception(brokenTypeTypeException(instanceHash)));
  }

  @Test
  public void merkle_tree_for_type_type_but_with_wrong_name_causes_exception() throws Exception {
    given(instanceHash =
        hash(
            hash(
                hash("TypeX"))));
    when(() -> valuesDb().get(instanceHash));
    thenThrown(exception(brokenTypeTypeException(instanceHash)));
  }

  // testing Merkle tree of name (type name or struct field name) node

  @Test
  public void merkle_tree_for_type_with_name_that_is_not_legal_utf8_sequence_causes_exception()
      throws Exception {
    when(instanceHash =
        hash(
            hash(typeType()),
            hash(
                hash(ByteString.of((byte) -64)))));
    when(() -> valuesDb().get(instanceHash));
    thenThrown(exception(corruptedValueException(instanceHash,
        "It is an instance of a Type which name cannot be decoded using UTF-8 encoding.")));
  }

  @Test
  public void merkle_tree_for_struct_type_with_field_name_that_is_not_legal_utf8_sequence_causes_exception() throws
      Exception {
    when(instanceHash =
        hash(
            hash(typeType()),
            hash(
                hash("Person"),
                hash(
                    hash(
                        hash("firstName"),
                        hash(stringType())
                    ),
                    hash(
                        hash(ByteString.of((byte) -64)),
                        hash(stringType()))
                ))));
    when(() -> valuesDb().get(instanceHash));
    thenThrown(exception(corruptedValueException(instanceHash, "It is an instance of a struct " +
        "Type which field name cannot be decoded using UTF-8 encoding.")));
  }

  // testing Merkle tree of type data

  @Test
  public void merkle_root_of_data_of_bool_type_with_two_children_causes_exception()
      throws Exception {
    doTestCorruptedBasicType("Bool");
  }

  @Test
  public void merkle_root_of_data_of_string_type_with_two_children_causes_exception()
      throws Exception {
    doTestCorruptedBasicType("String");
  }

  @Test
  public void merkle_root_of_data_of_blob_type_with_two_children_causes_exception()
      throws Exception {
    doTestCorruptedBasicType("Blob");
  }

  @Test
  public void merkle_root_of_data_of_nothing_type_with_two_children_causes_exception()
      throws Exception {
    doTestCorruptedBasicType("Nothing");
  }

  private void doTestCorruptedBasicType(String typeName) throws IOException {
    when(instanceHash =
        hash(
            hash(typeType()),
            hash(
                hash(typeName),
                hash("corrupted"))));
    when(() -> valuesDb().get(instanceHash));
    thenThrown(exception(corruptedValueException(instanceHash,
        "It is " + typeName + " type but its Merkle tree has unnecessary children.")));
  }

  @Test
  public void merkle_root_of_data_of_struct_type_with_one_children_causes_exception()
      throws Exception {
    when(instanceHash =
        hash(
            hash(typeType()),
            hash(
                hash("Person"),
                hash(
                    hash(
                        hash("firstName"),
                        hash(stringType())
                    ),
                    hash(
                        hash("lastName"),
                        hash(stringType()))
                ),
                hash("corrupted"))
        ));
    when(() -> valuesDb().get(instanceHash));
    thenThrown(exception(corruptedValueException(instanceHash,
        "It is struct type but its Merkle tree has unnecessary children.")));
  }

  @Test
  public void merkle_tree_of_struct_type_field_with_more_than_2_children_causes_exception()
      throws Exception {
    when(instanceHash =
        hash(
            hash(typeType()),
            hash(
                hash("Person"),
                hash(
                    hash(
                        hash("firstName"),
                        hash(stringType()),
                        hash("corrupted")
                    )
                ))
        ));
    when(() -> valuesDb().get(instanceHash));
    thenThrown(exception(corruptedValueException(instanceHash,
        "It is struct type but one of its field hashes doesn't have two children but 3.")));
  }

  private static ValuesDbException brokenTypeTypeException(Hash hash) {
    return corruptedValueException(hash, "Expected value which is instance of 'Type' but its Merkle"
        + " tree has only one child (so it should be Type type) but it has different hash.");
  }
}
