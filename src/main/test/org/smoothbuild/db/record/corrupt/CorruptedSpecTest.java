package org.smoothbuild.db.record.corrupt;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.db.record.spec.SpecKind.ARRAY;
import static org.smoothbuild.db.record.spec.SpecKind.BLOB;
import static org.smoothbuild.db.record.spec.SpecKind.BOOL;
import static org.smoothbuild.db.record.spec.SpecKind.NOTHING;
import static org.smoothbuild.db.record.spec.SpecKind.SPEC;
import static org.smoothbuild.db.record.spec.SpecKind.STRING;
import static org.smoothbuild.db.record.spec.SpecKind.TUPLE;
import static org.smoothbuild.testing.common.AssertCall.assertCall;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.record.db.RecordDbException;
import org.smoothbuild.db.record.spec.SpecKind;

public class CorruptedSpecTest extends AbstractCorruptedTestCase {
  @Test
  public void learning_test_create_array_spec() throws Exception {
    /*
     * This test makes sure that other tests in this class use proper scheme to save smooth
     * array spec in HashedDb.
     */
    assertThat(
        hash(
            hash(specSpec()),
            hash(
                hash(ARRAY.marker()),
                hash(stringSpec())
            )
        ))
        .isEqualTo(arraySpec(stringSpec()).hash());
  }

  @Test
  public void learning_test_create_string_spec() throws Exception {
    /*
     * This test makes sure that other tests in this class use proper scheme to save basic smooth
     * spec in HashedDb.
     */
    assertThat(
        hash(
            hash(specSpec()),
            hash(
                hash(STRING.marker()))))
        .isEqualTo(stringSpec().hash());
  }

  @Test
  public void learning_test_create_tuple_spec() throws Exception {
    /*
     * This test makes sure that other tests in this class use proper scheme to save tuple smooth
     * spec in HashedDb.
     */
    assertThat(
        hash(
            hash(specSpec()),
            hash(
                hash(TUPLE.marker()),
                hash(
                    hash(stringSpec()),
                    hash(stringSpec())
                ))))
        .isEqualTo(personSpec().hash());
  }

  @Test
  public void learning_test_create_spec_spec() throws Exception {
    /*
     * This test makes sure that other tests in this class use proper scheme to save smooth spec
     * spec in HashedDb.
     */
    assertThat(
        hash(
            hash(
                hash(SPEC.marker()))))
        .isEqualTo(specSpec().hash());
  }

  // testing Merkle tree of spec root node

  @Test
  public void merkle_root_with_three_children_causes_exception() throws Exception {
    Hash recordHash =
        hash(
            hash(specSpec()),
            hash(
                hash(STRING.marker())),
            hash("corrupted"));
    assertCall(() -> recordDb().get(recordHash))
        .throwsException(new RecordDbException(recordHash, null, null));
  }

  @Test
  public void merkle_root_with_two_children_when_first_one_is_not_spec_causes_exception()
      throws Exception {
    Hash recordHash =
        hash(
            hash("corrupted"),
            hash(
                hash("String")));
    assertCall(() -> recordDb().get(recordHash))
        .throwsException(RecordDbException.class);
  }

  @Test
  public void merkle_root_with_one_children_causes_exception() throws Exception {
    Hash recordHash =
        hash(
            hash(
                hash("String")));
    assertCall(() -> recordDb().get(recordHash))
        .throwsException(brokenSpecSpecException(recordHash));
  }

  @Test
  public void merkle_tree_for_spec_spec_but_with_wrong_marker_causes_exception() throws Exception {
    Hash recordHash =
        hash(
            hash(
                hash((byte) 33)));
    assertCall(() -> recordDb().get(recordHash))
        .throwsException(brokenSpecSpecException(recordHash));
  }

  // testing Merkle tree of name (spec name or tuple element name) node

  @Test
  public void merkle_tree_for_spec_with_name_that_is_not_known_spec()
      throws Exception {
    Hash recordHash =
        hash(
            hash(specSpec()),
            hash(
                hash((byte) 33)));
    assertCall(() -> recordDb().get(recordHash))
        .throwsException(new RecordDbException(
            recordHash, "Its spec == SPEC but its data has illegal SpecKind marker = 33."));
  }

  // testing Merkle tree of spec data

