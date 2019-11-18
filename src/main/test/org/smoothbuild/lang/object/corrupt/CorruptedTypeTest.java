package org.smoothbuild.lang.object.corrupt;

import static org.smoothbuild.testing.common.ExceptionMatcher.exception;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.db.hashed.DecodingHashSequenceException;
import org.smoothbuild.db.hashed.DecodingStringException;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.object.db.ObjectsDbException;

import okio.ByteString;

public class CorruptedTypeTest extends AbstractCorruptedTestCase {
  private Hash hash;
  private Hash instanceHash;
  private Hash notStringHash;
  private Hash dataHash;

  @Test
  public void learning_test_create_array_type() {
    /*
     * This test makes sure that other tests in this class use proper scheme to save smooth
     * array type in HashedDb.
     */
    when(() -> instanceHash =
        hash(
            hash(typeType()),
            hash(
                hash(""),
                hash(stringType())
            )
        ));
    thenReturned(arrayType(stringType()).hash());
  }

  @Test
  public void learning_test_create_string_type() {
    /*
     * This test makes sure that other tests in this class use proper scheme to save basic smooth
     * type in HashedDb.
     */
    when(() ->
        hash(
            hash(typeType()),
            hash(
                hash("String"))));
    thenReturned(stringType().hash());
  }

  @Test
  public void learning_test_create_struct_type() {
    /*
     * This test makes sure that other tests in this class use proper scheme to save struct smooth
     * type in HashedDb.
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

  @Test
  public void learning_test_create_type_type() {
    /*
     * This test makes sure that other tests in this class use proper scheme to save smooth type
     * type in HashedDb.
     */
    when(() ->
        hash(
            hash(
                hash("Type"))));
    thenReturned(typeType().hash());
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
    when(() -> objectsDb().get(instanceHash));
    thenThrown(exception(new ObjectsDbException(instanceHash, null, null)));
  }

  @Test
  public void merkle_root_with_two_children_when_first_one_is_not_type_causes_exception()
      throws Exception {
    given(instanceHash =
        hash(
            hash("corrupted"),
            hash(
                hash("String"))));
    when(() -> objectsDb().get(instanceHash));
    thenThrown(ObjectsDbException.class);
  }

  @Test
  public void merkle_root_with_one_children_causes_exception() throws Exception {
    given(instanceHash =
        hash(
            hash(
                hash("String"))));
    when(() -> objectsDb().get(instanceHash));
    thenThrown(exception(brokenTypeTypeException(instanceHash)));
  }

  @Test
  public void merkle_tree_for_type_type_but_with_wrong_name_causes_exception() throws Exception {
    given(instanceHash =
        hash(
            hash(
                hash("TypeX"))));
    when(() -> objectsDb().get(instanceHash));
    thenThrown(exception(brokenTypeTypeException(instanceHash)));
  }

  // testing Merkle tree of name (type name or struct field name) node

  @Test
  public void merkle_tree_for_type_with_name_that_is_not_legal_utf8_sequence_causes_exception()
      throws Exception {
    given(() -> notStringHash = hash(ByteString.of((byte) -64)));
    when(instanceHash =
        hash(
            hash(typeType()),
            hash(
                notStringHash)));
    when(() -> objectsDb().get(instanceHash));
    thenThrown(exception(new ObjectsDbException(instanceHash,
        new DecodingStringException(notStringHash, null))));
  }

