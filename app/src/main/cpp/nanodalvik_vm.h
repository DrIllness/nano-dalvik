#include <stdbool.h>
#include "util/stack.h"

#define STATE_HALTED 0
#define STATE_RUNNING 1
#define STATE_IDLE 2

#define PUSH "PUSH"
#define ADD "ADD"

typedef enum
{
    IDENTIFIER,
    LITERAL
} TokenType;

typedef enum
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
} OPCodeNames;

typedef struct OpCode
{
    OPCodeNames name;
    long operand;
} OpCode;

typedef struct Token
{
    TokenType type;
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

void nanodalvik_load_program(NanoDalvik* vm, const char* program);

Token* tokenize(const char* program, int* tokens_amount);

// opcode_len is the resulting size of parsed op codes
OpCode* parse(Token* tokens, int tokens_len, int* opcode_len);

bool nanodalvik_has_next_op(NanoDalvik* vm);

OpResult* nanodalvik_execute_next_op(NanoDalvik* vm);

void nanodalvik_clear(NanoDalvik* vm);






