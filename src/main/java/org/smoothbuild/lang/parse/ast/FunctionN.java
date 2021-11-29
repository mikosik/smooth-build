package org.smoothbuild.lang.parse.ast;

import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.collect.NList.nListWithDuplicates;

import java.util.List;
import java.util.Optional;

import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.base.type.impl.FunctionTypeS;
import org.smoothbuild.lang.base.type.impl.TypeS;
import org.smoothbuild.lang.parse.ast.StructN.ConstructorN;
import org.smoothbuild.util.collect.NList;
import org.smoothbuild.util.collect.Optionals;

import com.google.common.collect.ImmutableList;

public sealed class FunctionN extends EvalN
    permits RealFuncN, ConstructorN {
  private final NList<ItemN> params;

  public FunctionN(Optional<TypeN> typeNode, String name, Optional<ExprN> body,
      List<ItemN> params, Optional<AnnotationN> annotation, Location location) {
    super(typeNode, name, body, annotation, location);
    this.params = nListWithDuplicates(ImmutableList.copyOf(params));
  }

  public NList<ItemN> params() {
    return params;
  }

  public Optional<ImmutableList<TypeS>> optParamTypes() {
    return Optionals.pullUp(map(params(), ItemN::type));
  }

  public Optional<TypeS> resultType() {
    return type().map(f -> ((FunctionTypeS) f).result());
  }
}
