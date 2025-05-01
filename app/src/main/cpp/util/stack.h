#define STACK_EMPTY_ERROR -1
#define FAILED_TO_INIT_STACK -2
#define FAILED_TO_REALLOCATE_STACK -3
#define FAILED_TO_PUSH_ITEM -4

typedef struct
{
    void* elems;
    int elem_size;
    int logical_len;
    int allocated_len;
} Stack;

int stack_new(Stack* s, int elem_size);

void stack_dispose(Stack* s);

int stack_push(Stack* s, void* elem_address);

int stack_pop(Stack* s, void* popped_value);

static int stack_grow(Stack* s);
