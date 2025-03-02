package org.smoothbuild.compilerbackend;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.commontesting.AssertCall.assertCall;
import static org.smoothbuild.compilerfrontend.lang.name.Bindings.bindings;
import static org.smoothbuild.compilerfrontend.lang.name.NList.nlist;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.filesystem.base.FullPath;
import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.define.SExpr;
import org.smoothbuild.compilerfrontend.lang.define.SNamedEvaluable;
import org.smoothbuild.compilerfrontend.lang.name.Bindings;
import org.smoothbuild.compilerfrontend.testing.FrontendCompilerTestContext;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BBlob;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BExpr;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BLambda;
import org.smoothbuild.virtualmachine.bytecode.load.BytecodeLoader;
import org.smoothbuild.virtualmachine.bytecode.load.FileContentReader;
import org.smoothbuild.virtualmachine.testing.func.bytecode.ReturnAbc;
import org.smoothbuild.virtualmachine.testing.func.bytecode.ReturnIdFunc;
import org.smoothbuild.virtualmachine.testing.func.bytecode.ReturnReturnAbcFunc;

public class SbTranslatorTest extends FrontendCompilerTestContext {
  @Nested
  class _translate {
    @Nested
    class _named {
      @Nested
      class _named_value {
        @Test
        void mono_expression_value() throws Exception {
          var valueS = sValue("myValue", sInt(7));
          assertTranslation(valueS, bInt(7));
        }

        @Test
        void poly_expression_value() throws Exception {
          var emptyArrayValue = emptySArrayValue();
          var instantiateS = sInstantiate(list(sIntType()), emptyArrayValue);
          var orderB = bOrder(bIntType());
          assertTranslation(bindings(emptyArrayValue), instantiateS, orderB);
        }

        @Test
        void mono_expression_value_referencing_other_expression_value() throws Exception {
          var otherValue = sValue("otherValue", sInt(7));
          var myValue = sValue("myValue", sInstantiate(otherValue));
          assertTranslation(bindings(otherValue, myValue), sInstantiate(myValue), bInt(7));
        }

        @Test
        void poly_expression_value_instantiated_with_type_param_of_enclosing_value_type_param()
            throws Exception {
          var a = varA();
          var b = varB();

          var emptyArrayValueS = emptySArrayValue(a);
          var instantiatedEmptyArrayValueS = sInstantiate(list(b), emptyArrayValueS);

          var referencingValueS = sValue("referencing", instantiatedEmptyArrayValueS);
          var instantiatedReferencingValueS = sInstantiate(list(sIntType()), referencingValueS);

          var orderB = bOrder(bIntType());
          assertTranslation(
              bindings(emptyArrayValueS, referencingValueS), instantiatedReferencingValueS, orderB);
        }

        @Test
        void declaring_mono_native_value_fails() throws Exception {
          var path = moduleFullPath();
          var classBinaryName = "class.binary.name";
          var nativeAnnotation = sNativeAnnotation(location(path, 1), sString(classBinaryName));
          var nativeValueS =
              sAnnotatedValue(nativeAnnotation, sStringType(), "myValue", location(path, 2));

          var jar = bBlob(37);
          var fileContentReader = fileContentReaderMock(path.withExtension("jar"), jar);
          var translator = sbTranslator(fileContentReader, bindings(nativeValueS));

          assertCall(() -> translator.translateExpr(sInstantiate(nativeValueS)))
              .throwsException(new SbTranslatorException("Illegal value annotation: `@Native`."));
        }

        @Test
        void mono_bytecode_value() throws Exception {
          var clazz = ReturnAbc.class;
          var path = moduleFullPath();
          var classBinaryName = clazz.getCanonicalName();
          var ann = sBytecode(sString(classBinaryName), location(path, 1));
          var bytecodeValueS = sAnnotatedValue(ann, sStringType(), "myValue", location(path, 2));

          var fileContentReader =
              fileContentReaderMock(path.withExtension("jar"), blobBJarWithJavaByteCode(clazz));
          assertTranslation(
              fileContentReader,
              bindings(bytecodeValueS),
              sInstantiate(bytecodeValueS),
              bString("abc"));
        }

