#include <stdlib.h>
#include <string.h>
#include <android/log.h>

#include "nanodalvik_vm.h"

#define LOG_TAG "nanodalvik_vm.c"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)


void nanodalvik_initialize(NanoDalvik* vm)
{
    Stack* stack = malloc(sizeof(Stack));
    stack_new(stack, sizeof(long));

    vm->values_stack = stack;
    vm->state = STATE_IDLE;
    vm->heap = calloc(HEAP_INITIAL_SIZE, HEAP_ELEMENT_SIZE);
    vm->heap_current_size = HEAP_INITIAL_CAPACITY;
}

OpResult* nanodalvik_execute_next_op(NanoDalvik* vm)
{
    OpResult* op_res = malloc(sizeof(OpResult));
    op_res->output = "";

    OpCode* op = vm->program + vm->ip;
    long top_value;

    int error_code = NO_ERROR;
    if (nanodalvik_has_next_op(vm))
    {
        if (vm->ip < (vm->program_size))
        {
            switch (op->name)
            {
                case OP_UNDEFINED:
                    break;
                case OP_PUSH:
                    stack_push(vm->values_stack, &op->operand);
                    break;
                case OP_POP:
                    stack_pop(vm->values_stack, &top_value);
                    break;
                case OP_ADD:
                    if (vm->values_stack->logical_len < 2)
                    {
                        error_code = STACK_UNDERFLOW;
                    } else
                    {
                        long val1;
                        long val2;
                        long sum;
                        stack_pop(vm->values_stack, &val1);
                        stack_pop(vm->values_stack, &val2);
                        sum = val1 + val2;
                        stack_push(vm->values_stack, &sum);
                    }
                    break;
                case OP_PRINT:
                    if (stack_peek(vm->values_stack, &top_value) == STACK_EMPTY_ERROR)
                        error_code = STACK_UNDERFLOW;
                    else
                    {
                        int len = snprintf(NULL, 0, "%ld", top_value);
                        if (len >= 0)
                        {
                            op_res->output = malloc(len + 1);
                            if (op_res->output)
                            {
                                snprintf(op_res->output, len + 1, "%ld", top_value);
                            }
                        }
                    }
                    break;
                case OP_HALT:
                    vm->state = STATE_HALTED;
                    break;
                case OP_JMP:
                    if (op->operand < 0 || op->operand >= vm->program_size)
                    {
                        error_code = JUMP_INVALID_ADDRESS; // invalid jump
                    } else
                    {
                        vm->ip = op->operand -
                                 1; // -1 because ip will be incremented after this switch
                    }
                    break;
                case OP_JNZ:
                    if (vm->values_stack->logical_len < 1)
                    {
                        error_code = STACK_UNDERFLOW;
                    } else
                    {
                        stack_pop(vm->values_stack, &top_value);
                        if (top_value != 0)
                        {
                            if (op->operand < 0 || op->operand >= vm->program_size)
                            {
                                error_code = JUMP_INVALID_ADDRESS;
                            } else
                            {
                                vm->ip = op->operand -
                                         1; // -1 because ip will be incremented after this switch
                            }
                        }
                    }
                    break;
                case OP_JZ:
                    if (vm->values_stack->logical_len < 1)
                    {
                        error_code = STACK_UNDERFLOW;
                    } else
                    {
                        stack_pop(vm->values_stack, &top_value);
                        if (top_value == 0)
                        {
                            if (op->operand < 0 || op->operand >= vm->program_size)
                            {
                                error_code = JUMP_INVALID_ADDRESS;
                            } else
                            {
                                vm->ip = op->operand -
                                         1; // -1 because ip will be incremented after this switch
                            }
                        }
                    }
                    break;
                case OP_SUB:
                    if (vm->values_stack->logical_len < 2)
                    {
                        error_code = STACK_UNDERFLOW;
                    } else
                    {
                        long val2;
                        long val1;
                        long diff;
                        stack_pop(vm->values_stack, &val2);
                        stack_pop(vm->values_stack, &val1);
                        diff = val1 - val2;
                        stack_push(vm->values_stack, &diff);
                    }
                    break;
                case OP_MUL:
                    if (vm->values_stack->logical_len < 2)
                    {
                        error_code = STACK_UNDERFLOW;
                    } else
                    {
                        long val1;
                        long val2;
                        long product;
                        stack_pop(vm->values_stack, &val1);
                        stack_pop(vm->values_stack, &val2);
                        product = val1 * val2;
                        stack_push(vm->values_stack, &product);
                    }
                    break;
                case OP_DIV:
                    if (vm->values_stack->logical_len < 2)
                    {
                        error_code = STACK_UNDERFLOW;
                    } else
                    {
                        long val1;
                        long val2;
                        long quotient;
                        stack_pop(vm->values_stack, &val1);
                        stack_pop(vm->values_stack, &val2);
                        if (val2 == 0)
                        {
                            error_code = RUNTIME_ERROR;
                        } else
                        {
                            quotient = val1 / val2;
                            stack_push(vm->values_stack, &quotient);
                        }
                    }
                    break;
                case OP_MOD:
                    if (vm->values_stack->logical_len < 2)
                    {
                        error_code = STACK_UNDERFLOW;
                    } else
                    {
                        long val1;
                        long val2;
                        long remainder;
                        stack_pop(vm->values_stack, &val1);
                        stack_pop(vm->values_stack, &val2);
                        if (val2 == 0)
                        {
                            error_code = RUNTIME_ERROR;
                        } else
                        {
                            remainder = val1 % val2;
                            stack_push(vm->values_stack, &remainder);
                        }
                    }
                    break;
                case OP_NEG:
                    if (vm->values_stack->logical_len < 1)
                    {
                        error_code = STACK_UNDERFLOW;
                    } else
                    {
                        long val;
                        stack_pop(vm->values_stack, &val);
                        long negated = -val;
                        stack_push(vm->values_stack, &negated);
                    }
                    break;
                case OP_SWAP:
                    if (vm->values_stack->logical_len < 2)
                    {
                        error_code = STACK_UNDERFLOW;
                    } else
                    {
                        long val1;
                        long val2;
                        stack_pop(vm->values_stack, &val1);
                        stack_pop(vm->values_stack, &val2);
                        stack_push(vm->values_stack, &val1);
                        stack_push(vm->values_stack, &val2);
                    }
                    break;
                case OP_DROP:
                    if (vm->values_stack->logical_len < 1)
                    {
                        error_code = STACK_UNDERFLOW; //todo check on fibonacci program
                    } else
                    {
                        stack_pop(vm->values_stack, &top_value);
                    }
                    break;
                case OP_OVER:
                    if (vm->values_stack->logical_len < 2)
                    {
                        error_code = STACK_UNDERFLOW;
                    } else
                    {
                        long val1;
                        long val2;
                        stack_pop(vm->values_stack, &val1);
                        stack_pop(vm->values_stack, &val2);
                        stack_push(vm->values_stack, &val2);
                        stack_push(vm->values_stack, &val1);
                    }
                    break;
                case OP_DUP:
                    if (vm->values_stack->logical_len < 1)
                    {
                        error_code = STACK_UNDERFLOW;
                    } else
                    {
                        long val;
                        stack_pop(vm->values_stack, &val);
                        stack_push(vm->values_stack, &val);
                        stack_push(vm->values_stack, &val);
                    }
                    break;
                case OP_LOAD:
                    long heap_address;
                    heap_address = op->operand;
                    if (heap_address < 0 || heap_address >= vm->heap_current_size)
                    {
                        error_code = HEAP_FAILED_TO_LOAD;
                    } else
                    {
                        if (vm->heap == NULL)
                        {
                            error_code = HEAP_FAILED_TO_LOAD;
                        } else
                        {
                            HEAP_ELEMENT_TYPE* val_from_memory =
                            (HEAP_ELEMENT_TYPE*) (vm->heap) + heap_address;
                            stack_push(vm->values_stack, val_from_memory);
                        }
                    }
                    break;
                case OP_STORE:

                    if (vm->values_stack->logical_len < 1)
                    {
                        error_code = STACK_UNDERFLOW;
                    } else
                    {
                        long heap_address = op->operand;

                        stack_pop(vm->values_stack, &top_value);
                        if (heap_address < 0 || heap_address >= HEAP_MAX_SIZE)
                        {
                            error_code = HEAP_FAILED_TO_STORE;
                        } else
                        {
                            if (vm->heap != NULL)
                            {
                                if (heap_address > vm->heap_current_size &&
                                    heap_address < HEAP_MAX_SIZE)
                                {
                                    int new_size = (heap_address * HEAP_GROWTH_FACTOR <
                                                    HEAP_MAX_SIZE) ? heap_address *
                                                                     HEAP_GROWTH_FACTOR
                                                                   : HEAP_MAX_SIZE;
                                    vm->heap = realloc(vm->heap,
                                                       new_size *
                                                       HEAP_ELEMENT_SIZE);
                                    if (vm->heap != NULL)
                                        vm->heap_current_size = new_size;
                                    else
                                    {
                                        error_code = HEAP_FAILED_TO_ALLOCATE;
                                    }
                                }

                                memcpy(((HEAP_ELEMENT_TYPE*) vm->heap) + heap_address,
                                       &top_value, HEAP_ELEMENT_SIZE);
                            } else
                            {
                                error_code = HEAP_FAILED_TO_ALLOCATE;
                            }
                        }
                    }
                    break;
                case OP_CLEARMEM:
                    if (vm->heap != NULL)
                    {
                        free(vm->heap);
                    }
                    vm->heap = calloc(HEAP_INITIAL_CAPACITY, HEAP_ELEMENT_SIZE);
                    vm->heap_current_size = HEAP_INITIAL_CAPACITY;
                    break;
                case OP_CALL:

                    break;
                case OP_RET:
                    break;
            }
        }
        (vm->ip)++;
    }

    set_error(error_code, op_res);
    return op_res;
}

