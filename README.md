# SudokuWebRipper

A Java desktop application for my CMPE188 Project. This is used to rip Sudoku puzzles from online sources and save them to an SQLite Database.

## Installation

### Dependencies

* **SQLite3:** Data is stored into a single flat file [SQLite website](https://www.sqlite.org/)

### Getting Started

```shell
git clone https://github.com/David-Lerner/SudokuWebRipper
cd SudokuWebRipper/src
javac -cp ".;../lib/*" *.java
cd ..
java -cp "lib/*;src" <main class name>
```

Alternatively, using an IDE like Eclipse, create a new Java project from an existing git repo by importing from https://github.com/David-Lerner/SudokuWebRipper

### Main Classes

* `SQLiteJDBC` Used to test capabilities.
* `SudokuPuzzler` Loads puzzles from [puzzler.com](http://www.sudokupuzzler.com/newspaper/). The range can be adjusted through the START and END constants.
