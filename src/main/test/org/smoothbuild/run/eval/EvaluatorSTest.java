package org.smoothbuild.run.eval;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.smoothbuild.fs.base.PathS.path;
import static org.smoothbuild.fs.space.Space.PRJ;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.NList.nlist;

import java.math.BigInteger;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.compile.lang.define.ExprS;
import org.smoothbuild.compile.sb.BytecodeLoader;
import org.smoothbuild.load.FileLoader;
import org.smoothbuild.testing.TestContext;
import org.smoothbuild.testing.func.bytecode.ReturnIdFunc;
import org.smoothbuild.util.collect.Try;
import org.smoothbuild.vm.bytecode.expr.ExprB;
import org.smoothbuild.vm.bytecode.expr.value.ArrayB;
import org.smoothbuild.vm.bytecode.expr.value.IntB;
import org.smoothbuild.vm.bytecode.expr.value.TupleB;
import org.smoothbuild.vm.bytecode.type.value.TypeB;
import org.smoothbuild.vm.evaluate.plugin.NativeApi;
import org.smoothbuild.vm.evaluate.task.NativeMethodLoader;

import com.google.common.collect.ImmutableMap;

public class EvaluatorSTest extends TestContext {
  private final FileLoader fileLoader = mock(FileLoader.class);
  private final NativeMethodLoader nativeMethodLoader = mock(NativeMethodLoader.class);
  private final BytecodeLoader bytecodeLoader = mock(BytecodeLoader.class);

  @Nested
  class _evaluation {
    @Nested
    class _constant {
      @Test
      public void blob() throws EvaluatorExcS {
        assertThat(evaluate(blobS(7)))
            .isEqualTo(blobB(7));
      }

      @Test
      public void int_() throws EvaluatorExcS {
        assertThat(evaluate(intS(8)))
            .isEqualTo(intB(8));
      }

      @Test
      public void string() throws EvaluatorExcS {
        assertThat(evaluate(stringS("abc")))
            .isEqualTo(stringB("abc"));
      }
    }

    @Nested
    class _expression {
      @Nested
      class _call {
        @Test
        public void call_anonymous_func() throws EvaluatorExcS {
          var anonymousFuncS = anonymousFuncS(nlist(), intS(7));
          var callS = callS(monoizeS(anonymousFuncS));
          assertThat(evaluate(callS))
              .isEqualTo(intB(7));
        }

        @Test
        public void call_anonymous_function_returning_value_from_its_closure() throws EvaluatorExcS {
          var anonymousFunc = monoizeS(anonymousFuncS(nlist(), paramRefS(intTS(), "p")));
          var myFunc = monoizeS(funcS("myFunc", nlist(itemS(intTS(), "p")), callS(anonymousFunc)));
          var callS = callS(myFunc, intS(7));
          assertThat(evaluate(callS))
              .isEqualTo(intB(7));
        }

        @Test
        public void call_expression_function() throws EvaluatorExcS {
          var funcS = funcS("n", nlist(), intS(7));
          var callS = callS(monoizeS(funcS));
          assertThat(evaluate(callS))
              .isEqualTo(intB(7));
        }

        @Test
        public void call_poly_expression_function() throws EvaluatorExcS {
          var a = varA();
          var orderS = orderS(a, paramRefS(a, "e"));
          var funcS = funcS(arrayTS(a), "n", nlist(itemS(a, "e")), orderS);
          var callS = callS(monoizeS(varMap(a, intTS()), funcS), intS(7));
          assertThat(evaluate(callS))
              .isEqualTo(arrayB(intTB(), intB(7)));
        }

        @Test
        public void call_constructor() throws EvaluatorExcS {
          var constructorS = constructorS(structTS("MyStruct", nlist(sigS(intTS(), "field"))));
          var callS = callS(monoizeS(constructorS), intS(7));
          assertThat(evaluate(callS))
              .isEqualTo(tupleB(intB(7)));
        }

        @Test
        public void call_native_argless_func() throws Exception {
          var funcS = annotatedFuncS(
              nativeAnnotationS(1, stringS("class binary name")), intTS(), "f", nlist());
          var callS = callS(monoizeS(funcS));
          var jarB = blobB(137);
          when(fileLoader.load(filePath(PRJ, path("myBuild.jar"))))
              .thenReturn(jarB);
          when(nativeMethodLoader.load(any()))
              .thenReturn(Try.result(
                  EvaluatorSTest.class.getMethod("returnInt", NativeApi.class, TupleB.class)));
          assertThat(evaluate(callS))
              .isEqualTo(intB(173));
        }

        @Test
        public void call_native_func_with_param() throws Exception {
          var funcS = annotatedFuncS(nativeAnnotationS(1, stringS("class binary name")),
              intTS(), "f", nlist(itemS(intTS(), "p"))
          );
          var callS = callS(monoizeS(funcS), intS(77));
          var jarB = blobB(137);
          when(fileLoader.load(filePath(PRJ, path("myBuild.jar"))))
              .thenReturn(jarB);
          when(nativeMethodLoader.load(any()))
              .thenReturn(Try.result(
                  EvaluatorSTest.class.getMethod("returnIntParam", NativeApi.class, TupleB.class)));
          assertThat(evaluate(callS))
              .isEqualTo(intB(77));
        }
      }

      @Nested
      class _order {
        @Test
        public void order() throws EvaluatorExcS {
          assertThat(evaluate(orderS(intTS(), intS(7), intS(8))))
              .isEqualTo(arrayB(intTB(), intB(7), intB(8)));
        }
      }

