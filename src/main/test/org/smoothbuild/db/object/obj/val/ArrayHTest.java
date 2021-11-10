package org.smoothbuild.db.object.obj.val;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.collect.Lists.list;

import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.db.object.obj.base.ValueH;
import org.smoothbuild.db.object.type.TestingTypesH;
import org.smoothbuild.db.object.type.base.TypeH;
import org.smoothbuild.db.object.type.base.TypeHV;
import org.smoothbuild.db.object.type.val.NothingTypeH;
import org.smoothbuild.testing.TestingContext;

import okio.ByteString;

public class ArrayHTest extends TestingContext {
  @Test
  public void empty_nothing_array_can_be_iterated_as_tuple() {
    ArrayH array = objectDb().arrayBuilder(nothingOT())
        .build();
    assertThat(array.elements(TupleH.class))
        .isEmpty();
  }

  @Test
  public void string_array_cannot_be_iterated_as_tuple() {
    ArrayH array = objectDb().arrayBuilder(stringOT())
        .add(string("abc"))
        .build();
    assertCall(() -> array.elements(TupleH.class))
        .throwsException(new IllegalArgumentException(
            "[String] cannot be viewed as Iterable of " + TupleH.class.getCanonicalName() + "."));
  }

  @Test
  public void empty_array_is_empty() {
    ArrayH array = objectDb().arrayBuilder(stringOT())
        .build();
    assertThat(array.elements(StringH.class))
        .isEmpty();
  }

  @Test
  public void adding_null_is_forbidden() {
    ArrayHBuilder arrayBuilder = objectDb().arrayBuilder(stringOT());
    assertCall(() -> arrayBuilder.add(null))
        .throwsException(NullPointerException.class);
  }

  @Test
  public void adding_element_with_wrong_type_is_forbidden() {
    ArrayHBuilder arrayBuilder = objectDb().arrayBuilder(stringOT());
    assertCall(() -> arrayBuilder.add(blob(ByteString.of())))
        .throwsException(IllegalArgumentException.class);
  }

  @Test
  public void array_contains_added_element() {
    ArrayH array = objectDb().arrayBuilder(stringOT())
        .add(string("abc"))
        .build();
    assertThat(array.elements(StringH.class))
        .containsExactly(string("abc"));
  }

  @Test
  public void array_contains_added_element_via_add_all_method() {
    StringH str = string("abc");
    StringH str2 = string("def");
    ArrayH array = objectDb().arrayBuilder(stringOT())
        .addAll(list(str, str2))
        .build();
    assertThat(array.elements(StringH.class))
        .containsExactly(str, str2)
        .inOrder();
  }

  @Test
  public void array_contains_added_elements_in_order() {
    StringH str1 = string("abc");
    StringH str2 = string("def");
    StringH str3 = string("ghi");
    ArrayH array = objectDb().arrayBuilder(stringOT())
        .add(str1)
        .add(str2)
        .add(str3)
        .build();
    assertThat(array.elements(StringH.class))
        .containsExactly(str1, str2, str3)
        .inOrder();
  }

  @Test
  public void adding_same_element_twice_builds_array_with_two_elements() {
    StringH str = string("abc");
    ArrayH array = objectDb().arrayBuilder(stringOT())
        .add(str)
        .add(str)
        .build();
    assertThat(array.elements(StringH.class))
        .containsExactly(str, str);
  }

  @Test
  public void arrays_with_same_elements_have_same_hash() {
    StringH str1 = string("abc");
    StringH str2 = string("def");
    ArrayH array = objectDb().arrayBuilder(stringOT())
        .add(str1)
        .add(str2)
        .build();
    ArrayH array2 = objectDb().arrayBuilder(stringOT())
        .add(str1)
        .add(str2)
        .build();
    assertThat(array.hash())
        .isEqualTo(array2.hash());
  }

  @Test
  public void one_element_array_hash_is_different_than_its_element_hash() {
    StringH str = string("abc");
    ArrayH array = objectDb().arrayBuilder(stringOT())
        .add(str)
        .build();
    assertThat(array.hash())
        .isNotEqualTo(str.hash());
  }

