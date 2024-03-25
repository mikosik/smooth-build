package org.smoothbuild.compilerbackend;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.smoothbuild.common.bindings.Bindings.immutableBindings;
import static org.smoothbuild.common.bucket.base.FullPath.fullPath;
import static org.smoothbuild.common.bucket.base.Path.path;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.NList.nlist;
import static org.smoothbuild.common.testing.TestingBucketId.bucketId;
import static org.smoothbuild.commontesting.AssertCall.assertCall;
import static org.smoothbuild.compilerfrontend.lang.type.SVarSet.varSetS;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.bindings;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.emptySArrayValue;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.idSFunc;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.location;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sAnnotatedFunc;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sAnnotatedValue;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sBlob;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sBlobType;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sBytecode;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sCall;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sCombine;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sInt;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sIntType;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sLambda;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sNativeFunc;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sOrder;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sParamRef;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sSelect;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sSig;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sString;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sStringType;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sStructType;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.userModuleFullPath;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.varA;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.varB;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.bindings.ImmutableBindings;
import org.smoothbuild.common.bucket.base.FullPath;
import org.smoothbuild.compilerfrontend.lang.base.location.Location;
import org.smoothbuild.compilerfrontend.lang.define.SExpr;
import org.smoothbuild.compilerfrontend.lang.define.SNamedEvaluable;
import org.smoothbuild.compilerfrontend.testing.TestingSExpression;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BBlob;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BCall;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BExpr;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BLambda;
import org.smoothbuild.virtualmachine.bytecode.load.BytecodeLoader;
import org.smoothbuild.virtualmachine.bytecode.load.FilePersister;
import org.smoothbuild.virtualmachine.testing.TestingVirtualMachine;
import org.smoothbuild.virtualmachine.testing.func.bytecode.ReturnAbc;
import org.smoothbuild.virtualmachine.testing.func.bytecode.ReturnIdFunc;
import org.smoothbuild.virtualmachine.testing.func.bytecode.ReturnReturnAbcFunc;

public class SbTranslatorTest extends TestingVirtualMachine {
  @Nested
  class _translate {
    @Nested
    class _named {
      @Nested
      class _named_value {
        @Test
        public void mono_expression_value() throws Exception {
          var valueS = TestingSExpression.sValue("myValue", TestingSExpression.sInt(7));
          assertTranslation(valueS, bCall(bLambda(bInt(7))));
        }

        @Test
        public void poly_expression_value() throws Exception {
          var emptyArrayVal = TestingSExpression.emptySArrayValue();
          var instantiateS = TestingSExpression.sInstantiate(list(sIntType()), emptyArrayVal);
          var orderB = bOrder(bIntType());
          assertTranslation(bindings(emptyArrayVal), instantiateS, bCall(bLambda(orderB)));
        }

        @Test
        public void mono_expression_value_referencing_other_expression_value() throws Exception {
          var otherValue = TestingSExpression.sValue("otherValue", TestingSExpression.sInt(7));
          var myValue =
              TestingSExpression.sValue("myValue", TestingSExpression.sInstantiate(otherValue));
          assertTranslation(
              bindings(otherValue, myValue),
              TestingSExpression.sInstantiate(myValue),
              bCall(bLambda(bCall(bLambda(bInt(7))))));
        }

        @Test
        public void
            poly_expression_value_instantitaed_with_type_param_of_enclosing_value_type_param()
                throws Exception {
          var a = varA();
          var b = varB();

          var emptyArrayValueS = emptySArrayValue(a);
          var instantiatedEmptyArrayValueS =
              TestingSExpression.sInstantiate(list(b), emptyArrayValueS);

          var referencingValueS =
              TestingSExpression.sValue("referencing", instantiatedEmptyArrayValueS);
          var instantiatedReferencingValueS =
              TestingSExpression.sInstantiate(list(sIntType()), referencingValueS);

          var orderB = bOrder(bIntType());
          assertTranslation(
              bindings(emptyArrayValueS, referencingValueS),
              instantiatedReferencingValueS,
              bCall(bLambda(bCall(bLambda(orderB)))));
        }

