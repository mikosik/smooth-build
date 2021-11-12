package org.smoothbuild.lang.parse.ast;

import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.collect.NamedList.namedListWithDuplicates;

import java.util.List;
import java.util.Optional;

import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.base.type.impl.FunctionTypeS;
import org.smoothbuild.lang.base.type.impl.TypeS;
import org.smoothbuild.util.collect.NamedList;
import org.smoothbuild.util.collect.Optionals;

import com.google.common.collect.ImmutableList;

public class FunctionNode extends ReferencableNode {
  private final NamedList<ItemNode> params;

  public FunctionNode(Optional<TypeNode> typeNode, String name, Optional<ExprNode> body,
      List<ItemNode> params, Optional<AnnotationNode> annotation, Location location) {
    super(typeNode, name, body, annotation, location);
    this.params = namedListWithDuplicates(ImmutableList.copyOf(params));
  }

  public NamedList<ItemNode> params() {
    return params;
  }

  public Optional<ImmutableList<TypeS>> optParameterTypes() {
    return Optionals.pullUp(map(params(), ItemNode::type));
  }

  public Optional<TypeS> resultType() {
    return type().map(f -> ((FunctionTypeS) f).result());
  }
}
