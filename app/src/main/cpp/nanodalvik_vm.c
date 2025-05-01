#include <stdlib.h>
#include <string.h>
#include <android/log.h>

#include "nanodalvik_vm.h"

#define LOG_TAG "kekus"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

void nanodalvik_initialize(NanoDalvik* vm)
{
    vm = malloc(sizeof(NanoDalvik));

    Stack* stack = malloc(sizeof(Stack));
    stack_new(stack, sizeof(int));

    vm->values_stack = stack;
    vm->state = STATE_IDLE;
}

OpResult* nanodalvik_execute_next_op(NanoDalvik* vm)
{
    OpResult* op_res = malloc(sizeof(OpResult));
    if (nanodalvik_has_next_op(vm))
    {
        op_res = nanodalvik_execute_next_op(vm);
    }
    return op_res;
}

bool nanodalvik_has_next_op(NanoDalvik* vm)
{
    bool has_next = false;
    if (vm->ip < vm->program_size)
        has_next = true;

    return has_next;
}

void print_tokenization(Token* tokens, int size);

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

                if (word[0] > '0' && word[0] < '9')
                {
                    char* end_ptr;
                    (tokens[words_amount]).type = LITERAL;
                    (tokens[words_amount].literal) = malloc(sizeof(long));
                    *(tokens[words_amount].literal) = (long) strtol(word, &end_ptr, 10);
                    if (end_ptr != '\0')
                    {
                        // handle fail to parse int here here
                    }
                } else
                {
                    (tokens[words_amount]).type = IDENTIFIER;
                    (tokens[words_amount].identifier) = malloc(word_size * sizeof(char));
                    strcpy(tokens[words_amount].identifier, word);
                }

                words_amount++;
                word_size = 0;
            } else
                continue;
        } else
        {
            is_reading = true;
            word[word_size++] = c;
        }

        i++;
    }

    *tokens_amount = words_amount;

    print_tokenization(tokens, words_amount);

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
            LOGI("%ld", *t.literal);
        }
    }
}

OpCode* parse(Token* tokens, int* size)
{
    return NULL;
}

void nanodalvik_load_program(NanoDalvik* vm, const char* program)
{
    int tokens_amount = 0;
    Token* tokens = tokenize(program, &tokens_amount);
    //vm->program = malloc(sizeof (OpCode) * tokens_amount);
    //vm->program = parse(tokens, &vm->program_size);
}

void nanodalvik_clear(NanoDalvik* vm)
{
    if (vm->program != NULL)
        free(vm->program);

    stack_dispose(vm->values_stack);
    free(vm);
}