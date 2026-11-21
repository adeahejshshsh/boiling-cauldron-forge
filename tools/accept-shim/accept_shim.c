#define _GNU_SOURCE
#include <sys/socket.h>
#include <sys/types.h>
int accept(int sockfd, struct sockaddr *addr, socklen_t *addrlen) {
    return accept4(sockfd, addr, addrlen, 0);
}