        @Test
        public void mono_native_value() throws Exception {
          var fullPath = fullPath(bucketId("prj"), path("my/path"));
          var classBinaryName = "class.binary.name";
          var nativeAnnotation = TestingSExpression.sNativeAnnotation(
              location(fullPath, 1), TestingSExpression.sString(classBinaryName));
          var nativeValueS =
              sAnnotatedValue(nativeAnnotation, sStringType(), "myValue", location(fullPath, 2));

          var jar = bBlob(37);
          var filePersister = createFilePersisterMock(fullPath.withExtension("jar"), jar);
          var translator = sbTranslator(filePersister, bindings(nativeValueS));

          assertCall(() -> translator.translateExpr(TestingSExpression.sInstantiate(nativeValueS)))
              .throwsException(new SbTranslatorException("Illegal value annotation: `@Native`."));
        }

        @Test
        public void mono_bytecode_value() throws Exception {
          var clazz = ReturnAbc.class;
          var fullPath = fullPath(bucketId("prj"), path("my/path"));
          var classBinaryName = clazz.getCanonicalName();
          var ann = sBytecode(TestingSExpression.sString(classBinaryName), location(fullPath, 1));
          var bytecodeValueS =
              sAnnotatedValue(ann, sStringType(), "myValue", location(fullPath, 2));

          var filePersister = createFilePersisterMock(
              fullPath.withExtension("jar"), blobBJarWithJavaByteCode(clazz));
          assertTranslation(
              filePersister,
              bindings(bytecodeValueS),
              TestingSExpression.sInstantiate(bytecodeValueS),
              bString("abc"));
        }

        @Test
        public void poly_bytecode_value() throws Exception {
          var clazz = ReturnIdFunc.class;
          var a = varA();
          var funcTS = TestingSExpression.sFuncType(a, a);
          var fullPath = userModuleFullPath();
          var classBinaryName = clazz.getCanonicalName();
          var ann = sBytecode(TestingSExpression.sString(classBinaryName), location(fullPath, 1));
          var bytecodeValueS = TestingSExpression.sAnnotatedValue(2, ann, funcTS, "myFunc");

          var filePersister = createFilePersisterMock(
              fullPath.withExtension("jar"), blobBJarWithJavaByteCode(clazz));
          var instantiateS = TestingSExpression.sInstantiate(list(sIntType()), bytecodeValueS);
          assertTranslation(filePersister, bindings(bytecodeValueS), instantiateS, bIdFunc());
        }
      }

      @Nested
      class _named_func {
        @Test
        public void mono_expression_function() throws Exception {
          var funcS = TestingSExpression.sFunc("myFunc", nlist(), TestingSExpression.sInt(7));
          assertTranslation(funcS, bLambda(bInt(7)));
        }

        @Test
        public void poly_expression_function() throws Exception {
          var funcS = idSFunc();
          var instantiateS = TestingSExpression.sInstantiate(list(sIntType()), funcS);
          var lambdaB = bLambda(bFuncType(bIntType(), bIntType()), bReference(bIntType(), 0));
          assertTranslation(bindings(funcS), instantiateS, lambdaB);
        }

        @Test
        public void poly_expression_func_instantiated_with_type_param_of_enclosing_func_type_param()
            throws Exception {
          var b = varB();

          var idFuncS = idSFunc();
          var monoIdFuncS = TestingSExpression.sInstantiate(list(b), idFuncS);

          var bodyS = TestingSExpression.sCall(monoIdFuncS, TestingSExpression.sParamRef(b, "p"));
          var wrapFuncS =
              TestingSExpression.sFunc(b, "wrap", nlist(TestingSExpression.sItem(b, "p")), bodyS);
          var wrapMonoFuncS = TestingSExpression.sInstantiate(list(sIntType()), wrapFuncS);

          var idFuncB = bLambda(bFuncType(bIntType(), bIntType()), bReference(bIntType(), 0));
          var wrapFuncB =
              bLambda(bFuncType(bIntType(), bIntType()), bCall(idFuncB, bReference(bIntType(), 0)));
          assertTranslation(bindings(idFuncS, wrapFuncS), wrapMonoFuncS, wrapFuncB);
        }

