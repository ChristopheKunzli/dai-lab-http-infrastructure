Künzli Christophe, Gerber Tristan

# HTTP - infrastructure

## Step 0: Github & Readme

The notes for each step will be in this markdown file.

### Setting up git

To import our project, you can do the command:

```code
git clone https://github.com/ChristopheKunzli/dai-lab-http-infrastructure.git
```

## Step 1: Static Web site

First, created and named the folder **static-web-server**, a folder which will contain the static web server. We then
created a Dockerfile in the root of **static-web-server**, and imported the image of ``` nginx:alpine ```

In the nginx.conf file, we set our server name as ```localhost```, listening on port 80, and default location and index
name. This location is of the virtual space that is created when starting docker, and that is used in the Dockerfile
with lines such as ```COPY ./html /usr/share/nginx/html```. This line copies the html folder in the virtual space.

To run the image, we first use, while in the **static-web-server** folder, the command:

```code
docker build -t static-web-server .
```

This command is used to create the docker image sous le nom de **static-web-server**. To start it, we can use:

```code
docker run -d -p 80:80 static-web-server
```

with ```-d``` meaning ```detach```, to run in the background, and ```-p``` means that we specify the port (in our
case, 80)

## Step 2: Docker compose

At the root of this repository, we have created a **docker-compose.yml** file.

```yaml
services:
  static-web-server:
    build:
      context: ./static-web-server
      dockerfile: Dockerfile
    ports:
      - "80:80"
```

Build with the command:

```code
docker-compose build
```

Run with:

```code
docker-compose up -d
```

At this point, we can open a browser and search ```localhost:80```. You should see the website.

Stop with:

```code
docker-compose down
```

## Step 3: HTTP API server

To connect the Client and the server, we have created an httpserver folder. The ```Main.java``` file contains the routes :  
```code
app.get("/api/quotes", quoteController::getAll);
app.get("/api/quotes/{id}", quoteController::getOne);
app.post("/api/quotes", quoteController::create);
app.put("/api/quotes/{id}", quoteController::update);
app.delete("/api/quotes/{id}", quoteController::delete);
```

As it can be seen above, we are using CRUD operations using a controller. The controller implements all of the CRUD functions. It uses a Quote class as a basic object database structure.

To support this architecture, we changed the docker-compose :  
```code
  api-server:
    build:
      context: ./httpserver
      dockerfile: Dockerfile
    ports:
      - "7000:7000"
```

We also added a Dockerfile :  
```code
FROM openjdk:25-jdk-slim
WORKDIR /app
COPY target/httpserver-1.0.jar /app/httpserver-1.0.jar
EXPOSE 7000
CMD ["java", "-jar", "httpserver-1.0.jar"]
```

To access the API on a browser, the routes above can be inserted in the url (localhost/api/quotes).  
The functionnability can also be tested with the ```crud``` command.

## Step 4: Reverse proxy with Traefik

We have modified the **docker-compose.yml** file to include the reverse proxy with Traefik as a service.

More specifically, we removed ports configurations from the static-web-server and api-server services, then added the treafik
service.

We also added labels to the static-web-server and api-server services to specify the rules for Traefik.

```yaml

## Step 5: Scalability and load balancing

## Step 6: Load balancing with round-robin and sticky sessions

## Step 7: Securing Traefik with HTTPS

