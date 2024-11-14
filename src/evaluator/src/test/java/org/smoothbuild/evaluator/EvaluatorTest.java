package org.smoothbuild.evaluator;

import static com.google.common.truth.Truth.assertThat;
import static com.google.inject.Guice.createInjector;
import static org.awaitility.Awaitility.await;
import static org.smoothbuild.common.bucket.base.FullPath.fullPath;
import static org.smoothbuild.common.bucket.base.Path.path;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.Map.map;
import static org.smoothbuild.common.collect.NList.nlist;
import static org.smoothbuild.common.task.Tasks.argument;
import static org.smoothbuild.common.testing.TestingFilesystem.saveBytecodeInJar;
import static org.smoothbuild.common.testing.TestingInitializer.runInitializations;
import static org.smoothbuild.evaluator.ScheduleEvaluate.scheduleEvaluateCore;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.bindings.ImmutableBindings;
import org.smoothbuild.common.bucket.base.Filesystem;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.task.Scheduler;
import org.smoothbuild.common.testing.ReportTestWiring;
import org.smoothbuild.compilerbackend.CompilerBackendWiring;
import org.smoothbuild.compilerfrontend.lang.define.SExpr;
import org.smoothbuild.compilerfrontend.lang.define.SNamedEvaluable;
import org.smoothbuild.compilerfrontend.testing.FrontendCompilerTestContext;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BExpr;
import org.smoothbuild.virtualmachine.testing.VmTestWiring;
import org.smoothbuild.virtualmachine.testing.func.bytecode.ReturnIdFunc;
import org.smoothbuild.virtualmachine.testing.func.nativ.ReturnAbc;
import org.smoothbuild.virtualmachine.testing.func.nativ.StringIdentity;

public class EvaluatorTest extends FrontendCompilerTestContext {
  @Nested
  class _evaluation {
    @Nested
    class _constant {
      @Test
      void blob() throws BytecodeException {
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
          var LambdaS = sLambda(nlist(), sInt(7));
          var callS = sCall(sInstantiate(LambdaS));
          assertEvaluation(callS, bInt(7));
        }

        @Test
        void call_lambda_returning_enclosing_func_param() throws BytecodeException {
          var lambdaS = sInstantiate(sLambda(nlist(), sParamRef(sIntType(), "p")));
          var funcS = sFunc("myFunc", nlist(sItem(sIntType(), "p")), sCall(lambdaS));
          var callS = sCall(sInstantiate(funcS), sInt(7));
          assertEvaluation(newInjector(), bindings(funcS), callS, bInt(7));
        }

        @Test
        void call_expression_function() throws BytecodeException {
          var funcS = sFunc("n", nlist(), sInt(7));
          var callS = sCall(sInstantiate(funcS));
          assertEvaluation(newInjector(), bindings(funcS), callS, bInt(7));
        }

        @Test
        void call_poly_expression_function() throws BytecodeException {
          var a = varA();
          var orderS = sOrder(a, sParamRef(a, "e"));
          var funcS = sFunc(sArrayType(a), "n", nlist(sItem(a, "e")), orderS);
          var callS = sCall(sInstantiate(list(sIntType()), funcS), sInt(7));
          assertEvaluation(newInjector(), bindings(funcS), callS, bArray(bIntType(), bInt(7)));
        }

        @Test
        void call_constructor() throws BytecodeException {
          var constructorS =
              sConstructor(sStructType("MyStruct", nlist(sSig(sIntType(), "field"))));
          var callS = sCall(sInstantiate(constructorS), sInt(7));
          assertEvaluation(newInjector(), bindings(constructorS), callS, bTuple(bInt(7)));
        }

        @Test
        void call_native_argless_func() throws Exception {
          var injector = newInjector();
          var funcS = sAnnotatedFunc(
              sNativeAnnotation(1, sString(ReturnAbc.class.getName())),
              sStringType(),
              "f",
              nlist());
          var callS = sCall(sInstantiate(funcS));
          var buildJar = fullPath(PROJECT, path("module.jar"));
          var filesystem = injector.getInstance(Filesystem.class);
          saveBytecodeInJar(filesystem, buildJar, list(ReturnAbc.class));
          assertEvaluation(injector, bindings(funcS), callS, bString("abc"));
        }

        @Test
        void call_native_func_with_param() throws Exception {
          var funcS = sAnnotatedFunc(
              sNativeAnnotation(1, sString(StringIdentity.class.getName())),
              sStringType(),
              "f",
              nlist(sItem(sStringType(), "p")));
          var callS = sCall(sInstantiate(funcS), sString("abc"));
          var injector = newInjector();
          var buildJar = fullPath(PROJECT, path("module.jar"));
          var filesystem = injector.getInstance(Filesystem.class);
          saveBytecodeInJar(filesystem, buildJar, list(StringIdentity.class));
          assertEvaluation(injector, bindings(funcS), callS, bString("abc"));
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
          var funcS = sFunc("n", nlist(sItem(sIntType(), "p")), sParamRef(sIntType(), "p"));
          var callS = sCall(sInstantiate(funcS), sInt(7));
          assertEvaluation(newInjector(), bindings(funcS), callS, bInt(7));
        }
      }

