#include <limits>
#include "visualarr.h"

int main(int argc, char** argv) {
	visualarr arr(argv[1]);

	for(unsigned int i = 0; i < arr.size(); i++) {
		//find min in remaining list
		int minval = std::numeric_limits<int>::max();
		int minindex = -1;
		for(unsigned int o = i; o < arr.size(); o++) {
			arr.point(o);
			if(arr[o] < minval) {
				minval = arr[o];
				minindex = o;
				arr.mark(o);
			}
		}

		//swap current index with minimum
		int temp = arr[i];
		arr[i] = arr[minindex];
		arr[minindex] = temp;
	}
}
