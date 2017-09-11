#include <stdio.h>
#include <assert.h>
#include "sss.h"

/* CMSC 216, Fall 2015, Project #6
 * Public test 1 (public01.c)
 *
 * Tests executing one "compliation" command.  (In this test the compilation
 * command does not run the compiler- it's just a simple echo command that
 * prints something, which just tests the ability to execute one compilation
 * command.)
 */

int main(void) {
  Commands commands;

  commands= read_commands("compile-commands01", "test-commands01");

  assert(compile_program(commands) == 1);

  printf("Win!\n");  /* the assertion succeeded */
  
  return 0;
}