      @Nested
      class _select {
        @Test
        void select() throws BytecodeException {
          var structTS = sStructType("MyStruct", nlist(sSig(sIntType(), "f")));
          var constructorS = sConstructor(structTS);
          var callS = sCall(sInstantiate(constructorS), sInt(7));
          assertEvaluation(newInjector(), bindings(constructorS), sSelect(callS, "f"), bInt(7));
        }
      }
    }

    @Nested
    class _instantiate {
      @Nested
      class _lambda {
        @Test
        void mono_lambda() throws BytecodeException {
          assertEvaluation(sInstantiate(sLambda(sInt(7))), bLambda(bInt(7)));
        }

        @Test
        void poly_lambda() throws BytecodeException {
          var a = varA();
          var polyLambdaS = sLambda(nlist(sItem(a, "a")), sParamRef(a, "a"));
          var monoLambdaS = sInstantiate(list(sIntType()), polyLambdaS);
          assertEvaluation(monoLambdaS, bLambda(list(bIntType()), bReference(bIntType(), 0)));
        }
      }

      @Nested
      class _named_func {
        @Test
        void mono_expression_func() throws BytecodeException {
          assertEvaluation(intIdSFunc(), bIntIdLambda());
        }

        @Test
        void poly_expression_function() throws BytecodeException {
          var a = varA();
          var funcS = sFunc("n", nlist(sItem(a, "e")), sParamRef(a, "e"));
          var instantiateS = sInstantiate(list(sIntType()), funcS);
          assertEvaluation(newInjector(), bindings(funcS), instantiateS, bIntIdLambda());
        }

        @Test
        void ann_func() throws Exception {
          var injector = newInjector();
          var buildJar = fullPath(PROJECT, path("module.jar"));
          var filesystem = injector.getInstance(Filesystem.class);
          saveBytecodeInJar(filesystem, buildJar, list(ReturnIdFunc.class));
          var binaryName = ReturnIdFunc.class.getName();
          var bytecodeFuncS = sBytecodeFunc(binaryName, varA(), "f", nlist(sItem(varA(), "p")));
          var sExpr = sInstantiate(list(sIntType()), bytecodeFuncS);
          var expected = ReturnIdFunc.bytecode(bytecodeF(), map("A", bIntType()));
          assertEvaluation(injector, bindings(bytecodeFuncS), sExpr, expected);
        }

        @Test
        void constructor() throws BytecodeException {
          var constructorS =
              sConstructor(sStructType("MyStruct", nlist(sSig(sIntType(), "myField"))));
          assertEvaluation(
              constructorS, bLambda(list(bIntType()), bCombine(bReference(bIntType(), 0))));
        }
      }

      @Nested
      class _named_value {
        @Test
        void mono_expression_value() throws BytecodeException {
          var valueS = sValue(1, sIntType(), "name", sInt(7));
          assertEvaluation(newInjector(), bindings(valueS), sInstantiate(valueS), bInt(7));
        }

        @Test
        void poly_value() throws BytecodeException {
          var a = varA();
          var polyValue = sValue(1, sArrayType(a), "name", sOrder(a));
          var instantiatedValue = sInstantiate(list(sIntType()), polyValue);
          assertEvaluation(
              newInjector(), bindings(polyValue), instantiatedValue, bArray(bIntType()));
        }
      }

      @Nested
      class _constructor {
        @Test
        void constructor() throws BytecodeException {
          assertEvaluation(
              sConstructor(sStructType("MyStruct", nlist(sSig(sIntType(), "field")))),
              bLambda(
                  bLambdaType(bIntType(), bTupleType(bIntType())),
                  bCombine(bReference(bIntType(), 0))));
        }
      }
    }
  }

  private static Injector newInjector() {
    return createInjector(new AbstractModule() {
      @Override
      protected void configure() {
        install(new CompilerBackendWiring());
        install(new VmTestWiring());
        install(new ReportTestWiring());
      }
    });
  }

  private void assertEvaluation(SNamedEvaluable sNamedEvaluable, BExpr bExpr) {
    assertThat(evaluate(bindings(sNamedEvaluable), sInstantiate(sNamedEvaluable), newInjector()))
        .isEqualTo(bExpr);
  }

  private void assertEvaluation(SExpr sExpr, BExpr bExpr) {
    assertEvaluation(newInjector(), bindings(), sExpr, bExpr);
  }

  private void assertEvaluation(
      Injector injector, ImmutableBindings<SNamedEvaluable> evaluables, SExpr sExpr, BExpr bExpr) {
    assertThat(evaluate(evaluables, sExpr, injector)).isEqualTo(bExpr);
  }

  private BExpr evaluate(
      ImmutableBindings<SNamedEvaluable> evaluables, SExpr sExpr, Injector injector) {
    var bValues = evaluate(injector, evaluables, list(sExpr)).get().bValues();
    assertThat(bValues.size()).isEqualTo(1);
    return bValues.get(0);
  }

  private Maybe<EvaluatedExprs> evaluate(
      Injector injector, ImmutableBindings<SNamedEvaluable> evaluables, List<SExpr> exprs) {
    runInitializations(injector);
    var scheduler = injector.getInstance(Scheduler.class);
    var evaluatedExprs = scheduleEvaluateCore(scheduler, argument(exprs), argument(evaluables));
    await().until(() -> evaluatedExprs.toMaybe().isSome());
    return evaluatedExprs.get();
  }
}
