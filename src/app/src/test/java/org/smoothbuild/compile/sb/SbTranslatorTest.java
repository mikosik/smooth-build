package org.smoothbuild.compile.sb;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.smoothbuild.compile.fs.lang.type.VarSetS.varSetS;
import static org.smoothbuild.fs.base.PathS.path;
import static org.smoothbuild.fs.space.Space.PRJ;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.bindings.Bindings.immutableBindings;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.NList.nlist;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.compile.fs.lang.base.location.Location;
import org.smoothbuild.compile.fs.lang.define.ExprS;
import org.smoothbuild.compile.fs.lang.define.NamedEvaluableS;
import org.smoothbuild.fs.space.FilePath;
import org.smoothbuild.load.FileLoader;
import org.smoothbuild.testing.TestContext;
import org.smoothbuild.testing.func.bytecode.ReturnAbc;
import org.smoothbuild.testing.func.bytecode.ReturnIdFunc;
import org.smoothbuild.testing.func.bytecode.ReturnReturnAbcFunc;
import org.smoothbuild.util.bindings.ImmutableBindings;
import org.smoothbuild.vm.bytecode.expr.ExprB;
import org.smoothbuild.vm.bytecode.expr.oper.CallB;
import org.smoothbuild.vm.bytecode.expr.oper.ClosurizeB;
import org.smoothbuild.vm.bytecode.expr.value.BlobB;
import org.smoothbuild.vm.bytecode.expr.value.ExprFuncB;

public class SbTranslatorTest extends TestContext {
  @Nested
  class _translate {
    @Nested
    class _named {
      @Nested
      class _named_value {
        @Test
        public void mono_expression_value() {
          var valueS = valueS("myValue", intS(7));
          assertTranslation(valueS, callB(exprFuncB(intB(7))));
        }

        @Test
        public void poly_expression_value() {
          var emptyArrayVal = emptyArrayValueS();
          var instantiateS = instantiateS(list(intTS()), emptyArrayVal);
          var orderB = orderB(intTB());
          assertTranslation(bindings(emptyArrayVal), instantiateS, callB(exprFuncB(orderB)));
        }

        @Test
        public void mono_expression_value_referencing_other_expression_value() {
          var otherValue = valueS("otherValue", intS(7));
          var myValue = valueS("myValue", instantiateS(otherValue));
          assertTranslation(
              bindings(otherValue, myValue),
              instantiateS(myValue),
              callB(exprFuncB(callB(exprFuncB(intB(7))))));
        }

        @Test
        public void poly_expression_value_instantitaed_with_type_param_of_enclosing_value_type_param() {
          var a = varA();
          var b = varB();

          var emptyArrayValueS = emptyArrayValueS(a);
          var instantiatedEmptyArrayValueS = instantiateS(list(b), emptyArrayValueS);

          var referencingValueS = valueS("referencing", instantiatedEmptyArrayValueS);
          var instantiatedReferencingValueS = instantiateS(list(intTS()), referencingValueS);

          var orderB = orderB(intTB());
          assertTranslation(
              bindings(emptyArrayValueS, referencingValueS),
              instantiatedReferencingValueS,
              callB(exprFuncB(callB(exprFuncB(orderB)))));
        }

        @Test
        public void mono_native_value() {
          var filePath = filePath(PRJ, path("my/path"));
          var classBinaryName = "class.binary.name";
          var nativeAnnotation = nativeAnnotationS(location(filePath, 1), stringS(classBinaryName));
          var nativeValueS = annotatedValueS(
              nativeAnnotation, stringTS(), "myValue", location(filePath, 2));

          var jar = blobB(37);
          var fileLoader = createFileLoaderMock(filePath.withExtension("jar"), jar);
          var translator = sbTranslator(fileLoader, bindings(nativeValueS));

          assertCall(() -> translator.translateExpr(instantiateS(nativeValueS)))
              .throwsException(new SbTranslatorExc("Illegal value annotation: `@Native`."));
        }

