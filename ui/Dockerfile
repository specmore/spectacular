FROM node:12.14.0 AS build

# Create app directory
WORKDIR /usr/src/app

# Install app dependencies
COPY package*.json ./
RUN npm install

# Copy webpack config
COPY *.config.js ./

# Copy app source
COPY src src

# Bundle
RUN npm run build

###
FROM nginx:1.16.1

ENV API_LOCATION=http://backend

# Copy bundled web files
COPY --from=build /usr/src/app/dist /usr/share/nginx/html

# Copy and web config template
COPY web.template.conf /etc/nginx/conf.d/web.template
