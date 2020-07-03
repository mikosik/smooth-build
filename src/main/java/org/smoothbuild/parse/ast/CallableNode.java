package org.smoothbuild.parse.ast;

import java.util.List;

import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.base.ParameterInfo;

public class CallableNode extends NamedNode {
  private List<ParameterInfo> parameterInfos;

  public CallableNode(String name, Location location) {
    super(name, location);
  }

  public List<ParameterInfo> parameterInfos() {
    return parameterInfos;
  }

  public void setParameterInfos(List<ParameterInfo> parameterInfos) {
    this.parameterInfos = parameterInfos;
  }
}
