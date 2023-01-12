package org.smoothbuild.compile.fs.ps.infer;

import static org.smoothbuild.util.collect.Lists.list;

import org.smoothbuild.compile.fs.lang.type.TupleTS;
import org.smoothbuild.compile.fs.lang.type.tool.Unifier;
import org.smoothbuild.compile.fs.ps.ast.define.AnonymousFuncP;
import org.smoothbuild.compile.fs.ps.ast.define.BlobP;
import org.smoothbuild.compile.fs.ps.ast.define.CallP;
import org.smoothbuild.compile.fs.ps.ast.define.ExprP;
import org.smoothbuild.compile.fs.ps.ast.define.IntP;
import org.smoothbuild.compile.fs.ps.ast.define.MonoizableP;
import org.smoothbuild.compile.fs.ps.ast.define.MonoizeP;
import org.smoothbuild.compile.fs.ps.ast.define.NamedArgP;
import org.smoothbuild.compile.fs.ps.ast.define.OrderP;
import org.smoothbuild.compile.fs.ps.ast.define.ReferenceP;
import org.smoothbuild.compile.fs.ps.ast.define.SelectP;
import org.smoothbuild.compile.fs.ps.ast.define.StringP;

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
    // @formatter:off
    switch (expr) {
      case CallP          call          -> inferCall(call);
      case MonoizeP       monoizeP      -> inferMonoize(monoizeP);
      case NamedArgP      namedArg      -> inferNamedArg(namedArg);
      case OrderP         order         -> inferOrder(order);
      case SelectP        select        -> inferSelect(select);
      case StringP        string        -> {}
      case IntP           int_          -> {}
      case BlobP          blob          -> {}
    }
    // @formatter:on
  }

  private void inferCall(CallP call) {
    infer(call.callee());
    call.args().forEach(this::infer);
  }

  private void inferMonoize(MonoizeP monoizeP) {
    inferMonoizable(monoizeP.monoizable());
    inferMonoizeTypeArgs(monoizeP);
  }

  private void inferMonoizeTypeArgs(MonoizeP monoizeP) {
    for (var typeArg : monoizeP.typeArgs()) {
      var resolvedTypeArg = unifier.resolve(typeArg);
      for (var var : resolvedTypeArg.vars()) {
        if (var.isTemporary()) {
          unifier.unifyOrFailWithRuntimeException(var, new TupleTS(list()));
        }
      }
    }
  }

  private void inferMonoizable(MonoizableP monoizableP) {
    switch (monoizableP) {
      case AnonymousFuncP anonymousFuncP -> inferAnonymousFunc(anonymousFuncP);
      case ReferenceP referenceP -> {}
    }
  }

  private void inferAnonymousFunc(AnonymousFuncP anonymousFunc) {
    infer(anonymousFunc.bodyGet());
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
