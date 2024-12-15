package org.smoothbuild.compilerfrontend.testing;

import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_CAMEL;
import static org.smoothbuild.common.bindings.Bindings.immutableBindings;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.List.listOfAll;
import static org.smoothbuild.common.collect.Map.map;
import static org.smoothbuild.common.collect.Maybe.none;
import static org.smoothbuild.common.collect.Maybe.some;
import static org.smoothbuild.common.io.Okios.intToByteString;
import static org.smoothbuild.common.log.base.Log.error;
import static org.smoothbuild.compilerfrontend.lang.base.Fqn.fqn;
import static org.smoothbuild.compilerfrontend.lang.base.NList.nlist;
import static org.smoothbuild.compilerfrontend.lang.base.Name.referenceableName;
import static org.smoothbuild.compilerfrontend.lang.define.SItem.toTypes;
import static org.smoothbuild.compilerfrontend.lang.type.AnnotationNames.BYTECODE;
import static org.smoothbuild.compilerfrontend.lang.type.AnnotationNames.NATIVE_IMPURE;
import static org.smoothbuild.compilerfrontend.lang.type.AnnotationNames.NATIVE_PURE;
import static org.smoothbuild.compilerfrontend.lang.type.SVar.flexibleVar;
import static org.smoothbuild.compilerfrontend.lang.type.SVarSet.varSetS;

import java.math.BigInteger;
import java.util.ArrayList;
import org.smoothbuild.common.bindings.ImmutableBindings;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.Map;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.log.base.Log;
import org.smoothbuild.common.log.location.Location;
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
import org.smoothbuild.compilerfrontend.lang.base.Fqn;
import org.smoothbuild.compilerfrontend.lang.base.Id;
import org.smoothbuild.compilerfrontend.lang.base.Identifiable;
import org.smoothbuild.compilerfrontend.lang.base.NList;
import org.smoothbuild.compilerfrontend.lang.base.Name;
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
import org.smoothbuild.compilerfrontend.lang.define.SSelect;
import org.smoothbuild.compilerfrontend.lang.define.SString;
import org.smoothbuild.compilerfrontend.lang.type.SArrayType;
import org.smoothbuild.compilerfrontend.lang.type.SBlobType;
import org.smoothbuild.compilerfrontend.lang.type.SBoolType;
import org.smoothbuild.compilerfrontend.lang.type.SFuncSchema;
import org.smoothbuild.compilerfrontend.lang.type.SFuncType;
import org.smoothbuild.compilerfrontend.lang.type.SIntType;
import org.smoothbuild.compilerfrontend.lang.type.SInterfaceType;
import org.smoothbuild.compilerfrontend.lang.type.SSchema;
import org.smoothbuild.compilerfrontend.lang.type.SStringType;
import org.smoothbuild.compilerfrontend.lang.type.SStructType;
import org.smoothbuild.compilerfrontend.lang.type.STupleType;
import org.smoothbuild.compilerfrontend.lang.type.SType;
import org.smoothbuild.compilerfrontend.lang.type.STypes;
import org.smoothbuild.compilerfrontend.lang.type.SVar;
import org.smoothbuild.compilerfrontend.lang.type.SVarSet;
import org.smoothbuild.virtualmachine.testing.VmTestApi;

public interface FrontendCompilerTestApi extends VmTestApi {
  public default SArrayType sBlobArrayT() {
    return sArrayType(sBlobType());
  }

  public default SArrayType sBoolArrayT() {
    return sArrayType(sBoolType());
  }

  public default SArrayType sIntIntArrayT() {
    return sArrayType(sIntArrayT());
  }

  public default SArrayType sIntArrayT() {
    return sArrayType(sIntType());
  }

  public default SArrayType sStringStringArrayT() {
    return sArrayType(sStringArrayT());
  }

  public default SArrayType sStringArrayT() {
    return sArrayType(sStringType());
  }

  public default SArrayType sVarAArrayT() {
    return sArrayType(varA());
  }

  public default SArrayType sVarBArrayT() {
    return sArrayType(varB());
  }

  public default SArrayType sVar1ArrayT() {
    return sArrayType(var1());
  }

  public default SArrayType sVar2ArrayT() {
    return sArrayType(var2());
  }

  public default SArrayType sVar3ArrayT() {
    return sArrayType(var3());
  }

  public default SArrayType sArrayType(SType elemT) {
    return new SArrayType(elemT);
  }

  public default SBlobType sBlobType() {
    return STypes.BLOB;
  }