        @Test
        public void mono_bytecode_value() throws IOException {
          var clazz = ReturnAbc.class;
          var filePath = filePath(PRJ, path("my/path"));
          var classBinaryName = clazz.getCanonicalName();
          var ann = bytecodeS(stringS(classBinaryName), location(filePath, 1));
          var bytecodeValueS = annotatedValueS(ann, stringTS(), "myValue", location(filePath, 2));

          var fileLoader = createFileLoaderMock(
              filePath.withExtension("jar"), blobBJarWithJavaByteCode(clazz));
          assertTranslation(
              fileLoader, bindings(bytecodeValueS), instantiateS(bytecodeValueS), stringB("abc"));
        }

        @Test
        public void poly_bytecode_value() throws IOException {
          var clazz = ReturnIdFunc.class;
          var a = varA();
          var funcTS = funcTS(a, a);
          var filePath = smoothFilePath();
          var classBinaryName = clazz.getCanonicalName();
          var ann = bytecodeS(stringS(classBinaryName), location(filePath, 1));
          var bytecodeValueS = annotatedValueS(2, ann, funcTS, "myFunc");

          var fileLoader = createFileLoaderMock(
              filePath.withExtension("jar"), blobBJarWithJavaByteCode(clazz));
          var instantiateS = instantiateS(list(intTS()), bytecodeValueS);
          assertTranslation(fileLoader, bindings(bytecodeValueS), instantiateS, idFuncB());
        }
      }

      @Nested
      class _named_func {
        @Test
        public void mono_expression_function() {
          var funcS = funcS("myFunc", nlist(), intS(7));
          assertTranslation(funcS, exprFuncB(intB(7)));
        }

        @Test
        public void poly_expression_function() {
          var funcS = idFuncS();
          var instantiateS = instantiateS(list(intTS()), funcS);
          var funcB = exprFuncB(funcTB(intTB(), intTB()), referenceB(intTB(), 0));
          assertTranslation(bindings(funcS), instantiateS, funcB);
        }

        @Test
        public void poly_expression_func_instantiated_with_type_param_of_enclosing_func_type_param() {
          var b = varB();

          var idFuncS = idFuncS();
          var monoIdFuncS = instantiateS(list(b), idFuncS);

          var bodyS = callS(monoIdFuncS, paramRefS(b, "p"));
          var wrapFuncS = funcS(b, "wrap", nlist(itemS(b, "p")), bodyS);
          var wrapMonoFuncS = instantiateS(list(intTS()), wrapFuncS);

          var idFuncB = exprFuncB(funcTB(intTB(), intTB()), referenceB(intTB(), 0));
          var wrapFuncB = exprFuncB(funcTB(intTB(), intTB()),
              callB(idFuncB, referenceB(intTB(), 0)));
          assertTranslation(bindings(idFuncS, wrapFuncS), wrapMonoFuncS, wrapFuncB);
        }

        @Test
        public void mono_native_function() {
          var filePath = filePath(PRJ, path("my/path"));
          var classBinaryName = "class.binary.name";
          var annotationS = nativeAnnotationS(location(filePath, 1), stringS(classBinaryName));
          var nativeFuncS = annotatedFuncS(annotationS, intTS(), "myFunc", nlist(itemS(blobTS())));

          var funcTB = funcTB(blobTB(), intTB());
          var nativeFuncB = nativeFuncB(funcTB, blobB(37), stringB(classBinaryName), boolB(true));

          var fileLoader = createFileLoaderMock(filePath.withExtension("jar"), blobB(37));
          assertTranslation(
              fileLoader, bindings(nativeFuncS), instantiateS(nativeFuncS), nativeFuncB);
        }

        @Test
        public void poly_native_function() {
          var a = varA();
          var filePath = filePath(PRJ, path("my/path"));
          var classBinaryName = "class.binary.name";
          var annotationS = nativeAnnotationS(location(filePath, 1), stringS(classBinaryName));
          var nativeFuncS = annotatedFuncS(annotationS, a, "myIdentity", nlist(itemS(a, "param")));

          var funcTB = funcTB(intTB(), intTB());
          var nativeFuncB = nativeFuncB(funcTB, blobB(37), stringB(classBinaryName), boolB(true));

          var fileLoader = createFileLoaderMock(filePath.withExtension("jar"), blobB(37));
          var instantiateS = instantiateS(list(intTS()), nativeFuncS);
          assertTranslation(fileLoader, bindings(nativeFuncS), instantiateS, nativeFuncB);
        }

