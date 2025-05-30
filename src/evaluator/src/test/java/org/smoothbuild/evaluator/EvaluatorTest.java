package org.smoothbuild.evaluator;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.schedule.Tasks.argument;
import static org.smoothbuild.common.testing.AwaitHelper.await;
import static org.smoothbuild.common.testing.TestingFileSystem.saveBytecodeInJar;
import static org.smoothbuild.compilerfrontend.lang.name.Bindings.bindings;
import static org.smoothbuild.compilerfrontend.lang.name.NList.nlist;

import java.util.Map;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.compilerfrontend.lang.define.SExpr;
import org.smoothbuild.compilerfrontend.lang.define.SPolyEvaluable;
import org.smoothbuild.compilerfrontend.lang.name.Bindings;
import org.smoothbuild.evaluator.dagger.EvaluatorTestContext;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BExpr;
import org.smoothbuild.virtualmachine.testing.func.bytecode.ReturnIdFunc;
import org.smoothbuild.virtualmachine.testing.func.nativ.ReturnAbc;
import org.smoothbuild.virtualmachine.testing.func.nativ.StringIdentity;

public class EvaluatorTest extends EvaluatorTestContext {
  @Nested
  class _evaluation {
    @Nested
    class _constant {
      @Test
      void blob() throws Exception {
        assertEvaluation(sBlob(7), bBlob(7));
      }

      @Test
      void int_() throws BytecodeException {
        assertEvaluation(sInt(8), bInt(8));
      }

      @Test
      void string() throws BytecodeException {
        assertEvaluation(sString("abc"), bString("abc"));
      }
    }

    @Nested
    class _expression {
      @Nested
      class _call {
        @Test
        void call_lambda() throws BytecodeException {
          var sLambda = sLambda(nlist(), sInt(7));
          var sCall = sCall(sLambda);
          assertEvaluation(sCall, bInt(7));
        }

        @Test
        void call_lambda_returning_enclosing_func_param() throws BytecodeException {
          var sLambda = sLambda(nlist(), sParamRef(sIntType(), "p"));
          var sFunc = sPoly(sFunc("myFunc", nlist(sItem(sIntType(), "p")), sCall(sLambda)));
          var sCall = sCall(sInstantiate(sFunc), sInt(7));
          assertEvaluation(bindings(sFunc), sCall, bInt(7));
        }

        @Test
        void call_lambda_returning_enclosing_func_param_after_it_has_been_returned_by_that_func()
            throws BytecodeException {
          var sLambda = sLambda(nlist(), sParamRef(sIntType(), "p"));
          var sFunc = sPoly(sFunc("myFunc", nlist(sItem(sIntType(), "p")), sLambda));
          var callFunc = sCall(sInstantiate(sFunc), sInt(7));
          var callReturnedLambda = sCall(callFunc);
          assertEvaluation(bindings(sFunc), callReturnedLambda, bInt(7));
        }

        @Test
        void call_expression_function() throws BytecodeException {
          var sFunc = sPoly(sFunc("n", nlist(), sInt(7)));
          var sCall = sCall(sInstantiate(sFunc));
          assertEvaluation(bindings(sFunc), sCall, bInt(7));
        }

        @Test
        void call_poly_expression_function() throws BytecodeException {
          var a = varA();
          var sOrder = sOrder(a, sParamRef(a, "e"));
          var sFunc = sPoly(list(a), sFunc(sArrayType(a), "n", nlist(sItem(a, "e")), sOrder));
          var sCall = sCall(sInstantiate(sFunc, list(sIntType())), sInt(7));
          assertEvaluation(bindings(sFunc), sCall, bArray(bIntType(), bInt(7)));
        }

        @Test
        void call_constructor() throws BytecodeException {
          var sConstructor =
              sPoly(sConstructor(sStructType("MyStruct", nlist(sSig(sIntType(), "field")))));
          var sCall = sCall(sInstantiate(sConstructor), sInt(7));
          assertEvaluation(bindings(sConstructor), sCall, bTuple(bInt(7)));
        }

        @Test
        void call_native_argless_func() throws Exception {
          var sFunc = sPoly(sAnnotatedFunc(
              sNativeAnnotation(1, sString(ReturnAbc.class.getName())),
              sStringType(),
              "f",
              nlist()));
          var sCall = sCall(sInstantiate(sFunc));
          var buildJar = provide().projectPath().append("module.jar");
          var fileSystem = provide().fileSystem();
          saveBytecodeInJar(fileSystem, buildJar, list(ReturnAbc.class));
          assertEvaluation(bindings(sFunc), sCall, bString("abc"));
        }

        @Test
        void call_native_func_with_param() throws Exception {
          var sFunc = sPoly(sAnnotatedFunc(
              sNativeAnnotation(1, sString(StringIdentity.class.getName())),
              sStringType(),
              "f",
              nlist(sItem(sStringType(), "p"))));
          var sCall = sCall(sInstantiate(sFunc), sString("abc"));
          var buildJar = provide().projectPath().append("module.jar");
          var fileSystem = provide().fileSystem();
          saveBytecodeInJar(fileSystem, buildJar, list(StringIdentity.class));
          assertEvaluation(bindings(sFunc), sCall, bString("abc"));
        }
      }

      @Nested
      class _combine {
        @Test
        void combine() throws BytecodeException {
          assertEvaluation(sCombine(sInt(7), sString("abc")), bTuple(bInt(7), bString("abc")));
        }
      }

