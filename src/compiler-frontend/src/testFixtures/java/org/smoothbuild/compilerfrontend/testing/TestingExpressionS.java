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
import static org.smoothbuild.compilerfrontend.lang.define.ItemS.toTypes;
import static org.smoothbuild.compilerfrontend.lang.type.AnnotationNames.BYTECODE;
import static org.smoothbuild.compilerfrontend.lang.type.AnnotationNames.NATIVE_IMPURE;
import static org.smoothbuild.compilerfrontend.lang.type.AnnotationNames.NATIVE_PURE;
import static org.smoothbuild.compilerfrontend.lang.type.VarSetS.varSetS;

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
import org.smoothbuild.compilerfrontend.lang.define.AnnotatedFuncS;
import org.smoothbuild.compilerfrontend.lang.define.AnnotatedValueS;
import org.smoothbuild.compilerfrontend.lang.define.AnnotationS;
import org.smoothbuild.compilerfrontend.lang.define.BlobS;
import org.smoothbuild.compilerfrontend.lang.define.CallS;
import org.smoothbuild.compilerfrontend.lang.define.CombineS;
import org.smoothbuild.compilerfrontend.lang.define.ConstructorS;
import org.smoothbuild.compilerfrontend.lang.define.ExprS;
import org.smoothbuild.compilerfrontend.lang.define.InstantiateS;
import org.smoothbuild.compilerfrontend.lang.define.IntS;
import org.smoothbuild.compilerfrontend.lang.define.ItemS;
import org.smoothbuild.compilerfrontend.lang.define.ItemSigS;
import org.smoothbuild.compilerfrontend.lang.define.LambdaS;
import org.smoothbuild.compilerfrontend.lang.define.NamedEvaluableS;
import org.smoothbuild.compilerfrontend.lang.define.NamedExprFuncS;
import org.smoothbuild.compilerfrontend.lang.define.NamedExprValueS;
import org.smoothbuild.compilerfrontend.lang.define.NamedValueS;
import org.smoothbuild.compilerfrontend.lang.define.OrderS;
import org.smoothbuild.compilerfrontend.lang.define.PolymorphicS;
import org.smoothbuild.compilerfrontend.lang.define.ReferenceS;
import org.smoothbuild.compilerfrontend.lang.define.SelectS;
import org.smoothbuild.compilerfrontend.lang.define.StringS;
import org.smoothbuild.compilerfrontend.lang.define.TraceS;
import org.smoothbuild.compilerfrontend.lang.define.TraceS.Element;
import org.smoothbuild.compilerfrontend.lang.type.ArrayTS;
import org.smoothbuild.compilerfrontend.lang.type.BlobTS;
import org.smoothbuild.compilerfrontend.lang.type.BoolTS;
import org.smoothbuild.compilerfrontend.lang.type.FuncSchemaS;
import org.smoothbuild.compilerfrontend.lang.type.FuncTS;
import org.smoothbuild.compilerfrontend.lang.type.IntTS;
import org.smoothbuild.compilerfrontend.lang.type.InterfaceTS;
import org.smoothbuild.compilerfrontend.lang.type.SchemaS;
import org.smoothbuild.compilerfrontend.lang.type.StringTS;
import org.smoothbuild.compilerfrontend.lang.type.StructTS;
import org.smoothbuild.compilerfrontend.lang.type.TempVarS;
import org.smoothbuild.compilerfrontend.lang.type.TupleTS;
import org.smoothbuild.compilerfrontend.lang.type.TypeFS;
import org.smoothbuild.compilerfrontend.lang.type.TypeS;
import org.smoothbuild.compilerfrontend.lang.type.VarS;
import org.smoothbuild.compilerfrontend.lang.type.VarSetS;

public class TestingExpressionS {
  public static final BucketId STANDARD_LIBRARY_BUCKET_ID = bucketId("ssl");
  public static final BucketId PROJECT_BUCKET_ID = bucketId("prj");
  public static final String BUILD_FILE_PATH = "build.smooth";
  private static final String IMPORTED_FILE_PATH = "imported.smooth";
  static final FullPath STANDARD_LIBRARY_MODULE_FILE_PATH =
      fullPath(STANDARD_LIBRARY_BUCKET_ID, path("std_lib.smooth"));
  static final FullPath DEFAULT_MODULE_FILE_PATH =
      fullPath(PROJECT_BUCKET_ID, path("build.smooth"));

