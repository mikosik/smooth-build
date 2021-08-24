package org.smoothbuild.db.object.base;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.Lists.list;

import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.db.object.spec.NothingSpec;
import org.smoothbuild.db.object.spec.Spec;
import org.smoothbuild.db.object.spec.TestingSpecs;
import org.smoothbuild.testing.TestingContext;

import okio.ByteString;

public class ArrayTest extends TestingContext {
  @Test
  public void empty_nothing_array_can_be_iterated_as_tuple() {
    Array array = arrayBuilder(nothingS())
        .build();
    assertThat(array.asIterable(Tuple.class))
        .isEmpty();
  }

  @Test
  public void string_array_cannot_be_iterated_as_tuple() {
    Array array = arrayBuilder(strS())
        .add(strV("abc"))
        .build();
    assertCall(() -> array.asIterable(Tuple.class))
        .throwsException(new IllegalArgumentException(
            "[STRING] cannot be viewed as Iterable of " + Tuple.class.getCanonicalName() + "."));
  }

  @Test
  public void empty_array_is_empty() {
    Array array = arrayBuilder(strS())
        .build();
    assertThat(array.asIterable(Str.class))
        .isEmpty();
  }

  @Test
  public void adding_null_is_forbidden() {
    ArrayBuilder arrayBuilder = arrayBuilder(strS());
    assertCall(() -> arrayBuilder.add(null))
        .throwsException(NullPointerException.class);
  }

  @Test
  public void adding_element_with_wrong_smooth_spec_is_forbidden() {
    ArrayBuilder arrayBuilder = arrayBuilder(strS());
    assertCall(() -> arrayBuilder.add(blobV(ByteString.of())))
        .throwsException(IllegalArgumentException.class);
  }

  @Test
  public void array_contains_added_element() {
    Array array = arrayBuilder(strS())
        .add(strV("abc"))
        .build();
    assertThat(array.asIterable(Str.class))
        .containsExactly(strV("abc"));
  }

  @Test
  public void array_contains_added_element_via_add_all_method() {
    Str rstring = strV("abc");
    Str rstring2 = strV("def");
    Array array = arrayBuilder(strS())
        .addAll(list(rstring, rstring2))
        .build();
    assertThat(array.asIterable(Str.class))
        .containsExactly(rstring, rstring2)
        .inOrder();
  }

  @Test
  public void array_contains_added_elements_in_order() {
    Str rstring1 = strV("abc");
    Str rstring2 = strV("def");
    Str rstring3 = strV("ghi");
    Array array = arrayBuilder(strS())
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
    Str rstring = strV("abc");
    Array array = arrayBuilder(strS())
        .add(rstring)
        .add(rstring)
        .build();
    assertThat(array.asIterable(Str.class))
        .containsExactly(rstring, rstring);
  }

  @Test
  public void arrays_with_same_elements_have_same_hash() {
    Str rstring1 = strV("abc");
    Str rstring2 = strV("def");
    Array array = arrayBuilder(strS())
        .add(rstring1)
        .add(rstring2)
        .build();
    Array array2 = arrayBuilder(strS())
        .add(rstring1)
        .add(rstring2)
        .build();
    assertThat(array.hash())
        .isEqualTo(array2.hash());
  }

  @Test
  public void one_element_array_hash_is_different_than_its_element_hash() {
    Str rstring = strV("abc");
    Array array = arrayBuilder(strS())
        .add(rstring)
        .build();
    assertThat(array.hash())
        .isNotEqualTo(rstring.hash());
  }

  @Test
  public void arrays_with_same_elements_but_in_different_order_have_different_hashes() {
    Str rstring1 = strV("abc");
    Str rstring2 = strV("def");
    Array array = arrayBuilder(strS())
        .add(rstring1)
        .add(rstring2)
        .build();
    Array array2 = arrayBuilder(strS())
        .add(rstring2)
        .add(rstring1)
        .build();
    assertThat(array.hash())
        .isNotEqualTo(array2.hash());
  }

  @Test
  public void array_with_one_more_element_have_different_hash() {
    Str rstring1 = strV("abc");
    Str rstring2 = strV("def");
    Array array = arrayBuilder(strS())
        .add(rstring1)
        .build();
    Array array2 = arrayBuilder(strS())
        .add(rstring1)
        .add(rstring2)
        .build();
    assertThat(array.hash())
        .isNotEqualTo(array2.hash());
  }

  @Test
  public void array_can_be_read_by_hash() {
    Str rstring1 = strV("abc");
    Str rstring2 = strV("def");
    Array array = arrayBuilder(strS())
        .add(rstring1)
        .add(rstring2)
        .build();
    assertThat(objectDbOther().get(array.hash()))
        .isEqualTo(array);
  }

  @Test
  public void array_read_by_hash_contains_same_elements() {
    Str rstring1 = strV("abc");
    Str rstring2 = strV("def");
    Array array = arrayBuilder(strS())
        .add(rstring1)
        .add(rstring2)
        .build();
    assertThat(((Array) objectDbOther().get(array.hash())).asIterable(Str.class))
        .containsExactly(rstring1, rstring2)
        .inOrder();
  }

  @Test
  public void array_read_by_hash_has_same_hash() {
    Str rstring1 = strV("abc");
    Str rstring2 = strV("def");
    Array array = arrayBuilder(strS())
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
        .isEqualTo(arrayS(spec));
  }

  private static List<Spec> spec_test_data() {
    return TestingSpecs.SPECS_TO_TEST;
  }

  @Test
  public void to_string() {
    Str rstring1 = strV("abc");
    Str rstring2 = strV("def");
    Array array = arrayBuilder(strS())
        .add(rstring1)
        .add(rstring2)
        .build();
    assertThat(array.toString())
        .isEqualTo("""
            ["abc","def"]:""" + array.hash());
  }

  @Nested
  class _nothing_array {
    @Test
    public void spec_of_nothing_array_is_nothing_array() {
      Array array = emptyArrayOf(nothingS());
      assertThat(array.spec())
          .isEqualTo(arrayS(nothingS()));
    }

    @Test
    public void nothing_array_is_empty() {
      assertThat(emptyArrayOf(nothingS()).asIterable(Obj.class))
          .isEmpty();
    }

    @Test
    public void nothing_array_can_be_read_by_hash() {
      Array array = emptyArrayOf(nothingS());
      assertThat(objectDbOther().get(array.hash()))
          .isEqualTo(array);
    }

    @Test
    public void nothing_array_read_by_hash_is_empty() {
      Array array = emptyArrayOf(nothingS());
      assertThat(((Array) objectDbOther().get(array.hash())).asIterable(Obj.class))
          .isEmpty();
    }

    @Test
    public void nothing_array_to_string() {
      Array array = emptyArrayOf(nothingS());
      assertThat(array.toString())
          .isEqualTo("[]:" + array.hash());
    }

    private Array emptyArrayOf(NothingSpec elemSpec) {
      return arrayBuilder(elemSpec).build();
    }
  }
}