        @Test
        public void mono_native_function() throws Exception {
          var fullPath = fullPath(bucketId("prj"), path("my/path"));
          var classBinaryName = "class.binary.name";
          var annotationS = TestingSExpression.sNativeAnnotation(
              location(fullPath, 1), TestingSExpression.sString(classBinaryName));
          var nativeFuncS = TestingSExpression.sAnnotatedFunc(
              annotationS, sIntType(), "myFunc", nlist(TestingSExpression.sItem(sBlobType())));

          var funcTB = bFuncType(bBlobType(), bIntType());
          var nativeFuncB = bNativeFunc(funcTB, bBlob(37), bString(classBinaryName), bBool(true));

          var filePersister = createFilePersisterMock(fullPath.withExtension("jar"), bBlob(37));
          assertTranslation(
              filePersister,
              bindings(nativeFuncS),
              TestingSExpression.sInstantiate(nativeFuncS),
              nativeFuncB);
        }

        @Test
        public void poly_native_function() throws Exception {
          var a = varA();
          var fullPath = fullPath(bucketId("prj"), path("my/path"));
          var classBinaryName = "class.binary.name";
          var annotationS = TestingSExpression.sNativeAnnotation(
              location(fullPath, 1), TestingSExpression.sString(classBinaryName));
          var nativeFuncS = TestingSExpression.sAnnotatedFunc(
              annotationS, a, "myIdentity", nlist(TestingSExpression.sItem(a, "param")));

          var funcTB = bFuncType(bIntType(), bIntType());
          var nativeFuncB = bNativeFunc(funcTB, bBlob(37), bString(classBinaryName), bBool(true));

          var filePersister = createFilePersisterMock(fullPath.withExtension("jar"), bBlob(37));
          var instantiateS = TestingSExpression.sInstantiate(list(sIntType()), nativeFuncS);
          assertTranslation(filePersister, bindings(nativeFuncS), instantiateS, nativeFuncB);
        }

        @Test
        public void mono_bytecode_function() throws Exception {
          var clazz = ReturnReturnAbcFunc.class;
          var fullPath = fullPath(bucketId("prj"), path("my/path"));
          var classBinaryName = clazz.getCanonicalName();
          var annotationS =
              sBytecode(TestingSExpression.sString(classBinaryName), location(fullPath, 1));
          var bytecodeFuncS =
              sAnnotatedFunc(annotationS, sStringType(), "myFunc", nlist(), location(fullPath, 2));

          var filePersister = createFilePersisterMock(
              fullPath.withExtension("jar"), blobBJarWithJavaByteCode(clazz));
          assertTranslation(
              filePersister,
              bindings(bytecodeFuncS),
              TestingSExpression.sInstantiate(bytecodeFuncS),
              bReturnAbcFunc());
        }

        @Test
        public void poly_bytecode_function() throws Exception {
          var clazz = ReturnIdFunc.class;
          var a = varA();
          var funcTS = TestingSExpression.sFuncType(a, a);
          var fullPath = userModuleFullPath();
          var classBinaryName = clazz.getCanonicalName();
          var ann = TestingSExpression.sBytecode(classBinaryName, location(fullPath, 1));
          var bytecodeFuncS = TestingSExpression.sAnnotatedFunc(
              1, ann, funcTS.result(), "myFunc", nlist(TestingSExpression.sItem(a, "p")));

          var filePersister = createFilePersisterMock(
              fullPath.withExtension("jar"), blobBJarWithJavaByteCode(clazz));
          var instantiateS = TestingSExpression.sInstantiate(list(sIntType()), bytecodeFuncS);
          assertTranslation(filePersister, bindings(bytecodeFuncS), instantiateS, bIdFunc());
        }
      }
    }

    @Nested
    class _expr {
      @Test
      public void blob() throws Exception {
        var blobS = TestingSExpression.sBlob(37);
        assertTranslation(blobS, bBlob(37));
      }

      @Test
      public void int_() throws Exception {
        var intS = TestingSExpression.sInt(1);
        assertTranslation(intS, bInt(1));
      }

      @Test
      public void string() throws Exception {
        var stringS = TestingSExpression.sString("abc");
        assertTranslation(stringS, bString("abc"));
      }