  public static java.util.List<TypeS> typesToTest() {
    return nonCompositeTypes().stream()
        .flatMap(t -> compositeTypeSFactories().stream().map(f -> f.apply(t)))
        .toList();
  }

  public static List<TypeS> nonCompositeTypes() {
    return TypeFS.baseTs().append(new VarS("A"));
  }

  public static java.util.List<Function<TypeS, TypeS>> compositeTypeSFactories() {
    java.util.List<Function<TypeS, TypeS>> simpleFactories = java.util.List.of(
        TestingExpressionS::arrayTS,
        TestingExpressionS::funcTS,
        t -> funcTS(t, intTS()),
        TestingExpressionS::tupleTS,
        TestingExpressionS::structTS,
        TestingExpressionS::interfaceTS);
    java.util.List<Function<TypeS, TypeS>> factories = new ArrayList<>();
    factories.addAll(simpleFactories);
    for (var simpleFactory : simpleFactories) {
      for (var simpleFactory2 : simpleFactories) {
        Function<TypeS, TypeS> compositeFactory = t -> simpleFactory.apply(simpleFactory2.apply(t));
        factories.add(compositeFactory);
      }
    }
    return factories;
  }

  public static ArrayTS arrayTS(TypeS elemT) {
    return new ArrayTS(elemT);
  }

  public static BlobTS blobTS() {
    return TypeFS.BLOB;
  }

  public static BoolTS boolTS() {
    return TypeFS.BOOL;
  }

  public static FuncTS funcTS(TypeS resultT) {
    return funcTS(list(), resultT);
  }

  public static FuncTS funcTS(TypeS param1, TypeS resultT) {
    return funcTS(list(param1), resultT);
  }

  public static FuncTS funcTS(TypeS param1, TypeS param2, TypeS resultT) {
    return funcTS(list(param1, param2), resultT);
  }

  public static FuncTS funcTS(List<TypeS> paramTs, TypeS resultT) {
    return new FuncTS(tupleTS(paramTs), resultT);
  }

  public static TupleTS tupleTS(TypeS... itemTs) {
    return tupleTS(list(itemTs));
  }

  public static TupleTS tupleTS(List<TypeS> paramTs) {
    return new TupleTS(paramTs);
  }

  public static IntTS intTS() {
    return TypeFS.INT;
  }

  public static FuncSchemaS funcSchemaS(NList<ItemS> params, TypeS resultT) {
    return funcSchemaS(toTypes(params.list()), resultT);
  }

  public static FuncSchemaS funcSchemaS(TypeS resultT) {
    return funcSchemaS(funcTS(list(), resultT));
  }

  public static FuncSchemaS funcSchemaS(TypeS param1, TypeS resultT) {
    return funcSchemaS(funcTS(list(param1), resultT));
  }

  public static FuncSchemaS funcSchemaS(List<TypeS> paramTs, TypeS resultT) {
    return funcSchemaS(funcTS(paramTs, resultT));
  }

  private static FuncSchemaS funcSchemaS(FuncTS funcTS) {
    return funcSchemaS(funcTS.vars(), funcTS);
  }

  private static FuncSchemaS funcSchemaS(VarSetS quantifiedVars, FuncTS funcTS) {
    return new FuncSchemaS(quantifiedVars, funcTS);
  }

  public static InterfaceTS interfaceTS() {
    return interfaceTS(map());
  }

  public static InterfaceTS interfaceTS(TypeS... fieldTs) {
    return interfaceTS(typesToItemSigsMap(fieldTs));
  }

  public static InterfaceTS interfaceTS(ItemSigS... fieldTs) {
    return interfaceTS(itemSigsToMap(fieldTs));
  }

  public static InterfaceTS interfaceTS(Map<String, ItemSigS> fieldSignatures) {
    return new InterfaceTS(fieldSignatures);
  }

  public static SchemaS schemaS(TypeS typeS) {
    return new SchemaS(typeS.vars(), typeS);
  }

