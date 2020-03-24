package org.smoothbuild.util;

import static com.google.common.truth.Truth.assertThat;
import static java.util.Arrays.asList;
import static org.smoothbuild.util.Lists.concat;
import static org.smoothbuild.util.Lists.filter;
import static org.smoothbuild.util.Lists.list;
import static org.smoothbuild.util.Lists.map;
import static org.smoothbuild.util.Lists.sane;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@SuppressWarnings("ClassCanBeStatic")
public class ListsTest {
  @Nested
  class list {
    @Test
    public void with_no_elements(){
      assertThat(list())
          .isEmpty();
    }

    @Test
    public void with_one_element(){
      assertThat(list("abc"))
          .containsExactly("abc");
    }

    @Test
    public void with_two_elements(){
      assertThat(list("abc", "def"))
          .containsExactly("abc", "def")
          .inOrder();
    }

    @Test
    public void with_three_elements(){
      assertThat(list("abc", "def", "ghi"))
          .containsExactly("abc", "def", "ghi")
          .inOrder();
    }
  }

  @Nested
  class concat {
    @Test
    public void with_empty(){
      assertThat(concat(new ArrayList<>(), "element"))
          .containsExactly("element");
    }

    @Test
    public void with_non_empty(){
      assertThat(concat(asList("first"), "second"))
          .containsExactly("first", "second")
          .inOrder();
    }

    @Test
    public void concat_doesnt_modify_list(){
      List<String> list = asList("first");
      concat(list, "second");
      assertThat(list)
          .containsExactly("first");
    }
  }

  @Nested
  class filter {
    @Test
    public void returns_empty_for_empty_list(){
      assertThat(filter(new ArrayList<>(), x -> true))
          .isEmpty();
    }

    @Test
    public void returns_unmodified_list_when_predicate_is_always_true(){
      assertThat(filter(asList("first", "second", "third"), x -> true))
          .containsExactly("first", "second", "third")
          .inOrder();
    }

    @Test
    public void returns_empty_list_when_predicate_is_always_false(){
      assertThat(filter(asList("first", "second", "third"), x -> false))
          .isEmpty();
    }

    @Test
    public void leaves_only_elements_matching_predicate(){
      assertThat(filter(asList("first", "second", "third"), s -> s.startsWith("s")))
          .containsExactly("second")
          .inOrder();
    }
  }

  @Nested
  class map {
    @Test
    public void returns_empty_list_for_empty_argument(){
      assertThat(map(new ArrayList<String>(), String::toUpperCase))
          .isEmpty();
    }

    @Test
    public void returns_mapped_one_element(){
      assertThat(map(asList("abc"), String::toUpperCase))
          .containsExactly("ABC");
    }

    @Test
    public void mapping_with_two_elements(){
      assertThat(map(asList("abc", "def"), String::toUpperCase))
          .containsExactly("ABC", "DEF");
    }
  }

  @Nested
  class sane {
    @Test
    public void converts_null_to_empty_list(){
      assertThat(sane(null))
          .isEmpty();
    }

    @Test
    public void returns_empty_list_for_empty_list_argument(){
      assertThat(sane(new ArrayList<>()))
          .isEmpty();
    }

    @Test
    public void returns_unchanged_list_when_it_has_elements(){
      assertThat(sane(asList("abc", "def")))
          .containsExactly("abc", "def")
          .inOrder();
    }
  }
}