        @Test
        void poly_bytecode_value() throws Exception {
          var clazz = ReturnIdFunc.class;
          var a = varA();
          var funcTS = sFuncType(a, a);
          var fullPath = moduleFullPath();
          var classBinaryName = clazz.getCanonicalName();
          var ann = sBytecode(sString(classBinaryName), location(fullPath, 1));
          var bytecodeValueS = sAnnotatedValue(2, ann, funcTS, "myFunc");

          var fileContentReader =
              fileContentReaderMock(fullPath.withExtension("jar"), blobBJarWithJavaByteCode(clazz));
          var instantiateS = sInstantiate(list(sIntType()), bytecodeValueS);
          assertTranslation(
              fileContentReader, bindings(bytecodeValueS), instantiateS, bIntIdLambda());
        }
      }

      @Nested
      class _named_func {
        @Test
        void mono_expression_function() throws Exception {
          var funcS = sFunc("myFunc", nlist(), sInt(7));
          assertTranslation(funcS, bLambda(bInt(7)));
        }

        @Test
        void poly_expression_function() throws Exception {
          var funcS = idSFunc();
          var instantiateS = sInstantiate(list(sIntType()), funcS);
          var bLambda = bLambda(bIntIntLambdaType(), bReference(bIntType(), 0));
          assertTranslation(bindings(funcS), instantiateS, bLambda);
        }

        @Test
        void poly_expression_func_instantiated_with_type_param_of_enclosing_func_type_param()
            throws Exception {
          var b = varB();

          var idFuncS = idSFunc();
          var monoIdFuncS = sInstantiate(list(b), idFuncS);

          var bodyS = sCall(monoIdFuncS, sParamRef(b, "p"));
          var wrapFuncS = sFunc(b, "wrap", nlist(sItem(b, "p")), bodyS);
          var wrapMonoFuncS = sInstantiate(list(sIntType()), wrapFuncS);

          var bIdLambda = bLambda(bIntIntLambdaType(), bReference(bIntType(), 0));
          var bWrapLambda =
              bLambda(bIntIntLambdaType(), bCall(bIdLambda, bReference(bIntType(), 0)));
          assertTranslation(bindings(idFuncS, wrapFuncS), wrapMonoFuncS, bWrapLambda);
        }

        @Test
        void mono_native_function() throws Exception {
          var path = moduleFullPath();
          var jar = bBlob(37);
          var classBinaryName = "class.binary.name";
          var sAnnotation = sNativeAnnotation(location(path, 1), sString(classBinaryName));
          var sNativeFunc =
              sAnnotatedFunc(sAnnotation, sIntType(), "myFunc", nlist(sItem(sBlobType())));

          var bInvoke = bInvoke(
              bIntType(),
              bMethodTuple(jar, bString(classBinaryName)),
              bBool(true),
              bCombine(bReference(bBlobType(), 0)));
          var bLambda = bLambda(list(bBlobType()), bInvoke);

          var fileContentReader = fileContentReaderMock(path.withExtension("jar"), jar);
          assertTranslation(
              fileContentReader, bindings(sNativeFunc), sInstantiate(sNativeFunc), bLambda);
        }

        @Test
        void poly_native_function() throws Exception {
          var a = varA();
          var path = moduleFullPath();
          var jar = bBlob(37);
          var classBinaryName = "class.binary.name";
          var annotationS = sNativeAnnotation(location(path, 1), sString(classBinaryName));
          var sNativeFunc = sAnnotatedFunc(annotationS, a, "myIdentity", nlist(sItem(a, "param")));

          var bInvoke = bInvoke(
              bIntType(),
              bMethodTuple(jar, bString(classBinaryName)),
              bBool(true),
              bCombine(bReference(bIntType(), 0)));
          var bLambda = bLambda(list(bIntType()), bInvoke);

          var fileContentReader = fileContentReaderMock(path.withExtension("jar"), jar);
          var instantiateS = sInstantiate(list(sIntType()), sNativeFunc);
          assertTranslation(fileContentReader, bindings(sNativeFunc), instantiateS, bLambda);
        }