  public static StructTS personTS() {
    return structTS("Person", nlist(sigS(stringTS(), "firstName"), sigS(stringTS(), "lastName")));
  }

  public static StructTS animalTS() {
    return structTS("Animal", nlist(sigS(stringTS(), "name"), sigS(intTS(), "size")));
  }

  public static StringTS stringTS() {
    return TypeFS.STRING;
  }

  public static StructTS structTS(TypeS... fieldTs) {
    return structTS("MyStruct", fieldTs);
  }

  public static StructTS structTS(String myStruct) {
    return structTS(myStruct, nlist());
  }

  public static StructTS structTS(String myStruct, TypeS... fieldTs) {
    return structTS(myStruct, nlist(typesToItemSigs(fieldTs)));
  }

  public static StructTS structTS(String myStruct, ItemSigS... fieldSigs) {
    return structTS(myStruct, nlist(fieldSigs));
  }

  private static List<ItemSigS> typesToItemSigs(TypeS... fieldTs) {
    var builder = new ArrayList<ItemSigS>();
    for (int i = 0; i < fieldTs.length; i++) {
      builder.add(sigS(fieldTs[i], "param" + i));
    }
    return listOfAll(builder);
  }

  public static Map<String, ItemSigS> typesToItemSigsMap(TypeS... types) {
    return itemSigsToMap(typeTsToSigS(types));
  }

  public static Map<String, ItemSigS> itemSigsToMap(ItemSigS... itemSigs) {
    return itemSigsToMap(list(itemSigs));
  }

  public static Map<String, ItemSigS> itemSigsToMap(List<ItemSigS> sigs) {
    return sigs.toMap(ItemSigS::name, f -> f);
  }

  private static List<ItemSigS> typeTsToSigS(TypeS... types) {
    var builder = new ArrayList<ItemSigS>();
    for (int i = 0; i < types.length; i++) {
      builder.add(sigS(types[i], "param" + i));
    }
    return listOfAll(builder);
  }

  public static StructTS structTS(String name, NList<ItemSigS> fields) {
    return new StructTS(name, fields);
  }

  public static VarS tempVarA() {
    return tempVar("1");
  }

  public static TempVarS tempVar(String name) {
    return new TempVarS(name);
  }

  public static VarS varA() {
    return varS("A");
  }

  public static VarS varB() {
    return varS("B");
  }

  public static VarS varC() {
    return varS("C");
  }

  public static VarS varX() {
    return varS("X");
  }

  public static VarS varS(String name) {
    return new VarS(name);
  }

  // ExprS-s

  public static BlobS blobS(int data) {
    return blobS(1, data);
  }

  public static BlobS blobS(int line, int data) {
    return new BlobS(blobTS(), intToByteString(data), location(line));
  }

  public static CallS callS(ExprS callable, ExprS... args) {
    return callS(1, callable, args);
  }

  public static CallS callS(int line, ExprS callable, ExprS... args) {
    return new CallS(callable, combineS(line, args), location(line));
  }

  public static CombineS combineS(ExprS... args) {
    return combineS(13, args);
  }

  private static CombineS combineS(int line, ExprS... args) {
    var argsList = list(args);
    var evaluationType = new TupleTS(argsList.map(ExprS::evaluationType));
    return new CombineS(evaluationType, argsList, location(line));
  }

  public static IntS intS(int value) {
    return intS(1, value);
  }

  public static IntS intS(int line, int value) {
    return new IntS(intTS(), BigInteger.valueOf(value), location(line));
  }

  public static Map<VarS, TypeS> varMap() {
    return map();
  }

  public static Map<VarS, TypeS> varMap(VarS var, TypeS type) {
    return map(var, type);
  }

  public static InstantiateS instantiateS(NamedEvaluableS namedEvaluableS) {
    return instantiateS(17, namedEvaluableS);
  }

  public static InstantiateS instantiateS(int line, NamedEvaluableS namedEvaluableS) {
    return instantiateS(line, referenceS(line, namedEvaluableS));
  }

  public static InstantiateS instantiateS(List<TypeS> typeArgs, NamedEvaluableS namedEvaluableS) {
    return instantiateS(1, typeArgs, namedEvaluableS);
  }

