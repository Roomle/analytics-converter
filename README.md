# analytics-converter
Convert CSV files exported from Rubens Admin into different CSV files used to import in Google Data Studio

## How to execute
1) Make sure you have Java installed
2) Download the analytics-converter.jar
3) Execute the .jar file via the console:

java -jar analytics-converter.jar {source} {destination}

It takes two input parameters: 
* First parameter need to contain the source path. So the path were you saved your analytics CSV file.
* The second one should be the destination path. In other words: Were your created files should be saved. If nothing is entered here, the program will use the same path as for the source.

Make sure that you use the terminal in the folder where the analytics-converter.jar is saved. By default, it can be found in the "bin" folder.

## General Info
The files were created in vs code, thats why the vs.code folder is also in the repository. It is used for the dependencies and the build settings. New versions also need to include the builded jar file for the repository.

If there is a change in the strucure of the CSV from Rubens Admin, the converter needs to be updated as well.

### FFU
How to build with vs code:
Downlade the files. Open the folder with vs code -> "Open folder". Make changes. Build it via the "Jar Builder" extention for vs code -> vs code consol: task: run build task. JAR file can be executed like described above.
