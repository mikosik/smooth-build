package org.smoothbuild.compile.fs.ps.infer2;

import static org.smoothbuild.compile.fs.ps.CompileError.compileError;
import static org.smoothbuild.util.collect.Lists.generate;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.collect.NList.nlist;

import java.util.function.Supplier;

import org.smoothbuild.compile.fs.lang.define.ItemSigS;
import org.smoothbuild.compile.fs.lang.define.ScopeS;
import org.smoothbuild.compile.fs.lang.type.ArrayTS;
import org.smoothbuild.compile.fs.lang.type.BlobTS;
import org.smoothbuild.compile.fs.lang.type.FuncTS;
import org.smoothbuild.compile.fs.lang.type.IntTS;
import org.smoothbuild.compile.fs.lang.type.InterfaceTS;
import org.smoothbuild.compile.fs.lang.type.SchemaS;
import org.smoothbuild.compile.fs.lang.type.StringTS;
import org.smoothbuild.compile.fs.lang.type.StructTS;
import org.smoothbuild.compile.fs.lang.type.TypeS;
import org.smoothbuild.compile.fs.lang.type.tool.EqualityConstraint;
import org.smoothbuild.compile.fs.lang.type.tool.InstantiationConstraint;
import org.smoothbuild.compile.fs.lang.type.tool.Unifier;
import org.smoothbuild.compile.fs.lang.type.tool.UnifierExc;
import org.smoothbuild.compile.fs.ps.ast.ModuleVisitorP;
import org.smoothbuild.compile.fs.ps.ast.ScopingModuleVisitorP;
import org.smoothbuild.compile.fs.ps.ast.define.AnonymousFuncP;
import org.smoothbuild.compile.fs.ps.ast.define.ArrayTP;
import org.smoothbuild.compile.fs.ps.ast.define.BlobP;
import org.smoothbuild.compile.fs.ps.ast.define.CallP;
import org.smoothbuild.compile.fs.ps.ast.define.EvaluableP;
import org.smoothbuild.compile.fs.ps.ast.define.ExplicitTP;
import org.smoothbuild.compile.fs.ps.ast.define.ExprP;
import org.smoothbuild.compile.fs.ps.ast.define.FuncTP;
import org.smoothbuild.compile.fs.ps.ast.define.ImplicitTP;
import org.smoothbuild.compile.fs.ps.ast.define.InstantiateP;
import org.smoothbuild.compile.fs.ps.ast.define.IntP;
import org.smoothbuild.compile.fs.ps.ast.define.ItemP;
import org.smoothbuild.compile.fs.ps.ast.define.ModuleP;
import org.smoothbuild.compile.fs.ps.ast.define.NamedArgP;
import org.smoothbuild.compile.fs.ps.ast.define.NamedFuncP;
import org.smoothbuild.compile.fs.ps.ast.define.NamedValueP;
import org.smoothbuild.compile.fs.ps.ast.define.OrderP;
import org.smoothbuild.compile.fs.ps.ast.define.PolymorphicP;
import org.smoothbuild.compile.fs.ps.ast.define.ReferenceP;
import org.smoothbuild.compile.fs.ps.ast.define.ScopedP;
import org.smoothbuild.compile.fs.ps.ast.define.SelectP;
import org.smoothbuild.compile.fs.ps.ast.define.StringP;
import org.smoothbuild.compile.fs.ps.ast.define.StructP;
import org.smoothbuild.compile.fs.ps.ast.define.TypeP;
import org.smoothbuild.out.log.Log;
import org.smoothbuild.out.log.LogBuffer;
import org.smoothbuild.out.log.Logs;

import com.google.common.collect.ImmutableMap;

public class TypeUnifier {
  public static Logs unifyTypes(Unifier unifier, ScopeS importedS, ModuleP moduleP) {
    var logger = new LogBuffer();
    var typeTeller = new TypeTeller2(importedS, null);
    try {
      new UnifyingVisitor(typeTeller, unifier, logger).visitModule(moduleP);
    } catch (UnifyingFailedException e) {
      return logger;
    }
    return logger;
  }

  private static class UnifyingFailedException extends RuntimeException {}

  private static class UnifyingVisitor extends ScopingModuleVisitorP {
    private final TypeTeller2 typeTeller;
    private final Unifier unifier;
    private final LogBuffer logger;