        @Test
        public void mono_bytecode_function() throws IOException {
          var clazz = ReturnReturnAbcFunc.class;
          var filePath = filePath(PRJ, path("my/path"));
          var classBinaryName = clazz.getCanonicalName();
          var annotationS = bytecodeS(stringS(classBinaryName), location(filePath, 1));
          var bytecodeFuncS = annotatedFuncS(
              annotationS, stringTS(), "myFunc", nlist(), location(filePath, 2));

          var fileLoader = createFileLoaderMock(
              filePath.withExtension("jar"), blobBJarWithJavaByteCode(clazz));
          assertTranslation(
              fileLoader, bindings(bytecodeFuncS), instantiateS(bytecodeFuncS), returnAbcFuncB());
        }

        @Test
        public void poly_bytecode_function() throws IOException {
          var clazz = ReturnIdFunc.class;
          var a = varA();
          var funcTS = funcTS(a, a);
          var filePath = smoothFilePath();
          var classBinaryName = clazz.getCanonicalName();
          var ann = bytecodeS(classBinaryName, location(filePath, 1));
          var bytecodeFuncS = annotatedFuncS(
              1, ann, funcTS.result(), "myFunc", nlist(itemS(a, "p")));

          var fileLoader = createFileLoaderMock(
              filePath.withExtension("jar"), blobBJarWithJavaByteCode(clazz));
          var instantiateS = instantiateS(list(intTS()), bytecodeFuncS);
          assertTranslation(fileLoader, bindings(bytecodeFuncS), instantiateS, idFuncB());
        }

      }
    }

    @Nested
    class _expr {
      @Test
      public void blob() {
        var blobS = blobS(37);
        assertTranslation(blobS, blobB(37));
      }

      @Test
      public void int_() {
        var intS = intS(1);
        assertTranslation(intS, intB(1));
      }

      @Test
      public void string() {
        var stringS = stringS("abc");
        assertTranslation(stringS, stringB("abc"));
      }

      @Test
      public void lambda() {
        var lambda = lambdaS(varSetS(varA()), nlist(itemS(varA(), "p")), paramRefS(varA(), "p"));
        var monoLambdaS = instantiateS(list(intTS()), lambda);
        assertTranslation(monoLambdaS, closurizeB(list(intTB()), referenceB(intTB(), 0)));
      }

      @Test
      public void lambda_referencing_param_of_enclosing_function() {
        var monoLambdaS = instantiateS(lambdaS(paramRefS(intTS(), "p")));
        var monoFuncS = funcS("myFunc", nlist(itemS(intTS(), "p")), monoLambdaS);

        var bodyB = closurizeB(referenceB(intTB(), 0));
        var funcB = exprFuncB(funcTB(intTB(), funcTB(intTB())), bodyB);

        assertTranslation(monoFuncS, funcB);
      }

      @Test
      public void lambda_with_param_referencing_param_of_enclosing_function() {
        // myFunc(Int p) = (Blob a) -> p;
        var monoLambdaS = instantiateS(lambdaS(
            nlist(itemS(blobTS(), "a")), paramRefS(intTS(), "p")));
        var monoFuncS = funcS("myFunc", nlist(itemS(intTS(), "p")), monoLambdaS);

        var bodyB = closurizeB(list(blobTB()), referenceB(intTB(), 1));
        var funcB = exprFuncB(list(intTB()), bodyB);

        assertTranslation(monoFuncS, funcB);
      }

      @Test
      public void call() {
        var funcS = funcS("myFunc", nlist(), stringS("abc"));
        var callS = callS(instantiateS(funcS));
        assertTranslation(bindings(funcS), callS, callB(exprFuncB(stringB("abc"))));
      }

