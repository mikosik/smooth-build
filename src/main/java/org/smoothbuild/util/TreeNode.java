package org.smoothbuild.util;

import java.util.List;

public interface TreeNode<T extends TreeNode<T>> {
  public List<T> children();
}