  public default SBoolType sBoolType() {
    return STypes.BOOL;
  }

  public default SFuncType sBlobFuncType() {
    return sFuncType(sBlobType());
  }

  public default SFuncType sIntFuncType() {
    return sFuncType(sIntType());
  }

  public default SFuncType sStringFuncType() {
    return sFuncType(sStringType());
  }

  public default SFuncType sVarAFuncType() {
    return sFuncType(varA());
  }

  public default SFuncType sFuncType(SType resultType) {
    return sFuncType(list(), resultType);
  }

  public default SFuncType sFuncType(SType param1, SType resultType) {
    return sFuncType(list(param1), resultType);
  }

  public default SFuncType sFuncType(SType param1, SType param2, SType resultType) {
    return sFuncType(list(param1, param2), resultType);
  }

  public default SFuncType sFuncType(List<SType> paramTs, SType resultType) {
    return new SFuncType(sTupleType(paramTs), resultType);
  }

  public default STupleType sTupleType(SType... itemTypes) {
    return sTupleType(list(itemTypes));
  }

  public default STupleType sTupleType(List<SType> paramTypes) {
    return new STupleType(paramTypes);
  }

  public default SIntType sIntType() {
    return STypes.INT;
  }

  public default SFuncSchema sFuncSchema(NList<SItem> params, SType resultType) {
    return sFuncSchema(toTypes(params.list()), resultType);
  }

  public default SFuncSchema sFuncSchema(SType resultType) {
    return sFuncSchema(sFuncType(list(), resultType));
  }

  public default SFuncSchema sFuncSchema(SType paramType, SType resultType) {
    return sFuncSchema(sFuncType(list(paramType), resultType));
  }

  public default SFuncSchema sFuncSchema(List<SType> paramTypes, SType resultType) {
    return sFuncSchema(sFuncType(paramTypes, resultType));
  }

  private SFuncSchema sFuncSchema(SFuncType funcType) {
    return sFuncSchema(funcType.vars(), funcType);
  }

  private SFuncSchema sFuncSchema(SVarSet quantifiedVars, SFuncType funcType) {
    return new SFuncSchema(quantifiedVars, funcType);
  }

  public default SInterfaceType sInterfaceType() {
    return sInterfaceType(map());
  }

  public default SInterfaceType sInterfaceType(SType... fieldTypes) {
    return sInterfaceType(typesToItemSigsMap(fieldTypes));
  }

  public default SInterfaceType sInterfaceType(SItemSig... fieldTypes) {
    return sInterfaceType(itemSigsToMap(fieldTypes));
  }

  public default SInterfaceType sInterfaceType(Map<Name, SItemSig> fieldSignatures) {
    return new SInterfaceType(fieldSignatures);
  }

  public default SSchema sSchema(SType type) {
    return new SSchema(type.vars(), type);
  }

  public default SStringType sStringType() {
    return STypes.STRING;
  }

  public default SStructType sStructType(SType... fieldTypes) {
    return sStructType("MyStruct", fieldTypes);
  }

  public default SStructType sStructType(String name) {
    return sStructType(name, nlist());
  }

  public default SStructType sStructType(String name, SType... fieldTypes) {
    return sStructType(name, nlist(typesToItemSigs(fieldTypes)));
  }

  public default SStructType sStructType(String name, SItemSig... fieldSigs) {
    return sStructType(name, nlist(fieldSigs));
  }

  public default List<SItemSig> typesToItemSigs(SType... fieldTypes) {
    var builder = new ArrayList<SItemSig>();
    for (int i = 0; i < fieldTypes.length; i++) {
      builder.add(sSig(fieldTypes[i], "param" + i));
    }
    return listOfAll(builder);
  }

  public default Map<Name, SItemSig> typesToItemSigsMap(SType... types) {
    return itemSigsToMap(sTypesToSSigs(types));
  }

  public default Map<Name, SItemSig> itemSigsToMap(SItemSig... itemSigs) {
    return itemSigsToMap(list(itemSigs));
  }

  public default Map<Name, SItemSig> itemSigsToMap(List<SItemSig> sigs) {
    return sigs.toMap(SItemSig::name, f -> f);
  }

  public default List<SItemSig> sTypesToSSigs(SType... types) {
    var builder = new ArrayList<SItemSig>();
    for (int i = 0; i < types.length; i++) {
      builder.add(sSig(types[i], "param" + i));
    }
    return listOfAll(builder);
  }

