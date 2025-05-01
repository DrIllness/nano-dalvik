#include <stdbool.h>
#include "util/stack.h"

#define STATE_HALTED 0
#define STATE_RUNNING 1
#define STATE_IDLE 2

enum TokenType
{
    IDENTIFIER,
    LITERAL
};

enum OPCodeNames
{
    OP_PUSH,
    OP_POP,
    OP_ADD,
    OP_PRINT,
    OP_HALT,
    OP_JMP,
    OP_JNZ,
    OP_JZ,
    OP_SUB,
    OP_MUL,
    OP_DIV,
    OP_MOD,
    OP_NEG,
    OP_SWAP,
    OP_DROP,
    OP_OVER,
    OP_DUP,
    OP_LOAD,
    OP_STORE,
    OP_CLEARMEM,
    OP_CALL,
    OP_RET
};

typedef struct OpCode
{
    enum OPCodeNames name;
    int operand;
} OpCode;

typedef union Token
{
    enum TokenType type;
    char* identifier;
    long* literal;
} Token;

typedef struct OpResult
{
    char* output;
    char* error;
} OpResult;

void clear_op_result(OpResult* res);

typedef struct NanoDalvik
{
    OpCode* program;
    int program_size;
    int ip;
    int state;
    Stack* values_stack;
} NanoDalvik;


void nanodalvik_initialize(NanoDalvik* vm);

void nanodalvik_load_program(NanoDalvik* vm, const  char* program);

Token* tokenize(const char* program, int* tokens_amount);

// size is the resulting size of parsed op codes
OpCode* parse(Token* tokens, int* size);

bool nanodalvik_has_next_op(NanoDalvik* vm);

OpResult* nanodalvik_execute_next_op(NanoDalvik* vm);

void nanodalvik_clear(NanoDalvik* vm);






