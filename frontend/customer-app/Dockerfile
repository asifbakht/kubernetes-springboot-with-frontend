# Stage 1
FROM node:20.12.0-alpine as node
WORKDIR /app
COPY . .
RUN npm install
RUN npm run build
# Stage 2
FROM nginx:stable-alpine3.17-slim
RUN rm -rf /usr/share/nginx/html/*
COPY --from=node /app/dist/browser /usr/share/nginx/html/
# COPY nginx.conf /etc/nginx/nginx.conf
EXPOSE 80
# CMD [ "/usr/sbin/nginx", "-s", "reload" ]