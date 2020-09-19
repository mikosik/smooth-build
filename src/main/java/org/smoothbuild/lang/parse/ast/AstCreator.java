package org.smoothbuild.lang.parse.ast;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static org.smoothbuild.lang.parse.LocationHelpers.locationOf;
import static org.smoothbuild.util.Lists.map;
import static org.smoothbuild.util.Lists.sane;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.antlr.v4.runtime.tree.TerminalNode;
import org.smoothbuild.antlr.lang.SmoothBaseVisitor;
import org.smoothbuild.antlr.lang.SmoothParser.ArgContext;
import org.smoothbuild.antlr.lang.SmoothParser.ArgListContext;
import org.smoothbuild.antlr.lang.SmoothParser.ArrayTypeContext;
import org.smoothbuild.antlr.lang.SmoothParser.CallContext;
import org.smoothbuild.antlr.lang.SmoothParser.ExprContext;
import org.smoothbuild.antlr.lang.SmoothParser.FieldContext;
import org.smoothbuild.antlr.lang.SmoothParser.FieldListContext;
import org.smoothbuild.antlr.lang.SmoothParser.FieldReadContext;
import org.smoothbuild.antlr.lang.SmoothParser.FuncContext;
import org.smoothbuild.antlr.lang.SmoothParser.ModuleContext;
import org.smoothbuild.antlr.lang.SmoothParser.NonPipeExprContext;
import org.smoothbuild.antlr.lang.SmoothParser.ParamContext;
import org.smoothbuild.antlr.lang.SmoothParser.ParamListContext;
import org.smoothbuild.antlr.lang.SmoothParser.StructContext;
import org.smoothbuild.antlr.lang.SmoothParser.TypeContext;
import org.smoothbuild.antlr.lang.SmoothParser.TypeNameContext;
import org.smoothbuild.antlr.lang.SmoothParser.ValueContext;
import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.base.ModuleLocation;

