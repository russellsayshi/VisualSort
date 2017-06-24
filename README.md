# VisualSort
To help visualize sorting algorithms for programming classes. First stable version is out! Enjoy!

![Screenshot of array being sorted](http://i.imgur.com/g6a3uJ1.png)

## Advantages
Allows students to write array sorting code as normal, and displays a visual preview to help them understand what is happening in there code. Contains bubble sort, merge sort, and selection sort examples in the `cpp` folder. It is possible to write a client for this in any language however, not necessarily just `c++`. Docs for that are on their way.

## How to test on your own machine
Run `buildjar.sh` and run the generated jar file `out.jar`, or compile the Java files yourself if you want (`Runner` is the main class). then, pick which sort example you want from `cpp`, and run it following the directions in the appropriate readme. It will connect and display itself in the Java window, giving runtime stats once finished and checking for sorting errors.

## Remote operation
Sockets were chosen for communication because it allows the array sort program to be completely separate from the display program, and even allows the teacher to run the visualizer on their computer and have students connect to it, in order to verify that their algorithms are correct in an easier way than reading code line-by-line, as well as displaying whether or not the student's code is correct and the time it takes to sort the array.
