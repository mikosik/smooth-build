package org.smoothbuild.db.object.obj.val;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.Lists.list;

import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.db.object.obj.base.Val;
import org.smoothbuild.db.object.spec.TestingSpecs;
import org.smoothbuild.db.object.spec.base.Spec;
import org.smoothbuild.db.object.spec.base.ValSpec;
import org.smoothbuild.db.object.spec.val.NothingSpec;
import org.smoothbuild.testing.TestingContext;

import okio.ByteString;

public class ArrayTest extends TestingContext {
  @Test
  public void empty_nothing_array_can_be_iterated_as_rec() {
    Array array = objectDb().arrayBuilder(nothingSpec())
        .build();
    assertThat(array.elements(Rec.class))
        .isEmpty();
  }

  @Test
  public void string_array_cannot_be_iterated_as_rec() {
    Array array = objectDb().arrayBuilder(strSpec())
        .add(strVal("abc"))
        .build();
    assertCall(() -> array.elements(Rec.class))
        .throwsException(new IllegalArgumentException(
            "[STRING] cannot be viewed as Iterable of " + Rec.class.getCanonicalName() + "."));
  }

  @Test
  public void empty_array_is_empty() {
    Array array = objectDb().arrayBuilder(strSpec())
        .build();
    assertThat(array.elements(Str.class))
        .isEmpty();
  }

  @Test
  public void adding_null_is_forbidden() {
    ArrayBuilder arrayBuilder = objectDb().arrayBuilder(strSpec());
    assertCall(() -> arrayBuilder.add(null))
        .throwsException(NullPointerException.class);
  }

  @Test
  public void adding_element_with_wrong_spec_is_forbidden() {
    ArrayBuilder arrayBuilder = objectDb().arrayBuilder(strSpec());
    assertCall(() -> arrayBuilder.add(blobVal(ByteString.of())))
        .throwsException(IllegalArgumentException.class);
  }

  @Test
  public void array_contains_added_element() {
    Array array = objectDb().arrayBuilder(strSpec())
        .add(strVal("abc"))
        .build();
    assertThat(array.elements(Str.class))
        .containsExactly(strVal("abc"));
  }

  @Test
  public void array_contains_added_element_via_add_all_method() {
    Str str = strVal("abc");
    Str str2 = strVal("def");
    Array array = objectDb().arrayBuilder(strSpec())
        .addAll(list(str, str2))
        .build();
    assertThat(array.elements(Str.class))
        .containsExactly(str, str2)
        .inOrder();
  }

  @Test
  public void array_contains_added_elements_in_order() {
    Str str1 = strVal("abc");
    Str str2 = strVal("def");
    Str str3 = strVal("ghi");
    Array array = objectDb().arrayBuilder(strSpec())
        .add(str1)
        .add(str2)
        .add(str3)
        .build();
    assertThat(array.elements(Str.class))
        .containsExactly(str1, str2, str3)
        .inOrder();
  }

  @Test
  public void adding_same_element_twice_builds_array_with_two_elements() {
    Str str = strVal("abc");
    Array array = objectDb().arrayBuilder(strSpec())
        .add(str)
        .add(str)
        .build();
    assertThat(array.elements(Str.class))
        .containsExactly(str, str);
  }

  @Test
  public void arrays_with_same_elements_have_same_hash() {
    Str str1 = strVal("abc");
    Str str2 = strVal("def");
    Array array = objectDb().arrayBuilder(strSpec())
        .add(str1)
        .add(str2)
        .build();
    Array array2 = objectDb().arrayBuilder(strSpec())
        .add(str1)
        .add(str2)
        .build();
    assertThat(array.hash())
        .isEqualTo(array2.hash());
  }

  @Test
  public void one_element_array_hash_is_different_than_its_element_hash() {
    Str str = strVal("abc");
    Array array = objectDb().arrayBuilder(strSpec())
        .add(str)
        .build();
    assertThat(array.hash())
        .isNotEqualTo(str.hash());
  }

