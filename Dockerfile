FROM gradle as build-stage
WORKDIR /app
COPY ./ .
RUN gradle installDist

FROM amazoncorretto:16 as production-stage
EXPOSE 8080:8080
RUN mkdir /app
COPY --from=build-stage /app/build/install/lanenhanced-backend /app
WORKDIR /app/bin
VOLUME /app/bin/data
CMD ["./lanenhanced-backend"]