    public UnifyingVisitor(TypeTeller2 typeTeller, Unifier unifier, LogBuffer logger) {
      this.typeTeller = typeTeller;
      this.unifier = unifier;
      this.logger = logger;
    }

    @Override
    protected ModuleVisitorP createVisitorForScopeOf(ScopedP scopedP) {
      var scopeTypeTeller = typeTeller.withScope(scopedP.scope());
      return new UnifyingVisitor(scopeTypeTeller, unifier, logger);
    }

    @Override
    public void visitStructSignature(StructP structP) {
      super.visitStructSignature(structP);
      var fieldSignatures = nlist(map(structP.fields(), UnifyingVisitor::newItemSigS));
      var structTS = new StructTS(structP.name(), fieldSignatures);
      unify(structP.unifierType(), structTS, () -> structDeclarationHasTypeErrors(structP));
    }

    private static Log structDeclarationHasTypeErrors(StructP structP) {
      return compileError(structP, structP.q() + " declaration has type errors.");
    }

    private static ItemSigS newItemSigS(ItemP itemP) {
      return new ItemSigS(itemP.unifierType(), itemP.shortName());
    }

    @Override
    public void visitNamedValueSignature(NamedValueP namedValueP) {
      super.visitNamedValueSignature(namedValueP);
      unifyEvaluationTypeWithBodyType(namedValueP.type(), namedValueP);
      unifyValueTypeWithResultType(namedValueP);
    }

    private void unifyValueTypeWithResultType(NamedValueP namedValueP) {
      unify(
          namedValueP.unifierType(),
          namedValueP.type().unifierType(),
          () -> compileError(namedValueP, namedValueP.q() + " type error unifying named value."));
    }

    @Override
    public void visitNamedFuncSignature(NamedFuncP namedFuncP) {
      super.visitNamedFuncSignature(namedFuncP);
      unifyEvaluationTypeWithBodyType(namedFuncP.resultT(), namedFuncP);
      unifyFuncTypeWithParamAndResultType(namedFuncP);
    }

    private void unifyFuncTypeWithParamAndResultType(NamedFuncP namedFuncP) {
      var paramTs = map(namedFuncP.params(), ItemP::unifierType);
      var resT = namedFuncP.resultT().unifierType();
      var funcTS = new FuncTS(paramTs, resT);
      unify(namedFuncP.unifierType(), funcTS, () -> typeErrorUnifyingNamedFunc(namedFuncP));
    }

    private static Log typeErrorUnifyingNamedFunc(NamedFuncP namedFuncP) {
      return compileError(namedFuncP, namedFuncP.q() + " type error unifying named function.");
    }

    private void unifyEvaluationTypeWithBodyType(
        TypeP evaluationType, EvaluableP evaluableP) {
      evaluableP.body().ifPresent(b -> unify(
          evaluationType.unifierType(),
          b.unifierType(),
          () -> bodyTypeNotEqualToDeclaredTypeError(evaluableP)));
    }

    private static Log bodyTypeNotEqualToDeclaredTypeError(EvaluableP evaluableP) {
      return compileError(evaluableP, evaluableP.q() + " body type is not equal to declared type.");
    }

    @Override
    public void visitItem(ItemP itemP) {
      super.visitItem(itemP);
      unify(itemP.unifierType(), itemP.type().unifierType(), () -> unifyingItemError(itemP));
    }

    private static Log unifyingItemError(ItemP itemP) {
      return compileError(itemP, " type error unifying " + itemP.q() + ".");
    }

    @Override
    public void visitType(TypeP typeP) {
      super.visitType(typeP);
      unify(typeP.unifierType(), inferTypeOf(typeP), () -> unifyingTypeError(typeP));
    }

    private TypeS inferTypeOf(TypeP typeP) {
      return switch (typeP) {
        case ArrayTP arrayTP -> inferTypeOf(arrayTP);
        case FuncTP funcTP -> inferTypeOf(funcTP);
        case ImplicitTP implicitTP -> implicitTP.unifierType();
        case ExplicitTP explicitTP -> typeTeller.typeWithName(explicitTP.name());
      };
    }

    private TypeS inferTypeOf(ArrayTP arrayTP) {
      return new ArrayTS(inferTypeOf(arrayTP.elemT()));
    }

    private TypeS inferTypeOf(FuncTP funcTP) {
      var paramTs = map(funcTP.params(), this::inferTypeOf);
      var resT = inferTypeOf(funcTP.result());
      return new FuncTS(paramTs, resT);
    }