  public static InstantiateS instantiateS(
      int line, List<TypeS> typeArgs, NamedEvaluableS namedEvaluableS) {
    var location = location(line);
    var referenceS = new ReferenceS(namedEvaluableS.schema(), namedEvaluableS.name(), location);
    return instantiateS(typeArgs, referenceS, location);
  }

  public static InstantiateS instantiateS(PolymorphicS polymorphicS) {
    return instantiateS(polymorphicS, polymorphicS.location());
  }

  public static InstantiateS instantiateS(int line, PolymorphicS polymorphicS) {
    return instantiateS(polymorphicS, location(line));
  }

  public static InstantiateS instantiateS(PolymorphicS polymorphicS, Location location) {
    return instantiateS(list(), polymorphicS, location);
  }

  public static InstantiateS instantiateS(List<TypeS> typeArgs, PolymorphicS polymorphicS) {
    return instantiateS(1, typeArgs, polymorphicS);
  }

  public static InstantiateS instantiateS(
      int line, List<TypeS> typeArgs, PolymorphicS polymorphicS) {
    return instantiateS(typeArgs, polymorphicS, location(line));
  }

  public static InstantiateS instantiateS(
      List<TypeS> typeArgs, PolymorphicS polymorphicS, Location location) {
    return new InstantiateS(typeArgs, polymorphicS, location);
  }

  public static OrderS orderS(int line, ExprS headElem, ExprS... tailElems) {
    return new OrderS(
        arrayTS(headElem.evaluationType()), list(headElem).append(tailElems), location(line));
  }

  public static OrderS orderS(TypeS elemT, ExprS... exprs) {
    return orderS(1, elemT, exprs);
  }

  public static OrderS orderS(int line, TypeS elemT, ExprS... exprs) {
    return new OrderS(arrayTS(elemT), list(exprs), location(line));
  }

  public static InstantiateS paramRefS(TypeS type, String name) {
    return paramRefS(1, type, name);
  }

  public static InstantiateS paramRefS(int line, TypeS type, String name) {
    return instantiateS(line, referenceS(line, new SchemaS(varSetS(), type), name));
  }

  public static ReferenceS referenceS(int line, NamedEvaluableS namedEvaluableS) {
    return referenceS(line, namedEvaluableS.schema(), namedEvaluableS.name());
  }

  public static ReferenceS referenceS(int line, SchemaS schema, String name) {
    return referenceS(schema, name, location(line));
  }

  public static ReferenceS referenceS(SchemaS schema, String name, Location location) {
    return new ReferenceS(schema, name, location);
  }

  public static SelectS selectS(ExprS selectable, String field) {
    return selectS(1, selectable, field);
  }

  public static SelectS selectS(int line, ExprS selectable, String field) {
    return new SelectS(selectable, field, location(line));
  }

  public static StringS stringS() {
    return stringS("abc");
  }

  public static StringS stringS(String string) {
    return stringS(1, string);
  }

  public static StringS stringS(int line, String data) {
    return new StringS(stringTS(), data, location(line));
  }

  // other smooth language thingies

  private static AnnotationS bytecodeS(String path) {
    return bytecodeS(1, path);
  }

  public static AnnotationS bytecodeS(int line, String path) {
    return bytecodeS(line, stringS(line, path));
  }

  public static AnnotationS bytecodeS(int line, StringS path) {
    return bytecodeS(path, location(line));
  }

  public static AnnotationS bytecodeS(String path, Location location) {
    return bytecodeS(stringS(path), location);
  }

  public static AnnotationS bytecodeS(StringS path, Location location) {
    return new AnnotationS(BYTECODE, path, location);
  }

  public static AnnotationS nativeAnnotationS() {
    return nativeAnnotationS(1, stringS("impl"));
  }

  public static AnnotationS nativeAnnotationS(int line, StringS classBinaryName) {
    return nativeAnnotationS(line, classBinaryName, true);
  }

  public static AnnotationS nativeAnnotationS(int line, StringS classBinaryName, boolean pure) {
    return nativeAnnotationS(location(line), classBinaryName, pure);
  }

