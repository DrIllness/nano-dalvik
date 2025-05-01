#include "stack.h"
#include <assert.h>
#include <stdlib.h>
#include <string.h>

int stack_new(Stack* s, int elem_size)
{
    s->logical_len = 0;
    s->allocated_len = 4;
    s->elem_size = elem_size;
    s->elems = malloc(4 * elem_size);

    if (s->elems == NULL)
        return FAILED_TO_INIT_STACK;

    return 0;
}

void stack_dispose(Stack* s)
{
    free(s->elems);
}

int stack_push(Stack* s, void* value)
{
    // first check whether we need to allocate more memory, skipped for now
    if (s->allocated_len == s->logical_len)
    {
        if (stack_grow(s) == FAILED_TO_REALLOCATE_STACK)
        {
            return FAILED_TO_PUSH_ITEM;
        }
    }

    memcpy((char*) s->elems + s->logical_len * s->elem_size, value, s->elem_size);
    s->logical_len++;

    return 0;
}

static int stack_grow(Stack* s)
{
    s->allocated_len *= 2;
    void* new_elems = realloc(s->elems, s->allocated_len * s->elem_size);
    if (new_elems == NULL)
        return FAILED_TO_REALLOCATE_STACK;

    return 0;
}

int stack_pop(Stack* s, void* popped_value)
{
    if (s->logical_len == 0)
    {
        return STACK_EMPTY_ERROR;
    }
    s->logical_len--;
    memcpy(popped_value, (char*) s->elems + s->logical_len * s->elem_size, s->elem_size);

    return 0;
}