      @Test
      public void combine() {
        var combineS = combineS(intS(3), stringS("abc"));
        assertTranslation(combineS, combineB(intB(3), stringB("abc")));
      }

      @Test
      public void order() {
        var orderS = orderS(intTS(), intS(3), intS(7));
        assertTranslation(orderS, orderB(intB(3), intB(7)));
      }

      @Test
      public void param_ref() {
        var funcS = funcS("f", nlist(itemS(intTS(), "p")), paramRefS(intTS(), "p"));
        assertTranslation(funcS, idFuncB());
      }

      @Test
      public void param_ref_to_unknown_param_causes_exception() {
        var funcS = funcS("f", nlist(itemS(intTS(), "p")), paramRefS(intTS(), "p2"));
        assertCall(() -> newTranslator(bindings(funcS)).translateExpr(instantiateS(funcS)))
            .throwsException(
                new SbTranslatorExc("Cannot resolve `p2` at myBuild.smooth:1."));
      }

      @Test
      public void select() {
        var structTS = structTS("MyStruct", nlist(sigS(stringTS(), "field")));
        var constructorS = constructorS(structTS);
        var callS = callS(instantiateS(constructorS), stringS("abc"));
        var selectS = selectS(callS, "field");

        var ctorB = exprFuncB(list(stringTB()), combineB(referenceB(stringTB(), 0)));
        var callB = callB(ctorB, stringB("abc"));
        assertTranslation(bindings(constructorS), selectS, selectB(callB, intB(0)));
      }

      @Test
      public void instantiated_poly_expr_twice_with_outer_instantiation_actually_setting_its_var() {
        // regression test
        var monoLambdaS = instantiateS(lambdaS(varSetS(), paramRefS(varA(), "a")));
        var funcS = funcS("myFunc", nlist(itemS(varA(), "a")), monoLambdaS);
        var instantiateS = instantiateS(list(intTS()), funcS);

        var bodyB = closurizeB(referenceB(intTB(), 0));
        var funcB = exprFuncB(funcTB(intTB(), funcTB(intTB())), bodyB);

        assertTranslation(bindings(funcS), instantiateS, funcB);
      }
    }
  }

  @Nested
  class _bs_mapping {
    @Nested
    class _named {
      @Nested
      class _named_value {
        @Test
        public void expression_value() {
          var valueS = valueS(3, "myValue", intS(7, 37));
          assertValueNalMapping(
              bindings(valueS), instantiateS(9, valueS), location(9), "myValue", location(3));
        }

        @Test
        public void expression_value_referencing_other_expression_value() {
          var otherValueS = valueS(6, "otherValue", intS(7, 37));
          var valueS = valueS(5, "myValue", instantiateS(otherValueS));
          assertValueNalMapping(
              bindings(otherValueS, valueS),
              instantiateS(9, valueS),
              location(9),
              "myValue",
              location(5));
        }

        @Test
        public void bytecode_value() throws IOException {
          var clazz = ReturnAbc.class;
          var filePath = filePath();
          var classBinaryName = clazz.getCanonicalName();
          var ann = bytecodeS(stringS(classBinaryName), location(filePath, 7));
          var bytecodeValueS = annotatedValueS(ann, stringTS(), "myValue", location(filePath, 8));

          var fileLoader = createFileLoaderMock(
              filePath.withExtension("jar"), blobBJarWithJavaByteCode(clazz));
          var sbTranslator = sbTranslator(fileLoader, bindings(bytecodeValueS));
          var exprB = sbTranslator.translateExpr(instantiateS(3, bytecodeValueS));
          assertNalMapping(sbTranslator, exprB, "myValue", location(8));
        }
      }

      @Nested
      class _named_func {
        @Test
        public void expression_function() {
          var funcS = funcS(7, "myFunc", nlist(), intS(37));
          assertNalMapping(bindings(funcS), instantiateS(3, funcS), "myFunc", location(7));
        }

