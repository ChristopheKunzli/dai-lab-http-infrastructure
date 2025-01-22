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

To connect the Client and the server, we have created an httpserver folder. The ```Main.java``` file contains the
routes :

```code
app.get("/api/quotes", quoteController::getAll);
app.get("/api/quotes/{id}", quoteController::getOne);
app.post("/api/quotes", quoteController::create);
app.put("/api/quotes/{id}", quoteController::update);
app.delete("/api/quotes/{id}", quoteController::delete);
```

As it can be seen above, we are using CRUD operations using a controller. The controller implements all of the CRUD
functions. It uses a Quote class as a basic object database structure.

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

More specifically, we removed ports configurations from the static-web-server and api-server services, then added the
treafik
service.

We also added labels to the static-web-server and api-server services to specify the rules for Traefik.

## Step 5: Scalability and load balancing

To use multiple server instances we added this to each service in the **docker-compose.yml** file:

```yaml
deploy:
  replicas: 3
```

We can then dynamically change the number of instances with the command:

```code
docker-compose up -d --no-recreate --scale static-web-server=2 --scale api-server=5
```

*note*: the number of instances can be changed to any number.

To check the number of instances, we can use the command `docker ps` or go to `localhost:8080` to see the Traefik
dashboard.

To check logs of a service (in order to see which instance was used after a request), we can use the command:

```code
docker service logs static-web-server
```

*note*: the service name can be changed to any service.

## Step 6: Load balancing with round-robin and sticky sessions

We added these 2 labels to the api-server service in the **docker-compose.yml** file:

```yaml
- "traefik.http.services.api.loadbalancer.sticky.cookie=true"
- "traefik.http.services.api.loadbalancer.sticky.cookie.name=myStickyCookie"
```

The first label is used to enable sticky sessions, and the second is used to specify the name of the cookie.

To test that sticky sessions are used for the api we can simply send multiple requests and check in the logs that the
same server answers.

## Step 7: Securing Traefik with HTTPS

We started by creating a self-signed certificate with the command:

```code
openssl req -x509 -newkey rsa:4096 -keyout key.pem -out cert.pem -days 365 -nodes
```

Then we modified docker-compose.yml to use the certificate

## Optional step 1: Management UI

We chose to use portainer as a management UI.

### Configure portainer

To use portainer, we add it as a service to the **docker-compose.yml** file. Running on port 9000.

```yaml
  portainer:
    image: portainer/portainer-ce
    command: -H unix:///var/run/docker.sock
    ports:
      - "9000:9000"
    volumes:
      - /var/run/docker.sock:/var/run/docker.sock
      - portainer_data:/data

  volumes:
    portainer_data:
```

### How to use

First start the architecture as usual with:

```code
docker-compose up --build -d
```

Then go to `localhost:9000` (http, not https) in your browser. You will be asked to create an admin user. After that,
click on the only environment that shows up, and you will see the services running. Then, click on the `container` tab
to see the containers. From there you are free to manage the containers (stop, delete, create new ones). 

## Optional step 2: Integration API - static Web site

We successfully integrated the API with the static web site. The API is used to get the quotes and display them on the
website.

Also, the website sends post requests every 5 seconds to the API to create a new quote. (Just for demonstration
purposes)

This was done using the fetch API in JavaScript. The code can be found to the bottom of the
**static-web-server/html/index.html** file.

