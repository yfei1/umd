#include "machine.h"
#include "interpreter.h"
#include <stdlib.h>
#include <stdio.h>

int load_program(Machine *const spim, const Word program[], int program_size) {
  int return_value, i;

  /* When parameters are invalid */
  if(spim == NULL || program == NULL || program_size > MAX_MEMORY)
    return_value = 0;

  /* When the program size is a non-positive number */
  else if (program_size <= 0)
    return_value = 1;

  /* General case */
  else {
    /* Since spim is a constant pointer, copying intrucstions one by one */
    for (i = 0; i < program_size; i++)
      spim -> memory[i] = program[i];

    /*reset number stored in R11*/
    spim -> registers[NUM_REGISTERS-1] = 0;

    return_value = 1;
  }

  return return_value;
}



Status run_SPIM_program(Machine *const spim, int max_instr,
			int *const num_instr, int trace_flag)
{
  /*
    nxt_addr:  address of the next instruction to be executed
    nxt_instr: next instruction
   */
  unsigned int nxt_addr = spim -> registers[NUM_REGISTERS-1];
  unsigned int nxt_instr = spim -> memory[nxt_addr/4];

  int opcode = (nxt_instr >> 28) & 0xf;
  int reg1 = (nxt_instr >> 24) & 0xf;
  int reg2 = (nxt_instr >> 20) & 0xf;
  int reg3 = (nxt_instr >> 16) & 0xf;
  int mem_addr = nxt_instr & 0xffff;

  if(spim == NULL || num_instr == NULL)
    return PARAMETER_ERROR;
  else if (max_instr <= 0 || *num_instr >= max_instr)
    return TIMEOUT;

  if(!valid_instruction(nxt_instr))
    return INVALID_INSTRUCTION;

  switch(opcode) {
  case 0:
    return HALTED;
  case 1:
    spim->registers[reg1] =
      spim->registers[reg2] + spim->registers[reg3];
    break;
  case 2:
    spim->registers[reg1] =
      spim->registers[reg2] * spim->registers[reg3];
    break;
  case 3:
    spim->registers[reg1] = -spim->registers[reg2];
    break;
  case 4:
    spim->registers[reg1] =
      spim->registers[reg2] << mem_addr;
    break;
  case 5:
    spim->registers[reg1] =
      spim->registers[reg2] && spim->registers[reg3];
    break;
  case 6:
    spim->registers[reg1] =
      !spim->registers[reg2];
    break;
  case 7:
    spim->registers[reg1] = spim->memory[mem_addr/4];
    break;
  case 8:
    spim->registers[reg1] = mem_addr;
    break;
  case 9:
    spim->memory[mem_addr/4] = spim->registers[reg1];
    break;
  case 10:
    spim->registers[reg1] = spim->registers[reg2];
    break;
  case 11:
    break;
  case 12:
    scanf("%d", &spim->registers[reg1]);
    break;
  case 13:
    printf("%d\n", spim->registers[reg1]);
    break;
  }

  if(opcode == 11 && (spim->registers[reg1] != spim->registers[reg2]))
    spim->registers[NUM_REGISTERS-1] = mem_addr;
  else spim->registers[NUM_REGISTERS-1] += 4;


  (*num_instr)++;

  return run_SPIM_program(spim, max_instr, num_instr, trace_flag);
}


int reset(Machine *const spim) {
  if(spim == NULL)
    return 0;

  spim->registers[NUM_REGISTERS-1] = 0;
  return 1;
}
