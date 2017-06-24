#include "visualarr.h"

int main(int argc, char** argv) {
	visualarr arr;
	for(int o = 0; o < 10; o++) {
		for(unsigned int i = 0; i < arr.size(); i++) {
			arr[i] = o;
		}
	}
}
