Idea
Minimalistic stack-based VM for executing assembly-like code. First implementation is in Kotlin, but plans are to implement same VM in C/C++, utilizing JNI.

Fibonacci number example:
// 7th fibonacci number
push 7
store 0
drop

push 0
store 1
push 1
store 2
drop
drop

load 0
jz 26
drop
load 1
dup
load 2
add
store 1
drop
store 2
drop
load 0
push 1
sub
store 0
drop
jmp 9

load 1
print


TODO
add labels support for constants
(low priority) clean repetition in parser/engine
(low priority) color the commands. This takes to parse text in realtime, basically, on each
change in text field