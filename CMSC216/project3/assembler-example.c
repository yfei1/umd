#include <stdio.h>
#include "machine.h"
#include "assembler.h"
#include "interpreter.h"

/* This example uses your functions disassemble(), load_program(), and
 * run_SPIM_program(), so to run it you must have written them.  Of course
 * you can comment out the calls to load_program() and run_SPIM_program(),
 * to just see the assembler work (which will still use your disassemble).
 */

int main() {
  Machine spim= {{0}};  /* everything, including the PC, will be set to zero */
  Word memory_arr[MAX_MEMORY]= {0};
  int num_instructions_read, num_instructions_executed;

  /* change the name of the file to "stdin" if you want to read assembly
     instructions from the standard input, or using input redirection */
  num_instructions_read= assemble("assembly-program", memory_arr);

  if (num_instructions_read == 0)
    printf("Couldn't assemble program, or no instructions were read.\n");
  else {
    printf("The program read was:\n");
    disassemble(memory_arr, 0, num_instructions_read);

    printf("\nNow loading and running the program:\n");
    load_program(&spim, memory_arr, num_instructions_read);
    /* change the fourth argument, for trace_flag, to a nonzero value to see
     * your tracing output, and change 100 if you want to execute fewer, or
     * more, instructions
     */
    run_SPIM_program(&spim, 100, &num_instructions_executed, 0);
  }

  return 0;
}