      @Test
      public void lambda() throws Exception {
        var lambda = TestingSExpression.sLambda(
            varSetS(varA()),
            nlist(TestingSExpression.sItem(varA(), "p")),
            TestingSExpression.sParamRef(varA(), "p"));
        var monoLambdaS = TestingSExpression.sInstantiate(list(sIntType()), lambda);
        assertTranslation(monoLambdaS, bLambda(list(bIntType()), bReference(bIntType(), 0)));
      }

      @Test
      public void lambda_referencing_param_of_enclosing_function() throws Exception {
        var monoLambdaS = TestingSExpression.sInstantiate(
            TestingSExpression.sLambda(TestingSExpression.sParamRef(sIntType(), "p")));
        var monoFuncS = TestingSExpression.sFunc(
            "myFunc", nlist(TestingSExpression.sItem(sIntType(), "p")), monoLambdaS);

        var bodyB = bLambda(bReference(bIntType(), 0));
        var lambdaB = bLambda(bFuncType(bIntType(), bFuncType(bIntType())), bodyB);

        assertTranslation(monoFuncS, lambdaB);
      }

      @Test
      public void lambda_with_param_and_referencing_param_of_enclosing_function() throws Exception {
        // myFunc(Int i) = (Blob b) -> i;
        var monoLambdaS = TestingSExpression.sInstantiate(TestingSExpression.sLambda(
            nlist(TestingSExpression.sItem(sBlobType(), "b")),
            TestingSExpression.sParamRef(sIntType(), "i")));
        var monoFuncS = TestingSExpression.sFunc(
            "myFunc", nlist(TestingSExpression.sItem(sIntType(), "i")), monoLambdaS);

        var bodyB = bLambda(list(bBlobType()), bReference(bIntType(), 1));
        var lambdaB = bLambda(list(bIntType()), bodyB);

        assertTranslation(monoFuncS, lambdaB);
      }

      @Test
      public void call() throws Exception {
        var funcS = TestingSExpression.sFunc("myFunc", nlist(), TestingSExpression.sString("abc"));
        var callS = TestingSExpression.sCall(TestingSExpression.sInstantiate(funcS));
        assertTranslation(bindings(funcS), callS, bCall(bLambda(bString("abc"))));
      }

      @Test
      public void combine() throws Exception {
        var combineS = sCombine(TestingSExpression.sInt(3), TestingSExpression.sString("abc"));
        assertTranslation(combineS, bCombine(bInt(3), bString("abc")));
      }

      @Test
      public void order() throws Exception {
        var orderS = TestingSExpression.sOrder(
            sIntType(), TestingSExpression.sInt(3), TestingSExpression.sInt(7));
        assertTranslation(orderS, bOrder(bInt(3), bInt(7)));
      }

      @Test
      public void param_ref() throws Exception {
        var funcS = TestingSExpression.sFunc(
            "f",
            nlist(TestingSExpression.sItem(sIntType(), "p")),
            TestingSExpression.sParamRef(sIntType(), "p"));
        assertTranslation(funcS, bIdFunc());
      }

      @Test
      public void param_ref_to_unknown_param_causes_exception() {
        var funcS = TestingSExpression.sFunc(
            "f",
            nlist(TestingSExpression.sItem(sIntType(), "p")),
            TestingSExpression.sParamRef(sIntType(), "p2"));
        assertCall(() -> newTranslator(bindings(funcS))
                .translateExpr(TestingSExpression.sInstantiate(funcS)))
            .throwsException(
                new SbTranslatorException("Cannot resolve `p2` at {prj}/build.smooth:1."));
      }

      @Test
      public void select() throws Exception {
        var structTS = sStructType("MyStruct", nlist(sSig(sStringType(), "field")));
        var constructorS = TestingSExpression.sConstructor(structTS);
        var callS = TestingSExpression.sCall(
            TestingSExpression.sInstantiate(constructorS), TestingSExpression.sString("abc"));
        var selectS = sSelect(callS, "field");

        var ctorB = bLambda(list(bStringType()), bCombine(bReference(bStringType(), 0)));
        var callB = bCall(ctorB, bString("abc"));
        assertTranslation(bindings(constructorS), selectS, bSelect(callB, bInt(0)));
      }

