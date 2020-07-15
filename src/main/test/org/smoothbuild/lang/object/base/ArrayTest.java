package org.smoothbuild.lang.object.base;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.Lists.list;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestingContext;

import okio.ByteString;

public class ArrayTest extends TestingContext {
  @Test
  public void empty_nothing_array_can_be_iterated_as_struct() {
    Array array = arrayBuilder(nothingType())
        .build();
    assertThat(array.asIterable(Struct.class))
        .isEmpty();
  }

  @Test
  public void string_array_cannot_be_iterated_as_struct() {
    Array array = arrayBuilder(stringType())
        .add(string("abc"))
        .build();
    assertCall(() -> array.asIterable(Struct.class))
        .throwsException(new IllegalArgumentException(
            "[STRING] cannot be viewed as Iterable of " + Struct.class.getCanonicalName() + "."));
  }

  @Test
  public void empty_array_is_empty() {
    Array array = arrayBuilder(stringType())
        .build();
    assertThat(array.asIterable(SString.class))
        .isEmpty();
  }

  @Test
  public void adding_null_is_forbidden() {
    ArrayBuilder arrayBuilder = arrayBuilder(stringType());
    assertCall(() -> arrayBuilder.add(null))
        .throwsException(NullPointerException.class);
  }

  @Test
  public void adding_element_with_wrong_smooth_type_is_forbidden() {
    ArrayBuilder arrayBuilder = arrayBuilder(stringType());
    assertCall(() -> arrayBuilder.add(blob(ByteString.of())))
        .throwsException(IllegalArgumentException.class);
  }

  @Test
  public void array_contains_added_element() {
    Array array = arrayBuilder(stringType())
        .add(string("abc"))
        .build();
    assertThat(array.asIterable(SString.class))
        .containsExactly(string("abc"));
  }

  @Test
  public void array_contains_added_element_via_add_all_method() {
    SString sstring = string("abc");
    SString sstring2 = string("def");
    Array array = arrayBuilder(stringType())
        .addAll(list(sstring, sstring2))
        .build();
    assertThat(array.asIterable(SString.class))
        .containsExactly(sstring, sstring2)
        .inOrder();
  }

  @Test
  public void array_contains_added_elements_in_order() {
    SString sstring1 = string("abc");
    SString sstring2 = string("def");
    SString sstring3 = string("ghi");
    Array array = arrayBuilder(stringType())
        .add(sstring1)
        .add(sstring2)
        .add(sstring3)
        .build();
    assertThat(array.asIterable(SString.class))
        .containsExactly(sstring1, sstring2, sstring3)
        .inOrder();
  }

  @Test
  public void adding_same_element_twice_builds_array_with_two_elements() {
    SString sstring = string("abc");
    Array array = arrayBuilder(stringType())
        .add(sstring)
        .add(sstring)
        .build();
    assertThat(array.asIterable(SString.class))
        .containsExactly(sstring, sstring);
  }

  @Test
  public void arrays_with_same_elements_have_same_hash() {
    SString sstring1 = string("abc");
    SString sstring2 = string("def");
    Array array = arrayBuilder(stringType())
        .add(sstring1)
        .add(sstring2)
        .build();
    Array array2 = arrayBuilder(stringType())
        .add(sstring1)
        .add(sstring2)
        .build();
    assertThat(array.hash())
        .isEqualTo(array2.hash());
  }

  @Test
  public void one_element_array_hash_is_different_than_its_element_hash() {
    SString sstring = string("abc");
    Array array = arrayBuilder(stringType())
        .add(sstring)
        .build();
    assertThat(array.hash())
        .isNotEqualTo(sstring.hash());
  }

  @Test
  public void arrays_with_same_elements_but_in_different_order_have_different_hashes() {
    SString sstring1 = string("abc");
    SString sstring2 = string("def");
    Array array = arrayBuilder(stringType())
        .add(sstring1)
        .add(sstring2)
        .build();
    Array array2 = arrayBuilder(stringType())
        .add(sstring2)
        .add(sstring1)
        .build();
    assertThat(array.hash())
        .isNotEqualTo(array2.hash());
  }

  @Test
  public void array_with_one_more_element_have_different_hash() {
    SString sstring1 = string("abc");
    SString sstring2 = string("def");
    Array array = arrayBuilder(stringType())
        .add(sstring1)
        .build();
    Array array2 = arrayBuilder(stringType())
        .add(sstring1)
        .add(sstring2)
        .build();
    assertThat(array.hash())
        .isNotEqualTo(array2.hash());
  }

  @Test
  public void array_can_be_read_by_hash() {
    SString sstring1 = string("abc");
    SString sstring2 = string("def");
    Array array = arrayBuilder(stringType())
        .add(sstring1)
        .add(sstring2)
        .build();
    assertThat(objectDbOther().get(array.hash()))
        .isEqualTo(array);
  }

  @Test
  public void array_read_by_hash_contains_same_elements() {
    SString sstring1 = string("abc");
    SString sstring2 = string("def");
    Array array = arrayBuilder(stringType())
        .add(sstring1)
        .add(sstring2)
        .build();
    assertThat(((Array) objectDbOther().get(array.hash())).asIterable(SString.class))
        .containsExactly(sstring1, sstring2)
        .inOrder();
  }

  @Test
  public void array_read_by_hash_has_same_hash() {
    SString sstring1 = string("abc");
    SString sstring2 = string("def");
    Array array = arrayBuilder(stringType())
        .add(sstring1)
        .add(sstring2)
        .build();
    assertThat(objectDbOther().get(array.hash()).hash())
        .isEqualTo(array.hash());
  }

  @Test
  public void to_string() {
    SString sstring1 = string("abc");
    SString sstring2 = string("def");
    Array array = arrayBuilder(stringType())
        .add(sstring1)
        .add(sstring2)
        .build();
    assertThat(array.toString())
        .isEqualTo("[STRING](...):" + array.hash());
  }
}