      @Nested
      class param_ref {
        @Test
        public void param_ref() throws EvaluatorExcS {
          var func = monoizeS(funcS("n", nlist(itemS(intTS(), "p")), paramRefS(intTS(), "p")));
          var call = callS(func, intS(7));
          assertThat(evaluate(call))
              .isEqualTo(intB(7));
        }
      }

      @Nested
      class _select {
        @Test
        public void select() throws EvaluatorExcS {
          var structTS = structTS("MyStruct", nlist(sigS(intTS(), "f")));
          var constructorS = constructorS(structTS);
          var callS = callS(monoizeS(constructorS), intS(7));
          assertThat(evaluate(selectS(callS, "f")))
              .isEqualTo(intB(7));
        }
      }
    }

    @Nested
    class _monoizable {
      @Nested
      class _anonymous_function {
        @Test
        public void mono_anonymous_function() throws EvaluatorExcS {
          assertThat(evaluate(monoizeS(anonymousFuncS(intS(7)))))
              .isEqualTo(closureB(intB(7)));
        }

        @Test
        public void poly_anonymous_function() throws EvaluatorExcS {
          var a = varA();
          var polyAnonymousFuncS = anonymousFuncS(nlist(itemS(a, "a")), paramRefS(a, "a"));
          var monoAnonymousFuncS = monoizeS(varMap(a, intTS()), polyAnonymousFuncS);
          assertThat(evaluate(monoAnonymousFuncS))
              .isEqualTo(closureB(list(intTB()), refB(intTB(), 0)));
        }
      }

      @Nested
      class _named_func {
        @Test
        public void mono_expression_func() throws EvaluatorExcS {
          assertThat(evaluate(monoizeS(intIdFuncS())))
              .isEqualTo(idFuncB());
        }

        @Test
        public void poly_expression_function() throws EvaluatorExcS {
          var a = varA();
          var funcS = funcS("n", nlist(itemS(a, "e")), paramRefS(a, "e"));
          var monoizedS = monoizeS(varMap(a, intTS()), funcS);
          assertThat(evaluate(monoizedS))
              .isEqualTo(idFuncB());
        }

        @Test
        public void ann_func() throws Exception {
          var jar = blobB(123);
          var className = ReturnIdFunc.class.getCanonicalName();
          when(fileLoader.load(filePath(PRJ, path("myBuild.jar"))))
              .thenReturn(jar);
          var varMap = ImmutableMap.<String, TypeB>of("A", intTB());
          var funcB = ReturnIdFunc.bytecode(bytecodeF(), varMap);
          when(bytecodeLoader.load("myFunc", jar, className, varMap))
              .thenReturn(Try.result(funcB));

          var a = varA();
          var bytecodeFuncS = bytecodeFuncS(className, a, "myFunc", nlist(itemS(a, "p")));
          assertThat(evaluate(monoizeS(varMap(a, intTS()), bytecodeFuncS)))
              .isEqualTo(funcB);
        }

        @Test
        public void constructor() throws EvaluatorExcS {
          var constructorS = constructorS(structTS("MyStruct", nlist(sigS(intTS(), "myField"))));
          assertThat(evaluate(monoizeS(constructorS)))
              .isEqualTo(exprFuncB(list(intTB()), combineB(refB(intTB(), 0))));
        }
      }

      @Nested
      class _named_value {
        @Test
        public void mono_expression_value() throws EvaluatorExcS {
          var namedValue = monoizeS(valueS(1, intTS(), "name", intS(7)));
          assertThat(evaluate(namedValue))
              .isEqualTo(intB(7));
        }

        @Test
        public void poly_value() throws EvaluatorExcS {
          var a = varA();
          var polyValue = valueS(1, arrayTS(a), "name", orderS(a));
          var monoizedValue = monoizeS(varMap(a, intTS()), polyValue);
          assertThat(evaluate(monoizedValue))
              .isEqualTo(arrayB(intTB()));
        }
      }

      @Nested
      class _constructor {
        @Test
        public void constructor() throws EvaluatorExcS {
          var constructorS = constructorS(structTS("MyStruct", nlist(sigS(intTS(), "field"))));
          assertThat(evaluate(monoizeS(constructorS)))
              .isEqualTo(exprFuncB(funcTB(intTB(), tupleTB(intTB())), combineB(refB(intTB(), 0))));
        }
      }
    }
  }

  private ExprB evaluate(ExprS exprS) throws EvaluatorExcS {
    var resultMap = newEvaluator().evaluate(list(exprS)).get();
    assertThat(resultMap.size())
        .isEqualTo(1);
    return resultMap.get(0);
  }

  private EvaluatorS newEvaluator() {
    var sbTranslatorFacade = sbTranslatorFacade(fileLoader, bytecodeLoader);
    var vm = evaluatorB(nativeMethodLoader);
    return new EvaluatorS(sbTranslatorFacade, (bsMapping) -> vm, reporter());
  }

  public static IntB returnInt(NativeApi nativeApi, TupleB args) {
    return nativeApi.factory().int_(BigInteger.valueOf(173));
  }

  public static IntB returnIntParam(NativeApi nativeApi, TupleB args) {
    return (IntB) args.get(0);
  }

  public static ArrayB returnArrayParam(NativeApi nativeApi, TupleB args) {
    return (ArrayB) args.get(0);
  }
}
