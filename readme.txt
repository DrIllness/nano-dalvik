Idea
Idea is simple - make a minimalistic stack-based VM for executing assembly-like code. First implementation will be in Kotlin, but plans are to implement same VM in C/C++, utilizing JNI.


TODO
1. Highlight current line (probably need to update Op, to reflect which line it came from)
2. (low priority) color the commands. This takes to parse text in realtime, basically, on each
change in text field. Not necessary for now
3. Implement jump, substruct, multiply
4. Add support for labels?
