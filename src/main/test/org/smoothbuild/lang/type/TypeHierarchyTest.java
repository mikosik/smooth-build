package org.smoothbuild.lang.type;

import static com.google.common.collect.Collections2.permutations;
import static org.smoothbuild.lang.type.TestingTypes.typesDb;
import static org.smoothbuild.lang.type.TypeHierarchy.sortedTypes;
import static org.smoothbuild.util.Lists.list;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import java.util.List;

import org.junit.Test;

public class TypeHierarchyTest {
  @Test
  public void sorted_types_for_empty_hierarchy() throws Exception {
    assertSortedOrder(list());
  }

  @Test
  public void sorted_types_with_one_type() throws Exception {
    assertSortedOrder(list(TestingTypes.string));
  }

  @Test
  public void sorted_types_for_two_related_types() throws Exception {
    assertSortedOrder(list(TestingTypes.string, personType()));
  }

  @Test
  public void sorted_types_for_two_related_array_types() throws Exception {
    assertSortedOrder(list(typesDb.array(TestingTypes.string), typesDb.array(personType())));
  }

  @Test
  public void sorted_types_for_two_related_array_of_array_types() throws Exception {
    assertSortedOrder(list(typesDb.array(typesDb.array(TestingTypes.string)), typesDb.array(
        typesDb.array(personType()))));
  }

  @Test
  public void sorted_nothings() throws Exception {
    assertSortedOrder(list(typesDb.array(typesDb.array(TestingTypes.nothing)), typesDb.array(
        TestingTypes.nothing), TestingTypes.nothing));
  }

  @Test
  public void sorted_nothings_without_middle_one() throws Exception {
    assertSortedOrder(list(typesDb.array(typesDb.array(TestingTypes.nothing)),
        TestingTypes.nothing));
  }

  @Test
  public void sorted_types_comes_before_nothing() throws Exception {
    assertSortedOrder(list(TestingTypes.string, TestingTypes.nothing));
  }

  @Test
  public void sorted_array_types_comes_before_array_of_nothing() throws Exception {
    assertSortedOrder(list(typesDb.array(TestingTypes.string), typesDb.array(
        TestingTypes.nothing)));
  }

  @Test
  public void sorted_types_does_not_return_duplicated_types() throws Exception {
    when(() -> sortedTypes(list(TestingTypes.string, TestingTypes.string, TestingTypes.string)));
    thenReturned(list(TestingTypes.string));
  }

  private static void assertSortedOrder(List<ConcreteType> expected) {
    for (List<ConcreteType> permutation : permutations(expected)) {
      when(() -> sortedTypes(permutation));
      thenReturned(expected);
    }
  }

  private StructType personType() {
    return TestingTypes.personType(typesDb);
  }
}