  public default SStructType sStructType(String name, NList<SItemSig> fields) {
    return new SStructType(fqn(name), fields);
  }

  public default SVar varA() {
    return sVar("A");
  }

  public default SVar varB() {
    return sVar("B");
  }

  public default SVar varC() {
    return sVar("C");
  }

  public default SVar varX() {
    return sVar("X");
  }

  public default SVar var1() {
    return flexibleVar(1);
  }

  public default SVar var2() {
    return flexibleVar(2);
  }

  public default SVar var3() {
    return flexibleVar(3);
  }

  public default SVar sVar(String name) {
    return new SVar(name);
  }

  public default SBlob sBlob(int data) {
    return sBlob(1, data);
  }

  public default SBlob sBlob(int line, int data) {
    return new SBlob(sBlobType(), intToByteString(data), location(line));
  }

  public default SCall sCall(SExpr callable, SExpr... args) {
    return sCall(1, callable, args);
  }

  public default SCall sCall(int line, SExpr callable, SExpr... args) {
    return new SCall(callable, sCombine(line, args), location(line));
  }

  public default SCombine sCombine(SExpr... args) {
    return sCombine(13, args);
  }

  public default SCombine sCombine(int line, SExpr... args) {
    var argsList = list(args);
    var evaluationType = new STupleType(argsList.map(SExpr::evaluationType));
    return new SCombine(evaluationType, argsList, location(line));
  }

  public default SInt sInt(int value) {
    return sInt(1, value);
  }

  public default SInt sInt(int line, int value) {
    return new SInt(sIntType(), BigInteger.valueOf(value), location(line));
  }

  public default Map<SVar, SType> varMap() {
    return map();
  }

  public default Map<SVar, SType> varMap(SVar var, SType type) {
    return map(var, type);
  }

  public default SInstantiate sInstantiate(SNamedEvaluable namedEvaluable) {
    return sInstantiate(17, namedEvaluable);
  }

  public default SInstantiate sInstantiate(int line, SNamedEvaluable namedEvaluable) {
    return sInstantiate(line, sReference(line, namedEvaluable));
  }

  public default SInstantiate sInstantiate(List<SType> typeArgs, SNamedEvaluable namedEvaluable) {
    return sInstantiate(1, typeArgs, namedEvaluable);
  }

  public default SInstantiate sInstantiate(
      int line, List<SType> typeArgs, SNamedEvaluable namedEvaluable) {
    var location = location(line);
    var referenceS = new SReference(namedEvaluable.schema(), namedEvaluable.id(), location);
    return sInstantiate(typeArgs, referenceS, location);
  }

  public default SInstantiate sInstantiate(SPolymorphic polymorphic) {
    return sInstantiate(polymorphic, polymorphic.location());
  }

  public default SInstantiate sInstantiate(int line, SPolymorphic polymorphic) {
    return sInstantiate(polymorphic, location(line));
  }

  public default SInstantiate sInstantiate(SPolymorphic polymorphic, Location location) {
    return sInstantiate(list(), polymorphic, location);
  }

  public default SInstantiate sInstantiate(List<SType> typeArgs, SPolymorphic polymorphic) {
    return sInstantiate(1, typeArgs, polymorphic);
  }

  public default SInstantiate sInstantiate(
      int line, List<SType> typeArgs, SPolymorphic polymorphic) {
    return sInstantiate(typeArgs, polymorphic, location(line));
  }

  public default SInstantiate sInstantiate(
      List<SType> typeArgs, SPolymorphic polymorphic, Location location) {
    return new SInstantiate(typeArgs, polymorphic, location);
  }

  public default SOrder sOrder(int line, SExpr headElement, SExpr... tailElements) {
    return new SOrder(
        sArrayType(headElement.evaluationType()),
        list(headElement).append(tailElements),
        location(line));
  }

  public default SOrder sOrder(SType elementType, SExpr... exprs) {
    return sOrder(1, elementType, exprs);
  }

  public default SOrder sOrder(int line, SType elementType, SExpr... exprs) {
    return new SOrder(sArrayType(elementType), list(exprs), location(line));
  }

  public default SInstantiate sParamRef(SType type, String name) {
    return sParamRef(1, type, name);
  }

  public default SInstantiate sParamRef(int line, SType type, String name) {
    return sInstantiate(line, sReference(line, new SSchema(varSetS(), type), fqn(name)));
  }

