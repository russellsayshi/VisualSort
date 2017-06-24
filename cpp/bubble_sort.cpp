#include "visualarr.h"

int main(int argc, char** argv) {
	visualarr varr(argv[1]);
	while(true) {
		bool outOfOrder = false;
		for(unsigned int i = 0; i < varr.size() - 1; i++) {
			if(varr[i+1] < varr[i]) {
				int temp = varr[i+1];
				varr[i+1] = varr[i];
				varr[i] =  temp;
				outOfOrder = true;
			}
		}
		if(!outOfOrder) break;
	}
}
