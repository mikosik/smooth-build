package org.smoothbuild.compile.ps.infer;

import java.util.function.Predicate;

import org.smoothbuild.compile.lang.type.TypeS;
import org.smoothbuild.compile.lang.type.VarS;
import org.smoothbuild.compile.lang.type.tool.Unifier;
import org.smoothbuild.compile.ps.ast.expr.ExprP;
import org.smoothbuild.compile.ps.ast.refable.NamedFuncP;
import org.smoothbuild.compile.ps.ast.refable.NamedValueP;

public class ImplicitVarsNamer {
  private final Unifier unifier;

  public ImplicitVarsNamer(Unifier unifier) {
    this.unifier = unifier;
  }

  public void nameVarsInNamedValue(NamedValueP namedValue) {
    var resolvedT = unifier.resolve(namedValue.typeS());
    renameVarsAndUnify(resolvedT, VarS::isTemporary);
  }

  public void nameVarsInNamedFunc(NamedFuncP namedFunc) {
    var resolvedT = unifier.resolve(namedFunc.typeS());
    renameVarsAndUnify(resolvedT, VarS::isTemporary);
  }

  public void nameVarsInParamDefaultValue(ExprP exprP) {
    var resolvedT = unifier.resolve(exprP.typeS());
    renameVarsAndUnify(resolvedT, v -> true);
  }

  private void renameVarsAndUnify(TypeS resolvedT, Predicate<VarS> shouldRename) {
    var resolvedAndRenamedEvalT = resolvedT.renameVars(shouldRename);
    unifier.unifySafe(resolvedAndRenamedEvalT, resolvedT);
  }
}
