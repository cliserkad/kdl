// Generated from kdl.g4 by ANTLR 4.7.1

package main.com.xarql.kdl;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link kdlParser}.
 */
public interface kdlListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link kdlParser#bool}.
	 * @param ctx the parse tree
	 */
	void enterBool(kdlParser.BoolContext ctx);

	/**
	 * Exit a parse tree produced by {@link kdlParser#bool}.
	 * @param ctx the parse tree
	 */
	void exitBool(kdlParser.BoolContext ctx);

	/**
	 * Enter a parse tree produced by {@link kdlParser#literal}.
	 * @param ctx the parse tree
	 */
	void enterLiteral(kdlParser.LiteralContext ctx);

	/**
	 * Exit a parse tree produced by {@link kdlParser#literal}.
	 * @param ctx the parse tree
	 */
	void exitLiteral(kdlParser.LiteralContext ctx);

	/**
	 * Enter a parse tree produced by {@link kdlParser#number}.
	 * @param ctx the parse tree
	 */
	void enterNumber(kdlParser.NumberContext ctx);

	/**
	 * Exit a parse tree produced by {@link kdlParser#number}.
	 * @param ctx the parse tree
	 */
	void exitNumber(kdlParser.NumberContext ctx);

	/**
	 * Enter a parse tree produced by {@link kdlParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterStatement(kdlParser.StatementContext ctx);

	/**
	 * Exit a parse tree produced by {@link kdlParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitStatement(kdlParser.StatementContext ctx);

	/**
	 * Enter a parse tree produced by {@link kdlParser#mathExpression}.
	 * @param ctx the parse tree
	 */
	void enterMathExpression(kdlParser.MathExpressionContext ctx);

	/**
	 * Exit a parse tree produced by {@link kdlParser#mathExpression}.
	 * @param ctx the parse tree
	 */
	void exitMathExpression(kdlParser.MathExpressionContext ctx);

	/**
	 * Enter a parse tree produced by {@link kdlParser#valueExpression}.
	 * @param ctx the parse tree
	 */
	void enterValueExpression(kdlParser.ValueExpressionContext ctx);

	/**
	 * Exit a parse tree produced by {@link kdlParser#valueExpression}.
	 * @param ctx the parse tree
	 */
	void exitValueExpression(kdlParser.ValueExpressionContext ctx);

	/**
	 * Enter a parse tree produced by {@link kdlParser#operator}.
	 * @param ctx the parse tree
	 */
	void enterOperator(kdlParser.OperatorContext ctx);

	/**
	 * Exit a parse tree produced by {@link kdlParser#operator}.
	 * @param ctx the parse tree
	 */
	void exitOperator(kdlParser.OperatorContext ctx);

	/**
	 * Enter a parse tree produced by {@link kdlParser#variableDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterVariableDeclaration(kdlParser.VariableDeclarationContext ctx);

	/**
	 * Exit a parse tree produced by {@link kdlParser#variableDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitVariableDeclaration(kdlParser.VariableDeclarationContext ctx);

	/**
	 * Enter a parse tree produced by {@link kdlParser#variableAssignment}.
	 * @param ctx the parse tree
	 */
	void enterVariableAssignment(kdlParser.VariableAssignmentContext ctx);

	/**
	 * Exit a parse tree produced by {@link kdlParser#variableAssignment}.
	 * @param ctx the parse tree
	 */
	void exitVariableAssignment(kdlParser.VariableAssignmentContext ctx);

	/**
	 * Enter a parse tree produced by {@link kdlParser#typedVariable}.
	 * @param ctx the parse tree
	 */
	void enterTypedVariable(kdlParser.TypedVariableContext ctx);

	/**
	 * Exit a parse tree produced by {@link kdlParser#typedVariable}.
	 * @param ctx the parse tree
	 */
	void exitTypedVariable(kdlParser.TypedVariableContext ctx);

	/**
	 * Enter a parse tree produced by {@link kdlParser#methodCallStatement}.
	 * @param ctx the parse tree
	 */
	void enterMethodCallStatement(kdlParser.MethodCallStatementContext ctx);

	/**
	 * Exit a parse tree produced by {@link kdlParser#methodCallStatement}.
	 * @param ctx the parse tree
	 */
	void exitMethodCallStatement(kdlParser.MethodCallStatementContext ctx);

	/**
	 * Enter a parse tree produced by {@link kdlParser#methodCallChain}.
	 * @param ctx the parse tree
	 */
	void enterMethodCallChain(kdlParser.MethodCallChainContext ctx);

	/**
	 * Exit a parse tree produced by {@link kdlParser#methodCallChain}.
	 * @param ctx the parse tree
	 */
	void exitMethodCallChain(kdlParser.MethodCallChainContext ctx);

	/**
	 * Enter a parse tree produced by {@link kdlParser#methodCall}.
	 * @param ctx the parse tree
	 */
	void enterMethodCall(kdlParser.MethodCallContext ctx);

	/**
	 * Exit a parse tree produced by {@link kdlParser#methodCall}.
	 * @param ctx the parse tree
	 */
	void exitMethodCall(kdlParser.MethodCallContext ctx);

	/**
	 * Enter a parse tree produced by {@link kdlParser#regularMethodCall}.
	 * @param ctx the parse tree
	 */
	void enterRegularMethodCall(kdlParser.RegularMethodCallContext ctx);

	/**
	 * Exit a parse tree produced by {@link kdlParser#regularMethodCall}.
	 * @param ctx the parse tree
	 */
	void exitRegularMethodCall(kdlParser.RegularMethodCallContext ctx);

	/**
	 * Enter a parse tree produced by {@link kdlParser#objectiveMethodCall}.
	 * @param ctx the parse tree
	 */
	void enterObjectiveMethodCall(kdlParser.ObjectiveMethodCallContext ctx);

	/**
	 * Exit a parse tree produced by {@link kdlParser#objectiveMethodCall}.
	 * @param ctx the parse tree
	 */
	void exitObjectiveMethodCall(kdlParser.ObjectiveMethodCallContext ctx);

	/**
	 * Enter a parse tree produced by {@link kdlParser#staticMethodCall}.
	 * @param ctx the parse tree
	 */
	void enterStaticMethodCall(kdlParser.StaticMethodCallContext ctx);

	/**
	 * Exit a parse tree produced by {@link kdlParser#staticMethodCall}.
	 * @param ctx the parse tree
	 */
	void exitStaticMethodCall(kdlParser.StaticMethodCallContext ctx);

	/**
	 * Enter a parse tree produced by {@link kdlParser#parameterSet}.
	 * @param ctx the parse tree
	 */
	void enterParameterSet(kdlParser.ParameterSetContext ctx);

	/**
	 * Exit a parse tree produced by {@link kdlParser#parameterSet}.
	 * @param ctx the parse tree
	 */
	void exitParameterSet(kdlParser.ParameterSetContext ctx);

	/**
	 * Enter a parse tree produced by {@link kdlParser#parameter}.
	 * @param ctx the parse tree
	 */
	void enterParameter(kdlParser.ParameterContext ctx);

	/**
	 * Exit a parse tree produced by {@link kdlParser#parameter}.
	 * @param ctx the parse tree
	 */
	void exitParameter(kdlParser.ParameterContext ctx);

	/**
	 * Enter a parse tree produced by {@link kdlParser#methodDefinition}.
	 * @param ctx the parse tree
	 */
	void enterMethodDefinition(kdlParser.MethodDefinitionContext ctx);

	/**
	 * Exit a parse tree produced by {@link kdlParser#methodDefinition}.
	 * @param ctx the parse tree
	 */
	void exitMethodDefinition(kdlParser.MethodDefinitionContext ctx);

	/**
	 * Enter a parse tree produced by {@link kdlParser#methodType}.
	 * @param ctx the parse tree
	 */
	void enterMethodType(kdlParser.MethodTypeContext ctx);

	/**
	 * Exit a parse tree produced by {@link kdlParser#methodType}.
	 * @param ctx the parse tree
	 */
	void exitMethodType(kdlParser.MethodTypeContext ctx);

	/**
	 * Enter a parse tree produced by {@link kdlParser#parameterDefinition}.
	 * @param ctx the parse tree
	 */
	void enterParameterDefinition(kdlParser.ParameterDefinitionContext ctx);

	/**
	 * Exit a parse tree produced by {@link kdlParser#parameterDefinition}.
	 * @param ctx the parse tree
	 */
	void exitParameterDefinition(kdlParser.ParameterDefinitionContext ctx);

	/**
	 * Enter a parse tree produced by {@link kdlParser#methodBody}.
	 * @param ctx the parse tree
	 */
	void enterMethodBody(kdlParser.MethodBodyContext ctx);

	/**
	 * Exit a parse tree produced by {@link kdlParser#methodBody}.
	 * @param ctx the parse tree
	 */
	void exitMethodBody(kdlParser.MethodBodyContext ctx);

	/**
	 * Enter a parse tree produced by {@link kdlParser#returnStatement}.
	 * @param ctx the parse tree
	 */
	void enterReturnStatement(kdlParser.ReturnStatementContext ctx);

	/**
	 * Exit a parse tree produced by {@link kdlParser#returnStatement}.
	 * @param ctx the parse tree
	 */
	void exitReturnStatement(kdlParser.ReturnStatementContext ctx);

	/**
	 * Enter a parse tree produced by {@link kdlParser#type}.
	 * @param ctx the parse tree
	 */
	void enterType(kdlParser.TypeContext ctx);

	/**
	 * Exit a parse tree produced by {@link kdlParser#type}.
	 * @param ctx the parse tree
	 */
	void exitType(kdlParser.TypeContext ctx);

	/**
	 * Enter a parse tree produced by {@link kdlParser#basetype}.
	 * @param ctx the parse tree
	 */
	void enterBasetype(kdlParser.BasetypeContext ctx);

	/**
	 * Exit a parse tree produced by {@link kdlParser#basetype}.
	 * @param ctx the parse tree
	 */
	void exitBasetype(kdlParser.BasetypeContext ctx);

	/**
	 * Enter a parse tree produced by {@link kdlParser#source}.
	 * @param ctx the parse tree
	 */
	void enterSource(kdlParser.SourceContext ctx);

	/**
	 * Exit a parse tree produced by {@link kdlParser#source}.
	 * @param ctx the parse tree
	 */
	void exitSource(kdlParser.SourceContext ctx);

	/**
	 * Enter a parse tree produced by {@link kdlParser#clazz}.
	 * @param ctx the parse tree
	 */
	void enterClazz(kdlParser.ClazzContext ctx);

	/**
	 * Exit a parse tree produced by {@link kdlParser#clazz}.
	 * @param ctx the parse tree
	 */
	void exitClazz(kdlParser.ClazzContext ctx);

	/**
	 * Enter a parse tree produced by {@link kdlParser#constant}.
	 * @param ctx the parse tree
	 */
	void enterConstant(kdlParser.ConstantContext ctx);

	/**
	 * Exit a parse tree produced by {@link kdlParser#constant}.
	 * @param ctx the parse tree
	 */
	void exitConstant(kdlParser.ConstantContext ctx);

	/**
	 * Enter a parse tree produced by {@link kdlParser#run}.
	 * @param ctx the parse tree
	 */
	void enterRun(kdlParser.RunContext ctx);

	/**
	 * Exit a parse tree produced by {@link kdlParser#run}.
	 * @param ctx the parse tree
	 */
	void exitRun(kdlParser.RunContext ctx);
}