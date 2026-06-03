docker build -t udp-server:latest .
docker stop udp-server && docker rm udp-server
docker run -it --name udp-server -p 9898:9898/udp -v ./server-data:/app/data -e JAVA_OPTS="$*" udp-server:latest
