enum opcodes { HALT, ADD, MUL, NEG, SHL, AND, NOT, LW, LI, SW, MOVE, BNE,
               READ, WRITE };

#define R00 0
#define R01 1
#define R02 2
#define R03 3
#define R04 4
#define R05 5
#define R06 6
#define R07 7
#define R08 8
#define R09 9
#define R10 10
#define R11 11

typedef unsigned int Word;

void print_instruction(Word instruction);
int disassemble(const Word memory[], int starting_address, int num_words);
int encode_instruction(unsigned int opcode, unsigned int reg1,
                       unsigned int reg2, unsigned int reg3,
                       unsigned int memory_addr, Word *const instruction);
int valid_instruction(Word instruction);
