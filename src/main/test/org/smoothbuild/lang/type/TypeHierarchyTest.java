package org.smoothbuild.lang.type;

import static com.google.common.collect.Collections2.permutations;
import static org.smoothbuild.lang.type.ArrayType.arrayOf;
import static org.smoothbuild.lang.type.TypeHierarchy.sortedTypes;
import static org.smoothbuild.util.Lists.list;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import java.util.List;

import org.junit.Test;

import com.google.common.collect.ImmutableMap;

public class TypeHierarchyTest {
  private static final TypeSystem TYPE_SYSTEM = new TypeSystem();
  private static final Type STRING = TYPE_SYSTEM.string();
  private static final Type NOTHING = TYPE_SYSTEM.nothing();

  @Test
  public void sorted_types_for_empty_hierarchy() throws Exception {
    assertSortedOrder(list());
  }

  @Test
  public void sorted_types_with_one_type() throws Exception {
    assertSortedOrder(list(STRING));
  }

  @Test
  public void sorted_types_for_two_related_types() throws Exception {
    assertSortedOrder(list(STRING, personType()));
  }

  @Test
  public void sorted_types_for_two_related_array_types() throws Exception {
    assertSortedOrder(list(arrayOf(STRING), arrayOf(personType())));
  }

  @Test
  public void sorted_types_for_two_related_array_of_array_types() throws Exception {
    assertSortedOrder(list(arrayOf(arrayOf(STRING)), arrayOf(arrayOf(personType()))));
  }

  @Test
  public void sorted_nothings() throws Exception {
    assertSortedOrder(list(arrayOf(arrayOf(NOTHING)), arrayOf(NOTHING), NOTHING));
  }

  @Test
  public void sorted_nothings_without_middle_one() throws Exception {
    assertSortedOrder(list(arrayOf(arrayOf(NOTHING)), NOTHING));
  }

  @Test
  public void sorted_types_comes_before_nothing() throws Exception {
    assertSortedOrder(list(STRING, NOTHING));
  }

  @Test
  public void sorted_array_types_comes_before_array_of_nothing() throws Exception {
    assertSortedOrder(list(arrayOf(STRING), arrayOf(NOTHING)));
  }

  @Test
  public void sorted_types_does_not_return_duplicated_types() throws Exception {
    when(() -> sortedTypes(list(STRING, STRING, STRING)));
    thenReturned(list(STRING));
  }

  private static void assertSortedOrder(List<Type> expected) {
    for (List<Type> permutation : permutations(expected)) {
      when(() -> sortedTypes(permutation));
      thenReturned(expected);
    }
  }

  private static StructType personType() {
    return new StructType("Person", ImmutableMap.of("firstName", STRING, "lastName", STRING));
  }
}