      @Test
      public void instantiated_poly_expr_twice_with_outer_instantiation_actually_setting_its_var()
          throws Exception {
        // regression test
        var monoLambdaS = TestingSExpression.sInstantiate(
            TestingSExpression.sLambda(varSetS(), TestingSExpression.sParamRef(varA(), "a")));
        var funcS = TestingSExpression.sFunc(
            "myFunc", nlist(TestingSExpression.sItem(varA(), "a")), monoLambdaS);
        var instantiateS = TestingSExpression.sInstantiate(list(sIntType()), funcS);

        var bodyB = bLambda(bReference(bIntType(), 0));
        var lambdaB = bLambda(bFuncType(bIntType(), bFuncType(bIntType())), bodyB);

        assertTranslation(bindings(funcS), instantiateS, lambdaB);
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
        public void expression_value() throws Exception {
          var valueS = TestingSExpression.sValue(3, "myValue", sInt(7, 37));
          assertValueNalMapping(
              bindings(valueS),
              TestingSExpression.sInstantiate(9, valueS),
              location(9),
              "myValue",
              location(3));
        }

        @Test
        public void expression_value_referencing_other_expression_value() throws Exception {
          var otherValueS = TestingSExpression.sValue(6, "otherValue", sInt(7, 37));
          var valueS =
              TestingSExpression.sValue(5, "myValue", TestingSExpression.sInstantiate(otherValueS));
          assertValueNalMapping(
              bindings(otherValueS, valueS),
              TestingSExpression.sInstantiate(9, valueS),
              location(9),
              "myValue",
              location(5));
        }

        @Test
        public void bytecode_value() throws Exception {
          var clazz = ReturnAbc.class;
          var fullPath = userModuleFullPath();
          var classBinaryName = clazz.getCanonicalName();
          var ann = sBytecode(TestingSExpression.sString(classBinaryName), location(fullPath, 7));
          var bytecodeValueS =
              sAnnotatedValue(ann, sStringType(), "myValue", location(fullPath, 8));

          var filePersister = createFilePersisterMock(
              fullPath.withExtension("jar"), blobBJarWithJavaByteCode(clazz));
          var sbTranslator = sbTranslator(filePersister, bindings(bytecodeValueS));
          var exprB =
              sbTranslator.translateExpr(TestingSExpression.sInstantiate(3, bytecodeValueS));
          assertNalMapping(sbTranslator, exprB, "myValue", location(8));
        }
      }

      @Nested
      class _named_func {
        @Test
        public void expression_function() throws Exception {
          var funcS = TestingSExpression.sFunc(7, "myFunc", nlist(), TestingSExpression.sInt(37));
          assertNalMapping(
              bindings(funcS), TestingSExpression.sInstantiate(3, funcS), "myFunc", location(7));
        }

        @Test
        public void expression_inside_expression_function_body() throws Exception {
          var funcS = TestingSExpression.sFunc(7, "myFunc", nlist(), sInt(8, 37));
          var sbTranslator = newTranslator(bindings(funcS));
          var funcB = (BLambda) sbTranslator.translateExpr(TestingSExpression.sInstantiate(funcS));
          var body = funcB.body();
          assertNalMapping(sbTranslator, body, null, location(8));
        }

        @Test
        public void native_func() throws Exception {
          var fullPath = userModuleFullPath();
          var classBinaryName = "class.binary.name";
          var annotationS = TestingSExpression.sNativeAnnotation(
              location(fullPath, 1), TestingSExpression.sString(classBinaryName));
          var nativeFuncS = TestingSExpression.sAnnotatedFunc(
              2, annotationS, sIntType(), "myFunc", nlist(TestingSExpression.sItem(sBlobType())));

          var filePersister = createFilePersisterMock(fullPath.withExtension("jar"), bBlob(37));
          var sbTranslator = sbTranslator(filePersister, bindings(nativeFuncS));
          assertNalMapping(
              sbTranslator, TestingSExpression.sInstantiate(3, nativeFuncS), "myFunc", location(2));
        }

