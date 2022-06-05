# AnnotationCrac

The project provides the generation of a new Swing library, 
which can be used to get a copy of a graphical application 
for the needs of the [CRaC project](https://github.com/openjdk/crac/tree/crac). 
The CRaC project is a solution 
to the problem of slow start of Java programs. 


The AnnotationCrac project provides CRaC support for graphical 
applications written in Swing.


To generate the library, do this:
```
$ mvn -f AnnotationProcessing/ clean install
$ mvn -f AnnotationClient/ clean package
```