  public default SReference sReference(int line, SNamedEvaluable namedEvaluable) {
    return sReference(line, namedEvaluable.schema(), namedEvaluable.id());
  }

  public default SReference sReference(int line, SSchema schema, Id id) {
    return sReference(schema, id, location(line));
  }

  public default SReference sReference(SSchema schema, Id id, Location location) {
    return new SReference(schema, id, location);
  }

  public default SSelect sSelect(SExpr selectable, String field) {
    return sSelect(1, selectable, field);
  }

  public default SSelect sSelect(int line, SExpr selectable, String field) {
    return new SSelect(selectable, referenceableName(field), location(line));
  }

  public default SString sString() {
    return sString("abc");
  }

  public default SString sString(String string) {
    return sString(1, string);
  }

  public default SString sString(int line, String data) {
    return new SString(sStringType(), data, location(line));
  }

  public default SAnnotation sBytecode(String path) {
    return sBytecode(1, path);
  }

  public default SAnnotation sBytecode(int line, String path) {
    return sBytecode(line, sString(line, path));
  }

  public default SAnnotation sBytecode(int line, SString path) {
    return sBytecode(path, location(line));
  }

  public default SAnnotation sBytecode(String path, Location location) {
    return sBytecode(sString(path), location);
  }

  public default SAnnotation sBytecode(SString path, Location location) {
    return new SAnnotation(BYTECODE, path, location);
  }

  public default SAnnotation sNativeAnnotation() {
    return sNativeAnnotation(1, sString("impl"));
  }

  public default SAnnotation sNativeAnnotation(int line, SString classBinaryName) {
    return sNativeAnnotation(line, classBinaryName, true);
  }

  public default SAnnotation sNativeAnnotation(int line, SString classBinaryName, boolean pure) {
    return sNativeAnnotation(location(line), classBinaryName, pure);
  }

  public default SAnnotation sNativeAnnotation(Location location, SString classBinaryName) {
    return sNativeAnnotation(location, classBinaryName, true);
  }

  public default SAnnotation sNativeAnnotation(
      Location location, SString classBinaryName, boolean pure) {
    var name = pure ? NATIVE_PURE : NATIVE_IMPURE;
    return new SAnnotation(name, classBinaryName, location);
  }

  public default SItem sItem(SType type) {
    return sItem(1, type, "paramName");
  }

  public default SItem sItem(SType type, String name) {
    return sItem(1, type, name);
  }

  public default SItem sItem(int line, SType type, String name) {
    return new SItem(type, referenceableName(name), none(), location(line));
  }

  public default SItem sItem(SType type, String name, String defaultValueFullName) {
    return sItem(1, type, name, defaultValueFullName);
  }

  public default SItem sItem(int line, SType type, String name, String defaultValueFullName) {
    return sItem(line, type, name, some(defaultValueFullName));
  }

  public default SItem sItem(int line, SType type, String name, Maybe<String> defaultValueId) {
    return new SItem(type, referenceableName(name), defaultValueId.map(Fqn::fqn), location(line));
  }

  public default SAnnotatedValue sBytecodeValue(int line, SType type, String name) {
    return sAnnotatedValue(line, sBytecode(line - 1, "impl"), type, name);
  }

  public default SAnnotatedValue sAnnotatedValue(
      int line, SAnnotation annotation, SType type, String name) {
    return sAnnotatedValue(annotation, type, name, location(line));
  }

  public default SAnnotatedValue sAnnotatedValue(
      SAnnotation annotation, SType type, String name, Location location) {
    return new SAnnotatedValue(annotation, sSchema(type), fqn(name), location);
  }

  public default SNamedExprValue sValue(String name, SExpr body) {
    return sValue(1, name, body);
  }

  public default SNamedExprValue sValue(int line, String name, SExpr body) {
    return sValue(line, body.evaluationType(), name, body);
  }

  public default SNamedExprValue sValue(int line, SType type, String name, SExpr body) {
    return sValue(line, sSchema(type), name, body);
  }

  public default SNamedExprValue sValue(SSchema schema, String name, SExpr body) {
    return sValue(1, schema, name, body);
  }

  public default SNamedExprValue sValue(int line, SSchema schema, String name, SExpr body) {
    return new SNamedExprValue(schema, fqn(name), body, location(line));
  }

  public default SNamedValue emptySArrayValue() {
    return emptySArrayValue(varA());
  }

  public default SNamedValue emptySArrayValue(SVar elementType) {
    return sValue("emptyArray", sOrder(elementType));
  }