        @Test
        public void expression_inside_expression_function_body() {
          var funcS = funcS(7, "myFunc", nlist(), intS(8, 37));
          var sbTranslator = newTranslator(bindings(funcS));
          var funcB = (ExprFuncB) sbTranslator.translateExpr(instantiateS(funcS));
          var body = funcB.body();
          assertNalMapping(sbTranslator, body, null, location(8));
        }

        @Test
        public void native_func() {
          var filePath = filePath();
          var classBinaryName = "class.binary.name";
          var annotationS = nativeAnnotationS(location(filePath, 1), stringS(classBinaryName));
          var nativeFuncS = annotatedFuncS(
              2, annotationS, intTS(), "myFunc", nlist(itemS(blobTS())));

          var fileLoader = createFileLoaderMock(filePath.withExtension("jar"), blobB(37));
          var sbTranslator = sbTranslator(fileLoader, bindings(nativeFuncS));
          assertNalMapping(sbTranslator, instantiateS(3, nativeFuncS), "myFunc", location(2));
        }

        @Test
        public void bytecode_func() throws IOException {
          var clazz = ReturnReturnAbcFunc.class;
          var filePath = filePath();
          var classBinaryName = clazz.getCanonicalName();
          var ann = bytecodeS(stringS(classBinaryName), location(filePath, 1));
          var bytecodeFuncS = annotatedFuncS(
              ann, stringTS(), "myFunc", nlist(), location(filePath, 2));

          var fileLoader = createFileLoaderMock(
              filePath.withExtension("jar"), blobBJarWithJavaByteCode(clazz));
          var sbTranslator = sbTranslator(fileLoader, bindings(bytecodeFuncS));
          assertNalMapping(sbTranslator, instantiateS(bytecodeFuncS), "myFunc", location(2));
        }
      }
    }

    @Nested
    class _expr {
      @Test
      public void blob() {
        var blobS = blobS(7, 0x37);
        assertNalMapping(blobS, null, location(7));
      }

      @Test
      public void int_() {
        var intS = intS(7, 37);
        assertNalMapping(intS, null, location(7));
      }

      @Test
      public void string() {
        var stringS = stringS(7, "abc");
        assertNalMapping(stringS, null, location(7));
      }

      @Test
      public void lambda() {
        var monoLambdaS = instantiateS(lambdaS(7, nlist(), stringS("abc")));

        var sbTranslator = newTranslator();
        var closureB = (ClosurizeB) sbTranslator.translateExpr(monoLambdaS);
        var nameMapping = sbTranslator.bsMapping().nameMapping();
        var locationMapping = sbTranslator.bsMapping().locMapping();
        assertThat(nameMapping.get(closureB.hash()))
            .isEqualTo(null);
        assertThat(locationMapping.get(closureB.hash()))
            .isEqualTo(location(7));
        var exprFuncB = closureB.func();
        assertThat(nameMapping.get(exprFuncB.hash()))
            .isEqualTo("<lambda>");
        assertThat(locationMapping.get(exprFuncB.hash()))
            .isEqualTo(location(7));
      }

      @Test
      public void call() {
        var funcS = funcS(7, "myFunc", nlist(), stringS("abc"));
        var call = callS(8, instantiateS(funcS));
        assertNalMapping(bindings(funcS), call, null, location(8));
      }

      @Test
      public void order() {
        var order = orderS(3, intTS(), intS(6), intS(7));
        assertNalMapping(order, null, location(3));
      }

      @Test
      public void param_ref() {
        var funcS = funcS(4, "myFunc", nlist(itemS(intTS(), "p")), paramRefS(5, intTS(), "p"));
        var sbTranslator = newTranslator(bindings(funcS));
        var funcB = (ExprFuncB) sbTranslator.translateExpr(instantiateS(funcS));
        var refB = funcB.body();
        assertNalMapping(sbTranslator, refB, "p", location(5));
      }

      @Test
      public void select() {
        var structTS = structTS("MyStruct", nlist(sigS(stringTS(), "field")));
        var constructorS = constructorS(structTS);
        var callS = callS(instantiateS(constructorS), stringS("abc"));
        var selectS = selectS(4, callS, "field");
        assertNalMapping(bindings(constructorS), selectS, null, location(4));
      }

