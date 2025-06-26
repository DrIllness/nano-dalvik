Idea
Minimalistic stack-based VM for executing assembly-like code on Android.
Kotlin version is discontinued for now, C version is under development.

Fibonacci number example:
// 7th fibonacci number
push 7
store 0

push 0
store 1
push 1
store 2

load 0
jz 19
load 1
dup
load 2
add
store 1
store 2
load 0
push 1
sub
store 0
jmp 6

load 1
print


TODO
c version:
0. fix allocation for error str
1. impl label support for calls
2. impl label support for constants
