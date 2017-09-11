#include <stdio.h>
#include <assert.h>
#include "sss.h"
#include "memory-checking.h"

/* CMSC 216, Fall 2015, Project #6
 * Public test 6 (public06.c)
 *
 * Tests executing multiple compilation commands, then executing several
 * test commands (running several resulting executable programs) and
 * verifies that there are no errors in the heap or memory leaks.  (This is
 * the same as the previous test except for freeing the commands read, and
 * checking the consistency and correctness of the heap afterwards.)
 */

int main(void) {
  Commands commands;

  setup_memory_checking();

  commands= read_commands("compile-commands05+06", "test-commands05+06");

  assert(compile_program(commands) == 1);
  /* three tests in test-commands5 succeeded */
  assert(test_program(commands) == 3);

  /* now free the commands */
  clear_commands(&commands);
  check_heap();

  /* if this is the only thing printed the heap must be valid, and there
   * must not have been any memory leaks
   */
  printf("Win!\n");  /* the assertions succeeded */
  
  return 0;
}