      @Nested
      class _instantiate {
        @Test
        public void expression_value() {
          var emptyArrayVal = valueS(7, "emptyArray", orderS(varA()));
          var instantiateS = instantiateS(4, list(intTS()), emptyArrayVal);
          assertNalMapping(bindings(emptyArrayVal), instantiateS, null, location(4));
        }

        @Test
        public void expression_function() {
          var identity = idFuncS();
          var instantiateS = instantiateS(list(intTS()), identity);
          assertNalMapping(bindings(idFuncS()), instantiateS, "myId", location(1));
        }
      }
    }
  }

  @Nested
  class _caching {
    @Test
    public void expression_value_translation_result() {
      var valueS = valueS("myVal", stringS("abcdefghi"));
      assertTranslationIsCached(valueS);
    }

    @Test
    public void bytecode_value_translation_result() throws IOException {
      var clazz = ReturnAbc.class;
      var filePath = filePath(PRJ, path("my/path"));
      var classBinaryName = clazz.getCanonicalName();
      var ann = bytecodeS(stringS(classBinaryName), location(filePath, 1));
      var bytecodeValueS = annotatedValueS(ann, stringTS(), "myFunc", location(filePath, 2));
      var fileLoader = createFileLoaderMock(
          filePath.withExtension("jar"), blobBJarWithJavaByteCode(clazz));

      assertTranslationIsCached(fileLoader, bindings(bytecodeValueS), instantiateS(bytecodeValueS));
    }

    @Test
    public void expression_function_translation_result() {
      var funcS = funcS("myFunc", nlist(), stringS("abcdefghi"));
      assertTranslationIsCached(funcS);
    }

    @Test
    public void native_function_translation_result() {
      var funcS = nativeFuncS(stringTS(), "myFunc", nlist());
      assertTranslationIsCached(funcS);
    }

    @Test
    public void bytecode_function_translation_result() throws IOException {
      var clazz = ReturnReturnAbcFunc.class;
      var filePath = filePath(PRJ, path("my/path"));
      var classBinaryName = clazz.getCanonicalName();
      var ann = bytecodeS(stringS(classBinaryName), location(filePath, 1));
      var bytecodeFuncS = annotatedFuncS(ann, stringTS(), "myFunc", nlist(), location(filePath, 2));
      var fileLoader = createFileLoaderMock(
          filePath.withExtension("jar"), blobBJarWithJavaByteCode(clazz));

      assertTranslationIsCached(fileLoader, bindings(bytecodeFuncS), instantiateS(bytecodeFuncS));
    }

    @Test
    public void constructor_translation_result() {
      var myStruct = structTS("MyStruct", nlist(sigS(stringTS(), "name")));
      assertTranslationIsCached(constructorS(myStruct));
    }

    @Test
    public void instantiated_poly_function_translation_result() {
      var funcS = idFuncS();
      var instantiateS = instantiateS(list(intTS()), funcS);
      assertTranslationIsCached(bindings(funcS), instantiateS);
    }

    private void assertTranslationIsCached(NamedEvaluableS namedEvaluableS) {
      assertTranslationIsCached(bindings(namedEvaluableS), instantiateS(namedEvaluableS));
    }

    private void assertTranslationIsCached(
        ImmutableBindings<NamedEvaluableS> evaluables, ExprS exprS) {
      assertTranslationIsCached(exprS, newTranslator(evaluables));
    }

    private void assertTranslationIsCached(
        FileLoader fileLoader, ImmutableBindings<NamedEvaluableS> evaluables, ExprS exprS) {
      var sbTranslator = newTranslator(fileLoader, evaluables);
      assertTranslationIsCached(exprS, sbTranslator);
    }

    private void assertTranslationIsCached(ExprS exprS, SbTranslator sbTranslator) {
      assertThat(sbTranslator.translateExpr(exprS))
          .isSameInstanceAs(sbTranslator.translateExpr(exprS));
    }
  }

