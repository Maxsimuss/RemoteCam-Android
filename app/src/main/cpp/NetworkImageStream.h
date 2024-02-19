//
// Created by maksi on 1/15/2024.
//

#ifndef REMOTECAM_NETWORKIMAGESTREAM_H
#define REMOTECAM_NETWORKIMAGESTREAM_H

#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <netdb.h>
#include <unistd.h>

class NetworkImageStream {
public:
    NetworkImageStream(uint32_t addr);

    ~NetworkImageStream();

    void sendData(int frameNumber, char *data, int dataSize);
private:
    const int sendBufferSize = 1024 * 1024 * 2; // 2mb

    int32_t packetHeader[2];

    int socketFileDesc = -1;
    sockaddr_in address{};

    void connect();

    bool send(char* data, int size);
};


#endif //REMOTECAM_NETWORKIMAGESTREAM_H
