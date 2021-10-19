package org.smoothbuild.db.object.obj.val;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.Lists.list;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestingContextImpl;

public class StructTest extends TestingContextImpl {
  @Test
  public void creating_struct_with_item_spec_different_than_specified_in_its_spec_causes_exception() {
    assertCall(() -> structVal(animalSpec(), list(strVal("rabbit"), strVal("7"))))
        .throwsException(IllegalArgumentException.class);
  }

  @Test
  public void creating_struct_with_item_count_different_than_specified_in_its_spec_causes_exception() {
    assertCall(() -> structVal(animalSpec(), list(strVal("rabbit"))))
        .throwsException(IllegalArgumentException.class);
  }

  @Test
  public void reading_struct_spec() {
    assertThat(animalVal().spec())
        .isEqualTo(animalSpec());
  }

  @Test
  public void items_contains_object_passed_to_builder() {
    Str species = strVal("rabbit");
    Int speed = intVal(7);
    Struc_ struct = structVal(animalSpec(), list(species, speed));
    assertThat(struct.items())
        .isEqualTo(list(species, speed));
  }

  @Test
  public void struct_hash_is_different_than_its_item_hash() {
    Str species = strVal("rabbit");
    Int speed = intVal(7);
    Struc_ struct = structVal(animalSpec(), list(species, speed));
    assertThat(struct.hash())
        .isNotEqualTo(species.hash());
  }

  @Test
  public void structs_with_equal_items_are_equal() {
    assertThat(animalVal())
        .isEqualTo(animalVal());
  }

  @Test
  public void structs_with_one_item_different_are_not_equal() {
    assertThat(animalVal("abc", 7))
        .isNotEqualTo(animalVal("abc", 9));
  }

  @Test
  public void structs_with_equal_items_have_equal_hashes() {
    assertThat(animalVal().hash())
        .isEqualTo(animalVal().hash());
  }

  @Test
  public void structs_with_different_item_have_different_hashes() {
    assertThat(animalVal("abc", 7).hash())
        .isNotEqualTo(animalVal("abc", 9).hash());
  }

  @Test
  public void structs_with_equal_items_have_equal_hash_codes() {
    assertThat(animalVal().hashCode())
        .isEqualTo(animalVal().hashCode());
  }

  @Test
  public void structs_with_different_item_have_different_hash_codes() {
    assertThat(animalVal("abc", 7).hashCode())
        .isNotEqualTo(animalVal("abc", 9).hashCode());
  }

  @Test
  public void structs_can_be_read_by_hash() {
    Struc_ struct = animalVal();
    assertThat(objectDbOther().get(struct.hash()))
        .isEqualTo(struct);
  }

  @Test
  public void to_string() {
    Struc_ struct = animalVal("rabbit", 7);
    assertThat(struct.toString())
        .isEqualTo("""
            {species="rabbit",speed=7}@""" + struct.hash());
  }
}