  private void assertTranslation(
      ImmutableBindings<NamedEvaluableS> evaluables, ExprS exprS, ExprB expected) {
    assertTranslation(newTranslator(evaluables), exprS, expected);
  }

  private void assertTranslation(NamedEvaluableS namedEvaluableS, ExprB expectedB) {
    assertTranslation(bindings(namedEvaluableS), instantiateS(namedEvaluableS), expectedB);
  }

  private void assertTranslation(ExprS exprS, ExprB expected) {
    assertTranslation(newTranslator(), exprS, expected);
  }

  private void assertTranslation(
      FileLoader fileLoader,
      ImmutableBindings<NamedEvaluableS> evaluables,
      ExprS exprS,
      ExprB expected) {
    var sbTranslator = newTranslator(fileLoader, evaluables);
    assertTranslation(sbTranslator, exprS, expected);
  }

  private void assertTranslation(SbTranslator sbTranslator, ExprS exprS, ExprB expected) {
    assertThat(sbTranslator.translateExpr(exprS))
        .isEqualTo(expected);
  }

  private void assertValueNalMapping(
      ExprS exprS, Location expectedCallLocation, String expectedName, Location expectedLocation) {
    assertValueNalMapping(
        newTranslator(), exprS, expectedCallLocation, expectedName, expectedLocation);
  }

  private void assertValueNalMapping(
      ImmutableBindings<NamedEvaluableS> evaluables,
      ExprS exprS,
      Location expectedCallLocation,
      String expectedName,
      Location expectedLocation) {
    assertValueNalMapping(
        newTranslator(evaluables), exprS, expectedCallLocation, expectedName, expectedLocation);
  }

  private static void assertValueNalMapping(
      SbTranslator sbTranslator,
      ExprS exprS,
      Location expectedCallLocation,
      String expectedName,
      Location expectedLocation) {
    var call = ((CallB) sbTranslator.translateExpr(exprS));
    assertNalMapping(sbTranslator, call, null, expectedCallLocation);
    var called = call.subExprs().func();
    assertNalMapping(sbTranslator, called, expectedName, expectedLocation);
  }

  private void assertNalMapping(
      ImmutableBindings<NamedEvaluableS> evaluables,
      ExprS exprS,
      String expectedName,
      Location expectedLocation) {
    assertNalMapping(newTranslator(evaluables), exprS, expectedName, expectedLocation);
  }

  private void assertNalMapping(ExprS exprS, String expectedName, Location expectedLocation) {
    assertNalMapping(newTranslator(), exprS, expectedName, expectedLocation);
  }

  private void assertNalMapping(
      SbTranslator sbTranslator, ExprS exprS, String expectedName, Location expectedLocation) {
    var exprB = sbTranslator.translateExpr(exprS);
    assertNalMapping(sbTranslator, exprB, expectedName, expectedLocation);
  }

  private static void assertNalMapping(
      SbTranslator sbTranslator, ExprB exprB, String expectedName, Location expectedLocation) {
    var bsMapping = sbTranslator.bsMapping();
    assertThat(bsMapping.nameMapping().get(exprB.hash()))
        .isEqualTo(expectedName);
    assertThat(bsMapping.locMapping().get(exprB.hash()))
        .isEqualTo(expectedLocation);
  }

  private SbTranslator newTranslator() {
    return newTranslator(immutableBindings());
  }

  private SbTranslator newTranslator(ImmutableBindings<NamedEvaluableS> evaluables) {
    try {
      var fileLoader = mock(FileLoader.class);
      when(fileLoader.load(any())).thenReturn(blobB(1));
      return sbTranslator(fileLoader, evaluables);
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  private SbTranslator newTranslator(
      FileLoader fileLoader, ImmutableBindings<NamedEvaluableS> evaluables) {
    return sbTranslator(fileLoader, evaluables);
  }

  private FileLoader createFileLoaderMock(FilePath filePath, BlobB value) {
    try {
      FileLoader mock = mock(FileLoader.class);
      when(mock.load(filePath)).thenReturn(value);
      return mock;
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    }
  }
}
