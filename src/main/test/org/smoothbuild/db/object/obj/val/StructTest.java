package org.smoothbuild.db.object.obj.val;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.collect.Lists.list;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestingContextImpl;

public class StructTest extends TestingContextImpl {
  @Test
  public void creating_struct_with_item_type_different_than_specified_in_its_type_causes_exception() {
    assertCall(() -> struct(animalSpec(), list(string("rabbit"), string("7"))))
        .throwsException(IllegalArgumentException.class);
  }

  @Test
  public void creating_struct_with_item_count_different_than_specified_in_its_type_causes_exception() {
    assertCall(() -> struct(animalSpec(), list(string("rabbit"))))
        .throwsException(IllegalArgumentException.class);
  }

  @Test
  public void reading_struct_type() {
    assertThat(animal().type())
        .isEqualTo(animalSpec());
  }

  @Test
  public void items_contains_object_passed_to_builder() {
    Str species = string("rabbit");
    Int speed = int_(7);
    Struc_ struct = struct(animalSpec(), list(species, speed));
    assertThat(struct.items())
        .isEqualTo(list(species, speed));
  }

  @Test
  public void struct_hash_is_different_than_its_item_hash() {
    Str species = string("rabbit");
    Int speed = int_(7);
    Struc_ struct = struct(animalSpec(), list(species, speed));
    assertThat(struct.hash())
        .isNotEqualTo(species.hash());
  }

  @Test
  public void structs_with_equal_items_are_equal() {
    assertThat(animal())
        .isEqualTo(animal());
  }

  @Test
  public void structs_with_one_item_different_are_not_equal() {
    assertThat(animal("abc", 7))
        .isNotEqualTo(animal("abc", 9));
  }

  @Test
  public void structs_with_equal_items_have_equal_hashes() {
    assertThat(animal().hash())
        .isEqualTo(animal().hash());
  }

  @Test
  public void structs_with_different_item_have_different_hashes() {
    assertThat(animal("abc", 7).hash())
        .isNotEqualTo(animal("abc", 9).hash());
  }

  @Test
  public void structs_with_equal_items_have_equal_hash_codes() {
    assertThat(animal().hashCode())
        .isEqualTo(animal().hashCode());
  }

  @Test
  public void structs_with_different_item_have_different_hash_codes() {
    assertThat(animal("abc", 7).hashCode())
        .isNotEqualTo(animal("abc", 9).hashCode());
  }

  @Test
  public void structs_can_be_read_by_hash() {
    Struc_ struct = animal();
    assertThat(objectDbOther().get(struct.hash()))
        .isEqualTo(struct);
  }

  @Test
  public void to_string() {
    Struc_ struct = animal("rabbit", 7);
    assertThat(struct.toString())
        .isEqualTo("""
            {species="rabbit",speed=7}@""" + struct.hash());
  }
}
