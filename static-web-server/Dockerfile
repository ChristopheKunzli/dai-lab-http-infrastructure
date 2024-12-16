# Use the official Nginx image from the Docker Hub
FROM nginx:alpine

# Copy the static website content to the Nginx html directory
COPY ./html /usr/share/nginx/html

# Copy the Nginx configuration file
COPY ./nginx.conf /etc/nginx/nginx.conf