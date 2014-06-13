mx-server
=========

To compile, run `javac -d foldername src/*/*`  
To execute, run `java -cp lib/*:foldername core.Main`

"foldername" represents the folder you want the class files to be in.

Shell script for compiling and executing in Linux:

```bash
#!/bin/bash

CLASS_DIR='bin'
mkdir -p $CLASS_DIR
javac -d $CLASS_DIR src/*/*
java -cp lib/*:$CLASS_DIR core.Main
```

The JDBC driver depends on JDK used. Choose the proper one from http://jdbc.postgresql.org/download.html ... somehow and put in the libs folder.
