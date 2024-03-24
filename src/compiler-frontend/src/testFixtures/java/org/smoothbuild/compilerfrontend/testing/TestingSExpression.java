package org.smoothbuild.compilerfrontend.testing;

import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_CAMEL;
import static org.smoothbuild.common.bindings.Bindings.immutableBindings;
import static org.smoothbuild.common.bucket.base.FullPath.fullPath;
import static org.smoothbuild.common.bucket.base.Path.path;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.List.listOfAll;
import static org.smoothbuild.common.collect.Map.map;
import static org.smoothbuild.common.collect.Maybe.none;
import static org.smoothbuild.common.collect.Maybe.some;
import static org.smoothbuild.common.collect.NList.nlist;
import static org.smoothbuild.common.io.Okios.intToByteString;
import static org.smoothbuild.common.testing.TestingBucketId.bucketId;
import static org.smoothbuild.compilerfrontend.lang.base.location.Locations.fileLocation;
import static org.smoothbuild.compilerfrontend.lang.define.SItem.toTypes;
import static org.smoothbuild.compilerfrontend.lang.type.AnnotationNames.BYTECODE;
import static org.smoothbuild.compilerfrontend.lang.type.AnnotationNames.NATIVE_IMPURE;
import static org.smoothbuild.compilerfrontend.lang.type.AnnotationNames.NATIVE_PURE;
import static org.smoothbuild.compilerfrontend.lang.type.SVarSet.varSetS;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.function.Function;
import org.smoothbuild.common.bindings.ImmutableBindings;
import org.smoothbuild.common.bucket.base.BucketId;
import org.smoothbuild.common.bucket.base.FullPath;
import org.smoothbuild.common.bucket.base.SynchronizedBucket;
import org.smoothbuild.common.bucket.mem.MemoryBucket;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.Map;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.collect.NList;
import org.smoothbuild.common.collect.Named;
import org.smoothbuild.compilerfrontend.compile.ast.define.CallP;
import org.smoothbuild.compilerfrontend.compile.ast.define.ExplicitTP;
import org.smoothbuild.compilerfrontend.compile.ast.define.ExprP;
import org.smoothbuild.compilerfrontend.compile.ast.define.ImplicitTP;
import org.smoothbuild.compilerfrontend.compile.ast.define.InstantiateP;
import org.smoothbuild.compilerfrontend.compile.ast.define.IntP;
import org.smoothbuild.compilerfrontend.compile.ast.define.ItemP;
import org.smoothbuild.compilerfrontend.compile.ast.define.LambdaP;
import org.smoothbuild.compilerfrontend.compile.ast.define.ModuleP;
import org.smoothbuild.compilerfrontend.compile.ast.define.NamedEvaluableP;
import org.smoothbuild.compilerfrontend.compile.ast.define.NamedFuncP;
import org.smoothbuild.compilerfrontend.compile.ast.define.NamedValueP;
import org.smoothbuild.compilerfrontend.compile.ast.define.PolymorphicP;
import org.smoothbuild.compilerfrontend.compile.ast.define.ReferenceP;
import org.smoothbuild.compilerfrontend.compile.ast.define.StructP;
import org.smoothbuild.compilerfrontend.lang.base.location.Location;
import org.smoothbuild.compilerfrontend.lang.define.SAnnotatedFunc;
import org.smoothbuild.compilerfrontend.lang.define.SAnnotatedValue;
import org.smoothbuild.compilerfrontend.lang.define.SAnnotation;
import org.smoothbuild.compilerfrontend.lang.define.SBlob;
import org.smoothbuild.compilerfrontend.lang.define.SCall;
import org.smoothbuild.compilerfrontend.lang.define.SCombine;
import org.smoothbuild.compilerfrontend.lang.define.SConstructor;
import org.smoothbuild.compilerfrontend.lang.define.SExpr;
import org.smoothbuild.compilerfrontend.lang.define.SInstantiate;
import org.smoothbuild.compilerfrontend.lang.define.SInt;
import org.smoothbuild.compilerfrontend.lang.define.SItem;
import org.smoothbuild.compilerfrontend.lang.define.SItemSig;
import org.smoothbuild.compilerfrontend.lang.define.SLambda;
import org.smoothbuild.compilerfrontend.lang.define.SNamedEvaluable;
import org.smoothbuild.compilerfrontend.lang.define.SNamedExprFunc;
import org.smoothbuild.compilerfrontend.lang.define.SNamedExprValue;
import org.smoothbuild.compilerfrontend.lang.define.SNamedValue;
import org.smoothbuild.compilerfrontend.lang.define.SOrder;
import org.smoothbuild.compilerfrontend.lang.define.SPolymorphic;
import org.smoothbuild.compilerfrontend.lang.define.SReference;
import org.smoothbuild.compilerfrontend.lang.define.SString;
import org.smoothbuild.compilerfrontend.lang.define.STrace;
import org.smoothbuild.compilerfrontend.lang.define.STrace.Element;
import org.smoothbuild.compilerfrontend.lang.define.SelectS;
import org.smoothbuild.compilerfrontend.lang.type.SArrayType;
import org.smoothbuild.compilerfrontend.lang.type.SBlobType;
import org.smoothbuild.compilerfrontend.lang.type.SBoolType;
import org.smoothbuild.compilerfrontend.lang.type.SFuncSchema;
import org.smoothbuild.compilerfrontend.lang.type.SFuncType;
import org.smoothbuild.compilerfrontend.lang.type.SIntType;
import org.smoothbuild.compilerfrontend.lang.type.SInterfaceType;
import org.smoothbuild.compilerfrontend.lang.type.SStringType;
import org.smoothbuild.compilerfrontend.lang.type.SStructType;
import org.smoothbuild.compilerfrontend.lang.type.STempVar;
import org.smoothbuild.compilerfrontend.lang.type.STupleType;
import org.smoothbuild.compilerfrontend.lang.type.SType;
import org.smoothbuild.compilerfrontend.lang.type.STypes;
import org.smoothbuild.compilerfrontend.lang.type.SVar;
import org.smoothbuild.compilerfrontend.lang.type.SVarSet;
import org.smoothbuild.compilerfrontend.lang.type.SchemaS;