  public default SConstructor sConstructor(SStructType structType) {
    return sConstructor(1, structType, UPPER_CAMEL.to(LOWER_CAMEL, structType.name()));
  }

  public default SConstructor sConstructor(int line, SStructType structType) {
    return sConstructor(line, structType, structType.name());
  }

  public default SConstructor sConstructor(int line, SStructType structType, String name) {
    var fields = structType.fields();
    var params = fields.map(f -> new SItem(f.type(), f.name(), none(), location(2)));
    return new SConstructor(sFuncSchema(params, structType), fqn(name), params, location(line));
  }

  public default SAnnotatedFunc sBytecodeFunc(
      String path, SType resultType, String name, NList<SItem> params) {
    return sBytecodeFunc(1, path, resultType, name, params);
  }

  public default SAnnotatedFunc sBytecodeFunc(
      int line, SType resultType, String name, NList<SItem> params) {
    return sAnnotatedFunc(line, sBytecode(line - 1, "impl"), resultType, name, params);
  }

  public default SAnnotatedFunc sBytecodeFunc(
      int line, String path, SType resultType, String name, NList<SItem> params) {
    return sAnnotatedFunc(line, sBytecode(path), resultType, name, params);
  }

  public default SAnnotatedFunc sNativeFunc(SType resultType, String name, NList<SItem> params) {
    return sAnnotatedFunc(sNativeAnnotation(), resultType, name, params);
  }

  public default SAnnotatedFunc sAnnotatedFunc(
      SAnnotation ann, SType resultType, String name, NList<SItem> params) {
    return sAnnotatedFunc(1, ann, resultType, name, params);
  }

  public default SAnnotatedFunc sAnnotatedFunc(
      int line, SAnnotation ann, SType resultType, String name, NList<SItem> params) {
    return sAnnotatedFunc(ann, resultType, name, params, location(line));
  }

  public default SAnnotatedFunc sAnnotatedFunc(
      SAnnotation ann, SType resultType, String name, NList<SItem> params, Location location) {
    return new SAnnotatedFunc(ann, sFuncSchema(params, resultType), fqn(name), params, location);
  }

  public default SNamedExprFunc sFunc(int line, String name, NList<SItem> params, SExpr body) {
    return sFunc(line, body.evaluationType(), name, params, body);
  }

  public default SNamedExprFunc sFunc(String name, NList<SItem> params, SExpr body) {
    return sFunc(body.evaluationType(), name, params, body);
  }

  public default SNamedExprFunc sFunc(
      SType resultType, String name, NList<SItem> params, SExpr body) {
    return sFunc(1, resultType, name, params, body);
  }

  public default SNamedExprFunc sFunc(
      int line, SType resultType, String name, NList<SItem> params, SExpr body) {
    var schema = sFuncSchema(params, resultType);
    return new SNamedExprFunc(schema, fqn(name), params, body, location(line));
  }

  public default SLambda sLambda(SVarSet quantifiedVars, SExpr body) {
    return sLambda(quantifiedVars, nlist(), body);
  }

  public default SLambda sLambda(SVarSet quantifiedVars, NList<SItem> params, SExpr body) {
    return sLambda(1, quantifiedVars, params, body);
  }

  public default SLambda sLambda(
      int line, SVarSet quantifiedVars, NList<SItem> params, SExpr body) {
    var funcTS = sFuncType(toTypes(params.list()), body.evaluationType());
    var funcSSchema = sFuncSchema(quantifiedVars, funcTS);
    return new SLambda(funcSSchema, params, body, location(line));
  }

  public default SLambda sLambda(SExpr body) {
    return sLambda(1, nlist(), body);
  }

  public default SLambda sLambda(NList<SItem> params, SExpr body) {
    return sLambda(1, params, body);
  }

  public default SLambda sLambda(int line, NList<SItem> params, SExpr body) {
    var funcSSchema = sFuncSchema(toTypes(params.list()), body.evaluationType());
    return new SLambda(funcSSchema, params, body, location(line));
  }

  public default SNamedExprFunc idSFunc() {
    var a = varA();
    return sFunc(a, "myId", nlist(sItem(a, "a")), sParamRef(a, "a"));
  }

  public default SNamedExprFunc intIdSFunc() {
    return sFunc(sIntType(), "myIntId", nlist(sItem(sIntType(), "i")), sParamRef(sIntType(), "i"));
  }

  public default SNamedExprFunc returnIntSFunc() {
    return sFunc(sIntType(), "myReturnInt", nlist(), sInt(1, 3));
  }

