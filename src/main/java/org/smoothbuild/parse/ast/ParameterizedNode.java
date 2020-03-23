package org.smoothbuild.parse.ast;

import java.util.List;

import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.base.ParameterInfo;

public class ParameterizedNode extends NamedNode {
  public ParameterizedNode(String name, Location location) {
    super(name, location);
  }

  public List<? extends ParameterInfo> getParameterInfos() {
    @SuppressWarnings("unchecked")
    List<ParameterInfo> list = get(List.class);
    return list;
  }

  public void setParameterInfos(List<? extends ParameterInfo> parameterInfos) {
    set(List.class, parameterInfos);
  }
}
