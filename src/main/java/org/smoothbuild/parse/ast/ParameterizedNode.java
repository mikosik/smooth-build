package org.smoothbuild.parse.ast;

import java.util.List;

import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.base.ParameterInfo;

public class ParameterizedNode extends NamedNode {
  public ParameterizedNode(String name, Location location) {
    super(name, location);
  }

  public List<? extends ParameterInfo> getParameterInfos() {
    return get(List.class);
  }

  public void setParameterInfos(List<? extends ParameterInfo> parameterInfos) {
    set(List.class, parameterInfos);
  }
}