    private static Log unifyingTypeError(TypeP typeP) {
      return compileError(typeP, " type error unifying " + typeP.q() + ".");
    }

    @Override
    public void visitBlob(BlobP blobP) {
      super.visitBlob(blobP);
      unify(blobP.unifierType(), new BlobTS(), () -> typeErrorUnifyingBlob(blobP));
    }

    private static Log typeErrorUnifyingBlob(BlobP blobP) {
      return compileError(blobP, " type error unifying Blob.");
    }

    @Override
    public void visitCall(CallP callP) {
      super.visitCall(callP);
      var args = map(callP.positionedArgs(), ExprP::unifierType);
      var funcTS = new FuncTS(args, callP.unifierType());
      unify(funcTS, callP.callee().unifierType(), () -> typeErrorUnifyingCall(callP));
    }

    private static Log typeErrorUnifyingCall(CallP callP) {
      return compileError(callP, " type error unifying call.");
    }

    @Override
    public void visitInt(IntP intP) {
      unify(intP.unifierType(), new IntTS(), () -> typeErrorUnifyingInt(intP));
    }

    private static Log typeErrorUnifyingInt(IntP intP) {
      return compileError(intP, " type error unifying Int.");
    }

    @Override
    public void visitAnonymousFuncSignature(AnonymousFuncP anonymousFuncP) {
      calculate schema so visitMonoize can use it
      anonymousFuncP.setSchemaS(schemaS);
      visitType(anonymousFuncP.resultT());
      visitItems(anonymousFuncP.params());
    }

    @Override
    public void visitInstantiateP(InstantiateP instantiateP) {
      super.visitInstantiateP(instantiateP);

      var polymorphicP = instantiateP.polymorphic();
      var schema = polymorphicP.schemaS();
      unifier.add(new InstantiationConstraint(instantiateP.unifierType(), polymorphicP.unifiedType()));

      TODO
//      instantiateP.setTypeArgs(generate(schema.quantifiedVars().size(), unifier::newTempVar));
    }

    @Override
    public void visitNamedArg(NamedArgP namedArgP) {
      super.visitNamedArg(namedArgP);
      unify(
          namedArgP.unifierType(),
          namedArgP.expr().unifierType(),
          () -> typeErrorUnifyingNamedArg(namedArgP));
    }

    private static Log typeErrorUnifyingNamedArg(NamedArgP namedArgP) {
      return compileError(namedArgP, " type error unifying named argument.");
    }

    @Override
    public void visitOrder(OrderP orderP) {
      super.visitOrder(orderP);
      for (var elem : orderP.elems()) {
        unify(
            orderP.unifierType(),
            new ArrayTS(elem.unifierType()),
            () -> typeErrorUnifyingOrder(orderP));
      }
    }

    private static Log typeErrorUnifyingOrder(OrderP orderP) {
      return compileError(orderP.location(), " type error unifying array literal.");
    }

    @Override
    public void visitSelect(SelectP selectP) {
      super.visitSelect(selectP);
      var itemSigS = new ItemSigS(selectP.unifierType(), selectP.field());
      var interfaceTS = new InterfaceTS(ImmutableMap.of(itemSigS.name(), itemSigS));
      unify(
          selectP.selectable().unifierType(), interfaceTS, () -> typeErrorUnifyingSelect(selectP));
    }

    private static Log typeErrorUnifyingSelect(SelectP selectP) {
      return compileError(selectP.location(), " type error unifying field selection.");
    }

    @Override
    public void visitReference(ReferenceP referenceP) {
      super.visitReference(referenceP);
      var schemaS = typeTeller.schemaFor(referenceP.name())
          .orElseThrow(UnifyingFailedException::new);
      referenceP.setSchemaS(schemaS);
    }

    @Override
    public void visitString(StringP stringP) {
      unify(stringP.unifierType(), new StringTS(), () -> typeErrorUnifyingString(stringP));
    }

    private static Log typeErrorUnifyingString(StringP stringP) {
      return compileError(stringP.location(), " type error unifying String.");
    }

    private void unify(TypeS type1, TypeS type2, Supplier<Log> x) {
      try {
        unifier.add(new EqualityConstraint(type1, type2));
      } catch (UnifierExc e) {
        logger.log(x.get());
      }
    }
  }
}
