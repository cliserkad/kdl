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
decimalNumber: DIGIT? (DIGIT | SEPARATOR | UNDERSCORE)* DOT DIGIT (DIGIT | SEPARATOR | UNDERSCORE)*;
integer: DIGIT (DIGIT | SEPARATOR | UNDERSCORE)*;
literal: bool | CHAR_LIT | STRING_LIT | integer | decimalNumber;

statement: methodCallStatement | variableDeclaration | assignment | returnStatement | conditional | newObject STATEMENT_END;
methodCallStatement: methodCall STATEMENT_END;
block: BODY_OPEN statement* BODY_CLOSE;

// for loop
for_loop: FOR VARNAME ASSIGN range block;
for_each_loop: FOR VARNAME ASSIGN expression block;
range: expression? DOT DOT expression;

// conditionals
conditional: r_if | assertion | r_while | for_loop | for_each_loop;
r_if: R_IF condition block r_else?;
r_else: R_ELSE (block | statement);
assertion: ASSERT condition STATEMENT_END;
r_while: WHILE condition block;

constant: (CLASSNAME DOT)? CONSTNAME;
field: VARNAME (DOT VARNAME)*;
staticField: CLASSNAME DOT VARNAME;
variable: VARNAME;
value: methodCall | arrayLength| literal | variable | constant | field | staticField | indexAccess | subSequence | R_NULL | newObject;
newObject: CLASSNAME + parameterSet;
operator: PLUS | MINUS | DIVIDE | MULTIPLY | MODULUS;
expression: value (operator expression)?;

condition: expression (comparator expression)?;
comparator: EQUAL | NOT_EQUAL | REF_EQUAL | REF_NOT_EQUAL | MORE_THAN | LESS_THAN | MORE_OR_EQUAL | LESS_OR_EQUAL;
appender: AND | OR;

variableDeclaration: details (SEPARATOR VARNAME)* (ASSIGN expression)? STATEMENT_END;
assignment: (VARNAME | field) ((ASSIGN expression) | operatorAssign) STATEMENT_END;
operatorAssign: operator ASSIGN value;
details: type MUTABLE? VARNAME;
indexAccess: VARNAME BRACE_OPEN expression BRACE_CLOSE;
subSequence: VARNAME BRACE_OPEN range BRACE_CLOSE;

// method calls
methodCall: ((VARNAME | CLASSNAME) DOT)? VARNAME parameterSet;
parameterSet: PARAM_OPEN (expression (SEPARATOR expression)*)? PARAM_CLOSE;

// method definitions
methodDefinition: (details | VARNAME MUTABLE?) paramSet block;
paramSet: PARAM_OPEN ((VARNAME | param) (SEPARATOR param)*)? PARAM_CLOSE;
param: details (ASSIGN value)?;

returnStatement: RETURN expression STATEMENT_END;

type: (basetype | CLASSNAME) (BRACE_OPEN BRACE_CLOSE)*;
basetype: BOOLEAN | BYTE | SHORT | CHAR | INT | FLOAT | LONG | DOUBLE | STRING;

source: path? use* clazz EOF;
use: USE QUALIFIED_NAME STATEMENT_END;
path: PATH QUALIFIED_NAME STATEMENT_END;
clazz: TYPE CLASSNAME BODY_OPEN (constantDef | fieldDef | main | methodDefinition)* BODY_CLOSE;
constantDef: CONST CONSTNAME ASSIGN value STATEMENT_END;
fieldDef: variableDeclaration;
main: MAIN block;