void set_error(ErrorCode code, OpResult* op)
{
    switch (code)
    {
        case NO_ERROR:
            op->error = NULL;
            break;
        case STACK_UNDERFLOW:
            op->error = &ERRORS[0];
            break;
        case STACK_OVERFLOW:
            op->error = &ERRORS[1];
            break;
        case HEAP_FAILED_TO_ALLOCATE:
            op->error = &ERRORS[2];
            break;
        case HEAP_FAILED_TO_STORE:
            op->error = &ERRORS[3];
            break;
        case HEAP_FAILED_TO_LOAD:
            op->error = &ERRORS[4];
            break;
        case RUNTIME_ERROR:
            op->error = &ERRORS[5];
            break;
        case JUMP_INVALID_ADDRESS:
            op->error = &ERRORS[6];
            break;
    }
}

bool nanodalvik_has_next_op(NanoDalvik* vm)
{
    bool has_next = false;
    if (vm->ip < vm->program_size)
        has_next = true;

    return has_next;
}

void print_tokenization(Token* tokens, int size);

void word_to_token(char* word, Token* tokens, int words_amount, int word_size)
{
    if (word[0] >= '0' && word[0] <= '9')
    {
        char* end_ptr;
        (tokens + words_amount)->type = LITERAL;
        (tokens + words_amount)->literal = (long) strtol(word, &end_ptr, 10);
        if (end_ptr != (void*) 0)
        {
            // handle fail to parse int here here
        }
    } else
    {
        (tokens + words_amount)->type = IDENTIFIER;
        (tokens + words_amount)->identifier = malloc(word_size * sizeof(char));
        strcpy((tokens + words_amount)->identifier, word);
    }
}

