package org.smoothbuild.parse;

import static java.util.stream.Collectors.toMap;
import static org.smoothbuild.lang.function.base.Scope.scope;
import static org.smoothbuild.lang.type.ArrayType.arrayOf;
import static org.smoothbuild.lang.type.Types.NOTHING;
import static org.smoothbuild.lang.type.Types.STRING;
import static org.smoothbuild.lang.type.Types.basicTypes;
import static org.smoothbuild.lang.type.Types.closestCommonConvertibleTo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.smoothbuild.lang.function.Functions;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.function.base.Scope;
import org.smoothbuild.lang.function.base.TypedName;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.lang.type.Types;
import org.smoothbuild.lang.value.Value;
import org.smoothbuild.parse.ast.ArgNode;
import org.smoothbuild.parse.ast.ArrayNode;
import org.smoothbuild.parse.ast.ArrayTypeNode;
import org.smoothbuild.parse.ast.Ast;
import org.smoothbuild.parse.ast.CallNode;
import org.smoothbuild.parse.ast.ExprNode;
import org.smoothbuild.parse.ast.FuncNode;
import org.smoothbuild.parse.ast.ParamNode;
import org.smoothbuild.parse.ast.RefNode;
import org.smoothbuild.parse.ast.StringNode;
import org.smoothbuild.parse.ast.TypeNode;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

public class AssignTypes {
  public static List<ParseError> assignTypes(Functions functions, Ast ast) {
    final Type nonInferable = new Type("<NonInferable>", Value.class) {};
    List<ParseError> errors = new ArrayList<>();
    Map<Name, Type> functionTypes = functions
        .nameToFunctionMap()
        .entrySet()
        .stream()
        .collect(toMap(e -> e.getKey(), e -> e.getValue().type()));
    new AstVisitor() {
      Scope<Type> scope;

      @Override
      public void visitFunction(FuncNode func) {
        visitName(func.name());
        visitParams(func.params());

        scope = scope();
        func.params()
            .stream()
            .forEach(p -> scope.add(p.name(), p.get(Type.class)));
        if (!func.isNative()) {
          visitExpr(func.expr());
        }
        scope = null;

        Type type = funcType(func);
        func.set(Type.class, type);
        functionTypes.put(func.name(), type);
        List<TypedName> parameters = createParameters(func.params());
        if (parameters != null) {
          func.set(List.class, parameters);
        }
      }

      private Type funcType(FuncNode func) {
        if (func.isNative()) {
          if (func.hasType()) {
            return createType(func.type());
          } else {
            errors.add(new ParseError(func, "Function '" + func.name()
                + "' is native so should have declared result type."));
            return nonInferable;
          }
        } else {
          Type exprType = func.expr().get(Type.class);
          if (func.hasType()) {
            Type type = createType(func.type());
            if (type != nonInferable && exprType != nonInferable && !type.isAssignableFrom(
                exprType)) {
              errors.add(new ParseError(func, "Type of function's '" + func.name()
                  + "' expression is " + exprType
                  + " which is not convertable to function's declared result type " + type + "."));
            }
            return type;
          } else {
            return exprType;
          }
        }
      }

      private List<TypedName> createParameters(List<ParamNode> params) {
        Builder<TypedName> builder = ImmutableList.builder();
        for (ParamNode param : params) {
          if (param.has(TypedName.class)) {
            builder.add(param.get(TypedName.class));
          } else {
            return null;
          }
        }
        return builder.build();
      }

      @Override
      public void visitParam(ParamNode param) {
        super.visitParam(param);
        Type type = param.type().get(Type.class);
        param.set(Type.class, type);
        if (type != nonInferable) {
          param.set(TypedName.class, new TypedName(param.get(Type.class), param.name()));
          if (param.hasDefaultValue() && param.defaultValue().get(Type.class) != nonInferable) {
            Type valueType = param.defaultValue().get((Type.class));
            if (!type.isAssignableFrom(valueType)) {
              errors.add(new ParseError(param, "Parameter '" + param.name()
                  + "' is of type " + type + " so it cannot have default value of type "
                  + valueType + "."));
            }
          }
        }
      }

      @Override
      public void visitType(TypeNode type) {
        super.visitType(type);
        Type inferredType = createType(type);
        type.set(Type.class, inferredType == null ? nonInferable : inferredType);
      }

      private Type createType(TypeNode type) {
        if (type instanceof ArrayTypeNode) {
          TypeNode elementType = ((ArrayTypeNode) type).elementType();
          Type inferredElemType = createType(elementType);
          return inferredElemType == null ? null : arrayOf(inferredElemType);
        }
        Type result = Types.basicTypeFromString(type.name());
        if (result == null) {
          errors.add(new ParseError(type.location(), "Unknown type '" + type.name() + "'."));
        }
        return result;
      }

      @Override
      public void visitArray(ArrayNode array) {
        super.visitArray(array);
        array.set(Type.class, findArrayType(array));
      }

      private Type findArrayType(ArrayNode array) {
        List<ExprNode> expressions = array.elements();
        if (expressions.isEmpty()) {
          return arrayOf(NOTHING);
        }
        Type firstType = expressions.get(0).get(Type.class);
        if (nonInferable.equals(firstType)) {
          return nonInferable;
        }
        Type superType = firstType;
        for (int i = 1; i < expressions.size(); i++) {
          Type type = expressions.get(i).get(Type.class);
          if (nonInferable.equals(type)) {
            return nonInferable;
          }
          superType = closestCommonConvertibleTo(superType, type);

          if (superType == null) {
            errors.add(new ParseError(array,
                "Array cannot contain elements of incompatible types.\n"
                    + "First element has type '" + firstType + "' while element at index " + i
                    + " has type '" + type + "'."));
            return nonInferable;
          }
        }
        if (!basicTypes().contains(superType)) {
          errors.add(new ParseError(array, "Array cannot contain element with type '"
              + superType + "'. Only following types are allowed: " + basicTypes()
              + "."));
          return nonInferable;
        }
        return arrayOf(superType);
      }

      @Override
      public void visitCall(CallNode call) {
        super.visitCall(call);
        call.set(Type.class, functionTypes.get(call.name()));
      }

      @Override
      public void visitRef(RefNode ref) {
        super.visitRef(ref);
        ref.set(Type.class, scope.get(ref.name()));
      }

      @Override
      public void visitArg(ArgNode arg) {
        super.visitArg(arg);
        arg.set(Type.class, arg.expr().get(Type.class));
      }

      @Override
      public void visitString(StringNode string) {
        super.visitString(string);
        string.set(Type.class, STRING);
      }
    }.visitAst(ast);
    return errors;
  }
}