  @Test
  public void arrays_with_same_elements_but_in_different_order_have_different_hashes() {
    Str str1 = strVal("abc");
    Str str2 = strVal("def");
    Array array = objectDb().arrayBuilder(strSpec())
        .add(str1)
        .add(str2)
        .build();
    Array array2 = objectDb().arrayBuilder(strSpec())
        .add(str2)
        .add(str1)
        .build();
    assertThat(array.hash())
        .isNotEqualTo(array2.hash());
  }

  @Test
  public void array_with_one_more_element_have_different_hash() {
    Str str1 = strVal("abc");
    Str str2 = strVal("def");
    Array array = objectDb().arrayBuilder(strSpec())
        .add(str1)
        .build();
    Array array2 = objectDb().arrayBuilder(strSpec())
        .add(str1)
        .add(str2)
        .build();
    assertThat(array.hash())
        .isNotEqualTo(array2.hash());
  }

  @Test
  public void array_can_be_read_by_hash() {
    Str str1 = strVal("abc");
    Str str2 = strVal("def");
    Array array = objectDb().arrayBuilder(strSpec())
        .add(str1)
        .add(str2)
        .build();
    assertThat(objectDbOther().get(array.hash()))
        .isEqualTo(array);
  }

  @Test
  public void array_read_by_hash_contains_same_elements() {
    Str str1 = strVal("abc");
    Str str2 = strVal("def");
    Array array = objectDb().arrayBuilder(strSpec())
        .add(str1)
        .add(str2)
        .build();
    assertThat(((Array) objectDbOther().get(array.hash())).elements(Str.class))
        .containsExactly(str1, str2)
        .inOrder();
  }

  @Test
  public void array_read_by_hash_has_same_hash() {
    Str str1 = strVal("abc");
    Str str2 = strVal("def");
    Array array = objectDb().arrayBuilder(strSpec())
        .add(str1)
        .add(str2)
        .build();
    assertThat(objectDbOther().get(array.hash()).hash())
        .isEqualTo(array.hash());
  }

  @ParameterizedTest
  @MethodSource("spec_test_data")
  public void spec(ValSpec spec) {
    Array array = objectDb().arrayBuilder(spec).build();
    assertThat(array.spec())
        .isEqualTo(arraySpec(spec));
  }

  private static List<Spec> spec_test_data() {
    return TestingSpecs.VAL_SPECS_TO_TEST;
  }

  @Test
  public void to_string() {
    Str str1 = strVal("abc");
    Str str2 = strVal("def");
    Array array = objectDb().arrayBuilder(strSpec())
        .add(str1)
        .add(str2)
        .build();
    assertThat(array.toString())
        .isEqualTo("""
            ["abc","def"]:""" + array.hash());
  }

  @Nested
  class _nothing_array {
    @Test
    public void spec_of_nothing_array_is_nothing_array() {
      Array array = emptyArrayOf(nothingSpec());
      assertThat(array.spec())
          .isEqualTo(arraySpec(nothingSpec()));
    }

    @Test
    public void nothing_array_is_empty() {
      assertThat(emptyArrayOf(nothingSpec()).elements(Val.class))
          .isEmpty();
    }

    @Test
    public void nothing_array_can_be_read_by_hash() {
      Array array = emptyArrayOf(nothingSpec());
      assertThat(objectDbOther().get(array.hash()))
          .isEqualTo(array);
    }

    @Test
    public void nothing_array_read_by_hash_is_empty() {
      Array array = emptyArrayOf(nothingSpec());
      assertThat(((Array) objectDbOther().get(array.hash())).elements(Val.class))
          .isEmpty();
    }

    @Test
    public void nothing_array_to_string() {
      Array array = emptyArrayOf(nothingSpec());
      assertThat(array.toString())
          .isEqualTo("[]:" + array.hash());
    }

    private Array emptyArrayOf(NothingSpec elemSpec) {
      return objectDb().arrayBuilder(elemSpec).build();
    }
  }
}