        @Test
        public void bytecode_func() throws Exception {
          var clazz = ReturnReturnAbcFunc.class;
          var fullPath = userModuleFullPath();
          var classBinaryName = clazz.getCanonicalName();
          var ann = sBytecode(TestingSExpression.sString(classBinaryName), location(fullPath, 1));
          var bytecodeFuncS =
              sAnnotatedFunc(ann, sStringType(), "myFunc", nlist(), location(fullPath, 2));

          var filePersister = createFilePersisterMock(
              fullPath.withExtension("jar"), blobBJarWithJavaByteCode(clazz));
          var sbTranslator = sbTranslator(filePersister, bindings(bytecodeFuncS));
          assertNalMapping(
              sbTranslator, TestingSExpression.sInstantiate(bytecodeFuncS), "myFunc", location(2));
        }
      }
    }

    @Nested
    class _expr {
      @Test
      public void blob() throws Exception {
        var blobS = sBlob(7, 0x37);
        assertNalMapping(blobS, null, location(7));
      }

      @Test
      public void int_() throws Exception {
        var intS = sInt(7, 37);
        assertNalMapping(intS, null, location(7));
      }

      @Test
      public void string() throws Exception {
        var stringS = sString(7, "abc");
        assertNalMapping(stringS, null, location(7));
      }

      @Test
      public void lambda() throws Exception {
        var monoLambdaS =
            TestingSExpression.sInstantiate(sLambda(7, nlist(), TestingSExpression.sString("abc")));

        var sbTranslator = newTranslator();
        var bLambda = (BLambda) sbTranslator.translateExpr(monoLambdaS);
        var nameMapping = sbTranslator.bsMapping().nameMapping();
        var locationMapping = sbTranslator.bsMapping().locMapping();
        assertThat(nameMapping.get(bLambda.hash())).isEqualTo("<lambda>");
        assertThat(locationMapping.get(bLambda.hash())).isEqualTo(location(7));
      }

      @Test
      public void call() throws Exception {
        var funcS =
            TestingSExpression.sFunc(7, "myFunc", nlist(), TestingSExpression.sString("abc"));
        var call = sCall(8, TestingSExpression.sInstantiate(funcS));
        assertNalMapping(bindings(funcS), call, null, location(8));
      }

      @Test
      public void order() throws Exception {
        var order = sOrder(3, sIntType(), TestingSExpression.sInt(6), TestingSExpression.sInt(7));
        assertNalMapping(order, null, location(3));
      }

      @Test
      public void param_ref() throws Exception {
        var funcS = TestingSExpression.sFunc(
            4,
            "myFunc",
            nlist(TestingSExpression.sItem(sIntType(), "p")),
            sParamRef(5, sIntType(), "p"));
        var sbTranslator = newTranslator(bindings(funcS));
        var bLambda = (BLambda) sbTranslator.translateExpr(TestingSExpression.sInstantiate(funcS));
        assertNalMapping(sbTranslator, bLambda.body(), "p", location(5));
      }

      @Test
      public void select() throws Exception {
        var structTS = sStructType("MyStruct", nlist(sSig(sStringType(), "field")));
        var constructorS = TestingSExpression.sConstructor(structTS);
        var callS = TestingSExpression.sCall(
            TestingSExpression.sInstantiate(constructorS), TestingSExpression.sString("abc"));
        var selectS = TestingSExpression.sSelect(4, callS, "field");
        assertNalMapping(bindings(constructorS), selectS, null, location(4));
      }

      @Nested
      class _instantiate {
        @Test
        public void expression_value() throws Exception {
          var emptyArrayVal =
              TestingSExpression.sValue(7, "emptyArray", TestingSExpression.sOrder(varA()));
          var instantiateS = TestingSExpression.sInstantiate(4, list(sIntType()), emptyArrayVal);
          assertNalMapping(bindings(emptyArrayVal), instantiateS, null, location(4));
        }

        @Test
        public void expression_function() throws Exception {
          var identity = idSFunc();
          var instantiateS = TestingSExpression.sInstantiate(list(sIntType()), identity);
          assertNalMapping(bindings(idSFunc()), instantiateS, "myId", location(1));
        }
      }
    }
  }

  @Nested
  class _caching {
    @Test
    public void expression_value_translation_result() throws Exception {
      var valueS = TestingSExpression.sValue("myVal", TestingSExpression.sString("abcdefghi"));
      assertTranslationIsCached(valueS);
    }

