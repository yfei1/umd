/* Originally written by Saeed Alaei.  Modified and revised by Larry Herman.
 * (c) Larry Herman and Saeed Alaei, 2015.  You are allowed to use this code
 * yourself, but not to provide it to anyone else.
 * Never needs ironing.
 */

/* General features:
 *
 * All of the following may appear in a program to be assembled and will be
 * handled correctly:
 *   - Empty lines
 *   - Labels (must begin with a non-digit character and end with a colon)
 *   - Comments (begin with # or with //), which can appear on a line by
 *     themselves, or following an instruction at the end of the same line
 *   - The .data directive, of the form ".data num", which causes the value
 *     num to be stored in the current memory location
 *
 * More information:
 *   - Numeric constants may be given in decimal, in hexadecimal using an 0x
 *     prefix, or octal using an 0 prefix
 *   - Labels can be used wherever a memory address or a constant is expected
 *   - Multiple lables on the same line or on empty lines are valid
 *
 * Note that the assembler checks instructions for correct syntax (is the
 * instruction name valid and does it have the right number and types of
 * operands?), but other than ensuring that an instruction's opcode is
 * correct, it does not check the validity of instructions.
 */

/* Description of parameters and return value:
 *
 *   - name is the name of a file containing the program to be assembled.
 *     If name is either "stdin" or "", the assembly program will be read
 *     from the program's standard input.
 *
 *   - mem is where the instructions of the assembled program will be placed,
 *
 * Returns the number of instructions that were assembled.
 */
int assemble(const char* name, Word mem[]);