  public static AnnotationS nativeAnnotationS(Location location, StringS classBinaryName) {
    return nativeAnnotationS(location, classBinaryName, true);
  }

  public static AnnotationS nativeAnnotationS(
      Location location, StringS classBinaryName, boolean pure) {
    var name = pure ? NATIVE_PURE : NATIVE_IMPURE;
    return new AnnotationS(name, classBinaryName, location);
  }

  public static ItemS itemS(TypeS type) {
    return itemS(1, type, "paramName");
  }

  public static ItemS itemS(TypeS type, String name) {
    return itemS(1, type, name);
  }

  public static ItemS itemS(int line, TypeS type, String name) {
    return itemS(line, type, name, none());
  }

  public static ItemS itemS(int line, TypeS type, String name, ExprS body) {
    return itemS(line, type, name, some(body));
  }

  public static ItemS itemS(String name, ExprS body) {
    return itemS(body.evaluationType(), name, some(body));
  }

  public static ItemS itemS(TypeS type, String name, Maybe<ExprS> body) {
    return itemS(1, type, name, body);
  }

  public static ItemS itemS(int line, TypeS type, String name, Maybe<ExprS> body) {
    return itemSPoly(line, type, name, body.map(b -> valueS(line, name, b)));
  }

  public static ItemS itemS(int line, TypeS type, String name, NamedValueS body) {
    return itemSPoly(line, type, name, some(body));
  }

  public static ItemS itemSPoly(int line, TypeS type, String name, Maybe<NamedValueS> body) {
    return new ItemS(type, name, body, location(line));
  }

  public static AnnotatedValueS bytecodeValueS(int line, TypeS type, String name) {
    return annotatedValueS(line, bytecodeS(line - 1, "impl"), type, name);
  }

  public static AnnotatedValueS annotatedValueS(
      int line, AnnotationS annotationS, TypeS type, String name) {
    return annotatedValueS(annotationS, type, name, location(line));
  }

  public static AnnotatedValueS annotatedValueS(
      AnnotationS annotationS, TypeS type, String name, Location location) {
    return new AnnotatedValueS(annotationS, schemaS(type), name, location);
  }

  public static NamedExprValueS valueS(String name, ExprS body) {
    return valueS(1, name, body);
  }

  public static NamedExprValueS valueS(int line, String name, ExprS body) {
    return valueS(line, body.evaluationType(), name, body);
  }

  public static NamedExprValueS valueS(int line, TypeS type, String name, ExprS body) {
    return valueS(line, schemaS(type), name, body);
  }

  public static NamedExprValueS valueS(SchemaS schema, String name, ExprS body) {
    return valueS(1, schema, name, body);
  }

  public static NamedExprValueS valueS(int line, SchemaS schema, String name, ExprS body) {
    return new NamedExprValueS(schema, name, body, location(line));
  }

  public static NamedValueS emptyArrayValueS() {
    return emptyArrayValueS(varA());
  }

  public static NamedValueS emptyArrayValueS(VarS elemT) {
    return valueS("emptyArray", orderS(elemT));
  }

  public static ConstructorS constructorS(StructTS structT) {
    return constructorS(1, structT, UPPER_CAMEL.to(LOWER_CAMEL, structT.name()));
  }

  public static ConstructorS constructorS(int line, StructTS structT) {
    return constructorS(line, structT, structT.name());
  }

  public static ConstructorS constructorS(int line, StructTS structT, String name) {
    var fields = structT.fields();
    var params = fields.map(f -> new ItemS(f.type(), f.name(), none(), location(2)));
    return new ConstructorS(funcSchemaS(params, structT), name, params, location(line));
  }

  public static AnnotatedFuncS bytecodeFuncS(
      String path, TypeS resultT, String name, NList<ItemS> params) {
    return bytecodeFuncS(1, path, resultT, name, params);
  }

  public static AnnotatedFuncS bytecodeFuncS(
      int line, TypeS resultT, String name, NList<ItemS> params) {
    return annotatedFuncS(line, bytecodeS(line - 1, "impl"), resultT, name, params);
  }

