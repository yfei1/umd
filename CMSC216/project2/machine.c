/*Yufan Fei / yfei1 / 113169434 / Section#0403 */
/*I pledge on my honor that I have not given or received any unauthorized
 assistance on this assignment.*/

/* There are four functions listed below:
 1) Print out assembly code(32bits) as human readable words
 2) Apply the first function to part of a given array precede by 
    printing out address of current instruction in the memory in hex
 3) Use the passed in values to form a new instruction
 4) Check the validity of a given instruction
*/
#include "machine.h"
#include<stdio.h>


/*Self-defined function which would return number of operators 
 corresponds to the passed-in value*/
static int defnumop(int num) {
  if(num == 1 || num == 2 || num == 5) return 3;
  else if (num == 3 || num == 4 || num == 6 ||
	   num == 10 || num == 11) return 2;
  else if (num == 0) return 0;
  else if (num > 13 || num < 0) return -1;
  else return 1;
}



void print_instruction(Word instruction) {
  /*counter: index of the for loop
    opcode : type of operation represented by digits
    numop  : number of operands corresponds to its opcode
  */

  /*Initialize three variables that will be used in this function*/
  int counter = 0, opcode, numop = 0;
  opcode = instruction >> 28; /*First four bits*/
  numop = defnumop(opcode);


  switch (opcode) {
    case 0 :
      printf("halt ");
      break;
    case 1 :
      printf("add ");
      break;
    case 2 :
      printf("mul ");
      break;
    case 3 :
      printf("neg ");
      break;
    case 4 :
      printf("shl ");
      break;
    case 5 :
      printf("and ");
      break;
    case 6 :
      printf("not ");
      break;
    case 7 :
      printf("lw ");
      break;
    case 8 :
      printf("li ");
      break;
    case 9 :
      printf("sw ");
      break;
    case 10 :
      printf("move ");
      break;
    case 11 :
      printf("bne ");
      break;
    case 12 :
      printf("read ");
      break;
    case 13 :
      printf("write ");
      break;
  }

  /*Print out register numbers depends on number of operands */
  while (counter < numop) {
    printf(" R%02d", (instruction >> (24 - 4*counter)) & 0xf);
    counter++;
  }

  /*If operation needs to use mem then print out that address*/
  if(opcode == 4 || opcode == 7 ||
     opcode == 8 || opcode == 9 ||
     opcode == 11)
    /*Print out the last 16 bits of current instruction*/
    printf(" %05d", instruction & (0xff));

  printf("\n");
}


int disassemble(const Word memory[], int starting_address, int num_words) {
  /*Counter for the for loop*/
  int num_printed;

  /*standards for invalid passed-in values*/
  if (starting_address%4 != 0 || starting_address/4 >12288 ||
      starting_address/4 < 0  ||
      starting_address/4 + num_words > 12288 ||
      starting_address/4 + num_words < 0 ||
      memory == NULL)
    return -1;

  /*if number of words is less or equal than 0 then return 0
   and end this function*/
  if(num_words <= 0) return 0;
  
  for(num_printed = 0;
      num_printed < num_words &&
      valid_instruction(memory[starting_address/4+num_printed]);
      num_printed++)
    {
      /* If current function does not print out enough instructions and 
	 current memory address is valid then print out what it needs
      */
      printf("0x%04x: ",starting_address + 4*num_printed);
      print_instruction(memory[starting_address/4 + num_printed]);
    }
  
  return num_printed;
}


int encode_instruction(unsigned int opcode, unsigned int reg1,
			 unsigned int reg2, unsigned int reg3,
			 unsigned int memory_addr, Word *const instruction)
  {
    /* create a new empty integer to store the instruction*/
    unsigned int single_ins = 0;


    /* set operands value to zero if they are not nsed in current operator*/
    if(opcode == 0)
      reg1 = 0;
    if(opcode == 0 || opcode == 7 || opcode == 8 ||
       opcode == 9 || opcode == 12 || opcode ==13)
      reg2 = 0;
    if (opcode != 1 && opcode != 2 && opcode != 5)
      reg3 = 0;
    if(opcode != 4 && opcode != 7 && opcode != 8 &&
       opcode != 9 && opcode != 11)
      memory_addr = 0;

    /* bit shifts given value to their corresponding location*/
    single_ins = opcode << 28;
    single_ins |= reg1 << 24;
    single_ins |= reg2 << 20;
    single_ins |= reg3 << 16;
    single_ins |= memory_addr;

    
    /* if invalid then return 0 without assigning 
       value of single_ins to instruction
    */
    if(instruction == NULL || !valid_instruction(single_ins)) return 0;

    /*let the pointer points to an address that stores the instruction*/
    *instruction = single_ins;

    /* return 1 if the instruction pointer get its value*/
    return 1;
  }


  int valid_instruction(Word instruction) {

    /* 
     opcode: type of operation
     reg1  : register number that would be used as the first operand
     numop : number of operands corresponds to opcode
    */
    int opcode = instruction >> 28;
    int reg1 = instruction >> 24 & 0xf;
    int numop = defnumop(opcode);

    /* if opcode is invalid */
    if(opcode < 0 || opcode >13) return 0;

    /* if any used register number is out of scope */
    while(numop >0) {
      if(((instruction >> (numop*4 +12)) & 0xf) > 11) return 0;
      numop--;
    }

    /* if current operation would use memory and the address is invalid*/
    if(opcode == 4 || opcode == 7 || opcode == 8 || opcode == 9 || opcode == 11)
      if(instruction >>16 > 49151 || instruction%4 != 0)
	return 0;

    /* if register number in the first operand is invalid*/
    if(opcode != 0 && (reg1 == 0 || reg1 == 11)) return 0;

    /* if 'shl' instruction is invalid */
    if(opcode == 4 && (instruction & 0xff) > 31) return 0;

    return 1;
  }
