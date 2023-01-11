package org.smoothbuild.compile.fs.ps.infer2;

import static org.smoothbuild.util.collect.Lists.map;

import org.smoothbuild.compile.fs.lang.type.ArrayTS;
import org.smoothbuild.compile.fs.lang.type.FuncTS;
import org.smoothbuild.compile.fs.lang.type.TypeS;
import org.smoothbuild.compile.fs.lang.type.VarS;
import org.smoothbuild.compile.fs.lang.type.tool.Unifier;
import org.smoothbuild.compile.fs.ps.ast.ModuleVisitorP;
import org.smoothbuild.compile.fs.ps.ast.define.ArrayTP;
import org.smoothbuild.compile.fs.ps.ast.define.ExprP;
import org.smoothbuild.compile.fs.ps.ast.define.FuncTP;
import org.smoothbuild.compile.fs.ps.ast.define.InstantiateP;
import org.smoothbuild.compile.fs.ps.ast.define.ItemP;
import org.smoothbuild.compile.fs.ps.ast.define.ModuleP;
import org.smoothbuild.compile.fs.ps.ast.define.NamedArgP;
import org.smoothbuild.compile.fs.ps.ast.define.NamedEvaluableP;
import org.smoothbuild.compile.fs.ps.ast.define.StructP;
import org.smoothbuild.compile.fs.ps.ast.define.TypeP;

public class TempVarAssigner {
  public static void assignTempVars(Unifier unifier, ModuleP moduleP) {
    new Assigner(unifier).visitModule(moduleP);
  }

  private static class Assigner extends ModuleVisitorP {
    private final Unifier unifier;

    public Assigner(Unifier unifier) {
      this.unifier = unifier;
    }

    @Override
    public void visitType(TypeP typeP) {
      typeP.setUnifierType(createUnifierType(typeP));
    }

    private TypeS createUnifierType(TypeP typeP) {
      return switch (typeP) {
        case ArrayTP arrayTP -> createUnifierType(arrayTP);
        case FuncTP funcTP -> createUnifierType(funcTP);
        default -> newTempVar();
      };
    }

    private ArrayTS createUnifierType(ArrayTP arrayTP) {
      return new ArrayTS(createUnifierType(arrayTP.elemT()));
    }

    private FuncTS createUnifierType(FuncTP funcTP) {
      var paramTs = map(funcTP.params(), this::createUnifierType);
      var resT = createUnifierType(funcTP.result());
      return new FuncTS(paramTs, resT);
    }

    @Override
    public void visitStruct(StructP structP) {
      super.visitStruct(structP);
      structP.setUnifierType(newTempVar());
    }

    @Override
    public void visitNamedEvaluable(NamedEvaluableP namedEvaluableP) {
      super.visitNamedEvaluable(namedEvaluableP);
      namedEvaluableP.setUnifierType(newTempVar());
    }

    @Override
    public void visitItem(ItemP itemP) {
      super.visitItem(itemP);
      itemP.setUnifierType(newTempVar());
    }

    @Override
    public void visitExpr(ExprP exprP) {
      super.visitExpr(exprP);
      exprP.setUnifierType(newTempVar());
    }

    @Override
    public void visitInstantiateP(InstantiateP instantiateP) {
      super.visitInstantiateP(instantiateP);
      instantiateP.setUnifierType(newTempVar());
    }

    @Override
    public void visitNamedArg(NamedArgP namedArg) {
      super.visitNamedArg(namedArg);
      namedArg.setUnifierType(newTempVar());
    }

    private VarS newTempVar() {
      return unifier.newTempVar();
    }
  }
}