  @Test
  public void arrays_with_same_elements_but_in_different_order_have_different_hashes() {
    StringH str1 = string("abc");
    StringH str2 = string("def");
    ArrayH array = objectDb().arrayBuilder(stringOT())
        .add(str1)
        .add(str2)
        .build();
    ArrayH array2 = objectDb().arrayBuilder(stringOT())
        .add(str2)
        .add(str1)
        .build();
    assertThat(array.hash())
        .isNotEqualTo(array2.hash());
  }

  @Test
  public void array_with_one_more_element_have_different_hash() {
    StringH str1 = string("abc");
    StringH str2 = string("def");
    ArrayH array = objectDb().arrayBuilder(stringOT())
        .add(str1)
        .build();
    ArrayH array2 = objectDb().arrayBuilder(stringOT())
        .add(str1)
        .add(str2)
        .build();
    assertThat(array.hash())
        .isNotEqualTo(array2.hash());
  }

  @Test
  public void array_can_be_read_by_hash() {
    StringH str1 = string("abc");
    StringH str2 = string("def");
    ArrayH array = objectDb().arrayBuilder(stringOT())
        .add(str1)
        .add(str2)
        .build();
    assertThat(objectDbOther().get(array.hash()))
        .isEqualTo(array);
  }

  @Test
  public void array_read_by_hash_contains_same_elements() {
    StringH str1 = string("abc");
    StringH str2 = string("def");
    ArrayH array = objectDb().arrayBuilder(stringOT())
        .add(str1)
        .add(str2)
        .build();
    assertThat(((ArrayH) objectDbOther().get(array.hash())).elements(StringH.class))
        .containsExactly(str1, str2)
        .inOrder();
  }

  @Test
  public void array_read_by_hash_has_same_hash() {
    StringH str1 = string("abc");
    StringH str2 = string("def");
    ArrayH array = objectDb().arrayBuilder(stringOT())
        .add(str1)
        .add(str2)
        .build();
    assertThat(objectDbOther().get(array.hash()).hash())
        .isEqualTo(array.hash());
  }

  @ParameterizedTest
  @MethodSource("type_test_data")
  public void type(TypeHV type) {
    ArrayH array = objectDb().arrayBuilder(type).build();
    assertThat(array.type())
        .isEqualTo(arrayOT(type));
  }

  private static List<TypeH> type_test_data() {
    return TestingTypesH.TYPESV_TO_TEST;
  }

  @Test
  public void to_string() {
    StringH str1 = string("abc");
    StringH str2 = string("def");
    ArrayH array = objectDb().arrayBuilder(stringOT())
        .add(str1)
        .add(str2)
        .build();
    assertThat(array.toString())
        .isEqualTo("""
            ["abc","def"]@""" + array.hash());
  }

  @Nested
  class _nothing_array {
    @Test
    public void type_of_nothing_array_is_nothing_array() {
      ArrayH array = emptyArrayOf(nothingOT());
      assertThat(array.type())
          .isEqualTo(arrayOT(nothingOT()));
    }

    @Test
    public void nothing_array_is_empty() {
      assertThat(emptyArrayOf(nothingOT()).elements(ValueH.class))
          .isEmpty();
    }

    @Test
    public void nothing_array_can_be_read_by_hash() {
      ArrayH array = emptyArrayOf(nothingOT());
      assertThat(objectDbOther().get(array.hash()))
          .isEqualTo(array);
    }

    @Test
    public void nothing_array_read_by_hash_is_empty() {
      ArrayH array = emptyArrayOf(nothingOT());
      assertThat(((ArrayH) objectDbOther().get(array.hash())).elements(ValueH.class))
          .isEmpty();
    }

    @Test
    public void nothing_array_to_string() {
      ArrayH array = emptyArrayOf(nothingOT());
      assertThat(array.toString())
          .isEqualTo("[]@" + array.hash());
    }

    private ArrayH emptyArrayOf(NothingTypeH elemType) {
      return objectDb().arrayBuilder(elemType).build();
    }
  }
}
