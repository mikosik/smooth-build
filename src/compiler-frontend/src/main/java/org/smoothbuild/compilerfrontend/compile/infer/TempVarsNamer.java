package org.smoothbuild.compilerfrontend.compile.infer;

import static org.smoothbuild.compilerfrontend.lang.type.SVarSet.varSetS;

import org.smoothbuild.compilerfrontend.compile.ast.PModuleVisitor;
import org.smoothbuild.compilerfrontend.compile.ast.define.PEvaluable;
import org.smoothbuild.compilerfrontend.compile.ast.define.PLambda;
import org.smoothbuild.compilerfrontend.compile.ast.define.PNamedValue;
import org.smoothbuild.compilerfrontend.lang.type.SType;
import org.smoothbuild.compilerfrontend.lang.type.SVar;
import org.smoothbuild.compilerfrontend.lang.type.SVarSet;
import org.smoothbuild.compilerfrontend.lang.type.tool.EqualityConstraint;
import org.smoothbuild.compilerfrontend.lang.type.tool.Unifier;
import org.smoothbuild.compilerfrontend.lang.type.tool.UnusedVarsGenerator;

public class TempVarsNamer extends PModuleVisitor {
  private final Unifier unifier;
  private final SVarSet outerScopeVars;

  private TempVarsNamer(Unifier unifier, SVarSet outerScopeVars) {
    this.unifier = unifier;
    this.outerScopeVars = outerScopeVars;
  }

  public static void nameVarsInNamedValue(Unifier unifier, PNamedValue namedValue) {
    new TempVarsNamer(unifier, varSetS()).nameVarsInEvaluable(namedValue);
  }

  public static void nameVarsInNamedFunc(Unifier unifier, PEvaluable namedFunc) {
    new TempVarsNamer(unifier, varSetS()).nameVarsInEvaluable(namedFunc);
  }

  @Override
  public void visitLambda(PLambda pLambda) {
    nameVarsInEvaluable(pLambda);
  }

  private void nameVarsInEvaluable(PEvaluable evaluable) {
    var resolvedT = unifier.resolve(evaluable.sType());
    var body = evaluable.body();
    var localScopeVars = resolvedT.vars().filter(v -> !v.isTemporary());
    var fullScopeVars = outerScopeVars.withAddedAll(localScopeVars);
    body.ifPresent(b -> new TempVarsNamer(unifier, fullScopeVars).visitExpr(b));
    var resolvedAndRenamedT = nameVars(resolvedT, localScopeVars);
    unifier.addOrFailWithRuntimeException(new EqualityConstraint(resolvedAndRenamedT, resolvedT));
  }

  private static SType nameVars(SType resolvedT, SVarSet reservedVars) {
    var vars = resolvedT.vars();
    var varsToRename = vars.filter(SVar::isTemporary);
    var varGenerator = new UnusedVarsGenerator(reservedVars);
    var mapping = varsToRename.toList().toMap(v -> (SType) varGenerator.next());
    return resolvedT.mapVars(mapping);
  }
}
