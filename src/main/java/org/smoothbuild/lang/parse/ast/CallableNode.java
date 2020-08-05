package org.smoothbuild.lang.parse.ast;

import java.util.List;

import org.smoothbuild.lang.base.Item;
import org.smoothbuild.lang.base.Location;

import com.google.common.collect.ImmutableList;

public class CallableNode extends EvaluableNode {
  private List<? extends Item> parameterInfos;
  private final List<ItemNode> params;

  public CallableNode(TypeNode typeNode, String name, ExprNode exprNode, List<ItemNode> params,
      Location location) {
    super(typeNode, name, exprNode, location);
    this.params = ImmutableList.copyOf(params);
  }

  public List<ItemNode> params() {
    return params;
  }

  public List<? extends Item> parameterInfos() {
    return parameterInfos;
  }

  public void setParameterInfos(List<? extends Item> parameterInfos) {
    this.parameterInfos = parameterInfos;
  }
}
