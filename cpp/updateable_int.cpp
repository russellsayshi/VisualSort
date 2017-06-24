#include "visualarr.h"

updateable_int& updateable_int::operator=(const int& new_val) {
	this->val = new_val;
	callback(new_val);
	return *this;
}

updateable_int& updateable_int::operator=(const updateable_int& other) {
	*this = other.val;
	return *this;
}

updateable_int::operator int() const {
	return this->val;
}
