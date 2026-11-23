FROM ubuntu:latest
LABEL authors="clara"

ENTRYPOINT ["top", "-b"]