  @ParameterizedTest
  @MethodSource("basicSpecs")
  public void corrupted_basic_spec(SpecKind specKind) throws Exception {
    Hash recordHash =
        hash(
            hash(specSpec()),
            hash(
                hash(specKind.marker()),
                hash("corrupted")));
    assertCall(() -> recordDb().get(recordHash))
        .throwsException(new RecordDbException(recordHash, brokenSpecMessage(specKind, 2, 1)));
  }

  private static Stream<Arguments> basicSpecs() {
    return Stream.of(
        Arguments.of(NOTHING),
        Arguments.of(BLOB),
        Arguments.of(BOOL),
        Arguments.of(STRING)
    );
  }

  @Test
  public void tuple_spec_with_merkle_tree_with_data_with_one_children_causes_exception()
      throws Exception {
    Hash recordHash =
        hash(
            hash(specSpec()),
            hash(
                hash(TUPLE.marker())));
    assertCall(() -> recordDb().get(recordHash))
        .throwsException(new RecordDbException(recordHash, brokenSpecMessage(TUPLE, 1, 2)));
  }

  @Test
  public void tuple_spec_with_merkle_tree_with_data_with_second_children_not_being_array_causes_exception()
      throws Exception {
    Hash recordHash =
        hash(
            hash(specSpec()),
            hash(
                hash(TUPLE.marker()),
                hash(stringSpec())
                ));
    assertCall(() -> recordDb().get(recordHash))
        .throwsException(new RecordDbException(recordHash, elementReadingErrorMessage(1)));
  }

  @Test
  public void tuple_spec_with_merkle_tree_with_data_with_second_children_being_array_of_non_spec_causes_exception()
      throws Exception {
    Hash stringHash = hash(string("abc"));
    Hash recordHash =
        hash(
            hash(specSpec()),
            hash(
                hash(TUPLE.marker()),
                hash(
                    stringHash
                )));
    assertCall(() -> recordDb().get(recordHash))
        .throwsException(new RecordDbException(recordHash, elementReadingErrorMessage(0)))
        .withCause(new RecordDbException(stringHash,
            "Expected record which spec == SPEC but its Merkle tree's first child is not SPEC."));
  }

  @Test
  public void merkle_root_of_data_of_array_spec_with_one_children_causes_exception()
      throws Exception {
    Hash recordHash =
        hash(
            hash(specSpec()),
            hash(
                hash(ARRAY.marker())));
    assertCall(() -> recordDb().get(recordHash))
        .throwsException(new RecordDbException(recordHash, brokenSpecMessage(ARRAY, 1, 2)));
  }

  // corrupted dependent specs

  @Test
  public void merkle_tree_of_tuple_spec_with_corrupted_element_spec_causes_exception()
      throws Exception {
    Hash hash = Hash.of("not a spec");
    Hash recordHash =
        hash(
            hash(specSpec()),
            hash(
                hash(TUPLE.marker()),
                hash(
                    hash,
                    hash(stringSpec()))));
    assertCall(() -> recordDb().get(recordHash))
        .throwsException(new RecordDbException(recordHash, elementReadingErrorMessage(0)))
        .withCause(new RecordDbException(hash));
  }

  @Test
  public void merkle_tree_of_array_spec_with_corrupted_element_spec_causes_exception()
      throws Exception {
    Hash hash = Hash.of("not a type");
    Hash recordHash =
        hash(
            hash(specSpec()),
            hash(
                hash(ARRAY.marker()),
                hash));
    assertCall(() -> recordDb().get(recordHash))
        .throwsException(new RecordDbException(recordHash))
        .withCause(new RecordDbException(hash));
  }

  private static RecordDbException brokenSpecSpecException(Hash hash) {
    return new RecordDbException(hash, "Expected record which spec == SPEC. Its Merkle tree has "
        + "only one child (so the record itself should be == SPEC) but it has a different hash.");
  }

  private static String brokenSpecMessage(SpecKind specKind, int actual, int expected) {
    return "Its spec == SPEC and specKind == " + specKind + " but its dataHash has " + actual
        + " children when " + expected + " is expected.";
  }

  private static String elementReadingErrorMessage(int index) {
    return "Its spec == SPEC, its specKind == TUPLE but reading element spec at index " + index
        + " caused error.";
  }
}
