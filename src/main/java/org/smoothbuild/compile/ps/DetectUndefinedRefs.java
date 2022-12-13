package org.smoothbuild.compile.ps;

import static org.smoothbuild.compile.ps.CompileError.compileError;
import static org.smoothbuild.util.collect.Lists.map;

import java.util.HashSet;
import java.util.Set;

import org.smoothbuild.compile.lang.base.NalImpl;
import org.smoothbuild.compile.lang.define.DefsS;
import org.smoothbuild.compile.ps.ast.Ast;
import org.smoothbuild.compile.ps.ast.AstVisitor;
import org.smoothbuild.compile.ps.ast.expr.AnonymousFuncP;
import org.smoothbuild.compile.ps.ast.expr.FuncP;
import org.smoothbuild.compile.ps.ast.expr.NamedEvaluableP;
import org.smoothbuild.compile.ps.ast.expr.NamedFuncP;
import org.smoothbuild.compile.ps.ast.expr.RefP;
import org.smoothbuild.compile.ps.ast.expr.StructP;
import org.smoothbuild.out.log.LogBuffer;
import org.smoothbuild.out.log.Logs;
import org.smoothbuild.util.Strings;

import com.google.common.collect.ImmutableSet;

public class DetectUndefinedRefs extends AstVisitor {
  private final Ast ast;
  private final Set<String> definedNames;
  private final LogBuffer logs;

  public DetectUndefinedRefs(Ast ast, Set<String> definedNames, LogBuffer logs) {
    this.ast = ast;
    this.definedNames = new HashSet<>(definedNames);
    this.logs = logs;
  }

  public static Logs detectUndefinedRefs(Ast ast, DefsS imported) {
    ImmutableSet<String> definedNames = imported.evaluables().asMap().keySet();
    var detectUndefinedRefs = new DetectUndefinedRefs(ast, definedNames, new LogBuffer());
    detectUndefinedRefs.visitAst(ast);
    return detectUndefinedRefs.logs;
  }

  @Override
  public void visitStruct(StructP struct) {
    super.visitStruct(struct);
    definedNames.add(struct.constructor().name());
  }

  @Override
  public void visitNamedEvaluable(NamedEvaluableP namedEvaluable) {
    super.visitNamedEvaluable(namedEvaluable);
    definedNames.add(namedEvaluable.name());
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
      new DetectUndefinedRefs(ast, definedNamesInBodyScope, logs)
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
