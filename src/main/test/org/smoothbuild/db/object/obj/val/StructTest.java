package org.smoothbuild.db.object.obj.val;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.Lists.list;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.object.spec.val.StructSpec;
import org.smoothbuild.testing.TestingContext;

public class StructTest extends TestingContext {
  @Test
  public void creating_struct_with_rec_different_than_specified_in_its_spec_causes_exception() {
    assertCall(() -> animalVal(recVal(list(strVal()))))
        .throwsException(IllegalArgumentException.class);
  }

  @Test
  public void reading_struct_spec() {
    assertThat(animalVal().spec())
        .isEqualTo(animalSpec());
  }

  @Test
  public void rec_contains_object_passed_to_builder() {
    Rec rec = animalRecVal();
    Struc_ struct = animalVal(rec);
    assertThat(struct.rec())
        .isEqualTo(rec);
  }

  @Test
  public void struct_hash_is_different_of_its_rec_hash() {
    Struc_ struct = animalVal(animalRecVal());
    assertThat(struct.hash())
        .isNotEqualTo(animalRecVal().hash());
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

  private Struc_ animalVal() {
    return animalVal("abc", 7);
  }

  private Struc_ animalVal(String name, int speed) {
    return animalVal(animalRecVal(name, speed));
  }

  private Struc_ animalVal(Rec rec) {
    return structVal(animalSpec(), rec);
  }

  private StructSpec animalSpec() {
    return structSpec(list(strSpec(), intSpec()), list("name", "speed"));
  }

  private Rec animalRecVal() {
    return animalRecVal("rabbit", 7);
  }

  private Rec animalRecVal(String name, int speed) {
    return recVal(list(strVal(name), intVal(speed)));
  }

  @Test
  public void to_string() {
    Struc_ struct = animalVal("rabbit", 7);
    assertThat(struct.toString())
        .isEqualTo("""
            {name="rabbit",speed=7}@""" + struct.hash());
  }
}
