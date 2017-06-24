#include "visualarr.h"

bool visualarr::send_num(int num) {
	//std::cout << "Sending " << num << std::endl;
	num = htonl(num);
	if(send(clientSocket, &num, sizeof(num), 0) < 0) {
		return false;
	}
	return true;
}

int visualarr::recv_num() {
	int buffer;
	int retval;
	if((retval = recv(clientSocket, &buffer, sizeof(buffer), MSG_WAITALL)) < 0) {
		std::string msg = "Failed to receive number! Err: " + std::to_string(errno);
		throw socket_exception(msg);
	}
	if(retval == 0) {
		throw socket_eof_exception("Server closed connection.");
	}
	buffer = ntohl(buffer);
	return buffer;
}

void visualarr::mark(int start, int end) {
	send_num(2);
	send_num(start);
	send_num(end);
}

void visualarr::mark(int index) {
	mark(index, index+1);
}

void visualarr::point(int index) {
	send_num(4);
	send_num(index);
	if(time_delay_point_ms > 0) {
		std::this_thread::sleep_for(std::chrono::milliseconds(time_delay_point_ms));
	}
}

void visualarr::clearMark() {
	send_num(3);
}

visualarr::visualarr(std::vector<int> arr, std::string ip, int port) {
	this->arr = arr;
	this->clientSocket = -1;
	int clientSocket;
	struct sockaddr_in serverAddr;
	socklen_t addr_size;

	clientSocket = socket(PF_INET, SOCK_STREAM, 0);
	//std::cout << "Socket number: " << clientSocket << std::endl;

	serverAddr.sin_family = AF_INET;
	serverAddr.sin_port = htons(port);
	serverAddr.sin_addr.s_addr = inet_addr(ip.c_str());
	memset(serverAddr.sin_zero, '\0', sizeof serverAddr.sin_zero);	

	addr_size = sizeof(serverAddr);
	if(connect(clientSocket, (struct sockaddr *)&serverAddr, addr_size) < 0) {
		std::cerr << "Failed to connect to server. Err: " << errno << std::endl;
		this->clientSocket = -1;
		return;
	}
	connected = true;
	this->clientSocket = clientSocket;

	if(!send_num(HANDSHAKE_NUM)) {
		std::cerr << "Failed to handshake with server." << std::endl;
		return;
	}

	time_delay_ms = recv_num();
	time_delay_point_ms = recv_num();

	if(arr.size() == 0) {
		send_num(0);
		//Read in an array from the server
		int len = recv_num();
		this->arr = std::vector<int>(len);
		for(int i = 0; i < len; i++) {
			this->arr[i] = recv_num();
		}
	} else {
		send_num(1);
		//Use our own array and send it to the server
		int len = arr.size();
		if(!send_num(len)) {
			std::cerr << "Could not send array length." << std::endl;
			return;
		}

		for(int i = 0; i < len; i++) {
			if(!send_num(arr[i])) {
				std::cerr << "Array failed to send partway through." << std::endl;
				return;
			}
		}
	}
}

bool visualarr::isConnected() {
	return connected;
}

std::size_t visualarr::size() {
	return arr.size();
}

updateable_int visualarr::operator[](int i) {
	if(!connected) {
		throw socket_exception("Not connected!");
	}

	if(i < 0 || (unsigned int)i >= size()) {
		std::string msg = "Not in bounds: " + std::to_string(i);
		throw index_out_of_bounds_exception(msg);
	}

	auto callback = [=](int val) {
		if(!isConnected()) {
			throw socket_exception("Cannot set data when not connected.");
		}
		send_num(1);
		send_num(i);
		send_num(val);

		arr[i] = val;

		if(time_delay_ms > 0) {
			std::this_thread::sleep_for(std::chrono::milliseconds(time_delay_ms));
		}
	};

	updateable_int ret = updateable_int(arr[i], callback);
	return ret;
}

visualarr::~visualarr() {
	if(clientSocket > 0) {
		send_num(0); //shutdown
		shutdown(clientSocket, 2);
	}
}
