package org.smoothbuild.compilerfrontend.compile.infer;

import static org.smoothbuild.common.collect.List.list;

import org.smoothbuild.compilerfrontend.compile.ast.define.BlobP;
import org.smoothbuild.compilerfrontend.compile.ast.define.CallP;
import org.smoothbuild.compilerfrontend.compile.ast.define.ExprP;
import org.smoothbuild.compilerfrontend.compile.ast.define.InstantiateP;
import org.smoothbuild.compilerfrontend.compile.ast.define.IntP;
import org.smoothbuild.compilerfrontend.compile.ast.define.LambdaP;
import org.smoothbuild.compilerfrontend.compile.ast.define.NamedArgP;
import org.smoothbuild.compilerfrontend.compile.ast.define.OrderP;
import org.smoothbuild.compilerfrontend.compile.ast.define.PolymorphicP;
import org.smoothbuild.compilerfrontend.compile.ast.define.ReferenceP;
import org.smoothbuild.compilerfrontend.compile.ast.define.SelectP;
import org.smoothbuild.compilerfrontend.compile.ast.define.StringP;
import org.smoothbuild.compilerfrontend.lang.type.TupleTS;
import org.smoothbuild.compilerfrontend.lang.type.tool.EqualityConstraint;
import org.smoothbuild.compilerfrontend.lang.type.tool.Unifier;

/**
 * Infers unit type (which is empty tuple) for each quantified var
 * in any call to polymorphic function when that quantified var:
 * - is not used in function result type
 * - is resolved as temp-var after inferring phase (= it is not constrained).
 */
public class UnitTypeInferrer {
  private final Unifier unifier;

  public UnitTypeInferrer(Unifier unifier) {
    this.unifier = unifier;
  }

  public void infer(ExprP expr) {
    switch (expr) {
      case CallP call -> inferCall(call);
      case InstantiateP instantiateP -> inferInstantiate(instantiateP);
      case NamedArgP namedArg -> inferNamedArg(namedArg);
      case OrderP order -> inferOrder(order);
      case SelectP select -> inferSelect(select);
      case StringP string -> {}
      case IntP int_ -> {}
      case BlobP blob -> {}
    }
  }

  private void inferCall(CallP call) {
    infer(call.callee());
    call.args().forEach(this::infer);
  }

  private void inferInstantiate(InstantiateP instantiateP) {
    inferPolymorphic(instantiateP.polymorphic());
    inferInstantiateTypeArgs(instantiateP);
  }

  private void inferInstantiateTypeArgs(InstantiateP instantiateP) {
    for (var typeArg : instantiateP.typeArgs()) {
      var resolvedTypeArg = unifier.resolve(typeArg);
      for (var var : resolvedTypeArg.vars()) {
        if (var.isTemporary()) {
          unifier.addOrFailWithRuntimeException(new EqualityConstraint(var, new TupleTS(list())));
        }
      }
    }
  }

  private void inferPolymorphic(PolymorphicP polymorphicP) {
    switch (polymorphicP) {
      case LambdaP lambdaP -> inferLambda(lambdaP);
      case ReferenceP referenceP -> {}
    }
  }

  private void inferLambda(LambdaP lambda) {
    infer(lambda.bodyGet());
  }

  private void inferNamedArg(NamedArgP namedArg) {
    infer(namedArg.expr());
  }

  private void inferOrder(OrderP order) {
    order.elems().forEach(this::infer);
  }

  private void inferSelect(SelectP select) {
    infer(select.selectable());
  }
}