public class TestingSExpression {
  public static final BucketId STANDARD_LIBRARY_BUCKET_ID = bucketId("ssl");
  public static final BucketId PROJECT_BUCKET_ID = bucketId("prj");
  public static final String BUILD_FILE_PATH = "build.smooth";
  private static final String IMPORTED_FILE_PATH = "imported.smooth";
  static final FullPath STANDARD_LIBRARY_MODULE_FILE_PATH =
      fullPath(STANDARD_LIBRARY_BUCKET_ID, path("std_lib.smooth"));
  static final FullPath DEFAULT_MODULE_FILE_PATH =
      fullPath(PROJECT_BUCKET_ID, path("build.smooth"));

  public static java.util.List<SType> typesToTest() {
    return nonCompositeTypes().stream()
        .flatMap(t -> compositeTypeSFactories().stream().map(f -> f.apply(t)))
        .toList();
  }

  public static List<SType> nonCompositeTypes() {
    return STypes.baseTypes().append(new SVar("A"));
  }

  public static java.util.List<Function<SType, SType>> compositeTypeSFactories() {
    java.util.List<Function<SType, SType>> simpleFactories = java.util.List.of(
        TestingSExpression::arrayTS,
        TestingSExpression::funcTS,
        t -> funcTS(t, intTS()),
        TestingSExpression::tupleTS,
        TestingSExpression::structTS,
        TestingSExpression::interfaceTS);
    java.util.List<Function<SType, SType>> factories = new ArrayList<>();
    factories.addAll(simpleFactories);
    for (var simpleFactory : simpleFactories) {
      for (var simpleFactory2 : simpleFactories) {
        Function<SType, SType> compositeFactory = t -> simpleFactory.apply(simpleFactory2.apply(t));
        factories.add(compositeFactory);
      }
    }
    return factories;
  }

