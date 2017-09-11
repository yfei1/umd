	.text

main:	li	$v0, 5
	syscall
	sw	$v0, n			#scanf("%d", &n)

while1:	lw      $t0, n                  #t0 >> n
	lw      $t1, max                #t1 >> max
	la      $t2, arr		#t2 >>addr of arr

	beqz	$t0, ewhl1		#while(n != 0)
	li	$t6, 100
	bge	$t1, $t6, eif1		#if (max < 100)

	mul	$t3, $t1, 4		#t3 is the offset
	add	$t3, $t3, $t2
	sw	$t0, ($t3)		#arr[max] = n

	add	$t1, $t1, 1		#max++
	sw	$t1, max

eif1:	li	$v0, 5
	syscall
	sw	$v0, n			#scanf("%d", &n)
	j 	while1

ewhl1:	li	$v0, 5
	syscall
	sw	$v0, element		#scanf("%d", &element)


while2:	lw 	$t0, element		#t0 >> element
	beqz	$t0, ewhl2
	li	$t3, 0			#idx = 0
	sw	$t3, idx		#t3 >> idx

while3:	bge	$t3, $t1, ewhl3		#while(idx < max &&
	mul	$t4, $t3, 4		#arr[idx] != element)
	add	$t4, $t4, $t2
	lw	$t5, ($t4)
	beq	$t5, $t0, ewhl3

	add	$t3, $t3, 1
	sw	$t3, idx		#idx++
	j	while3

ewhl3:	bge	$t3, $t1, else2		#if (idx < max)
	li	$v0, 1			#printf("%d", idx)
	move	$a0, $t3
	syscall
	j	eif2

else2:	li	$v0, 1			#printf("%d", -1)
	li	$a0, -1
	syscall

eif2:	li	$v0, 11			#printf("%c", '\n')
	li	$a0, 10
	syscall

	li	$v0, 5			#scanf("%d", &element)
	syscall
	sw	$v0, element
	j	while2

ewhl2:	li 	$v0, 10
	syscall

	.data

arr:			.word	0:100
n:			.word	0
max:			.word	0
element:		.word	0
idx:			.word	0
pos:			.word	0
