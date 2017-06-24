#!/bin/bash
#sample usage: ./buildsort.sh bubble_sort.cpp
if [ $# -ne 1 ]; then
	echo "Usage: $0 file.cpp"
	exit
fi
g++ -Wall -std=c++11 $1 updateable_int.cpp visualarr.cpp -o sort.out