    @Test
    public void bytecode_value_translation_result() throws Exception {
      var clazz = ReturnAbc.class;
      var fullPath = fullPath(bucketId("prj"), path("my/path"));
      var classBinaryName = clazz.getCanonicalName();
      var ann = sBytecode(TestingSExpression.sString(classBinaryName), location(fullPath, 1));
      var bytecodeValueS = sAnnotatedValue(ann, sStringType(), "myFunc", location(fullPath, 2));
      var filePersister =
          createFilePersisterMock(fullPath.withExtension("jar"), blobBJarWithJavaByteCode(clazz));

      assertTranslationIsCached(
          filePersister, bindings(bytecodeValueS), TestingSExpression.sInstantiate(bytecodeValueS));
    }

    @Test
    public void expression_function_translation_result() throws Exception {
      var funcS =
          TestingSExpression.sFunc("myFunc", nlist(), TestingSExpression.sString("abcdefghi"));
      assertTranslationIsCached(funcS);
    }

    @Test
    public void native_function_translation_result() throws Exception {
      var funcS = sNativeFunc(sStringType(), "myFunc", nlist());
      assertTranslationIsCached(funcS);
    }

    @Test
    public void bytecode_function_translation_result() throws Exception {
      var clazz = ReturnReturnAbcFunc.class;
      var fullPath = fullPath(bucketId("prj"), path("my/path"));
      var classBinaryName = clazz.getCanonicalName();
      var ann = sBytecode(TestingSExpression.sString(classBinaryName), location(fullPath, 1));
      var bytecodeFuncS =
          sAnnotatedFunc(ann, sStringType(), "myFunc", nlist(), location(fullPath, 2));
      var filePersister =
          createFilePersisterMock(fullPath.withExtension("jar"), blobBJarWithJavaByteCode(clazz));

      assertTranslationIsCached(
          filePersister, bindings(bytecodeFuncS), TestingSExpression.sInstantiate(bytecodeFuncS));
    }

    @Test
    public void constructor_translation_result() throws Exception {
      var myStruct = sStructType("MyStruct", nlist(sSig(sStringType(), "name")));
      assertTranslationIsCached(TestingSExpression.sConstructor(myStruct));
    }

    @Test
    public void instantiated_poly_function_translation_result() throws Exception {
      var funcS = idSFunc();
      var instantiateS = TestingSExpression.sInstantiate(list(sIntType()), funcS);
      assertTranslationIsCached(bindings(funcS), instantiateS);
    }

    private void assertTranslationIsCached(SNamedEvaluable sNamedEvaluable) throws Exception {
      assertTranslationIsCached(
          bindings(sNamedEvaluable), TestingSExpression.sInstantiate(sNamedEvaluable));
    }

    private void assertTranslationIsCached(
        ImmutableBindings<SNamedEvaluable> evaluables, SExpr sExpr) throws Exception {
      assertTranslationIsCached(sExpr, newTranslator(evaluables));
    }

    private void assertTranslationIsCached(
        FilePersister filePersister, ImmutableBindings<SNamedEvaluable> evaluables, SExpr sExpr)
        throws SbTranslatorException {
      var sbTranslator = newTranslator(filePersister, evaluables);
      assertTranslationIsCached(sExpr, sbTranslator);
    }

    private void assertTranslationIsCached(SExpr sExpr, SbTranslator sbTranslator)
        throws SbTranslatorException {
      assertThat(sbTranslator.translateExpr(sExpr))
          .isSameInstanceAs(sbTranslator.translateExpr(sExpr));
    }
  }

  private void assertTranslation(
      ImmutableBindings<SNamedEvaluable> evaluables, SExpr sExpr, BExpr expected) throws Exception {
    assertTranslation(newTranslator(evaluables), sExpr, expected);
  }

  private void assertTranslation(SNamedEvaluable sNamedEvaluable, BExpr expectedB)
      throws Exception {
    assertTranslation(
        bindings(sNamedEvaluable), TestingSExpression.sInstantiate(sNamedEvaluable), expectedB);
  }

  private void assertTranslation(SExpr sExpr, BExpr expected) throws Exception {
    assertTranslation(newTranslator(), sExpr, expected);
  }

  private void assertTranslation(
      FilePersister filePersister,
      ImmutableBindings<SNamedEvaluable> evaluables,
      SExpr sExpr,
      BExpr expected)
      throws SbTranslatorException {
    var sbTranslator = newTranslator(filePersister, evaluables);
    assertTranslation(sbTranslator, sExpr, expected);
  }

