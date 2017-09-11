#include <stdio.h>
#include <assert.h>
#include "sss.h"
#include "memory-checking.h"

/* CMSC 216, Fall 2015, Project #6
 * Public test 3 (public03.c)
 *
 * Tests executing multiple compilation commands and verifies that there are
 * no errors in the heap or memory leaks.  (In this test the compilation
 * commands do not run the compiler- they're just simple echo commands that
 * print something, which just tests the ability to execute multiple
 * compilation commands.)  (This is the same as the previous test except for
 * freeing the data read, and checking the consistency and correctness of
 * the heap afterwards.)
 */

int main(void) {
  Commands commands;

  setup_memory_checking();

  commands= read_commands("compile-commands02+03", "test-commands02+03");

  assert(compile_program(commands) == 1);

  /* now free the commands */
  clear_commands(&commands);
  check_heap();

  /* if this is the only thing printed the heap must be valid, and there
   * must not have been any memory leaks
   */
  printf("Win!\n");  /* the assertion succeeded */
  
  return 0;
}
