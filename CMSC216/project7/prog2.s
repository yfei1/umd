	.text

main:	li 	$sp, 0x7ffffffc		#initialize the stack pointer
	li 	$v0, 5			#scanf n
	syscall
	sw 	$v0, n

	lw 	$t0, n			#push n into the stack
	sw 	$t0, ($sp)		#let sp points to
	sub 	$sp, $sp, 4		#the first unused memory unit

	jal 	cd			#call f

	add 	$sp, $sp, 4

	move	$t0, $v0		#store ret_val into $t0

	li	$v0, 1			#printf("%d", count_digits(n))
	move	$a0, $t0
	syscall

	li	$v0, 11			#printf("%c", '\n')
	li	$a0, 10
	syscall

	li 	$v0, 10			#quit the program
	syscall

cd:	sub 	$sp, $sp, 12		#prologue
	sw 	$ra, 12($sp)		#leave 4 bytes for
	sw	$fp, 8($sp)		#the local variable
	add	$fp, $sp, 12

	li 	$t0, 0			#t0 >> num_digits

	lw	$t1, 4($fp)		#t1 >> value
	bgez	$t1, do
	neg	$t1, $t1

do:	div 	$t1, $t1, 10		#do {
	add 	$t0, $t0, 1		#value /= 10
	bgtz	$t1, do			#num_digits++
					#} while (value > 0)

	move	$v0, $t0		#make a cp of ret_val to $v0

	lw	$ra, 12($sp)		#epilogue
	lw	$fp, 8($sp)
	add	$sp, $sp, 12
	jr	$ra

	.data
n:		.word 0
	
