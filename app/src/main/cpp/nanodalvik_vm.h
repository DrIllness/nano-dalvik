#include <stdbool.h>
#include "util/stack.h"

#define STATE_HALTED 0
#define STATE_RUNNING 1
#define STATE_IDLE 2

typedef enum OPCodeName
{
    OP_UNDEFINED = -1,
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
} OPCodeName;

typedef struct OpMetaData
{
    OPCodeName name;
    char* raw_name;
    bool needs_operand;
} OpMetaData;

static int INSTRUCTION_SET_SIZE = 22;
static OpMetaData COMMANDS[] = {
        {OP_PUSH,     "PUSH",     true},
        {OP_POP,      "POP",      false},
        {OP_ADD,      "ADD",      false},
        {OP_PRINT,    "PRINT",    false},
        {OP_HALT,     "HALT",     false},
        {OP_JMP,      "JMP",      true},
        {OP_JNZ,      "JNZ",      true},
        {OP_JZ,       "JZ",       true},
        {OP_SUB,      "SUB",      false},
        {OP_MUL,      "MUL",      false},
        {OP_DIV,      "DIV",      false},
        {OP_MOD,      "MOD",      false},
        {OP_NEG,      "NEG",      false},
        {OP_SWAP,     "SWAP",     false},
        {OP_DROP,     "DROP",     false},
        {OP_OVER,     "OVER",     false},
        {OP_DUP,      "DUP",      false},
        {OP_LOAD,     "LOAD",     true},
        {OP_STORE,    "STORE",    true},
        {OP_CLEARMEM, "CLEARMEM", false},
        {OP_CALL,     "CALL",     false},
        {OP_RET, "RET", false}
};

typedef enum
{
    IDENTIFIER,
    LITERAL
} TokenType;

typedef struct OpCode
{
    OPCodeName name;
    long operand;
} OpCode;

typedef struct Token
{
    TokenType type;
    char* identifier;
    long literal;
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