        @Test
        void mono_bytecode_function() throws Exception {
          var clazz = ReturnReturnAbcFunc.class;
          var path = moduleFullPath();
          var classBinaryName = clazz.getCanonicalName();
          var annotationS = sBytecode(sString(classBinaryName), location(path, 1));
          var bytecodeFuncS =
              sAnnotatedFunc(annotationS, sStringType(), "myFunc", nlist(), location(path, 2));

          var fileContentReader =
              fileContentReaderMock(path.withExtension("jar"), blobBJarWithJavaByteCode(clazz));
          assertTranslation(
              fileContentReader,
              bindings(bytecodeFuncS),
              sInstantiate(bytecodeFuncS),
              bReturnAbcLambda());
        }

        @Test
        void poly_bytecode_function() throws Exception {
          var clazz = ReturnIdFunc.class;
          var a = varA();
          var funcTS = sFuncType(a, a);
          var fullPath = moduleFullPath();
          var classBinaryName = clazz.getCanonicalName();
          var ann = sBytecode(classBinaryName, location(fullPath, 1));
          var bytecodeFuncS =
              sAnnotatedFunc(1, ann, funcTS.result(), "myFunc", nlist(sItem(a, "p")));

          var fileContentReader =
              fileContentReaderMock(fullPath.withExtension("jar"), blobBJarWithJavaByteCode(clazz));
          var instantiateS = sInstantiate(list(sIntType()), bytecodeFuncS);
          assertTranslation(
              fileContentReader, bindings(bytecodeFuncS), instantiateS, bIntIdLambda());
        }
      }
    }

    @Nested
    class _expr {
      @Test
      void blob() throws Exception {
        var blobS = sBlob(37);
        assertTranslation(blobS, bBlob(37));
      }

      @Test
      void int_() throws Exception {
        var intS = sInt(1);
        assertTranslation(intS, bInt(1));
      }

      @Test
      void string() throws Exception {
        var stringS = sString("abc");
        assertTranslation(stringS, bString("abc"));
      }

      @Test
      void lambda() throws Exception {
        var lambda = sLambda(list(), nlist(sItem(sIntType(), "p")), sParamRef(sIntType(), "p"));
        assertTranslation(lambda, bLambda(list(bIntType()), bReference(bIntType(), 0)));
      }

      @Test
      void lambda_referencing_param_of_enclosing_function() throws Exception {
        var sLambda = sLambda(sParamRef(sIntType(), "p"));
        var sFunc = sFunc("myFunc", nlist(sItem(sIntType(), "p")), sLambda);

        var bBody = bLambda(bReference(bIntType(), 0));
        var bLambda = bLambda(bLambdaType(bIntType(), bIntLambdaType()), bBody);

        assertTranslation(sFunc, bLambda);
      }

      @Test
      void lambda_with_param_and_referencing_param_of_enclosing_function() throws Exception {
        // myFunc(Int i) = (Blob b) -> i;
        var sLambda = sLambda(nlist(sItem(sBlobType(), "b")), sParamRef(sIntType(), "i"));
        var sFunc = sFunc("myFunc", nlist(sItem(sIntType(), "i")), sLambda);

        var bBody = bLambda(list(bBlobType()), bReference(bIntType(), 1));
        var bLambda = bLambda(list(bIntType()), bBody);

        assertTranslation(sFunc, bLambda);
      }

      @Test
      void call() throws Exception {
        var funcS = sFunc("myFunc", nlist(), sString("abc"));
        var callS = sCall(sInstantiate(funcS));
        assertTranslation(bindings(funcS), callS, bCall(bLambda(bString("abc"))));
      }

      @Test
      void combine() throws Exception {
        var combineS = sCombine(sInt(3), sString("abc"));
        assertTranslation(combineS, bCombine(bInt(3), bString("abc")));
      }

