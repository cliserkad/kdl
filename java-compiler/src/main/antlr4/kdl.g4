parser grammar kdl;

@header {
  package com.xarql.kdl.antlr;
}

options {
    tokenVocab = kdlLexer;
}

arrayLength: VARNAME DOT SIZE;

// literals
bool: TRUE | FALSE;
decimalNumber: DIGIT+ DOT DIGIT+;
integer: DIGIT+;
literal: bool | CHAR_LIT | STRING_LIT | integer | decimalNumber;

statement: methodCallStatement | variableDeclaration | variableAssignment | returnStatement | conditional | newObject STATEMENT_END;
methodCallStatement: methodCall STATEMENT_END;
block: BODY_OPEN statement* BODY_CLOSE;

// for loop
for_loop: FOR VARNAME ASSIGN range block;
range: expression? DOT DOT expression;

// conditionals
conditional: r_if | assertion | r_while | for_loop;
r_if: R_IF condition block r_else?;
r_else: R_ELSE (block | statement);
assertion: ASSERT condition STATEMENT_END;
r_while: WHILE condition block;

value: methodCall | arrayLength| literal | VARNAME | CONSTNAME | indexAccess | R_NULL | newObject;
newObject: CLASSNAME + parameterSet;
operator: PLUS | MINUS | DIVIDE | MULTIPLY | MODULUS;
expression: value (operator expression)?;

condition: singleCondition (appender singleCondition)?;
singleCondition: expression (comparator expression)?;
comparator: EQUAL | NOT_EQUAL | REF_EQUAL | REF_NOT_EQUAL | MORE_THAN | LESS_THAN | MORE_OR_EQUAL | LESS_OR_EQUAL;
appender: AND | OR;

variableDeclaration: typedVariable (SEPARATOR VARNAME)* (ASSIGN expression)? STATEMENT_END;
variableAssignment: VARNAME assignment STATEMENT_END;
assignment: (ASSIGN expression) | operatorAssign;
operatorAssign: operator ASSIGN value;
typedVariable: type MUTABLE? VARNAME;
indexAccess: VARNAME BRACE_OPEN expression BRACE_CLOSE;

// method calls
methodCall: ((VARNAME | CLASSNAME) DOT)? VARNAME parameterSet;
parameterSet: PARAM_OPEN (expression (SEPARATOR expression)*)? PARAM_CLOSE;

// method definitions
methodDefinition: methodType (typedVariable | VARNAME) parameterDefinition block;
methodType: (METHOD | FUNCTION)?;
parameterDefinition: PARAM_OPEN typedVariable? (SEPARATOR typedVariable)* PARAM_CLOSE;

returnStatement: RETURN expression STATEMENT_END;

type: basetype | CLASSNAME;
basetype: BOOLEAN | BYTE | SHORT | CHAR | INT | FLOAT | LONG | DOUBLE | STRING;

source: path? use* clazz EOF;
use: USE QUALIFIED_NAME STATEMENT_END;
path: PATH QUALIFIED_NAME STATEMENT_END;
clazz: TYPE CLASSNAME BODY_OPEN (constant | main | variableDeclaration | methodDefinition)* BODY_CLOSE;
constant: CONST CONSTNAME ASSIGN literal STATEMENT_END;
main: MAIN block;