  public static SArrayType arrayTS(SType elemT) {
    return new SArrayType(elemT);
  }

  public static SBlobType blobTS() {
    return STypes.BLOB;
  }

  public static SBoolType boolTS() {
    return STypes.BOOL;
  }

  public static SFuncType funcTS(SType resultT) {
    return funcTS(list(), resultT);
  }

  public static SFuncType funcTS(SType param1, SType resultT) {
    return funcTS(list(param1), resultT);
  }

  public static SFuncType funcTS(SType param1, SType param2, SType resultT) {
    return funcTS(list(param1, param2), resultT);
  }

  public static SFuncType funcTS(List<SType> paramTs, SType resultT) {
    return new SFuncType(tupleTS(paramTs), resultT);
  }

  public static STupleType tupleTS(SType... itemTs) {
    return tupleTS(list(itemTs));
  }

  public static STupleType tupleTS(List<SType> paramTs) {
    return new STupleType(paramTs);
  }

  public static SIntType intTS() {
    return STypes.INT;
  }

  public static SFuncSchema funcSchemaS(NList<SItem> params, SType resultT) {
    return funcSchemaS(toTypes(params.list()), resultT);
  }

  public static SFuncSchema funcSchemaS(SType resultT) {
    return funcSchemaS(funcTS(list(), resultT));
  }

  public static SFuncSchema funcSchemaS(SType param1, SType resultT) {
    return funcSchemaS(funcTS(list(param1), resultT));
  }

  public static SFuncSchema funcSchemaS(List<SType> paramTs, SType resultT) {
    return funcSchemaS(funcTS(paramTs, resultT));
  }

  private static SFuncSchema funcSchemaS(SFuncType sFuncType) {
    return funcSchemaS(sFuncType.vars(), sFuncType);
  }

  private static SFuncSchema funcSchemaS(SVarSet quantifiedVars, SFuncType sFuncType) {
    return new SFuncSchema(quantifiedVars, sFuncType);
  }

  public static SInterfaceType interfaceTS() {
    return interfaceTS(map());
  }

  public static SInterfaceType interfaceTS(SType... fieldTs) {
    return interfaceTS(typesToItemSigsMap(fieldTs));
  }

  public static SInterfaceType interfaceTS(SItemSig... fieldTs) {
    return interfaceTS(itemSigsToMap(fieldTs));
  }

  public static SInterfaceType interfaceTS(Map<String, SItemSig> fieldSignatures) {
    return new SInterfaceType(fieldSignatures);
  }

  public static SchemaS schemaS(SType sType) {
    return new SchemaS(sType.vars(), sType);
  }

  public static SStructType personTS() {
    return structTS("Person", nlist(sigS(stringTS(), "firstName"), sigS(stringTS(), "lastName")));
  }

  public static SStructType animalTS() {
    return structTS("Animal", nlist(sigS(stringTS(), "name"), sigS(intTS(), "size")));
  }

  public static SStringType stringTS() {
    return STypes.STRING;
  }

  public static SStructType structTS(SType... fieldTs) {
    return structTS("MyStruct", fieldTs);
  }

  public static SStructType structTS(String myStruct) {
    return structTS(myStruct, nlist());
  }

  public static SStructType structTS(String myStruct, SType... fieldTs) {
    return structTS(myStruct, nlist(typesToItemSigs(fieldTs)));
  }

  public static SStructType structTS(String myStruct, SItemSig... fieldSigs) {
    return structTS(myStruct, nlist(fieldSigs));
  }

  private static List<SItemSig> typesToItemSigs(SType... fieldTs) {
    var builder = new ArrayList<SItemSig>();
    for (int i = 0; i < fieldTs.length; i++) {
      builder.add(sigS(fieldTs[i], "param" + i));
    }
    return listOfAll(builder);
  }