  public static AnnotatedFuncS bytecodeFuncS(
      int line, String path, TypeS resultT, String name, NList<ItemS> params) {
    return annotatedFuncS(line, bytecodeS(path), resultT, name, params);
  }

  public static AnnotatedFuncS nativeFuncS(TypeS resultT, String name, NList<ItemS> params) {
    return annotatedFuncS(nativeAnnotationS(), resultT, name, params);
  }

  public static AnnotatedFuncS annotatedFuncS(
      AnnotationS ann, TypeS resultT, String name, NList<ItemS> params) {
    return annotatedFuncS(1, ann, resultT, name, params);
  }

  public static AnnotatedFuncS annotatedFuncS(
      int line, AnnotationS ann, TypeS resultT, String name, NList<ItemS> params) {
    return annotatedFuncS(ann, resultT, name, params, location(line));
  }

  public static AnnotatedFuncS annotatedFuncS(
      AnnotationS ann, TypeS resultT, String name, NList<ItemS> params, Location location) {
    return new AnnotatedFuncS(ann, funcSchemaS(params, resultT), name, params, location);
  }

  public static NamedExprFuncS funcS(int line, String name, NList<ItemS> params, ExprS body) {
    return funcS(line, body.evaluationType(), name, params, body);
  }

  public static NamedExprFuncS funcS(String name, NList<ItemS> params, ExprS body) {
    return funcS(body.evaluationType(), name, params, body);
  }

  public static NamedExprFuncS funcS(TypeS resultT, String name, NList<ItemS> params, ExprS body) {
    return funcS(1, resultT, name, params, body);
  }

  public static NamedExprFuncS funcS(
      int line, TypeS resultT, String name, NList<ItemS> params, ExprS body) {
    var schema = funcSchemaS(params, resultT);
    return new NamedExprFuncS(schema, name, params, body, location(line));
  }

  public static LambdaS lambdaS(VarSetS quantifiedVars, ExprS body) {
    return lambdaS(quantifiedVars, nlist(), body);
  }

  public static LambdaS lambdaS(VarSetS quantifiedVars, NList<ItemS> params, ExprS body) {
    return lambdaS(1, quantifiedVars, params, body);
  }

  public static LambdaS lambdaS(int line, VarSetS quantifiedVars, NList<ItemS> params, ExprS body) {
    var funcTS = funcTS(toTypes(params.list()), body.evaluationType());
    var funcSchemaS = funcSchemaS(quantifiedVars, funcTS);
    return new LambdaS(funcSchemaS, params, body, location(line));
  }

  public static LambdaS lambdaS(ExprS body) {
    return lambdaS(1, nlist(), body);
  }

  public static LambdaS lambdaS(NList<ItemS> params, ExprS body) {
    return lambdaS(1, params, body);
  }

  public static LambdaS lambdaS(int line, NList<ItemS> params, ExprS body) {
    var funcSchemaS = funcSchemaS(toTypes(params.list()), body.evaluationType());
    return new LambdaS(funcSchemaS, params, body, location(line));
  }

  public static NamedExprFuncS idFuncS() {
    var a = varA();
    return funcS(a, "myId", nlist(itemS(a, "a")), paramRefS(a, "a"));
  }

  public static NamedExprFuncS intIdFuncS() {
    return funcS(intTS(), "myIntId", nlist(itemS(intTS(), "i")), paramRefS(intTS(), "i"));
  }

  public static NamedExprFuncS returnIntFuncS() {
    return funcS(intTS(), "myReturnInt", nlist(), intS(1, 3));
  }

  public static ItemSigS sigS(TypeS type, String name) {
    return new ItemSigS(type, name);
  }

  public static TraceS traceS() {
    return new TraceS();
  }

  public static TraceS traceS(String name2, int line2, String name1, int line1) {
    return traceS(name2, location(line2), name1, location(line1));
  }

  public static TraceS traceS(String name2, Location location2, String name1, Location location1) {
    var element1 = new Element(name1, location1, null);
    var element2 = new Element(name2, location2, element1);
    return new TraceS(element2);
  }

  public static TraceS traceS(String name, int line) {
    return traceS(name, location(line));
  }

  public static TraceS traceS(String name, Location location) {
    return new TraceS(new TraceS.Element(name, location, null));
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
