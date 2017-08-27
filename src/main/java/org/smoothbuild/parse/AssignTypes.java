package org.smoothbuild.parse;

import static java.util.stream.Collectors.toMap;
import static org.smoothbuild.lang.type.Types.NIL;
import static org.smoothbuild.lang.type.Types.STRING;
import static org.smoothbuild.lang.type.Types.commonSuperType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.smoothbuild.lang.function.Functions;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.function.base.Parameter;
import org.smoothbuild.lang.function.base.Signature;
import org.smoothbuild.lang.type.ArrayType;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.lang.type.Types;
import org.smoothbuild.parse.ast.ArgNode;
import org.smoothbuild.parse.ast.ArrayNode;
import org.smoothbuild.parse.ast.ArrayTypeNode;
import org.smoothbuild.parse.ast.Ast;
import org.smoothbuild.parse.ast.CallNode;
import org.smoothbuild.parse.ast.ExprNode;
import org.smoothbuild.parse.ast.FuncNode;
import org.smoothbuild.parse.ast.ParamNode;
import org.smoothbuild.parse.ast.StringNode;
import org.smoothbuild.parse.ast.TypeNode;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

public class AssignTypes {
  public static List<ParseError> assignTypes(Functions functions, Ast ast) {
    List<ParseError> errors = new ArrayList<>();
    Map<Name, Type> functionTypes = functions
        .nameToFunctionMap()
        .entrySet()
        .stream()
        .collect(toMap(e -> e.getKey(), e -> e.getValue().type()));
    new AstVisitor() {
      public void visitFunction(FuncNode function) {
        super.visitFunction(function);
        Type type = function.expr().get(Type.class);
        function.set(Type.class, type);
        functionTypes.put(function.name(), type);
        List<Parameter> parameters = createParameters(function.params());
        Signature signature = (type == null || parameters == null)
            ? null
            : new Signature(type, function.name(), parameters);
        function.set(Signature.class, signature);
      }

      private List<Parameter> createParameters(List<ParamNode> params) {
        Builder<Parameter> builder = ImmutableList.builder();
        for (ParamNode param : params) {
          Parameter parameter = param.get(Parameter.class);
          if (parameter == null) {
            return null;
          }
          builder.add(parameter);
        }
        return builder.build();
      }

      public void visitParam(ParamNode param) {
        super.visitParam(param);
        Type type = param.type().get(Type.class);
        param.set(Type.class, type);
        Parameter parameter = type == null
            ? null
            : new Parameter(param.get(Type.class), param.name(), null);
        param.set(Parameter.class, parameter);
      }

      public void visitType(TypeNode type) {
        super.visitType(type);
        type.set(Type.class, createType(type));
      }

      private Type createType(TypeNode type) {
        if (type instanceof ArrayTypeNode) {
          TypeNode elementType = ((ArrayTypeNode) type).elementType();
          return Types.arrayOf(createType(elementType));
        }
        Type result = Types.basicTypeFromString(type.name());
        if (result == null) {
          errors.add(new ParseError(type.location(), "Unknown type '" + type.name() + "'."));
        }
        return result;
      }

      public void visitArray(ArrayNode array) {
        super.visitArray(array);
        array.set(Type.class, findArrayType(array));
      }

      private Type findArrayType(ArrayNode array) {
        List<ExprNode> expressions = array.elements();
        if (expressions.isEmpty()) {
          return NIL;
        }
        Type firstType = expressions.get(0).get(Type.class);
        if (firstType == null) {
          return null;
        }
        Type superType = firstType;
        for (int i = 1; i < expressions.size(); i++) {
          Type type = expressions.get(i).get(Type.class);
          if (type == null) {
            return null;
          }
          superType = commonSuperType(superType, type);

          if (superType == null) {
            errors.add(new ParseError(array,
                "Array cannot contain elements of incompatible types.\n"
                    + "First element has type '" + firstType + "' while element at index " + i
                    + " has type '" + type + "'."));
            return null;
          }
        }
        ArrayType arrayType = Types.arrayOf(superType);
        if (arrayType == null) {
          errors.add(new ParseError(array, "Array cannot contain element with type '"
              + superType + "'. Only following types are allowed: " + Types.basicTypes()
              + "."));
        }
        return arrayType;
      }

      public void visitCall(CallNode call) {
        super.visitCall(call);
        call.set(Type.class, functionTypes.get(call.name()));
      }

      public void visitArg(ArgNode arg) {
        super.visitArg(arg);
        arg.set(Type.class, arg.expr().get(Type.class));
      }

      public void visitString(StringNode string) {
        super.visitString(string);
        string.set(Type.class, STRING);
      }
    }.visitAst(ast);
    return errors;
  }
}
