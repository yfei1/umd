#include <stdio.h>
#include <assert.h>
#include "sss.h"

/* CMSC 216, Fall 2015, Project #6
 * Public test 7 (public07.c)
 *
 * Tests executing some compilation commands where the compilation does not
 * succeed (a command has a nonzero exit status).
 */

int main(void) {
  Commands commands;

  commands= read_commands("compile-commands07", "test-commands07");

  assert(compile_program(commands) == 0);

  printf("Win!\n");  /* the assertion succeeded */
  
  return 0;
}