      @Test
      void order() throws Exception {
        var orderS = sOrder(sIntType(), sInt(3), sInt(7));
        assertTranslation(orderS, bOrder(bInt(3), bInt(7)));
      }

      @Test
      void param_ref() throws Exception {
        var funcS = sFunc("f", nlist(sItem(sIntType(), "p")), sParamRef(sIntType(), "p"));
        assertTranslation(funcS, bIntIdLambda());
      }

      @Test
      void param_ref_to_unknown_param_causes_exception() {
        var funcS = sFunc("f", nlist(sItem(sIntType(), "p")), sParamRef(sIntType(), "p2"));
        assertCall(() -> newTranslator(bindings(funcS)).translateExpr(sInstantiate(funcS)))
            .throwsException(
                new SbTranslatorException("{t-project}/module.smooth:1: Cannot resolve `p2`."));
      }

      @Test
      void select() throws Exception {
        var sStructType = sStructType("MyStruct", nlist(sSig(sStringType(), "field")));
        var sConstructor = sConstructor(sStructType);
        var sCall = sCall(sInstantiate(sConstructor), sString("abc"));
        var sSelect = sSelect(sCall, "field");

        var bConstructor = bLambda(list(bStringType()), bCombine(bReference(bStringType(), 0)));
        var bCall = bCall(bConstructor, bString("abc"));
        assertTranslation(bindings(sConstructor), sSelect, bSelect(bCall, bInt(0)));
      }

      @Test
      void instantiated_poly_expr_twice_with_outer_instantiation_actually_setting_its_var()
          throws Exception {
        // regression test
        var sLambda = sLambda(list(), sParamRef(varA(), "a"));
        var sFunc = sFunc("myFunc", nlist(sItem(varA(), "a")), sLambda);
        var sInstantiate = sInstantiate(list(sIntType()), sFunc);

        var bBody = bLambda(bReference(bIntType(), 0));
        var bLambda = bLambda(bLambdaType(bIntType(), bIntLambdaType()), bBody);

        assertTranslation(bindings(sFunc), sInstantiate, bLambda);
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
        void expression_value() throws Exception {
          var valueS = sValue(3, "myValue", sInt(7, 37));
          Bindings<SNamedEvaluable> evaluables = bindings(valueS);
          SExpr sExpr = sInstantiate(9, valueS);
          assertNalMapping(evaluables, sExpr, null, location(7));
        }

        @Test
        void expression_value_referencing_other_expression_value() throws Exception {
          var otherValueS = sValue(6, "otherValue", sInt(7, 37));
          var valueS = sValue(5, "myValue", sInstantiate(otherValueS));
          Bindings<SNamedEvaluable> evaluables = bindings(otherValueS, valueS);
          SExpr sExpr = sInstantiate(9, valueS);
          assertNalMapping(evaluables, sExpr, null, location(7));
        }

        @Test
        void bytecode_value() throws Exception {
          var clazz = ReturnAbc.class;
          var fullPath = moduleFullPath();
          var classBinaryName = clazz.getCanonicalName();
          var ann = sBytecode(sString(classBinaryName), location(fullPath, 7));
          var bytecodeValueS =
              sAnnotatedValue(ann, sStringType(), "myValue", location(fullPath, 8));

          var fileContentReader =
              fileContentReaderMock(fullPath.withExtension("jar"), blobBJarWithJavaByteCode(clazz));
          var sbTranslator = sbTranslator(fileContentReader, bindings(bytecodeValueS));
          var exprB = sbTranslator.translateExpr(sInstantiate(3, bytecodeValueS));
          assertNalMapping(sbTranslator, exprB, "myValue", location(8));
        }
      }

      @Nested
      class _named_func {
        @Test
        void expression_function() throws Exception {
          var funcS = sFunc(7, "myFunc", nlist(), sInt(37));
          assertNalMapping(bindings(funcS), sInstantiate(3, funcS), "myFunc", location(7));
        }

