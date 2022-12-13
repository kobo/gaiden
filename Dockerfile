# syntax=docker/dockerfile:1.4

#
# Build Stage
#

FROM gradle:6-jdk8 as builder

COPY --link . /app/gaiden
WORKDIR /app/gaiden
RUN --mount=type=cache,target=/home/gradle/.gradle,sharing=private \
    gradle --no-daemon install

#
# Runtime Stage
#

FROM openjdk:8-alpine

COPY --link --from=builder /app/gaiden/build/install/gaiden /usr/local/gaiden
ENV PATH=/usr/local/gaiden/bin:${PATH} \
    GAIDEN_WATCH_PORT=8080 \
    GAIDEN_OUTPUT_DIR=build
WORKDIR /app
EXPOSE 8080
ENTRYPOINT ["gaiden"]