  public static Map<String, SItemSig> typesToItemSigsMap(SType... types) {
    return itemSigsToMap(typeTsToSigS(types));
  }

  public static Map<String, SItemSig> itemSigsToMap(SItemSig... itemSigs) {
    return itemSigsToMap(list(itemSigs));
  }

  public static Map<String, SItemSig> itemSigsToMap(List<SItemSig> sigs) {
    return sigs.toMap(SItemSig::name, f -> f);
  }

  private static List<SItemSig> typeTsToSigS(SType... types) {
    var builder = new ArrayList<SItemSig>();
    for (int i = 0; i < types.length; i++) {
      builder.add(sigS(types[i], "param" + i));
    }
    return listOfAll(builder);
  }

  public static SStructType structTS(String name, NList<SItemSig> fields) {
    return new SStructType(name, fields);
  }

  public static SVar tempVarA() {
    return tempVar("1");
  }

  public static STempVar tempVar(String name) {
    return new STempVar(name);
  }

  public static SVar varA() {
    return varS("A");
  }

  public static SVar varB() {
    return varS("B");
  }

  public static SVar varC() {
    return varS("C");
  }

  public static SVar varX() {
    return varS("X");
  }

  public static SVar varS(String name) {
    return new SVar(name);
  }

  // ExprS-s

  public static SBlob blobS(int data) {
    return blobS(1, data);
  }

  public static SBlob blobS(int line, int data) {
    return new SBlob(blobTS(), intToByteString(data), location(line));
  }

  public static SCall callS(SExpr callable, SExpr... args) {
    return callS(1, callable, args);
  }

  public static SCall callS(int line, SExpr callable, SExpr... args) {
    return new SCall(callable, combineS(line, args), location(line));
  }

  public static SCombine combineS(SExpr... args) {
    return combineS(13, args);
  }

  private static SCombine combineS(int line, SExpr... args) {
    var argsList = list(args);
    var evaluationType = new STupleType(argsList.map(SExpr::evaluationType));
    return new SCombine(evaluationType, argsList, location(line));
  }

  public static SInt intS(int value) {
    return intS(1, value);
  }

  public static SInt intS(int line, int value) {
    return new SInt(intTS(), BigInteger.valueOf(value), location(line));
  }

  public static Map<SVar, SType> varMap() {
    return map();
  }

  public static Map<SVar, SType> varMap(SVar var, SType type) {
    return map(var, type);
  }

  public static SInstantiate instantiateS(SNamedEvaluable sNamedEvaluable) {
    return instantiateS(17, sNamedEvaluable);
  }

  public static SInstantiate instantiateS(int line, SNamedEvaluable sNamedEvaluable) {
    return instantiateS(line, referenceS(line, sNamedEvaluable));
  }

  public static SInstantiate instantiateS(List<SType> typeArgs, SNamedEvaluable sNamedEvaluable) {
    return instantiateS(1, typeArgs, sNamedEvaluable);
  }

  public static SInstantiate instantiateS(
      int line, List<SType> typeArgs, SNamedEvaluable sNamedEvaluable) {
    var location = location(line);
    var referenceS = new SReference(sNamedEvaluable.schema(), sNamedEvaluable.name(), location);
    return instantiateS(typeArgs, referenceS, location);
  }

  public static SInstantiate instantiateS(SPolymorphic sPolymorphic) {
    return instantiateS(sPolymorphic, sPolymorphic.location());
  }

  public static SInstantiate instantiateS(int line, SPolymorphic sPolymorphic) {
    return instantiateS(sPolymorphic, location(line));
  }

  public static SInstantiate instantiateS(SPolymorphic sPolymorphic, Location location) {
    return instantiateS(list(), sPolymorphic, location);
  }

  public static SInstantiate instantiateS(List<SType> typeArgs, SPolymorphic sPolymorphic) {
    return instantiateS(1, typeArgs, sPolymorphic);
  }

