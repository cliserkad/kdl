grammar kdl;

@header {
package com.xarql.kdl.antlr4;
}

// skip over whitespace
WS : [ \t\r\n]+ -> skip;

// keywords
CLASS: 'class';
CONST: 'const';
RUN: 'run';
METHOD: 'mtd';
FUNCTION: 'fnc';
TRUE: 'true';
FALSE: 'false';
RETURN: 'return';
INT: 'int';
SEE: 'see';

// syntax
BODY_OPEN: '{'; // opening bracket
BODY_CLOSE: '}'; // closing bracket
PARAM_OPEN: '('; // opening paren
PARAM_CLOSE: ')'; // closing paren
DOT: '.';
SEPARATOR: ',';
STATEMENT_END: ';';
ASSIGN: '=';
COMPARE: '?';
PLUS: '+';
MINUS: '-';
DIVIDE: '/';
MULTIPLY: '*';
MODULUS: '%';

fragment DIGIT      : [0-9];
fragment UPLETTER   : [A-Z];
fragment DNLETTER   : [a-z];
fragment LETTER     : UPLETTER | DNLETTER;
fragment ALPHANUM   : LETTER | DIGIT;
fragment UNDERSCORE : '_';
fragment DNTEXT     : DNLETTER+;

CONSTNAME : UPLETTER (UPLETTER | DIGIT | UNDERSCORE)+;
CLASSNAME : UPLETTER (LETTER)+;
VARNAME   : DNLETTER (LETTER | DIGIT)*;

QUALIFIED_NAME: (DNTEXT '.')+ CLASSNAME;

ESCAPED_QUOTE : '\\"';

// literals
STRING : '"' (ESCAPED_QUOTE | ~'"')* '"';
bool: TRUE | FALSE;
literal: bool | STRING | number;
number: ('0' | '1' | '2' | '3' | '4' | '5' | '6' | '7' | '8' | '9')*;

statement: methodCallStatement | variableDeclaration | variableAssignment;
mathExpression: valueExpression operator valueExpression;
valueExpression: ~'+' methodCallChain | literal | VARNAME | CONSTNAME;
operator: PLUS | MINUS | DIVIDE | MULTIPLY | MODULUS;

variableDeclaration: typedVariable (SEPARATOR VARNAME)* (ASSIGN (valueExpression | mathExpression))? STATEMENT_END;
variableAssignment: VARNAME (SEPARATOR VARNAME)* ASSIGN valueExpression STATEMENT_END;
typedVariable: type VARNAME;

// method calls
methodCallStatement: methodCallChain STATEMENT_END;
methodCallChain: methodCall (DOT regularMethodCall)*;
methodCall: regularMethodCall | objectiveMethodCall | staticMethodCall;
regularMethodCall: VARNAME parameterSet;
objectiveMethodCall: VARNAME DOT regularMethodCall;
staticMethodCall: CLASSNAME DOT regularMethodCall;
parameterSet: PARAM_OPEN parameter? (SEPARATOR parameter)* PARAM_CLOSE;
parameter: DIGIT+ | literal | CONSTNAME | methodCall | VARNAME;

// method definitions
methodDefinition: methodType type VARNAME parameterDefinition '{' methodBody '}';
methodType: METHOD | FUNCTION | RUN;
parameterDefinition: PARAM_OPEN typedVariable? (SEPARATOR typedVariable)* PARAM_CLOSE;

methodBody: methodCallStatement* returnStatement;
returnStatement: RETURN (VARNAME | literal) ';';

type: basetype | CLASSNAME;
basetype: INT | 'string';

source: see* clazz;
see: SEE QUALIFIED_NAME STATEMENT_END;
clazz: CLASS CLASSNAME BODY_OPEN (constant | run | variableDeclaration | methodDefinition)* BODY_CLOSE;
constant: CONST CONSTNAME ASSIGN literal STATEMENT_END;
run: RUN BODY_OPEN statement* BODY_CLOSE;
