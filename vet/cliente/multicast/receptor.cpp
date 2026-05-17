#include <iostream>
#include <cstring>
#include <arpa/inet.h>
#include <sys/socket.h>
#include <unistd.h>

using namespace std;

int main() {
    int sock = socket(AF_INET, SOCK_DGRAM, 0);

    int sim = 1;
    setsockopt(sock, SOL_SOCKET, SO_REUSEADDR, &sim, sizeof(sim));

    sockaddr_in serverAddr{};
    serverAddr.sin_family = AF_INET;
    serverAddr.sin_port = htons(7899);
    serverAddr.sin_addr.s_addr = INADDR_ANY; 

    bind(sock, (sockaddr*)&serverAddr, sizeof(serverAddr));

    ip_mreq mreq;
    mreq.imr_multiaddr.s_addr = inet_addr("230.1.1.1");  // Endereço multicast
    mreq.imr_interface.s_addr = INADDR_ANY;

    setsockopt(sock, IPPROTO_IP, IP_ADD_MEMBERSHIP, &mreq, sizeof(mreq));

    cout << "[MULTICAST] ouvindo alertas..." << endl;

    char buffer[1024];
    while (true) {
        int n = recv(sock, buffer, sizeof(buffer)-1, 0);
        buffer[n] = '\0';
        cout << "[ALERTA] " << buffer << endl;
    }

}