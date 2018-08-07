import socketserver

class MyServer(socketserver.BaseRequestHandler):
    def handle(self):
        self.data = self.request.recv(1024).strip()

def main():
    print("Server is starting\n")
    with socketserver.TCPServer(("localhost",6200),MyServer) as server:
        server.serve_forever()
    print("Server gestartet")
if __name__ == "__main__":
    main()
