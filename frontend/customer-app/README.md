# CustomerApp

This project was generated with [Angular CLI](https://github.com/angular/angular-cli) version 17.3.2.

## Development server

Run `ng serve` for a dev server. Navigate to `http://localhost:7000/`. The application will automatically reload if you change any of the source files.

## Code scaffolding

Run `ng generate component component-name` to generate a new component. You can also use `ng generate directive|pipe|service|class|guard|interface|enum|module`.

## Build

Run `ng build` to build the project. The build artifacts will be stored in the `dist/` directory.

## Running unit tests

Run `ng test` to execute the unit tests via [Karma](https://karma-runner.github.io).

## Running end-to-end tests

Run `ng e2e` to execute the end-to-end tests via a platform of your choice. To use this command, you need to first add a package that implements end-to-end testing capabilities.

## Further help

To get more help on the Angular CLI use `ng help` or go check out the [Angular CLI Overview and Command Reference](https://angular.io/cli) page.

## Creating a Docker Image

1. **Build Docker Image**: Open a terminal or command prompt and navigate to the root directory of your project. Run the following Docker command to build the Docker image:

   ```bash
   docker build -t your-docker-image-name .
   ```

   Replace `your-docker-image-name` with the desired name for your Docker image.

2. **Create Docker Image TAG**: Once the Docker image is built, you can create a tag using the following command:

   ```bash
   docker tag image-name-tag user-name/image-name
   ```

   Replace `image-name-tag` with the your custom provided tag name.
   Replace `user-name` with the name of your Docker hub user-name.
   Replace `image-name` with the name of your Docker image.

3. **Push Docker Image**: Once the Docker image tag is created, you can push that image to your docker hub account with following command:

   ```bash
   docker push user-name/image-name
   ```

   Replace `user-name` with the name of your Docker hub user-name.
   Replace `image-name` with the name of your Docker image.

4. **Use your docker image in KUBERNETES INFRA**: After the docker image is pushed clone following git project Kubernetes INFRA folders:

   ```bash
   goto infra/customer/frontend

   open base-depl.yaml file and search for "image" and replace the url with your docker hub image url

   image: user-name/image-name:latest

   ```

5. **Open Kubernetes infra readme.md file for more details**
