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
    ArrayH array = objectHDb().arrayBuilder(nothingHT())
        .build();
    assertThat(array.elems(TupleH.class))
        .isEmpty();
  }

  @Test
  public void string_array_cannot_be_iterated_as_tuple() {
    ArrayH array = objectHDb().arrayBuilder(stringHT())
        .add(stringH("abc"))
        .build();
    assertCall(() -> array.elems(TupleH.class))
        .throwsException(new IllegalArgumentException(
            "[String] cannot be viewed as Iterable of " + TupleH.class.getCanonicalName() + "."));
  }

  @Test
  public void empty_array_is_empty() {
    ArrayH array = objectHDb().arrayBuilder(stringHT())
        .build();
    assertThat(array.elems(StringH.class))
        .isEmpty();
  }

  @Test
  public void adding_null_is_forbidden() {
    ArrayHBuilder arrayBuilder = objectHDb().arrayBuilder(stringHT());
    assertCall(() -> arrayBuilder.add(null))
        .throwsException(NullPointerException.class);
  }

  @Test
  public void adding_elem_with_wrong_type_is_forbidden() {
    ArrayHBuilder arrayBuilder = objectHDb().arrayBuilder(stringHT());
    assertCall(() -> arrayBuilder.add(blobH(ByteString.of())))
        .throwsException(IllegalArgumentException.class);
  }

  @Test
  public void array_contains_added_elem() {
    ArrayH array = objectHDb().arrayBuilder(stringHT())
        .add(stringH("abc"))
        .build();
    assertThat(array.elems(StringH.class))
        .containsExactly(stringH("abc"));
  }

  @Test
  public void array_contains_added_elem_via_add_all_method() {
    StringH str = stringH("abc");
    StringH str2 = stringH("def");
    ArrayH array = objectHDb().arrayBuilder(stringHT())
        .addAll(list(str, str2))
        .build();
    assertThat(array.elems(StringH.class))
        .containsExactly(str, str2)
        .inOrder();
  }

  @Test
  public void array_contains_added_elems_in_order() {
    StringH str1 = stringH("abc");
    StringH str2 = stringH("def");
    StringH str3 = stringH("ghi");
    ArrayH array = objectHDb().arrayBuilder(stringHT())
        .add(str1)
        .add(str2)
        .add(str3)
        .build();
    assertThat(array.elems(StringH.class))
        .containsExactly(str1, str2, str3)
        .inOrder();
  }

  @Test
  public void adding_same_elem_twice_builds_array_with_two_elems() {
    StringH str = stringH("abc");
    ArrayH array = objectHDb().arrayBuilder(stringHT())
        .add(str)
        .add(str)
        .build();
    assertThat(array.elems(StringH.class))
        .containsExactly(str, str);
  }

  @Test
  public void arrays_with_same_elems_have_same_hash() {
    StringH str1 = stringH("abc");
    StringH str2 = stringH("def");
    ArrayH array = objectHDb().arrayBuilder(stringHT())
        .add(str1)
        .add(str2)
        .build();
    ArrayH array2 = objectHDb().arrayBuilder(stringHT())
        .add(str1)
        .add(str2)
        .build();
    assertThat(array.hash())
        .isEqualTo(array2.hash());
  }

  @Test
  public void one_elem_array_hash_is_different_than_its_elem_hash() {
    StringH str = stringH("abc");
    ArrayH array = objectHDb().arrayBuilder(stringHT())
        .add(str)
        .build();
    assertThat(array.hash())
        .isNotEqualTo(str.hash());
  }

  @Test
  public void arrays_with_same_elems_but_in_different_order_have_different_hashes() {
    StringH str1 = stringH("abc");
    StringH str2 = stringH("def");
    ArrayH array = objectHDb().arrayBuilder(stringHT())
        .add(str1)
        .add(str2)
        .build();
    ArrayH array2 = objectHDb().arrayBuilder(stringHT())
        .add(str2)
        .add(str1)
        .build();
    assertThat(array.hash())
        .isNotEqualTo(array2.hash());
  }

  @Test
  public void array_with_one_more_elem_have_different_hash() {
    StringH str1 = stringH("abc");
    StringH str2 = stringH("def");
    ArrayH array = objectHDb().arrayBuilder(stringHT())
        .add(str1)
        .build();
    ArrayH array2 = objectHDb().arrayBuilder(stringHT())
        .add(str1)
        .add(str2)
        .build();
    assertThat(array.hash())
        .isNotEqualTo(array2.hash());
  }

  @Test
  public void array_can_be_read_by_hash() {
    StringH str1 = stringH("abc");
    StringH str2 = stringH("def");
    ArrayH array = objectHDb().arrayBuilder(stringHT())
        .add(str1)
        .add(str2)
        .build();
    assertThat(objectHDbOther().get(array.hash()))
        .isEqualTo(array);
  }

  @Test
  public void array_read_by_hash_contains_same_elems() {
    StringH str1 = stringH("abc");
    StringH str2 = stringH("def");
    ArrayH array = objectHDb().arrayBuilder(stringHT())
        .add(str1)
        .add(str2)
        .build();
    assertThat(((ArrayH) objectHDbOther().get(array.hash())).elems(StringH.class))
        .containsExactly(str1, str2)
        .inOrder();
  }

  @Test
  public void array_read_by_hash_has_same_hash() {
    StringH str1 = stringH("abc");
    StringH str2 = stringH("def");
    ArrayH array = objectHDb().arrayBuilder(stringHT())
        .add(str1)
        .add(str2)
        .build();
    assertThat(objectHDbOther().get(array.hash()).hash())
        .isEqualTo(array.hash());
  }

  @ParameterizedTest
  @MethodSource("type_test_data")
  public void type(TypeHV type) {
    ArrayH array = objectHDb().arrayBuilder(type).build();
    assertThat(array.type())
        .isEqualTo(arrayHT(type));
  }

  private static List<TypeH> type_test_data() {
    return TestingTypesH.TYPESV_TO_TEST;
  }

  @Test
  public void to_string() {
    StringH str1 = stringH("abc");
    StringH str2 = stringH("def");
    ArrayH array = objectHDb().arrayBuilder(stringHT())
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
      ArrayH array = emptyArrayOf(nothingHT());
      assertThat(array.type())
          .isEqualTo(arrayHT(nothingHT()));
    }

    @Test
    public void nothing_array_is_empty() {
      assertThat(emptyArrayOf(nothingHT()).elems(ValueH.class))
          .isEmpty();
    }

    @Test
    public void nothing_array_can_be_read_by_hash() {
      ArrayH array = emptyArrayOf(nothingHT());
      assertThat(objectHDbOther().get(array.hash()))
          .isEqualTo(array);
    }

    @Test
    public void nothing_array_read_by_hash_is_empty() {
      ArrayH array = emptyArrayOf(nothingHT());
      assertThat(((ArrayH) objectHDbOther().get(array.hash())).elems(ValueH.class))
          .isEmpty();
    }

    @Test
    public void nothing_array_to_string() {
      ArrayH array = emptyArrayOf(nothingHT());
      assertThat(array.toString())
          .isEqualTo("[]@" + array.hash());
    }

    private ArrayH emptyArrayOf(NothingTypeH elemType) {
      return objectHDb().arrayBuilder(elemType).build();
    }
  }
}