  @Test
  public void merkle_tree_for_struct_type_with_field_name_that_is_not_legal_utf8_sequence_causes_exception() {
    given(() -> notStringHash = hash(ByteString.of((byte) -64)));
    given(() -> instanceHash =
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
                        notStringHash,
                        hash(stringType()))
                ))));
    when(() -> objectsDb().get(instanceHash));
    thenThrown(exception(new ObjectsDbException(instanceHash,
        new DecodingStringException(notStringHash, null))));
  }

  // testing Merkle tree of type data

  @Test
  public void merkle_root_of_data_of_bool_type_with_two_children_causes_exception() {
    doTestCorruptedBasicType("Bool");
  }

  @Test
  public void merkle_root_of_data_of_string_type_with_two_children_causes_exception() {
    doTestCorruptedBasicType("String");
  }

  @Test
  public void merkle_root_of_data_of_blob_type_with_two_children_causes_exception() {
    doTestCorruptedBasicType("Blob");
  }

  @Test
  public void merkle_root_of_data_of_nothing_type_with_two_children_causes_exception() {
    doTestCorruptedBasicType("Nothing");
  }

  private void doTestCorruptedBasicType(String typeName) {
    when(() -> instanceHash =
        hash(
            hash(typeType()),
            hash(
                hash(typeName),
                hash("corrupted"))));
    when(() -> objectsDb().get(instanceHash));
    thenThrown(exception(new ObjectsDbException(instanceHash, "It is '" + typeName +
            "' type but its Merkle root has 2 children when 1 is expected.")));
  }

  @Test
  public void merkle_root_of_data_of_struct_type_with_one_children_causes_exception()
      throws Exception {
    when(instanceHash =
        hash(
            hash(typeType()),
            hash(
                hash("Person")
            )
        ));
    when(() -> objectsDb().get(instanceHash));
    thenThrown(exception(new ObjectsDbException(instanceHash,
        "It is 'Person' type but its Merkle root has 1 children when 2 is expected.")));
  }

  @Test
  public void merkle_root_of_data_of_array_type_with_one_children_causes_exception()
      throws Exception {
    when(instanceHash =
        hash(
            hash(typeType()),
            hash(
                hash("")
            )
        ));
    when(() -> objectsDb().get(instanceHash));
    thenThrown(exception(new ObjectsDbException(instanceHash,
        "It is '[]' type but its Merkle root has 1 children when 2 is expected.")));
  }

  @Test
  public void merkle_tree_of_struct_type_field_with_more_than_2_children_causes_exception() {
    given(() -> dataHash = hash(
        hash("firstName"),
        hash(stringType()),
        hash("corrupted")
    ));
    given(() -> instanceHash =
        hash(
            hash(typeType()),
            hash(
                hash("Person"),
                hash(
                    dataHash
                ))
        ));
    when(() -> objectsDb().get(instanceHash));
    thenThrown(exception(new ObjectsDbException(instanceHash,
        new DecodingHashSequenceException(dataHash, 2, 3))));
  }

  // corrupted dependent types

  @Test
  public void merkle_tree_of_struct_type_with_corrupted_field_type_causes_exception() {
    given(() -> hash = Hash.of("not a type"));
    given(() -> instanceHash =
        hash(
            hash(typeType()),
            hash(
                hash("Person"),
                hash(
                    hash(
                        hash("firstName"),
                        hash
                    ),
                    hash(
                        hash("lastName"),
                        hash(stringType()))
                ))));
    when(() -> objectsDb().get(instanceHash));
    thenThrown(exception(
        new ObjectsDbException(instanceHash, new ObjectsDbException(hash, (Exception) null))));
  }

  @Test
  public void merkle_tree_of_array_type_with_corrupted_element_type_causes_exception() {
    given(() -> hash = Hash.of("not a type"));
    given(() -> instanceHash =
        hash(
            hash(typeType()),
            hash(
                hash(""),
                hash
            )
        ));
    when(() -> objectsDb().get(instanceHash));
    thenThrown(exception(
        new ObjectsDbException(instanceHash, new ObjectsDbException(hash, (Exception) null))));
  }

  private static ObjectsDbException brokenTypeTypeException(Hash hash) {
    return new ObjectsDbException(hash, "Expected object which is instance of 'Type' type but " +
        "its Merkle tree has only one child (so it should be 'Type' type) " +
        "but it has different hash.");
  }
}
