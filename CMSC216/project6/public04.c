#include <stdio.h>
#include <assert.h>
#include "sss.h"

/* CMSC 216, Fall 2015, Project #6
 * Public test 4 (public04.c)
 *
 * Tests executing one compilation command, then executing one test command
 * (running the resulting executable program).
 */

int main(void) {
  Commands commands;

  commands= read_commands("compile-commands04", "test-commands04");

  assert(compile_program(commands) == 1);
  assert(test_program(commands) == 1);

  printf("Win!\n");  /* the assertions succeeded */
  
  return 0;
}
