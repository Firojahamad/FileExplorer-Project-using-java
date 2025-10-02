# Java CMD File Explorer

A simple **Command-Line File Explorer** implemented in Java. This project allows users to navigate directories, manage files, and perform basic file operations directly from the command line.

---

## Features

- List files and directories (`ls`)
- Display current directory (`pwd`)
- Change directory (`cd <dir>`)
- Copy files and directories (`copy <source> <dest>`)
- Move files and directories (`move <source> <dest>`)
- Rename files or directories (`rename <oldName> <newName>`)
- Delete files or directories (`del <file/dir>`)
- View file or directory info (`info <file/dir>`)
- Search for files recursively (`search <name>`)
- Open files using default system applications (`open <file>`)
- Create new files (`create <file>`)
- Edit files (`edit <file>`)
- Create directories (`mkdir <dirname>`)
- Delete directories recursively (`rmdir <dirname>`)
- Exit the application (`exit`)

---

## Demo

```bash
C:\Users\Username\Documents> java FileExplorer
=== Java CMD File Explorer ===
Commands: ls, cd <dir>, pwd, copy <src> <dest>, move <src> <dest>, rename <old> <new>, del <file/dir>, search <name>, info <file/dir>, open <file>, create <file>, edit <file>, mkdir <dirname>, rmdir <dirname>, exit

C:\Users\Username\Documents> ls
[DIR] Projects
[FILE] example.txt

C:\Users\Username\Documents> create test.txt
File created: test.txt

C:\Users\Username\Documents> edit test.txt
Enter new content (type 'SAVE' in a new line to save and exit):
Hello World
SAVE
File updated successfully!
