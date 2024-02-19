//
// Created by maksi on 1/15/2024.
//

#include "NetworkImageStream.h"

void NetworkImageStream::sendData(int frameNumber, char *data, int dataSize) {

    packetHeader[0] = frameNumber;
    packetHeader[1] = dataSize;

    if (!send((char *) packetHeader, sizeof(packetHeader))) {
        send(data, dataSize);
    }
}

bool NetworkImageStream::send(char *data, int size) {
    int written = 0;

    while (written != size) {
        int w = write(socketFileDesc, &data[written], size - written);

        if (w < 0) {
            connect();

            return true; // fail silently, this is not critical
        }

        written += size;
    }

    return false;
}

NetworkImageStream::NetworkImageStream(uint32_t addr) {
    int port = 43921;

    address.sin_family = AF_INET;
    address.sin_addr.s_addr = addr;
    address.sin_port = htons(port);
}

NetworkImageStream::~NetworkImageStream() {
    close(socketFileDesc);
}

void NetworkImageStream::connect() {
    close(socketFileDesc);

    socketFileDesc = socket(AF_INET, SOCK_STREAM, 0);
    setsockopt(socketFileDesc, SOL_SOCKET, SO_SNDBUF, &sendBufferSize, sizeof(sendBufferSize));
    setsockopt(socketFileDesc, SOL_SOCKET, SO_RCVBUF, &sendBufferSize, sizeof(sendBufferSize));
    ::connect(socketFileDesc, (sockaddr *) &address, sizeof(address));
}
