package org.smoothbuild.compile.ps;

import static org.smoothbuild.compile.ps.CompileError.compileError;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.smoothbuild.compile.lang.define.DefsS;
import org.smoothbuild.compile.ps.ast.Ast;
import org.smoothbuild.compile.ps.ast.expr.CallP;
import org.smoothbuild.compile.ps.ast.expr.DefaultArgP;
import org.smoothbuild.compile.ps.ast.expr.ExprP;
import org.smoothbuild.compile.ps.ast.expr.NamedArgP;
import org.smoothbuild.compile.ps.ast.expr.OrderP;
import org.smoothbuild.compile.ps.ast.expr.RefP;
import org.smoothbuild.compile.ps.ast.expr.SelectP;
import org.smoothbuild.compile.ps.ast.expr.ValP;
import org.smoothbuild.compile.ps.ast.refable.FuncP;
import org.smoothbuild.compile.ps.ast.refable.NamedValP;
import org.smoothbuild.compile.ps.ast.refable.PolyRefableP;
import org.smoothbuild.out.log.LogBuffer;
import org.smoothbuild.out.log.Logs;
import org.smoothbuild.util.Strings;

import com.google.common.collect.ImmutableSet;

public class DetectUndefinedRefs {
  private final Ast ast;
  private final Set<String> definedNames;
  private final LogBuffer logs;

  public DetectUndefinedRefs(Ast ast, Set<String> definedNames, LogBuffer logs) {
    this.ast = ast;
    this.definedNames = new HashSet<>(definedNames);
    this.logs = logs;
  }

  public static Logs detectUndefinedRefs(Ast ast, DefsS imported) {
    ImmutableSet<String> definedNames = imported.refables().asMap().keySet();
    return new DetectUndefinedRefs(ast, definedNames, new LogBuffer())
        .visit();
  }

  private Logs visit() {
    ast.structs().forEach(s -> definedNames.add(s.ctor().name()));
    ast.refables().forEach(polyRefableP -> {
      visitRefable(polyRefableP);
      definedNames.add(polyRefableP.name());
    });
    return logs;
  }

  private void visitRefable(PolyRefableP polyRefableP) {
    switch (polyRefableP) {
      case FuncP funcP -> visitFunc(funcP);
      case NamedValP namedValP -> visitVal(namedValP);
    }
  }

  private void visitFunc(FuncP funcP) {
    funcP.params().forEach(p -> p.body().ifPresent(this::visitExpr));
    funcP.body().ifPresent(body -> {
      var definedNamesWithParams = new HashSet<>(definedNames);
      funcP.params().forEach(p -> definedNamesWithParams.add(p.name()));
      new DetectUndefinedRefs(ast, definedNamesWithParams, logs).visitExpr(body);
    });
  }

  private void visitVal(NamedValP namedValP) {
    namedValP.body().ifPresent(this::visitExpr);
  }

  private void visitExprs(List<ExprP> exprs) {
    exprs.forEach(this::visitExpr);
  }

  private void visitExpr(ExprP expr) {
    switch (expr) {
      case CallP callP -> visitCall(callP);
      case ValP valP -> {}
      case NamedArgP namedArgP -> visitExpr(namedArgP.expr());
      case OrderP orderP -> visitExprs(orderP.elems());
      case RefP refP -> visitRef(refP);
      case SelectP selectP -> visitExpr(selectP.selectable());
      case DefaultArgP defaultArgP -> throw new RuntimeException("shouldn't happen");
    }
  }

  private void visitCall(CallP callP) {
    visitExpr(callP.callee());
    visitExprs(callP.args());
  }

  private void visitRef(RefP refP) {
    var name = refP.name();
    if (!definedNames.contains(name)) {
      logs.log(compileError(refP, Strings.q(name) + " is undefined."));
    }
  }
}
