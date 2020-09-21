package org.smoothbuild.lang.parse.ast;

import java.util.List;

import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.base.type.ItemSignature;

import com.google.common.collect.ImmutableList;

public class CallableNode extends EvaluableNode {
  private List<? extends ItemSignature> parameterInfos;
  private final List<ItemNode> params;

  public CallableNode(TypeNode typeNode, String name, ExprNode exprNode, List<ItemNode> params,
      Location location) {
    super(typeNode, name, exprNode, location);
    this.params = ImmutableList.copyOf(params);
  }

  public List<ItemNode> params() {
    return params;
  }

  public List<? extends ItemSignature> parameterInfos() {
    return parameterInfos;
  }

  public void setParameterInfos(List<? extends ItemSignature> parameterInfos) {
    this.parameterInfos = parameterInfos;
  }
}
