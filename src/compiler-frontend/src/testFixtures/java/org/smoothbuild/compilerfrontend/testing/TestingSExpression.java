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
import org.smoothbuild.compilerfrontend.compile.ast.define.PCall;
import org.smoothbuild.compilerfrontend.compile.ast.define.PExplicitType;
import org.smoothbuild.compilerfrontend.compile.ast.define.PExpr;
import org.smoothbuild.compilerfrontend.compile.ast.define.PImplicitType;
import org.smoothbuild.compilerfrontend.compile.ast.define.PInstantiate;
import org.smoothbuild.compilerfrontend.compile.ast.define.PInt;
import org.smoothbuild.compilerfrontend.compile.ast.define.PItem;
import org.smoothbuild.compilerfrontend.compile.ast.define.PLambda;
import org.smoothbuild.compilerfrontend.compile.ast.define.PModule;
import org.smoothbuild.compilerfrontend.compile.ast.define.PNamedEvaluable;
import org.smoothbuild.compilerfrontend.compile.ast.define.PNamedFunc;
import org.smoothbuild.compilerfrontend.compile.ast.define.PNamedValue;
import org.smoothbuild.compilerfrontend.compile.ast.define.PPolymorphic;
import org.smoothbuild.compilerfrontend.compile.ast.define.PReference;
import org.smoothbuild.compilerfrontend.compile.ast.define.PStruct;
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
        TestingSExpression::sArrayType,
        TestingSExpression::sFuncType,
        t -> sFuncType(t, sIntType()),
        TestingSExpression::sTupleType,
        TestingSExpression::sStructType,
        TestingSExpression::sInterfaceType);
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

  public static SArrayType sArrayType(SType elemT) {
    return new SArrayType(elemT);
  }

  public static SBlobType sBlobType() {
    return STypes.BLOB;
  }

  public static SBoolType sBoolType() {
    return STypes.BOOL;
  }

  public static SFuncType sFuncType(SType resultType) {
    return sFuncType(list(), resultType);
  }

  public static SFuncType sFuncType(SType param1, SType resultType) {
    return sFuncType(list(param1), resultType);
  }

  public static SFuncType sFuncType(SType param1, SType param2, SType resultType) {
    return sFuncType(list(param1, param2), resultType);
  }

  public static SFuncType sFuncType(List<SType> paramTs, SType resultType) {
    return new SFuncType(sTupleType(paramTs), resultType);
  }

  public static STupleType sTupleType(SType... itemTypes) {
    return sTupleType(list(itemTypes));
  }

  public static STupleType sTupleType(List<SType> paramTypes) {
    return new STupleType(paramTypes);
  }

  public static SIntType sIntType() {
    return STypes.INT;
  }

  public static SFuncSchema sFuncSchema(NList<SItem> params, SType resultType) {
    return sFuncSchema(toTypes(params.list()), resultType);
  }

  public static SFuncSchema sFuncSchema(SType resultType) {
    return sFuncSchema(sFuncType(list(), resultType));
  }

  public static SFuncSchema sFuncSchema(SType paramType, SType resultType) {
    return sFuncSchema(sFuncType(list(paramType), resultType));
  }

  public static SFuncSchema sFuncSchema(List<SType> paramTypes, SType resultType) {
    return sFuncSchema(sFuncType(paramTypes, resultType));
  }

  private static SFuncSchema sFuncSchema(SFuncType funcType) {
    return sFuncSchema(funcType.vars(), funcType);
  }

  private static SFuncSchema sFuncSchema(SVarSet quantifiedVars, SFuncType funcType) {
    return new SFuncSchema(quantifiedVars, funcType);
  }

  public static SInterfaceType sInterfaceType() {
    return sInterfaceType(map());
  }

  public static SInterfaceType sInterfaceType(SType... fieldTypes) {
    return sInterfaceType(typesToItemSigsMap(fieldTypes));
  }

  public static SInterfaceType sInterfaceType(SItemSig... fieldTypes) {
    return sInterfaceType(itemSigsToMap(fieldTypes));
  }

  public static SInterfaceType sInterfaceType(Map<String, SItemSig> fieldSignatures) {
    return new SInterfaceType(fieldSignatures);
  }

  public static SchemaS sSchema(SType type) {
    return new SchemaS(type.vars(), type);
  }

  public static SStructType sPersonType() {
    return sStructType(
        "Person", nlist(sSig(sStringType(), "firstName"), sSig(sStringType(), "lastName")));
  }

  public static SStructType sAnimalType() {
    return sStructType("Animal", nlist(sSig(sStringType(), "name"), sSig(sIntType(), "size")));
  }

  public static SStringType sStringType() {
    return STypes.STRING;
  }

  public static SStructType sStructType(SType... fieldTypes) {
    return sStructType("MyStruct", fieldTypes);
  }

  public static SStructType sStructType(String name) {
    return sStructType(name, nlist());
  }

  public static SStructType sStructType(String name, SType... fieldTypes) {
    return sStructType(name, nlist(typesToItemSigs(fieldTypes)));
  }

  public static SStructType sStructType(String name, SItemSig... fieldSigs) {
    return sStructType(name, nlist(fieldSigs));
  }

  private static List<SItemSig> typesToItemSigs(SType... fieldTypes) {
    var builder = new ArrayList<SItemSig>();
    for (int i = 0; i < fieldTypes.length; i++) {
      builder.add(sSig(fieldTypes[i], "param" + i));
    }
    return listOfAll(builder);
  }

  public static Map<String, SItemSig> typesToItemSigsMap(SType... types) {
    return itemSigsToMap(sTypesToSSigs(types));
  }

  public static Map<String, SItemSig> itemSigsToMap(SItemSig... itemSigs) {
    return itemSigsToMap(list(itemSigs));
  }

  public static Map<String, SItemSig> itemSigsToMap(List<SItemSig> sigs) {
    return sigs.toMap(SItemSig::name, f -> f);
  }

  private static List<SItemSig> sTypesToSSigs(SType... types) {
    var builder = new ArrayList<SItemSig>();
    for (int i = 0; i < types.length; i++) {
      builder.add(sSig(types[i], "param" + i));
    }
    return listOfAll(builder);
  }

  public static SStructType sStructType(String name, NList<SItemSig> fields) {
    return new SStructType(name, fields);
  }

  public static SVar sTempVarA() {
    return sTempVar("1");
  }

  public static STempVar sTempVar(String name) {
    return new STempVar(name);
  }

  public static SVar varA() {
    return sVar("A");
  }

  public static SVar varB() {
    return sVar("B");
  }

  public static SVar varC() {
    return sVar("C");
  }

  public static SVar varX() {
    return sVar("X");
  }

  public static SVar sVar(String name) {
    return new SVar(name);
  }

  // ExprS-s

  public static SBlob sBlob(int data) {
    return sBlob(1, data);
  }

  public static SBlob sBlob(int line, int data) {
    return new SBlob(sBlobType(), intToByteString(data), location(line));
  }

  public static SCall sCall(SExpr callable, SExpr... args) {
    return sCall(1, callable, args);
  }

  public static SCall sCall(int line, SExpr callable, SExpr... args) {
    return new SCall(callable, sCombine(line, args), location(line));
  }

  public static SCombine sCombine(SExpr... args) {
    return sCombine(13, args);
  }

  private static SCombine sCombine(int line, SExpr... args) {
    var argsList = list(args);
    var evaluationType = new STupleType(argsList.map(SExpr::evaluationType));
    return new SCombine(evaluationType, argsList, location(line));
  }

  public static SInt sInt(int value) {
    return sInt(1, value);
  }

  public static SInt sInt(int line, int value) {
    return new SInt(sIntType(), BigInteger.valueOf(value), location(line));
  }

  public static Map<SVar, SType> varMap() {
    return map();
  }

  public static Map<SVar, SType> varMap(SVar var, SType type) {
    return map(var, type);
  }

  public static SInstantiate sInstantiate(SNamedEvaluable namedEvaluable) {
    return sInstantiate(17, namedEvaluable);
  }

  public static SInstantiate sInstantiate(int line, SNamedEvaluable namedEvaluable) {
    return sInstantiate(line, sReference(line, namedEvaluable));
  }

  public static SInstantiate sInstantiate(List<SType> typeArgs, SNamedEvaluable namedEvaluable) {
    return sInstantiate(1, typeArgs, namedEvaluable);
  }

  public static SInstantiate sInstantiate(
      int line, List<SType> typeArgs, SNamedEvaluable namedEvaluable) {
    var location = location(line);
    var referenceS = new SReference(namedEvaluable.schema(), namedEvaluable.name(), location);
    return sInstantiate(typeArgs, referenceS, location);
  }

  public static SInstantiate sInstantiate(SPolymorphic polymorphic) {
    return sInstantiate(polymorphic, polymorphic.location());
  }

  public static SInstantiate sInstantiate(int line, SPolymorphic polymorphic) {
    return sInstantiate(polymorphic, location(line));
  }

  public static SInstantiate sInstantiate(SPolymorphic polymorphic, Location location) {
    return sInstantiate(list(), polymorphic, location);
  }

  public static SInstantiate sInstantiate(List<SType> typeArgs, SPolymorphic polymorphic) {
    return sInstantiate(1, typeArgs, polymorphic);
  }

  public static SInstantiate sInstantiate(
      int line, List<SType> typeArgs, SPolymorphic polymorphic) {
    return sInstantiate(typeArgs, polymorphic, location(line));
  }

  public static SInstantiate sInstantiate(
      List<SType> typeArgs, SPolymorphic polymorphic, Location location) {
    return new SInstantiate(typeArgs, polymorphic, location);
  }

  public static SOrder sOrder(int line, SExpr headElement, SExpr... tailElements) {
    return new SOrder(
        sArrayType(headElement.evaluationType()),
        list(headElement).append(tailElements),
        location(line));
  }

  public static SOrder sOrder(SType elementType, SExpr... exprs) {
    return sOrder(1, elementType, exprs);
  }

  public static SOrder sOrder(int line, SType elementType, SExpr... exprs) {
    return new SOrder(sArrayType(elementType), list(exprs), location(line));
  }

  public static SInstantiate sParamRef(SType type, String name) {
    return sParamRef(1, type, name);
  }

  public static SInstantiate sParamRef(int line, SType type, String name) {
    return sInstantiate(line, sReference(line, new SchemaS(varSetS(), type), name));
  }

  public static SReference sReference(int line, SNamedEvaluable namedEvaluable) {
    return sReference(line, namedEvaluable.schema(), namedEvaluable.name());
  }

  public static SReference sReference(int line, SchemaS schema, String name) {
    return sReference(schema, name, location(line));
  }

  public static SReference sReference(SchemaS schema, String name, Location location) {
    return new SReference(schema, name, location);
  }

  public static SelectS sSelect(SExpr selectable, String field) {
    return sSelect(1, selectable, field);
  }

  public static SelectS sSelect(int line, SExpr selectable, String field) {
    return new SelectS(selectable, field, location(line));
  }

  public static SString sString() {
    return sString("abc");
  }

  public static SString sString(String string) {
    return sString(1, string);
  }

  public static SString sString(int line, String data) {
    return new SString(sStringType(), data, location(line));
  }

  // other smooth language thingies

  private static SAnnotation sBytecode(String path) {
    return sBytecode(1, path);
  }

  public static SAnnotation sBytecode(int line, String path) {
    return sBytecode(line, sString(line, path));
  }

  public static SAnnotation sBytecode(int line, SString path) {
    return sBytecode(path, location(line));
  }

  public static SAnnotation sBytecode(String path, Location location) {
    return sBytecode(sString(path), location);
  }

  public static SAnnotation sBytecode(SString path, Location location) {
    return new SAnnotation(BYTECODE, path, location);
  }

  public static SAnnotation sNativeAnnotation() {
    return sNativeAnnotation(1, sString("impl"));
  }

  public static SAnnotation sNativeAnnotation(int line, SString classBinaryName) {
    return sNativeAnnotation(line, classBinaryName, true);
  }

  public static SAnnotation sNativeAnnotation(int line, SString classBinaryName, boolean pure) {
    return sNativeAnnotation(location(line), classBinaryName, pure);
  }

  public static SAnnotation sNativeAnnotation(Location location, SString classBinaryName) {
    return sNativeAnnotation(location, classBinaryName, true);
  }

  public static SAnnotation sNativeAnnotation(
      Location location, SString classBinaryName, boolean pure) {
    var name = pure ? NATIVE_PURE : NATIVE_IMPURE;
    return new SAnnotation(name, classBinaryName, location);
  }

  public static SItem sItem(SType type) {
    return sItem(1, type, "paramName");
  }

  public static SItem sItem(SType type, String name) {
    return sItem(1, type, name);
  }

  public static SItem sItem(int line, SType type, String name) {
    return sItem(line, type, name, none());
  }

  public static SItem sItem(int line, SType type, String name, SExpr body) {
    return sItem(line, type, name, some(body));
  }

  public static SItem sItem(String name, SExpr body) {
    return sItem(body.evaluationType(), name, some(body));
  }

  public static SItem sItem(SType type, String name, Maybe<SExpr> body) {
    return sItem(1, type, name, body);
  }

  public static SItem sItem(int line, SType type, String name, Maybe<SExpr> body) {
    return sItemPoly(line, type, name, body.map(b -> sValue(line, name, b)));
  }

  public static SItem sItem(int line, SType type, String name, SNamedValue body) {
    return sItemPoly(line, type, name, some(body));
  }

  public static SItem sItemPoly(int line, SType type, String name, Maybe<SNamedValue> body) {
    return new SItem(type, name, body, location(line));
  }

  public static SAnnotatedValue sBytecodeValue(int line, SType type, String name) {
    return sAnnotatedValue(line, sBytecode(line - 1, "impl"), type, name);
  }

  public static SAnnotatedValue sAnnotatedValue(
      int line, SAnnotation annotation, SType type, String name) {
    return sAnnotatedValue(annotation, type, name, location(line));
  }

  public static SAnnotatedValue sAnnotatedValue(
      SAnnotation annotation, SType type, String name, Location location) {
    return new SAnnotatedValue(annotation, sSchema(type), name, location);
  }

  public static SNamedExprValue sValue(String name, SExpr body) {
    return sValue(1, name, body);
  }

  public static SNamedExprValue sValue(int line, String name, SExpr body) {
    return sValue(line, body.evaluationType(), name, body);
  }

  public static SNamedExprValue sValue(int line, SType type, String name, SExpr body) {
    return sValue(line, sSchema(type), name, body);
  }

  public static SNamedExprValue sValue(SchemaS schema, String name, SExpr body) {
    return sValue(1, schema, name, body);
  }

  public static SNamedExprValue sValue(int line, SchemaS schema, String name, SExpr body) {
    return new SNamedExprValue(schema, name, body, location(line));
  }

  public static SNamedValue emptySArrayValue() {
    return emptySArrayValue(varA());
  }

  public static SNamedValue emptySArrayValue(SVar elementType) {
    return sValue("emptyArray", sOrder(elementType));
  }

  public static SConstructor sConstructor(SStructType structType) {
    return sConstructor(1, structType, UPPER_CAMEL.to(LOWER_CAMEL, structType.name()));
  }

  public static SConstructor sConstructor(int line, SStructType structType) {
    return sConstructor(line, structType, structType.name());
  }

  public static SConstructor sConstructor(int line, SStructType structType, String name) {
    var fields = structType.fields();
    var params = fields.map(f -> new SItem(f.type(), f.name(), none(), location(2)));
    return new SConstructor(sFuncSchema(params, structType), name, params, location(line));
  }

  public static SAnnotatedFunc sBytecodeFunc(
      String path, SType resultType, String name, NList<SItem> params) {
    return sBytecodeFunc(1, path, resultType, name, params);
  }

  public static SAnnotatedFunc sBytecodeFunc(
      int line, SType resultType, String name, NList<SItem> params) {
    return sAnnotatedFunc(line, sBytecode(line - 1, "impl"), resultType, name, params);
  }

  public static SAnnotatedFunc sBytecodeFunc(
      int line, String path, SType resultType, String name, NList<SItem> params) {
    return sAnnotatedFunc(line, sBytecode(path), resultType, name, params);
  }

  public static SAnnotatedFunc sNativeFunc(SType resultType, String name, NList<SItem> params) {
    return sAnnotatedFunc(sNativeAnnotation(), resultType, name, params);
  }

  public static SAnnotatedFunc sAnnotatedFunc(
      SAnnotation ann, SType resultType, String name, NList<SItem> params) {
    return sAnnotatedFunc(1, ann, resultType, name, params);
  }

  public static SAnnotatedFunc sAnnotatedFunc(
      int line, SAnnotation ann, SType resultType, String name, NList<SItem> params) {
    return sAnnotatedFunc(ann, resultType, name, params, location(line));
  }

  public static SAnnotatedFunc sAnnotatedFunc(
      SAnnotation ann, SType resultType, String name, NList<SItem> params, Location location) {
    return new SAnnotatedFunc(ann, sFuncSchema(params, resultType), name, params, location);
  }

  public static SNamedExprFunc sFunc(int line, String name, NList<SItem> params, SExpr body) {
    return sFunc(line, body.evaluationType(), name, params, body);
  }

  public static SNamedExprFunc sFunc(String name, NList<SItem> params, SExpr body) {
    return sFunc(body.evaluationType(), name, params, body);
  }

  public static SNamedExprFunc sFunc(
      SType resultType, String name, NList<SItem> params, SExpr body) {
    return sFunc(1, resultType, name, params, body);
  }

  public static SNamedExprFunc sFunc(
      int line, SType resultType, String name, NList<SItem> params, SExpr body) {
    var schema = sFuncSchema(params, resultType);
    return new SNamedExprFunc(schema, name, params, body, location(line));
  }

  public static SLambda sLambda(SVarSet quantifiedVars, SExpr body) {
    return sLambda(quantifiedVars, nlist(), body);
  }

  public static SLambda sLambda(SVarSet quantifiedVars, NList<SItem> params, SExpr body) {
    return sLambda(1, quantifiedVars, params, body);
  }

  public static SLambda sLambda(int line, SVarSet quantifiedVars, NList<SItem> params, SExpr body) {
    var funcTS = sFuncType(toTypes(params.list()), body.evaluationType());
    var funcSchemaS = sFuncSchema(quantifiedVars, funcTS);
    return new SLambda(funcSchemaS, params, body, location(line));
  }

  public static SLambda sLambda(SExpr body) {
    return sLambda(1, nlist(), body);
  }

  public static SLambda sLambda(NList<SItem> params, SExpr body) {
    return sLambda(1, params, body);
  }

  public static SLambda sLambda(int line, NList<SItem> params, SExpr body) {
    var funcSchemaS = sFuncSchema(toTypes(params.list()), body.evaluationType());
    return new SLambda(funcSchemaS, params, body, location(line));
  }

  public static SNamedExprFunc idSFunc() {
    var a = varA();
    return sFunc(a, "myId", nlist(sItem(a, "a")), sParamRef(a, "a"));
  }

  public static SNamedExprFunc intIdSFunc() {
    return sFunc(sIntType(), "myIntId", nlist(sItem(sIntType(), "i")), sParamRef(sIntType(), "i"));
  }

  public static SNamedExprFunc returnIntSFunc() {
    return sFunc(sIntType(), "myReturnInt", nlist(), sInt(1, 3));
  }

  public static SItemSig sSig(SType type, String name) {
    return new SItemSig(type, name);
  }

  public static STrace sTrace() {
    return new STrace();
  }

  public static STrace sTrace(String name2, int line2, String name1, int line1) {
    return sTrace(name2, location(line2), name1, location(line1));
  }

  public static STrace sTrace(String name2, Location location2, String name1, Location location1) {
    var element1 = new Element(name1, location1, null);
    var element2 = new Element(name2, location2, element1);
    return new STrace(element2);
  }

  public static STrace sTrace(String name, int line) {
    return sTrace(name, location(line));
  }

  public static STrace sTrace(String name, Location location) {
    return new STrace(new STrace.Element(name, location, null));
  }

  // P - parsed objects

  public static PModule pModule(List<PStruct> structs, List<PNamedEvaluable> evaluables) {
    return new PModule("", structs, evaluables);
  }

  public static PInstantiate pLambda(NList<PItem> params, PExpr body) {
    return pInstantiate(new PLambda("^1", params, body, location()));
  }

  public static PCall pCall(PExpr callee) {
    return pCall(callee, location());
  }

  public static PCall pCall(PExpr callee, Location location) {
    return new PCall(callee, list(), location);
  }

  public static PInstantiate pInstantiate(PPolymorphic polymorphic) {
    return new PInstantiate(polymorphic, polymorphic.location());
  }

  public static PNamedFunc pNamedFunc() {
    return pNamedFunc(nlist(pItem()));
  }

  public static PNamedFunc pNamedFunc(String name) {
    return pNamedFunc(name, nlist(pItem()));
  }

  public static PNamedFunc pNamedFunc(String name, int line) {
    return pNamedFunc(name, nlist(pItem()), none(), location(line));
  }

  public static PNamedFunc pNamedFunc(NList<PItem> params) {
    return pNamedFunc("myFunc", params);
  }

  public static PNamedFunc pNamedFunc(String name, PExpr body) {
    return pNamedFunc(name, nlist(), some(body));
  }

  public static PNamedFunc pNamedFunc(String name, NList<PItem> params) {
    return pNamedFunc(name, params, none());
  }

  public static PNamedFunc pNamedFunc(String name, NList<PItem> params, Maybe<PExpr> body) {
    return pNamedFunc(name, params, body, location());
  }

  public static PNamedFunc pNamedFunc(
      String name, NList<PItem> params, Maybe<PExpr> body, Location location) {
    var resultT = new PImplicitType(location);
    return new PNamedFunc(resultT, name, shortName(name), params, body, none(), location);
  }

  public static PNamedValue pNamedValue() {
    return pNamedValue(pInt());
  }

  public static PNamedValue pNamedValue(PExpr body) {
    return pNamedValue("myValue", body);
  }

  public static PNamedValue pNamedValue(String name) {
    return pNamedValue(name, pInt());
  }

  public static PNamedValue pNamedValue(String name, PExpr body) {
    var location = location();
    var type = new PImplicitType(location);
    return new PNamedValue(type, name, shortName(name), some(body), none(), location);
  }

  public static PItem pItem() {
    return pItem(some(pNamedValue()));
  }

  public static PItem pItem(Maybe<PNamedValue> defaultValue) {
    return pItem("param1", defaultValue);
  }

  public static PItem pItem(String name) {
    return pItem(name, none());
  }

  public static PItem pItem(String name, PExpr defaultValue) {
    return pItem(name, pNamedValue(defaultValue));
  }

  public static PItem pItem(String name, PNamedValue defaultValue) {
    return pItem(name, some(defaultValue));
  }

  public static PItem pItem(String name, Maybe<PNamedValue> defaultValue) {
    return new PItem(new PExplicitType("Int", location()), name, defaultValue, location());
  }

  public static PInt pInt() {
    return new PInt("7", location());
  }

  public static PInstantiate pReference(String name) {
    return pReference(name, location(7));
  }

  public static PInstantiate pReference(String name, Location location) {
    return pInstantiate(new PReference(name, location));
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