        @Test
        void expression_inside_expression_function_body() throws Exception {
          var funcS = sFunc(7, "myFunc", nlist(), sInt(8, 37));
          var sbTranslator = newTranslator(bindings(funcS));
          var funcB = (BLambda) sbTranslator.translateExpr(sInstantiate(funcS));
          var body = funcB.body();
          assertNalMapping(sbTranslator, body, null, location(8));
        }

        @Test
        void native_func() throws Exception {
          var fullPath = moduleFullPath();
          var classBinaryName = "class.binary.name";
          var sAnnotation = sNativeAnnotation(location(fullPath, 1), sString(classBinaryName));
          var sNativeFunc =
              sAnnotatedFunc(2, sAnnotation, sIntType(), "myFunc", nlist(sItem(sBlobType())));

          var fileContentReader = fileContentReaderMock(fullPath.withExtension("jar"), bBlob(37));
          var sbTranslator = sbTranslator(fileContentReader, bindings(sNativeFunc));
          assertNalMapping(sbTranslator, sInstantiate(3, sNativeFunc), "myFunc", location(2));
        }

        @Test
        void bytecode_func() throws Exception {
          var clazz = ReturnReturnAbcFunc.class;
          var fullPath = moduleFullPath();
          var classBinaryName = clazz.getCanonicalName();
          var ann = sBytecode(sString(classBinaryName), location(fullPath, 1));
          var bytecodeFuncS =
              sAnnotatedFunc(ann, sStringType(), "myFunc", nlist(), location(fullPath, 2));

          var fileContentReader =
              fileContentReaderMock(fullPath.withExtension("jar"), blobBJarWithJavaByteCode(clazz));
          var sbTranslator = sbTranslator(fileContentReader, bindings(bytecodeFuncS));
          assertNalMapping(sbTranslator, sInstantiate(bytecodeFuncS), "myFunc", location(2));
        }
      }
    }

    @Nested
    class _expr {
      @Test
      void blob() throws Exception {
        var blobS = sBlob(7, 0x37);
        assertNalMapping(blobS, null, location(7));
      }

      @Test
      void int_() throws Exception {
        var intS = sInt(7, 37);
        assertNalMapping(intS, null, location(7));
      }

      @Test
      void string() throws Exception {
        var stringS = sString(7, "abc");
        assertNalMapping(stringS, null, location(7));
      }

      @Test
      void lambda() throws Exception {
        var sLambda = sLambda(7, nlist(), sString("abc"));

        var sbTranslator = newTranslator();
        var bLambda = (BLambda) sbTranslator.translateExpr(sLambda);
        var names = sbTranslator.bExprAttributes().names();
        var locations = sbTranslator.bExprAttributes().locations();
        assertThat(names.get(bLambda.hash())).isEqualTo("<lambda>");
        assertThat(locations.get(bLambda.hash())).isEqualTo(location(7));
      }

      @Test
      void call() throws Exception {
        var funcS = sFunc(7, "myFunc", nlist(), sString("abc"));
        var call = sCall(8, sInstantiate(funcS));
        assertNalMapping(bindings(funcS), call, null, location(8));
      }

      @Test
      void order() throws Exception {
        var order = sOrder(3, sIntType(), sInt(6), sInt(7));
        assertNalMapping(order, null, location(3));
      }

      @Test
      void param_ref() throws Exception {
        var funcS =
            sFunc(4, "myFunc", nlist(sItem(sIntType(), "p")), sParamRef(5, sIntType(), "p"));
        var sbTranslator = newTranslator(bindings(funcS));
        var bLambda = (BLambda) sbTranslator.translateExpr(sInstantiate(funcS));
        assertNalMapping(sbTranslator, bLambda.body(), "p", location(5));
      }

      @Test
      void select() throws Exception {
        var structTS = sStructType("MyStruct", nlist(sSig(sStringType(), "field")));
        var constructorS = sConstructor(structTS);
        var callS = sCall(sInstantiate(constructorS), sString("abc"));
        var selectS = sSelect(4, callS, "field");
        assertNalMapping(bindings(constructorS), selectS, null, location(4));
      }