  public static SInstantiate instantiateS(
      int line, List<SType> typeArgs, SPolymorphic sPolymorphic) {
    return instantiateS(typeArgs, sPolymorphic, location(line));
  }

  public static SInstantiate instantiateS(
      List<SType> typeArgs, SPolymorphic sPolymorphic, Location location) {
    return new SInstantiate(typeArgs, sPolymorphic, location);
  }

  public static SOrder orderS(int line, SExpr headElem, SExpr... tailElems) {
    return new SOrder(
        arrayTS(headElem.evaluationType()), list(headElem).append(tailElems), location(line));
  }

  public static SOrder orderS(SType elemT, SExpr... exprs) {
    return orderS(1, elemT, exprs);
  }

  public static SOrder orderS(int line, SType elemT, SExpr... exprs) {
    return new SOrder(arrayTS(elemT), list(exprs), location(line));
  }

  public static SInstantiate paramRefS(SType type, String name) {
    return paramRefS(1, type, name);
  }

  public static SInstantiate paramRefS(int line, SType type, String name) {
    return instantiateS(line, referenceS(line, new SchemaS(varSetS(), type), name));
  }

  public static SReference referenceS(int line, SNamedEvaluable sNamedEvaluable) {
    return referenceS(line, sNamedEvaluable.schema(), sNamedEvaluable.name());
  }

  public static SReference referenceS(int line, SchemaS schema, String name) {
    return referenceS(schema, name, location(line));
  }

  public static SReference referenceS(SchemaS schema, String name, Location location) {
    return new SReference(schema, name, location);
  }

  public static SelectS selectS(SExpr selectable, String field) {
    return selectS(1, selectable, field);
  }

  public static SelectS selectS(int line, SExpr selectable, String field) {
    return new SelectS(selectable, field, location(line));
  }

  public static SString stringS() {
    return stringS("abc");
  }

  public static SString stringS(String string) {
    return stringS(1, string);
  }

  public static SString stringS(int line, String data) {
    return new SString(stringTS(), data, location(line));
  }

  // other smooth language thingies

  private static SAnnotation bytecodeS(String path) {
    return bytecodeS(1, path);
  }

  public static SAnnotation bytecodeS(int line, String path) {
    return bytecodeS(line, stringS(line, path));
  }

  public static SAnnotation bytecodeS(int line, SString path) {
    return bytecodeS(path, location(line));
  }

  public static SAnnotation bytecodeS(String path, Location location) {
    return bytecodeS(stringS(path), location);
  }

  public static SAnnotation bytecodeS(SString path, Location location) {
    return new SAnnotation(BYTECODE, path, location);
  }

  public static SAnnotation nativeAnnotationS() {
    return nativeAnnotationS(1, stringS("impl"));
  }

  public static SAnnotation nativeAnnotationS(int line, SString classBinaryName) {
    return nativeAnnotationS(line, classBinaryName, true);
  }

  public static SAnnotation nativeAnnotationS(int line, SString classBinaryName, boolean pure) {
    return nativeAnnotationS(location(line), classBinaryName, pure);
  }

  public static SAnnotation nativeAnnotationS(Location location, SString classBinaryName) {
    return nativeAnnotationS(location, classBinaryName, true);
  }

  public static SAnnotation nativeAnnotationS(
      Location location, SString classBinaryName, boolean pure) {
    var name = pure ? NATIVE_PURE : NATIVE_IMPURE;
    return new SAnnotation(name, classBinaryName, location);
  }

  public static SItem itemS(SType type) {
    return itemS(1, type, "paramName");
  }

  public static SItem itemS(SType type, String name) {
    return itemS(1, type, name);
  }

  public static SItem itemS(int line, SType type, String name) {
    return itemS(line, type, name, none());
  }

  public static SItem itemS(int line, SType type, String name, SExpr body) {
    return itemS(line, type, name, some(body));
  }

  public static SItem itemS(String name, SExpr body) {
    return itemS(body.evaluationType(), name, some(body));
  }

