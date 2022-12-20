package org.smoothbuild.compile.ps;

import static org.smoothbuild.compile.ps.CompileError.compileError;
import static org.smoothbuild.util.collect.Lists.map;

import java.util.HashSet;
import java.util.Set;

import org.smoothbuild.compile.lang.base.NalImpl;
import org.smoothbuild.compile.lang.define.DefinitionsS;
import org.smoothbuild.compile.ps.ast.ModuleVisitorP;
import org.smoothbuild.compile.ps.ast.expr.AnonymousFuncP;
import org.smoothbuild.compile.ps.ast.expr.FuncP;
import org.smoothbuild.compile.ps.ast.expr.ModuleP;
import org.smoothbuild.compile.ps.ast.expr.NamedEvaluableP;
import org.smoothbuild.compile.ps.ast.expr.NamedFuncP;
import org.smoothbuild.compile.ps.ast.expr.RefP;
import org.smoothbuild.compile.ps.ast.expr.StructP;
import org.smoothbuild.out.log.LogBuffer;
import org.smoothbuild.out.log.Logs;
import org.smoothbuild.util.Strings;

public class DetectUndefinedRefs extends ModuleVisitorP {
  private final ModuleP moduleP;
  private final Set<String> definedNames;
  private final LogBuffer logs;

  public DetectUndefinedRefs(ModuleP moduleP, Set<String> definedNames, LogBuffer logs) {
    this.moduleP = moduleP;
    this.definedNames = new HashSet<>(definedNames);
    this.logs = logs;
  }

  public static Logs detectUndefinedRefs(ModuleP moduleP, DefinitionsS imported) {
    Set<String> definedNames = imported.evaluables().toMap().keySet();
    var detectUndefinedRefs = new DetectUndefinedRefs(moduleP, definedNames, new LogBuffer());
    detectUndefinedRefs.visitAst(moduleP);
    return detectUndefinedRefs.logs;
  }

  @Override
  public void visitStruct(StructP structP) {
    super.visitStruct(structP);
    definedNames.add(structP.constructor().name());
  }

  @Override
  public void visitNamedEvaluable(NamedEvaluableP namedEvaluableP) {
    super.visitNamedEvaluable(namedEvaluableP);
    definedNames.add(namedEvaluableP.name());
  }

  @Override
  public void visitNamedFunc(NamedFuncP namedFuncP) {
    visitFunc(namedFuncP);
  }

  @Override
  public void visitAnonymousFunc(AnonymousFuncP anonymousFuncP) {
    visitFunc(anonymousFuncP);
  }

  private void visitFunc(FuncP funcP) {
    funcP.params().forEach(p -> p.defaultValue().ifPresent(this::visitNamedValue));
    funcP.body().ifPresent(body -> {
      var definedNamesInBodyScope = new HashSet<>(definedNames);
      definedNamesInBodyScope.addAll(map(funcP.params(), NalImpl::name));
      new DetectUndefinedRefs(moduleP, definedNamesInBodyScope, logs)
          .visitExpr(body);
    });
  }

  @Override
  public void visitRef(RefP refP) {
    var name = refP.name();
    if (!definedNames.contains(name)) {
      logs.log(compileError(refP, Strings.q(name) + " is undefined."));
    }
  }
}
