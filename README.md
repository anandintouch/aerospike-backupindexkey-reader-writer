**Aerospike-backupindexkey-reader-writer**
=====================================
This repository contains "aerospike-backupindexkey-reader-writer" tool which can be used to read index keys from AS backup files and write it to a new file.


----------

**To run this tool:**
 1. The source code can be imported into your IDE and/or built using Maven
 
    mvn clean install
 2. Sample command line argument is mentioned below

> ./run_indexkeyloader -BKF [backup files path] -NF [new file path]

***Example:***

    ./run_indexkeyloader -BKF /Users/anandprakash/Documents/Anand/project/Mytools -NF /Users/anandprakash/Documents/Anand/project/Mytools/IndexKeyFile.txt

where **BKF** is followed by backup files path(directory)
and   **NF**  is followed by the path of the new Index keys file to be created