  public static SItem itemS(SType type, String name, Maybe<SExpr> body) {
    return itemS(1, type, name, body);
  }

  public static SItem itemS(int line, SType type, String name, Maybe<SExpr> body) {
    return itemSPoly(line, type, name, body.map(b -> valueS(line, name, b)));
  }

  public static SItem itemS(int line, SType type, String name, SNamedValue body) {
    return itemSPoly(line, type, name, some(body));
  }

  public static SItem itemSPoly(int line, SType type, String name, Maybe<SNamedValue> body) {
    return new SItem(type, name, body, location(line));
  }

  public static SAnnotatedValue bytecodeValueS(int line, SType type, String name) {
    return annotatedValueS(line, bytecodeS(line - 1, "impl"), type, name);
  }

  public static SAnnotatedValue annotatedValueS(
      int line, SAnnotation annotation, SType type, String name) {
    return annotatedValueS(annotation, type, name, location(line));
  }

  public static SAnnotatedValue annotatedValueS(
      SAnnotation annotation, SType type, String name, Location location) {
    return new SAnnotatedValue(annotation, schemaS(type), name, location);
  }

  public static SNamedExprValue valueS(String name, SExpr body) {
    return valueS(1, name, body);
  }

  public static SNamedExprValue valueS(int line, String name, SExpr body) {
    return valueS(line, body.evaluationType(), name, body);
  }

  public static SNamedExprValue valueS(int line, SType type, String name, SExpr body) {
    return valueS(line, schemaS(type), name, body);
  }

  public static SNamedExprValue valueS(SchemaS schema, String name, SExpr body) {
    return valueS(1, schema, name, body);
  }

  public static SNamedExprValue valueS(int line, SchemaS schema, String name, SExpr body) {
    return new SNamedExprValue(schema, name, body, location(line));
  }

  public static SNamedValue emptyArrayValueS() {
    return emptyArrayValueS(varA());
  }

  public static SNamedValue emptyArrayValueS(SVar elemT) {
    return valueS("emptyArray", orderS(elemT));
  }

  public static SConstructor constructorS(SStructType structT) {
    return constructorS(1, structT, UPPER_CAMEL.to(LOWER_CAMEL, structT.name()));
  }

  public static SConstructor constructorS(int line, SStructType structT) {
    return constructorS(line, structT, structT.name());
  }

  public static SConstructor constructorS(int line, SStructType structT, String name) {
    var fields = structT.fields();
    var params = fields.map(f -> new SItem(f.type(), f.name(), none(), location(2)));
    return new SConstructor(funcSchemaS(params, structT), name, params, location(line));
  }

  public static SAnnotatedFunc bytecodeFuncS(
      String path, SType resultT, String name, NList<SItem> params) {
    return bytecodeFuncS(1, path, resultT, name, params);
  }

  public static SAnnotatedFunc bytecodeFuncS(
      int line, SType resultT, String name, NList<SItem> params) {
    return annotatedFuncS(line, bytecodeS(line - 1, "impl"), resultT, name, params);
  }

  public static SAnnotatedFunc bytecodeFuncS(
      int line, String path, SType resultT, String name, NList<SItem> params) {
    return annotatedFuncS(line, bytecodeS(path), resultT, name, params);
  }

  public static SAnnotatedFunc nativeFuncS(SType resultT, String name, NList<SItem> params) {
    return annotatedFuncS(nativeAnnotationS(), resultT, name, params);
  }

  public static SAnnotatedFunc annotatedFuncS(
      SAnnotation ann, SType resultT, String name, NList<SItem> params) {
    return annotatedFuncS(1, ann, resultT, name, params);
  }

  public static SAnnotatedFunc annotatedFuncS(
      int line, SAnnotation ann, SType resultT, String name, NList<SItem> params) {
    return annotatedFuncS(ann, resultT, name, params, location(line));
  }

