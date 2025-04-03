Idea
Idea is simple - make a minimalistic stack-based VM for executing assembly-like code. First implementation will be in Kotlin, but plans are to implement same VM in C/C++, utilizing JNI.


TODO
1. Add step by step execution (need to change approach, split execution to loadProgram/execute).
In case of step by step exec probably need to generate errors as we go, not at once. However, tokenize
whole input. Add IP
2. Highlight current line (probably need to update Op, to reflect which line it came from)
3. (low priority) color the commands. This takes to parse text in realtime, basically, on each
change in text field. Not necessary for now
4. Implement jump, substruct, multiply
5. Add support for labels?
