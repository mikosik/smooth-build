package org.smoothbuild.compile.ps;

import static org.smoothbuild.compile.ps.CompileError.compileError;

import java.util.HashSet;
import java.util.Set;

import org.smoothbuild.compile.lang.define.DefsS;
import org.smoothbuild.compile.ps.ast.Ast;
import org.smoothbuild.compile.ps.ast.AstVisitor;
import org.smoothbuild.compile.ps.ast.StructP;
import org.smoothbuild.compile.ps.ast.expr.RefP;
import org.smoothbuild.compile.ps.ast.refable.FuncP;
import org.smoothbuild.compile.ps.ast.refable.PolyEvaluableP;
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
    definedNames.add(struct.ctor().name());
  }

  @Override
  public void visitEvaluable(PolyEvaluableP evaluable) {
    super.visitEvaluable(evaluable);
    definedNames.add(evaluable.name());
  }

  @Override
  public void visitFunc(FuncP funcP) {
    funcP.params().forEach(p -> p.defaultVal().ifPresent(this::visitExpr));
    funcP.body().ifPresent(body -> {
      var definedNamesWithParams = new HashSet<>(definedNames);
      funcP.params().forEach(p -> definedNamesWithParams.add(p.name()));
      new DetectUndefinedRefs(ast, definedNamesWithParams, logs).visitExpr(body);
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