  public static SAnnotatedFunc annotatedFuncS(
      SAnnotation ann, SType resultT, String name, NList<SItem> params, Location location) {
    return new SAnnotatedFunc(ann, funcSchemaS(params, resultT), name, params, location);
  }

  public static SNamedExprFunc funcS(int line, String name, NList<SItem> params, SExpr body) {
    return funcS(line, body.evaluationType(), name, params, body);
  }

  public static SNamedExprFunc funcS(String name, NList<SItem> params, SExpr body) {
    return funcS(body.evaluationType(), name, params, body);
  }

  public static SNamedExprFunc funcS(SType resultT, String name, NList<SItem> params, SExpr body) {
    return funcS(1, resultT, name, params, body);
  }

  public static SNamedExprFunc funcS(
      int line, SType resultT, String name, NList<SItem> params, SExpr body) {
    var schema = funcSchemaS(params, resultT);
    return new SNamedExprFunc(schema, name, params, body, location(line));
  }

  public static SLambda lambdaS(SVarSet quantifiedVars, SExpr body) {
    return lambdaS(quantifiedVars, nlist(), body);
  }

  public static SLambda lambdaS(SVarSet quantifiedVars, NList<SItem> params, SExpr body) {
    return lambdaS(1, quantifiedVars, params, body);
  }

  public static SLambda lambdaS(int line, SVarSet quantifiedVars, NList<SItem> params, SExpr body) {
    var funcTS = funcTS(toTypes(params.list()), body.evaluationType());
    var funcSchemaS = funcSchemaS(quantifiedVars, funcTS);
    return new SLambda(funcSchemaS, params, body, location(line));
  }

  public static SLambda lambdaS(SExpr body) {
    return lambdaS(1, nlist(), body);
  }

  public static SLambda lambdaS(NList<SItem> params, SExpr body) {
    return lambdaS(1, params, body);
  }

  public static SLambda lambdaS(int line, NList<SItem> params, SExpr body) {
    var funcSchemaS = funcSchemaS(toTypes(params.list()), body.evaluationType());
    return new SLambda(funcSchemaS, params, body, location(line));
  }

  public static SNamedExprFunc idFuncS() {
    var a = varA();
    return funcS(a, "myId", nlist(itemS(a, "a")), paramRefS(a, "a"));
  }

  public static SNamedExprFunc intIdFuncS() {
    return funcS(intTS(), "myIntId", nlist(itemS(intTS(), "i")), paramRefS(intTS(), "i"));
  }

  public static SNamedExprFunc returnIntFuncS() {
    return funcS(intTS(), "myReturnInt", nlist(), intS(1, 3));
  }

  public static SItemSig sigS(SType type, String name) {
    return new SItemSig(type, name);
  }

  public static STrace traceS() {
    return new STrace();
  }

  public static STrace traceS(String name2, int line2, String name1, int line1) {
    return traceS(name2, location(line2), name1, location(line1));
  }

  public static STrace traceS(String name2, Location location2, String name1, Location location1) {
    var element1 = new Element(name1, location1, null);
    var element2 = new Element(name2, location2, element1);
    return new STrace(element2);
  }

  public static STrace traceS(String name, int line) {
    return traceS(name, location(line));
  }

  public static STrace traceS(String name, Location location) {
    return new STrace(new STrace.Element(name, location, null));
  }

  // P - parsed objects

  public static ModuleP moduleP(List<StructP> structs, List<NamedEvaluableP> evaluables) {
    return new ModuleP("", structs, evaluables);
  }

  public static InstantiateP lambdaP(NList<ItemP> params, ExprP body) {
    return instantiateP(new LambdaP("^1", params, body, location()));
  }

  public static CallP callP(ExprP callee) {
    return callP(callee, location());
  }

  public static CallP callP(ExprP callee, Location location) {
    return new CallP(callee, list(), location);
  }

  public static InstantiateP instantiateP(PolymorphicP polymorphicP) {
    return new InstantiateP(polymorphicP, polymorphicP.location());
  }

