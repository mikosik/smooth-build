package org.smoothbuild.parse.ast;

import java.util.List;

import org.smoothbuild.lang.base.ItemInfo;
import org.smoothbuild.lang.base.Location;

import com.google.common.collect.ImmutableList;

public class CallableNode extends NamedNode {
  private List<ItemInfo> parameterInfos;
  private final List<ItemNode> params;

  public CallableNode(String name, List<ItemNode> params, Location location) {
    super(name, location);
    this.params = ImmutableList.copyOf(params);
  }

  public List<ItemNode> params() {
    return params;
  }

  public List<ItemInfo> parameterInfos() {
    return parameterInfos;
  }

  public void setParameterInfos(List<ItemInfo> parameterInfos) {
    this.parameterInfos = parameterInfos;
  }
}
