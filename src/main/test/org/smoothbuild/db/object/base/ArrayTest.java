package org.smoothbuild.db.object.base;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.Lists.list;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.db.object.spec.Spec;
import org.smoothbuild.db.object.spec.TestingSpecs;
import org.smoothbuild.testing.TestingContext;

import com.google.common.truth.Truth;

import okio.ByteString;

public class ArrayTest extends TestingContext {
  @Test
  public void empty_nothing_array_can_be_iterated_as_tuple() {
    Array array = arrayBuilder(nothingSpec())
        .build();
    assertThat(array.asIterable(Tuple.class))
        .isEmpty();
  }

  @Test
  public void string_array_cannot_be_iterated_as_tuple() {
    Array array = arrayBuilder(stringSpec())
        .add(string("abc"))
        .build();
    assertCall(() -> array.asIterable(Tuple.class))
        .throwsException(new IllegalArgumentException(
            "[STRING] cannot be viewed as Iterable of " + Tuple.class.getCanonicalName() + "."));
  }

  @Test
  public void empty_array_is_empty() {
    Array array = arrayBuilder(stringSpec())
        .build();
    assertThat(array.asIterable(Str.class))
        .isEmpty();
  }

  @Test
  public void adding_null_is_forbidden() {
    ArrayBuilder arrayBuilder = arrayBuilder(stringSpec());
    assertCall(() -> arrayBuilder.add(null))
        .throwsException(NullPointerException.class);
  }

  @Test
  public void adding_element_with_wrong_smooth_spec_is_forbidden() {
    ArrayBuilder arrayBuilder = arrayBuilder(stringSpec());
    assertCall(() -> arrayBuilder.add(blob(ByteString.of())))
        .throwsException(IllegalArgumentException.class);
  }

  @Test
  public void array_contains_added_element() {
    Array array = arrayBuilder(stringSpec())
        .add(string("abc"))
        .build();
    assertThat(array.asIterable(Str.class))
        .containsExactly(string("abc"));
  }

  @Test
  public void array_contains_added_element_via_add_all_method() {
    Str rstring = string("abc");
    Str rstring2 = string("def");
    Array array = arrayBuilder(stringSpec())
        .addAll(list(rstring, rstring2))
        .build();
    assertThat(array.asIterable(Str.class))
        .containsExactly(rstring, rstring2)
        .inOrder();
  }

  @Test
  public void array_contains_added_elements_in_order() {
    Str rstring1 = string("abc");
    Str rstring2 = string("def");
    Str rstring3 = string("ghi");
    Array array = arrayBuilder(stringSpec())
        .add(rstring1)
        .add(rstring2)
        .add(rstring3)
        .build();
    assertThat(array.asIterable(Str.class))
        .containsExactly(rstring1, rstring2, rstring3)
        .inOrder();
  }

  @Test
  public void adding_same_element_twice_builds_array_with_two_elements() {
    Str rstring = string("abc");
    Array array = arrayBuilder(stringSpec())
        .add(rstring)
        .add(rstring)
        .build();
    assertThat(array.asIterable(Str.class))
        .containsExactly(rstring, rstring);
  }

  @Test
  public void arrays_with_same_elements_have_same_hash() {
    Str rstring1 = string("abc");
    Str rstring2 = string("def");
    Array array = arrayBuilder(stringSpec())
        .add(rstring1)
        .add(rstring2)
        .build();
    Array array2 = arrayBuilder(stringSpec())
        .add(rstring1)
        .add(rstring2)
        .build();
    assertThat(array.hash())
        .isEqualTo(array2.hash());
  }

  @Test
  public void one_element_array_hash_is_different_than_its_element_hash() {
    Str rstring = string("abc");
    Array array = arrayBuilder(stringSpec())
        .add(rstring)
        .build();
    assertThat(array.hash())
        .isNotEqualTo(rstring.hash());
  }

  @Test
  public void arrays_with_same_elements_but_in_different_order_have_different_hashes() {
    Str rstring1 = string("abc");
    Str rstring2 = string("def");
    Array array = arrayBuilder(stringSpec())
        .add(rstring1)
        .add(rstring2)
        .build();
    Array array2 = arrayBuilder(stringSpec())
        .add(rstring2)
        .add(rstring1)
        .build();
    assertThat(array.hash())
        .isNotEqualTo(array2.hash());
  }

  @Test
  public void array_with_one_more_element_have_different_hash() {
    Str rstring1 = string("abc");
    Str rstring2 = string("def");
    Array array = arrayBuilder(stringSpec())
        .add(rstring1)
        .build();
    Array array2 = arrayBuilder(stringSpec())
        .add(rstring1)
        .add(rstring2)
        .build();
    assertThat(array.hash())
        .isNotEqualTo(array2.hash());
  }

  @Test
  public void array_can_be_read_by_hash() {
    Str rstring1 = string("abc");
    Str rstring2 = string("def");
    Array array = arrayBuilder(stringSpec())
        .add(rstring1)
        .add(rstring2)
        .build();
    Truth.assertThat(objectDbOther().get(array.hash()))
        .isEqualTo(array);
  }

  @Test
  public void array_read_by_hash_contains_same_elements() {
    Str rstring1 = string("abc");
    Str rstring2 = string("def");
    Array array = arrayBuilder(stringSpec())
        .add(rstring1)
        .add(rstring2)
        .build();
    assertThat(((Array) objectDbOther().get(array.hash())).asIterable(Str.class))
        .containsExactly(rstring1, rstring2)
        .inOrder();
  }

  @Test
  public void array_read_by_hash_has_same_hash() {
    Str rstring1 = string("abc");
    Str rstring2 = string("def");
    Array array = arrayBuilder(stringSpec())
        .add(rstring1)
        .add(rstring2)
        .build();
    assertThat(objectDbOther().get(array.hash()).hash())
        .isEqualTo(array.hash());
  }

  @ParameterizedTest
  @MethodSource("spec_test_data")
  public void spec(Spec spec) {
    Array array = arrayBuilder(spec).build();
    assertThat(array.spec())
        .isEqualTo(arraySpec(spec));
  }

  private static List<Spec> spec_test_data() {
    return TestingSpecs.SPECS_TO_TEST;
  }

  @Test
  public void to_string() {
    Str rstring1 = string("abc");
    Str rstring2 = string("def");
    Array array = arrayBuilder(stringSpec())
        .add(rstring1)
        .add(rstring2)
        .build();
    assertThat(array.toString())
        .isEqualTo("""
            ["abc","def"]:""" + array.hash());
  }
}
