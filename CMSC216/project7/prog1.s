	.text
main:	li $v0, 5				#scan m
	syscall
	sw $v0, m

	li $v0, 5				#scan n
	syscall
	sw $v0, n

	li $v0, 5				#scan mod_value
	syscall
	sw $v0, mod_value

	lw $t0, m				#load m, n, count
	lw $t1, mod_value			#t0 >> m
	lw $t4, n				#t1 >> mod_value
	lw $t3, count				#t4 >> n
						#t3 >> count

while:	ble $t0, $t4, loop			#if m <= n

	li $v0, 1				#print count
	lw $a0, count
	syscall

	li $v0, 11				#print a newline
	li $a0, 10
	syscall

	li $v0, 10				#quit the program
	syscall

loop:	rem $t2, $t0, $t1
	beq $t2, 0, if				#if (m % mod_value == 0)

endif:	add $t0, $t0, 1
	sw $t0, m				#m++
	j while

if:	add $t3, $t3, 1
	sw $t3, count				#count++
	j endif

	.data
m:		.word 0
n:		.word 0
mod_value:	.word 0
count:		.word 0
