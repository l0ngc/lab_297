export CLASSPATH="/chalmers/groups/cse-ds2/javapackages:."
javac *.java

for i in 0 1 2
do
java mcgui.Main ExampleCaster $i localhostsetup &
done