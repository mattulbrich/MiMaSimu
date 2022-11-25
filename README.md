# MiMaSimu
A Simulator for the minimal machine (taught at the kit in Karlsruhe, Germany, see http://ti.ira.uka.de/), with a graphical UI.

![MiMaSimu](mima.png?raw=true)

## Overview

 * [Compile instructions](#compile-instructions)
   * [Compilation](#compilation)
   * [Creating a JAR-File](#creating-an-executable-jar-file)
 * [General instructions](#general-instructions)
 * [Architecture of the MIMA](#architecture-of-the-mima-minimal-machine)
   * [Registers](#registers)
   * [Alu operations](#alu-operations)
   * [Instruction set](#instruction set)
     * [Instruction format](#instruction-format)
     * [Op-Codes](#op-codes)
 * [Example program](#example-program)
 * [Other stuff](#other-stuff)

## Compile instructions

### Compilation
- use following command to compile alle files to a bin directory:
```
mkdir bin
javac -d bin -sourcepath src src/org/Mima.java
```
- run by using:
```
java -classpath bin org.Mima
```

### Creating an executable JAR-File

- change to the bin folder
- use ```jar cvfe MiMaSimu.jar org.Mima org``` to create a jar file
- execute by using:
``` java -jar MiMaSimu.jar```

## General instructions
The internal memory can be edited by clicking the edit memory button.
One can load MiMa-Assembly-Code by either passing the file as command line argument or by the use of the "load assembly" button.

### Assembly notation
#### Numbers
All numbers are being notated in HEX-Code
#### Memory Addresses
The address of the next upcoming line can be declared by the ```*=``` operator:
```
*=0x80 ;LDC 0x40 will be stored at address 0x80 in memory
LDC 0x40
```
#### Address Macros
One can name memory address so that a later referal is possible. The name of the memory address may only contain ```[A-Z_]```
If the address is not being used for a command one must add ```DS``` to clarify the declaration
```
*=0x80
VAR_1   DS  ;one can now refer to 0x80 by writing VAR_1
VAR_2   LDC 0x42    ;one can now refer to (and even jump to) VAR_2 (will be replaced by 0x81)
```
#### Entry point
The first storage cell with the first operation of the program must be named ```START```:
```
START   LDC 0x1 ;First command to be executed (=entry point)
```

#### Comments
Semicolons ";" can be used to add comments to files
#### Other
Have another look at the notes about memory notation and the MIMA architecture
### Memory Notation
In Memory notation "//" can be used to add comments.
commands as well as values can be stored at a specific address by just typing the address before the value:
```
0x00002 42 //value 42 at address 0x2
```

If no address is given the value is stored at the address of the value before incremented by one
```
0x00002 42 // value at address 0x2
43 // value at address 0x3
44 // value at address 0x4
```
addresses as well as data and instructions can ether be written in hex (0x2A), decimal (42) or dual (0b101010).

if no start address is given, the program starts at address 0x00000.

the start address can be explicitly defined as:

```
start 0x100 // program starts at 0x100
```

or can be given as annotation:
```
0x100 0x100042 ;START
0x100 0x100042 //START
0x100 0x100042 start
```
it doesn't matter if lower or upper case is used as well as spaces or tabulators.

The memory address 0x04242 is mapped to the consoles output.

## Architecture of the MIMA (minimal machine)

### Registers

* Acc (Akku): Accumulator
* X: first ALU operand
* Y: second ALU operand
* Z: ALU result
* One (Eins): constant 1
* IAR: instruction address register
* IR: instruction register
* SAR: memory address register
* SDR: memory data register

### ALU operations

c_2c_1c_0	| Operation
:--------------:|:---------
000		| do nothing (Z --> Z)
001		| X + Y --> Z
010		| rotate X to the right --> Z
011		| X AND Y --> Z
100		| X OR Y --> Z
101		| X XOR Y --> Z
110		| one's complement of X --> Z
111		| Z <-- (X == Y)?-1:0

### Instruction set

#### Instruction format

* OpCode < F:  [4 Bit OpCode][20 Bit Address or constant]
* OpCode >= F: [8 Bit OpCode][0x0000]

#### Op-Codes

OpCode 	| mnemonik	| Description
:------:|:--------------|:-----------
0	| LDC c		| c --> Acc
1	| LDV a		| < a > --> Acc
2	| STV a		| Acc --> < a >
3	| ADD a		| Acc + < a > --> Acc
4	| AND a		| Acc AND < a > --> Acc
5	| OR a		| Acc OR < a > --> Acc
6	| XOR a		| Acc XOR < a > --> Acc
7	| EQL a		| if(Acc == < a >){-1 --> Acc} else {0 --> Acc}
8	| JMP a		| Jump to address a
9	| JMN a		| Jump to address a if acc < 0
A	| LDIV a	| << a >> --> Acc
B	| STIV a	| Acc --> << a >>
C	| JMS a		| jump subroutine (see below)
D	| JIND a	| jump indirect (see below)
E	|		| free
F0	| HALT		| stops the minimal machine
F1	| NOT		| one's complement(Acc) --> Acc
F2	| RAR		| rotates Acc on the the right --> Acc
F3 - FF	|		| free

##### Notes
* Bits shifted out right within a __RAR__ command will be pushed in at the left.
* The instruction __JMS target__ saves the address of the succeeding instruction (return address) to the address given by target and initiates a jump to target + 1.
* __JIND target__ initiates a jump to the address which is stored at the target address. (Jmp <target>)

## Example Program
```
*=0x70
VAR0	DS		;one can now refer to 0x80 by writing VAR_1
VAR1	DS

*=0x80			;LDC 0x42 will be stored at address 0x80 in memory
POS1	LDC 0x42
	ADD VAR1
	HALT
START   LDC 0x1		;First command to be executed (=entry point)
	STV VAR0
	LDC 0x2
	ADD VAR0
	STV 0x71
	JMP POS1
```

## Other stuff
Give a look at the MIMA simulator (mimasim) and assambler (mimasm) by cbdev: https://github.com/cbdevnet/mima/