      @Nested
      class _lambda {
        @Test
        void mono_lambda() throws BytecodeException {
          assertEvaluation(sLambda(sInt(7)), bLambda(bInt(7)));
        }
      }

      @Nested
      class _order {
        @Test
        void order() throws BytecodeException {
          assertEvaluation(
              sOrder(sIntType(), sInt(7), sInt(8)), bArray(bIntType(), bInt(7), bInt(8)));
        }
      }

      @Nested
      class _param_ref {
        @Test
        void param_ref() throws BytecodeException {
          var funcS = sPoly(sFunc("n", nlist(sItem(sIntType(), "p")), sParamRef(sIntType(), "p")));
          var callS = sCall(sInstantiate(funcS), sInt(7));
          assertEvaluation(bindings(funcS), callS, bInt(7));
        }
      }

      @Nested
      class _select {
        @Test
        void select() throws BytecodeException {
          var structTS = sStructType("MyStruct", nlist(sSig(sIntType(), "f")));
          var constructorS = sPoly(sConstructor(structTS));
          var callS = sCall(sInstantiate(constructorS), sInt(7));
          assertEvaluation(bindings(constructorS), sStructSelect(callS, "f"), bInt(7));
        }
      }
    }

    @Nested
    class _instantiate {
      @Nested
      class _named_func {
        @Test
        void mono_expression_func() throws BytecodeException {
          assertEvaluation(sPoly(intIdSFunc()), bIntIdLambda());
        }

        @Test
        void poly_expression_function() throws BytecodeException {
          var a = varA();
          var funcS = sPoly(list(varA()), sFunc("n", nlist(sItem(a, "e")), sParamRef(a, "e")));
          var instantiateS = sInstantiate(funcS, list(sIntType()));
          assertEvaluation(bindings(funcS), instantiateS, bIntIdLambda());
        }

        @Test
        void ann_func() throws Exception {
          var buildJar = provide().projectPath().append("module.jar");
          var fileSystem = provide().fileSystem();
          saveBytecodeInJar(fileSystem, buildJar, list(ReturnIdFunc.class));
          var binaryName = ReturnIdFunc.class.getName();
          var sBytecodeFunc = sPoly(
              list(varA()), sBytecodeFunc(binaryName, varA(), "f", nlist(sItem(varA(), "p"))));
          var sExpr = sInstantiate(sBytecodeFunc, list(sIntType()));
          var expected =
              ReturnIdFunc.bytecode(provide().bytecodeFactory(), Map.of("A", bIntType()));
          assertEvaluation(bindings(sBytecodeFunc), sExpr, expected);
        }

        @Test
        void constructor() throws BytecodeException {
          var constructorS =
              sPoly(sConstructor(sStructType("MyStruct", nlist(sSig(sIntType(), "myField")))));
          assertEvaluation(
              constructorS, bLambda(list(bIntType()), bCombine(bReference(bIntType(), 0))));
        }
      }

      @Nested
      class _named_value {
        @Test
        void mono_expression_value() throws BytecodeException {
          var valueS = sPoly(sValue(1, sIntType(), "name", sInt(7)));
          assertEvaluation(bindings(valueS), sInstantiate(valueS), bInt(7));
        }

        @Test
        void poly_value() throws BytecodeException {
          var a = varA();
          var polyValue = sPoly(list(a), sValue(1, sArrayType(a), "name", sOrder(a)));
          var instantiatedValue = sInstantiate(polyValue, list(sIntType()));
          assertEvaluation(bindings(polyValue), instantiatedValue, bArray(bIntType()));
        }
      }

      @Nested
      class _constructor {
        @Test
        void constructor() throws BytecodeException {
          assertEvaluation(
              sPoly(sConstructor(sStructType("MyStruct", nlist(sSig(sIntType(), "field"))))),
              bLambda(
                  bLambdaType(bIntType(), bTupleType(bIntType())),
                  bCombine(bReference(bIntType(), 0))));
        }
      }
    }
  }

  private void assertEvaluation(SPolyEvaluable sNamedEvaluable, BExpr bExpr) {
    assertThat(evaluate(bindings(sNamedEvaluable), sInstantiate(sNamedEvaluable)))
        .isEqualTo(bExpr);
  }

  private void assertEvaluation(SExpr sExpr, BExpr bExpr) {
    assertEvaluation(bindings(), sExpr, bExpr);
  }

  private void assertEvaluation(Bindings<SPolyEvaluable> evaluables, SExpr sExpr, BExpr bExpr) {
    assertThat(evaluate(evaluables, sExpr)).isEqualTo(bExpr);
  }

  private BExpr evaluate(Bindings<SPolyEvaluable> evaluables, SExpr sExpr) {
    var bValues = evaluate(evaluables, list(sExpr)).get().bValues();
    assertThat(bValues.size()).isEqualTo(1);
    return bValues.get(0);
  }

  private Maybe<EvaluatedExprs> evaluate(Bindings<SPolyEvaluable> evaluables, List<SExpr> exprs) {
    var scheduler = provide().scheduler();
    var evaluateCore = provide().evaluateCore();
    var evaluatedExprs = scheduler.submit(evaluateCore, argument(exprs), argument(evaluables));
    await().until(() -> evaluatedExprs.toMaybe().isSome());
    return evaluatedExprs.get();
  }
}