  public default SItemSig sSig(SType type, String name) {
    return new SItemSig(type, referenceableName(name));
  }

  public default PModule pModule(List<PStruct> structs, List<PNamedEvaluable> evaluables) {
    return new PModule("Module.smooth", structs, evaluables);
  }

  public default PInstantiate pLambda(NList<PItem> params, PExpr body) {
    var pLambda = new PLambda("lambda_1", params, body, location());
    pLambda.setId(fqn("lambda_1"));
    return pInstantiate(pLambda);
  }

  public default PCall pCall(PExpr callee) {
    return pCall(callee, location());
  }

  public default PCall pCall(PExpr callee, Location location) {
    return new PCall(callee, list(), location);
  }

  public default PInstantiate pInstantiate(PPolymorphic polymorphic) {
    return new PInstantiate(polymorphic, polymorphic.location());
  }

  public default PNamedFunc pNamedFunc() {
    return pNamedFunc(nlist(pItem()));
  }

  public default PNamedFunc pNamedFunc(String name) {
    return pNamedFunc(name, nlist(pItem()));
  }

  public default PNamedFunc pNamedFunc(String name, int line) {
    return pNamedFunc(name, nlist(pItem()), none(), location(line));
  }

  public default PNamedFunc pNamedFunc(NList<PItem> params) {
    return pNamedFunc("myFunc", params);
  }

  public default PNamedFunc pNamedFunc(String name, PExpr body) {
    return pNamedFunc(name, nlist(), some(body));
  }

  public default PNamedFunc pNamedFunc(String name, NList<PItem> params) {
    return pNamedFunc(name, params, none());
  }

  public default PNamedFunc pNamedFunc(String name, NList<PItem> params, Maybe<PExpr> body) {
    return pNamedFunc(name, params, body, location());
  }

  public default PNamedFunc pNamedFunc(
      String name, NList<PItem> params, Maybe<PExpr> body, Location location) {
    var resultT = new PImplicitType(location);
    var pNamedFunc = new PNamedFunc(resultT, name, params, body, none(), location);
    pNamedFunc.setId(fqn(name));
    return pNamedFunc;
  }

  public default PNamedValue pNamedValue() {
    return pNamedValue(pInt());
  }

  public default PNamedValue pNamedValue(PExpr body) {
    return pNamedValue("myValue", body);
  }

  public default PNamedValue pNamedValue(String name) {
    return pNamedValue(name, pInt());
  }

  public default PNamedValue pNamedValue(String name, PExpr body) {
    var location = location();
    var type = new PImplicitType(location);
    return pNamedValue(name, body, type, location);
  }

  private static PNamedValue pNamedValue(
      String name, PExpr body, PImplicitType type, Location location) {
    var pNamedValue = new PNamedValue(type, name, some(body), none(), location);
    pNamedValue.setId(fqn(name));
    return pNamedValue;
  }

  public default PItem pItem() {
    return pItem(some(pInt()));
  }

  public default PItem pItem(Maybe<PExpr> defaultValue) {
    return pItem("param1", defaultValue);
  }

  public default PItem pItem(String name) {
    return pItem(name, none());
  }

  public default PItem pItem(String name, PExpr defaultValue) {
    return pItem(name, some(defaultValue));
  }

  public default PItem pItem(String name, Maybe<PExpr> defaultValue) {
    var pItem = new PItem(new PExplicitType("Int", location()), name, defaultValue, location());
    pItem.setName(referenceableName(name));
    return pItem;
  }

  public default PInt pInt() {
    return new PInt("7", location());
  }

  public default PInstantiate pReference(String name) {
    return pReference(name, location(7));
  }

  public default PInstantiate pReference(String name, Location location) {
    return pInstantiate(getPReference(name, location));
  }

  private static PReference getPReference(String name, Location location) {
    var pReference = new PReference(name, location);
    pReference.setId(fqn(name));
    return pReference;
  }

  @SuppressWarnings("unchecked")
  public default <T extends Identifiable> ImmutableBindings<T> bindings(T... nameds) {
    return immutableBindings(list(nameds).toMap(t -> t.id().full(), v -> v));
  }

  public default Log err(int line, String message) {
    return error(moduleFullPath().toString() + ":" + line + ": " + message);
  }

  public default Location location() {
    return location(11);
  }

  public default Location location(int line) {
    return location(moduleFullPath(), line);
  }
}