  private void assertTranslation(SbTranslator sbTranslator, SExpr sExpr, BExpr expected)
      throws SbTranslatorException {
    assertThat(sbTranslator.translateExpr(sExpr)).isEqualTo(expected);
  }

  private void assertValueNalMapping(
      SExpr sExpr, Location expectedCallLocation, String expectedName, Location expectedLocation)
      throws Exception {
    assertValueNalMapping(
        newTranslator(), sExpr, expectedCallLocation, expectedName, expectedLocation);
  }

  private void assertValueNalMapping(
      ImmutableBindings<SNamedEvaluable> evaluables,
      SExpr sExpr,
      Location expectedCallLocation,
      String expectedName,
      Location expectedLocation)
      throws Exception {
    assertValueNalMapping(
        newTranslator(evaluables), sExpr, expectedCallLocation, expectedName, expectedLocation);
  }

  private static void assertValueNalMapping(
      SbTranslator sbTranslator,
      SExpr sExpr,
      Location expectedCallLocation,
      String expectedName,
      Location expectedLocation)
      throws Exception {
    var call = ((BCall) sbTranslator.translateExpr(sExpr));
    assertNalMapping(sbTranslator, call, null, expectedCallLocation);
    var called = call.subExprs().func();
    assertNalMapping(sbTranslator, called, expectedName, expectedLocation);
  }

  private void assertNalMapping(
      ImmutableBindings<SNamedEvaluable> evaluables,
      SExpr sExpr,
      String expectedName,
      Location expectedLocation)
      throws Exception {
    assertNalMapping(newTranslator(evaluables), sExpr, expectedName, expectedLocation);
  }

  private void assertNalMapping(SExpr sExpr, String expectedName, Location expectedLocation)
      throws Exception {
    assertNalMapping(newTranslator(), sExpr, expectedName, expectedLocation);
  }

  private void assertNalMapping(
      SbTranslator sbTranslator, SExpr sExpr, String expectedName, Location expectedLocation)
      throws SbTranslatorException {
    var exprB = sbTranslator.translateExpr(sExpr);
    assertNalMapping(sbTranslator, exprB, expectedName, expectedLocation);
  }

  private static void assertNalMapping(
      SbTranslator sbTranslator, BExpr expr, String expectedName, Location expectedLocation) {
    var bsMapping = sbTranslator.bsMapping();
    assertThat(bsMapping.nameMapping().get(expr.hash())).isEqualTo(expectedName);
    assertThat(bsMapping.locMapping().get(expr.hash())).isEqualTo(expectedLocation);
  }

  private SbTranslator newTranslator() throws Exception {
    return newTranslator(immutableBindings());
  }

  private SbTranslator newTranslator(ImmutableBindings<SNamedEvaluable> evaluables)
      throws Exception {
    var filePersister = mock(FilePersister.class);
    when(filePersister.persist(any())).thenReturn(bBlob(1));
    return sbTranslator(filePersister, evaluables);
  }

  private SbTranslator newTranslator(
      FilePersister filePersister, ImmutableBindings<SNamedEvaluable> evaluables) {
    return sbTranslator(filePersister, evaluables);
  }

  private FilePersister createFilePersisterMock(FullPath fullPath, BBlob bBlob)
      throws BytecodeException {
    FilePersister mock = mock(FilePersister.class);
    when(mock.persist(fullPath)).thenReturn(bBlob);
    return mock;
  }

  public SbTranslator sbTranslator(ImmutableBindings<SNamedEvaluable> evaluables) {
    return sbTranslator(filePersister(), evaluables);
  }

  public SbTranslator sbTranslator(
      FilePersister filePersister, ImmutableBindings<SNamedEvaluable> evaluables) {
    return sbTranslator(filePersister, bytecodeLoader(), evaluables);
  }

  private SbTranslator sbTranslator(
      FilePersister filePersister,
      BytecodeLoader bytecodeLoader,
      ImmutableBindings<SNamedEvaluable> evaluables) {
    return new SbTranslator(bytecodeF(), filePersister, bytecodeLoader, evaluables);
  }
}
