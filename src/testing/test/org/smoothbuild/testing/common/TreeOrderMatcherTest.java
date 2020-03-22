package org.smoothbuild.testing.common;

import static org.smoothbuild.testing.common.TestTreeNode.node;
import static org.smoothbuild.util.Lists.list;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.jupiter.api.Test;

public class TreeOrderMatcherTest {
  private TreeOrderMatcher<TestTreeNode> matcher;

  @Test
  public void empty_list_matches_empty_tree() {
    given(matcher = new TreeOrderMatcher<>(null));
    when(() -> matcher.matchesSafely(list()));
    thenReturned(true);
  }

  @Test
  public void empty_list_matches_non_empty_tree() {
    given(matcher = new TreeOrderMatcher<>(node("root")));
    when(() -> matcher.matchesSafely(list()));
    thenReturned(true);
  }

  @Test
  public void list_matches_tree_which_does_not_contain_list_elements() {
    given(matcher = new TreeOrderMatcher<>(node("root")));
    when(() -> matcher.matchesSafely(list(node("A"), node("B"))));
    thenReturned(true);
  }

  @Test
  public void list_matches_tree_when_elements_match_parent_child_relationship() {
    given(matcher = new TreeOrderMatcher<>(node("root", list(node("child A"), node("child B")))));
    when(() -> matcher.matchesSafely(list(node("child A"), node("child B"), node("root"))));
    thenReturned(true);
  }

  @Test
  public void list_matches_tree_when_elements_match_parent_child_relationship_with_children_reversed() {
    given(matcher = new TreeOrderMatcher<>(node("root", list(node("child A"), node("child B")))));
    when(() -> matcher.matchesSafely(list(node("child B"), node("child A"), node("root"))));
    thenReturned(true);
  }

  @Test
  public void list_does_not_matche_tree_when_parent_is_before_child() {
    given(matcher = new TreeOrderMatcher<>(node("root", list(node("child A"), node("child B")))));
    when(() -> matcher.matchesSafely(list(node("root"), node("child A"))));
    thenReturned(false);
  }
}
