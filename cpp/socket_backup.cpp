#include <sys/socket.h>
#include <netinet/in.h>
#include <cstring>
#include <arpa/inet.h>
#include <string>
#include <errno.h>
#include <functional>
#include <iostream>
#include <vector>
#include <exception>
#include <chrono>
#include <thread>
#define HANDSHAKE_NUM 5309352

class socket_exception: public std::runtime_error
{
public:
	socket_exception(std::string const& msg) :
		std::runtime_error(msg)
	{}
};

class socket_eof_exception: public socket_exception {
public:
	socket_eof_exception(std::string const& msg) :
		socket_exception(msg)
	{}
};

class index_out_of_bounds_exception: public std::runtime_error
{
public:
	index_out_of_bounds_exception(std::string const& msg) :
		std::runtime_error(msg)
	{}
};

class updateable_int {
private:
	int val;
	std::function<void(int)> callback;

public:
	updateable_int(int val, std::function<void(int)> callback) : callback(callback), val(val) {}
	updateable_int& operator=(const int& new_val) {
		this->val = new_val;
		callback(new_val);
		return *this;
	}
	updateable_int& operator=(const updateable_int& other) {
		*this = other.val;
	}
	operator int() const {
		return this->val;
	}
};

class visualarr {
private:
	bool connected = false;
	std::vector<int> arr;
	int clientSocket;
	int time_delay_ms = 0;

	bool send_num(int num) {
		//std::cout << "Sending " << num << std::endl;
		num = htonl(num);
		if(send(clientSocket, &num, sizeof(num), 0) < 0) {
			return false;
		}
		return true;
	}

	int recv_num() {
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

public:
	visualarr(std::vector<int> arr={}, std::string ip = "127.0.0.1", int port = 25671) {
		this->arr = arr;
		this->clientSocket = -1;
		int clientSocket;
		char buffer[1024];
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

	visualarr(std::string ip, int port = 25671) : visualarr({}, ip, port) {}

	bool isConnected() {
		return connected;
	}

	std::size_t size() {
		return arr.size();
	}

	updateable_int operator[](int i) {
		if(!connected) {
			throw socket_exception("Not connected!");
		}

		if(i < 0 || i >= size()) {
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

	~visualarr() {
		if(clientSocket > 0) {
			send_num(0); //shutdown
			shutdown(clientSocket, 2);
		}
	}
};

int main(int argc, char** argv) {
	visualarr varr(argv[1]);
	while(true) {
		bool outOfOrder = false;
		for(int i = 0; i < varr.size() - 1; i++) {
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
