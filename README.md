# JFileTail
Java "tail" utility (similar to pygtail or logtail2)

This project provides two features:
- An API 
- A command-line interface

Each invocation (API or command line) reads only the portion of the file not read by
previous invocations. It will restart at the beginning of the file if any of the following
are true:
- The file has been replaced
- The file is smaller than at previous invocation


## Command-line interface

Future CLI improvements are likely coming, but for now, the invocation is simply to pass in a file name
as the only argument. For instance, if you have saved this tool in `jfiletail.jar`, then you could run:
```
java -jar jfiletail.jar <filename>
```

## API

Function is accessed solely through the `io.github.theprez.jfiletail.FileNewContentsReader` class, which
implements the standard `Reader` functions. The Reader will only read portions of the file that have not been read
since last invocation. 

The constructor takes two arguments:
- The `File` you wish to read
- the file's encoding. A special value of `*TAG` is allowed when running on IBM i, which will read the file based
on the filesystem ccsid tag. 

```java
FileNewContentsReader reader = new FileNewContentsReader(file, "*TAG")
```
