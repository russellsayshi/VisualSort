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
	updateable_int(int val, std::function<void(int)> callback) : val(val), callback(callback) {}
	updateable_int& operator=(const int& new_val);
	updateable_int& operator=(const updateable_int& other);
	operator int() const;
};

class visualarr {
private:
	bool connected = false;
	std::vector<int> arr;
	int clientSocket;
	int time_delay_ms = 0;
	int time_delay_point_ms = 0;

	bool send_num(int num);
	int recv_num();

public:
	visualarr(std::vector<int> arr={}, std::string ip = "127.0.0.1", int port = 25671);

	visualarr(std::string ip, int port = 25671) : visualarr({}, ip, port) {}

	visualarr(const visualarr& other) = delete;

	bool isConnected();

	std::size_t size();

	updateable_int operator[](int i);
	void mark(int, int);
	void mark(int);
	void point(int);
	void clearMark();

	~visualarr();
};
