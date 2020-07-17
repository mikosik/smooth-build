package org.smoothbuild.parse.ast;

import java.util.List;

import org.smoothbuild.lang.base.Item;
import org.smoothbuild.lang.base.Location;

import com.google.common.collect.ImmutableList;

public class CallableNode extends NamedNode {
  private List<Item> parameterInfos;
  private final List<ItemNode> params;

  public CallableNode(String name, List<ItemNode> params, Location location) {
    super(name, location);
    this.params = ImmutableList.copyOf(params);
  }

  public List<ItemNode> params() {
    return params;
  }

  public List<Item> parameterInfos() {
    return parameterInfos;
  }

  public void setParameterInfos(List<Item> parameterInfos) {
    this.parameterInfos = parameterInfos;
  }
}
