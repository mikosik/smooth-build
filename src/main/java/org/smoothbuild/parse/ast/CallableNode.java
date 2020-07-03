package org.smoothbuild.parse.ast;

import java.util.List;

import org.smoothbuild.lang.base.ItemInfo;
import org.smoothbuild.lang.base.Location;

public class CallableNode extends NamedNode {
  private List<ItemInfo> parameterInfos;

  public CallableNode(String name, Location location) {
    super(name, location);
  }

  public List<ItemInfo> parameterInfos() {
    return parameterInfos;
  }

  public void setParameterInfos(List<ItemInfo> parameterInfos) {
    this.parameterInfos = parameterInfos;
  }
}
