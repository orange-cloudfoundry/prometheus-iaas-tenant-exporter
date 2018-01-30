## build custom prometheus
docker build -t my-prometheus .

## run custom prometheus
docker run --name prometheus -d -p 127.0.0.1:9090:9090 my-prometheus