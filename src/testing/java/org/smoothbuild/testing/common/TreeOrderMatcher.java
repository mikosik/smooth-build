package org.smoothbuild.testing.common;

import static org.smoothbuild.util.TreeNodes.treeToString;

import java.util.List;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.smoothbuild.util.TreeNode;

/**
 * Matches lists which elements are ordered according to parent-child relationship specified by
 * given tree.
 */
public class TreeOrderMatcher<T extends TreeNode<T>> extends TypeSafeMatcher<List<T>> {
  private final T tree;

  public TreeOrderMatcher(T tree) {
    this.tree = tree;
  }

  @Override
  public void describeTo(Description description) {
    description.appendText(
        "is List<T> which elements order is imposed by children-parent order given by tree:\n"
            + treeToString(tree));
  }

  @Override
  protected void describeMismatchSafely(List<T> order, Description mismatchDescription) {
    String error = checkTree(tree, order);
    if (error == null) {
      throw new RuntimeException();
    } else {
      mismatchDescription.appendText(error);
    }
  }

  @Override
  protected boolean matchesSafely(List<T> order) {
    if (tree != null) {
      String errors = checkTree(tree, order);
      return errors == null;
    }
    return true;
  }

  private String checkTrees(List<T> trees, List<T> order) {
    for (T tree : trees) {
      String error = checkTree(tree, order);
      if (error != null) {
        return error;
      }
    }
    return null;
  }

  private String checkTree(T parent, List<T> order) {
    for (T child : parent.children()) {
      if (orderOf(parent, order) < orderOf(child, order)) {
        return "Child " + child + " should be located before its parent " + parent + ".";
      }
    }
    return checkTrees(parent.children(), order);
  }

  private int orderOf(T task, List<T> order) {
    return order.indexOf(task);
  }
}
