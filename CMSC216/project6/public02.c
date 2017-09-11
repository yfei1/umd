#include <stdio.h>
#include <assert.h>
#include "sss.h"

/* CMSC 216, Fall 2015, Project #6
 * Public test 2 (public02.c)
 *
 * Tests executing multiple compilation commands.  (In this test the
 * compilation commands do not run the compiler- they're just simple echo
 * commands that print something, which just tests the ability to execute
 * multiple compilation commands.)
 */

int main(void) {
  Commands commands;

  commands= read_commands("compile-commands02+03", "test-commands02+03");

  assert(compile_program(commands) == 1);

  printf("Win!\n");  /* the assertion succeeded */
  
  return 0;
}
