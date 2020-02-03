package org.smoothbuild.testing.common;

import java.util.List;
import java.util.Objects;

import org.smoothbuild.util.TreeNode;

public class TestTreeNode implements TreeNode<TestTreeNode> {
  private final String name;
  private final List<TestTreeNode> children;

  public static TestTreeNode node(String name) {
    return node(name, List.of());
  }

  public static TestTreeNode node(String name, List<TestTreeNode> children) {
    return new TestTreeNode(name, children);
  }

  public TestTreeNode(String name, List<TestTreeNode> children) {
    this.name = name;
    this.children = children;
  }

  @Override
  public List<TestTreeNode> children() {
    return children;
  }

  @Override
  public String toString() {
    return name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TestTreeNode node = (TestTreeNode) o;
    return Objects.equals(name, node.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }
}
