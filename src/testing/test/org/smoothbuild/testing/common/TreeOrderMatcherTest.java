package org.smoothbuild.testing.common;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.common.TestTreeNode.node;
import static org.smoothbuild.util.Lists.list;

import org.junit.jupiter.api.Test;

public class TreeOrderMatcherTest {
  private TreeOrderMatcher<TestTreeNode> matcher;

  @Test
  public void empty_list_matches_empty_tree() {
    matcher = new TreeOrderMatcher<>(null);
    assertThat(matcher.matchesSafely(list()))
        .isTrue();
  }

  @Test
  public void empty_list_matches_non_empty_tree() {
    matcher = new TreeOrderMatcher<>(node("root"));
    assertThat(matcher.matchesSafely(list()))
        .isTrue();
  }

  @Test
  public void list_matches_tree_which_does_not_contain_list_elements() {
    matcher = new TreeOrderMatcher<>(node("root"));
    assertThat(matcher.matchesSafely(list(node("A"), node("B"))))
        .isTrue();
  }

  @Test
  public void list_matches_tree_when_elements_match_parent_child_relationship() {
    matcher = new TreeOrderMatcher<>(node("root", list(node("child A"), node("child B"))));
    assertThat(matcher.matchesSafely(list(node("child A"), node("child B"), node("root"))))
        .isTrue();
  }

  @Test
  public void list_matches_tree_when_elements_match_parent_child_relationship_with_children_reversed() {
    matcher = new TreeOrderMatcher<>(node("root", list(node("child A"), node("child B"))));
    assertThat(matcher.matchesSafely(list(node("child B"), node("child A"), node("root"))))
        .isTrue();
  }

  @Test
  public void list_does_not_matche_tree_when_parent_is_before_child() {
    matcher = new TreeOrderMatcher<>(node("root", list(node("child A"), node("child B"))));
    assertThat(matcher.matchesSafely(list(node("root"), node("child A"))))
        .isFalse();
  }
}