public class AstCreator {
  public static Ast fromParseTree(ModuleLocation moduleLocation, ModuleContext module) {
    List<StructNode> structs = new ArrayList<>();
    List<EvaluableNode> evaluables = new ArrayList<>();
    new SmoothBaseVisitor<Void>() {
      @Override
      public Void visitStruct(StructContext struct) {
        String name = struct.TNAME().getText();
        Location location = locationOf(moduleLocation, struct.TNAME().getSymbol());
        List<ItemNode> fields = createFields(struct.fieldList());
        structs.add(new StructNode(name, fields, location));
        return null;
      }

      private List<ItemNode> createFields(FieldListContext fieldList) {
        if (fieldList != null) {
          return sane(fieldList.field())
              .stream()
              .map(this::createField)
              .collect(toImmutableList());
        }
        return new ArrayList<>();
      }

      private ItemNode createField(FieldContext field) {
        TypeNode type = createType(field.type());
        TerminalNode nameNode = field.NAME();
        String name = nameNode.getText();
        Location location = locationOf(moduleLocation, nameNode);
        return new ItemNode(type, name, Optional.empty(), location);
      }

      @Override
      public Void visitValue(ValueContext value) {
        TypeNode type = value.type() == null ? null : createType(value.type());
        TerminalNode nameNode = value.NAME();
        String name = nameNode.getText();
        ExprNode expr = value.expr() == null ? null : createExpr(value.expr());
        visitChildren(value);
        evaluables.add(new ValueNode(type, name, expr, locationOf(moduleLocation, nameNode)));
        return null;
      }

      @Override
      public Void visitFunc(FuncContext func) {
        TypeNode type = func.type() == null ? null : createType(func.type());
        TerminalNode nameNode = func.NAME();
        String name = nameNode.getText();
        List<ItemNode> params = createParams(func.paramList());
        ExprNode expr = func.expr() == null ? null : createExpr(func.expr());
        visitChildren(func);
        evaluables.add(
            new FuncNode(type, name, params, expr, locationOf(moduleLocation, nameNode)));
        return null;
      }

      private List<ItemNode> createParams(ParamListContext paramList) {
        ArrayList<ItemNode> result = new ArrayList<>();
        if (paramList != null) {
          return sane(paramList.param())
              .stream().map(this::createParam)
              .collect(toImmutableList());
        }
        return result;
      }

      private ItemNode createParam(ParamContext param) {
        TypeNode type = createType(param.type());
        String name = param.NAME().getText();
        Location location = locationOf(moduleLocation, param);
        Optional<ExprNode> defaultValue = Optional.ofNullable(param.expr()).map(this::createExpr);
        return new ItemNode(type, name, defaultValue, location);
      }

      private ExprNode createExpr(ExprContext expr) {
        NonPipeExprContext initialExpression = expr.nonPipeExpr();
        ExprNode result = createNonPipeExpr(initialExpression);
        List<CallContext> callsInPipe = expr.call();
        for (int i = 0; i < callsInPipe.size(); i++) {
          CallContext call = callsInPipe.get(i);
          result = createCallInPipe(result, expr, i, call.NAME(), createArgList(call.argList()));
        }
        return result;
      }

      private ExprNode createCallInPipe(ExprNode result, ExprContext expr, int i,
          TerminalNode calledName, List<ArgNode> argList) {
        // Location of nameless piped argument is set to the location of pipe character '|'.
        Location location = locationOf(moduleLocation, expr.p.get(i));
        List<ArgNode> args = new ArrayList<>();
        args.add(new ArgNode(null, result, location));
        args.addAll(argList);
        return new CallNode(calledName.getText(), args, locationOf(moduleLocation, calledName));
      }

      private ExprNode createNonPipeExpr(NonPipeExprContext expr) {
        if (expr.fieldRead() != null) {
          ExprNode structExpr = createNonPipeExpr(expr.nonPipeExpr());
          FieldReadContext accessor = expr.fieldRead();
          String name = accessor.NAME().getText();
          return new FieldReadNode(structExpr, name, locationOf(moduleLocation, accessor));
        }
        if (expr.array() != null) {
          List<ExprNode> elements = map(expr.array().expr(), this::createExpr);
          return new ArrayNode(elements, locationOf(moduleLocation, expr));
        }
        if (expr.call() != null) {
          CallContext call = expr.call();
          Location location = locationOf(moduleLocation, call);
          List<ArgNode> args = createArgList(call.argList());
          return new CallNode(call.NAME().getText(), args, location);
        }
        if (expr.NAME() != null) {
          TerminalNode name = expr.NAME();
          return new RefNode(name.getText(), locationOf(moduleLocation, name));
        }
        if (expr.STRING() != null) {
          String quotedString = expr.STRING().getText();
          return new StringNode(quotedString.substring(1, quotedString.length() - 1),
              locationOf(moduleLocation, expr));
        }
        if (expr.BLOB() != null) {
          return new BlobNode(
              expr.BLOB().getText().substring(2), locationOf(moduleLocation, expr));
        }
        throw new RuntimeException("Illegal parse tree: " + NonPipeExprContext.class.getSimpleName()
            + " without children.");
      }

      private List<ArgNode> createArgList(ArgListContext argList) {
        List<ArgNode> result = new ArrayList<>();
        if (argList != null) {
          List<ArgContext> args = argList.arg();
          for (ArgContext arg : args) {
            ExprContext expr = arg.expr();
            TerminalNode nameNode = arg.NAME();
            String name = nameNode == null ? null : nameNode.getText();
            ExprNode exprNode = createExpr(expr);
            result.add(new ArgNode(name, exprNode, locationOf(moduleLocation, arg)));
          }
        }
        return result;
      }

      private TypeNode createType(TypeContext type) {
        if (type instanceof TypeNameContext typeIdentifier) {
          return createType(typeIdentifier);
        }
        if (type instanceof ArrayTypeContext arrayType) {
          return createArrayType(arrayType);
        }
        throw new RuntimeException("Illegal parse tree: " + TypeContext.class.getSimpleName()
            + " without children.");
      }

      private TypeNode createType(TypeNameContext type) {
        return new TypeNode(
            type.getText(), locationOf(moduleLocation, type.TNAME()));
      }

      private TypeNode createArrayType(ArrayTypeContext arrayType) {
        TypeNode elementType = createType(arrayType.type());
        return new ArrayTypeNode(elementType, locationOf(moduleLocation, arrayType));
      }
    }.visit(module);
    return new Ast(structs, evaluables);
  }
}