      @Nested
      class _instantiate {
        @Test
        void expression_value() throws Exception {
          var sValue = sValue(7, "emptyArray", sOrder(8, varA()));
          var sInstantiate = sInstantiate(4, list(sIntType()), sValue);
          assertNalMapping(bindings(sValue), sInstantiate, null, location(8));
        }

        @Test
        void expression_function() throws Exception {
          var identity = idSFunc();
          var instantiateS = sInstantiate(list(sIntType()), identity);
          assertNalMapping(bindings(idSFunc()), instantiateS, "myId", location(1));
        }
      }
    }
  }

  @Nested
  class _caching {
    @Test
    void expression_value_translation_result() throws Exception {
      var valueS = sValue("myVal", sString("abcdefghi"));
      assertTranslationIsCached(valueS);
    }

    @Test
    void bytecode_value_translation_result() throws Exception {
      var clazz = ReturnAbc.class;
      var path = moduleFullPath();
      var classBinaryName = clazz.getCanonicalName();
      var ann = sBytecode(sString(classBinaryName), location(path, 1));
      var bytecodeValueS = sAnnotatedValue(ann, sStringType(), "myFunc", location(path, 2));
      var fileContentReader =
          fileContentReaderMock(path.withExtension("jar"), blobBJarWithJavaByteCode(clazz));

      assertTranslationIsCached(
          fileContentReader, bindings(bytecodeValueS), sInstantiate(bytecodeValueS));
    }

    @Test
    void expression_function_translation_result() throws Exception {
      var funcS = sFunc("myFunc", nlist(), sString("abcdefghi"));
      assertTranslationIsCached(funcS);
    }

    @Test
    void native_function_translation_result() throws Exception {
      var sNativeFunc = sNativeFunc(sStringType(), "myFunc", nlist());
      assertTranslationIsCached(sNativeFunc);
    }

    @Test
    void bytecode_function_translation_result() throws Exception {
      var clazz = ReturnReturnAbcFunc.class;
      var path = moduleFullPath();
      var classBinaryName = clazz.getCanonicalName();
      var ann = sBytecode(sString(classBinaryName), location(path, 1));
      var bytecodeFuncS = sAnnotatedFunc(ann, sStringType(), "myFunc", nlist(), location(path, 2));
      var fileContentReader =
          fileContentReaderMock(path.withExtension("jar"), blobBJarWithJavaByteCode(clazz));

      assertTranslationIsCached(
          fileContentReader, bindings(bytecodeFuncS), sInstantiate(bytecodeFuncS));
    }

    @Test
    void constructor_translation_result() throws Exception {
      var myStruct = sStructType("MyStruct", nlist(sSig(sStringType(), "name")));
      assertTranslationIsCached(sConstructor(myStruct));
    }

    @Test
    void instantiated_poly_function_translation_result() throws Exception {
      var funcS = idSFunc();
      var instantiateS = sInstantiate(list(sIntType()), funcS);
      assertTranslationIsCached(bindings(funcS), instantiateS);
    }

    private void assertTranslationIsCached(SNamedEvaluable sNamedEvaluable) throws Exception {
      assertTranslationIsCached(bindings(sNamedEvaluable), sInstantiate(sNamedEvaluable));
    }

    private void assertTranslationIsCached(Bindings<SNamedEvaluable> evaluables, SExpr sExpr)
        throws Exception {
      assertTranslationIsCached(sExpr, newTranslator(evaluables));
    }

    private void assertTranslationIsCached(
        FileContentReader fileContentReader, Bindings<SNamedEvaluable> evaluables, SExpr sExpr)
        throws SbTranslatorException {
      var sbTranslator = newTranslator(fileContentReader, evaluables);
      assertTranslationIsCached(sExpr, sbTranslator);
    }

    private void assertTranslationIsCached(SExpr sExpr, SbTranslator sbTranslator)
        throws SbTranslatorException {
      assertThat(sbTranslator.translateExpr(sExpr))
          .isSameInstanceAs(sbTranslator.translateExpr(sExpr));
    }
  }

