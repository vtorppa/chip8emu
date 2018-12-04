package torvi.chip8

import org.scalatest.FunSuite

class InstructionSuite extends  FunSuite {

  test("00E0: Clear screen instruction") {
    val emu = new Emulator()
    emu.programCounter = 0x259
    emu.drawFlag = false

    Instruction.clearScreen(emu, 0x00e0)

    assert(emu.programCounter == (0x259 + 2).toShort)
    assert(emu.drawFlag)
  }

  test("00EE: Return from subroutine") {
    val emu = new Emulator()
    val addr = 0x305.toByte
    emu.programCounter = 0x300
    emu.stack.push(addr)

    Instruction.returnFromSub(emu, 0x00ee.toShort)

    assert(emu.programCounter == (addr + 2).toShort)
    assert(emu.stack.isEmpty)
  }

  test("00EE: Return from subroutine throws error if stack underflow") {
    val emu = new Emulator()

    assertThrows[StackUnderflowException]{
      Instruction.returnFromSub(emu, 0x00ee.toShort)
    }
  }

  test("1NNN: Jump") {
    val emu = new Emulator()
    emu.programCounter = 0x300

    Instruction.jumpToAddr(emu, 0x1234)
    assert(emu.programCounter == 0x234.toShort)
  }

  test("2NNN: Call subroutine") {
    val emu = new Emulator()
    emu.programCounter = 0x300

    Instruction.callSub(emu, 0x2234)
    assert(emu.stack.length == 1)
    assert(emu.stack.pop() == 0x300)
    assert(emu.programCounter == 0x234.toShort)
  }

  test("3XNN: Skips next instruction if VX equals NN") {
    val emu = new Emulator()
    emu.programCounter = 0x300
    emu.registers(4) = 0x7

    Instruction.skipVxEqualsNn(emu, 0x3407)
    assert(emu.programCounter == (0x300 + 4).toShort)
  }

  test("3XNN: Doesn't skip next instruction if VX does not equal NN") {
    val emu = new Emulator()
    emu.programCounter = 0x300
    emu.registers(4) = 0x5

    Instruction.skipVxEqualsNn(emu, 0x3404)
    assert(emu.programCounter == (0x300 + 2).toShort)
  }

  test("4XNN: Skips next instruction if VX does not equal NN") {
    val emu = new Emulator()
    emu.programCounter = 0x300
    emu.registers(4) = 0x5

    Instruction.skipVxNotEqualsNn(emu, 0x4404)
    assert(emu.programCounter == (0x300 + 4).toShort)
  }

  test("4XNN: Doesn't skip next instruction if VX equals NN") {
    val emu = new Emulator()
    emu.programCounter = 0x300
    emu.registers(4) = 0x7

    Instruction.skipVxNotEqualsNn(emu, 0x4407)
    assert(emu.programCounter == (0x300 + 2).toShort)
  }

  test("5XY0: Skips next instruction if VX equals VY") {
    val emu = new Emulator()
    emu.programCounter = 0x300
    emu.registers(4) = 0x7
    emu.registers(5) = 0x7

    Instruction.skipVxEqualsVy(emu, 0x5450)
    assert(emu.programCounter == (0x300 + 4).toShort)
  }

  test("5XY0: Doesn't skip next instruction if VX does not equal VY") {
    val emu = new Emulator()
    emu.programCounter = 0x300
    emu.registers(4) = 0x5
    emu.registers(5) = 0x6

    Instruction.skipVxEqualsNn(emu, 0x5450)
    assert(emu.programCounter == (0x300 + 2).toShort)
  }

  test("6XNN: Set VX to NN") {
    val emu = new Emulator()
    emu.programCounter = 0x300
    emu.registers(4) = 0x5

    Instruction.setVxToNn(emu, 0x6466)
    assert(emu.registers(4) == 0x66)
    assert(emu.programCounter == (0x300 + 2).toShort)
  }

  test("7XNN: Add NN to VX") {
    val emu = new Emulator()
    emu.programCounter = 0x300
    emu.registers(4) = 0x5

    Instruction.addNnToVx(emu, 0x7466)
    assert(emu.registers(4) == 0x5 + 0x66)
    assert(emu.programCounter == (0x300 + 2).toShort)
  }
}