  public static NamedFuncP namedFuncP() {
    return namedFuncP(nlist(itemP()));
  }

  public static NamedFuncP namedFuncP(String name) {
    return namedFuncP(name, nlist(itemP()));
  }

  public static NamedFuncP namedFuncP(String name, int line) {
    return namedFuncP(name, nlist(itemP()), none(), location(line));
  }

  public static NamedFuncP namedFuncP(NList<ItemP> params) {
    return namedFuncP("myFunc", params);
  }

  public static NamedFuncP namedFuncP(String name, ExprP body) {
    return namedFuncP(name, nlist(), some(body));
  }

  public static NamedFuncP namedFuncP(String name, NList<ItemP> params) {
    return namedFuncP(name, params, none());
  }

  public static NamedFuncP namedFuncP(String name, NList<ItemP> params, Maybe<ExprP> body) {
    return namedFuncP(name, params, body, location());
  }

  public static NamedFuncP namedFuncP(
      String name, NList<ItemP> params, Maybe<ExprP> body, Location location) {
    var resultT = new ImplicitTP(location);
    return new NamedFuncP(resultT, name, shortName(name), params, body, none(), location);
  }

  public static NamedValueP namedValueP() {
    return namedValueP(intP());
  }

  public static NamedValueP namedValueP(ExprP body) {
    return namedValueP("myValue", body);
  }

  public static NamedValueP namedValueP(String name) {
    return namedValueP(name, intP());
  }

  public static NamedValueP namedValueP(String name, ExprP body) {
    var location = location();
    var type = new ImplicitTP(location);
    return new NamedValueP(type, name, shortName(name), some(body), none(), location);
  }

  public static ItemP itemP() {
    return itemP(some(namedValueP()));
  }

  public static ItemP itemP(Maybe<NamedValueP> defaultValue) {
    return itemP("param1", defaultValue);
  }

  public static ItemP itemP(String name) {
    return itemP(name, none());
  }

  public static ItemP itemP(String name, ExprP defaultValue) {
    return itemP(name, namedValueP(defaultValue));
  }

  public static ItemP itemP(String name, NamedValueP defaultValue) {
    return itemP(name, some(defaultValue));
  }

  public static ItemP itemP(String name, Maybe<NamedValueP> defaultValue) {
    return new ItemP(new ExplicitTP("Int", location()), name, defaultValue, location());
  }

  public static IntP intP() {
    return new IntP("7", location());
  }

  public static InstantiateP referenceP(String name) {
    return referenceP(name, location(7));
  }

  public static InstantiateP referenceP(String name, Location location) {
    return instantiateP(new ReferenceP(name, location));
  }

  // location

  public static Location location() {
    return location(11);
  }

  public static Location location(int line) {
    return location(userModuleFullPath(), line);
  }

  public static Location location(BucketId bucketId) {
    return location(fullPath(bucketId, path("path")), 17);
  }

  public static Location location(FullPath fullPath, int line) {
    return fileLocation(fullPath, line);
  }

  public static FullPath userModuleFullPath() {
    return projectPath(BUILD_FILE_PATH);
  }

  public static FullPath nativeFileFullPath() {
    return userModuleFullPath().withExtension("jar");
  }

  public static FullPath importedBuildFullPath() {
    return new FullPath(STANDARD_LIBRARY_BUCKET_ID, path(IMPORTED_FILE_PATH));
  }

  public static FullPath projectPath(String path) {
    return new FullPath(PROJECT_BUCKET_ID, path(path));
  }

  public static SynchronizedBucket synchronizedMemoryBucket() {
    return new SynchronizedBucket(new MemoryBucket());
  }

  private static String shortName(String fullName) {
    return fullName.substring(Math.max(0, fullName.lastIndexOf(':')));
  }

  @SafeVarargs
  public static <T extends Named> ImmutableBindings<T> bindings(T... nameds) {
    return immutableBindings(list(nameds).toMap(Named::name, v -> v));
  }
}