  private void assertTranslation(Bindings<SNamedEvaluable> evaluables, SExpr sExpr, BExpr expected)
      throws Exception {
    assertTranslation(newTranslator(evaluables), sExpr, expected);
  }

  private void assertTranslation(SNamedEvaluable sNamedEvaluable, BExpr expectedB)
      throws Exception {
    assertTranslation(bindings(sNamedEvaluable), sInstantiate(sNamedEvaluable), expectedB);
  }

  private void assertTranslation(SExpr sExpr, BExpr expected) throws Exception {
    assertTranslation(newTranslator(), sExpr, expected);
  }

  private void assertTranslation(
      FileContentReader fileContentReader,
      Bindings<SNamedEvaluable> evaluables,
      SExpr sExpr,
      BExpr expected)
      throws SbTranslatorException {
    var sbTranslator = newTranslator(fileContentReader, evaluables);
    assertTranslation(sbTranslator, sExpr, expected);
  }

  private void assertTranslation(SbTranslator sbTranslator, SExpr sExpr, BExpr expected)
      throws SbTranslatorException {
    assertThat(sbTranslator.translateExpr(sExpr)).isEqualTo(expected);
  }

  private void assertNalMapping(
      Bindings<SNamedEvaluable> evaluables,
      SExpr sExpr,
      String expectedName,
      Location expectedLocation)
      throws Exception {
    var sbTranslator = newTranslator(evaluables);
    assertNalMapping(sbTranslator, sExpr, expectedName, expectedLocation);
  }

  private void assertNalMapping(SExpr sExpr, String expectedName, Location expectedLocation)
      throws Exception {
    assertNalMapping(newTranslator(), sExpr, expectedName, expectedLocation);
  }

  private static void assertNalMapping(
      SbTranslator sbTranslator, SExpr sExpr, String expectedName, Location expectedLocation)
      throws SbTranslatorException {
    var exprB = sbTranslator.translateExpr(sExpr);
    assertNalMapping(sbTranslator, exprB, expectedName, expectedLocation);
  }

  private static void assertNalMapping(
      SbTranslator sbTranslator, BExpr expr, String expectedName, Location expectedLocation) {
    var bExprAttributes = sbTranslator.bExprAttributes();
    assertThat(bExprAttributes.names().get(expr.hash())).isEqualTo(expectedName);
    assertThat(bExprAttributes.locations().get(expr.hash())).isEqualTo(expectedLocation);
  }

  private SbTranslator newTranslator() throws Exception {
    return newTranslator(bindings());
  }

  private SbTranslator newTranslator(Bindings<SNamedEvaluable> evaluables) throws Exception {
    var fileContentReader = mock(FileContentReader.class);
    when(fileContentReader.read(any())).thenReturn(bBlob(1));
    return sbTranslator(fileContentReader, evaluables);
  }

  private SbTranslator newTranslator(
      FileContentReader fileContentReader, Bindings<SNamedEvaluable> evaluables) {
    return sbTranslator(fileContentReader, evaluables);
  }

  private FileContentReader fileContentReaderMock(FullPath fullPath, BBlob bBlob) throws Exception {
    FileContentReader mock = mock(FileContentReader.class);
    when(mock.read(fullPath)).thenReturn(bBlob);
    return mock;
  }

  public SbTranslator sbTranslator(Bindings<SNamedEvaluable> evaluables) {
    return sbTranslator(fileContentReader(), evaluables);
  }

  public SbTranslator sbTranslator(
      FileContentReader fileContentReader, Bindings<SNamedEvaluable> evaluables) {
    return sbTranslator(fileContentReader, bytecodeLoader(), evaluables);
  }

  private SbTranslator sbTranslator(
      FileContentReader fileContentReader,
      BytecodeLoader bytecodeLoader,
      Bindings<SNamedEvaluable> evaluables) {
    return new SbTranslator(bytecodeF(), fileContentReader, bytecodeLoader, evaluables);
  }
}
