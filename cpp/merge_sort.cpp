#include "visualarr.h"

template <typename T>
void print_array(T& arr, int start, int end) {
	for(int i = start; i < end; i++) {
		std::cout << arr[i] << ", ";
	}
	std::cout << std::endl;
}

void merge_helper(visualarr& arr, int start, int end) {
	if(end - start > 1) {
		int diff = end-start;
		int firstlen = diff/2;
		merge_helper(arr, start, start+firstlen);
		merge_helper(arr, start+firstlen, end);

		arr.mark(start, end);

		int leftindex = start;
		int rightindex = start+firstlen;
		int* tmparr = new int[diff];

		for(int i = start; i < end; i++) {
			if(rightindex >= end || (leftindex < start+firstlen && (arr[leftindex] < arr[rightindex]))) {
				tmparr[i-start] = arr[leftindex++];
			} else {
				tmparr[i-start] = arr[rightindex++];
			}
		}
		for(int i = 0; i < diff; i++) {
			arr[start + i] = tmparr[i];
		}
	}
}


int main(int argc, char** argv) {
	visualarr arr(argv[1]);
	merge_helper(arr, 0, arr.size());
}
