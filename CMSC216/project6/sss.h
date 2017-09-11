#if !defined(SSS_H)
#define SSS_H

#include "sss-implementation.h"

Commands read_commands(const char *compile_cmds, const char *test_cmds);
void clear_commands(Commands *const commands);
int compile_program(Commands commands);
int test_program(Commands commands);

#endif