Token* tokenize(const char* program, int* tokens_amount)
{
    Token* tokens = malloc((sizeof(Token)) * 30); // init capacity
    char* word = malloc(sizeof(char) * 30); // we assume word not bigger than 30 chars for now

    int i = 0;
    int words_amount = 0;
    int word_size = 0;
    char c;
    bool is_reading = false;
    while ((c = program[i]) != '\0')
    {
        if (c == ' ' || c == '\n' || c == '\t')
        {
            if (is_reading)
            {
                is_reading = false;
                word[word_size++] = '\0';

                word_to_token(word, tokens, words_amount, word_size);

                words_amount++;
                word_size = 0;
            } else
            {
                i++;
                continue;
            }
        } else
        {
            is_reading = true;
            word[word_size++] = c;
        }

        i++;
    }

    if (is_reading)
    {
        word[word_size++] = '\0';
        word_to_token(word, tokens, words_amount++, word_size);
    }

    *tokens_amount = words_amount;

    //print_tokenization(tokens, words_amount);

    return tokens;
}

void print_tokenization(Token* tokens, int size)
{
    Token t;
    for (int i = 0; i < size; i++)
    {
        t = *(tokens + i);
        if (t.type == IDENTIFIER)
        {
            LOGI("%s", t.identifier); // for some reason gives numeric value
        } else
        {
            LOGI("%ld", t.literal);
        }
    }
}

static void check_for_following_literal(OpCode* current_op, Token* next_token, OPCodeName name,
                                        bool* error_occurred)
{
    *error_occurred = next_token->type != LITERAL;
    if (!*error_occurred)
    {
        current_op->name = name;
        current_op->operand = next_token->literal;
    }
}

OPCodeName str_to_opcode_name(char* str)
{
    OPCodeName code = OP_UNDEFINED;
    int i = 0;
    while (i < INSTRUCTION_SET_SIZE)
    {
        if (strcasecmp(str, COMMANDS[i].raw_name) == 0)
        {
            code = COMMANDS[i].name;
            return code;
        }
        i++;
    }
    return code;
}

OpCode* parse(Token* tokens, int tokens_len, int* opcode_len)
{
    int opcode_amount = 0;
    OpCode* codes = malloc(sizeof(OpCode) * 100); // todo remove const size
    int i = 0;
    while (i < tokens_len)
    {
        OPCodeName current_op_code_name = OP_UNDEFINED;
        if (tokens[i].type == IDENTIFIER)
        {
            bool has_parser_error = false;
            current_op_code_name = str_to_opcode_name(tokens[i].identifier);
            if (COMMANDS[current_op_code_name].needs_operand)
            {
                if ((i + 1 < tokens_len))
                {
                    check_for_following_literal(&codes[opcode_amount++],
                                                &tokens[++i],
                                                current_op_code_name,
                                                &has_parser_error);
                } else
                {
                    has_parser_error = true;
                }
            } else
            {
                codes[opcode_amount++].name = current_op_code_name;
            }
        }
        i++;
    }

    *opcode_len = opcode_amount;

    return codes;
}

void nanodalvik_load_program(NanoDalvik* vm, const char* program)
{
    vm->ip = 0;
    int tokens_amount = 0;
    Token* tokens = tokenize(program, &tokens_amount);

    vm->program = parse(tokens, tokens_amount, &vm->program_size);
}

void nanodalvik_clear(NanoDalvik* vm)
{
    //
}